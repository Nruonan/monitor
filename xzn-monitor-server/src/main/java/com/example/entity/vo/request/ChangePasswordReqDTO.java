package com.example.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Nruonan
 * @description
 */
@Data
public class ChangePasswordReqDTO {
    @Length(min =6, max=16)
    String password;
    @Length(min =6, max=16)
    String newPassword;
}
