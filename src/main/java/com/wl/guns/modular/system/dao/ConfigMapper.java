package com.wl.guns.modular.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.wl.guns.modular.system.model.Config;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 配置表 Mapper 接口
 * </p>
 *
 * @author zx
 * @since 2019-06-14
 */
public interface ConfigMapper extends BaseMapper<Config> {
    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    List<Config> configList(@Param("page") Page<Config> page, @Param("configName") String configName, @Param("configKey") String configKey,
                            @Param("configType") String configType, @Param("startTime") String startTime, @Param("endTime") String endTime);


}
