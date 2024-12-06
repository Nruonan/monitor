package com.example.task;

import com.example.entity.RuntimeDetail;
import com.example.utils.MonitorUtils;
import com.example.utils.NetUtils;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author Nruonan
 * @description
 */
@Component
public class MonitorJobBean extends QuartzJobBean {

    @Resource
    MonitorUtils monitorUtils;
    @Resource
    NetUtils netUtils;

    protected void executeInternal(JobExecutionContext context) {
        RuntimeDetail runtimeDetail = monitorUtils.monitorRuntimeDetail();
        netUtils.updateRuntimeDetails(runtimeDetail);
    }
}
