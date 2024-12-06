package com.example.config;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.ConnectionConfig;
import com.example.utils.NetUtils;
import jakarta.annotation.Resource;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
/**
 * @author Nruonan
 * @description
 */
@Slf4j
@Configuration
public class ServerConfiguration {

    @Resource
    NetUtils netUtils;

    @Bean
    ConnectionConfig connectionConfig() {
        log.info("正在加载服务端链接配置...");

        // 尝试从配置文件中读取ConnectionConfig对象实例
        ConnectionConfig config = readConfigurationFromFile();

        // 检查从配置文件读取的配置是否为空如果为空，则调用registerToServer方法进行注册并获取配置
        if (Optional.ofNullable(config).isEmpty()){
            config = this.registerToServer();
        }
        // 返回ConnectionConfig对象实例
        return config;
    }
    /**
     * 向服务器注册客户端
     *
     * @return ConnectionConfig 返回一个包含服务器地址和Token的配置对象
     */
    private ConnectionConfig registerToServer(){
        Scanner scanner = new Scanner(System.in);
        String token, address;
        do {
            // 提示用户输入服务器地址
            log.info("请输入需要注册的服务端访问地址，地址类似 'http://192.168.9.22:8080' 这种写法:");
            address = scanner.nextLine();
            // 提示用户输入注册Token
            log.info("请输入服务端生成的用于注册客户端的Token密钥:");
            token = scanner.nextLine();
        }while (!netUtils.registerToServer(address, token));
        // 创建ConnectionConfig对象
        ConnectionConfig config = new ConnectionConfig(address, token);
        // 将配置信息保存到文件
        this.saveConfigurationToFile(config);
        return config;
    }

    /**
     * 将连接配置信息保存到文件中
     *
     * @param config 要保存的连接配置信息，包含服务端的连接参数等信息
     */
    private void saveConfigurationToFile(ConnectionConfig config) {
        // 创建配置目录如果目录不存在且成功创建，则记录日志
        File dir = new File("config");
        if (!dir.exists() && dir.mkdir()){
            log.info("创建用于保存服务端链接信息的目录已完成");
        }

        // 定义配置文件路径并尝试创建文件对象
        File file = new File("config/server.json");
        try(
            // 使用FileWriter以自动管理资源的方式打开文件，准备写入配置信息
            FileWriter fileWriter = new FileWriter(file)
        ) {
            // 将ConnectionConfig对象转换为JSON对象，并进一步转换为字符串后写入文件
            fileWriter.write(JSONObject.from(config).toJSONString());
        } catch (IOException e) {
            log.error("保存配置文件时出现问题", e);
        }
        log.info("服务端链接信息已保存成功！");
    }


    /**
     * 从文件中读取配置信息
     *
     * @return ConnectionConfig 返回解析后的配置对象，如果读取或解析失败，则返回null
     */
    private ConnectionConfig readConfigurationFromFile() {
        // 创建File对象以访问配置文件
        File file = new File("config/server.json");
        // 检查配置文件是否存在
        if (file.exists()){
            try (FileInputStream fileInputStream = new FileInputStream(file)){
                // 读取文件内容并转换为字符串
                String raw = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
                // 解析JSON字符串并将其转换为ConnectionConfig对象
                return JSONObject.parseObject(raw).to(ConnectionConfig.class);
            } catch (IOException e) {
                // 如果发生IO错误，将其包装为RuntimeException并抛出
                throw new RuntimeException(e);
            }
        }
        // 如果配置文件不存在，返回null
        return null;
    }

}
