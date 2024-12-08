package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDO;
import com.example.entity.vo.request.ChangePasswordReqDTO;
import com.example.entity.vo.request.ConfirmResetReqDTO;
import com.example.entity.vo.request.EmailResetReqDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<AccountDO>, UserDetailsService {
    AccountDO findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type, String email, String address);
    String resetEmailAccountPassword(EmailResetReqDTO info);
    String resetConfirm(ConfirmResetReqDTO info);

    Boolean logout(HttpServletRequest request, HttpServletResponse response);

    boolean changePassword(int id, ChangePasswordReqDTO requestParam);
}
