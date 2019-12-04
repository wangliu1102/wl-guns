package com.wl.guns.modular.quartz.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ErrorResponseData;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.alibaba.fastjson.JSONObject;
import com.wl.guns.core.common.exception.BizExceptionEnum;
import com.wl.guns.core.shiro.ShiroKit;
import com.wl.guns.modular.quartz.model.SysJob;
import com.wl.guns.modular.quartz.service.ISysJobService;
import com.wl.guns.modular.quartz.util.ReplaceForStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 调度任务信息操作处理
 * <p>
 * Created by 王柳
 * Date 2019/01/12 10:11
 * version:1.0
 */
@Slf4j
@Controller
@RequestMapping("/quartz/job")
public class SysJobController extends BaseController {

    private String PREFIX = "/quartz/job/";

    @Autowired
    private ISysJobService jobService;

    /**
     * 跳转到调度任务信息列表
     *
     * @return
     */
    @RequestMapping("")
    public String job() {
        return PREFIX + "job.html";
    }

    /**
     * 查询调度任务信息列表
     *
     * @param job
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object list(SysJob job) {
        if (StrUtil.isNotEmpty(job.getInvokeTarget())) {
            job.setInvokeTarget(ReplaceForStringUtil.replace(job.getInvokeTarget()));
        }
        List<SysJob> list = jobService.selectJobList(job);
        return list;
    }

    /**
     * 删除定时任务
     *
     * @param job
     * @return
     */
    @RequestMapping("/remove")
    @ResponseBody
    public ResponseData remove(SysJob job) {
        int tip = 0;
        try {
            SysJob job2 = jobService.selectJobById(job.getId());
            tip = jobService.deleteJob(job2);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        if (tip > 0) {
            return SUCCESS_TIP;
        } else {
            return new ErrorResponseData(500, "删除失败");
        }
    }

    /**
     * 任务调度状态修改
     *
     * @param job
     * @return
     */
    @RequestMapping("/changeStatus")
    @ResponseBody
    public ResponseData changeStatus(SysJob job) {

        SysJob newJob = jobService.selectJobById(job.getId());
        newJob.setStatus(job.getStatus());
        int tip = 0;
        try {
            tip = jobService.changeStatus(newJob);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        if (tip > 0) {
            return SUCCESS_TIP;
        } else {
            return new ErrorResponseData(500, "操作失败");
        }

    }

    /**
     * 任务调度立即执行一次
     *
     * @param job
     * @return
     */
    @RequestMapping("/run")
    @ResponseBody
    public ResponseData run(SysJob job) {

        try {
            jobService.run(job);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return SUCCESS_TIP;
    }

    /**
     * 跳转到新增调度页面
     *
     * @return
     */
    @RequestMapping("/job_add")
    public String add() {
        return PREFIX + "job_add.html";
    }

    /**
     * 新增保存调度
     *
     * @param job
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    public ResponseData addSave(@Valid SysJob job, BindingResult result) {
        if (result.hasErrors()) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        if (StrUtil.isNotEmpty(job.getInvokeTarget())) {
            job.setInvokeTarget(ReplaceForStringUtil.replace(job.getInvokeTarget()));
        }
        job.setCreateBy(ShiroKit.getUser().getAccount());
        job.setCreateTime(new Date());
        try {
            jobService.insertJob(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS_TIP;
    }

    /**
     * 跳转到修改调度页面
     *
     * @param jobId
     * @param model
     * @return
     */
    @RequestMapping("/job_edit/{jobId}")
    public String edit(@PathVariable("jobId") Long jobId, Model model) {
        SysJob job = jobService.selectJobById(jobId);
        model.addAttribute("job", job);
        return PREFIX + "job_edit.html";
    }

    /**
     * 修改保存调度
     *
     * @param job
     * @param result
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public ResponseData editSave(@Valid SysJob job, BindingResult result) {
        if (result.hasErrors()) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        if (StrUtil.isNotEmpty(job.getInvokeTarget())) {
            job.setInvokeTarget(ReplaceForStringUtil.replace(job.getInvokeTarget()));
        }
        job.setUpdateBy(ShiroKit.getUser().getAccount());
        job.setUpdateTime(new Date());
        try {
            jobService.updateJob(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS_TIP;
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param job
     * @return
     */
    @RequestMapping("/checkCronExpressionIsValid")
    @ResponseBody
    public Object checkCronExpressionIsValid(SysJob job) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("valid", jobService.checkCronExpressionIsValid(job.getCronExpression()));
        return jsonObject;
    }

    /**
     * 定时任务详情
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        SysJob job = jobService.selectJobById(id);
        String status = job.getStatus();
        String jobGroup = job.getJobGroup();
        String concurrent = job.getConcurrent();
        String misfirePolicy = job.getMisfirePolicy();
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
        if (StrUtil.isNotEmpty(concurrent)) {
            if (StrUtil.equals("0", concurrent)) {
                concurrent = "允许";
            } else {
                concurrent = "禁止";
            }
        }
        if (StrUtil.isNotEmpty(misfirePolicy)) {
            if (StrUtil.equals("1", misfirePolicy)) {
                misfirePolicy = "立即执行";
            } else if (StrUtil.equals("2", misfirePolicy)) {
                misfirePolicy = "执行一次";
            } else {
                misfirePolicy = "放弃执行";
            }
        }
        model.addAttribute("jobGroupName", jobGroup);
        model.addAttribute("statusName", status);
        model.addAttribute("concurrentName", concurrent);
        model.addAttribute("misfirePolicyName", misfirePolicy);
        model.addAttribute("job", job);
        return PREFIX + "detail.html";
    }
}
