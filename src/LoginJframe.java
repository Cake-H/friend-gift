import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginJframe extends JFrame implements ActionListener, Database {
    // 颜色定义
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    // 组件定义
    JButton login = new JButton("登录");
    JButton register = new JButton("注册");

    JButton tips = new JButton();

    String id = "";
    String password = "";
    String captcha = "";

    JTextField idField = new JTextField();
    JPasswordField passwordField = new JPasswordField(); // 改为密码框
    JTextField captchaField = new JTextField();

    JButton captchaButton = new JButton();
    Player player = new Player("zhouqixuan", "20041201");
    List<Player> players = new ArrayList<>(); // 添加players列表

    JLabel loginTips = new JLabel(); // 设置是否登录成功的提示
    JLabel titleLabel = new JLabel("拼图游戏"); // 标题

    // 登录界面设计
    public LoginJframe() {
        initJframe(); // 创建窗口
        setCaptcha(); // 生成验证码
        initButton(); // 创建按钮
        initTextField(); // 添加文本框
        initImage(); // 添加图片

        players.add(player);

        this.setVisible(true);
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
        // 设置验证码按钮
        captchaButton.setBounds(330, 320, 100, 40);
        captchaButton.setText(captcha);
        captchaButton.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 18));
        captchaButton.setForeground(new Color(r.nextInt(100), r.nextInt(100), r.nextInt(255)));
        captchaButton.setBackground(LIGHT_GRAY);
        captchaButton.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        captchaButton.addActionListener(this);
        // 添加点击更换验证码的提示
        captchaButton.setToolTipText("点击更换验证码");
        this.getContentPane().add(captchaButton);
    }

    private void initJframe() {
        this.setUndecorated(false);
        this.setSize(500, 600);
        this.setTitle("登入幻想星球");
        // 设置界面置顶
        this.setAlwaysOnTop(true);
        // 设置界面至正中央
        this.setLocationRelativeTo(null);
        // 设置游戏的关闭模式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // 取消默认的居中放置，按照XY轴的形式添加组件
        this.setLayout(null);
        // 设置背景色
        this.getContentPane().setBackground(Color.WHITE);
    }

    private void initImage() {
//        // 顶部装饰面板
//        JPanel topPanel = new JPanel();
//        topPanel.setBounds(0, 0, 500, 150);
//        topPanel.setBackground(PRIMARY_COLOR);
//        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 50));
//
//        // 设置标题
//        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
//        titleLabel.setForeground(Color.WHITE);
//        topPanel.add(titleLabel);
//
//        this.getContentPane().add(topPanel);

        //添加背景图片
        ImageIcon bg = new ImageIcon("picture\\login\\bg.jpg");
        JLabel jLabel = new JLabel(bg);
        jLabel.setBounds(0, 0, 500, 600);
        this.getContentPane().add(jLabel);
    }

    private void initTextField() {
        JLabel idLabel = new JLabel("账号：");
        JLabel passwordLabel = new JLabel("密码：");
        JLabel captchaLabel = new JLabel("验证码：");

        // 设置标签样式
        Font labelFont = new Font("微软雅黑", Font.BOLD, 18);
        idLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        captchaLabel.setFont(labelFont);
        idLabel.setForeground(TEXT_COLOR);
        passwordLabel.setForeground(TEXT_COLOR);
        captchaLabel.setForeground(TEXT_COLOR);

        // 设置标签位置
        idLabel.setBounds(100, 200, 80, 40);
        passwordLabel.setBounds(100, 260, 80, 40);
        captchaLabel.setBounds(100, 320, 80, 40);

        // 设置提示信息样式
        loginTips.setBounds(160, 370, 250, 30);
        loginTips.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginTips.setForeground(ERROR_COLOR);

        this.getContentPane().add(idLabel);
        this.getContentPane().add(passwordLabel);
        this.getContentPane().add(captchaLabel);
        this.getContentPane().add(loginTips);

        // 设置文本框样式
        Font fieldFont = new Font("微软雅黑", Font.BOLD, 16);
        idField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        captchaField.setFont(fieldFont);

        // 设置文本框边框和内边距
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        captchaField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 设置文本框位置
        idField.setBounds(170, 200, 220, 40);
        passwordField.setBounds(170, 260, 220, 40);
        captchaField.setBounds(170, 320, 150, 40);

        this.getContentPane().add(idField);
        this.getContentPane().add(passwordField);
        this.getContentPane().add(captchaField);
    }

    private void initButton() {
        // 登录按钮设置
        login.setBounds(180, 410, 110, 45);
        styleButton(login, PRIMARY_COLOR, Color.WHITE);
        login.addActionListener(this);
        this.getContentPane().add(login);

        // 提示按钮设置
        tips.setBounds(200, 400, 10, 10);
        tips.setBackground(TEXT_COLOR);
        tips.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 0));
        tips.addActionListener(this);
        this.getContentPane().add(tips);

//        // 注册按钮设置
//        register.setBounds(270, 430, 110, 45);
//        styleButton(register, Color.WHITE, PRIMARY_COLOR);
//        register.addActionListener(this);
//        this.getContentPane().add(register);
    }

    // 按钮样式设置方法
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        button.setFocusPainted(false);

        // 添加鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void setTipsTextArea(JButton tips) {
        // 创建提示内容组件（类似文本框的样式）
        JTextField tipComponent = new JTextField("账号就是图片上这个佬猥琐的全拼~");
        tipComponent.setEditable(false); // 设置为不可编辑
        tipComponent.setBackground(Color.LIGHT_GRAY);
        tipComponent.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        tipComponent.setPreferredSize(new Dimension(200, 30));

        // 获取按钮在屏幕上的位置
        Point location = tips.getLocationOnScreen();

        // 创建PopupFactory
        PopupFactory popupFactory = PopupFactory.getSharedInstance();

        // 创建Popup
        Popup popup = popupFactory.getPopup(
                tips,
                tipComponent,
                location.x + 10,
                location.y - 10
        );
        popup.show();

        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.hide();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == login) {
            id = idField.getText().trim();
            int indexId = findPlayerId(id);
            password = new String(passwordField.getPassword()); // 从密码框获取密码

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
                System.out.println("请输入验证码");
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
                        this.dispose();
                        new GameOneJframe();
                    } else {
                        loginTips.setText("您输入的密码不正确哟，密码是8位数");
                        passwordField.setText("");
                        captchaField.setText("");
                        setCaptcha();
                    }
                }
            }
        } else if (obj == register) {
            this.dispose();
            new RegisterJframe();
        } else if (obj == captchaButton) {
            setCaptcha();
        } else if (obj == tips) {
            setTipsTextArea(tips);
        }
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
        new LoginJframe();
    }
}