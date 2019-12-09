package com.wl.guns.modular.system.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author 王柳
 * @since 2017-07-11
 */
@TableName("sys_role")
@Data
public class Role extends Model<Role> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 序号
     */
    private Integer num;
    /**
     * 父角色id
     */
    private Integer pid;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 部门名称
     */
    private Integer deptid;
    /**
     * 提示
     */
    private String tips;
    /**
     * 保留字段(暂时没用）
     */
    private Integer version;

    /**
     * 角色权限字符
     */
    private String roleKey;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限 5:仅本人数据权限）
     */
    private String dataScope;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
