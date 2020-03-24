package com.wl.guns.modular.system.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.wl.guns.modular.system.model.Config;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 配置表 服务类
 * </p>
 *
 * @author zx
 * @since 2019-06-17
 */
public interface IConfigService extends IService<Config> {
    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    List<Config> configList(@Param("page") Page<Config> page, @Param("configName") String configName, @Param("configKey") String configKey,
                            @Param("configType") String configType, @Param("startTime") String startTime, @Param("endTime") String endTime);


}


