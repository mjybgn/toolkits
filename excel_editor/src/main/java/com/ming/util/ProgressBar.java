package com.ming.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Title: ProgressBar
 * Description: 进度条
 *
 * @author ming
 * @date 2021/11/11
 */
@SuppressWarnings("serial")
public class ProgressBar extends JWindow implements Runnable{

    private static final int PROGRESS_BAR_CAPACITY = 100;// 进度条容量（默认100）

    private int progress;// 当前进度

    private int maxProgress;// 最大进度

    // 定义加载窗口大小
    public static final int LOAD_WIDTH = 455;
    public static final int LOAD_HEIGHT = 315;
    // 获取屏幕窗口大小
    public static final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    // 定义进度条组件
    public JProgressBar progressbar;
    // 定义标签组件
    public JLabel label;
    public JButton closeBtn;
    public JButton minimizeBtn;

    public final static Image closeIcon = new ImageIcon("../toolkits/excel_editor/src/main/resources/static/image/round_close.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    public final static Image minimizeIcon = new ImageIcon("../toolkits/excel_editor/src/main/resources/static/image/minimize.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    private final static ImageIcon bg = new ImageIcon("../toolkits/excel_editor/src/main/resources/static/image/progressBarBG.png");

    // 构造函数
    public ProgressBar(int maxProgress) {
        this.maxProgress = maxProgress;
        closeBtn = new JButton(new ImageIcon(closeIcon));
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                System.exit(0);
            }
        });
        closeBtn.setBounds(0,0,20,20);
        minimizeBtn = new JButton(new ImageIcon(minimizeIcon));
        minimizeBtn.addActionListener(e -> close());
        minimizeBtn.setBounds(20,0,20,20);
        // 创建标签,并在标签上放置一张图片
        label = new JLabel(bg);
        label.setBounds(0, 0, LOAD_WIDTH, LOAD_HEIGHT - 15);
        // 创建进度条
        progressbar = new JProgressBar();
        // 显示当前进度值信息
        progressbar.setStringPainted(true);
        // 设置进度条边框不显示
        progressbar.setBorderPainted(false);
        // 设置进度条的前景色
        progressbar.setForeground(new Color(0, 210, 40));
        // 设置进度条的背景色
        progressbar.setBackground(new Color(188, 190, 194));
        progressbar.setBounds(0, LOAD_HEIGHT - 15, LOAD_WIDTH, 15);
        this.setAlwaysOnTop(true);
        // 添加组件
        this.add(closeBtn);
        this.add(minimizeBtn);
        this.add(label);
        this.add(progressbar);
        // 设置布局为空
        this.setLayout(null);
        // 设置窗口初始位置
        this.setLocation((WIDTH - LOAD_WIDTH) / 2, (HEIGHT - LOAD_HEIGHT) / 2);
        // 设置窗口大小
        this.setSize(LOAD_WIDTH, LOAD_HEIGHT);
        // 设置窗口显示
        this.setVisible(true);
    }

    public synchronized void load() {
        new Thread(this).start();
    }

    public void close() {
        this.dispose();
    }

    @Override
    public void run() {
        this.progress = this.progress + 1;
        double present = (double) this.progress/this.maxProgress;// 当前进度百分比
        int count = (int)(present * PROGRESS_BAR_CAPACITY);
        progressbar.setValue(count);
        if (present == 1) {
            this.dispose();
        }
    }
}
