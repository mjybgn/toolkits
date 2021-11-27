package com.ming.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DomParse {

    // xml对应的id
    private static final Integer SOURCE_DIR_ID = 0;
    private static final Integer TARGET_DIR_ID = 1;
    private static final Integer PUBLIC_WORK_DIR_ID = 2;

    public static String getSourceDir() {
        return getPathName(SOURCE_DIR_ID);
    }

    public static String getTargetDir() {
        return getPathName(TARGET_DIR_ID);
    }

    public static String getWorkDir() {
        return getPathName(PUBLIC_WORK_DIR_ID);
    }

    /*
    * desc: 通过id获取config.xml文件中的资源路径
    * */
    private static String getPathName(int id) {
        //创建解析器工厂实例，并生成解析器
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Element node = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();//创建需要解析的文档对象
            File f = new File("excel_editor/config/config.xml");
            Document doc = builder.parse(f);
            Element root = doc.getDocumentElement();
            NodeList list = root.getElementsByTagName("path");
            node = (Element) list.item(id);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(node).getElementsByTagName("value").item(0).getFirstChild().getNodeValue();
    }
}
