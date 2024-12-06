package com.example.utils;

import cn.hutool.core.bean.BeanUtil;
import com.example.entity.dto.RuntimeDataDO;
import com.example.entity.vo.request.RuntimeDetailReqDTO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Nruonan
 * @description
 */
@Component
public class InfluxDbUtils {

    @Value("${spring.influxdb.url}")
    String url;
    @Value("${spring.influxdb.user}")
    String user;
    @Value("${spring.influxdb.password}")
    String password;
    private final String BUCKET = "monitor";
    private final String ORG = "monitor";
    private InfluxDBClient client;
    @PostConstruct
    public void init(){
        // 构建InfluxDB客户端
        client = InfluxDBClientFactory.create(url, user, password.toCharArray());
    }

    /**
     * 将运行时数据写入数据库
     * @param clientId 客户端ID，用于标识数据来源
     * @param requestParam 包含运行时详细信息的请求数据传输对象
     */
    public void writeRuntimeData(int clientId, RuntimeDetailReqDTO requestParam){
        // 将请求参数转换为RuntimeDataDO对象
        RuntimeDataDO runtimeDataDO = BeanUtil.toBean(requestParam, RuntimeDataDO.class);
        // 设置客户端ID
        runtimeDataDO.setClientId(clientId);
        // 设置时间戳为Instant类型，以适应数据库写入要求
        runtimeDataDO.setTimestamp(new Date(requestParam.getTimestamp()).toInstant());
        // 获取WriteApiBlocking实例，用于写入数据库
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        // 写入测量数据到数据库，指定bucket、组织、写入精度和运行时数据对象
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.NS, runtimeDataDO);
    }
}
