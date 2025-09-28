package friend_gift_JavaFX.gameThreeFX;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX 台球小游戏
 * 功能：
 * - 模拟台球桌上的基本物理效果
 * - 实现球与边界、球与球之间的碰撞检测
 * - 支持鼠标拖动白球击球
 */
public class GameThreeFX extends Application {

    // 游戏常量
    private static final int TABLE_WIDTH = 800;      // 台球桌宽度
    private static final int TABLE_HEIGHT = 500;     // 台球桌高度
    private static final int BALL_RADIUS = 15;       // 球的半径
    private static final double FRICTION = 0.99;     // 摩擦系数（值越接近1，减速越慢）
    private static final double MAX_SPEED = 10;      // 最大球速
    private static final double POCKET_RADIUS = 20;  // 球袋半径

    private static final double[][] POCKETS = {
            {0, 0}, {TABLE_WIDTH / 2, 0}, {TABLE_WIDTH, 0},
            {0, TABLE_HEIGHT}, {TABLE_WIDTH / 2, TABLE_HEIGHT}, {TABLE_WIDTH, TABLE_HEIGHT}
    };

    // 游戏变量
    private List<Ball> balls;         // 存储所有球的列表
    private boolean isDragging = false; // 是否正在拖动白球
    private double dragStartX, dragStartY; // 鼠标拖动开始的位置
    private Ball whiteBall;           // 白球引用

    @Override
    public void start(Stage primaryStage) {
        // 创建画布
        Canvas canvas = new Canvas(TABLE_WIDTH, TABLE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 初始化球
        initializeBalls();

        // 注册鼠标事件处理器
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);

        // 创建场景
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, TABLE_WIDTH, TABLE_HEIGHT);

        // 设置舞台
        primaryStage.setTitle("球球归洞咯");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 创建并启动游戏循环
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();   // 更新游戏状态
                draw(gc);   // 绘制游戏画面
            }
        }.start();
    }

    /**
     * 初始化所有球
     */
    private void initializeBalls() {
        balls = new ArrayList<>();

        // 创建白球
        whiteBall = new Ball(TABLE_WIDTH / 4, TABLE_HEIGHT / 2, BALL_RADIUS, Color.WHITE);
        balls.add(whiteBall);

        // 彩球颜色数组
        Color[] colors = {
                Color.YELLOW, Color.BLUE, Color.RED, Color.PURPLE,
                Color.ORANGE, Color.GREEN, Color.BROWN, Color.BLACK,
                Color.YELLOW, Color.BLUE, Color.RED, Color.PURPLE,
                Color.ORANGE, Color.GREEN, Color.BROWN
        };

        // 三角形排列彩球
        int row = 0;          // 当前行数
        int count = 0;        // 当前行中的球计数
        int startX = TABLE_WIDTH * 3 / 4; // 三角形中心X坐标
        int startY = TABLE_HEIGHT / 2;    // 三角形中心Y坐标

        for (int i = 0; i < colors.length; i++) {
            // 计算当前球的位置
            int x = startX - row * BALL_RADIUS;
            int y = startY - row * BALL_RADIUS + count * BALL_RADIUS * 2;

            // 添加新球
            balls.add(new Ball(x, y, BALL_RADIUS, colors[i]));

            count++;
            // 当当前行球数等于行数+1时，换行
            if (count > row) {
                row++;
                count = 0;
            }
        }
    }

    private void checkPockets() {
        List<Ball> ballsToRemove = new ArrayList<>();

        for(Ball ball : balls) {
            for (double[] pocket : POCKETS) {
                double dx = ball.x - pocket[0];
                double dy = ball.y - pocket[1];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < POCKET_RADIUS) {
                    ballsToRemove.add(ball);
//                    if(ball != whiteBall) {
//                        score++; // 计分
//                    }
                    break;
                }
            }
        }

        balls.removeAll(ballsToRemove);
    }

    /**
     * 处理鼠标按下事件
     */
    private void handleMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        // 检查是否点击了白球
        if (Math.hypot(x - whiteBall.x, y - whiteBall.y) <= BALL_RADIUS) {
            isDragging = true;
            dragStartX = x;
            dragStartY = y;
        }
    }

    /**
     * 处理鼠标拖动事件
     */
    private void handleMouseDragged(MouseEvent event) {
        if (isDragging) {
            // 可以在这里绘制瞄准线
        }
    }

    /**
     * 处理鼠标释放事件
     */
    private void handleMouseReleased(MouseEvent event) {
        if (isDragging) {
            // 计算拖动的距离
            double dx = dragStartX - event.getX();
            double dy = dragStartY - event.getY();

            // 设置白球的初速度
            whiteBall.vx = dx / 10;
            whiteBall.vy = dy / 10;

            isDragging = false;
        }
    }

    /**
     * 更新所有游戏元素的状态
     */
    private void update() {
        // 更新所有球的位置
        for (Ball ball : balls) {
            ball.x += ball.vx;
            ball.y += ball.vy;

            // 应用摩擦力（逐渐减速）
            ball.vx *= FRICTION;
            ball.vy *= FRICTION;

            // 边界碰撞检测
            if (ball.x - ball.radius < 0 || ball.x + ball.radius > TABLE_WIDTH) {
                ball.vx *= -1; // 水平方向反弹
            }
            if (ball.y - ball.radius < 0 || ball.y + ball.radius > TABLE_HEIGHT) {
                ball.vy *= -1; // 垂直方向反弹
            }
        }

        // 球与球之间的碰撞检测
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball b1 = balls.get(i);
                Ball b2 = balls.get(j);

                // 计算两球之间的距离
                double dx = b2.x - b1.x;
                double dy = b2.y - b1.y;
                double distance = Math.hypot(dx, dy);

                // 如果两球距离小于半径之和，则发生碰撞
                if (distance < b1.radius + b2.radius) {
                    // 计算碰撞角度
                    double angle = Math.atan2(dy, dx);
                    double sin = Math.sin(angle);
                    double cos = Math.cos(angle);

                    // 旋转b1的速度向量
                    double vx1 = b1.vx * cos + b1.vy * sin;
                    double vy1 = -b1.vx * sin + b1.vy * cos;

                    // 旋转b2的速度向量
                    double vx2 = b2.vx * cos + b2.vy * sin;
                    double vy2 = -b2.vx * sin + b2.vy * cos;

                    // 沿x轴交换速度（一维弹性碰撞）
                    double temp = vx1;
                    vx1 = vx2;
                    vx2 = temp;

                    // 旋转回原来的坐标系
                    b1.vx = vx1 * cos - vy1 * sin;
                    b1.vy = vx1 * sin + vy1 * cos;
                    b2.vx = vx2 * cos - vy2 * sin;
                    b2.vy = vx2 * sin + vy2 * cos;

                    // 防止球重叠
                    double overlap = (b1.radius + b2.radius) - distance;
                    b1.x -= overlap * cos / 2;
                    b1.y -= overlap * sin / 2;
                    b2.x += overlap * cos / 2;
                    b2.y += overlap * sin / 2;
                }
            }
        }

        // 检查入洞
        checkPockets();
    }

    /**
     * 绘制游戏画面
     */
    private void draw(GraphicsContext gc) {
        // 绘制台球桌背景
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);

        // 绘制球袋
        gc.setFill(Color.BLACK);
        for (double[] pocket : POCKETS) {
            gc.fillOval(pocket[0] - POCKET_RADIUS, pocket[1] - POCKET_RADIUS,
                    POCKET_RADIUS * 2, POCKET_RADIUS * 2);
        }

        // 绘制所有球
        for (Ball ball : balls) {
            // 绘制球的主体
            gc.setFill(ball.color);
            gc.fillOval(ball.x - ball.radius, ball.y - ball.radius,
                    ball.radius * 2, ball.radius * 2);

            // 绘制高光效果
            gc.setFill(Color.rgb(255, 255, 255, 0.3));
            gc.fillOval(ball.x - ball.radius / 2, ball.y - ball.radius / 2,
                    ball.radius, ball.radius);
        }


    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 球的内部类
     */
    private static class Ball {
        double x, y;       // 球的位置
        double vx, vy;     // 球的速度
        double radius;     // 球的半径
        Color color;       // 球的颜色

        Ball(double x, double y, double radius, Color color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }
    }
}
