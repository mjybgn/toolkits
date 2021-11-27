package com.ming.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Title: TimeSpeed
 * Description: 时间调节速度参数常量表
 *
 * @author ming
 * @date 2021/11/19
 */
public class TimeSpeedConst {
    /**
     * 默认情况下1秒，可调节速度共4挡
     */
    public static final ArrayList<Integer> speedLevelList = new ArrayList<>(Arrays.asList(1000, 60000, 3600000, 86400000));

    /**
     * 默认操作秒
     */
    public static final Integer optionLevel = Calendar.SECOND;

}
