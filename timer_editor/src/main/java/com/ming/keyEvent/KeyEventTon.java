package com.ming.keyEvent;

import com.ming.TimeMain;
import com.ming.service.TimeService;
import com.ming.service.TimeServiceImpl;
import com.ming.timer.TimerTasks;

import javax.swing.*;
import java.util.Calendar;

/**
 * Title: KeyBoardSingleTon
 * Description: 键盘按键单例
 *
 * @author ming
 * @date 2021/11/22
 */
public class KeyEventTon {

    private TimeService timeService;
    public static int flag = 1;
    public static Calendar dateTime;
    private TimerTasks timer;
    private TimerTasks.LoadingTask loadingTask;

    private KeyEventTon() {
    }

    private static class KeyBoardSingleTon {
        private static KeyEventTon INSTANCE = new KeyEventTon();
    }

    public static KeyEventTon getInstance() {
        return KeyBoardSingleTon.INSTANCE;
    }

    /**
     * describe: 键盘按下事件处理
     *
     * @Param: 37 ←，38 ↑，39 →，40 ↓
     * @Return: TODO
     */
    public void pressed(int keyCode, JLabel content, TimeMain timeMain) {
        if (timeService == null) {
            timeService = new TimeServiceImpl();
        }
        dateTime = Calendar.getInstance();
        switch (keyCode) {
            case 37:
                if (flag != 1) return;
                loadingTask = new TimerTasks.LoadingTask();
                loadingTask.loading(timeMain.loadingBox);
                flag = 37;
                timer = new TimerTasks.UpdateTimerTask(dateTime, -1, content, 50, false);
                timer.start();
                System.out.println("时间回溯");
                break;
            case 39:
                if (flag != 1) return;
                loadingTask = new TimerTasks.LoadingTask();
                loadingTask.loading(timeMain.loadingBox);
                flag = 39;
                timer = new TimerTasks.UpdateTimerTask(dateTime, 1, content, 50, true);
                timer.start();
                System.out.println("时间流逝");
                break;
            case 32:
                if (flag != 1) return;
                flag = 32;
                timeService.resetTime(dateTime);
                System.out.println("时间重塑");
                break;
        }
    }

    /**
     * describe: 键盘松开事件处理
     *
     * @Param: 37 ←，38 ↑，39 →，40 ↓
     * @Return: TODO
     */
    public void released(int keyCode, JLabel content, JLabel speedText, TimeMain timeMain) throws InterruptedException {
        if (timeService == null) {
            timeService = new TimeServiceImpl();
        }
        switch (keyCode) {
            case 37:
                if (flag != 37) return;
                timer.stop();
                while (!timer.isTimerOver()) {
                    Thread.sleep(50);
                }
                timeService.submit(timer.getDateTime());
                flag = 1;
                loadingTask.stopLoading(timeMain.loadingBox);
                break;
            case 38:
                if (flag != 1) return;
                flag = 38;
                timeService.updateSpeed(1);
                speedText.setText(timeService.getSpeedText());
                System.out.println("速度加倍");
                flag = 1;
                break;
            case 39:
                if (flag != 39) return;
                timer.stop();
                while (!timer.isTimerOver()) {
                    Thread.sleep(50);
                }
                timeService.submit(timer.getDateTime());
                flag = 1;
                loadingTask.stopLoading(timeMain.loadingBox);
                break;
            case 40:
                if (flag != 1) return;
                flag = 40;
                timeService.updateSpeed(-1);
                speedText.setText(timeService.getSpeedText());
                System.out.println("速度减缓");
                flag = 1;
                break;
            case 32:
                if (flag != 32) return;
                content.setText(timeService.getSystemTime());
                flag = 1;
                break;
        }
    }
}