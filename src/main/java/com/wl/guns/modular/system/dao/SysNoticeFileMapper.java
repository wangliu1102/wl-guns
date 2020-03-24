package com.wl.guns.modular.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.wl.guns.modular.system.model.SysNoticeFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通知上传文件列表(sys_notice_file)  数据库访问层
 *
 * @author 王柳
 * @date 2020-02-24 18:04:26
 */
public interface SysNoticeFileMapper extends BaseMapper<SysNoticeFile> {

    /**
     * 通过实体作为筛选条件查询
     *
     * @param page          分页对象
     * @param sysNoticeFile sysNoticeFile 实例对象
     * @return 对象列表
     */
    List<Map<String, Object>> queryAll(@Param("page") Page<SysNoticeFile> page, SysNoticeFile sysNoticeFile);

}
