package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONObject;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class RuntimeDetailsRespDTO {
    double disk;
    double memory;
    List<JSONObject> list = new LinkedList<>();
}
