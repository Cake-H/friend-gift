package friend_gift_Swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// 主界面
public class Home extends JFrame implements ActionListener {
    private Popup imagePopup; // 存储图片浮动窗口
    JButton button = new JButton("<html>点击进入<br>最终章</html>");

    public Home() {
        initJFrame(); // 设置界面
        initImage(); // 插入图片
        initText(); // 插入文本
        initButton(); // 插入按键
        this.setVisible(true);
    }

    private void initJFrame() {
        this.setSize(1000, 800);
        this.setTitle("现在是幻想时间~");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null); // 界面正中央
        this.setLayout(null);

        this.getContentPane().setBackground(Color.white);
    }

    private void initImage() {

    }

    private void initText() {

    }

    private void initButton() {
        button.setBackground(Color.yellow);
        button.setBounds(400, 300, 200, 200);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 30));

        this.getContentPane().add(button);
    }

    public static void main(String[] args) {
        new Home();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == button) {
            if (imagePopup != null) {
                imagePopup.hide();
            }

            try {
                ImageIcon imageIcon = new ImageIcon("");
                Image image = imageIcon.getImage().getScaledInstance(600, 800, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(image);

                JLabel imageJLabel = new JLabel(imageIcon);

                imageJLabel.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {

                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        int code = e.getKeyCode(); //找到按键对于的按键值
                        if (code == 27 && imagePopup != null) {
                            imagePopup.hide();
                            imagePopup = null;

                        }
                    }
                });

                Point location = this.getLocation();

                PopupFactory popupFactory = PopupFactory.getSharedInstance();
                imagePopup = popupFactory.getPopup(
                        button,
                        imageJLabel,
                        location.x,
                        location.y
                );

                imagePopup.show();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "图片加载失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
