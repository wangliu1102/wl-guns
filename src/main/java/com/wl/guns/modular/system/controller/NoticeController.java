package com.wl.guns.modular.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.wl.guns.config.properties.GunsProperties;
import com.wl.guns.core.common.GlobalData;
import com.wl.guns.core.common.annotion.BussinessLog;
import com.wl.guns.core.common.constant.Const;
import com.wl.guns.core.common.constant.dictmap.NoticeMap;
import com.wl.guns.core.common.constant.factory.ConstantFactory;
import com.wl.guns.core.common.exception.BizExceptionEnum;
import com.wl.guns.core.log.LogObjectHolder;
import com.wl.guns.core.util.file.FileUtils;
import com.wl.guns.modular.system.model.Notice;
import com.wl.guns.modular.system.model.SysNoticeFile;
import com.wl.guns.modular.system.service.INoticeService;
import com.wl.guns.modular.system.service.ISysNoticeFileService;
import com.wl.guns.modular.system.warpper.NoticeWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 信息控制器
 *
 * @author zx
 * @Date 2019-05-09 23:02:21
 */
@Controller
@RequestMapping("/notice")
public class NoticeController extends BaseController {

    private String PREFIX = "/system/notice/";

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private ISysNoticeFileService sysNoticeFileService;

    @Autowired
    private GunsProperties gunsProperties;

    /**
     * 跳转到信息列表首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "notice.html";
    }

    /**
     * 跳转到添加信息
     */
    @RequestMapping("/notice_add")
    public String noticeAdd() {
        return PREFIX + "notice_add.html";
    }

    /**
     * 跳转到修改信息
     */
    @RequestMapping("/notice_update/{noticeId}")
    public String noticeUpdate(@PathVariable Integer noticeId, Model model) {
        Notice notice = this.noticeService.selectById(noticeId);
        model.addAttribute("notice", notice);
        LogObjectHolder.me().set(notice);
        return PREFIX + "notice_edit.html";
    }

    /**
     * 跳转到首页信息
     */
    @RequestMapping("/hello")
    public String hello() {
        List<Map<String, Object>> notices = noticeService.list(null);
        super.setAttr("noticeList", notices);
        return "/blackboard.html";
    }

    /**
     * 获取信息列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        Notice notice = new Notice();
        notice.setTitle(condition);
        notice.setContent(condition);
        List<Map<String, Object>> list = this.noticeService.list(notice);
        return super.warpObject(new NoticeWrapper(list));
    }

    /**
     * 新增信息
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @BussinessLog(value = "新增信息", key = "notice", dict = NoticeMap.class)
    public Object add(@RequestParam(required = false) MultipartFile[] files, @RequestParam String notice) {
        Notice newNotice = JSONObject.parseObject(notice, Notice.class);
        if (ToolUtil.isOneEmpty(newNotice, newNotice.getTitle(), newNotice.getSimpleDes(), newNotice.getContent())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        noticeService.editNotice(newNotice, files, true, getHttpServletRequest());

        return SUCCESS_TIP;
    }

    /**
     * 删除信息
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @BussinessLog(value = "删除信息", key = "noticeId", dict = NoticeMap.class)
    public Object delete(@RequestParam Integer noticeId) {
        //缓存信息名称
        LogObjectHolder.me().set(ConstantFactory.me().getNoticeTitle(noticeId));
        noticeService.deleteNotice(noticeId);
        return SUCCESS_TIP;
    }

    /**
     * 修改信息
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @BussinessLog(value = "修改信息", key = "notice", dict = NoticeMap.class)
    public Object update(@RequestParam(required = false) MultipartFile[] files, @RequestParam String notice) {
        Notice newNotice = JSONObject.parseObject(notice, Notice.class);
        if (ToolUtil.isOneEmpty(newNotice, newNotice.getId(), newNotice.getTitle(), newNotice.getSimpleDes(), newNotice.getContent())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        noticeService.editNotice(newNotice, files, false, getHttpServletRequest());

        return SUCCESS_TIP;
    }

    /**
     * 查看信息详情
     *
     * @param noticeId
     * @param model
     * @return
     */
    @RequestMapping("/detail/{noticeId}")
    public String detail(@PathVariable Integer noticeId, Model model) {
        Notice notice = noticeService.selectById(noticeId);
        EntityWrapper<SysNoticeFile> wrapper = new EntityWrapper<>();
        wrapper.eq("notice_id", noticeId);
        List<SysNoticeFile> sysNoticeFiles = sysNoticeFileService.selectList(wrapper);

        model.addAttribute("sysNoticeFiles", sysNoticeFiles);
        model.addAttribute("notice", notice);
        model.addAttribute("creator", ConstantFactory.me().getUserNameById(Integer.parseInt(notice.getDeptId())));
        model.addAttribute("deptName", ConstantFactory.me().getDeptName(Integer.parseInt(notice.getDeptId())));
        return PREFIX + "notice_detail.html";
    }

    @RequestMapping(value = "/uploadImg")
    @ResponseBody
    public JSONObject uploadImg(@RequestParam(value = "myFileName") MultipartFile[] myFileName, HttpServletRequest request) {
        try {
            String uploadDir = gunsProperties.getFileUploadPath();
            String suffix;
            String fileName;
            String path;
            File myFile;
            //获取项目路径+端口号 比如：http://localhost:8080/
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

//            String basePath = GlobalData.CONFIGPARAMETERS.get("domainName") == null ? Const.DEFAULT_DOMAIN : GlobalData.CONFIGPARAMETERS.get("domainName");
            List<String> stringList = new ArrayList<>();
            for (MultipartFile file : myFileName) {
                suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                fileName = DateUtil.formatDate(new Date()) + "_" + System.currentTimeMillis() + suffix;
                path = uploadDir + fileName;
                myFile = new File(path);
                FileUtils.createDirectory(myFile);
                file.transferTo(myFile);
                stringList.add(basePath + gunsProperties.getFileUploadMapping() + fileName);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errno", 0);
            jsonObject.put("data", stringList);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
