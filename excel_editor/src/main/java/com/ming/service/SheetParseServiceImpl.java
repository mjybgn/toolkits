package com.ming.service;

import com.ming.pojo.Header;
import com.ming.util.ParseUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

/**
 * Title: ParseContentImpl
 * Description: 解析sheet实现类（标准表）
 *
 * @author ming
 * @date 2021/11/11
 */
public class SheetParseServiceImpl implements SheetParseService {

    /**
     * describe:解析单个sheet（标准表[标准header + data])
     *
     * @Param: sheet:sheet对象 | output:解析内容输出出口
     */
    @Override
    public void parseSheet(Sheet sheet, OutputStreamWriter output) throws IOException {
        int rows = sheet.getPhysicalNumberOfRows();
        if (rows <= 0) return;
        Header header = new Header();
        parseHeader(sheet, header);
        String tableName = header.getHeaderList().get(0)[1];
        output.write("(function()" + tableName + "={\n");
        // 获取真实数据，获取每行
        for (int i = 4; i < rows; i++) {
            Row row = sheet.getRow(i);
            parseRow(row, header, output);
        }
        output.write("}end)();\n\n");
    }

    /**
     * describe:解析表头
     *
     * @Param: sheet:sheet对象 | header:解析内容存放对象
     */
    private void parseHeader(Sheet sheet, Header header) {
        // 获取数据头
        int cellNum = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < cellNum; i++) {
            String name = getCellValueByHeader(sheet.getRow(2).getCell(i));
            String type = getCellValueByHeader(sheet.getRow(3).getCell(i));
            header.add(type, name);
        }
    }

    /**
     * describe:解析单个row
     *
     * @Param: row：行对象 | header：字段属性信息
     */
    private void parseRow(Row row, Header header, OutputStreamWriter output) throws IOException {
        if (row == null) return;
//        int cellNum = row.getPhysicalNumberOfCells();
        int cellNum = header.getHeaderList().size();
        String result = "";
        String rowNum = "";
        //获取每个单元格
        for (int i = 0; i < cellNum; i++) {
            Cell cell = row.getCell(i);
            if (cell == null || cell.toString().equals("")) continue;
            String[] info = header.getHeaderList().get(i);
            String type = info[0].toLowerCase(Locale.ROOT);
            String name = info[1];
            if (i == 0) rowNum = getCellValue(cell, type);
            result = result + name + "=" + getCellValue(cell, type) + ",";
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
            if (!ParseUtil.isInteger(rowNum)) {
                // 如果序号为字符串类型的，就是用(行号减去表头长度)代替
                rowNum = String.valueOf(row.getRowNum() - 3);
            }
            output.write("\t[" + rowNum + "]={" + result + "},\n");
        }
    }

    /**
     * describe:获取表格cell实际数据（用于解析字段属性信息）
     *
     * @Param: cell:格子对象
     * @Return: 返回处理过后的真实值
     */
    private String getCellValueByHeader(Cell cell) {
        String value = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_FORMULA:
                    try {
                        value = String.valueOf(cell.getNumericCellValue());
                        if (ParseUtil.isFloat(value)) {
                            value = ParseUtil.floatToInt(value);
                        }
                    } catch (IllegalStateException e) {
                        try {
                            value = String.valueOf(cell.getRichStringCellValue());
                        } catch (Exception ex) {
                            return "";
                        }
                    }
                    break;
                case HSSFCell.CELL_TYPE_NUMERIC:
                    value = String.valueOf((int) cell.getNumericCellValue());
                    break;
                case HSSFCell.CELL_TYPE_STRING:
                    String str = String.valueOf(cell.getRichStringCellValue());
                    value = ParseUtil.changeTest(str);
                    break;
            }
        }
        return value;
    }

    /**
     * describe:获取表格cell实际数据
     *
     * @Param: cell:格子对象 | typeStr:字段所属数据类型
     * @Return: 返回处理过后的真实值
     */
    private String getCellValue(Cell cell, String typeStr) {
        String value = null;
        if (cell != null) {
            Integer type = ParseUtil.checkCellType(typeStr);
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_FORMULA:
                    try {
                        value = String.valueOf(cell.getNumericCellValue());
                        if (ParseUtil.isFloat(value)) {
                            value = ParseUtil.floatToInt(value);
                        }
                    } catch (IllegalStateException e) {
                        try {
                            value = "[[ " + cell.getRichStringCellValue() + " ]]";
                        } catch (Exception ex) {
                            return "";
                        }
                    }
                    break;
                case HSSFCell.CELL_TYPE_NUMERIC:
                    value = String.valueOf((int) cell.getNumericCellValue());
                    break;
                case HSSFCell.CELL_TYPE_STRING:
                    String str = String.valueOf(cell.getRichStringCellValue());
                    value = ParseUtil.changeTest(str, type);
                    break;
            }
        }
        return value;
    }
}
