package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ChangePasswordReqDTO;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nruonan
 * @description
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    AccountService service;

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestBody()ChangePasswordReqDTO requestParam,
            @RequestAttribute(Const.ATTR_USER_ID) int id){
        return service.changePassword(id, requestParam) ? RestBean.success(): RestBean.failure(401,"输入原密码错误");
    }
}
