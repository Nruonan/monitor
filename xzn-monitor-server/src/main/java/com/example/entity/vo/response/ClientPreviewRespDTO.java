package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class ClientPreviewRespDTO {
    int id;
    boolean online;
    String name;
    String location;
    String osName;
    String osVersion;
    String ip;
    String cpuName;
    int cpuCore;
    double memory;
    double cpuUsage;
    double memoryUsage;
    double networkUpload;
    double networkDownload;
}
