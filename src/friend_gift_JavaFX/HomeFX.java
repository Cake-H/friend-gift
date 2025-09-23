package friend_gift_JavaFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.awt.*;

public class HomeFX extends Application {
    private MediaPlayer mediaPlayer; // 视频播放器核心
    private Slider slider; // 视频进度条


    private Button lastButton = new Button(); // 进入最终章

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);




        BorderPane root = new BorderPane(); // 根分区

        Scene scene = new Scene(root, 800, 600); // 创建窗口，并载入根分区

        primaryStage.setScene(scene); // 设置窗口
        primaryStage.show(); // 可视化
    }


    private void initStage(Stage primaryStage) {
        primaryStage.setTitle("现在是幻想时间~");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(true); // 窗口可缩放
        primaryStage.centerOnScreen(); // 窗口居中

    }

    public static void main(String[] args) {
        launch(args);
    }
}
