package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.ClientDO;
import com.example.entity.vo.request.ClientDetailReqDTO;
import com.example.entity.vo.request.RuntimeDetailReqDTO;
import com.example.service.ClientService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nruonan
 * @description
 */
@RestController
@RequestMapping(value ="/monitor")
public class ClientController {
    @Resource
    ClientService clientService;

    @GetMapping("/register")
    public RestBean<Void> registerClient(@RequestHeader("Authorization") String token){
        return clientService.verifyAndRegister(token) ?
            RestBean.success() : RestBean.failure(401, "客户端注册失败,检查token是否正确");
    }

    @PostMapping("/detail")
    public RestBean<Void> updateClientDetail(@RequestAttribute(Const.ATTR_CLIENT)ClientDO client,
        @RequestBody @Valid ClientDetailReqDTO requestParam){
        clientService.updateClientDetail(client,requestParam);
        return  RestBean.success();
    }

    @PostMapping("/runtime")
    public RestBean<Void> updateRuntimeDetails(@RequestAttribute(Const.ATTR_CLIENT)ClientDO client,
        @RequestBody @Valid RuntimeDetailReqDTO requestParam){
        clientService.updateRuntimeDetails(client,requestParam);
        return  RestBean.success();
    }
}
