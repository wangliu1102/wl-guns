package com.wl.guns.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * 通知文件列表类
 *
 * @author 王柳
 * @date 2020/2/24 17:31
 */
@Data
@TableName("sys_notice_file")
public class SysNoticeFile extends BaseModel<SysNoticeFile> {

    /**
     * 通知id
     */
    private Integer noticeId;

    /**
     * 真实文件名
     */
    private String realFileName;

    /**
     * 路径文件名
     */
    private String pathFileName;

    /**
     * 文件路径
     */
    private String fileUrl;
}
