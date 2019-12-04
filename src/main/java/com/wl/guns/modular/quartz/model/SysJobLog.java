package com.wl.guns.modular.quartz.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 王柳
 * @description 定时任务调度日志表 sys_job_log
 * @date 2019/11/26 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobLog extends Model<SysJobLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "job_log_id", type = IdType.AUTO)
    private Long jobLogId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    private String invokeTarget;

    /**
     * 日志信息
     */
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    private String status;

    /**
     * 异常信息
     */
    private String exceptionInfo;

    /**
     * 创建时间（create_time）
     */
    private Date createTime;

    /**
     * 开始时间
     */
    @TableField(exist = false)
    private String beginTime;

    /**
     * 结束时间
     */
    @TableField(exist = false)
    private String endTime;

    @Override
    protected Serializable pkVal() {
        return this.jobLogId;
    }
}
