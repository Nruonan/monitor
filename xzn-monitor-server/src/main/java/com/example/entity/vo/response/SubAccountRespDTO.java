package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
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
public class SubAccountRespDTO {
    int id;
    String username;
    String email;
    JSONArray clientList;
}
