package com.wl.guns.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.wl.guns.core.util.annotationexcel.Excel;
import com.wl.guns.core.util.annotationexcel.Excels;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author 王柳
 * @since 2017-07-11
 */
@TableName("sys_user")
@Data
public class User extends BaseModel<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 头像
     */
    private String avatar;
    /**
     * 账号
     */
    @Excel(name = "账号")
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * md5密码盐
     */
    private String salt;
    /**
     * 名字
     */
    @Excel(name = "用户名称")
    private String name;
    /**
     * 生日
     */
    @Excel(name = "生日", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    /**
     * 性别（1：男 2：女）
     */
    @Excel(name = "用户性别", readConverterExp = "1=男,2=女,3=未知")
    private Integer sex;
    /**
     * 电子邮件
     */
    @Excel(name = "用户邮箱")
    private String email;
    /**
     * 电话
     */
    @Excel(name = "手机号码")
    private String phone;
    /**
     * 角色id
     */
    private String roleid;
    /**
     * 部门id
     */
    @Excel(name = "部门编号", type = Excel.Type.IMPORT)
    private Integer deptid;
    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    @Excel(name = "帐号状态", readConverterExp = "1=启用,2=冻结,3=删除")
    private Integer status;

    /**
     * 保留字段
     */
    private Integer version;

    /**
     * 部门对象, @TableField(exist = false):不在数据表中的字段
     */
    @Excels({
            @Excel(name = "部门全称", targetAttr = "fullname", type = Excel.Type.EXPORT),
            @Excel(name = "部门简称", targetAttr = "simplename", type = Excel.Type.EXPORT)
    })
    @TableField(exist = false)
    private Dept dept;

    @TableField(exist = false)
    private String beginTime;

    @TableField(exist = false)
    private String endTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
