package com.example.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import lombok.Data;

@Data
@Measurement(name = "runtime")
public class RuntimeDataDO {
    @Column(tag = true)
    int clientId;
    @Column(timestamp = true)
    Instant timestamp;
    @Column
    double cpuUsage;
    @Column
    double memoryUsage;
    @Column
    double diskUsage;
    @Column
    double networkUpload;
    @Column
    double networkDownload;
    @Column
    double diskRead;
    @Column
    double diskWrite;
}
