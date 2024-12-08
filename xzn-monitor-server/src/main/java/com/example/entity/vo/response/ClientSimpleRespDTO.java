package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class ClientSimpleRespDTO {
    int id;
    String name;
    String location;
    String osName;
    String osVersion;
    String ip;
}
