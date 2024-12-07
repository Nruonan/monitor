package com.example.entity.vo.request;

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
public class RuntimeDetailReqDTO {
    long timestamp;
    double cpuUsage;
    double memoryUsage;
    double diskUsage;
    double networkUpload;
    double networkDownload;
    double diskRead;
    double diskWrite;
}
