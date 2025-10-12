package friend_gift_JavaFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;

public class VideoFX {
    private MediaPlayer mediaPlayer; // 视频播放器核心
    private Slider progressSlider;   // 视频进度条
    private BorderPane root;         // 根布局（提前初始化）
    private Button lastButton = new Button("进入最终章"); // 进入最终章按钮（补充文本，避免空按钮）

    public VideoFX(Stage stage) {
        root = new BorderPane();

        // 视频路径（建议用File+URI处理，避免手动拼接"file:"可能出错）
        File videoFile = new File("C:/Users/86135/Desktop/e1bf07dafcb402b1f2d5bb03694c8567.mp4");
        initVideo(videoFile, stage);

    }

    public BorderPane getVideoFXRoot() {
        return root;
    }

//    private void initStage(Stage primaryStage) {
//        primaryStage.setTitle("现在是幻想时间~");
//        primaryStage.setWidth(800);
//        primaryStage.setHeight(600);
//        primaryStage.setResizable(true);
//        primaryStage.centerOnScreen();
//    }

    private void initVideo(File videoFile, Stage stage) {
        try {
            // 1. 先检查视频文件（排除文件不存在/不是MP4的问题）
            if (!videoFile.exists()) {
                throw new Exception("视频文件不存在！路径：" + videoFile.getAbsolutePath());
            }
            if (!videoFile.getName().toLowerCase().endsWith(".mp4")) {
                throw new Exception("文件不是MP4格式！JavaFX仅支持H.264编码的MP4");
            }

            // 2. 用URI自动编码路径（避免手动写"file:"可能遗漏的问题）
            URI videoUri = videoFile.toURI();
            Media media = new Media(videoUri.toString());

            // 3. 创建播放器（核心步骤）
            mediaPlayer = new MediaPlayer(media);

            // 4. 视频显示容器配置
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true); // 保持宽高比，避免拉伸
            // 视频尺寸跟随窗口（减去边距，避免贴边）
            mediaView.fitWidthProperty().bind(stage.widthProperty().subtract(40));
            mediaView.fitHeightProperty().bind(stage.heightProperty().subtract(100));

            // 5. 创建控制栏（含修复后的进度条）
            HBox controlBar = createControlBar();

            // 6. 组装布局（添加"进入最终章"按钮到顶部）
//            root.setTop(lastButton);        // 顶部放最终章按钮
            root.setCenter(mediaView);      // 中间放视频
            root.setBottom(controlBar);     // 底部放控制栏
            // 组件对齐与边距（优化布局）
//            BorderPane.setAlignment(lastButton, Pos.CENTER);
//            BorderPane.setMargin(lastButton, new Insets(10));
            BorderPane.setAlignment(controlBar, Pos.CENTER);
            BorderPane.setMargin(controlBar, new Insets(10));
        } catch (Exception e) {
            // 【关键修复2：加载失败时给root添加错误提示，避免空指针】
            String errorMsg = "视频加载失败：" + e.getMessage() + "\n\n" +
                    "请务必检查：\n" +
                    "1. 视频是否为【H.264编码的MP4】（用PotPlayer看属性→详细信息→视频编码）\n" +
                    "2. 视频文件未损坏（手动双击能正常播放）\n" +
                    "3. JavaFX SDK是【Windows x64版本】（匹配你的64位JDK）";

            Label errorLabel = new Label(errorMsg);
            errorLabel.setWrapText(true); // 自动换行
            errorLabel.setPadding(new Insets(20));
            errorLabel.setStyle("-fx-text-fill: #cc0000; -fx-font-size: 14px;"); // 红色错误文字
            root.setCenter(errorLabel); // 错误信息显示在窗口中间

            // 打印错误日志，方便调试
            System.err.println(errorMsg);
            e.printStackTrace();
        }
    }

    public void stopAllPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    /**
     * 创建控制栏（修复进度条监听器，确保进度随视频更新）
     */
    private HBox createControlBar() {
        HBox controlBar = new HBox(20);
        controlBar.setPadding(new Insets(10));
        controlBar.setAlignment(Pos.CENTER);

        // 1. 播放/暂停按钮
        Button playBtn = new Button("播放");
        playBtn.setOnAction(e -> {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playBtn.setText("播放");
            } else {
                mediaPlayer.play();
                playBtn.setText("暂停");
            }
        });

        // 2. 停止按钮
        Button stopBtn = new Button("停止");
        stopBtn.setOnAction(e -> {
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.ZERO); // 回到开头
            playBtn.setText("播放");
        });

        // 3. 进度条（【关键修复3：用currentTimeProperty监听实时进度】）
        progressSlider = new Slider();
        progressSlider.setPrefWidth(500);
        progressSlider.setMin(0);

        // 3.1 视频加载完成后，设置进度条最大值（总时长）
        mediaPlayer.totalDurationProperty().addListener((obs, oldVal, newVal) -> {
            progressSlider.setMax(newVal.toSeconds());
        });

        // 3.2 实时更新进度条（监听当前播放时间，而非总时长）
        mediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
            progressSlider.setValue(newVal.toSeconds());
        });

        // 3.3 拖动进度条定位视频
        progressSlider.setOnMouseDragged(e -> {
            double seekTime = progressSlider.getValue();
            mediaPlayer.seek(Duration.seconds(seekTime));
        });

        // 添加组件到控制栏
        controlBar.getChildren().addAll(playBtn, stopBtn, progressSlider);
        return controlBar;
    }

}