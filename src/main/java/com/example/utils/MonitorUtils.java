package com.example.utils;

import com.example.entity.BaseDetail;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

/**
 * @author Nruonan
 * @description
 */
@Slf4j
@Component
public class MonitorUtils {
    private final SystemInfo info = new SystemInfo();
    private final Properties properties = System.getProperties();
    public BaseDetail monitorBaseDetail(){
        OperatingSystem os = info.getOperatingSystem();
        HardwareAbstractionLayer hardware = info.getHardware();
        double memory = hardware.getMemory().getTotal() / 1024.0 / 1024 / 1024;
        double diskSize = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 / 1024 / 1024;
        String ip = Objects.requireNonNull(this.findNetworkInterface(hardware)).getIPv4addr()[0];
        return BaseDetail.builder()
            .osArch(properties.getProperty("os.arch"))
            .osName(os.getFamily())
            .osVersion(os.getVersionInfo().getVersion())
            .osBit(os.getBitness())
            .cpuName(hardware.getProcessor().getProcessorIdentifier().getName())
            .cpuCore(hardware.getProcessor().getLogicalProcessorCount())
            .memory(memory)
            .disk(diskSize)
            .ip(ip)
            .build();
    }

    /**
     * 在硬件抽象层中查找符合特定条件的网络接口
     * @param hardware 硬件抽象层对象，用于获取网络接口信息
     * @return 返回符合条件的NetworkIF对象，如果没有找到则返回null
     */
    private NetworkIF findNetworkInterface(HardwareAbstractionLayer hardware){
        try{
            // 遍历所有网络接口
            for (NetworkIF network : hardware.getNetworkIFs()){
                // 获取当前网络接口的IPv4地址
                String[] ipv4Addr = network.getIPv4addr();
                // 查询网络接口的详细信息
                NetworkInterface ni = network.queryNetworkInterface();
                // 检查网络接口是否符合预定条件
                if (!ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                    && (ni.getName().startsWith("eth") || ni.getName().startsWith("en"))
                    && ipv4Addr.length > 0){
                    // 如果符合条件，返回该网络接口
                    return network;
                }
            }
        }catch (IOException ioe){
            // 如果在读取网络接口信息时发生错误，记录错误信息
            log.error("读取网络接口信息时出错", ioe);
        }
        // 如果没有找到符合条件的网络接口，返回null
        return null;
    }
}
