package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class SshSettingsRespDTO {
    int id;
    String username;
    String email;
    JSONArray clientList;
    String ip;
}
