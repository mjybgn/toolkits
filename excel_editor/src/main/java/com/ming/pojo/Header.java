package com.ming.pojo;

import java.util.ArrayList;
import java.util.List;

public class Header {

    List<String[]> headerList = new ArrayList<>();

    public Header(List<String[]> headerList) {
        this.headerList = headerList;
    }

    public Header() {
    }

    public List<String[]> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<String[]> headerList) {
        this.headerList = headerList;
    }

    public void add(String type, String name) {
        String[] info = {type, name};
        this.headerList.add(info);
    }
}
