package com.wl.guns.modular.system.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.wl.guns.modular.system.model.Notice;
import com.wl.guns.modular.system.vo.NoticeVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通知表 服务类
 * </p>
 *
 * @author zx123
 * @since 2018-02-22
 */
public interface INoticeService extends IService<Notice> {

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
    List<NoticeVO> getSimpleList(Page page, String sql);

    /**
     * 获取防控信息详情
     *
     * @param id
     * @return
     */
    NoticeVO getDetail(Integer id);

    /**
     * 插入后可以获取主键id
     *
     * @param newNotice
     * @return
     */
    boolean insertReturnId(Notice newNotice);

    /**
     * 添加/修改通知
     *
     * @param notice 通知信息
     * @param files  文件
     * @param isAdd  是否添加
     * @param request
     * @return
     */

    boolean editNotice(Notice notice, MultipartFile[] files, boolean isAdd, HttpServletRequest request);

    /**
     * 删除通知
     *
     * @param noticeId 通知id
     * @return
     */
    boolean deleteNotice(Integer noticeId);
}
