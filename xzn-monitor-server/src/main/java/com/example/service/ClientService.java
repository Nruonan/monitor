package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.ClientDO;
import com.example.entity.vo.request.ClientDetailReqDTO;
import com.example.entity.vo.request.RenameClientReqDTO;
import com.example.entity.vo.request.RenameNodeReqDTO;
import com.example.entity.vo.request.RuntimeDetailReqDTO;
import com.example.entity.vo.request.SshConnectionReqDTO;
import com.example.entity.vo.response.ClientDetailsRespDTO;
import com.example.entity.vo.response.ClientPreviewRespDTO;
import com.example.entity.vo.response.ClientSimpleRespDTO;
import com.example.entity.vo.response.RuntimeDetailRespDTO;
import com.example.entity.vo.response.RuntimeHistoryRespDTO;
import com.example.entity.vo.response.SshSettingsRespDTO;
import java.util.List;

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
    List<ClientPreviewRespDTO> listAllClient();

    List<ClientSimpleRespDTO> simpleClientList();
    void renameClient(RenameClientReqDTO requestParam);
    void renameNode(RenameNodeReqDTO requestParam);
    ClientDetailsRespDTO clientDetails(int id);
    RuntimeHistoryRespDTO clientRuntimeDetailsHistory(int clientId);
    RuntimeDetailRespDTO clientRuntimeDetailsNow(int clientId);
    void deleteClient(int clientId);
    SshSettingsRespDTO sshSettings(int clientId);
    void saveSshConnection(SshConnectionReqDTO requestParam);
}
