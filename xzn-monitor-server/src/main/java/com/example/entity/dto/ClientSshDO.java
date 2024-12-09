package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
@TableName("db_client_ssh")
public class ClientSshDO {
    @TableId
    Integer id;
    Integer port;
    String username;
    String password;
}
