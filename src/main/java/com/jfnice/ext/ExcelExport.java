package com.jfnice.ext;

import com.jfinal.render.Render;
import com.jfnice.kit.UrlKit;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 导出excel
 */
public class ExcelExport extends Render {

    private SXSSFWorkbook wb;
    private String fileName;

    public ExcelExport(SXSSFWorkbook wb, String fileName) {
        this.wb = wb;
        this.fileName = fileName;
    }

    @Override
    public void render() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + new String((fileName + " " + time).getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            response.setHeader("filename", UrlKit.encodeUrl(fileName) + " " + time + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "filename");wb.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
