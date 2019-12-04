package com.wl.guns.modular.quartz.model;

import cn.hutool.core.util.StrUtil;
import com.wl.guns.modular.BaseModel;
import com.wl.guns.modular.quartz.constant.ScheduleConstants;
import com.wl.guns.modular.quartz.util.CronUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author 王柳
 * @description 定时任务调度表 sys_job
 * @date 2019/11/26 10:01
 */
@Data
public class SysJob extends BaseModel<SysJob> {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(min = 0, max = 64, message = "任务名称不能超过64个字符")
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(min = 0, max = 1000, message = "调用目标字符串长度不能超过500个字符")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(min = 0, max = 255, message = "Cron执行表达式不能超过255个字符")
    private String cronExpression;

    /**
     * cron计划策略 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
     */
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    private String concurrent;

    /**
     * 备注
     */
    private String remark;

    /**
     * 任务状态（0正常 1暂停）
     */
    private String status;

    public Date getNextValidTime() {
        if (StrUtil.isNotEmpty(cronExpression)) {
            return CronUtils.getNextExecution(cronExpression);
        }
        return null;
    }
}
