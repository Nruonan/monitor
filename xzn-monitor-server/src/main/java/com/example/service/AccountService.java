package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDO;
import com.example.entity.vo.request.ConfirmResetDTOReq;
import com.example.entity.vo.request.EmailResetDTOReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<AccountDO>, UserDetailsService {
    AccountDO findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type, String email, String address);
    String resetEmailAccountPassword(EmailResetDTOReq info);
    String resetConfirm(ConfirmResetDTOReq info);

    Boolean logout(HttpServletRequest request, HttpServletResponse response);
}
