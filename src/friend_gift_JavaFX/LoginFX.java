package friend_gift_JavaFX;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.PauseTransition;

public class LoginFX extends Application implements Database {
    // 颜色定义
    private static final Color PRIMARY_COLOR = new Color(52/255.0, 152/255.0, 219/255.0, 1);
    private static final Color SECONDARY_COLOR = new Color(41/255.0, 128/255.0, 185/255.0, 1);
    private static final Color TEXT_COLOR = new Color(51/255.0, 51/255.0, 51/255.0, 1);
    private static final Color LIGHT_GRAY = new Color(245/255.0, 245/255.0, 245/255.0, 1);
    private static final Color ERROR_COLOR = new Color(231/255.0, 76/255.0, 60/255.0, 1);

    // 组件定义
    private Button loginButton = new Button("登录"); // 登录按钮
    private Button registerButton = new Button("注册"); // 注册按钮
    private Label tipsButton = new Label(); // 提示按钮
    private Button captchaButton = new Button(); // 验证码按钮

    private String captcha = ""; // 存储当前生成的验证码

    private TextField idField = new TextField(); // 账号输入框
    private PasswordField passwordField = new PasswordField(); // 密码输入框
    private TextField captchaField = new TextField(); // 验证码输入框

    private Player player = new Player("zhouqixuan", "20041201");
    private List<Player> players = new ArrayList<>();

    private Label loginTips = new Label(); // 登录提示标签
    private Label titleLabel = new Label(""); // 标题

    // 创建登录表单面板（VBox垂直布局，组件按垂直方向排序，间距统一）
    private VBox loginPanel = new VBox(20);

    // 创建表单网格面板
    private GridPane formGrid = new GridPane(); // 网格布局

    // 验证码行用HBox水平布局 包裹输入框和按钮
    private HBox captchaBox = new HBox(10); // 组件间距 10 像素

    // 登录按钮HBox水平布局
    private HBox LoginButtonBox = new HBox(5); // 按钮间距5

    // 隐藏提示按钮水平布局
    private HBox tipsButtonBox = new HBox(1);

    // 创建主面板：StackPane（根布局容器）堆叠布局，组件按顺序堆叠，后加的在上面
    private StackPane root = new StackPane();
    /**
     * JavaFX 程序核心方法：初始化窗口和UI布局
     * 类似 Swing 中在 main 方法里创建JFrame并添加组件
     * @param primaryStage 主窗口
     */
    @Override
    public void start(Stage primaryStage) {
        // 初始化主窗口属性
        primaryStage.setTitle("登入幻想星球"); // 设置标题
        primaryStage.setWidth(500); // 设置宽度
        primaryStage.setHeight(600); // 设置高度
        primaryStage.setResizable(false); // 禁止窗口缩放
        primaryStage.getIcons().add(new Image("file:picture/login/bg.jpg"));

        // 初始化数据
        players.add(player);

        initImage(); // 载入图片

        initLoginPanel(); // 载入登录面板

        initLabel(); // 载入标签

        initTextField(); // 载入文本框

        initButton(); // 载入按键

        // 生成验证码
        setCaptcha();
        styleCaptchaButton(); // 设置验证码按钮样式

        // 水平排列输入框和按钮
        captchaBox.getChildren().addAll(captchaField, captchaButton);
        formGrid.add(captchaBox, 1, 2); // 水平布局加入网格：列 1，行 2

        // 添加所有组件到登录面板（VBox 垂直排列：标题 -> 表单 -> 按钮）
        loginPanel.getChildren().addAll(titleLabel, formGrid, LoginButtonBox, tipsButtonBox);

        // 将登录面板加入根布局
        root.getChildren().add(loginPanel);

        // 创建场景：JavaF 特有概念，包含根布局和窗口大小
        // 类似 Swing中把根容器加入JFrame，Scene 是组件的“舞台场景”
        Scene scene = new Scene(root);
        primaryStage.setScene(scene); // 主窗口设置场景

        // 显示窗口
        primaryStage.show();

        // 设置事件监听（按钮点击、输入框变化等）
        setEventListeners(primaryStage);
    }

    /**
     * 载入登录面板
     */
    private void initLoginPanel() {
        loginPanel.setAlignment(Pos.CENTER); // 面板内所有组件居中对齐
        // 面板内边距（上150，右50，下50，左50）
        loginPanel.setPadding(new Insets(100, 50, 50, 50));
        loginPanel.setOpacity(0.9); // 面板透明度（0完全透明，1不透明）

        // 登录栏组件面板
        formGrid.setHgap(10); // 列之间间距10像素
        formGrid.setVgap(20); // 行之间间距20像素
        formGrid.setAlignment(Pos.CENTER); // 网格内组件居中
    }

    private void initImage() {
        // 添加背景图片
        try {
            Image bgImage = new Image("file:picture/login/bg.jpg");
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(500);
            bgView.setFitHeight(600);
            root.getChildren().add(bgView); // 把图片加入根布局（作为底层）
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // 如果背景图片加载失败，使用默认背景色
            root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        }
    }

    /**
     * 载入标签
     */
    private void initLabel() {
        // 设置标题
        titleLabel.setFont(new Font("微软雅黑", 35));
        titleLabel.setTextFill(Color.WHITE); // 文本颜色（白色）
        titleLabel.setAlignment(Pos.CENTER); // 标签内文本居中
        // 标签下方留20像素间距
        titleLabel.setPadding(new Insets(0, 0, 20, 0));

        // 账号标签
        Label idLabel = new Label("账号：");
        styleLabel(idLabel); // 调用自定义方法设置标签样式
        formGrid.add(idLabel, 0, 0); // 加入网格：列 0，行 0

        // 密码标签
        Label passwordLabel = new Label("密码：");
        styleLabel(passwordLabel);
        formGrid.add(passwordLabel, 0, 1); // 列 0，行 1

        // 验证码标签
        Label captchaLabel = new Label("验证码：");
        styleLabel(captchaLabel);
        formGrid.add(captchaLabel, 0, 2);

        // 提示信息
        loginTips.setFont(new Font("微软雅黑", 14)); // 提示文本字体
        loginTips.setTextFill(ERROR_COLOR); // 提示文本颜色
        formGrid.add(loginTips, 1, 3); // 提示标签加入网格：列 1，行 3
    }

    /**
     * 载入文本框
     */
    private void initTextField() {
        // 账号输入栏
        styleTextField(idField); // 调用自定义方法设置输入框样式
        formGrid.add(idField, 1, 0); // 加入网格：列 1，行 0

        // 密码输入栏
        styleTextField(passwordField);
        formGrid.add(passwordField, 1, 1); // 列 1， 行 1

        // 验证码输入栏
        styleTextField(captchaField);
        captchaField.setPrefWidth(150); // 固定输入框宽度（150 像素）
    }

    /**
     * 载入按钮
     */
    private void initButton() {
        // 登录按钮
        styleButton(loginButton, PRIMARY_COLOR, Color.WHITE); // 设置登录按钮样式
        LoginButtonBox.setAlignment(Pos.CENTER); // 按钮居中
        LoginButtonBox.getChildren().add(loginButton); // 加入登录按钮

        // 提示按钮
        styleTipsButton();
        // 调整位置
        HBox.setMargin(tipsButton, new Insets(10, 35, 0, 0));
        tipsButtonBox.setAlignment(Pos.CENTER);
        tipsButtonBox.getChildren().add(tipsButton); // 加入提示按钮

        GridPane.setMargin(tipsButton, new Insets(1, 0, 0, 0));
    }

    /**
     * 自定义标签样式
     *
     * @param label 标签
     */
    private void styleLabel(Label label) {
        label.setFont(new Font("微软雅黑", 18));
        label.setTextFill(Color.BLACK);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(80);
    }

    /**
     * 统一设置TextField样式
     * @param textField 要设置样式的输入框
     */
    private void styleTextField(TextField textField) {
        textField.setFont(new Font("微软雅黑", 16));
        textField.setPrefSize(220, 40);
        textField.setBorder(new Border(new BorderStroke(
                new Color(200/255.0, 200/255.0, 200/255.0, 1), // 边框颜色（浅灰）
                BorderStrokeStyle.SOLID, // 边框样式（实线）
                new CornerRadii(3), // 边框圆角
                new BorderWidths(1) // 边框宽度
        )));
        textField.setPadding(new Insets(5, 10, 5, 10)); // 输入框内边距（文本与边框间距）
    }

    /**
     * 统一设置 Button 样式
     * @param button 要设置样式的按钮
     * @param bgColor 按钮背景色
     * @param textColor 按钮文本色
     */
    private void styleButton(Button button, Color bgColor, Color textColor) {
        button.setFont(new Font("微软雅黑", 16));
        button.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(5), null)));
        button.setTextFill(textColor);
        button.setPrefSize(110, 45);
        button.setBorder(new Border(new BorderStroke(
                PRIMARY_COLOR, // 边框颜色
                BorderStrokeStyle.SOLID, // 实线
                new CornerRadii(5), // 圆角
                new BorderWidths(2) // 边框宽度
        )));
        button.setFocusTraversable(false); // 取消按钮默认焦点

        // 鼠标悬停效果
        button.setOnMouseEntered(e -> {
            // 鼠标进入时：背景色变为次要色（深蓝）
            button.setBackground(new Background(new BackgroundFill(SECONDARY_COLOR, new CornerRadii(5), null)));
        });

        button.setOnMouseExited(e -> {
            // 鼠标离开时：背景色恢复原背景色
            button.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(5), null)));
        });
    }

    /**
     * 设置验证码按钮样式
     */
    private void styleCaptchaButton() {
        captchaButton.setPrefSize(100, 40);
        captchaButton.setFont(new Font("Arial", 18)); // Arial字体更难识别，增加安全性
        captchaButton.setBackground(new Background(new BackgroundFill(LIGHT_GRAY, new CornerRadii(3), null)));
        captchaButton.setBorder(new Border(new BorderStroke(
                PRIMARY_COLOR,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(1)
        )));
        // 鼠标悬停时显示文本
        captchaButton.setTooltip(new Tooltip("点击更换验证码"));
    }

    /**
     * 设置隐藏提示按钮样式
     */
    private void styleTipsButton() {
        // 1. 设置 Label 为小点（文本用一个像素的字符）
        tipsButton.setText("•"); // 用小点字符替代按钮
        tipsButton.setFont(new Font(20)); // 字体大小设为1（最小有效字体）
        tipsButton.setTextFill(Color.BLACK);
        // 2. 去除内边距和边框
        tipsButton.setPadding(new Insets(0));

//        // 临时添加背景色（帮助定位Label位置，调试完可删除）
//        tipsButton.setBackground(new Background(
//                new BackgroundFill(Color.RED, new CornerRadii(1), null)
//        ));

        // 3. 设置尺寸（视觉上比 Button 更小）
        tipsButton.setPrefSize(10, 10);

        // 4. 允许点击（Label 默认不可点击，需添加鼠标事件）
        tipsButton.setCursor(Cursor.HAND); // 鼠标悬停显示手型
        tipsButton.setTooltip(new Tooltip("查看提示"));
    }

    /**
     * 生成 4 为随机验证码（数字 + 大写字母 + 小写字母）
     */
    private void setCaptcha() {
        captcha = "";
        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            int type = r.nextInt(3);
            switch (type) {
                case 0:
                    captcha += r.nextInt(10);
                    break;
                case 1:
                    captcha += (char) (r.nextInt(26) + 65);
                    break;
                case 2:
                    captcha += (char) (r.nextInt(26) + 97);
                    break;
            }
        }

        // 设置验证码文本和随机颜色
        captchaButton.setText(captcha);
        // 随机设置验证码文本颜色
        captchaButton.setTextFill(new Color(
                r.nextDouble() * 0.4,  // 0-0.4的红色分量
                r.nextDouble() * 0.4,  // 0-0.4的绿色分量
                r.nextDouble(),        // 0-1的蓝色分量
                1                      // 不透明
        ));
    }

    /**
     * 绑定所有组件的事件监听
     * @param primaryStage 主窗口（用于登录成功后关闭）
     */
    private void setEventListeners(Stage primaryStage) {
        // 登录按钮事件
        loginButton.setOnAction(e -> handleLogin());

        // 验证码按钮事件
        captchaButton.setOnAction(e -> setCaptcha());

        // 提示按钮事件
        tipsButton.setOnMouseClicked(e -> showTips());
    }

    /**
     * 处理登录逻辑
     */
    private void handleLogin() {
        String id = idField.getText().trim(); // trim去除前后空格，避免用户误输入空格
        int indexId = findPlayerId(id);
        String password = passwordField.getText();

        if (id.isEmpty()) {
            loginTips.setText("账号提示：请仔细观察");
        } else if (password.isEmpty()) {
            if (indexId == -1) {
                loginTips.setText("账号不对哈哈哈这么简单都看不出来");
                setCaptcha();
            } else {
                loginTips.setText("请输入您的密码，密码是账号的生日");
            }
        } else if (captchaField.getText().isEmpty()) {
            loginTips.setText("请输入验证码");
        } else if (!captcha.equalsIgnoreCase(captchaField.getText().trim())) {
            loginTips.setText("验证码错误，请重新输入");
            captchaField.setText("");
            setCaptcha();
        } else {
            if (indexId == -1) {
                loginTips.setText("账号不对哈哈哈这么简单都看不出来");
                captchaField.setText("");
                setCaptcha();
            } else {
                if (players.get(indexId).getPassword().equals(password)) {
                    // 登录成功，关闭当前窗口，打开游戏窗口
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.close();
                    // 打开游戏一窗口
                    new GameOneFX().start(new Stage());
                } else {
                    loginTips.setText("您输入的密码不正确哟，密码是8位数");
                    passwordField.setText("");
                    captchaField.setText("");
                    setCaptcha();
                }
            }
        }
    }

    /**
     * 显示提示弹窗
     */
    private void showTips() {
        // 创建提示弹窗
        Popup popup = new Popup();
        popup.setAutoHide(true); // 点击弹窗外部时自动隐藏

        TextField tipField = new TextField("账号就是图片上这个佬猥琐的全拼~");
        tipField.setEditable(false); // 不可编辑
        tipField.setFocusTraversable(false); // 取消焦点，取消默认全选
        tipField.setAlignment(Pos.CENTER); // 设置文本居中

        tipField.setBackground(new Background(new BackgroundFill(LIGHT_GRAY, new CornerRadii(3), null)));
        tipField.setBorder(new Border(new BorderStroke(
                Color.DARKGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(1)
        )));
        tipField.setPrefWidth(210);

        popup.getContent().add(tipField);

        // 计算弹窗位置
        Point2D point = tipsButton.localToScene(0.0, 0.0);
        double sceneX = point.getX() + tipsButton.getScene().getX() + tipsButton.getScene().getWindow().getX();
        double sceneY = point.getY() + tipsButton.getScene().getY() + tipsButton.getScene().getWindow().getY();

        popup.show(tipsButton, sceneX + 5, sceneY - 30);

        // 3秒后自动隐藏
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    @Override
    public void add(Player player) {
        players.add(player);
    }

    @Override
    public void delete(Player player) {
        players.remove(player);
    }

    @Override
    public int findPlayerId(String id) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
