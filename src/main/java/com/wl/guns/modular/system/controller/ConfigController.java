package com.wl.guns.modular.system.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.plugins.Page;
import com.wl.guns.core.common.annotion.BussinessLog;
import com.wl.guns.core.common.constant.dictmap.ConfigDict;
import com.wl.guns.core.common.constant.factory.PageFactory;
import com.wl.guns.core.common.exception.BizExceptionEnum;
import com.wl.guns.core.common.page.PageInfoBT;
import com.wl.guns.core.shiro.ShiroKit;
import com.wl.guns.StartupRunner;
import com.wl.guns.modular.system.model.Config;
import com.wl.guns.modular.system.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * 参数设置控制器
 *
 * @author zx
 * @since 2019-06-17
 */
@Controller
@RequestMapping("/config")
public class ConfigController extends BaseController {

    private String PREFIX = "/system/config/";

    @Autowired
    private IConfigService configService;
    @Autowired
    private StartupRunner startupRunner;

    /**
     * 跳转到参数设置首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "config.html";
    }

    /**
     * 跳转到添加参数
     */
    @RequestMapping("/config_add")
    public String businessAdd() {
        return PREFIX + "config_add.html";
    }

    /**
     * 跳转到修改参数
     */
    @RequestMapping("/config_update/{id}")
    public String businessUpdate(@PathVariable Integer id, Model model) {
        Config config = configService.selectById(id);
        model.addAttribute("Config", config);
        return PREFIX + "config_edit.html";
    }

    /**
     * 获取参数列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String configName, String configKey, String configType, String startTime, String endTime) {
        Page<Config> page = new PageFactory<Config>().defaultPage();
        List<Config> result = configService.configList(page, configName, configKey, configType, startTime, endTime);
        page.setRecords(result);
        return new PageInfoBT<>(page);
    }

    /**
     * 新增参数
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @BussinessLog(value = "新增参数", key = "id", dict = ConfigDict.class)
    public Object add(Config config) {
        if (ToolUtil.isOneEmpty(config.getConfigName(), config.getConfigKey(), config.getConfigValue(),
                config.getConfigType(), config.getRemark())) {
            throw new ServiceException(BizExceptionEnum.DB_RESOURCE_NULL);
        }
        config.setCreateBy(ShiroKit.getUser().getId().toString());
        config.setCreateTime(new Date());
        configService.insert(config);
        startupRunner.flushConfigs();
        return SUCCESS_TIP;

    }

    /**
     * 删除参数
     */
    @BussinessLog(value = "删除参数", key = "id", dict = ConfigDict.class)
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer id) {
        if (id == null) {
            throw new ServiceException(BizExceptionEnum.DB_RESOURCE_NULL);
        }

        Config config = configService.selectById(id);

        configService.deleteById(config);
        startupRunner.flushConfigs();
        return SUCCESS_TIP;
    }

    /**
     * 修改参数
     */
    @BussinessLog(value = "修改参数", key = "id", dict = ConfigDict.class)
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(Config config) {
        if (ToolUtil.isOneEmpty(config.getId(), config.getConfigName(), config.getConfigKey(), config.getConfigValue(),
                config.getConfigType(), config.getRemark())) {
            throw new ServiceException(BizExceptionEnum.DB_RESOURCE_NULL);
        }
        Config conf = configService.selectById(config.getId());
        conf.setConfigName(config.getConfigName());
        conf.setConfigKey(config.getConfigKey());
        conf.setConfigValue(config.getConfigValue());
        conf.setConfigType(config.getConfigType());
        conf.setRemark(config.getRemark());
        configService.updateById(conf);
        startupRunner.flushConfigs();
        return SUCCESS_TIP;
    }

}
