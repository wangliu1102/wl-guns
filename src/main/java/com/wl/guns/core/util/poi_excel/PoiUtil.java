package com.wl.guns.core.util.poi_excel;

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
