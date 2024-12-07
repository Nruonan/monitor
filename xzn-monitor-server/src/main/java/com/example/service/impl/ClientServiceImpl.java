package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.ClientDO;
import com.example.entity.dto.ClientDetailDO;
import com.example.entity.dto.RuntimeDetailDO;
import com.example.entity.vo.request.ClientDetailReqDTO;
import com.example.entity.vo.request.RenameClientReqDTO;
import com.example.entity.vo.request.RuntimeDetailReqDTO;
import com.example.entity.vo.response.ClientDetailsRespDTO;
import com.example.entity.vo.response.ClientPreviewRespDTO;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
import com.example.service.ClientService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * @author Nruonan
 * @description
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, ClientDO> implements ClientService {

    @Resource
    ClientDetailMapper detailMapper;
    private String registerToken = this.generateNewToken();
    // 多线程map 保存数据
    private final Map<Integer, ClientDO> clientCache = new ConcurrentHashMap<>();
    private final Map<String, ClientDO> clientTokenCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.list().forEach(this::addClientCache);
    }
    @Override
    public String registerToken() {
        return registerToken;
    }
    /**
     * 验证客户端的注册令牌并进行注册
     * 
     * @param token 客户端提供的注册令牌
     * @return 如果注册成功返回true，否则返回false
     */
    @Override
    public boolean verifyAndRegister(String token) {
        // 检查传入的令牌是否与当前注册令牌匹配
        if (this.registerToken.equals(token)){
            // 生成一个新的客户端ID
            int id = this.randoClientId();
            // 创建一个新的客户端对象，初始状态为未命名主机，并使用当前时间作为注册时间
            ClientDO client = new ClientDO(id, "未命名主机", token, "cn","未命名节点", new Date());
            // 尝试保存客户端对象到数据库或持久化存储
            if (this.save(client)) {
                // 保存成功后，更新注册令牌以备下次注册
                registerToken = this.registerToken();
                // 将新客户端对象添加到缓存中，以便快速访问
                this.addClientCache(client);
                // 注册成功，返回true
                return true;
            }
        }
        // 如果注册失败，返回false
        return false;
    }

    /**
     * 更新客户端详情信息
     * @param client 客户端基本信息对象，用于获取客户端ID
     * @param requestParam 请求参数对象，包含要更新的客户端详情信息
     */
    @Override
    public void updateClientDetail(ClientDO client, ClientDetailReqDTO requestParam) {
        // 创建一个新的客户端详情对象
        ClientDetailDO clientDetailDO = new ClientDetailDO();
        // 将请求参数复制到客户端详情对象中
        BeanUtil.copyProperties(requestParam,clientDetailDO);
        // 设置客户端详情对象的ID为当前客户端的ID
        clientDetailDO.setId(client.getId());
        // 检查数据库中是否存在当前客户端的详情记录
        if (Optional.ofNullable(detailMapper.selectById(client.getId())).isPresent()){
            // 如果存在，则更新详情记录
            detailMapper.updateById(clientDetailDO);
        }else{
            // 如果不存在，则插入新的详情记录
            detailMapper.insert(clientDetailDO);
        }
        System.out.println(clientDetailDO);
    }
    private Map<Integer, RuntimeDetailDO> currentRuntime = new ConcurrentHashMap<>();
    @Override
    public void updateRuntimeDetails(ClientDO client, RuntimeDetailReqDTO requestParam) {
        RuntimeDetailDO bean = BeanUtil.toBean(requestParam, RuntimeDetailDO.class);
        currentRuntime.put(client.getId(),bean);
        System.out.println(bean);
    }

    /**
     * 列出所有客户端详细信息
     * 
     * 本方法从客户端缓存中获取所有客户端信息，将每条信息转换为ClientDetailReqDTO对象，并根据客户端ID从数据库中获取详细信息
     * 如果客户端的运行时信息在当前缓存中存在且时间戳在60秒内，也将其信息合并到DTO对象中
     * 
     * @return 包含所有客户端详细信息的DTO列表
     */
    @Override
    public List<ClientPreviewRespDTO> listAllClient() {
        return clientCache.values().stream().map(clientDO ->{
                // 将客户端信息转换为ClientDetailReqDTO对象
            ClientPreviewRespDTO bean = BeanUtil.toBean(clientDO, ClientPreviewRespDTO.class);
                // 从数据库中获取客户端详细信息并复制到DTO对象中
                BeanUtil.copyProperties(detailMapper.selectById(clientDO.getId()),bean);
                // 获取当前缓存中的客户端运行时信息
                RuntimeDetailDO runtime = currentRuntime.get(clientDO.getId());
                // 如果运行时信息存在且时间戳在60秒内，将其信息复制到DTO对象中
                if (isOline(runtime)){
                    BeanUtil.copyProperties(runtime,bean);
                    // 设置在线
                    bean.setOnline(true);
                }
                return bean;
        }).toList();
    }

    /**
     * 重命名客户端
     * @param requestParam 包含要更新的客户端ID和新名称的请求对象
     */
    @Override
    public void renameClient(RenameClientReqDTO requestParam) {
        // 更新客户端名称
        this.update(Wrappers.lambdaUpdate(ClientDO.class)
            .eq(ClientDO::getId,requestParam.getId())
            .set(ClientDO::getName,requestParam.getName()));
        // 重新初始化配置
        this.init();
    }

    @Override
    public ClientDetailsRespDTO clientDetails(int id) {
        ClientDO clientDO = this.clientCache.get(id);
        ClientDetailsRespDTO dto = BeanUtil.toBean(clientDO, ClientDetailsRespDTO.class);
        BeanUtil.copyProperties(detailMapper.selectById(id),dto);
        dto.setOnline(this.isOline(currentRuntime.get(id)));
        return dto;
    }
    private boolean isOline(RuntimeDetailDO runtime){
        return runtime != null && System.currentTimeMillis() - runtime.getTimestamp() < 60 * 1000;
    }
    @Override
    public ClientDO findClientByToken(String token) {
        return clientTokenCache.get(token);
    }

    @Override
    public ClientDO findClientById(int id) {
        return clientCache.get(id);
    }

    /**
     * 将客户端信息添加到缓存中
     * @param client 要添加到缓存中的客户端信息，包括客户端的ID和Token等关键数据
     */
    private void addClientCache(ClientDO client){
        // 将客户端信息根据其ID添加到缓存中
        clientCache.put(client.getId(), client);
        clientTokenCache.put(client.getToken(), client);
    }
    private int randoClientId(){
        return RandomUtil.randomInt(10000000, 99999999);
    }
    private String generateNewToken() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPORSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
