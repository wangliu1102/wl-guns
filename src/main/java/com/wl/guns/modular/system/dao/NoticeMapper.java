package com.wl.guns.modular.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.wl.guns.modular.system.model.Notice;
import com.wl.guns.modular.system.vo.NoticeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通知表 Mapper 接口
 * </p>
 *
 * @author zx
 * @since 2019-05-11
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 获取通知列表
     */
    List<Map<String, Object>> list(Notice notice);

    /**
     * 获取防控信息列表
     *
     * @param page
     * @param sql
     * @return
     */
    List<NoticeVO> getSimpleList(Page page, @Param("sql") String sql);

    /**
     * 获取防控信息详情
     *
     * @param id
     * @return
     */
    NoticeVO getDetail(@Param("query") Integer id);

    /**
     * 插入后可以获取主键id
     *
     * @param newNotice
     * @return
     */
    boolean insertReturnId(Notice newNotice);
}
