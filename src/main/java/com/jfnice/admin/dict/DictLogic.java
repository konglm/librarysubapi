package com.jfnice.admin.dict;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.*;
import com.jfnice.admin.asset.AssetService;
import com.jfnice.admin.setting.SettingService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.Asset;
import com.jfnice.model.Dict;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DictLogic {

    @Inject
    private SettingService settingService;
    @Inject
    private AssetService assetService;

    public Page<Dict> queryPageOrList(CondPara condPara) {
        SqlPara sqlPara = Db.getSqlPara("DictLogic.queryPageOrList", condPara.toKv());
        return Dict.dao.paginate(condPara.getPageNumber(), condPara.getPageSize(), sqlPara);
    }

    public List<Record> queryDistinctTagList() {
        String sql = Db.getSql("DictLogic.queryDistinctTagList");
        return Db.find(sql);
    }

    public SXSSFWorkbook createExcel(CondPara condPara) {
        SqlPara sqlPara = Db.getSqlPara("DictLogic.queryPageOrList", condPara.toKv());
        List<Dict> dictList = Dict.dao.find(sqlPara);
        Map<String, String> keyTitleMap = settingService.getDefaultKeyTitleMap(condPara.getAccess());

        SXSSFWorkbook wb = new SXSSFWorkbook();
        SXSSFSheet sheet = wb.createSheet("sheet1");
        SXSSFRow row = sheet.createRow(0);
        SXSSFCell cell;
        String key;
        Dict dict;

        CellStyle cellStyle = wb.createCellStyle();
        Font cellFont = wb.createFont();
        cellFont.setFontName("宋体");
        cellFont.setFontHeightInPoints((short) 10);
        cellStyle.setFont(cellFont);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        List<String> exportableKeyList = condPara.getExportableKeyList();
        for (int i = 0, size = exportableKeyList.size(); i < size; i++) {
            sheet.setDefaultColumnStyle(i, cellStyle);
            cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyTitleMap.get(exportableKeyList.get(i)));
        }

        for (int i = 0, len = dictList.size(); i < len; i++) {
            dict = dictList.get(i);
            row = sheet.createRow(i + 1);
            for (int j = 0, size = exportableKeyList.size(); j < size; j++) {
                cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                key = exportableKeyList.get(j);
                switch (key) {
                    case "sort":
                        cell.setCellValue(dict.getSort());
                        break;
                    case "seq":
                        cell.setCellValue(i + 1);
                        break;
                    case "display":
                        cell.setCellValue(!dict.getDisplay() ? "是" : "否");
                        break;
                    case "status":
                        cell.setCellValue(dict.getStatus() == 1 ? "正常" : "禁用");
                        break;
                    default:
                        cell.setCellValue(dict.getStr(key));
                }
            }
        }
        return wb;
    }

    @SuppressWarnings("serial")
    public void importExcel(String fileUrl) {
        Map<String, String> titleFieldMap = new HashMap<String, String>() {{
            put("标签", "tag");
            put("键名", "k");
            put("键值", "v");
            put("标记", "label");
            put("样式", "style");
            put("显示", "display");
            put("排序", "sort");
        }};

        Asset asset = assetService.getAssetByUrl(fileUrl);
        String filePath = JFinal.me().getConstants().getBaseUploadPath() + asset.getUrl();

        Throwable cause = null;
        XSSFWorkbook wb = null;
        XSSFSheet sheet = null;
        XSSFRow titleRow = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        XSSFCellStyle normalCellStyle = null;
        XSSFCellStyle warningCellStyle = null;

        String sql = null;
        String field = null;
        int[] result = null;
        List<Object[]> paras = new ArrayList<Object[]>();
        Map<Integer, Integer> paraIndexRowNumMap = new HashMap<Integer, Integer>();
        Map<String, Integer> fieldColNumMap = new HashMap<String, Integer>();

        try {
            FileInputStream fis = new FileInputStream(filePath);
            wb = new XSSFWorkbook(fis);
            fis.close();

            sheet = wb.getSheetAt(0);
            normalCellStyle = wb.createCellStyle();
            normalCellStyle.setFillPattern(FillPatternType.NO_FILL);
            warningCellStyle = wb.createCellStyle();
            warningCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            warningCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            titleRow = sheet.getRow(0);
            cell = titleRow.createCell(titleRow.getLastCellNum());
            cell.setCellStyle(warningCellStyle);
            cell.setCellValue("ERROR INFO");
            for (int colNum = 0; colNum < titleRow.getLastCellNum() - 1; colNum++) {
                cell = titleRow.getCell(colNum);
                if (cell == null) {
                    continue;
                }
                cell.setCellComment(null);
                cell.setCellStyle(normalCellStyle);
                cell.setCellType(CellType.STRING);
                field = titleFieldMap.get(cell.getStringCellValue().trim());
                if (field == null) {
                    continue;
                }
                fieldColNumMap.put(field, colNum);
            }

            if (fieldColNumMap.size() < titleFieldMap.size()) {
                throw new ErrorMsg("导入失败！表头缺失，请参照下载模版的表头补充信息！");
            }

            int paraIndex = 0;
            Record record = null;
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //日期型字段处理
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum);
                }

                record = new Record();
                int colNum = 0;
                for (Map.Entry<String, Integer> entry : fieldColNumMap.entrySet()) {
                    field = entry.getKey();
                    colNum = entry.getValue();
                    cell = row.getCell(colNum);
                    if (cell == null) {
                        record.set(field, null);
                        continue;
                    }
                    cell.setCellComment(null);
                    cell.setCellStyle(normalCellStyle);

                    switch (field) {
                        /* 日期型字段的处理方式（用户输入有可能格式不对又或者是字符串格式而不是日期格式）
						case "datetime":
							if ( cell.getCellTypeEnum().equals(CellType.NUMERIC) ) {
								try { cell.setCellValue(sdf.format(cell.getDateCellValue())); } catch (Exception e) {}
							}
							cell.setCellType(CellType.STRING);
							record.set(field, cell.getStringCellValue().trim());
							break;
						*/
                        case "display":
                            cell.setCellType(CellType.STRING);
                            record.set(field, "是".equals(cell.getStringCellValue().trim()) ? 1 : 0);
                            break;
                        default:
                            cell.setCellType(CellType.STRING);
                            record.set(field, cell.getStringCellValue().trim());
                    }
                }

                if (record.getColumnValues().length > 0) {
                    record.set("status", 1);
                    TreeMap<String, Object> columnMap = new TreeMap<String, Object>(record.getColumns());
                    paras.add(columnMap.values().toArray(new Object[columnMap.values().size()]));
                    paraIndexRowNumMap.put(paraIndex++, rowNum);
                }
            }

            //核对字段次序，书写对应的sql
            TreeMap<String, Object> columnMap = new TreeMap<String, Object>(record.getColumns());
            System.out.println(columnMap.keySet());
            System.out.println(columnMap.values());
            sql = " INSERT INTO dict(display, k, label, sort, status, style, tag, v) "
                    + " SELECT a.* "
                    + " FROM (SELECT ? AS display, ? AS k, ? AS label, ? AS sort, ? AS status, ? AS style, ? AS tag, ? AS v FROM DUAL) a "
                    + " WHERE NOT EXISTS(SELECT 1 FROM dict WHERE del = 0 AND tag = a.tag AND k = a.k) ";
            result = Db.batch(sql, paras.toArray(new Object[paras.size()][]), paras.size());
            DictKit.clear();
        } catch (IOException e) {
            cause = e;
        } catch (ActiveRecordException e) {
            if (e.getCause() instanceof BatchUpdateException) {
                BatchUpdateException ex = (BatchUpdateException) e.getCause();
                result = ex.getUpdateCounts();
            } else {
                cause = e;
            }
        }

        if (cause != null) {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            throw new ErrorMsg(cause);
        }

        boolean hasError = false;
        Integer colNum = null;
        String msg = null;
        for (int i = 0; i < result.length; i++) {
            if (0 == result[i]) {
                hasError = true;
                int rowNum = paraIndexRowNumMap.get(i);
                row = sheet.getRow(rowNum);
                cell = row.getCell(titleRow.getLastCellNum());
                if (cell == null) {
                    cell = row.createCell(row.getLastCellNum());
                }
                cell.setCellStyle(warningCellStyle);
                cell.setCellValue("Record already exists");
                continue;
            }

            if (result[i] < 0 && result[i] != Statement.SUCCESS_NO_INFO) {
                hasError = true;
                try {
                    Db.update(sql, paras.get(i));
                } catch (ActiveRecordException e) {
                    SQLException ex = (SQLException) e.getCause();
                    field = ex.getMessage().replaceAll("^.*column \'(.+)\'.*$", "$1");
                    colNum = fieldColNumMap.get(field);
                    int rowNum = paraIndexRowNumMap.get(i);
                    row = sheet.getRow(rowNum);
                    msg = null;
                    if (colNum != null) {
                        cell = row.getCell(colNum);
                        cell.setCellStyle(warningCellStyle);
                        msg = ex.getMessage().replaceAll("for column \'.+\' at row \\d+", "");
                        Comment comment = sheet.createDrawingPatriarch().createCellComment(new XSSFClientAnchor(0, 0, 0, 0, colNum, rowNum, colNum + 5, rowNum + 2));
                        comment.setString(new XSSFRichTextString(msg));
                        cell.setCellComment(comment);
                    }

                    cell = row.getCell(titleRow.getLastCellNum());
                    if (cell == null) {
                        cell = row.createCell(row.getLastCellNum());
                    }
                    cell.setCellStyle(warningCellStyle);
                    cell.setCellValue(msg == null ? ex.getMessage() : msg);
                }
            }
        }

        if (hasError) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filePath);
                wb.write(fos);
                fos.flush();
                throw new ErrorMsg("导入失败！录入数据有误！");
            } catch (IOException e) {
                e.printStackTrace();
                throw new ErrorMsg(e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        if (wb != null) {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
