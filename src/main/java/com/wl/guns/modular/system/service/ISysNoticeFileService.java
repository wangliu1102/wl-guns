package com.wl.guns.modular.system.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.wl.guns.modular.system.model.SysNoticeFile;

import java.util.List;
import java.util.Map;

/**
 * 通知上传文件列表(SysNoticeFile)  服务接口
 *
 * @author 王柳
 * @date 2020-02-24 18:04:26
 */
public interface ISysNoticeFileService extends IService<SysNoticeFile> {

    /**
     * 通过实体作为筛选条件查询
     *
     * @param page          分页对象
     * @param sysNoticeFile sysNoticeFile 实例对象
     * @return 对象列表
     */
    List<Map<String, Object>> queryAll(Page<SysNoticeFile> page, SysNoticeFile sysNoticeFile);


}
