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
