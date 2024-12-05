package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ClientService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
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
}
