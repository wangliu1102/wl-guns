package com.wl.guns.modular.system.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wl.guns.modular.system.dao.ConfigMapper;
import com.wl.guns.modular.system.model.Config;
import com.wl.guns.modular.system.service.IConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 配置表 服务实现类
 * <p>
 *
 * @author zx
 * @since 2019-06-17
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Override
    public List<Config> configList(Page<Config> page, String configName, String configKey, String configType, String startTime,
                                   String endTime) {
        return this.configMapper.configList(page, configName, configKey, configType, startTime, endTime);
    }


}
