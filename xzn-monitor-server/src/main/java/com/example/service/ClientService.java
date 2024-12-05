package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.ClientDO;

/**
 * @author Nruonan
 * @description
 */
public interface ClientService extends IService<ClientDO> {
    String registerToken();
    ClientDO findClientByToken(String token);
    ClientDO findClientById(int id);
    boolean verifyAndRegister(String token);
}
