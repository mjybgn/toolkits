package com.ming;

import com.ming.util.FileUtil;

/**
 * Title: ImportResource
 * Description: 导入资源（21.程序用表/...  ------  resources/excel/...)
 *
 * @author ming
 * @date 2021/11/13
 */
public class ImportResource {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        // 资源导入
        FileUtil.copyMulFile();
        long endTime = System.currentTimeMillis();
        System.out.println("run time：" + (endTime - startTime));
    }
}
