package com.ming;

import com.ming.service.FileParseService;
import com.ming.service.FileParseServiceImpl;
import com.ming.util.DomParse;
import com.ming.util.FileService;
import com.ming.util.PinYinUtil;
import com.ming.util.ProgressBar;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

/**
 * Title: EditorMain
 * Description: 转换单个excel
 *
 * @author ming
 * @date 2021/11/13
 */
public class EditorMain {

    private static final Logger LOGGER = Logger.getLogger(EditorMain.class);

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        if (scan.hasNext()) {
            String fileName = PinYinUtil.changePinyin(scan.next()).toLowerCase(Locale.ROOT);// 获取输入的字符串，并转换为拼音
            scan.close();
            List<String> parseList = FileService.getFileNameByPinYin(fileName);// 通过关键字拼音检索相关联的文件
            if (parseList.size() <= 0) {
                LOGGER.debug(fileName + "? " + "No search this chart!");
                return;
            }
            transition(parseList, fileName);
        }
    }


    private static void transition(List<String> parseList, String fileName) {
        long startTime = System.currentTimeMillis();
        ProgressBar progressBar = new ProgressBar(parseList.size());
        for (String str : parseList) {
            String[] split = str.split("\\\\");
            int len = split.length;
            String createFileName = split[len - 1];
            String pinyin = PinYinUtil.changePinyin(createFileName).toLowerCase(Locale.ROOT);
            OutputStreamWriter output = null;
            if (pinyin.contains(fileName)) {
                try {
                    File file = new File(DomParse.getTargetDir() + "/" + pinyin.split("\\.")[0] + ".lua");
                    output = new OutputStreamWriter(new FileOutputStream(file));
                    // 若目标目录下所有文件当中有包含搜索的关键字的文件，则解析该文件（模糊搜索查询），todo：设置一个模糊比例
                    FileParseService parseService = new FileParseServiceImpl();
                    parseService.parse(str, output);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Objects.requireNonNull(output).flush();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            progressBar.load();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("run time：" + (endTime-startTime));
    }
}
