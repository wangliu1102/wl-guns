package com.wl.guns.modular.quartz.controller;

import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import com.baomidou.mybatisplus.plugins.Page;
import com.wl.guns.core.common.constant.factory.PageFactory;
import com.wl.guns.core.common.page.PageInfoBT;
import com.wl.guns.modular.quartz.model.SysJobLog;
import com.wl.guns.modular.quartz.service.ISysJobLogService;
import com.wl.guns.modular.quartz.util.ReplaceForStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度日志操作处理
 * <p>
 * Created by 王柳
 * Date 2019/01/12 10:11
 * version:1.0
 */
@Controller
@RequestMapping("/quartz/jobLog")
public class SysJobLogController extends BaseController {

    private String PREFIX = "/quartz/jobLog/";

    @Autowired
    private ISysJobLogService jobLogService;

    /**
     * 跳转到调度日志列表
     *
     * @return
     */
    @RequestMapping("")
    public String jobLog() {
        return PREFIX + "jobLog.html";
    }

    /**
     * 查询调度日志列表
     *
     * @param sysJobLog
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object list(SysJobLog sysJobLog) {
        if (StrUtil.isNotEmpty(sysJobLog.getInvokeTarget())) {
            sysJobLog.setInvokeTarget(ReplaceForStringUtil.replace(sysJobLog.getInvokeTarget()));
        }
        Page<SysJobLog> page = new PageFactory<SysJobLog>().defaultPage();
        List<SysJobLog> list = jobLogService.selectJobLogList(page, sysJobLog);
        page.setRecords(list);
        return new PageInfoBT<>(page);
    }


    /**
     * 删除调度日志（批量）
     *
     * @param ids
     * @return
     */
    @RequestMapping("/remove")
    @ResponseBody
    public ResponseData remove(String ids) {
        jobLogService.deleteJobLogByIds(ids);
        return SUCCESS_TIP;
    }

    /**
     * 清空定时任务调度日志
     *
     * @return
     */
    @RequestMapping("/clean")
    @ResponseBody
    public ResponseData clean() {
        jobLogService.cleanJobLog();
        return SUCCESS_TIP;
    }

    /**
     * 日志详情
     *
     * @param jobLogId
     * @param model
     * @return
     */
    @RequestMapping("/detail/{jobLogId}")
    public String detail(@PathVariable("jobLogId") Long jobLogId, Model model) {
        SysJobLog jobLog = jobLogService.selectJobLogById(jobLogId);
        String status = jobLog.getStatus();
        String jobGroup = jobLog.getJobGroup();
        if (StrUtil.isNotEmpty(status)) {
            if (StrUtil.equals("0", status)) {
                status = "成功";
            } else {
                status = "失败";
            }
        }
        if (StrUtil.isNotEmpty(jobGroup)) {
            if (StrUtil.equals("DEFAULT", jobGroup)) {
                status = "默认";
            } else {
                status = "系统";
            }
        }
        model.addAttribute("jobGroupName", jobGroup);
        model.addAttribute("statusName", status);
        model.addAttribute("jobLog", jobLog);
        return PREFIX + "detail.html";
    }
}
