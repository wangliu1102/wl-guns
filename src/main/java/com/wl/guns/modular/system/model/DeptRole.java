package com.wl.guns.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * 角色和部门关联表
 *
 * @author 王柳
 * @since 2019-05-11
 */
@TableName("sys_role_dept")
@Data
public class DeptRole {


    /**
     * 部门id
     */
    private Integer deptId;
    /**
     * 角色id
     */
    private Integer roleId;


}
