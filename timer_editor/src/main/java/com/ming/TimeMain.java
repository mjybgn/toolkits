package com.ming;

import com.ming.keyEvent.KeyEventTon;
import com.ming.service.TimeService;
import com.ming.service.TimeServiceImpl;
import com.ming.timer.TimerTasks;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;

/**
 * Title: TimeMain
 * Description:
 * 1.磁盘读取次数太多，优化方向：键盘事件结束以后再修改系统时间
 * 2.网络请求次数太多，优化方向，定时请求，闲时自动计时
 * 3.增加卡点延时功能，整点整天整月会延时
 *
 * @author ming
 * @date 2021/11/24
 */
public class TimeMain implements KeyListener, Runnable {

    private JFrame frame;
    private JPanel panel1;
    private JPanel leftBox;
    private JLabel left;
    private JPanel panel2;
    private JLabel realTime;
    private JLabel content;
    private JPanel rightBox;
    private JLabel right;
    private JPanel spaceBox;
    private JLabel space;
    private JLabel spaceText;
    private JPanel panel3;
    private JPanel panel4;
    private JPanel panel5;
    public JPanel loadingBox;
    private JLabel speedText;
    private Timer pointTimer;
    private static JLabel point;
    private static final int M_WIDTH = 450;
    private static final int M_HEIGHT = 200;
    private Timer sysTimer;
    private Timer netTimer;

    private final BufferedImage bi = ImageIO.read(new File("../toolkits/timer_editor/src/main/resources/static/image/loading.png"));
    public final static Image safeIcon = new ImageIcon("../toolkits/timer_editor/src/main/resources/static/image/green_point.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    public final static Image dangerIcon = new ImageIcon("../toolkits/timer_editor/src/main/resources/static/image/red_point.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    public final static Image defaultIcon = new ImageIcon("../toolkits/timer_editor/src/main/resources/static/image/default_point.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    public final static Image leftIcon = new ImageIcon("../toolkits/timer_editor/src/main/resources/static/image/left.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    public final static Image rightIcon = new ImageIcon("../toolkits/timer_editor/src/main/resources/static/image/right.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    public final static Image spaceIcon = new ImageIcon("../toolkits/timer_editor/src/main/resources/static/image/space.png").getImage().getScaledInstance(70, 30, Image.SCALE_SMOOTH);

    private static final Font CONTENT_FONT_1 = new Font(Font.SERIF, Font.PLAIN, 26);
    private static final Font TIPS_FONT_1 = new Font(Font.SERIF, Font.ITALIC, 12);

    private static final Color BUTTON_COLOR_1 = new Color(0xE3E3E3);
    private static final Color BUTTON_COLOR_2 = new Color(0xEAEAFD);
    private static final Color BUTTON_COLOR_3 = new Color(0xE2E2FF);

    private static final Border BORDER_SPACE_1 = BorderFactory.createBevelBorder(0, new Color(0x595959), Color.black);
    private static final Border BORDER_SPACE_2 = BorderFactory.createBevelBorder(0, Color.black, Color.black);

    public static void main(String[] args) throws IOException {
        TimeMain timeMain = new TimeMain();
        Thread thread = new Thread(timeMain);
        thread.start();
    }

    public TimeMain() throws IOException {
        frame = new JFrame("时间设置器");
        frame.setSize(M_WIDTH, M_HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel1 = new JPanel(new BorderLayout());
        panel1.setBounds(0, 0, M_WIDTH, M_HEIGHT);
        frame.add(panel1);

        leftBox = new JPanel(new BorderLayout());
        rightBox = new JPanel(new BorderLayout());
        left = new JLabel(new ImageIcon(leftIcon));
        leftBox.add(left, BorderLayout.CENTER);
        right = new JLabel(new ImageIcon(rightIcon));
        rightBox.add(right, BorderLayout.CENTER);
        panel1.add(leftBox, BorderLayout.WEST);
        panel1.add(rightBox, BorderLayout.EAST);

        TimeService timeService = new TimeServiceImpl();
        panel2 = new JPanel();
        realTime = new JLabel();
        realTime.setText(timeService.getNetTime());
        realTime.setHorizontalAlignment(SwingConstants.CENTER);
        realTime.setFont(CONTENT_FONT_1);
        panel2.add(realTime);
        content = new JLabel();
        content.setHorizontalAlignment(SwingConstants.CENTER);
        content.setText(timeService.getSystemTime());
        content.setFont(CONTENT_FONT_1);
        panel2.add(content);
        spaceBox = new JPanel();
        spaceBox.setBackground(BUTTON_COLOR_2);
        space = new JLabel(new ImageIcon(spaceIcon));
        spaceText = new JLabel("按空格键修正时间");
        spaceText.setFont(TIPS_FONT_1);
        spaceBox.add(space);
        spaceBox.add(spaceText);
        spaceBox.setBorder(BORDER_SPACE_1);
        panel2.add(spaceBox);
        panel1.add(panel2, BorderLayout.CENTER);

        panel3 = new JPanel(new BorderLayout());
        loadingBox = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(bi.getWidth(), bi.getHeight());
            }
        };
        panel4 = new JPanel();
        panel4.add(loadingBox);
        panel3.add(panel4, BorderLayout.WEST);

        panel5 = new JPanel();
        speedText = new JLabel(timeService.getSpeedText());
        speedText.setFont(TIPS_FONT_1);
        panel5.add(speedText);
        panel3.add(panel5, BorderLayout.EAST);

        point = new JLabel(new ImageIcon(safeIcon));
        panel5.add(point);
        panel1.add(panel3, BorderLayout.SOUTH);

        frame.setContentPane(panel1);
        frame.setResizable(false);// 禁用最大化
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // 红点
        dangerPoint();

        // 按键图形提示
        int keyCode = e.getKeyCode();
        if (keyCode == 37) {
            leftBox.setBackground(BUTTON_COLOR_1);
        } else if (keyCode == 39) {
            rightBox.setBackground(BUTTON_COLOR_1);
        } else if (keyCode == 32) {
            spaceBox.setBackground(BUTTON_COLOR_3);
            spaceBox.setBorder(BORDER_SPACE_2);
        }

        // 停止自动刷新时间定时任务
        sysTimer.cancel();
        // 键盘按下以后，固定时间段刷新一次
        KeyEventTon key = KeyEventTon.getInstance();
        key.pressed(e.getKeyCode(), content, this);
    }

    @SneakyThrows
    @Override
    public void keyReleased(KeyEvent e) {
        // 松开以后，修改系统时间
        KeyEventTon key = KeyEventTon.getInstance();
        key.released(e.getKeyCode(), content, speedText, this);
        // 重新开启自动刷新时间定时任务
        sysTimer = new Timer();
        sysTimer.schedule(new TimerTasks.SystemTimerTask(content), 500, 1000L);
        // 重新加载一下网络时间，因为Timer对系统时间很敏感，当时间不同步时会自动cancel
        netTimer.cancel();
        netTimer = new Timer();
        netTimer.schedule(new TimerTasks.NetTimerTask(realTime), 1000L, 1000L);
        safePoint();
        int keyCode = e.getKeyCode();
        if (keyCode == 37) {
            leftBox.setBackground(null);
        } else if (keyCode == 39) {
            rightBox.setBackground(null);
        } else if (keyCode == 32) {
            spaceBox.setBackground(BUTTON_COLOR_2);
            spaceBox.setBorder(BORDER_SPACE_1);
        }
    }

    @Override
    public void run() {
        sysTimer = new Timer();
        sysTimer.schedule(new TimerTasks.SystemTimerTask(content), 0L, 1000L);
        netTimer = new Timer();
        netTimer.schedule(new TimerTasks.NetTimerTask(realTime), 0L, 1000L);
        pointTimer = new Timer();
        pointTimer.schedule(new TimerTasks.PointFlashTask(point), 1500L, 1500L);
    }

    public void safePoint() {
        point.setIcon(new ImageIcon(safeIcon));
    }

    public void dangerPoint() {
        point.setIcon(new ImageIcon(dangerIcon));
    }

}
