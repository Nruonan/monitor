package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
@Builder
@AllArgsConstructor
public class RuntimeDetail {
    long timestamp;
    double cpuUsage;
    double memoryUsage;
    double diskUsage;
    double networkUsage;
    double networkDownload;
    double diskRead;
    double diskWrite;
}
