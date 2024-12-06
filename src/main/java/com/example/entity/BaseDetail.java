package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Nruonan
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseDetail {
    // 操作系统架构类型
    String osArch;
    // 操作系统名称
    String osName;
    // 操作系统版本
    String osVersion;
    // 操作系统位数
    int osBit;
    // CPU名称
    String cpuName;
    // CPU核心数
    int cpuCore;
    // 内存大小（单位未指定）
    double memory;
    // 磁盘大小（单位未指定）
    double disk;
    // IP地址
    String ip;
}
