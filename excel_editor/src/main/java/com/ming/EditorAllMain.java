package com.ming;

import com.ming.service.FileParseService;
import com.ming.service.FileParseServiceImpl;
import com.ming.util.DomParse;
import com.ming.util.FileService;
import com.ming.util.PinYinUtil;
import com.ming.util.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;

/**
 * Title: EditorAllMain
 * Description: 转换所有excel
 *
 * @author ming
 * @date 2021/11/11
 */
public class EditorAllMain {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        // 1.获取根目录下所有excel
        List<String> list = FileService.getInstance().getSourceFileList();
        // 2.解析excel
        ProgressBar progressBar = new ProgressBar(list.size());
        for (String str : list) {
            parse(str);
            progressBar.load();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("run time：" + (endTime - startTime));
    }

    /*
     * 解析
     * */
    private static void parse(String str) {
        String[] split = str.split("\\\\");
        int len = split.length;
        String createFileName = split[len - 1];
        String pinyin = PinYinUtil.changePinyin(createFileName);
        OutputStreamWriter output = null;
        try {
            File file = new File(DomParse.getTargetDir() + "/" + pinyin.split("\\.")[0] + ".lua");
            output = new OutputStreamWriter(new FileOutputStream(file));
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
}
