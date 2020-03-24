package com.wl.guns.modular.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 王柳
 * @date 2020/2/17 18:53
 */
@Data
public class NoticeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 机构id
     */
    private String deptName;

    /**
     * 简介
     */
    private String simpleDes;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String creator;
}
