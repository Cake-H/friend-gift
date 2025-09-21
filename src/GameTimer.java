import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 计时器监听器接口，用于回调通知时间变化
interface TimerListener {
    void onTimeChanged(int remainingSeconds);
    void onTimeExpired();
}

// 独立的计时器类
public class GameTimer extends JPanel {
    private Timer timer;
    private int totalSeconds; // 总计时秒数
    private int remainingSeconds; // 剩余秒数
    private TimerListener listener; // 回调监听器
    private JLabel timeLabel; // 时间显示组件

    public GameTimer(int totalSeconds, TimerListener listener) {
        this.totalSeconds = totalSeconds;
        this.remainingSeconds = totalSeconds;
        this.listener = listener;

        initUI();
        initTimer();
    }

    // 初始化计时器UI
    private void initUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false); // 透明背景，不影响游戏界面

        JLabel label = new JLabel("剩余时间：");
        label.setForeground(Color.black);
        label.setFont(new Font("宋体", Font.BOLD, 16));

        timeLabel = new JLabel(formatTime(remainingSeconds));
        timeLabel.setForeground(Color.black);
        timeLabel.setFont(new Font("宋体", Font.BOLD, 16));

        add(label);
        add(timeLabel);
    }

    // 初始化计时逻辑
    private void initTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingSeconds--;
                updateTimeDisplay();

                // 通知监听器时间变化
                if (listener != null) {
                    listener.onTimeChanged(remainingSeconds);
                }

                // 时间到
                if (remainingSeconds <= 0) {
                    stop();
                    if (listener != null) {
                        listener.onTimeExpired();
                    }
                }
            }
        });
    }

    // 格式化时间显示（mm:ss）
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    // 更新时间显示
    private void updateTimeDisplay() {
        timeLabel.setText(formatTime(remainingSeconds));

        if(remainingSeconds <= 10) {
            timeLabel.setForeground(Color.red);
        } else {
            timeLabel.setForeground(Color.black);
        }
    }

    // 启动计时器
    public void start() {
        timer.start();
    }

    // 停止计时器
    public void stop() {
        timer.stop();
    }

    // 重置计时器
    public void reset() {
        remainingSeconds = totalSeconds;
        updateTimeDisplay();
    }

    // 获取剩余秒数
    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
    