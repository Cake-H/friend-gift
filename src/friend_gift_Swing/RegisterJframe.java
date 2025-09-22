package friend_gift_Swing;

import friend_gift_JavaFX.LoginFX;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class RegisterJframe extends JFrame implements ActionListener, Database_Jframe {
    JLabel registerTips = new JLabel();

    JTextField idField = new JTextField();
    JTextField passwordField = new JTextField();
    JTextField passwordAgainField = new JTextField();
    JTextField captchaField = new JTextField();

    String captcha = "";

    JButton captchaButton = new JButton();
    JButton register = new JButton("注册");
    //注册界面设计
    public RegisterJframe(){
        initJframe();

        initTextField();

        setCaptcha();

        initButton();
    }

    public void initJframe(){
        this.setSize(488,500);
        this.setVisible(true);
        this.setTitle("拼图 注册");
        //设置界面置顶
        this.setAlwaysOnTop(true);
        //设置界面至正中央
        this.setLocationRelativeTo(null);
        //设置游戏的关闭模式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //取消默认的居中放置，只有取消了才会按照XY轴的的形式添加组件
        this.setLayout(null);
    }

    private void initTextField() {
        JLabel idLabel = new JLabel("请设置您的账户名：");
        JLabel passwordLabel = new JLabel("请设置您的登录密码：");
        JLabel passwordAgainLabel = new JLabel("请再次确认您的密码：");
        JLabel captchaLabel = new JLabel("验证码：");

        idLabel.setBounds(50, 90, 150, 60);
        passwordLabel.setBounds(50, 140, 150, 60);
        passwordAgainLabel.setBounds(50, 190, 150, 60);
        captchaLabel.setBounds(125, 240, 100, 60);

        registerTips.setBounds(70, 360, 200, 60);

        this.getContentPane().add(idLabel);
        this.getContentPane().add(passwordLabel);
        this.getContentPane().add(passwordAgainLabel);
        this.getContentPane().add(captchaLabel);
        this.getContentPane().add(registerTips);

        //添加文本框
        idField.setBounds(180, 100, 210, 40);
        passwordField.setBounds(180, 150, 210, 40);
        passwordAgainField.setBounds(180, 200, 210, 40);
        captchaField.setBounds(180, 250, 140, 40);

        this.getContentPane().add(idField);
        this.getContentPane().add(passwordField);
        this.getContentPane().add(passwordAgainField);
        this.getContentPane().add(captchaField);
    }

    private void setCaptcha() {
        captcha = "";
        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            int type = r.nextInt(3);
            switch (type) {
                case 0 -> captcha += r.nextInt(10);
                case 1 -> captcha += (char) (r.nextInt(26) + 65);
                case 2 -> captcha += (char) (r.nextInt(26) + 97);
            }
        }
        //设置验证码按钮
        captchaButton.setBounds(320, 250, 70, 40);
        captchaButton.setText(captcha);
        this.getContentPane().add(captchaButton);
    }

    private void initButton() {
        //注册按钮设置
        register.setBounds(160, 320, 120, 40);
        register.addActionListener(this);
        this.getContentPane().add(register);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o == register) {
            String id = idField.getText();
            String password = passwordField.getText();
            String passwordAgain = passwordAgainField.getText();
            String captchaIn = captchaField.getText();
            if(id.isEmpty()) {
                registerTips.setText("账户名为空");
            } else if(password.isEmpty()) {
                registerTips.setText("密码为空");
            } else if(passwordAgain.isEmpty()) {
                registerTips.setText("请再次确认您的密码");
            } else if(captchaIn.isEmpty()) {
                registerTips.setText("请输入验证码");
            } else if (!captchaIn.equalsIgnoreCase(captcha)) {
                registerTips.setText("验证码错误，请重新输入");
                captchaField.setText("");
                setCaptcha();
            } else {
                int index = findPlayerId(id);
                if(index != -1) {
                    registerTips.setText("当前用户名已存在");
                    captchaField.setText("");
                    setCaptcha();
                } else if(!password.equals(passwordAgain)) {
                    registerTips.setText("两次输入的密码不同，请确认您的密码");
                    captchaField.setText("");
                    setCaptcha();
                } else {
                    Database_Jframe.players.add(new Player_Jframe(id, password));
                    JDialog jDialog = new JDialog();
                    JLabel jLabel = new JLabel("注册成功！快登录游戏吧！");
                    jLabel.setBounds(50, 20, 200, 60);
                    jDialog.getContentPane().add(jLabel);
                    jDialog.setSize(300, 300);
                    jDialog.setLocationRelativeTo(null);
                    jDialog.setAlwaysOnTop(true);
                    jDialog.setVisible(true);
                    jDialog.setLayout(null);
                    JButton jButton = new JButton("点击跳转登录页面");
                    jButton.setBounds(50, 120, 80, 40);
                    jDialog.getContentPane().add(jButton);
                    jButton.addActionListener(e1 -> {
                        jDialog.setVisible(false);
                        new LoginFX();
                    });
                    this.dispose();
                }
            }
        }
    }

    @Override
    public void add(Player_Jframe playerJframe) {
        Database_Jframe.players.add(playerJframe);
    }

    @Override
    public void delete(Player_Jframe playerJframe) {
        Database_Jframe.players.remove(playerJframe);
    }

    @Override
    public int findPlayerId(String id) {
        for (int i = 0; i < Database_Jframe.players.size(); i++) {
            if (Database_Jframe.players.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
