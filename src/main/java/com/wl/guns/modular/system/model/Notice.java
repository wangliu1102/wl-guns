package com.wl.guns.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * <p>
 * 信息发布表
 * </p>
 *
 * @author zx
 * @since 2020-02-15
 */
@Data
@TableName("sys_notice")
public class Notice extends BaseModel<Notice> {
    /**
     * 标题
     */
    private String title;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 内容
     */
    private String content;

    /**
     * 机构id
     */
    private String deptId;

    /**
     * 简介
     */
    private String simpleDes;

    /**
     * 真实文件名
     */
    private String fileName;
}
