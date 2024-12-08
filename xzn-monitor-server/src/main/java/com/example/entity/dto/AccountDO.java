package com.example.entity.dto;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 数据库中的用户信息
 * @author Nruonan
 */
@Data
@TableName("db_account")
@AllArgsConstructor
public class AccountDO implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    String password;
    String email;
    String role;
    String clients;
    Date registerTime;

    public List<Integer> getClientList(){
        if (clients == null)return Collections.emptyList();
        return JSONArray.parse(clients).toList(Integer.class);
    }
}
