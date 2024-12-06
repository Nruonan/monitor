package com.example.entity.vo.request;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class ClientDetailReqDTO {
    // 操作系统架构类型
    @NotNull
    String osArch;
    // 操作系统名称
    @NotNull
    String osName;
    // 操作系统版本
    @NotNull
    String osVersion;
    // 操作系统位数
    @NotNull
    int osBit;
    // CPU名称
    @NotNull
    String cpuName;
    // CPU核心数
    @NotNull
    int cpuCore;
    // 内存大小
    @NotNull
    double memory;
    // 磁盘大小
    @NotNull
    double disk;
    // IP地址
    @NotNull
    String ip;
}
