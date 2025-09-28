package friend_gift_JavaFX;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class GameOneFX extends Application {
    // 游戏数据和状态
    private int[][] data = new int[4][4]; // 存储拼图数据
    private int x = 0, y = 0; // 空白图片的位置
    private String path = "picture\\life\\"; // 图片路径
    private Random random = new Random();
    private int whichImage = random.nextInt(3); // 随机选择图片组

    // 胜利条件数据
    private final int[][] win = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };

    private int step = 0; // 步数统计
    private Stage primaryStage; // 主界面
    private GridPane puzzleGrid; // 拼图网格
    private Label stepLabel; // 步数标签
    private boolean showingCompleteImage = false; // 是否显示完整图片

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        initStage();
        initData();
        initUI();

        stage.show();
    }

    // 初始化舞台
    private void initStage() {
        primaryStage.setTitle("拼图单机版 v1.0");
        primaryStage.setWidth(603);
        primaryStage.setHeight(680);
        primaryStage.getIcons().add(new Image("file:picture\\gameOne\\pt_tb.png"));
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
    }

    // 初始化游戏数据
    private void initData() {
        int[] tempArr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        // 随机打乱数组
        for (int i = 0; i < tempArr.length; i++) {
            int index = random.nextInt(tempArr.length);
            int temp = tempArr[i];
            tempArr[i] = tempArr[index];
            tempArr[index] = temp;
        }

        // 填充二维数组并记录空白位置
        for (int i = 0; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                x = i / 4;
                y = i % 4;
            }
            data[i / 4][i % 4] = tempArr[i];
        }

        // 设置图片路径
        switch (whichImage) {
            case 0:
            case 1:
            case 2:
                path = "picture\\gameOne\\life\\";
                break;
        }
    }

    // 初始化UI
    private void initUI() {
        // 创建主容器
        BorderPane root = new BorderPane();

        // 创建菜单栏
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // 创建主游戏区域
        StackPane gameArea = new StackPane();

        // 创建步数标签
        stepLabel = new Label("步数：" + step);
        stepLabel.setFont(new Font("微软雅黑", 14));
        stepLabel.setTextFill(Color.BLACK);
        stepLabel.setPadding(new Insets(10, 0, 0, 50));

        // 创建拼图网格
        puzzleGrid = new GridPane();
        puzzleGrid.setHgap(0);
        puzzleGrid.setVgap(0);
        puzzleGrid.setPadding(new Insets(50, 30, 0, 0));

        // 加载拼图图片
        loadPuzzleImages();

        // 创建容器放置步数和拼图
        VBox gameContainer = new VBox(10);
        gameContainer.getChildren().addAll(stepLabel, puzzleGrid);
        gameContainer.setAlignment(Pos.TOP_LEFT);

        // 添加背景图片
        ImageView background = new ImageView(new Image("file:picture\\gameOne\\bg.jpg"));
        background.setFitWidth(520);
        background.setFitHeight(580);

        // 将游戏元素放在背景上方
        StackPane.setAlignment(gameContainer, Pos.TOP_LEFT);
        StackPane.setMargin(gameContainer, new Insets(30, 0, 0, 32));
        gameArea.getChildren().addAll(background, gameContainer);

        root.setCenter(gameArea);

        // 创建场景并添加键盘事件
        Scene scene = new Scene(root);
        setupKeyHandlers(scene);

        primaryStage.setScene(scene);
    }

    // 创建菜单栏
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // 功能菜单
        Menu functionMenu = new Menu("功能");

        // 更换图片子菜单
        Menu changeImageMenu = new Menu("更换图片");
        MenuItem landscapeItem = new MenuItem("风景");
        MenuItem animalItem = new MenuItem("动物");
        MenuItem lifeItem = new MenuItem("生活");

        changeImageMenu.getItems().addAll(landscapeItem, animalItem, lifeItem);

        // 其他功能项
        MenuItem replayItem = new MenuItem("重新开始");
        MenuItem reLoginItem = new MenuItem("重新登录");
        MenuItem closeItem = new MenuItem("关闭游戏");

        functionMenu.getItems().addAll(changeImageMenu, replayItem, reLoginItem, closeItem);

        // 关于我们菜单
        Menu aboutMenu = new Menu("关于我们");
        MenuItem accountItem = new MenuItem("公众号");
        aboutMenu.getItems().add(accountItem);

        // 添加事件处理
        landscapeItem.setOnAction(e -> changeImage(0));
        animalItem.setOnAction(e -> changeImage(1));
        lifeItem.setOnAction(e -> changeImage(2));
        replayItem.setOnAction(e -> restartGame());
        reLoginItem.setOnAction(e -> reLogin());
        closeItem.setOnAction(e -> primaryStage.close());
        accountItem.setOnAction(e -> showAccountInfo());

        menuBar.getMenus().addAll(functionMenu, aboutMenu);
        return menuBar;
    }

    // 加载拼图图片的核心方法：根据游戏状态（正常/胜利/显示完整图）加载对应内容
    private void loadPuzzleImages() {
        // 清空当前网格中的所有组件，准备重新加载
        puzzleGrid.getChildren().clear();

        // 检查是否已胜利且未处于显示完整图片模式：如果胜利则显示胜利界面
        if (victory() && !showingCompleteImage) {
            showVictoryImage();  // 显示胜利图片和弹窗
            return;  // 终止方法，不再执行后续加载逻辑
        }

        // 显示完整图片模式：当用户按A键时触发
        if (showingCompleteImage) {
            // 创建完整图片的ImageView，路径为当前图片组的"完整.jpg"
            ImageView completeImage = new ImageView(new Image("file:" + path + "完整.jpg"));
            // 设置图片大小为420x420（4个105x105拼图块的总尺寸）
            completeImage.setFitWidth(420);
            completeImage.setFitHeight(420);
            // 将完整图片添加到网格，占4行4列（填满整个拼图区域）
            puzzleGrid.add(completeImage, 0, 0, 4, 4);
            // 让图片在网格中居中显示
            puzzleGrid.setAlignment(Pos.CENTER);
            return;  // 终止方法，完成完整图片显示
        }

        // 正常拼图模式：循环加载4x4网格中的每个拼图块
        for (int i = 0; i < 4; i++) {  // 行索引（i对应二维数组的行）
            for (int j = 0; j < 4; j++) {  // 列索引（j对应二维数组的列）
                int value = data[i][j];  // 获取当前位置的拼图数值（0表示空白）

                if (value == 0) {
                    // 空白区域：创建一个灰色面板代替空白块
                    Pane emptyPane = new Pane();
                    emptyPane.setPrefSize(105, 105);  // 每个拼图块的固定大小
                    // 设置样式：浅灰色背景+灰色边框，与拼图块区分
                    emptyPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");
                    // 将空白面板添加到网格的(j,i)位置（GridPane的列在前，行在后）
                    puzzleGrid.add(emptyPane, j, i);
                } else {
                    // 拼图块：尝试加载对应数值的图片
                    try {
                        // 加载图片：路径为当前图片组+数值.jpg（如"picture\life\1.jpg"）
                        ImageView imageView = new ImageView(new Image("file:" + path + value + ".jpg"));
                        imageView.setFitWidth(105);  // 固定宽度，确保拼图对齐
                        imageView.setFitHeight(105); // 固定高度，确保拼图对齐
                        imageView.setStyle("" +
                                "-fx-border-width: 2px;"  + // 边框宽度
                                "-fx-border-style: solid;" +  // 边框样式（实线）
                                "-fx-border-color: #cccccc;"  // 边框颜色（浅灰）
                        );  // 灰色边框，区分拼图块
                        // 将拼图块添加到网格的(j,i)位置
                        puzzleGrid.add(imageView, j, i);
                    } catch (Exception e) {
                        // 图片加载失败时的容错处理：显示带数值的错误标签
                        System.out.println("图片加载失败: " + path + value + ".jpg");  // 打印错误路径
                        Label errorLabel = new Label(String.valueOf(value));  // 显示数值，方便调试
                        errorLabel.setPrefSize(105, 105);  // 与拼图块同大小
                        // 错误样式：浅红色背景+红色边框，明显区分正常拼图
                        errorLabel.setStyle("-fx-background-color: #ffcccc; -fx-border-color: #cccccc;");
                        errorLabel.setAlignment(Pos.CENTER);  // 数值居中显示
                        puzzleGrid.add(errorLabel, j, i);  // 添加到网格
                    }
                }
            }
        }

        // 确保整个拼图网格在容器中居中显示
        puzzleGrid.setAlignment(Pos.CENTER);
    }

    // 设置键盘事件处理
    private void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            // 按A键显示完整图片
            if (code == KeyCode.A) {
                showingCompleteImage = true;
                loadPuzzleImages();
                return;
            }

            // 按W键直接完成游戏
            if (code == KeyCode.W) {
                data = new int[][]{
                        {1, 2, 3, 4},
                        {5, 6, 7, 8},
                        {9, 10, 11, 12},
                        {13, 14, 15, 0}
                };
                x = 3;
                y = 3;
                loadPuzzleImages();
                return;
            }

            // 如果已经胜利则不处理其他按键
            if (victory()) {
                return;
            }

            // 处理方向键移动
            switch (code) {
                case LEFT:
                    moveLeft();
                    break;
                case RIGHT:
                    moveRight();
                    break;
                case UP:
                    moveUp();
                    break;
                case DOWN:
                    moveDown();
                    break;
            }
        });

        // 监听按键释放事件
        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();

            // 松开A键：恢复显示拼图
            if (code == KeyCode.A) {
                // 直接设置为隐藏状态（不反转）
                showingCompleteImage = false;
                loadPuzzleImages();
            }
        });
    }

    // 移动方法
    private void moveLeft() {
        if (y < 3) {
            data[x][y] = data[x][y + 1];
            data[x][y + 1] = 0;
            y++;
            step++;
            updateStepLabel();
            loadPuzzleImages();
        }
    }

    private void moveRight() {
        if (y > 0) {
            data[x][y] = data[x][y - 1];
            data[x][y - 1] = 0;
            y--;
            step++;
            updateStepLabel();
            loadPuzzleImages();
        }
    }

    private void moveUp() {
        if (x < 3) {
            data[x][y] = data[x + 1][y];
            data[x + 1][y] = 0;
            x++;
            step++;
            updateStepLabel();
            loadPuzzleImages();
        }
    }

    private void moveDown() {
        if (x > 0) {
            data[x][y] = data[x - 1][y];
            data[x - 1][y] = 0;
            x--;
            step++;
            updateStepLabel();
            loadPuzzleImages();
        }
    }

    // 更新步数标签
    private void updateStepLabel() {
        stepLabel.setText("步数：" + step);
    }

    // 检查是否胜利
    private boolean victory() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (data[i][j] != win[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // 显示胜利图片
    private void showVictoryImage() {
        ImageView victoryImage = new ImageView(new Image("file:picture\\win.png"));
        victoryImage.setFitWidth(200);
        victoryImage.setFitHeight(76);
        puzzleGrid.add(victoryImage, 0, 0, 4, 4);
        puzzleGrid.setAlignment(Pos.CENTER);

        // 显示胜利弹窗
        showVictoryDialog();
    }

    // 显示胜利弹窗
    private void showVictoryDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("恭喜通关！");
        dialog.setWidth(300);
        dialog.setHeight(200);
        dialog.centerOnScreen();

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label message = new Label("恭喜通过本关！点击确定3秒后进入下一章~");
        message.setAlignment(Pos.CENTER);

        // 倒计时标签
        Label countdownLabel = new Label();

        Button confirmBtn = new Button("确定");
        confirmBtn.setOnAction(e -> {
            content.getChildren().remove(confirmBtn);
            content.getChildren().add(countdownLabel);

            // 3秒倒计时
            int[] seconds = {3};
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                seconds[0]--;
                countdownLabel.setText("准备进入下一关：" + seconds[0] + "秒");

                if (seconds[0] <= 0) {
                    dialog.close();
                    // 关闭当前游戏，打开下一关
                    primaryStage.close();
                    try {
                        new GameTwoFX().start(new Stage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }));
            timeline.setCycleCount(3);
            timeline.play();
        });

        content.getChildren().addAll(message, confirmBtn);
        dialog.setScene(new Scene(content));
        dialog.show();
    }

    // 更换图片
    private void changeImage(int type) {
        whichImage = type;
        switch (type) {
            case 0:
                path = "picture\\landscape\\";
                break;
            case 1:
                path = "picture\\animal\\";
                break;
            case 2:
                path = "picture\\life\\";
                break;
        }
        restartGame();
    }

    // 重新开始游戏
    private void restartGame() {
        step = 0;
        updateStepLabel();
        showingCompleteImage = false;
        initData();
        loadPuzzleImages();
    }

    // 重新登录
    private void reLogin() {
        primaryStage.close();
        new LoginFX().start(new Stage());
    }

    // 显示公众号信息
    private void showAccountInfo() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("关于我们");
        dialog.setWidth(344);
        dialog.setHeight(344);
        dialog.centerOnScreen();

        Label label = new Label("小H的公众号");
        label.setFont(new Font("微软雅黑", 18));
        label.setAlignment(Pos.CENTER);

        dialog.setScene(new Scene(new StackPane(label)));
        dialog.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
