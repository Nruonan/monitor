package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
@TableName("db_client")
@AllArgsConstructor
public class ClientDO {
    @TableId
    private Integer id;
    private String name;
    private String token;
    private String location;
    private String node;
    private Date registerTime;
}
