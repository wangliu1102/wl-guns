package com.wl.guns.core.util.poiexcel;

/**
 * @description  EXCEL常量类
 * @author 王柳
 * @date 2019/11/22 10:44
 */
public class ExcelConstant {

    /**
     * 控制导出的session标志
     */
    public static final String EXPORT_FLAG = "exportFlag";

    /**
     * 导出excel标志，用于loading图标的显示和隐藏，0代表导出没有结束，-1代表导出结束
     */
    public static final String RESULT_CODE = "resultCode";

    /**
     * 导入excel成功标志
     */
    public static final String IMPORT_SUCCESS = "01";

    /**
     * 导入excel失败标志(文件为空)
     */
    public static final String IMPORT_NULL = "02";

    /**
     * 导入excel失败标志
     */
    public static final String IMPORT_ERROR = "03";

    /**
     * 每个sheet存储的记录数 100W
     */
    public static final Integer PER_SHEET_ROW_COUNT = 1000000;

    /**
     * 每次向EXCEL写入的记录数(查询每页数据大小) 20W
     */
    public static final Integer PER_WRITE_ROW_COUNT = 200000;


    /**
     * 每个sheet的写入次数 5
     */
    public static final Integer PER_SHEET_WRITE_COUNT = PER_SHEET_ROW_COUNT / PER_WRITE_ROW_COUNT;

}
