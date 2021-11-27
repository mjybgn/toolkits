package com.ming.service;

import javax.swing.*;
import java.util.Calendar;

/**
 * Title: TimeService
 * Description: TODO
 *
 * @author ming
 * @date 2021/11/19
 */
public interface TimeService {

    void submit(Calendar dateTime);

    void updateTime(Calendar dateTime, int type);

    void updateSpeed(int type);

    String getSystemTime();

    String getNetTime();

    String getSpeedText();

    int getSpeedLevel();

    void setText(JLabel content, Calendar dateTime);

    void resetTime(Calendar dateTime);
}
