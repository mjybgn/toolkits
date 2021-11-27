package com.ming.service;

import com.ming.data.TimeSpeedConst;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Title: TimeServiceImpl
 * Description: TODO
 *
 * @author ming
 * @date 2021/11/19
 */
public class TimeServiceImpl implements TimeService {

    private static int speedLevel = 0;
    private static final String[] dataStr = new String[]{
            "http://www.baidu.com",
            "http://www.bbc.co.uk",
            "http://google.com/",
            "http://www.509.cc",
            "http://www.koreafilm.or.kr",
            "http://www.mainsky.de",
            "http://www.yahoo.com"
    };
    private final Lock lock = new ReentrantReadWriteLock().readLock();

    /**
     * Title: TimeServiceImpl
     * Description: 提交修改的时间
     *
     * @author ming
     * @date 2021/11/19
     */
    @Override
    public void submit(Calendar dateTime) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        String date = format1.format(dateTime.getTime());
        String time = format2.format(dateTime.getTime());
        try {
            Runtime.getRuntime().exec("cmd /c date " + date);
            Runtime.getRuntime().exec("cmd /c time " + time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * describe: 根据按压按键的瞬时时间进行系统时间的调整
     *
     * @Param: dateTime 当前时间，pressMoment 按下控制按键的瞬时时间，type 操作类型（1:增加/-1:减少）
     * 刷新机制：加速事件过程中，前端0.1s刷新一次，后端开始和结束时各刷新一次
     * @Return: TODO
     */
    @Override
    public void updateTime(Calendar startTime, int type) {
        Integer speedLevel = TimeSpeedConst.speedLevelList.get(getSpeedLevel());
        double timeMoment = (speedLevel * type) * 0.001;

        int optionValue = startTime.get(TimeSpeedConst.optionLevel);
        optionValue = (int) (optionValue + timeMoment);
        startTime.set(TimeSpeedConst.optionLevel, optionValue);
    }

    /**
     * describe: 修改时间加速度
     *
     * @Param: type  1-速度(+)  -1-速度(-)
     * @Return: TODO
     */
    @Override
    public void updateSpeed(int type) {
        int speedLevel = getSpeedLevel();
        if (type > 0) {
            // 加速
            int size = TimeSpeedConst.speedLevelList.size();
            if (speedLevel < size - 1) {
                setSpeedLevel(++speedLevel);
            }
        } else {
            // 减速
            if (speedLevel > 0) {
                setSpeedLevel(--speedLevel);
            }
        }
    }

    @Override
    public String getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(calendar.getTime());
    }

    /**
     * describe: 联网获取时间
     *
     * @Param: TODO
     * @Return: TODO
     */
    @Override
    public String getNetTime() {
        lock.lock();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (String str : dataStr) {
                Date date = getWebsiteDatetime(str, 5000);
                if (date == null) continue;
                //            System.out.println(format.format(date));
                return "北京时间：" + format.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "Unable to connect the internet";
    }

    /**
     * describe: 获取指定url连接的页面时间
     *
     * @Param: TODO
     * @Return: TODO
     */
    public static Date getWebsiteDatetime(String webUrl, int timeOut) {
        try {
            URL url = new URL(webUrl);// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.setReadTimeout(timeOut);
            uc.setConnectTimeout(timeOut);
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            return new Date(ld);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * describe: 获取当前时间调节速度等级
     * @Param: TODO
     * @Return: TODO
     */
    @Override
    public int getSpeedLevel() {
        return speedLevel;
    }

    public static void setSpeedLevel(int speedLevel) {
        TimeServiceImpl.speedLevel = speedLevel;
    }

    /**
     * describe: 获取当前速度等级
     *
     * @Param: TODO
     * @Return: TODO
     */
    @Override
    public String getSpeedText() {
        String endStr = "/times），可以通过↑↓进行调节";
        String result = "当前调节速度为（";
        Integer speedLevel = TimeSpeedConst.speedLevelList.get(getSpeedLevel());
        int second = speedLevel / 1000;
        int minute = second / 60;
        int hour = minute / 60;
        int day = (hour / 24);
        if (day > 0) {
            result += day + "day";
        } else if (hour > 0) {
            result += hour + "hour";
        } else if (minute > 0) {
            result += minute + "min";
        } else {
            result += second + "s";
        }
        return result + endStr;
    }

    /**
     * describe: 设置动态时间
     * @Param: TODO
     * @Return: TODO
     */
    @Override
    public void setText(JLabel content, Calendar dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        content.setText(sdf.format(dateTime.getTime()));
    }

    /**
     * describe: 重设系统时间（调整系统时间为北京时间）
     * @Param: TODO
     * @Return: TODO
     */
    @Override
    public void resetTime(Calendar dateTime) {
        lock.lock();
        try {
            Date date = null;
            for (String str : dataStr) {
                date = getWebsiteDatetime(str, 5000);
                if (date != null) break;
            }
            if (date != null) {
                dateTime.setTime(date);
                submit(dateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}