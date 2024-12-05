package com.example.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.ClientDO;
import com.example.mapper.ClientMapper;
import com.example.service.ClientService;
import jakarta.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * @author Nruonan
 * @description
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, ClientDO> implements ClientService {

    private String registerToken = this.registerToken;
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
    @Override
    public boolean verifyAndRegister(String token) {
        if (this.registerToken.equals(token)){
            int id = this.randoClientId();
            ClientDO client = new ClientDO(id, "未命名主机", token, new Date());
            if (this.save(client)) {
                registerToken = this.registerToken();
                this.addClientCache(client);
                return true;
            }
        }
        return false;
    }

    @Override
    public ClientDO findClientByToken(String token) {
        return clientTokenCache.get(token);
    }

    @Override
    public ClientDO findClientById(int id) {
        return clientCache.get(id);
    }

    private void addClientCache(ClientDO client){
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
