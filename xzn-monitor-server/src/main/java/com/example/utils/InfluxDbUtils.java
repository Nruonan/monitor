package com.example.utils;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.dto.RuntimeDataDO;
import com.example.entity.vo.request.RuntimeDetailReqDTO;
import com.example.entity.vo.response.RuntimeHistoryRespDTO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    /**
     * 读取客户端过去一小时的运行历史记录
     * @param clientId 客户端ID，用于过滤查询结果，只返回指定客户端的运行历史记录
     * @return RuntimeHistoryRespDTO 包含运行历史记录的响应对象，如果过去一小时内没有运行记录，则返回一个空的响应对象
     */
    public RuntimeHistoryRespDTO readRuntimeHistory(int clientId) {
        // 初始化响应对象
        RuntimeHistoryRespDTO dto = new RuntimeHistoryRespDTO();
        
        // 定义查询字符串，用于从InfluxDB数据库中获取过去一小时内measurement为"runtime"且clientId匹配的记录
        String query = """
            from(bucket: "%s")
            |> range(start: %s)
            |> filter(fn: (r) => r["_measurement"] == "runtime")
            |> filter(fn: (r) => r["clientId"] == "%s")
            """;
        
        // 格式化查询字符串，插入数据库桶名称和时间范围
        String format = String.format(query, BUCKET, "-1h",clientId);
        
        // 执行查询并获取结果，结果为一个包含多个FluxTable的列表
        List<FluxTable> tables = client.getQueryApi().query(format, ORG);
        
        // 获取查询结果的大小，如果为0则直接返回空的响应对象
        int size = tables.size();
        if (size == 0)return dto;
        
        // 获取第一个表格中的所有记录
        List<FluxRecord> records = tables.get(0).getRecords();
        
        // 遍历所有记录，将每条记录的时间戳和字段值封装成JSONObject对象，然后添加到响应对象的列表中
        for(int i = 0; i < records.size(); i++){
            JSONObject object = new JSONObject();
            // 添加记录的时间戳到JSONObject对象
            object.put("timestamp", records.get(i).getTime());
            // 遍历所有表格，将每条记录的字段名和字段值添加到JSONObject对象中
            for (int j =0; j < size; j++){
                FluxRecord fluxRecord = tables.get(j).getRecords().get(i);
                object.put(fluxRecord.getField(),fluxRecord.getValue());
            }
            // 将封装好的JSONObject对象添加到响应对象的列表中
            dto.getList().add(object);
        }
        return dto;
    }
}
