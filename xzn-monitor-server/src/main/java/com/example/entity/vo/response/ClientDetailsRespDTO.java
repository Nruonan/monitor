package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class ClientDetailsRespDTO {
    int id;
    boolean online;
    String name;
    String location;
    String node;
    String osName;
    String osVersion;
    String ip;
    String cpuName;
    int cpuCore;
    double memory;
    double disk;
}
