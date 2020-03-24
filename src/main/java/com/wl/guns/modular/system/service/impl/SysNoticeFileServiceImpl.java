package com.wl.guns.modular.system.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wl.guns.modular.system.dao.SysNoticeFileMapper;
import com.wl.guns.modular.system.model.SysNoticeFile;
import com.wl.guns.modular.system.service.ISysNoticeFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 通知上传文件列表(SysNoticeFile)  服务实现类
 *
 * @author 王柳
 * @date 2020-02-24 18:04:26
 */
@Slf4j
@Service
public class SysNoticeFileServiceImpl extends ServiceImpl<SysNoticeFileMapper, SysNoticeFile> implements ISysNoticeFileService {

    @Resource
    private SysNoticeFileMapper sysNoticeFileMapper;

    /**
     * 通过实体作为筛选条件查询
     *
     * @param page 分页对象, sysNoticeFile 实例对象
     * @return 对象列表
     */
    @Override
    public List<Map<String, Object>> queryAll(Page<SysNoticeFile> page, SysNoticeFile sysNoticeFile) {
        return sysNoticeFileMapper.queryAll(page, sysNoticeFile);
    }

}
