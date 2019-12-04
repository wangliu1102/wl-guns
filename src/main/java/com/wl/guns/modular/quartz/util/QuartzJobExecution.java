package com.wl.guns.modular.quartz.util;

import com.wl.guns.modular.quartz.model.SysJob;
import org.quartz.JobExecutionContext;

/**
 * @author 王柳
 * @description 定时任务处理（允许并发执行）
 * @date 2019/11/26 13:53
 */
public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
