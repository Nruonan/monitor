package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class RuntimeDetailRespDTO {
    long timestamp;
    double cpuUsage;
    double memoryUsage;
    double diskUsage;
    double networkUpload;
    double networkDownload;
    double diskRead;
    double diskWrite;
}
