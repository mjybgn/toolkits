package com.ming.service;

import com.ming.util.ExcelUtil;
import com.ming.util.FileService;
import com.ming.util.PinYinUtil;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

/**
 * Title: ParseServiceImpl
 * Description: excel文件解析实现类
 *
 * @author ming
 * @date 2021/11/11
 */
public class FileParseServiceImpl implements FileParseService {

    private static final Logger LOGGER = Logger.getLogger(FileParseServiceImpl.class);

    /**
     * describe:解析单个xlsx文件
     * @Param: path:需要解析的目标文件路径 | output:解析内容输出出口
     */
    public void parse(String path, OutputStreamWriter output) throws IOException {
        LOGGER.debug("parse: " + path + "...");
        ZipSecureFile.setMinInflateRatio(-1.0d);
        Workbook book = FileService.getBookByPath(path);
        int sheetNum = book.getNumberOfSheets();
        if (sheetNum <= 0) return;
        //获取每个Sheet表
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = book.getSheetAt(i);
            int type = ExcelUtil.checkSheetType(sheet);
            String sheetName = PinYinUtil.changePinyin(sheet.getSheetName()).toLowerCase(Locale.ROOT);
            if (type == 1) {
                try {
                    new SheetParseServiceImpl().parseSheet(sheet, output);
                } catch (NullPointerException | IOException e) {
                    output.write("(function()" + sheetName + "={\n}end)();\n\n");
                    LOGGER.debug("---sheet" + sheetName + "表头有问题");
                }
            } else {
                output.write("(function()sheet" + i + "={\n}end)();\n\n");
//                new SpecialParseImpl().parseSheet(sheet, output);
                System.out.println(path);
                LOGGER.debug("sheet" + sheetName + " type error! enable to parse this ex1cel.");
            }
        }
        LOGGER.debug("-----" + path + "----- (parse finish)");
    }

}
