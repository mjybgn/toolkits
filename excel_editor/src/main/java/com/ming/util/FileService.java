package com.ming.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/*
 * 文件资源管理类
 * */
public class FileService {

    private static class fileManagerSingleTon {
        public static final FileService INSTANCE = new FileService();
    }

    public static FileService getInstance() {
        return fileManagerSingleTon.INSTANCE;
    }

    /*
     * 获取所有excel路径
     * */
    public List<String> getSourceFileList() {
        String sourceDir = DomParse.getSourceDir();
        return getExcelList(sourceDir);
    }

    /*
    * 获取公共目录下的所有excel表的文件路径
    * */
    public List<String> getWorkExcelFileList() {
        String workDir = DomParse.getWorkDir();
        return getExcelList(workDir);
    }

    /*
     * 获取目标路径下所有excel类型的文件路径
     * */
    private List<String> getExcelList(String sourceDir) {
        List<String> resultList = new ArrayList<>();
        File dir = new File(sourceDir);
        File[] tempList = dir.listFiles();
        if (tempList == null) return resultList;
        for (File value : tempList) {
            if (!value.isFile()) continue;
            String str = value.toString();
            String extension = str.substring(str.lastIndexOf(".") + 1);// 扩展名
            if (!isExcel(extension)) continue;
            resultList.add(value.toString());// 将资源文件目录下所有 excel格式 的文件路径存入到结果集当中
        }
        return resultList;
    }

    /*
     * 判断扩展名是否属于excel
     * */
    private boolean isExcel(String extension) {
        return Arrays.asList("xls", "xlsx", "XLS", "XLSX").contains(extension);
    }

    public static Workbook getBookByPath(String path) {
        String[] split = path.split("\\.");
        Workbook book = null;
        //根据文件后缀（xls/xlsx）进行判断
        try {
            String extension = split[split.length - 1];
            if ("xls".equals(extension)) {
                FileInputStream fis = new FileInputStream(path);   //文件流对象
                book = new HSSFWorkbook(fis);
            } else if ("xlsx".equals(extension)) {
                book = new XSSFWorkbook(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    public static ArrayList<String> getFileNameByPinYin(String pathName) {
        // 1.获取根目录下所有excel
        List<String> list = FileService.getInstance().getSourceFileList();
        ArrayList<String> parseList = new ArrayList<>();
        for (String path : list) {
            String[] split = path.split("\\\\");
            int len = split.length;
            String str = PinYinUtil.changePinyin(split[len - 1]).toLowerCase(Locale.ROOT);// 将文件名提取出来，并转换成拼音
            if (str.contains(pathName)) {
                parseList.add(path);
            }
        }
        return parseList;
    }

}
