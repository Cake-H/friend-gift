package friend_gift_JavaFX;

import friend_gift_JavaFX.gameThreeFX.GameThreeFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainPage extends Application {
    private Stage mainStage; // 全局窗口

    private GameOneFX currentGameOne;
    private GameTwoFX currentGameTwo;

    private GameThreeFX currentGameThree;

    private VideoFX currentVideo;

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        mainStage.setWidth(500);
        mainStage.setHeight(600);
        mainStage.setResizable(false); // 禁止缩放
        // 窗口关闭时停止所有定时器（防止内存泄漏）
        mainStage.setOnCloseRequest(e -> {
            if (currentGameTwo != null) {
                currentGameTwo.stopAllTimelines();
            }

            if(currentVideo != null) {
                currentVideo.stopAllPlayer();
            }
        });
        // showLogin(); // 加载登录界面
        // showGameOne();
        showGameThree();
        mainStage.show(); // 显示主窗口
    }

    public void showLogin() {
        mainStage.setWidth(500);
        mainStage.setHeight(600);
        mainStage.setTitle("登入幻想星球");
        LoginFX loginFX = new LoginFX(); // 创建LoginFX示例
        // 设置登录回调：登录成功后切换到GameOne
        loginFX.setOnLooginSuccessListener(this::showGameOne);
        // 创建登录场景
        Scene loginScence = new Scene(loginFX.getLoginRoot(), 500, 600);
        mainStage.getIcons().add(new Image("file:picture/login/bg.jpg"));

        // 主窗口设置登录场景
        mainStage.setScene(loginScence);
    }

    public void showGameOne() {
        mainStage.setWidth(603);
        mainStage.setHeight(680);
        mainStage.setTitle("华容道拼图");
        currentGameOne = new GameOneFX();

        currentGameOne.setOnVictoryListener(this::showGameTwo);

        currentGameOne.setOnReLoginListener(this::showLogin);

        Scene gameOneScene = new Scene(currentGameOne.getGameOneRoot(), 603, 680);

        mainStage.getIcons().add(new Image("file:picture\\gameOne\\pt_tb.png"));
        currentGameOne.setupKeyHandlers(gameOneScene); // 绑定按键事件

        mainStage.setScene(gameOneScene);
    }

    public void showGameTwo() {
        mainStage.setWidth(603);
        mainStage.setHeight(700);
        mainStage.setTitle("不要让小球掉落~");
        currentGameTwo = new GameTwoFX();

        currentGameTwo.setOnVictoryListener(this::showGameThree);

        Scene gameTwoScene = new Scene(currentGameTwo.getGameTwoRoot(), 603, 700);

        mainStage.getIcons().add(new Image("file:picture\\gameTwo\\tq_tb.png"));
        currentGameTwo.setupKeyEvents(gameTwoScene);

        mainStage.setScene(gameTwoScene);
    }

    public void showGameThree() {
        mainStage.setWidth(800);
        mainStage.setHeight(530);
        mainStage.setTitle("球球归洞咯~");
        currentGameThree = new GameThreeFX();

        currentGameThree.setOnvictoryListener(this::showVideo);

        Scene gameThreeScene = new Scene(currentGameThree.getGameThreeRoot(), 800, 500);
        currentGameThree.setupKeyEvents(gameThreeScene);

        mainStage.setScene(gameThreeScene);
    }

    public void showVideo() {
        mainStage.setWidth(800);
        mainStage.setHeight(600);
        mainStage.setTitle("现在是幻想时间~");
        currentVideo = new VideoFX(mainStage);

        Scene videoScene = new Scene(currentVideo.getVideoFXRoot(), 800, 600);

        mainStage.setScene(videoScene);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
