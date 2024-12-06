package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.ClientDO;
import com.example.entity.vo.request.ClientDetailReqDTO;
import com.example.entity.vo.request.RuntimeDetailReqDTO;

/**
 * @author Nruonan
 * @description
 */
public interface ClientService extends IService<ClientDO> {
    String registerToken();
    ClientDO findClientByToken(String token);
    ClientDO findClientById(int id);
    boolean verifyAndRegister(String token);
    void updateClientDetail(ClientDO client, ClientDetailReqDTO requestParam);
    void updateRuntimeDetails(ClientDO client, RuntimeDetailReqDTO requestParam);
}
