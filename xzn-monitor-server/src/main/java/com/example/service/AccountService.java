package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDO;
import com.example.entity.vo.request.ConfirmResetReq;
import com.example.entity.vo.request.EmailResetReq;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<AccountDO>, UserDetailsService {
    AccountDO findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type, String email, String address);
    String resetEmailAccountPassword(EmailResetReq info);
    String resetConfirm(ConfirmResetReq info);
}
