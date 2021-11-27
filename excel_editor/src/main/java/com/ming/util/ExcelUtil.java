package com.ming.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Objects;

public class ExcelUtil {

    /*
     * 检测excel属于哪种类型
     * @return 0：empty；1：有header；2：没有header
     * */
    public static int checkSheetType(Sheet sheet) {
        int rows = sheet.getPhysicalNumberOfRows();
        if (rows <= 4) return 0;
        Row row = sheet.getRow(1);
        if (row == null) return 2;
        Cell cell = sheet.getRow(1).getCell(0);
        if (cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            String result = String.valueOf(cell.getRichStringCellValue());
            if (Objects.equals(result, "INT") || Objects.equals(result, "STRING")) {
                return 1;
            }
        }
        return 2;
    }
}
