package com.ming.service;

import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.OutputStreamWriter;

public interface SheetParseService {

    void parseSheet(Sheet sheet, OutputStreamWriter output) throws IOException;
}
