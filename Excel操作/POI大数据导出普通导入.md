# 简介

具体参考https://github.com/wangliu1102/wl-guns中的用户管理模块的POI导入、导出功能

工具包在com.wl.guns.core.util.poiexcel路径下

![img](../img/357f2968-1804-48a9-812b-366b1661e317.png)

大数据量导出使用**SXSSFWorkbook**工作簿，并分Sheet页查询导出，每次查询指定数据量写入工作簿，当数据量超过指定范围，新建Sheet页，继续导出。

导入功能，需要指定导入模板，通过读取Excel每行每列的内容值，来得到需要导出的对象，并插入数据库当中。

需要引入依赖包

 

```
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>3.17</version>
        </dependency>
```

# 工具类代码

## EXCEL常量类：ExcelConstant

 

```
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

```

## EXCEL写数据委托类：WriteExcelDataDelegated

 

```
package com.wl.guns.core.util.poiexcel;

import org.apache.poi.xssf.streaming.SXSSFSheet;

/**
 * @description  EXCEL写数据委托类
 * @author 王柳
 * @date 2019/11/22 10:44
 */
public interface WriteExcelDataDelegated {
    /**
     * EXCEL写数据委托类  针对不同的情况自行实现
     *
     * @param eachSheet     指定SHEET
     * @param startRowCount 开始行
     * @param endRowCount   结束行
     * @param currentPage   分批查询开始页
     * @param pageSize      分批查询数据量
     * @throws Exception
     */
    Boolean writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception;

}
```

## POI导入导出工具类：PoiUtil

 

```
package com.wl.guns.core.util.poiexcel;

import cn.stylefeng.roses.core.util.ToolUtil;
import com.wl.guns.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.Cell.*;

/**
 * @author 王柳
 * @description SXSSFWorkbook导入导出Excel工具类（导出针对大数据量的情况）
 * @date 2019/11/22 10:44
 */
@Slf4j
public class PoiUtil {

    /**
     * 初始化EXCEL(sheet个数和标题)
     *
     * @param totalRowCount 总记录数
     * @param titles        标题集合
     * @return SXSSFWorkbook对象
     * @auther 王柳
     */
    public static SXSSFWorkbook initExcel(Integer totalRowCount, String[] titles) {

        // 在内存当中保持 100 行 , 超过的数据放到硬盘中
        SXSSFWorkbook wb = new SXSSFWorkbook(100);

        Integer sheetCount = ((totalRowCount % ExcelConstant.PER_SHEET_ROW_COUNT == 0) ?
                (totalRowCount / ExcelConstant.PER_SHEET_ROW_COUNT) : (totalRowCount / ExcelConstant.PER_SHEET_ROW_COUNT + 1));

        SXSSFSheet sheet;
        SXSSFRow headRow;
        SXSSFCell headRowCell;
        // 根据总记录数创建sheet并分配标题
        for (int i = 0; i < sheetCount; i++) {
            sheet = wb.createSheet("sheet" + (i + 1));
            headRow = sheet.createRow(0);

            // 调整列宽以适合内容,配合sheet.autoSizeColumn方法使用
            sheet.trackAllColumnsForAutoSizing();

            for (int j = 0; j < titles.length; j++) {
                headRowCell = headRow.createCell(j);
                headRowCell.setCellValue(titles[j]);
                headRowCell.setCellStyle(getHeadStyle(wb));
                //  让列宽随着导出的列长自动适应 ,调整列宽度
                sheet.autoSizeColumn((short) j);
            }
        }

        return wb;
    }

    /**
     * 下载EXCEL到本地指定的文件夹
     *
     * @param wb         EXCEL对象SXSSFWorkbook
     * @param exportPath 导出路径
     * @auther 王柳
     */
    public static void downLoadExcelToLocalPath(SXSSFWorkbook wb, String exportPath) {
        FileOutputStream fops = null;
        try {
            fops = new FileOutputStream(exportPath);
            wb.write(fops);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != wb) {
                try {
                    wb.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (null != fops) {
                try {
                    fops.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 下载EXCEL到浏览器
     *
     * @param wb       EXCEL对象SXSSFWorkbook
     * @param response
     * @param fileName 文件名称
     * @throws IOException
     * @auther 王柳
     */
    public static void downLoadExcelToWebsite(SXSSFWorkbook wb, HttpServletRequest request, HttpServletResponse response, String fileName) throws IOException {

        //设置下载的文件名
        response.setHeader("Content-disposition", "attachment; filename="
                + new String((fileName + ".xlsx").getBytes("utf-8"), "ISO8859-1"));

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            request.getSession().removeAttribute(ExcelConstant.EXPORT_FLAG);
            wb.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != wb) {
                try {
                    wb.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 导出Excel到本地指定路径
     *
     * @param totalRowCount           总记录数
     * @param titles                  标题
     * @param exportPath              导出路径
     * @param writeExcelDataDelegated 向EXCEL写数据/处理格式的委托类 自行实现
     * @throws Exception
     * @auther 王柳
     */
    public static final void exportExcelToLocalPath(Integer start, Integer totalRowCount, String[] titles, String exportPath, WriteExcelDataDelegated writeExcelDataDelegated) throws Exception {

        log.info("开始导出：" + DateUtil.getTime(new Date()));
        // 初始化EXCEL
        SXSSFWorkbook wb = PoiUtil.initExcel(totalRowCount, titles);

        Boolean isDownLoadExcel = isDownLoadExcel(start, totalRowCount, wb, writeExcelDataDelegated);
        if (isDownLoadExcel) {
            // 下载EXCEL
            PoiUtil.downLoadExcelToLocalPath(wb, exportPath);
            log.info("导出完成：" + DateUtil.getTime(new Date()));
        } else {
            log.info("无数据，不导出：" + DateUtil.getTime(new Date()));
        }

    }


    /**
     * 导出Excel到浏览器
     *
     * @param response
     * @param totalRowCount           总记录数
     * @param fileName                文件名称
     * @param titles                  标题
     * @param writeExcelDataDelegated 向EXCEL写数据/处理格式的委托类 自行实现
     * @throws Exception
     * @auther 王柳
     */
    public static final void exportExcelToWebsite(Integer start, HttpServletRequest request, HttpServletResponse response, Integer totalRowCount, String fileName, String[] titles, WriteExcelDataDelegated writeExcelDataDelegated) throws Exception {

        log.info("开始导出：" + DateUtil.getTime(new Date()));

        // 初始化EXCEL
        SXSSFWorkbook wb = PoiUtil.initExcel(totalRowCount, titles);

        Boolean isDownLoadExcel = isDownLoadExcel(start, totalRowCount, wb, writeExcelDataDelegated);

        if (isDownLoadExcel) {
            // 下载EXCEL
            PoiUtil.downLoadExcelToWebsite(wb, request, response, fileName);
            log.info("导出完成：" + DateUtil.getTime(new Date()));
        } else {
            log.info("无数据，不导出：" + DateUtil.getTime(new Date()));
        }

    }

    /**
     * @Description 是否可以下载excel
     * @auther 王柳
     * @date 2019/6/4 19:51
     * @params [start, totalRowCount, wb, writeExcelDataDelegated]
     */
    public static Boolean isDownLoadExcel(Integer start, Integer totalRowCount, SXSSFWorkbook wb, WriteExcelDataDelegated writeExcelDataDelegated) throws Exception {

        Boolean isDownLoadExcel = false;

        // 调用委托类分批写数据
        int sheetCount = wb.getNumberOfSheets();

        int maxCurrentPage = ((totalRowCount % ExcelConstant.PER_WRITE_ROW_COUNT == 0) ?
                (totalRowCount / ExcelConstant.PER_WRITE_ROW_COUNT) : (totalRowCount / ExcelConstant.PER_WRITE_ROW_COUNT + 1));
        SXSSFSheet eachSheet;
        for (int i = 0; i < sheetCount; i++) {
            eachSheet = wb.getSheetAt(i);

            for (int j = 1; j <= start; j++) {

                int currentPage = i * ExcelConstant.PER_SHEET_WRITE_COUNT + j;
                if (currentPage > maxCurrentPage) {
                    break;
                }
                int pageSize = ExcelConstant.PER_WRITE_ROW_COUNT;
                int startRowCount = (j - 1) * ExcelConstant.PER_WRITE_ROW_COUNT + 1;
                int endRowCount = startRowCount + pageSize - 1;


                isDownLoadExcel = writeExcelDataDelegated.writeExcelData(eachSheet, startRowCount, endRowCount, currentPage, pageSize);

            }
        }
        return isDownLoadExcel;
    }

    /**
     * @Description 获取查询次数
     * @auther 王柳
     * @date 2019/6/4 15:03
     * @params [totalRowCount]
     */
    public static Integer getStart(Integer totalRowCount) {
        Integer start = 0;
        if (totalRowCount <= ExcelConstant.PER_WRITE_ROW_COUNT) {
            // 数据小于20万 查一次
            start = 1;
        } else if (totalRowCount > ExcelConstant.PER_WRITE_ROW_COUNT && totalRowCount < ExcelConstant.PER_SHEET_ROW_COUNT) {
            // 数据大于20万小于100万 查2-4次
            start = ((totalRowCount % ExcelConstant.PER_WRITE_ROW_COUNT == 0) ?
                    (totalRowCount / ExcelConstant.PER_WRITE_ROW_COUNT) : (totalRowCount / ExcelConstant.PER_WRITE_ROW_COUNT + 1));
        } else {
            // 数据大于等于100万 查5次
            start = ExcelConstant.PER_SHEET_WRITE_COUNT;
        }

        return start;
    }

    /**
     * @Description 将数据写入Excel工作簿Workbook
     * @auther 王柳
     * @date 2019/6/4 15:29
     * @params [datas, result, eachSheet, startRowCount, endRowCount]
     */
    public static Boolean writeWorkbook(String[] datas, List<Map<String, Object>> result, SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount) {
        if (!CollectionUtils.isEmpty(result)) {
            SXSSFRow eachDataRow;
            SXSSFCell cell;
            for (int i = startRowCount; i <= endRowCount; i++) {
                eachDataRow = eachSheet.createRow(i);

                // 调整列宽以适合内容,配合sheet.autoSizeColumn方法使用
//                eachSheet.trackAllColumnsForAutoSizing();
                if ((i - startRowCount) < result.size()) {
                    Map<String, Object> map = result.get(i - startRowCount);

                    for (int j = 0; j < datas.length; j++) {
                        cell = eachDataRow.createCell(j);

                        // 设置单元格样式会严重影响性能
//                        cell.setCellStyle(getBodyStyle(eachSheet.getWorkbook()));
                        String value = objectToString(map.get(datas[j]));
                        if (ToolUtil.isEmpty(value)) {
                            cell.setCellValue("");
                        } else {
                            cell.setCellValue(value);
                        }
                        //调整列宽度
//                        eachSheet.autoSizeColumn((short) j);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * @Description 将Object转换为String
     * @auther 王柳
     * @date 2019/6/5 8:27
     * @params [o]
     */
    private static String objectToString(Object o) {
        if (o == null) {
            return "";
        } else if (o instanceof String) {
            return (String) o;
        } else if (o instanceof Integer) {
            return String.valueOf((Integer) o);
        } else if (o instanceof Long) {
            return String.valueOf((Long) o);
        } else if (o instanceof Double) {
            return String.valueOf((Double) o);
        } else if (o instanceof Float) {
            return String.valueOf((Float) o);
        } else if (o instanceof Boolean) {
            return String.valueOf((Boolean) o);
        } else if (o instanceof Date) {
            // yyyy-MM-dd
//            return DateUtil.getDay((Date) o);
            // yyyy-MM-dd HH:mm:ss
            return DateUtil.getTime((Date) o);
        } else {
            return "";
        }
    }


    /**
     * @Description 设置表头的单元格样式
     * @auther 王柳
     * @date 2019/6/5 8:27
     * @params [workbook]
     */
    public static CellStyle getHeadStyle(SXSSFWorkbook workbook) {
        // 创建单元格样式
        CellStyle cellStyle = workbook.createCellStyle();

        // 设置单元格的背景颜色为淡蓝色
        cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        // 设置填充字体的样式
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 设置单元格居中对齐
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 设置单元格垂直居中对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 如果文本太长，控制是否应自动调整单元格以缩小以适合
//         cellStyle.setShrinkToFit(true);

        // 创建单元格内容显示不下时自动换行
        cellStyle.setWrapText(true);

        // 设置单元格字体样式
        XSSFFont font = (XSSFFont) workbook.createFont();
        // 这是字体加粗
        font.setBold(true);
        // 设置字体的样式
        font.setFontName("宋体");
        // 设置字体的大小
        font.setFontHeight(12);
        // 将字体填充到表格中去
        cellStyle.setFont(font);

        // 设置单元格边框为细线条（上下左右）
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        return cellStyle;

    }

    /**
     * @Description 设置表体的单元格样式
     * @auther 王柳
     * @date 2019/6/5 8:27
     * @params [workbook]
     */
    public static CellStyle getBodyStyle(SXSSFWorkbook workbook) {
        // 创建单元格样式
        CellStyle cellStyle = workbook.createCellStyle();

        // 设置单元格居中对齐
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 设置单元格居中对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 创建单元格内容不显示自动换行
        cellStyle.setWrapText(true);

        // 设置单元格字体样式
        XSSFFont font = (XSSFFont) workbook.createFont();
        // 设置字体
        font.setFontName("宋体");
        // 设置字体的大小
        font.setFontHeight(10);
        // 将字体添加到表格中去
        cellStyle.setFont(font);

        // 设置单元格边框为细线条
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        return cellStyle;

    }

    /**
     * @Description 导入Excel时，读取Excel中的内容
     * @auther 王柳
     * @date 2019/6/5 8:26
     * @params [is]
     */
    public static List<List<String>> readExcelContent(InputStream is) {
        List<List<String>> content = new ArrayList<>();
        XSSFWorkbook wb;
        XSSFSheet sheet;
        XSSFRow row;
        String str;
        try {
            wb = new XSSFWorkbook(is);
            sheet = wb.getSheetAt(0);
            // 得到总行数
            int rowNum = sheet.getLastRowNum();
            row = sheet.getRow(0);
            int colNum = row.getPhysicalNumberOfCells();
            // 正文内容应该从第二行开始,第一行为表头的标题
            for (int i = 1; i <= rowNum; i++) {
                row = sheet.getRow(i);
                int j = 0;
                List<String> list = new ArrayList<String>();
                while (j < colNum) {
                    str = getCellFormatValue(row.getCell((short) j)).trim();
                    list.add(str);
                    j++;
                }
                content.add(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * @Description 根据Cell类获取数据
     * @auther 王柳
     * @date 2019/6/5 8:27
     * @params [cell]
     */
    private static String getCellFormatValue(XSSFCell cell) {
        String cellvalue = "";
        if (ToolUtil.isNotEmpty(cell)) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                case CELL_TYPE_NUMERIC:
                    // 数字
                    // 判断当前的cell是否为Date
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        //  yyyy-MM-dd hh:mm:ss
                        cellvalue = DateUtil.getTime(date);
                        //  yyyy-MM-dd
//                        cellvalue = DateUtil.getDay(date);
                    } else {
                        // 如果是纯数字,取得当前Cell的数值
                        cellvalue = objectToString(cell.getNumericCellValue());
                    }
                    break;
                case CELL_TYPE_STRING:
                    // 字符串
                    cellvalue = cell.getStringCellValue();
                    break;
                case CELL_TYPE_BOOLEAN:
                    // BOOLEAN
                    cellvalue = objectToString(cell.getBooleanCellValue());
                    break;
                case CELL_TYPE_FORMULA:
                    // 公式
                    cellvalue = cell.getCellFormula();
                    break;
                case CELL_TYPE_BLANK:
                    // 空值
                    cellvalue = "";
                    break;
                case CELL_TYPE_ERROR:
                    // 故障
                    cellvalue = "非法字符";
                    break;
                default:
                    cellvalue = "未知类型";
                    break;
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }
}
```

# 导出例子

1、前台通过模拟发送POST请求进行浏览器方式的导出，并可以携带查询参数。

 

```
function httpPost(URL, PARAMS) {
    var temp = document.createElement("form");
    temp.action = URL;
    temp.method = "post";
    temp.style.display = "none";

    for (var x in PARAMS) {
        var opt = document.createElement("textarea");
        opt.name = x;
        opt.value = PARAMS[x];
        temp.appendChild(opt);
    }

    document.body.appendChild(temp);
    temp.submit();

    return temp;
}
```

 

```
MgrUser.exportPoi = function(){
    var selected = $('#' + this.id).bootstrapTable('getData');
    if (selected == null || selected == "" || selected.length == 0) {
        layer.alert("数据为空，无法导出！");
        return false;
    }
    var queryData = {};
    queryData['beginTime'] = $("#beginTime").val().trim();
    queryData['endTime'] = $("#endTime").val().trim();
    queryData['name'] = $("#name").val().trim();

    Feng.confirm("确定要导出查询的数据吗？", function () {
        var index = layer.load(0, {shade: [0.3, '#f5f5f5']}); //0代表加载的风格，支持0-2

        var result = httpPost(Feng.ctxPath + "/mgr/exportPoi?queryData=", queryData);
        console.log(result);

        /*定时器判断导出进度是否完成*/
        var timer = setInterval(function () {
            $.ajax({
                url: Feng.ctxPath + "/mgr/isPoiExport",
                type: "GET",
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.resultCode == 0) {
                        layer.close(index);
                        clearInterval(timer);
                    }
                },
                error: function (e) {
                    layer.close(index);
                    console.log(e.responseText);
                }
            });
        }, 1000);

    });
};
```

2、后台传入HttpServletRequest 和 HttpServletResponse 参数，调用PoiUtil的导出方法，导出Excel到浏览器

 

```
 /**
     * @description POI导出
     * @author 王柳
     * @date 2019/11/22 9:41
     * @params [request, response]
     */
    @RequestMapping("/exportPoi")
    public void exportPoi(HttpServletRequest request, HttpServletResponse response) {
        getSession().setAttribute(ExcelConstant.EXPORT_FLAG, "true");

        try {
            userService.exportPoi(request, response);
        } catch (Exception e) {
            log.error("POI导出Excel异常{}", e.getMessage());
        }
    }

    /**
     * @description 判断是否POI导出excel是否完成，用于loading的显示和隐藏
     * @author 王柳
     * @date 2019/11/22 9:45
     * @params []
     */
    @RequestMapping("/isPoiExport")
    @ResponseBody
    public Object isExport() {
        JSONObject jsonObject = new JSONObject();
        if (getSession().getAttribute(ExcelConstant.EXPORT_FLAG) == null) {
            jsonObject.put(ExcelConstant.RESULT_CODE, 0);
        } else {
            jsonObject.put(ExcelConstant.RESULT_CODE, -1);
        }
        return jsonObject;
    }
```

 

```
    @Override
    public void exportPoi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = request.getParameter("name");
        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");

        // 总记录数
        Integer totalRowCount = baseMapper.selectListCount(name, beginTime, endTime);
        log.info("totalRowCount:  " + totalRowCount);
        Integer start = PoiUtil.getStart(totalRowCount);

        // 导出EXCEL文件名称
        String filaName = "用户信息_" + DateUtil.getTime(new Date());

        // 标题
        String[] titles = {"ID", "账号", "姓名", "性别", "生日", "部门", "邮箱", "电话", "创建时间", "状态"};

        // 查询的sql对应的名称
        String[] datas = {"id", "account", "name", "sexName", "birthday", "deptName", "email", "phone", "createtime", "statusName"};

        // 开始导出
        PoiUtil.exportExcelToWebsite(start, request, response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
            @Override
            public Boolean writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {
                Pagination page = new Pagination(currentPage, pageSize);
                log.info("currentPage:" + currentPage + ",pageSize:" + pageSize);
                List<Map<String, Object>> result = baseMapper.selectPoiExport(page, name, beginTime, endTime);
                log.info("writeExcelData------------result.size: " + result.size());

                Boolean isDownload = PoiUtil.writeWorkbook(datas, result, eachSheet, startRowCount, endRowCount);
                return isDownload;
            }
        });
    }
```

3、SQL查询语句

 

```
    <select id="selectListCount" resultType="java.lang.Integer">
        select count(*)
        from sys_user
        <where>
            status != 3
            <if test="name != null and name != ''">
                and (phone like CONCAT('%',#{name},'%')
                or account like CONCAT('%',#{name},'%')
                or `name` like CONCAT('%',#{name},'%'))
            </if>
            <if test="beginTime != null and beginTime != ''">
                and (createtime <![CDATA[>= ]]> CONCAT(#{beginTime},' 00:00:00'))
            </if>
            <if test="endTime != null and endTime != ''">
                and (createtime <![CDATA[<= ]]>  CONCAT(#{endTime},' 23:59:59'))
            </if>
        </where>
    </select>

    <select id="selectPoiExport" resultType="java.util.Map">
        select
        a.id,
        a.account,
        a.name,
        a.birthday,
        a.sex,
        m.name as sexName,
        a.email,
        a.phone,
        a.deptid,
        d.fullname as deptName,
        a.status,
        n.name as statusName,
        a.createtime,
        a.version
        from sys_user a
        left join sys_dict m on m.code=a.sex and m.pid = (select id from sys_dict where code ='sys_sex')
        left join sys_dict n on n.code=a.status and n.pid = (select id from sys_dict where code ='account_state')
        left join sys_dept d on d.id=a.deptid
        <where>
            status != 3
            <if test="name != null and name != ''">
                and (phone like CONCAT('%',#{name},'%')
                or account like CONCAT('%',#{name},'%')
                or `name` like CONCAT('%',#{name},'%'))
            </if>
            <if test="beginTime != null and beginTime != ''">
                and (createtime <![CDATA[>= ]]> CONCAT(#{beginTime},' 00:00:00'))
            </if>
            <if test="endTime != null and endTime != ''">
                and (createtime <![CDATA[<= ]]>  CONCAT(#{endTime},' 23:59:59'))
            </if>
        </where>
        order by createtime desc
    </select>
```

# 导入例子

1、前台点击跳转至导入页面，选择导入的文件进行上传，导入文件需要严格按照导入模板来写入数据

 

```
/**
 * POI导入
 */
MgrUser.importPoi = function(){
    var index = layer.open({
        type: 2,
        title: '导入',
        area: ['420px', '220px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/mgr/importPoiExcel'
    });
    this.layerIndex = index;
};
```

 

```
@layout("/common/_container.html"){
<div class="ibox float-e-margins">
    <form method="POST">
        <div class="ibox-content">
            <div class="form-horizontal">

                <div class="row">
                    <div class="col-sm-12">
                        <input type="file" id="upfile" name="upfile" placeholder="选择需要导入的文件"/>
                    </div>
                </div>
                <div class="row btn-group-m-t">
                    <div class="col-sm-10">
                        <#button btnCss="info" name="提交" id="ensure" icon="fa-check" clickFun="UserInfoDlg.importPoi()"/>
                        <#button btnCss="danger" name="取消" id="cancel" icon="fa-eraser" clickFun="UserInfoDlg.close()"/>
                    </div>
                </div>

            </div>
        </div>
    </form>
</div>

<script src="${ctxPath}/static/modular/system/user/user_info.js"></script>

@}
```

2、上传后，点击提交，将文件上传至后台，后台解析文件中的数据

 

```
/**
 * POI导入
 */
UserInfoDlg.importPoi = function () {
    var formData = new FormData();
    var name = $("#upfile").val();
    formData.append("file", $("#upfile")[0].files[0]);
    formData.append("name", name);
    var suffix = name.substr(name.lastIndexOf('.') + 1);
    if (suffix != "xlsx") {
        layer.alert("文件格式必须是.xlsx");
        return false;
    }
    var index = layer.load(0, {shade: [0.3, '#f5f5f5']}); //0代表加载的风格，支持0-2
    $.ajax({
        url: Feng.ctxPath + "/mgr/importPoi",
        type: 'POST',
        async: true,
        data: formData,
        // 告诉jQuery不要去处理发送的数据
        processData: false,
        // 告诉jQuery不要去设置Content-Type请求头
        contentType: false,
        success: function (responseStr) {
            layer.close(index);
            if (responseStr == "01") {
                Feng.alert("导入成功！");
                window.parent.MgrUser.table.refresh();
                UserInfoDlg.close();
            } else if (responseStr == "02") {
                Feng.alert("导入失败！文件为空或者文件所有数据都与库中数据重复");
            } else {
                Feng.alert("导入失败！请联系系统管理员");
            }
        },
        error: function (data) {
            layer.close(index);
            Feng.error("导入失败!" + data.responseJSON.message + "!");
        }
    });
}
```

3、后台接受传过来的文件，调用PoiUtil工具类中的读取文件解析数据的方法，将Excel每条数据解析成对象，插入到数据库中

 

```
    /**
     * 跳转到POI导入页面
     */
    @RequestMapping("/importPoiExcel")
    public String importPoiExcel() {
        return PREFIX + "user_import_poi_excel.html";
    }

    /**
     * @description POI导入
     * @author 王柳
     * @date 2019/11/22 9:51
     * @params [file]
     */
    @RequestMapping("/importPoi")
    @ResponseBody
    public Object importPoi(@RequestParam("file") MultipartFile file) {
        //上传标志
        String flag = ExcelConstant.IMPORT_NULL;

        // 判断文件是否为空
        if (!file.isEmpty()) {
            try {
                //获取输入流
                InputStream is = file.getInputStream();
                List<User> userList = userService.writeExelData(is);
                if (userList.size() == 0) {
                    flag = ExcelConstant.IMPORT_NULL;
                } else {
                    // 批量导入
                    Boolean isSuccess = userService.insertBatch(userList);
                    if (isSuccess) {
                        flag = ExcelConstant.IMPORT_SUCCESS;
                    } else {
                        flag = ExcelConstant.IMPORT_ERROR;
                    }
                }
            } catch (Exception e) {
                //上传出错
                flag = ExcelConstant.IMPORT_ERROR;
                log.error("POI导入Excel异常{}", e.getMessage());
            }
        }
        return flag;
    }
```

 

```
    @Override
    public List<User> writeExelData(InputStream is) {
        List<List<String>> list = PoiUtil.readExcelContent(is);

        // 要根据账号去重
        List<User> userList = new ArrayList<>();
        List<String> row;
        User user;
        String account;
        for (int i = 0, j = list.size(); i < j; i++) {
            row = list.get(i);
            account = row.get(0);
            EntityWrapper<User> wrapper = new EntityWrapper<>();
            // 账号在数据库中重复的数量
            Integer num = baseMapper.selectCount(wrapper.eq("account", account).ne("status", ManagerStatus.DELETED));

            for (int k = 0, m = userList.size(); k < m; k++) {
                if (account.equals(userList.get(k).getAccount())) {
                    // 文件中的账号重复
                    num = 1;
                    break;
                }
            }
            // 账号在文件和数据库中都不重复
            if (num == 0) {
                user = new User();
                user.setAccount(account);
                user.setName(row.get(1));
                user.setSex(Integer.valueOf(row.get(2)));
                user.setBirthday(DateUtil.parseTime(row.get(3)));
                user.setDeptid(Integer.valueOf(row.get(4)));
                user.setEmail(row.get(5));
                user.setPhone(row.get(6));
                user.setStatus(Integer.valueOf(row.get(7)));
                user.setCreatetime(new Date());

                userList.add(user);

            }
        }

        return userList;
    }
```