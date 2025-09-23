package friend_gift_JavaFX;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform; // 新增：用于推迟UI任务执行

/**
 * JavaFX 小球反弹游戏：控制长条接住小球，防止掉落，共3个关卡，计时结束通关
 * 核心逻辑：方向键控制长条移动，小球碰撞边界/长条后反弹，倒计时结束则通关下一关
 */
public class GameTwoFX extends Application {
    // ========================= 游戏核心变量（对应原 Swing 成员变量）=========================
    private Timeline ballTimeline;    // 小球移动定时器（替代 Swing Timer）
    private Timeline stripTimeline;   // 长条移动定时器（替代 Swing Timer）
    private Timeline countDownTimeline;// 关卡倒计时定时器（替代原 GameTimer）
    private Canvas gameCanvas;        // 游戏画布（替代 Swing JPanel，用于自定义绘制）
    private GraphicsContext gc;       // 画布绘图上下文（用于绘制小球、长条、文字）
    private Label countDownLabel;     // 倒计时显示标签（替代原 GameTimer 显示）

    // 小球属性：位置(x,y)、大小、速度(dx,dy)
    private double circleX = 10;
    private double circleY = 10;
    private int circleSize = 20;
    private double c_dx = 2;  // 小球x方向速度（正数右移，负数左移）
    private double c_dy = 2;  // 小球y方向速度（正数下移，负数上移）

    // 长条属性：位置(x,y)、宽高、速度(dx,dy)
    private double stripX = 10;
    private double stripY = 500;
    private int stripWidth = 60;
    private int stripHeight = 5;
    private double s_dx = 5;  // 长条x方向速度
    private double s_dy = 5;  // 长条y方向速度

    // 按键状态：记录方向键是否被按下（控制长条移动）
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    // 关卡与胜利状态：1-3关，通关标志控制关卡切换
    private int currentRound = 1;
    private boolean isRoundOneWin = false;
    private boolean isRoundTwoWin = false;
    private boolean isRoundThreeWin = false;

    // 倒计时时间：每关60秒（对应原 GameTimer 的60秒）
    private int remainingSeconds = 60;


    // ========================= 程序入口：JavaFX 启动方法 =========================
    @Override
    public void start(Stage primaryStage) {
        // 1. 初始化主窗口（Stage 对应 Swing JFrame）
        initStage(primaryStage);

        // 2. 初始化布局（BorderPane 对应原 BorderLayout，分上中下区域）
        BorderPane root = new BorderPane();

        // 3. 初始化倒计时标签（放在顶部）
        initCountDownLabel();
//        root.setTop(countDownLabel);
//        BorderPane.setAlignment(countDownLabel, Pos.CENTER);  // 倒计时标签居中

        // 4. 初始化游戏画布（放在中间，核心绘制区域）
        initGameCanvas();
        root.setCenter(gameCanvas);

        // 5. 初始化菜单（顶部菜单栏，对应原 JMenuBar）
        MenuBar menuBar = initMenuBar();
        VBox topVBox = new VBox(5);
        topVBox.getChildren().addAll(menuBar, countDownLabel);
        BorderPane.setAlignment(topVBox, Pos.TOP_LEFT);

        root.setTop(topVBox);
        // 6. 初始化场景（Scene 对应原 JFrame 的 ContentPane）
        Scene scene = new Scene(root, 603, 700);
        // 绑定按键事件（控制长条移动，替代原 KeyListener）
        initKeyEvents(scene);

        // 7. 绑定场景到窗口，显示窗口
        primaryStage.setScene(scene);
        primaryStage.show();

        // 8. 启动第一关（游戏入口）
        initRound(1);
    }


    // ========================= 窗口初始化 =========================
    /**
     * 初始化主窗口属性（大小、标题、关闭方式等）
     * @param primaryStage 主窗口（JavaFX 顶层容器）
     */
    private void initStage(Stage primaryStage) {
        primaryStage.setTitle("不要让小球掉落~");  // 窗口标题（与原 Swing 一致）
        primaryStage.setWidth(603);                // 窗口宽度（与原 Swing 一致）
        primaryStage.setHeight(680);               // 窗口高度（与原 Swing 一致）
        primaryStage.setResizable(false);          // 禁止窗口缩放（避免游戏区域变形）
        primaryStage.centerOnScreen();             // 窗口居中（替代原 setLocationRelativeTo(null)）
        // 窗口关闭时停止所有定时器（防止内存泄漏）
        primaryStage.setOnCloseRequest(e -> stopAllTimelines());
    }


    // ========================= 倒计时标签初始化 =========================
    /**
     * 初始化倒计时显示（顶部标签，显示剩余秒数）
     */
    private void initCountDownLabel() {
        countDownLabel = new Label("剩余时间：" + remainingSeconds + "秒");
        countDownLabel.setFont(new Font("微软雅黑", 16));  // 字体样式
        countDownLabel.setTextFill(Color.RED);            // 文字红色（突出计时）
        countDownLabel.setPadding(new javafx.geometry.Insets(5));  // 内边距（避免贴边）
    }


    // ========================= 游戏画布初始化 =========================
    /**
     * 初始化游戏画布（核心绘制区域，小球、长条、关卡文字都画在这里）
     */
    private void initGameCanvas() {
        gameCanvas = new Canvas(603, 700);  // 画布大小与窗口一致
        gc = gameCanvas.getGraphicsContext2D();  // 获取绘图上下文（画笔）
        // 初始绘制空白画布（白色背景，与原 Swing JPanel 默认背景一致）
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }


    // ========================= 菜单初始化 =========================
    /**
     * 初始化菜单栏（"重新开始"菜单，包含1-3关选项）
     * @return MenuBar 菜单栏（替代原 JMenuBar）
     */
    private MenuBar initMenuBar() {
        MenuBar menuBar = new MenuBar();

        // 1. 创建"重新开始"菜单（对应原 JMenu）
        Menu replayMenu = new Menu("重新开始");

        // 2. 创建菜单项（1-3关，对应原 JMenuItem）
        MenuItem replayOne = new MenuItem("第一关");
        MenuItem replayTwo = new MenuItem("第二关");
        MenuItem replayThree = new MenuItem("第三关");

        // 3. 绑定菜单项点击事件（处理重新开始逻辑）
        replayOne.setOnAction(e -> handleReplay(1));
        replayTwo.setOnAction(e -> handleReplay(2));
        replayThree.setOnAction(e -> handleReplay(3));

        // 4. 组装菜单（菜单项加入菜单，菜单加入菜单栏）
        replayMenu.getItems().addAll(replayOne, replayTwo, replayThree);
        menuBar.getMenus().add(replayMenu);

        return menuBar;
    }


    // ========================= 按键事件初始化 =========================
    /**
     * 绑定按键事件（方向键控制长条移动，A/D键调整长条宽度）
     * @param scene 场景（按键事件需绑定到场景上）
     */
    private void initKeyEvents(Scene scene) {
        // 1. 按键按下事件：记录按键状态为"按下"
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    leftPressed = true;
                    break;
                case RIGHT:
                    rightPressed = true;
                    break;
                case UP:
                    upPressed = true;
                    break;
                case DOWN:
                    downPressed = true;
                    break;
                case A:  // A键：长条宽度减少20（原 Swing 逻辑保留）
                    stripWidth = Math.max(30, stripWidth - 30);  // 最小宽度25，避免消失
                    break;
                case D:  // D键：长条宽度增加20（原 Swing 逻辑保留）
                    stripWidth = Math.min(680, stripWidth + 30); // 最大宽度680，避免超出窗口
                    break;
            }
            // 按下按键时启动长条定时器（防止定时器未启动）
            if (stripTimeline != null && !stripTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
                stripTimeline.play();
            }
        });

        // 2. 按键释放事件：记录按键状态为"未按下"
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT:
                    leftPressed = false;
                    break;
                case RIGHT:
                    rightPressed = false;
                    break;
                case UP:
                    upPressed = false;
                    break;
                case DOWN:
                    downPressed = false;
                    break;
            }
            // 所有按键都释放时，停止长条定时器（节省资源）
            if (!leftPressed && !rightPressed && !upPressed && !downPressed) {
                if (stripTimeline != null) {
                    stripTimeline.stop();
                }
            }
        });
    }


    // ========================= 关卡初始化（核心逻辑）=========================
    /**
     * 初始化指定关卡（复位位置、设置速度、启动定时器）
     * @param round 关卡编号（1-3）
     */
    private void initRound(int round) {
        // 1. 停止所有旧定时器（避免多定时器冲突）
        stopAllTimelines();

        // 2. 复位游戏状态（位置、按键、倒计时）
        resetGameState();

        // 3. 更新当前关卡，设置对应速度（难度递增）
        currentRound = round;
        setRoundSpeed(round);

        // 4. 启动游戏定时器（小球移动、长条移动、倒计时）
        startGameTimelines();

        // 5. 初始绘制关卡界面（显示关卡文字）
        drawGame();
    }


    // ========================= 游戏状态复位 =========================
    /**
     * 复位游戏状态（小球/长条位置、按键状态、倒计时、长条宽度）
     */
    private void resetGameState() {
        // 小球位置复位到左上角（10,10）
        circleX = 10;
        circleY = 10;
        // 长条位置复位到（10,500）
        stripX = 10;
        stripY = 500;
        // 长条宽度复位到默认55
        stripWidth = 55;
        // 按键状态复位（防止关卡切换后按键仍被激活）
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;
        // 倒计时复位到60秒
        remainingSeconds = 60;
        countDownLabel.setText("剩余时间：" + remainingSeconds + "秒");
    }


    // ========================= 关卡速度设置（难度控制）=========================
    /**
     * 根据关卡设置小球和长条的速度（关卡越高，速度越快，难度越大）
     * @param round 关卡编号（1-3）
     */
    private void setRoundSpeed(int round) {
        switch (round) {
            case 1:  // 第一关：速度最慢
                s_dx = 5;
                s_dy = 5;
                c_dx = 2;
                c_dy = 2;
                break;
            case 2:  // 第二关：速度中等
                s_dx = 10;
                s_dy = 5;
                c_dx = 3;
                c_dy = 3;
                break;
            case 3:  // 第三关：速度最快（长条y方向速度减缓，增加难度）
                s_dx = 15;
                s_dy = 1;
                c_dx = 3;
                c_dy = 3;
                break;
        }
    }

    // ========================= 启动游戏定时器（动画核心）=========================
    /**
     * 启动三个核心定时器：
     * 1. 小球移动定时器：10ms/帧（控制小球位置更新）
     * 2. 长条移动定时器：10ms/帧（控制长条位置更新，避免移动过于频繁）
     * 3. 倒计时定时器：1s/帧（控制关卡计时）
     */
    private void startGameTimelines() {
        // 1. 小球移动定时器（1ms/帧，与原 Swing ballTimer 延迟一致）
        ballTimeline = new Timeline(new KeyFrame(
                Duration.millis(10),  // 每10毫秒执行一次
                e -> {
                    updateBallPosition();  // 更新小球位置
                    checkCollision();      // 检测碰撞（边界/长条）
                    drawGame();            // 重绘画布（显示最新状态）
                }
        ));
        ballTimeline.setCycleCount(Timeline.INDEFINITE);  // 无限循环（直到停止）
        ballTimeline.play();  // 启动定时器

        // 2. 长条移动定时器（10ms/帧，与原 Swing stripTimer 延迟一致）
        stripTimeline = new Timeline(new KeyFrame(
                Duration.millis(10),  // 每10毫秒执行一次
                e -> {
                    updateStripPosition();  // 更新长条位置
                    drawGame();             // 重绘画布
                }
        ));
        stripTimeline.setCycleCount(Timeline.INDEFINITE);
        stripTimeline.play();

        // 3. 倒计时定时器（1s/帧，控制关卡计时）
        countDownTimeline = new Timeline(new KeyFrame(
                Duration.seconds(1),  // 每1秒执行一次
                e -> {
                    remainingSeconds--;  // 剩余时间减1
                    countDownLabel.setText("剩余时间：" + remainingSeconds + "秒");
                    // 时间到：触发关卡通关逻辑
                    if (remainingSeconds <= 0) {
                        handleTimeExpired();
                    }
                }
        ));
        countDownTimeline.setCycleCount(Timeline.INDEFINITE);
        countDownTimeline.play();
    }


    // ========================= 小球位置更新 =========================
    /**
     * 根据小球速度更新位置（x方向加c_dx，y方向加c_dy）
     */
    private void updateBallPosition() {
        circleX += c_dx;
        circleY += c_dy;
    }


    // ========================= 长条位置更新 =========================
    /**
     * 根据按键状态更新长条位置（边界检测：避免长条超出窗口）
     */
    private void updateStripPosition() {
        // 左移：按键按下且长条左边界不小于0
        if (leftPressed && stripX > 0) {
            stripX -= s_dx;
        }
        // 右移：按键按下且长条右边界不大于窗口宽度
        if (rightPressed && stripX + stripWidth < gameCanvas.getWidth()) {
            stripX += s_dx;
        }
        // 上移：按键按下且长条上边界不小于0
        if (upPressed && stripY > 0) {
            stripY -= s_dy;
        }
        // 下移：按键按下且长条下边界不大于窗口高度
        if (downPressed && stripY + stripHeight < gameCanvas.getHeight()) {
            stripY += s_dy;
        }
    }


    // ========================= 碰撞检测（核心逻辑）=========================
    /**
     * 检测小球碰撞：
     * 1. 碰撞左右上边界：速度反向
     * 2. 碰撞长条：y方向速度反向（反弹）
     * 3. 碰撞下边界：游戏结束（小球掉落）
     */
    private void checkCollision() {
        // 1. 碰撞长条：小球底部接触长条顶部，且小球x范围与长条x范围重叠
        if (circleX >= stripX - 2 &&                // 小球左边界 <= 长条右边界
                circleX + circleSize <= stripX + stripWidth + 2 &&  // 小球右边界 >= 长条左边界
                circleY + circleSize >= stripY  - 1&&  // 小球底部 >= 长条顶部
                circleY + circleSize <= stripY + c_dy) { // 小球底部 <= 长条顶部+小球y速度（避免穿透）
            c_dy = -c_dy;  // y方向速度反向（小球反弹向上）
        }

        // 2. 碰撞左右边界：x方向速度反向
        if (circleX <= 0 || circleX + circleSize >= gameCanvas.getWidth()) {
            c_dx = -c_dx;
        }

        // 3. 碰撞上边界：y方向速度反向
        if (circleY <= 0) {
            c_dy = -c_dy;
        }

        // 4. 碰撞下边界：游戏结束（小球掉落）
        if (circleY + circleSize >= gameCanvas.getHeight()) {
            stopAllTimelines();  // 停止所有定时器
            showGameOverDialog();// 显示游戏结束弹窗
        }
    }


    // ========================= 绘制游戏元素 =========================
    /**
     * 绘制游戏所有元素：背景、小球、长条、关卡文字
     */
    private void drawGame() {
        // 1. 清除画布（每次绘制前清空，避免残影）
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // 2. 绘制小球（黄色圆形，对应原 drawCircle）
        gc.setFill(Color.YELLOW);
        gc.fillOval(circleX, circleY, circleSize, circleSize);  // 椭圆（宽高相等即圆形）

        // 3. 绘制长条（黑色矩形，对应原 drawStrip）
        gc.setFill(Color.rgb(160, 90, 40));
        gc.fillRect(stripX, stripY, stripWidth, stripHeight);  // 矩形（长条）

        // 4. 绘制关卡文字（红色"第X关"，对应原 paintComponent 中的文字）
        gc.setFill(Color.RED);
        gc.setFont(new Font("微软雅黑", 20));
        gc.fillText("第" + currentRound + "关", 20, 30);  // 文字位置（20,30）
    }


    // ========================= 菜单点击处理（重新开始关卡）=========================
    /**
     * 处理"重新开始"菜单点击：验证关卡权限（需先通前一关），再初始化对应关卡
     * @param round 要重新开始的关卡
     */
    private void handleReplay(int round) {
        // 验证关卡权限：第二关需先通第一关，第三关需先通前两关
        if (round == 2 && !isRoundOneWin) {
            showAlert("提示", "请先通过第一关~");
            return;
        }
        if (round == 3 && (!isRoundOneWin || !isRoundTwoWin)) {
            showAlert("提示", "请先通过前两关~");
            return;
        }

        // 初始化目标关卡
        initRound(round);
    }


    /**
     * 倒计时结束：标记当前关卡胜利，切换到下一关或显示通关弹窗（修复后）
     */
    private void handleTimeExpired() {
        // 1. 先立即停止所有定时器（必须在 runLater 外）
        stopAllTimelines();

        // 2. 推迟弹窗和关卡切换逻辑
        Platform.runLater(() -> {
            // 标记当前关卡胜利（原逻辑保留）
            if (currentRound == 1) {
                isRoundOneWin = true;
                showAlert("通关提示", "恭喜通过第1关！进入下一关");
                initRound(2);  // 切换到第二关
            } else if (currentRound == 2) {
                isRoundTwoWin = true;
                showAlert("通关提示", "恭喜通过第2关！进入下一关");
                initRound(3);  // 切换到第三关
            } else if (currentRound == 3) {
                isRoundThreeWin = true;
                showAlert("通关提示", "恭喜通关所有关卡！");
            }
        });
    }


    /**
     * 小球掉落时显示游戏结束弹窗（修复后：推迟到动画周期外执行）
     */
    private void showGameOverDialog() {
        // 1. 先立即停止所有定时器（必须在 runLater 外，避免动画继续执行）
        stopAllTimelines();

        // 2. 用 Platform.runLater 推迟弹窗，等待当前动画周期结束
        Platform.runLater(() -> {
            showAlert("游戏结束", "小球掉落了~");
            // 3. 弹窗关闭后，重新启动当前关卡（原逻辑保留）
//            initRound(currentRound);
        });
    }


    // ========================= 通用弹窗工具 =========================
    /**
     * 显示弹窗（提示/警告/通关信息，替代原 JOptionPane）
     * @param title 弹窗标题
     * @param content 弹窗内容
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);  // 隐藏头部文本
        alert.setContentText(content);
        alert.initOwner(gameCanvas.getScene().getWindow());  // 弹窗依附于游戏窗口
        alert.showAndWait();  // 阻塞式弹窗（不关闭则无法操作游戏）
    }


    // ========================= 停止所有定时器 =========================
    /**
     * 停止所有定时器（关卡切换、窗口关闭时调用，避免内存泄漏）
     */
    private void stopAllTimelines() {
        if (ballTimeline != null) {
            ballTimeline.stop();
        }
        if (stripTimeline != null) {
            stripTimeline.stop();
        }
        if (countDownTimeline != null) {
            countDownTimeline.stop();
        }
    }


    // ========================= 程序启动入口 =========================
    public static void main(String[] args) {
        launch(args);  // JavaFX 固定启动方式（触发 start 方法）
    }
}