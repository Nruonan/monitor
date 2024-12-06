package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.BaseDetail;
import com.example.entity.ConnectionConfig;
import com.example.entity.Response;
import com.example.entity.RuntimeDetail;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author Nruonan
 * @description
 */
@Slf4j
@Component
public class NetUtils {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Resource
    @Lazy
    ConnectionConfig config;

    /**
     * 向服务器注册客户端
     *
     * 此方法负责向给定地址的服务器发送注册请求，以完成客户端的注册过程
     * 它通过调用 doGet 方法来执行 HTTP GET 请求，并根据服务器的响应来判断注册是否成功
     *
     * @param address 服务器地址，用于指定注册请求的目标服务器
     * @param token 注册所需的令牌，用于验证客户端的身份
     * @return 返回一个布尔值，表示注册是否成功如果服务器响应成功，则返回 true，否则返回 false
     */
    public boolean registerToServer(String address, String token) {
        log.info("正在向服务端注册，请稍后...");
        // 执行 HTTP GET 请求以尝试注册，并获取响应结果
        Response response = this.doGet("/register", address, token);
        // 根据响应结果判断注册是否成功
        if (response.success()){
            log.info("客户端注册成功");
        }else{
            log.error("客户端注册失败，原因为：{}",response.message());
        }
        // 返回注册是否成功的布尔值
        return response.success();
    }

    /**
     * 执行GET请求
     *
     * @param url 请求的URL
     * @return 返回响应对象
     *
     */
    private Response doGet(String url){
        // 调用重载的doGet方法，传入请求的URL，以及从配置中获取的地址和令牌
        return this.doGet(url, config.getAddress(), config.getToken());
    }

    /**
     * 执行GET请求并返回响应
     *
     * 该方法构造一个GET请求，发送到指定的地址和端点，并附带授权令牌，然后解析响应体为Response对象
     *
     * @param url 请求的URL路径
     * @param address 服务器地址
     * @param token 授权令牌，用于验证请求的合法性
     * @return 返回解析后的Response对象，包含服务器的响应信息
     */
    private Response doGet(String url, String address, String token) {
        try{
            // 构建GET请求，设置请求URI和授权头
            HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(new URI(address + "/monitor" + url))
                .header("Authorization", token)
                .build();

            // 发送请求并接收响应
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 解析响应体为Response对象并返回
            return JSONObject.parseObject(response.body()).to(Response.class);
        } catch (Exception e) {
            // 记录错误日志，并返回错误响应对象
            log.error("在发起服务端请求时出现错误",e);
            return Response.errorResponse(e);
        }
    }
    /**
     * 更新系统基本信息
     * @param detail 要更新的系统基本信息对象，包含需要更新的详细信息
     */
    public void updateBaseDetails(BaseDetail detail){
        // 发送HTTP POST请求到服务器，请求路径为"/detail"，携带参数为当前系统基本信息对象
        Response response = this.doPost("/detail", detail);
        
        // 根据服务器返回的响应结果，判断更新操作是否成功
        if (response.success()){
            // 如果响应表示操作成功，则记录日志信息，表明系统基本信息已成功更新
            log.info("系统基本信息已更新完成");
        }else{
            // 如果响应表示操作失败，则记录错误日志，包含失败的原因信息
            log.error("系统更新原因更新失败：{}",response.message());
        }
    }
    /**
     * 更新系统实时数据信息
     * @param detail 要更新的系统实时数据对象
     */
    public void updateRuntimeDetails(RuntimeDetail detail){
        // 发送HTTP POST请求到服务器，请求路径为"/runtime"，携带参数为当前系统基本信息对象
        Response response = this.doPost("/runtime", detail);

        // 根据服务器返回的响应结果，判断更新操作是否成功
        if (response.success()){
            // 如果响应表示操作成功，则记录日志信息，表明系统基本信息已成功更新
            log.info("系统基本信息已更新完成");
        }else{
            // 如果响应表示操作失败，则记录错误日志，包含失败的原因信息
            log.error("系统更新原因更新失败：{}",response.message());
        }
    }
    /**
     * 执行POST请求并返回响应结果
     * 此方法构建一个POST请求，将给定的数据转换为JSON格式作为请求体，发送到指定的URL，并解析返回的响应
     * 
     * @param url 请求的URL路径，将被追加到配置的服务器地址后面
     * @param data 请求的数据，将被转换为JSON格式
     * @return Response对象，包含从服务器返回的响应数据
     * @throws RuntimeException 如果请求过程中发生错误，将抛出运行时异常
     */
    private Response doPost(String url, Object data){
        try{
            // 将输入数据转换为JSON字符串
            String rawData = JSONObject.from(data).toJSONString();
            
            // 构建HTTP POST请求
            HttpRequest request = HttpRequest.newBuilder().POST(BodyPublishers.ofString(rawData))
                .uri(new URI(config.getAddress() + "/monitor" + url))
                .header("Authorization", config.getToken())
                .header("Content-Type", "application/json")
                .build();
            
            // 发送请求并接收响应
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
            // 解析响应体为Response对象并返回
            return JSONObject.parseObject(response.body()).to(Response.class);
        } catch (Exception e) {
            log.error("在发起服务端请求时出现问题",e);
            return Response.errorResponse(e);
        }
    }
}
