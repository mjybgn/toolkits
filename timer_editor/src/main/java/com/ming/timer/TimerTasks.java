package com.ming.timer;

import com.ming.TimeMain;
import com.ming.keyEvent.KeyEventTon;
import com.ming.service.TimeService;
import com.ming.service.TimeServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class TimerTasks {

    public void start() {
    }

    public void stop() {
    }

    public boolean isTimerOver() {
        return false;
    }

    public Calendar getDateTime() {
        return Calendar.getInstance();
    }

    public static class UpdateTimerTask extends TimerTasks {

        private final TimeService timeService;
        private int flag;
        private int taskNum;
        private final static int DELAY_TIME = 800;
        private static Calendar dateTime;

        public UpdateTimerTask(Calendar dateTime, int type, JLabel content, int period, boolean openDelay) {
            timeService = new TimeServiceImpl();
            flag = 1;
            taskNum = 0;
            Thread thread = new Thread(() -> {
                doIt(dateTime, type, content, openDelay);
                timeService.submit(dateTime);
                // 延迟加载
                for (int i = 0; i < DELAY_TIME; i++) {
                    if (flag != 1) return;
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (flag == 1) {
                    taskNum++;
                    try {
                        sleep(period);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    doIt(dateTime, type, content, openDelay);
                    taskNum--;
                }
            });
            thread.start();
        }

        private void doIt(Calendar dateTime, int type, JLabel content, boolean openDelay) {
            timeService.updateTime(dateTime, type);
            // 整点前判断
            if (openDelay) {
                boolean delay = onTimeDelay(dateTime);
                timeService.setText(content, dateTime);
                this.dateTime = dateTime;
                if (delay) {
                    try {
                        Thread.sleep(DELAY_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.dateTime = dateTime;
            timeService.setText(content, dateTime);
        }

        public boolean isTimerOver() {
            if (flag == 1 || taskNum > 0) {
                return false;
            }
            return true;
        }

        public Calendar getDateTime() {
            return this.dateTime;
        }

        public void start() {
            setFlag(1);
        }

        public void stop() {
            setFlag(0);
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        private boolean onTimeDelay(Calendar dateTime) {
            int speedLevel = timeService.getSpeedLevel();
            boolean delay = false;
            switch (speedLevel) {
                case 0:
                    int second = dateTime.get(Calendar.SECOND);
                    if (second == 59) {
                        delay = true;
                    }
                    break;
                case 1:
                    int minute = dateTime.get(Calendar.MINUTE);
                    if (minute == 59) {
                        dateTime.set(Calendar.SECOND, 59);
                        delay = true;
                    }
                    break;
                case 2:
                    int hour = dateTime.get(Calendar.HOUR_OF_DAY);
                    if (hour == 23) {
                        dateTime.set(Calendar.MINUTE, 59);
                        dateTime.set(Calendar.SECOND, 59);
                        delay = true;
                    }
                    break;
                case 3:
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), 0);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    int day = dateTime.get(Calendar.DAY_OF_MONTH);
                    if (day == dayOfMonth - 1) {
                        dateTime.set(Calendar.HOUR_OF_DAY, 23);
                        dateTime.set(Calendar.MINUTE, 59);
                        dateTime.set(Calendar.SECOND, 59);
                        delay = true;
                    }
                    break;
            }
            return delay;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemTimerTask extends TimerTask {

        private JLabel content;
        private TimeService timeService;

        public SystemTimerTask(JLabel content) {
            if (timeService == null) timeService = new TimeServiceImpl();
            this.content = content;
        }

        @Override
        public void run() {
            content.setText(timeService.getSystemTime());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetTimerTask extends TimerTask {

        private JLabel realTime;
        private TimeService timeService;

        public NetTimerTask(JLabel realTime) {
            if (timeService == null) timeService = new TimeServiceImpl();
            this.realTime = realTime;
        }

        @Override
        public void run() {
            realTime.setText(timeService.getNetTime());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointFlashTask extends TimerTask {

        private JLabel point;
        private TimeService timeService;

        public PointFlashTask(JLabel point) {
            if (timeService == null) timeService = new TimeServiceImpl();
            this.point = point;
        }

        @SneakyThrows
        @Override
        public void run() {
            Icon defaultIcon = point.getIcon();
            if (KeyEventTon.flag != 1) return;
            point.setIcon(new ImageIcon(TimeMain.defaultIcon));
            Thread.sleep(200);
            point.setIcon(defaultIcon);
            Thread.sleep(800);
        }
    }

    public static class LoadingTask {

        private int loadingFlag = 0;

        public LoadingTask() {
        }

        public void loading(JPanel loadingBox) {
            loadingFlag = 1;
            Graphics2D graphics = (Graphics2D) loadingBox.getGraphics();
            new Thread(() -> {
                double present = 0.002;
                BufferedImage bi = null;
                try {
                    bi = ImageIO.read(new File("../excel_editor/src/main/resources/static/image/loading.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (loadingFlag == 1) {
                    graphics.rotate(Math.PI * present, bi.getWidth() * 0.5, bi.getHeight() * 0.5);
                    graphics.drawImage(bi, 0, 0, null);
                    present = present >= 0.2 ? 0.002 : present + 0.002;
                    try {
                        sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    graphics.clearRect(0, 0, 100, 100);
                }
            }).start();
        }

        public void stopLoading(JPanel loadingBox) {
            loadingFlag = 0;
        }
    }
}

