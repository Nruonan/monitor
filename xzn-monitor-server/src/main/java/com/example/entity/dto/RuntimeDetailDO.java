package com.example.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Nruonan
 */
@Data
@Builder
@AllArgsConstructor
public class RuntimeDetailDO {
    long timestamp;
    double cpuUsage;
    double memoryUsage;
    double diskUsage;
    double networkUsage;
    double networkDownload;
    double diskRead;
    double diskWrite;
}