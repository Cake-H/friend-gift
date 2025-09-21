import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameTwoJframe extends JFrame implements KeyListener, ActionListener, TimerListener {
    Timer ballTimer;
    Timer stripTimer;
    JPanel jPanel; // 游戏画布
    GameTimer gameTimer; // 引入计时器对象

    int circleX = 10;
    int circleY = 10;
    int stripX = 10;
    int stripY = 500;

    int circleSize = 20;
    int width = 55;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    int height = 5;

    int c_dx = 2; // 小球x方向速度
    int c_dy = 2; // 小球y方向速度

    int s_dx = 5; // 长条x方向速度
    int s_dy = 5; // 长条y方向速度

    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean upPressed = false;
    boolean downPressed = false;

    int round = 1; // 第几回合

    static boolean isRoundOneWin = false;

    static boolean isRoundTwoWin = false;

    static boolean isRoundThreeWin = false;

    JMenuItem replayOne = new JMenuItem("第一关");
    JMenuItem replayTwo = new JMenuItem("第二关");

    JMenuItem replayThree = new JMenuItem("第三关");

    public GameTwoJframe() throws HeadlessException {
        initJFrame();
        initJMenuBar();
        // 初始化1分钟计时器，并注册当前类为监听器
        gameTimer = new GameTimer(60, this);
        // 将计时器添加到界面顶部
        this.getContentPane().add(gameTimer, BorderLayout.NORTH);

        round(1); // 启动第一关
    }

    private void initJFrame() {
        this.setSize(603, 680);
        this.setTitle("不要让小球掉落~");
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout()); // 修改布局为BorderLayout，方便放置计时器
        this.addKeyListener(this);
        this.setVisible(true);
    }

    private void initJMenuBar() {
        JMenuBar jMenuBar = new JMenuBar(); // 设置菜单

        // 创建菜单选项
        JMenu replay = new JMenu("重新开始");

        replay.add(replayOne);
        replay.add(replayTwo);
        replay.add(replayThree);

        replayOne.addActionListener(this);
        replayTwo.addActionListener(this);
        replayThree.addActionListener(this);

        jMenuBar.add(replay);
        this.setJMenuBar(jMenuBar);

    }

    private void round(int roundn) {
        // 停止所有旧定时器
        if (ballTimer != null) ballTimer.stop();
        if (stripTimer != null) stripTimer.stop();

        // 复位图形位置
        resetPositions();

        // 初始化关卡参数
        round = roundn;
        setDxDy();
        setWidth(55);
        // 重新初始化游戏面板
        if (jPanel != null) {
            this.getContentPane().remove(jPanel);
        }
        initPanel();
        this.getContentPane().add(jPanel, BorderLayout.CENTER); // 游戏面板放中间
        this.revalidate();
        this.repaint();

        // 重置并启动计时器
        gameTimer.reset();
        gameTimer.start();

        // 启动游戏定时器
        initTimer();
    }

    private void resetPositions() {
        circleX = 10;
        circleY = 10;
        stripX = 10;
        stripY = 500;
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;
    }

    private void initPanel() {
        jPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCircle(g, circleSize);
                drawStrip(g, width, height);
                g.setColor(Color.RED);
                g.drawString("第" + round + "关", 20, 20);
            }
        };
        jPanel.setBounds(0, 0, 603, 680);
        jPanel.setLayout(null);
    }

    private void drawStrip(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        g.fillRect(stripX, stripY, width, height);
    }

    private void drawCircle(Graphics g, int circleSize) {
        g.setColor(Color.yellow);
        g.fillOval(circleX, circleY, circleSize, circleSize);
    }

    private void setDxDy() {
        if (round == 1) {
            s_dx = 5;
            s_dy = 5;
            c_dx = 2;
            c_dy = 2;
        } else if (round == 2) {
            s_dx = 10;
            s_dy = 5;
            c_dx = 3;
            c_dy = 3;
        } else if (round == 3) {
            s_dx = 15;
            s_dy = 1;
            c_dx = 3;
            c_dy = 3;
        }
    }

    private void checkCollision() {
        // 触碰长条，y方向反转
        if (circleX >= stripX - 1
                && circleX <= stripX + width + 1
                && circleY + circleSize >= stripY - 1
                && circleY + circleSize <= stripY + c_dy) {
            c_dy = -c_dy;
        }
        // 触碰左右边界
        if (circleX <= 0 || circleX + circleSize >= this.getWidth() - 1) {
            c_dx = -c_dx;
        }
        // 触碰上边界
        if (circleY <= 0) {
            c_dy = -c_dy;
        }
        // 触碰下界游戏结束
        if (circleY + circleSize >= getHeight()) {
            ballTimer.stop();
            if (stripTimer != null) stripTimer.stop();
            gameTimer.stop(); // 停止计时器
            JOptionPane.showMessageDialog(this, "游戏结束！小球掉落了~");
        }
    }

    private void ballMoving() {
        circleX += c_dx;
        circleY += c_dy;
        checkCollision();
    }

    private void replay(int round) {
        round(round);
    }

    private void initTimer() {
        // 小球定时器
        ballTimer = new Timer(1, e -> {
            ballMoving();
            jPanel.repaint();
        });
        ballTimer.start();

        // 长条移动定时器
        stripTimer = new Timer(10, e -> {
            if (round == 1) {
                if (leftPressed && stripX > 0) stripX -= s_dx;
                if (rightPressed && stripX + width < 608) stripX += s_dx;
                if (upPressed && stripY > 0) stripY -= s_dy;
                if (downPressed && stripY + height < 680) stripY += s_dy;
            } else if (round == 2) {
                if (leftPressed && stripX > 0) stripX -= s_dx;
                if (rightPressed && stripX + width < 608) stripX += s_dx;
                if (upPressed && stripY > 0) stripY -= s_dy;
                if (downPressed && stripY + height < 680) stripY += s_dy;
            } else if (round == 3) {
                if (leftPressed && stripX > 0) stripX -= s_dx;
                if (rightPressed && stripX + width < 608) stripX += s_dx;
                if (upPressed && stripY > 0) stripY -= s_dy;
                if (downPressed && stripY + height < 680) stripY += s_dy;
            }
            jPanel.repaint();
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == replayOne) {
            replay(1);
        } else if (obj == replayTwo) {
            if (!isRoundOneWin) {
                JOptionPane.showMessageDialog(this, "请先通过第一关~");
                return;
            }
            replay(2);
        } else if (obj == replayThree) {
            if (!isRoundOneWin) {
                JOptionPane.showMessageDialog(this, "请先通过第一关~");
                return;
            }
            if (!isRoundTwoWin) {
                JOptionPane.showMessageDialog(this, "请先通过第二关~");
                return;
            }
            replay(3);
        }
    }

    public static void main(String[] args) {
        new GameTwoJframe().setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) leftPressed = true;
        else if (code == KeyEvent.VK_RIGHT) rightPressed = true;
        else if (code == KeyEvent.VK_UP) upPressed = true;
        else if (code == KeyEvent.VK_DOWN) downPressed = true;

        if (stripTimer != null && !stripTimer.isRunning()) {
            stripTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) leftPressed = false;
        else if (code == KeyEvent.VK_RIGHT) rightPressed = false;
        else if (code == KeyEvent.VK_UP) upPressed = false;
        else if (code == KeyEvent.VK_DOWN) downPressed = false;

        if (!leftPressed && !rightPressed && !upPressed && !downPressed) {
            if (stripTimer != null) stripTimer.stop();
        }

        if (code == 65) {
            width -= 20;
        } else if (code == 68) {
            width += 20;
        }
        // System.out.println(code);
    }

    // 实现TimerListener接口方法 - 时间变化时调用
    @Override
    public void onTimeChanged(int remainingSeconds) {

    }

    // 实现TimerListener接口方法 - 时间结束时调用
    @Override
    public void onTimeExpired() {
        ballTimer.stop();
        if (stripTimer != null) stripTimer.stop();

        if (round == 1) {
            isRoundOneWin = true;
            JOptionPane.showMessageDialog(jPanel, "恭喜通过第" + round + "关！进入下一关");
            round(2);
        } else if (round == 2) {
            isRoundTwoWin = true;
            JOptionPane.showMessageDialog(jPanel, "恭喜通过第" + round + "关！进入下一关");
            round(3);
        } else if (round == 3) {
            isRoundThreeWin = true;
            JOptionPane.showMessageDialog(jPanel, "恭喜通关所有关卡！");
        }
    }
}
