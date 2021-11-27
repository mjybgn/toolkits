package com.ming.util;

import java.util.regex.Pattern;

public class ParseUtil {

    /**
     * describe: 判断要解析的数据类型
     *
     * @Param: 识别字符串
     * @Return: 0:null | 1:(1) | 2:(1,1) | 3:(1_1) | 4:(1_1,1_1)
     */
    public static int checkParseType(String str) {
        if (str.isEmpty()) return 0;
        if (str.contains(",")) {
            if (str.contains("_")) {
                // n * n (12_32,12_32)
                return 4;
            } else {
                // 1 * n (12,32)
                return 2;
            }
        } else {
            if (str.contains("_")) {
                // n * 1 (12_32)
                return 3;
            } else {
                // number(12)
                return 1;
            }
        }
    }

    /**
     * describe: 解析空值，解析数字（12）--（1），解析数字(12,32)--(1 * n)
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static String parseDefault(String str) {
        return "{" + str + "}";
    }

    /**
     * describe: 解析一维数组(12_32)--(n * 1)
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static String parseYiWeiArr(String str) {
        String result = "";
        if (str.contains("_")) {
            String[] split = str.split("_");
            int len = split.length;
            if (len <= 0 || !isInteger(split[0])) return str;
            result += "{" + split[0];
            for (int i = 1; i < len; i++) {
                result += "," + split[i];
            }
            result += "}";
            return result;
        } else {
            return str;
        }
    }

    /**
     * describe: 解析二维数组(12_32, 12_32 ...)--((n * 1) * n)
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static String parseErWeiArr(String str) {
        String result = "";
        String[] split = str.split(",");
        int len = split.length;
        if (len <= 0) return str;
        result += "{";
        for (String list : split) {
            // 一维数组
            String temp = parseYiWeiArr(list);
            if (!temp.equals("")) {
                result = result + parseYiWeiArr(list) + ",";
            }
        }
        if (result.lastIndexOf(",") == (result.length() - 1)) {
            result = result.substring(0, result.length() - 1);
        }
        result += "}";
        return result;
    }

    /**
     * describe: 解析二维数组(12_32, 12_32 ...)--((n * 1) * n)
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static String parseErWeiArr2(String str) {
        String result = "";
        String[] split = str.split("\\|");
        int len = split.length;
        if (len <= 0) return str;
        result += "{";
        for (String list : split) {
            // 一维数组
            String temp = parseYiWeiArr(list);
            if (!temp.equals("")) {
                result = result + parseYiWeiArr(list) + ",";
            }
        }
        if (result.lastIndexOf(",") == (result.length() - 1)) {
            result = result.substring(0, result.length() - 1);
        }
        result += "}";
        return result;
    }

    /**
     * describe: 解析三维数组(12_32|12_32, 12_32|12_32 ...)--( {[(n * 1) * n] * n} )
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static String parseSanWeiArr(String str) {
        String result = "";
        String[] split = str.split(",");
        int len = split.length;
        if (len <= 0) return str;
        result += "{";
        for (String list : split) {
            // 二维维数组
            String temp = parseErWeiArr2(list);
            if (!temp.equals("")) {
                result = result + parseErWeiArr2(list) + ",";
            }
        }
        if (result.lastIndexOf(",") == (result.length() - 1)) {
            result = result.substring(0, result.length() - 1);
        }
        result += "}";
        return result;
    }

    /**
     * describe: header字符串转换
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static String changeTest(String str) {
        if (str.isEmpty()) return "";
        String result = "";
        // 一维数组
        if (str.contains("_")) {
            String[] split = str.split("_");
            int len = split.length;
            if (len <= 0 || !isInteger(split[0])) return str;
            result += "{" + split[0];
            for (int i = 1; i < len; i++) {
                result += "," + split[i];
            }
            result += "}";
            return result;
        } else {
            return str;
        }
    }

    /**
     * describe:将 1_2_3_4..._n的格式转换成{1,2,3,4...n}
     *
     * @Param: str 需要转换的字符串，type 转换类型（[,]、[_]、[,_]...)
     * @Return: TODO
     */
    public static String changeTest(String str, int type) {
        if (str.isEmpty()) return "";
        String result = "";
        if (type == 1 || type == 2) {
            // 普通描述，加[[]]
            return getTextByString(str);
        } else if (type == 3) {
            // 一维数组
            return parseYiWeiArr(str);
        } else if (type == 4) {
            // 二维数组，包含(null | 12 | 12,32 | 12_32 | 12_32,12_32)五种情况
            int typeNum = checkParseType(str);
            switch (typeNum) {
                case 3:
                    result = parseYiWeiArr(str);
                    break;
                case 4:
                    result = parseErWeiArr(str);
                    break;
                default:
                    result = parseDefault(str);
                    break;
            }
            return result;
        } else if (type == 5) {
            return parseSanWeiArr(str);
        } else if (type == 6) {
            return "[[" + str + "]]";
        } else {
            return parseDefault(str);
        }
    }

    /*
     * 判断传入字符串是否为数字(正则表达式）
     * 只能判断整数
     *  */
    public static boolean isInteger(String str) {
        if (!str.isEmpty()) {
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            return pattern.matcher(str).matches();
        }
        return false;
    }

    /**
     * describe: 判断传入字符串是否为数字(正则表达式），只能判断整数
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static boolean isFloat(String str) {
        if (str.isEmpty()) return false;
        return str.matches("-?[0-9]+.*[0-9]*");
    }

    /**
     * describe: 将 float类型的字符串 转换为 int类型的字符串
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static String floatToInt(String str) {
        if (str.isEmpty()) return str;
        String[] split = str.split("\\.");
        int len = split.length;
        if (len <= 1 || !isInteger(split[1])) return str;
        if (Integer.parseInt(split[1]) > 0) {
            return str;
        }
        return split[0];
    }

    /**
     * describe: 判断header对应的参数类型
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static Integer checkCellType(String type) {
        switch (type) {
            case "int":
                return 1;
            case "string":
                return 2;
            case "[_]":
                return 3;
            case "[,_]":
            case "[,;_]":
                return 4;
            case "[,|_]":
                return 5;
            case "no":
                return 6;
            default:
                return 0;
        }
    }

    private static String getTextByString(String str) {
        return "[[ " + str.replaceAll("\n", "") + " ]]";
    }
}
