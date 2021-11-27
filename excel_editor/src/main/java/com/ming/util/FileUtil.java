package com.ming.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtil {

    /*
     * 复制多个文件
     * */
    public static void copyMulFile() {
        List<String> workExcelFileList = FileService.getInstance().getWorkExcelFileList();
        String sourceDir = DomParse.getSourceDir();
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(workExcelFileList.size());
        ProgressBar progressBar = new ProgressBar(workExcelFileList.size() - 1);
        for (String path : workExcelFileList) {
            Runnable run = () -> {
                String[] split = path.split("\\\\");
                int len = split.length;
                String fileName = split[len - 1];
                String newPath = sourceDir + "\\\\" + fileName;
                copyFile(path, newPath);
                countDownLatch.countDown();
                progressBar.load();
            };
            service.submit(run);
        }
        try {
            countDownLatch.await();
            service.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * 复制单个文件
     * */
    public static void copyFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(oldFile);
            out = new FileOutputStream(file);
            byte[] buffer = new byte[2097152];
            int readByte = 0;
            while ((readByte = in.read(buffer)) != -1) {
                out.write(buffer, 0, readByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(in).close();
                Objects.requireNonNull(out).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
