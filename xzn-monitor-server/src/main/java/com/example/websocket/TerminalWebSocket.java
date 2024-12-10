package com.example.websocket;

import com.example.entity.dto.ClientDetailDO;
import com.example.entity.dto.ClientSshDO;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientSshMapper;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import jakarta.annotation.Resource;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Nruonan
 * @description
 */
@Slf4j
@Component
@ServerEndpoint("/terminal/{clientId}")
public class TerminalWebSocket {

    private static ClientDetailMapper detailMapper;

    @Resource
    public void setDetailMapper(ClientDetailMapper detailMapper) {
        TerminalWebSocket.detailMapper = detailMapper;
    }

    private static ClientSshMapper sshMapper;

    @Resource
    public void setSshMapper(ClientSshMapper sshMapper) {
        TerminalWebSocket.sshMapper = sshMapper;
    }


    private static final Map<Session,Shell> sessionMap = new ConcurrentHashMap<>();
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) throws IOException {
        ClientDetailDO detail = detailMapper.selectById(clientId);
        ClientSshDO ssh = sshMapper.selectById(clientId);
        if (detail == null || ssh == null) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "无法识别此主机"));
            return;
        }
        if (this.createSshConnection(session,ssh, detail.getIp())){
            log.info("主机 {} 的ssh连接已创建", detail.getIp());
        }
    }

    /**
     * 处理接收到的消息
     * 
     * @param session 与客户端的会话对象，用于标识和管理与特定客户端的连接
     * @param message 从客户端接收到的消息，此消息将被发送到与当前会话关联的Shell对象
     * @throws IOException 如果在写入数据到输出流时发生错误
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // 从会话映射中获取与当前会话关联的Shell对象
        Shell shell = sessionMap.get(session);
        // 获取Shell对象的输出流，用于向Shell发送数据
        OutputStream output = shell.output;
        // 将接收到的消息转换为字节数组，并写入到Shell的输出流中
        output.write(message.getBytes(StandardCharsets.UTF_8));
        // 刷新输出流，表示数据发送完成
        output.flush();
    }
    
    @OnClose
    public void onClose(Session session) throws IOException {
        Shell shell = sessionMap.get(session);
        if (shell != null){
            shell.close();
            sessionMap.remove(session);
            log.info("主机 {} 的SSH连接已断开", shell.js.getHost());
        }
    }
    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        log.error("用户WebSocket连接出现错误", error);
        session.close();
    }
    /**
     * 创建SSH连接
     * 
     * @param session WebSocket会话对象，用于与客户端通信
     * @param ssh 包含SSH连接信息的数据对象，包括用户名、密码和端口等
     * @param ip 要连接的SSH服务器的IP地址
     * @return 如果SSH连接成功，则返回true；否则返回false
     * @throws IOException 如果在创建SSH连接过程中发生I/O错误
     */
    private boolean createSshConnection(Session session, ClientSshDO ssh, String ip) throws IOException {
        try {
            // 创建JSch对象，用于管理SSH连接
            JSch jsch = new JSch();
            
            // 使用JSch对象创建SSH会话
            com.jcraft.jsch.Session js = jsch.getSession(ssh.getUsername(), ip, ssh.getPort());
            js.setPassword(ssh.getPassword());
            js.setConfig("StrictHostKeyChecking","no");
            js.setTimeout(5000);
            // 建立SSH连接
            js.connect();
            // 打开一个Shell通道
            ChannelShell channel = (ChannelShell) js.openChannel("shell");
            channel.setPtyType("xterm");
            channel.connect(3000);
            if (!channel.isConnected()) {
                throw new JSchException("Failed to connect the channel");
            }
            // 将当前WebSocket会话与SSH会话和Shell通道关联，并存储到会话映射中
            sessionMap.put(session,new Shell(session, js, channel));
            
            // SSH连接成功，返回true
            return true;
        } catch (Exception e) {
            // 捕获异常，并根据异常消息类型处理
            String message = e.getMessage();
            if(message.equals("Auth fail")) {
                // 如果认证失败，关闭WebSocket会话，并记录错误日志
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,
                    "登录SSH失败，用户名或密码错误"));
                log.error("连接SSH失败，用户名或密码错误，登录失败");
            } else if(message.contains("Connection refused")) {
                // 如果连接被拒绝，关闭WebSocket会话，并记录错误日志
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,
                    "连接被拒绝，可能是没有启动SSH服务或是放开端口"));
                log.error("连接SSH失败，连接被拒绝，可能是没有启动SSH服务或是放开端口");
            } else {
                // 其他异常，关闭WebSocket会话，并记录错误日志
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, message));
                log.error("连接SSH时出现错误", e);
            }
        }
        return false;
    }
    private class Shell {
        private final Session session;
        private final com.jcraft.jsch.Session js;
        private final ChannelShell channel;
        private final InputStream input;
        private final OutputStream output;
        public Shell(Session session, com.jcraft.jsch.Session js, ChannelShell channel)
            throws IOException {
            this.session = session;
            this.js = js;
            this.channel = channel;
            this.input = channel.getInputStream();
            this.output = channel.getOutputStream();
            service.submit(this::read);
        }
        
        /**
         * 读取输入流中的数据并发送到session
         */
        private void read(){
            try{
                // 创建一个1MB的缓冲区用于读取输入流中的数据
                byte[] buffer = new byte[1024 * 1024];
                int i;
                // 循环读取输入流中的数据，直到输入流结束
                while((i = input.read(buffer)) != -1){
                    // 将读取到的数据转换为字符串，并指定使用UTF-8编码
                    String text = new String(Arrays.copyOfRange(buffer, 0, i), StandardCharsets.UTF_8);
                    // 将转换后的字符串发送到session的远程端
                    session.getBasicRemote().sendText(text);
                }
            }catch (Exception e){
                // 捕获异常，并记录错误日志
                log.error("读取SSH输入流时出现问题",e);
            }
        }
        public void close() throws IOException {
            input.close();
            output.close();
            channel.disconnect();
            js.disconnect();
            service.shutdown();
        }
    }

}

