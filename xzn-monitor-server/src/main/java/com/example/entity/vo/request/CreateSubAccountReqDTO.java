package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Nruonan
 * @description
 */
@Data
public class CreateSubAccountReqDTO {
    @Length(min =1, max=10)
    String username;
    @Email
    String email;
    @Length(min = 6, max =16)
    String password;
    @Size(min = 1)
    List<Integer> clients;
}
