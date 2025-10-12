package friend_gift_JavaFX.gameThreeFX;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JavaFX 台球小游戏
 * 功能：
 * - 鼠标滑动直接转动球杆（无需点击）
 * - 球杆严格贴白球外侧，不穿透
 * - 模拟真实台球物理与规则
 */
public class GameThreeFX {
    private AnimationTimer gameTimer;
    private boolean isGameLoopRunning = true;

    // 游戏常量
    private static final int TABLE_WIDTH = 800;      // 台球桌宽度
    private static final int TABLE_HEIGHT = 500;     // 台球桌高度
    private static final int BALL_RADIUS = 15;       // 球的半径
    private static final double FRICTION = 0.99;     // 摩擦系数
    private static final double MAX_SPEED = 10;      // 最大球速
    private static final double POCKET_RADIUS = 30;  // 球袋半径

    // 球杆核心参数（优化：长杆+合理拉拽范围）
    private static final double POLE_FIXED_LENGTH = 220;  // 球杆固定长度（更长，视觉更舒适）
    private static final double MAX_PULL_DISTANCE = 100;  // 最大拉拽距离（更大发力空间）
    private static final double POLE_WIDTH = 4;           // 球杆粗细（适配长杆）
    private static final double POLE_HIT_SCALE = 0.16;   // 力度系数（适配大拉拽，避免超速）

    // 球袋位置
    private static final double[][] POCKETS = {
            {10, 10}, {TABLE_WIDTH / 2.0, 10}, {TABLE_WIDTH - 10, 10},
            {10, TABLE_HEIGHT - 10}, {TABLE_WIDTH / 2.0, TABLE_HEIGHT - 10}, {TABLE_WIDTH - 10, TABLE_HEIGHT - 10}
    };

    // 游戏变量
    private List<Ball> balls;
    private Ball whiteBall;
    private int score = 0;
    private boolean isFreeBall = false;
    private boolean placingWhiteBall = false;

    // 球杆状态（核心：仅两种状态，无中间态）
    private boolean isPullingPole = false;  // 仅"拉动球杆"时为true，其他时候都为false
    private double aimAngle = 0;            // 瞄准角度（白球→目标的方向，弧度制）
    private Point poleStart;                // 球杆起点（白球外侧，不穿球）
    private Point poleEnd;                  // 球杆终点（拉拽端）
    private double currentPullDistance = 0; // 当前拉拽距离

    private StackPane root;

    public interface OnVictoryListener {
        void onVictory();
    }

    private OnVictoryListener victoryListener;
    private Canvas canvas;

    public GameThreeFX() {
        canvas = new Canvas(TABLE_WIDTH, TABLE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        initializeBalls();
        initPolePoints(); // 初始化球杆位置（确保一开始就不穿球）

        // 鼠标事件：仅处理"放置自由球"和"拉动球杆"，转动无需点击
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
//        canvas.setOnMouseMoved(this::handleMouseMoved);

        root = new StackPane(canvas);

        // 游戏循环：实时更新球杆位置（无需点击）
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isGameLoopRunning) {
                    return;
                }
                update();
                draw(gc);
            }
        };
        gameTimer.start();
    }

    // 对外提供布局
    public StackPane getGameThreeRoot() {
        return root;
    }

    public void setOnvictoryListener(OnVictoryListener listener) {
        this.victoryListener = listener;
    }

    /**
     * 初始化所有球（原有逻辑不变）
     */
    private void initializeBalls() {
        balls = new ArrayList<>();

        // 白球（右侧中间）
        whiteBall = new Ball(TABLE_WIDTH * 3 / 4, TABLE_HEIGHT / 2, BALL_RADIUS, Color.WHITE, 0);
        balls.add(whiteBall);

        // 单色球（1-8号）
        Color[] solidColors = {
                Color.YELLOW, Color.BLUE, Color.RED, Color.PURPLE,
                Color.ORANGE, Color.GREEN, Color.BROWN, Color.BLACK
        };
        for (int i = 0; i < 8; i++) {
            balls.add(new Ball(0, 0, BALL_RADIUS, solidColors[i], i + 1));
        }

        // 双色球（9-15号）
        for (int i = 0; i < 7; i++) {
            balls.add(new Ball(0, 0, BALL_RADIUS, solidColors[i], i + 9));
        }

        arrangeBallsInTriangle();
    }

    /**
     * 三角排列目标球（原有逻辑不变）
     */
    private void arrangeBallsInTriangle() {
        int fixed1stPos = 0; // 1号球（顶点）
        int fixed8thPos = 4; // 8号球（第三排中间）

        // 随机球池
        List<Ball> randomBalls = new ArrayList<>();
        for (Ball ball : balls) {
            if (ball.number == 0 || ball.number == 1 || ball.number == 8) continue;
            randomBalls.add(ball);
        }
        Collections.shuffle(randomBalls);

        // 填充位置
        Ball[] positionBalls = new Ball[15];
        positionBalls[fixed1stPos] = findBallByNumber(1);
        positionBalls[fixed8thPos] = findBallByNumber(8);
        int randomIndex = 0;
        for (int i = 0; i < 15; i++) {
            if (positionBalls[i] == null) positionBalls[i] = randomBalls.get(randomIndex++);
        }

        // 计算坐标
        double triangleCenterX = TABLE_WIDTH * 1 / 4;
        double triangleCenterY = TABLE_HEIGHT / 2;
        double rowSpacing = BALL_RADIUS * 1.8;
        double colSpacing = BALL_RADIUS * 1.8;

        int posIndex = 0;
        for (int row = 0; row < 5; row++) {
            int ballsInRow = row + 1;
            double firstBallX = triangleCenterX + (2 - row) * colSpacing;
            double rowY = triangleCenterY - (ballsInRow - 1) * rowSpacing / 2;

            for (int col = 0; col < ballsInRow; col++) {
                Ball currentBall = positionBalls[posIndex++];
                currentBall.x = firstBallX;
                currentBall.y = rowY + col * rowSpacing;
            }
        }
    }

    private Ball findBallByNumber(int number) {
        for (Ball ball : balls) if (ball.number == number) return ball;
        return null;
    }

    /**
     * 初始化球杆位置（关键：确保一开始就贴白球外侧）
     */
    private void initPolePoints() {
        // 初始瞄准方向：白球→三角形中心（朝左）
        double dx = (TABLE_WIDTH * 1 / 4) - whiteBall.x;
        double dy = (TABLE_HEIGHT / 2) - whiteBall.y;
        aimAngle = Math.atan2(dy, dx); // 瞄准角度：白球到目标的方向
        calculatePolePoints(); // 计算初始端点（不穿球）
    }

    /**
     * 核心修复1：计算球杆端点（严格贴白球外侧，不穿透）
     * 逻辑：
     * 1. 瞄准方向 = aimAngle（白球→目标）
     * 2. 球杆方向 = 瞄准方向的反方向（避免穿白球）
     * 3. 球杆起点 = 白球中心 + 瞄准方向 * BALL_RADIUS（刚好在白球外侧）
     * 4. 球杆终点 = 球杆起点 + 球杆方向 * (固定长度 + 拉拽距离)
     */
    private void calculatePolePoints() {
        // 1. 计算球杆起点（白球外侧，瞄准方向的边缘）
        double startX = whiteBall.x - Math.cos(aimAngle) * BALL_RADIUS;
        double startY = whiteBall.y - Math.sin(aimAngle) * BALL_RADIUS;
        poleStart = new Point(startX, startY);

        // 2. 计算球杆终点（反瞄准方向延伸，不穿白球）
        double poleDirX = -Math.cos(aimAngle); // 球杆方向 = 反瞄准方向
        double poleDirY = -Math.sin(aimAngle);
        double totalLength = POLE_FIXED_LENGTH + currentPullDistance;
        double endX = startX + poleDirX * totalLength;
        double endY = startY + poleDirY * totalLength;
        poleEnd = new Point(endX, endY);
    }

    /**
     * 核心修复2：计算拉拽距离（仅沿球杆方向，不跑偏）
     */
    private void calculatePullDistance(double mouseX, double mouseY) {
        // 球杆方向向量（从起点到终点）
        double poleDirX = poleEnd.x - poleStart.x;
        double poleDirY = poleEnd.y - poleStart.y;
        double poleLength = Math.hypot(poleDirX, poleDirY);
        if (poleLength < 0.1) return; // 避免除以0

        // 鼠标相对于球杆起点的向量
        double mouseDirX = mouseX - poleStart.x;
        double mouseDirY = mouseY - poleStart.y;

        // 仅计算沿球杆方向的拉拽距离（避免横向跑偏）
        double pullDistance = (mouseDirX * poleDirX + mouseDirY * poleDirY) / poleLength;
        // 限制范围：0~最大拉拽距离（只能向后拉，不能向前推）
        currentPullDistance = Math.max(0, Math.min(pullDistance, MAX_PULL_DISTANCE));
    }

    /**
     * 检查球是否入洞（原有逻辑不变）
     */
    private void checkPockets() {
        List<Ball> ballsToRemove = new ArrayList<>();

        for (Ball ball : balls) {
            if (Math.abs(ball.vx) < 0.01 && Math.abs(ball.vy) < 0.01) continue;

            for (double[] pocket : POCKETS) {
                double distance = Math.hypot(ball.x - pocket[0], ball.y - pocket[1]);
                if (distance < POCKET_RADIUS) {
                    ballsToRemove.add(ball);
                    if (ball != whiteBall) score++;
                    else isFreeBall = true;
                    break;
                }
            }
        }
        balls.removeAll(ballsToRemove);
    }

    /**
     * 检查所有球是否静止（新增：用于判断是否可以击球）
     */
    private boolean allBallsStopped() {
        for (Ball ball : balls) {
            if (Math.abs(ball.vx) > 0.02 || Math.abs(ball.vy) > 0.02) {
                return false;
            }
        }
        return true;
    }

    /**
     * 更新游戏状态（改进：添加球静止检查）
     */
    private void update() {
        // 胜利/失败判定
        if (isVictory()) {
            gameTimer.stop();
            isGameLoopRunning = false;
            showVictoryDialog();
            return;
        }
        if (isFail()) {
            gameTimer.stop();
            isGameLoopRunning = false;
            showAlert("游戏失败", "白球也进洞咯，你失败啦！");
            return;
        }

        // 球的物理运动
        for (Ball ball : balls) {
            ball.x += ball.vx;
            ball.y += ball.vy;
            ball.vx *= FRICTION;
            ball.vy *= FRICTION;

            // 边界反弹
            if (ball.x - ball.radius < 0 || ball.x + ball.radius > TABLE_WIDTH) ball.vx *= -1;
            if (ball.y - ball.radius < 0 || ball.y + ball.radius > TABLE_HEIGHT) ball.vy *= -1;

            // 限制在边界内
            ball.x = Math.max(ball.radius, Math.min(ball.x, TABLE_WIDTH - ball.radius));
            ball.y = Math.max(ball.radius, Math.min(ball.y, TABLE_HEIGHT - ball.radius));
        }

        // 球与球碰撞
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball b1 = balls.get(i);
                Ball b2 = balls.get(j);
                double distance = Math.hypot(b2.x - b1.x, b2.y - b1.y);

                if (distance < b1.radius + b2.radius) {
                    double angle = Math.atan2(b2.y - b1.y, b2.x - b1.x);
                    double sin = Math.sin(angle);
                    double cos = Math.cos(angle);

                    double vx1 = b1.vx * cos + b1.vy * sin;
                    double vy1 = -b1.vx * sin + b1.vy * cos;
                    double vx2 = b2.vx * cos + b2.vy * sin;
                    double vy2 = -b2.vx * sin + b2.vy * cos;

                    double temp = vx1;
                    vx1 = vx2;
                    vx2 = temp;

                    b1.vx = vx1 * cos - vy1 * sin;
                    b1.vy = vx1 * sin + vy1 * cos;
                    b2.vx = vx2 * cos - vy2 * sin;
                    b2.vy = vx2 * sin + vy2 * cos;

                    double overlap = (b1.radius + b2.radius) - distance;
                    b1.x -= overlap * cos / 2;
                    b1.y -= overlap * sin / 2;
                    b2.x += overlap * cos / 2;
                    b2.y += overlap * sin / 2;
                }
            }
        }

        checkPockets();

        // 只有当所有球静止时才能操作球杆（新增）
        if (!allBallsStopped()) {
            isPullingPole = false; // 球在运动时不能拉杆
        }
    }

    /**
     * 绘制游戏画面（核心：球杆不穿球）
     */
    private void draw(GraphicsContext gc) {
        // 1. 绘制台球桌
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);

        // 2. 绘制球袋
        gc.setFill(Color.BLACK);
        for (double[] pocket : POCKETS) {
            gc.fillOval(pocket[0] - POCKET_RADIUS, pocket[1] - POCKET_RADIUS,
                    POCKET_RADIUS * 2, POCKET_RADIUS * 2);
        }

        // 3. 绘制所有球
        for (Ball ball : balls) {
            // 球主体
            gc.setFill(ball.color);
            gc.fillOval(ball.x - ball.radius, ball.y - ball.radius,
                    ball.radius * 2, ball.radius * 2);

            // 双色球（9-15号）
            if (ball.number >= 9 && ball.number <= 15) {
                gc.setFill(Color.WHITE);
                // 下半部分
                gc.fillOval(ball.x - ball.radius / 2, ball.y + ball.radius / 2 + 2,
                        ball.radius + 1, ball.radius / 2 - 3);
                // 上半部分
                gc.fillOval(ball.x - ball.radius / 2 - 0.5, ball.y - ball.radius,
                        ball.radius + 1, ball.radius / 2 - 3);
            }

            // 高光
            gc.setFill(Color.rgb(255, 255, 255, 0.4));
            gc.fillOval(ball.x - ball.radius / 2, ball.y - ball.radius / 2,
                    ball.radius, ball.radius);

            // 球号
            if (ball.number > 0) {
                gc.setFill(ball.number == 8 ? Color.WHITE : Color.BLACK);
                gc.setFont(new Font(15));
                double fontX = ball.x - (ball.number <= 9 ? 4 : 8);
                double fontY = ball.y + 6;
                gc.fillText(String.valueOf(ball.number), fontX, fontY);
            }
        }

        // 4. 自由球提示
        if (isFreeBall) {
            gc.setFill(Color.YELLOW);
            gc.setFont(new Font(20));
            gc.fillText("自由球：点击放置白球", 20, 50);
        }

        // 5. 核心：绘制球杆（不穿白球，无需点击即跟随）
        // 只有当所有球静止且不是自由球时才显示球杆（新增条件）
        if (!isFreeBall && allBallsStopped() && poleStart != null && poleEnd != null) {
            // 球杆主体（木质色）
            gc.setStroke(Color.BURLYWOOD);
            gc.setLineWidth(POLE_WIDTH);
            gc.strokeLine(poleStart.x, poleStart.y, poleEnd.x, poleEnd.y);

            // 球杆末端（黑色杆头）
            gc.setFill(Color.BLACK);
            double headRadius = POLE_WIDTH * 1.5;
            gc.fillOval(poleEnd.x - headRadius, poleEnd.y - headRadius,
                    headRadius * 2, headRadius * 2);

            // 拉拽提示条（红色，显示发力程度）
            if (isPullingPole && currentPullDistance > 0) {
                // 提示条：从球杆起点到拉拽位置（沿球杆方向）
                double tipEndX = poleStart.x + (poleEnd.x - poleStart.x) * (POLE_FIXED_LENGTH / (POLE_FIXED_LENGTH + currentPullDistance));
                double tipEndY = poleStart.y + (poleEnd.y - poleStart.y) * (POLE_FIXED_LENGTH / (POLE_FIXED_LENGTH + currentPullDistance));
                gc.setStroke(Color.RED);
                gc.setLineWidth(POLE_WIDTH / 1.2);
                gc.strokeLine(poleStart.x, poleStart.y, tipEndX, tipEndY);

                // 绘制力度指示器（新增）
                double powerRatio = currentPullDistance / MAX_PULL_DISTANCE;
                gc.setFill(Color.GRAY);
                gc.fillRect(20, TABLE_HEIGHT - 40, 200, 20);

                if (powerRatio < 0.3) {
                    gc.setFill(Color.GREEN);
                } else if (powerRatio < 0.7) {
                    gc.setFill(Color.YELLOW);
                } else {
                    gc.setFill(Color.RED);
                }
                gc.fillRect(20, TABLE_HEIGHT - 40, 200 * powerRatio, 20);

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(20, TABLE_HEIGHT - 40, 200, 20);

                gc.setFill(Color.WHITE);
                gc.setFont(new Font(14));
                gc.fillText("力度: " + (int) (powerRatio * 100) + "%", 90, TABLE_HEIGHT - 26);
            }
        }

        // 6. 绘制分数
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(20));
        gc.fillText("得分：" + score, 20, 30);

        // 7. 状态提示（新增）
        if (!allBallsStopped() && !isFreeBall) {
            gc.setFill(Color.YELLOW);
            gc.fillText("等待球静止...", TABLE_WIDTH - 150, 30);
        }
    }

    // 胜利/失败判定（原有逻辑不变）
    private boolean isVictory() {
        return score >= 15 && balls.size() == 1;
    }

    private boolean isFail() {
        return score >= 15 && balls.size() < 1;
    }

    // 胜利弹窗（原有逻辑不变）
    private void showVictoryDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(root.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("恭喜通关！");
        dialog.setWidth(300);
        dialog.setHeight(200);
        dialog.centerOnScreen();

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label message = new Label("球技不错噢！点击确定3秒后进入下一章~");
        message.setAlignment(Pos.CENTER);
        Label countdownLabel = new Label();

        Button confirmBtn = new Button("确定");
        confirmBtn.setOnAction(e -> {
            content.getChildren().remove(confirmBtn);
            content.getChildren().add(countdownLabel);

            int[] seconds = {3};
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                seconds[0]--;
                countdownLabel.setText("准备进入下一章：" + seconds[0] + "秒");
                if (seconds[0] <= 0) {
                    dialog.close();
                    if (victoryListener != null) victoryListener.onVictory();
                }
            }));
            timeline.setCycleCount(3);
            timeline.play();
        });

        content.getChildren().addAll(message, confirmBtn);
        dialog.setScene(new Scene(content));
        dialog.show();
    }

    // 提示弹窗（原有逻辑不变）
    private void showAlert(String title, String content) {
        ButtonType replayButton = new ButtonType("再来一次");
        ButtonType determineButton = new ButtonType("确定");

        Alert alert = new Alert(Alert.AlertType.INFORMATION, content, replayButton, determineButton);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.initOwner(root.getScene().getWindow());

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == replayButton) {
                score = 0;
                initializeBalls();
                initPolePoints(); // 重置球杆位置
                if (!isGameLoopRunning) {
                    isGameLoopRunning = true;
                    gameTimer.start();
                }
            }
        });
    }

    /**
     * 鼠标按下事件（仅两个作用：1.放置自由球 2.开始拉动球杆）
     */
    private void handleMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (isFreeBall) {
            // 自由球：放置白球
            whiteBall.x = Math.max(BALL_RADIUS, Math.min(x, TABLE_WIDTH - BALL_RADIUS));
            whiteBall.y = Math.max(BALL_RADIUS, Math.min(y, TABLE_HEIGHT - BALL_RADIUS));
            placingWhiteBall = true;
            whiteBall.vx = 0;
            whiteBall.vy = 0;
            if (!balls.contains(whiteBall)) {
                balls.add(whiteBall);
            }
        } else {
            // 非自由球：点击球杆→开始拉动（仅这一步需要点击）
            // 只有当所有球静止时才能操作球杆（新增条件）
            if (allBallsStopped() && !isPullingPole && isPointOnPole(x, y)) {
                isPullingPole = true;
                currentPullDistance = 0; // 初始拉拽距离归零
            }
        }
    }

    /**
     * 核心修复3：鼠标拖动事件
     */
    private void handleMouseDragged(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (isFreeBall && placingWhiteBall) {
            // 自由球：拖动放置白球
            whiteBall.x = Math.max(BALL_RADIUS, Math.min(x, TABLE_WIDTH - BALL_RADIUS));
            whiteBall.y = Math.max(BALL_RADIUS, Math.min(y, TABLE_HEIGHT - BALL_RADIUS));
        } else if (isPullingPole) {
            // 拉动状态：计算拉拽距离
            calculatePullDistance(x, y);
            calculatePolePoints(); // 更新球杆长度
        } else {
            // 拖动转动（按下鼠标移动时也能瞄准）
            // 只有当所有球静止时才能瞄准（新增条件）
            if (allBallsStopped()) {
                double dx = x - whiteBall.x;
                double dy = y - whiteBall.y;
                aimAngle = Math.atan2(dy, dx);
                calculatePolePoints();
            }
        }
    }

    /**
     * 鼠标移动事件
     */
    private void handleMouseMoved(MouseEvent event) {
        // 只有当所有球静止且不是自由球且没有在拉杆时才能转动球杆（新增条件）
        if (!isFreeBall && !isPullingPole && allBallsStopped()) {
            double dx = event.getX() - whiteBall.x;
            double dy = event.getY() - whiteBall.y;
            aimAngle = Math.atan2(dy, dx);
            calculatePolePoints();
        }
    }

    /**
     * 鼠标释放事件（仅两个作用：1.确认放置自由球 2.击球）
     */
    private void handleMouseReleased(MouseEvent event) {
        if (isFreeBall && placingWhiteBall) {
            // 自由球：确认放置，重置球杆
            isFreeBall = false;
            placingWhiteBall = false;
            initPolePoints(); // 重新初始化球杆（瞄准方向正确）
        } else if (isPullingPole) {
            // 拉动后释放：击球
            double speed = currentPullDistance * POLE_HIT_SCALE;
            whiteBall.vx = speed * Math.cos(aimAngle); // 沿瞄准方向击球
            whiteBall.vy = speed * Math.sin(aimAngle);

            // 重置球杆状态：恢复"滑动即转动"
            isPullingPole = false;
            currentPullDistance = 0;
            calculatePolePoints(); // 重置球杆长度
        }
    }

    /**
     * 辅助：判断鼠标是否点击在球杆上（用于开始拉动）
     */
    private boolean isPointOnPole(double px, double py) {
        if (poleStart == null || poleEnd == null) return false;

        // 计算点到线段的距离（容差3px，易点击）
        double lineLength = Math.hypot(poleEnd.x - poleStart.x, poleEnd.y - poleStart.y);
        double distToStart = Math.hypot(px - poleStart.x, py - poleStart.y);
        double distToEnd = Math.hypot(px - poleEnd.x, py - poleEnd.y);

        // 点在线段上的条件：距离之和≈线段长度，且在 segment 范围内
        return Math.abs(distToStart + distToEnd - lineLength) < 3
                && distToStart <= lineLength + 3
                && distToEnd <= lineLength + 3;
    }

    /**
     * 绑定按键事件（原有逻辑不变）
     */
    public void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.S) {
                gameTimer.stop();
                isGameLoopRunning = false;
                showVictoryDialog();
            }
        });
    }

    /**
     * 辅助类：存储坐标（球杆端点用）
     */
    private static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 球的内部类（原有逻辑不变）
     */
    private static class Ball {
        double x, y;
        double vx, vy;
        double radius;
        Color color;
        int number = 0;

        Ball(double x, double y, double radius, Color color, int number) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.number = number;
            this.vx = 0;
            this.vy = 0;
        }
    }
}