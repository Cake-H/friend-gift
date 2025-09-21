import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
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
    private Button loginButton = new Button("登录");
    private Button registerButton = new Button("注册");
    private Button tipsButton = new Button();
    private Button captchaButton = new Button();

    private String captcha = "";

    private TextField idField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField captchaField = new TextField();

    private Player player = new Player("zhouqixuan", "20041201");
    private List<Player> players = new ArrayList<>();

    private Label loginTips = new Label();
    private Label titleLabel = new Label("幻想星球");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("登入幻想星球");
        primaryStage.setWidth(500);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        // 初始化数据
        players.add(player);

        // 创建主面板
        StackPane root = new StackPane();

        // 添加背景图片
        try {
            Image bgImage = new Image("file:picture/login/bg.jpg");
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(500);
            bgView.setFitHeight(600);
            root.getChildren().add(bgView);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // 如果背景图片加载失败，使用默认背景色
            root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        }

        // 创建登录表单面板
        VBox loginPanel = new VBox(20);
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setPadding(new Insets(150, 50, 50, 50));
        loginPanel.setOpacity(0.9);

        // 设置标题
        titleLabel.setFont(new Font("微软雅黑", 32));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(0, 0, 30, 0));

        // 创建表单网格面板
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(20);
        formGrid.setAlignment(Pos.CENTER);

        // 账号输入
        Label idLabel = new Label("账号：");
        styleLabel(idLabel);
        formGrid.add(idLabel, 0, 0);

        styleTextField(idField);
        formGrid.add(idField, 1, 0);

        // 密码输入
        Label passwordLabel = new Label("密码：");
        styleLabel(passwordLabel);
        formGrid.add(passwordLabel, 0, 1);

        styleTextField(passwordField);
        formGrid.add(passwordField, 1, 1);

        // 验证码输入
        Label captchaLabel = new Label("验证码：");
        styleLabel(captchaLabel);
        formGrid.add(captchaLabel, 0, 2);

        HBox captchaBox = new HBox(10);
        styleTextField(captchaField);
        captchaField.setPrefWidth(150);

        // 生成验证码
        setCaptcha();
        styleCaptchaButton();

        captchaBox.getChildren().addAll(captchaField, captchaButton);
        formGrid.add(captchaBox, 1, 2);

        // 提示信息
        loginTips.setFont(new Font("微软雅黑", 14));
        loginTips.setTextFill(ERROR_COLOR);
        formGrid.add(loginTips, 1, 3);

        // 提示按钮
        styleTipsButton();

        formGrid.add(tipsButton, 1, 3);

        // 登录按钮
        styleButton(loginButton, PRIMARY_COLOR, Color.WHITE);
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(loginButton);

        // 添加所有组件到登录面板
        loginPanel.getChildren().addAll(titleLabel, formGrid, buttonBox);

        root.getChildren().add(loginPanel);

        // 创建场景
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // 显示窗口
        primaryStage.show();

        // 设置事件监听
        setEventListeners(primaryStage);
    }

    private void styleLabel(Label label) {
        label.setFont(new Font("微软雅黑", 18));
        label.setTextFill(TEXT_COLOR);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setPrefWidth(80);
    }

    private void styleTextField(TextField textField) {
        textField.setFont(new Font("微软雅黑", 16));
        textField.setPrefSize(220, 40);
        textField.setBorder(new Border(new BorderStroke(
                new Color(200/255.0, 200/255.0, 200/255.0, 1),
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(1)
        )));
        textField.setPadding(new Insets(5, 10, 5, 10));
    }

    private void styleButton(Button button, Color bgColor, Color textColor) {
        button.setFont(new Font("微软雅黑", 16));
        button.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(5), null)));
        button.setTextFill(textColor);
        button.setPrefSize(110, 45);
        button.setBorder(new Border(new BorderStroke(
                PRIMARY_COLOR,
                BorderStrokeStyle.SOLID,
                new CornerRadii(5),
                new BorderWidths(2)
        )));
        button.setFocusTraversable(false);

        // 鼠标悬停效果
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(SECONDARY_COLOR, new CornerRadii(5), null)));
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(5), null)));
        });
    }

    private void styleCaptchaButton() {
        captchaButton.setPrefSize(100, 40);
        captchaButton.setFont(new Font("Arial", 18));
        captchaButton.setBackground(new Background(new BackgroundFill(LIGHT_GRAY, new CornerRadii(3), null)));
        captchaButton.setBorder(new Border(new BorderStroke(
                PRIMARY_COLOR,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(1)
        )));
        captchaButton.setTooltip(new Tooltip("点击更换验证码"));
    }

    private void styleTipsButton() {
        tipsButton.setPrefSize(5, 5);
        tipsButton.setBackground(new Background(new BackgroundFill(TEXT_COLOR, new CornerRadii(2), null)));
        tipsButton.setBorder(new Border(new BorderStroke(
                TEXT_COLOR,
                BorderStrokeStyle.SOLID,
                new CornerRadii(2),
                new BorderWidths(0)
        )));
        tipsButton.setTooltip(new Tooltip("查看提示"));
    }

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
        captchaButton.setTextFill(new Color(
                r.nextDouble() * 0.4,  // 0-0.4的红色分量
                r.nextDouble() * 0.4,  // 0-0.4的绿色分量
                r.nextDouble(),        // 0-1的蓝色分量
                1
        ));
    }

    private void setEventListeners(Stage primaryStage) {
        // 登录按钮事件
        loginButton.setOnAction(e -> handleLogin());

        // 验证码按钮事件
        captchaButton.setOnAction(e -> setCaptcha());

        // 提示按钮事件
        tipsButton.setOnAction(e -> showTips());
    }

    private void handleLogin() {
        String id = idField.getText().trim();
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
                    // 这里应该打开游戏窗口
                    // new GameOneFX().start(new Stage());
                } else {
                    loginTips.setText("您输入的密码不正确哟，密码是8位数");
                    passwordField.setText("");
                    captchaField.setText("");
                    setCaptcha();
                }
            }
        }
    }

    private void showTips() {
        // 创建提示弹窗
        Popup popup = new Popup();
        popup.setAutoHide(true);

        TextField tipField = new TextField("账号就是图片上这个佬猥琐的全拼~");
        tipField.setEditable(false);
        tipField.setBackground(new Background(new BackgroundFill(LIGHT_GRAY, new CornerRadii(3), null)));
        tipField.setBorder(new Border(new BorderStroke(
                Color.DARKGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(1)
        )));
        tipField.setPrefWidth(200);

        popup.getContent().add(tipField);

        // 计算弹窗位置
        Point2D point = tipsButton.localToScene(0.0, 0.0);
        double sceneX = point.getX() + tipsButton.getScene().getX() + tipsButton.getScene().getWindow().getX();
        double sceneY = point.getY() + tipsButton.getScene().getY() + tipsButton.getScene().getWindow().getY();

        popup.show(tipsButton, sceneX + 10, sceneY - 40);

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
