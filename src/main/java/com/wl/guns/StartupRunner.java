package com.wl.guns;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.wl.guns.core.common.GlobalData;
import com.wl.guns.modular.system.model.Config;
import com.wl.guns.modular.system.model.Dict;
import com.wl.guns.modular.system.service.IConfigService;
import com.wl.guns.modular.system.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Date: Created in 2019/01/05 20:53
 * Copyright: Copyright (c) 2019
 * Description： 启动加载数据
 *
 * @author zx
 */
@Component
@Order(value = 1)
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private IDictService dictService;
    @Autowired
    private IConfigService configService;


    /**
     * 刷新全局对象中的数据字典
     */
    public void flushDictionary() {
        GlobalData.DICTIONARYS.clear();
        initDicts();
    }

    /**
     * 刷新全局对象中的系统参数配置
     */
    public void flushConfigs() {
        GlobalData.CONFIGPARAMETERS.clear();
        initConfigs();
    }


    @Override
    public void run(String... args) {
        initDicts();
        initConfigs();
    }

    /**
     * 初始化数据字典
     */
    private void initDicts() {
        List<Dict> list = dictService.selectList(new EntityWrapper().eq("pid", "0"));
        list.forEach(dict -> GlobalData.DICTIONARYS.put(dict.getCode(), dictService.selectList(new EntityWrapper().eq("pid", dict.getId()).orderBy("num"))));
        System.out.println(">>>>>>>>>>>>>>>数据字典初始化完成<<<<<<<<<<<<<");
    }

    /**
     * 初始化参数配置
     */
    private void initConfigs() {
        List<Config> configList = configService.selectList(null);
        configList.forEach(config -> GlobalData.CONFIGPARAMETERS.put(config.getConfigKey(), config.getConfigValue()));
        System.out.println(">>>>>>>>>>>>>>>系统参数配置初始化完成<<<<<<<<<<<<<");
    }
}
