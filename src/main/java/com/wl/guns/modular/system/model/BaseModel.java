package com.wl.guns.modular.system.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.wl.guns.core.util.annotationexcel.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 王柳
 * @description 基础类
 * @date 2019/11/26 10:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseModel<T extends BaseModel> extends Model<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Excel(name = "用户序号", cellType = Excel.ColumnType.NUMERIC, prompt = "用户编号")
    @TableId(value = "id", type = IdType.AUTO)
    protected Integer id;

    /**
     * 创建时间（create_time）
     */
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    protected Date createTime;

    /**
     * 创建人(create_by)
     */
    protected String createBy;

    /**
     * 修改时间 (update_time)
     */
    protected Date updateTime;

    /**
     * 修改人(update_by)
     */
    protected String updateBy;

    /**
     * 请求参数
     */
    @TableField(exist = false)
    private Map<String, Object> params;


    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
