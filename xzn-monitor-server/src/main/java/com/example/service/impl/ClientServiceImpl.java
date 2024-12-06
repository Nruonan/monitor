package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.ClientDO;
import com.example.entity.dto.ClientDetailDO;
import com.example.entity.vo.request.ClientDetailReqDTO;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
import com.example.service.ClientService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.security.SecureRandom;
import java.util.Date;
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
            ClientDO client = new ClientDO(id, "未命名主机", token, new Date());
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

    @Override
    public void updateClientDetail(ClientDO client, ClientDetailReqDTO requestParam) {
        ClientDetailDO clientDetailDO = new ClientDetailDO();
        BeanUtil.copyProperties(requestParam,clientDetailDO);
        clientDetailDO.setId(client.getId());
        if (Optional.ofNullable(detailMapper.selectById(client.getId())).isPresent()){
            detailMapper.updateById(clientDetailDO);
        }else{
            detailMapper.insert(clientDetailDO);
        }
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
        System.out.println(sb);
        return sb.toString();
    }
}
