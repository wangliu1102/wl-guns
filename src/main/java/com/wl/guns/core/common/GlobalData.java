package com.wl.guns.core.common;

import com.wl.guns.modular.system.model.Dict;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: zx
 * Date: Created in 2020/02/15 9:30
 * Copyright: Copyright (c) 2020
 * Description： 系统全局数据
 */
public class GlobalData {

    /**
     * 保存系统配置字典值
     */
    public static Map<String, List<Dict>> DICTIONARYS = new HashMap<>();

    /**
     * 保存系统参数配置
     */
    public static Map<String, String> CONFIGPARAMETERS = new HashMap<>();

    /**
     * 保存员工健康数据
     */
    public static Map<String, Map<String, String>> HEALTH_MAP = new HashMap<>();

}
