import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class GameOneJframe extends JFrame implements KeyListener, ActionListener {
    //游戏界面设计

    int[][] data = new int[4][4]; //创建二维数组，加载图片时根据它的顺序加载
    //记录空白图片的位置
    int x = 0;
    int y = 0;

    //定义一个变量，记录当前展示图片的路径
    String path = "picture\\life\\";

    Random r = new Random();
    int whichImage = r.nextInt(3);//随机图片

    //定义一个二维数组，存储正确的数据
    int[][] win = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };

    int step = 0; // 定义变量用来统计步数

    //创建选项下面的条目对象
    JMenuItem landscape = new JMenuItem("风景");
    JMenuItem animal = new JMenuItem("动物");
    JMenuItem life = new JMenuItem("生活");
    JMenuItem replayItem = new JMenuItem("重新开始");
    JMenuItem reLoginItem = new JMenuItem("重新登录");
    JMenuItem closeItem = new JMenuItem("关闭游戏");

    JMenuItem accountItem = new JMenuItem("公众号");

    public GameOneJframe() {
        initJFrame();//初始化代码

        initJMenuBar();//初始化菜单

        initData(); //初始化数据

        initImage();//初始化图片（根据打乱后的结果加载图片）

        this.setVisible(true);
    }

    private void initData() {
        int[] tempArr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        switch (whichImage) {
            case 0:
                path = "picture\\life\\";
                break;
            case 1:
                path = "picture\\life\\";
                break;
            case 2:
                path = "picture\\life\\";
                break;
        }

        for (int i = 0; i < tempArr.length; i++) { //打乱数字顺序
            int index = r.nextInt(tempArr.length);
            int temp = tempArr[i];
            tempArr[i] = tempArr[index];
            tempArr[index] = temp;
        }

        for (int i = 0; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                x = i / 4;
                y = i % 4;
            }
            data[i / 4][i % 4] = tempArr[i];
        }
    }

    public void initJFrame() {
        this.setSize(603, 680);
        this.setTitle("拼图单机版 v1.0");
        //设置界面置顶
        this.setAlwaysOnTop(false);
        //设置界面至正中央
        this.setLocationRelativeTo(null);
        //设置游戏的关闭模式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //取消默认的居中放置，只有取消了才会按照XY轴的的形式添加组件
        this.setLayout(null);
        //给整个界面添加键盘监听事件
        this.addKeyListener(this);
    }

    public void initJMenuBar() {
        //设置菜单
        JMenuBar jMenuBar = new JMenuBar();

        //创建菜单上面的两个选项的对象
        JMenu functionJMenu = new JMenu("功能");
        JMenu aboutJMenu = new JMenu("关于我们");
        JMenu changeImage = new JMenu("更换图片");
        //将每个选项下面的条目对象添加到选项下
        functionJMenu.add(changeImage);
        changeImage.add(landscape);
        changeImage.add(animal);
        changeImage.add(life);

        functionJMenu.add(replayItem);
        functionJMenu.add(reLoginItem);
        functionJMenu.add(closeItem);

        aboutJMenu.add(accountItem);

        //给条目绑定事件
        landscape.addActionListener(this);
        animal.addActionListener(this);
        life.addActionListener(this);

        replayItem.addActionListener(this);
        reLoginItem.addActionListener(this);
        closeItem.addActionListener(this);

        accountItem.addActionListener(this);

        //将菜单里面的两个选项添加到菜单
        jMenuBar.add(functionJMenu);
        jMenuBar.add(aboutJMenu);

        //将菜单放在窗口中
        this.setJMenuBar(jMenuBar);
    }

    public void initVictoryJFrame() {
        JFrame victoryJFrame = new JFrame("恭喜通关！");
        victoryJFrame.setSize(100, 50);
        victoryJFrame.setLocationRelativeTo(null);
        victoryJFrame.setAlwaysOnTop(true);
        victoryJFrame.add(new TextField("恭喜通过本关！点击确定进入下一章~"));
        victoryJFrame.add(new Button("确定"));

        victoryJFrame.setVisible(true);

        victoryJFrame.addKeyListener(this);

    }
    public void initImage() {
        //清空原本已经出现的所有图片
        this.getContentPane().removeAll();

        if (victory()) {
            JLabel winJLabel = new JLabel(new ImageIcon("picture\\win.png"));
            winJLabel.setBounds(203, 253, 200, 76);
            this.getContentPane().add(winJLabel);
            initVictoryJFrame(); // 弹出通关弹窗

        }

        JLabel stepCount = new JLabel("步数：" + step);
        stepCount.setBounds(50, 30, 100, 20);
        this.getContentPane().add(stepCount);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String filename = path + data[i][j] + ".jpg";
                initImage_each(filename, 105 * j + 83, 105 * i + 134);
            }
        }

        //添加背景图片
        ImageIcon bg = new ImageIcon("picture\\game\\bg.jpg");
        JLabel jLabel = new JLabel(bg);
        jLabel.setBounds(32, 30, 520, 580);
        this.getContentPane().add(jLabel);

        //刷新一下界面
        this.getContentPane().repaint();
    }

    public void initImage_each(String filename, int x, int y) {
        //创建一个图片ImageIcon的对象
        ImageIcon image = new ImageIcon(filename);
        //创建一个JLabel的对象（管理容器）
        JLabel jLabel = new JLabel(image);
        //指定图片位置
        jLabel.setBounds(x, y, 105, 105);
        //给图片添加边框
        jLabel.setBorder(new BevelBorder(1));
        //0：让图片凸起来
        //1：让图片凹下去
        //把管理容器添加到界面中
        this.getContentPane().add(jLabel);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (victory()) {
            return;
        }
        int code = e.getKeyCode();
        if (code == 65) {
            this.getContentPane().removeAll();

            JLabel picture = new JLabel(new ImageIcon(path + "完整.jpg"));
            picture.setBounds(83, 134, 420, 420);
            this.getContentPane().add(picture);
            //添加背景图片
            ImageIcon bg = new ImageIcon("picture\\game\\bg.jpg");
            JLabel jLabel = new JLabel(bg);
            jLabel.setBounds(32, 30, 520, 580);
            this.getContentPane().add(jLabel);
            //刷新界面
            this.getContentPane().repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //判断游戏是否胜利，如果胜利直接结束
        if (victory()) {
            return;
        }

        //对上，下，左，右进行判断
        //左：37 上：38 右：39 下：40
        int code = e.getKeyCode(); //找到按键对于的按键值
//         System.out.println(code);
        if (code == 37) {
            if (y == 3) {
                //表示空白方块已经在最下方
                return;
            }
            data[x][y] = data[x][y + 1];
            data[x][y + 1] = 0;
            y++;

            step++;//每移动一次，计数器自增一次
            //调用方法按照最新数据加载图片
            initImage();

        } else if (code == 38) {
            if (x == 3) {
                //表示空白方块已经在最下方
                return;
            }
            data[x][y] = data[x + 1][y];
            data[x + 1][y] = 0;
            x++;
            step++;
            //调用方法按照最新数据加载图片
            initImage();
        } else if (code == 39) {
            if (y == 0) {
                //表示空白方块已经在最下方
                return;
            }
            data[x][y] = data[x][y - 1];
            data[x][y - 1] = 0;
            y--;
            step++;
            //调用方法按照最新数据加载图片
            initImage();
        } else if (code == 40) {
            if (x == 0) {
                //表示空白方块已经在最下方
                return;
            }
            data[x][y] = data[x - 1][y];
            data[x - 1][y] = 0;
            x--;
            step++;
            //调用方法按照最新数据加载图片
            initImage();
        } else if (code == 65) {
            initImage();
        } else if (code == 87) {
            data = new int[][]{
                    {1, 2, 3, 4},
                    {5, 6, 7, 8},
                    {9, 10, 11, 12},
                    {13, 14, 15, 0}
            };
            initImage();
        }
    }

    //判断是否胜利
    public boolean victory() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] != win[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //获取当前被点击的条目对象
        Object obj = e.getSource();
        if (obj == replayItem) {
            step = 0;
            initData();
            initImage();
        } else if (obj == reLoginItem) {
            this.dispose();
            new LoginJframe();
        } else if (obj == closeItem) {
            System.exit(0);//直接关闭虚拟机
        } else if (obj == accountItem) {
            //创建一个弹窗对象
            JDialog jDialog = new JDialog();
            //创建一个管理图片的容器对象
            JLabel jLabel = new JLabel("小H的公众号");

            jLabel.setBounds(0, 0, 258, 258);

            jDialog.getContentPane().add(jLabel);
            //给弹框设置大小
            jDialog.setSize(344, 344);
            jDialog.setAlwaysOnTop(true);
            //让弹框居中
            jDialog.setLocationRelativeTo(null);
            //弹框不关闭则无法操作下面的界面
            jDialog.setModal(true);
            jDialog.setVisible(true);

        } else if (obj == landscape) {

        } else if (obj == animal) {

        } else if (obj == life) {

        }
    }
}
