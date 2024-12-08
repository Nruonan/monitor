package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ChangePasswordReqDTO;
import com.example.entity.vo.request.CreateSubAccountReqDTO;
import com.example.entity.vo.request.ModifyEmailReqDTO;
import com.example.entity.vo.response.SubAccountRespDTO;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public RestBean<Void> changePassword(@RequestBody() @Valid ChangePasswordReqDTO requestParam,
            @RequestAttribute(Const.ATTR_USER_ID) int id){
        return service.changePassword(id, requestParam) ? RestBean.success(): RestBean.failure(401,"输入原密码错误");
    }
    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody ModifyEmailReqDTO requestParam){
        String s = service.modifyEmail(id, requestParam);
        if (s == null){
            return RestBean.success();
        }else{
            return RestBean.failure(401,s);
        }

    }
    @PostMapping("/sub/create")
    public RestBean<Void> createSubAccount(@RequestBody() @Valid CreateSubAccountReqDTO requestParam){
        service.createSubAccount(requestParam);
        return RestBean.success();
    }
    @GetMapping("/sub/delete")
    public RestBean<Void> deleteSubAccount(@RequestAttribute(Const.ATTR_USER_ID) int id,@RequestParam("uid") int uid){
        if (uid == id)
            return RestBean.failure(401, "不能删除自己");
        service.deleteSubAccount(uid);
        return RestBean.success();
    }

    @GetMapping("/sub/list")
    public RestBean<List<SubAccountRespDTO>> subAccountList(){
        return RestBean.success(service.subAccountList());
    }
}
