package com.ming.service;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Title: ParseService
 * Description: excel文件解析功能接口
 *
 * @author ming
 * @date 2021/11/11
 */
public interface FileParseService {

    void parse(String path, OutputStreamWriter output) throws IOException;
}
