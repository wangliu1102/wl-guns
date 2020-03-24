package com.wl.guns.modular.system.service.impl;

import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wl.guns.config.properties.GunsProperties;
import com.wl.guns.core.common.annotion.DataScope;
import com.wl.guns.core.common.exception.BizExceptionEnum;
import com.wl.guns.core.shiro.ShiroKit;
import com.wl.guns.core.util.ReplaceUtil;
import com.wl.guns.core.util.file.FileUtils;
import com.wl.guns.modular.system.dao.NoticeMapper;
import com.wl.guns.modular.system.model.Notice;
import com.wl.guns.modular.system.model.SysNoticeFile;
import com.wl.guns.modular.system.service.INoticeService;
import com.wl.guns.modular.system.service.ISysNoticeFileService;
import com.wl.guns.modular.system.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通知表 服务实现类
 * </p>
 *
 * @author zx123
 * @since 2018-02-22
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    @Autowired
    private ISysNoticeFileService sysNoticeFileService;

    @Autowired
    private GunsProperties gunsProperties;

    @DataScope
    @Override
    public List<Map<String, Object>> list(Notice notice) {
        return this.baseMapper.list(notice);
    }

    /**
     * 获取防控信息列表
     *
     * @param page
     * @param sql
     * @return
     */
    @Override
    public List<NoticeVO> getSimpleList(Page page, String sql) {
        return this.baseMapper.getSimpleList(page, sql);
    }

    /**
     * 获取防控信息详情
     *
     * @param id
     * @return
     */
    @Override
    public NoticeVO getDetail(Integer id) {
        return this.baseMapper.getDetail(id);
    }

    /**
     * 插入后可以获取主键id
     *
     * @param newNotice
     * @return
     */
    @Override
    public boolean insertReturnId(Notice newNotice) {
        return this.baseMapper.insertReturnId(newNotice);
    }

    /**
     * 添加/修改通知
     *
     * @param notice             通知信息
     * @param files              文件
     * @param isAdd              是否添加
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean editNotice(Notice notice, MultipartFile[] files, boolean isAdd, HttpServletRequest request) {
        List<String> pathFileNames = new ArrayList<>();
        List<String> urls = new ArrayList();
        List<String> realFileNames = new ArrayList<>();
        String uploadDir = gunsProperties.getFileUploadPath();
        try {
            if (files.length > 0) {
                if (!isAdd) {
                    // 编辑：重新上传了文件
                    // 先删除旧文件 并重新上传
                    EntityWrapper<SysNoticeFile> wrapper = new EntityWrapper<>();
                    wrapper.eq("notice_id", notice.getId());
                    List<SysNoticeFile> sysNoticeFiles = sysNoticeFileService.selectList(wrapper);
                    List<String> pathFileNameList = new ArrayList<>();
                    for (SysNoticeFile sysNoticeFile : sysNoticeFiles) {
                        if (ToolUtil.isNotEmpty(sysNoticeFile.getPathFileName())) {
                            pathFileNameList.add(sysNoticeFile.getPathFileName());
                        }
                    }
                    sysNoticeFileService.delete(wrapper);
                    FileUtils.deleteNoticeFile(pathFileNameList, uploadDir);
                }
                String realFileName;
                String fileName;
                File myFile;
                //获取项目路径+端口号 比如：http://localhost:8080/
                String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
//                String basePath = GlobalData.CONFIGPARAMETERS.get("domainName") == null ? Const.DEFAULT_DOMAIN : GlobalData.CONFIGPARAMETERS.get("domainName");
                for (MultipartFile file : files) {
                    realFileName = file.getOriginalFilename();
                    fileName = System.currentTimeMillis() + "_" + realFileName;
                    myFile = new File(uploadDir + fileName);
                    FileUtils.createDirectory(myFile);
                    file.transferTo(myFile);
                    realFileNames.add(realFileName);
                    pathFileNames.add(fileName);
                    urls.add(basePath + gunsProperties.getFileUploadMapping() + fileName);

                }
            }

            notice.setContent(ReplaceUtil.replaceNotice(notice.getContent()));
            if (isAdd) {
                notice.setDeptId(ShiroKit.getUser().getDeptId() + "");
                notice.setCreateBy(ShiroKit.getUser().getId() + "");
                notice.setCreateTime(new Date());
                this.insertReturnId(notice);
            } else {
                notice.setUpdateBy(ShiroKit.getUser().getId() + "  ");
                notice.setUpdateTime(new Date());
                this.updateById(notice);
            }

            Integer noticeId = notice.getId();

            List<SysNoticeFile> noticeFiles = new ArrayList<>();
            SysNoticeFile sysNoticeFile;
            for (int i = 0, k = urls.size(); i < k; i++) {
                sysNoticeFile = new SysNoticeFile();
                sysNoticeFile.setNoticeId(noticeId);
                sysNoticeFile.setRealFileName(realFileNames.get(i));
                sysNoticeFile.setPathFileName(pathFileNames.get(i));
                sysNoticeFile.setFileUrl(urls.get(i));
                sysNoticeFile.setCreateBy(ShiroKit.getUser().getId() + "");
                sysNoticeFile.setCreateTime(new Date());
                noticeFiles.add(sysNoticeFile);
            }
            if (noticeFiles.size() > 0) {
                sysNoticeFileService.insertBatch(noticeFiles);
            }
            return true;
        } catch (Exception e) {
            // 出错，删除已上传文件
            FileUtils.deleteNoticeFile(pathFileNames, uploadDir);
            throw new ServiceException(BizExceptionEnum.SERVER_ERROR);
        }
    }


    /**
     * 删除通知
     *
     * @param noticeId 通知id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteNotice(Integer noticeId) {
        try {
            this.deleteById(noticeId);

            EntityWrapper<SysNoticeFile> wrapper = new EntityWrapper<>();
            wrapper.eq("notice_id", noticeId);
            List<SysNoticeFile> sysNoticeFiles = sysNoticeFileService.selectList(wrapper);
            String uploadDir = gunsProperties.getFileUploadPath();
            List<String> pathFileNameList = new ArrayList<>();
            for (SysNoticeFile sysNoticeFile : sysNoticeFiles) {
                if (ToolUtil.isNotEmpty(sysNoticeFile.getPathFileName())) {
                    pathFileNameList.add(sysNoticeFile.getPathFileName());
                }
            }

            sysNoticeFileService.delete(wrapper);
            FileUtils.deleteNoticeFile(pathFileNameList, uploadDir);
            return true;
        } catch (Exception e) {
            // 出错
            throw new ServiceException(BizExceptionEnum.SERVER_ERROR);
        }
    }


}
