package com.example.utils;

import com.example.entity.BaseDetail;
import com.example.entity.RuntimeDetail;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
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
    public RuntimeDetail monitorRuntimeDetail(){
        double statisticTime = 0.5;
         try{
             HardwareAbstractionLayer hardware = info.getHardware();
             NetworkIF ni = Objects.requireNonNull(this.findNetworkInterface(hardware));
             CentralProcessor processor = hardware.getProcessor();
             double upload = ni.getBytesSent();
             double download = ni.getBytesRecv();
             double read = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum();
             double write = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum();
             long[] systemCpuLoadTicks = processor.getSystemCpuLoadTicks();
             Thread.sleep((long) (statisticTime * 1000));
             ni = Objects.requireNonNull(this.findNetworkInterface(hardware));
             upload = (ni.getBytesSent() - upload) / statisticTime;
             download = (ni.getBytesRecv() - download) / statisticTime;
             read = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum() - read) / statisticTime;
             write = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum() - write) / statisticTime;
             double memory = (hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) / 1024.0 / 1024 / 1024;
             double disk = Arrays.stream(File.listRoots())
                 .mapToLong(file -> file.getTotalSpace()- file.getFreeSpace()).sum() / 1024.0 / 1024 / 1024;
             return RuntimeDetail.builder()
                 .cpuUsage(this.calculateCpuUsage(processor, systemCpuLoadTicks))
                 .memoryUsage(memory)
                 .diskUsage(disk)
                 .networkDownload(download / 1024)
                 .networkUsage(upload / 1024)
                 .diskRead(read / 1024 / 1024)
                 .diskWrite(write / 1024 / 1024)
                 .timestamp(new Date().getTime())
                 .build();
         } catch (Exception e) {
             log.error("读取运行时数据出现问题",e);
         }
         return null;
    }
    private double calculateCpuUsage(CentralProcessor processor, long[] prevTicks){
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
            - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
            - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
            - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
            - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
            - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long cUser = ticks[CentralProcessor.TickType.USER.getIndex()]
            - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
            - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
            - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = cUser + nice + cSys + idle + ioWait + irq + softIrq + steal;
        return (cSys + cUser) * 1.0 / totalCpu;
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
