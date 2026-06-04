package ui.modern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 现代化登录窗口
 * 单独的登录窗口，有用户名和密码输入框
 * 密码框用 JPasswordField 隐藏输入
 * 最多3次机会，错误显示红色提示
 */
public class ModernLoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JLabel attemptsLabel;
    private JButton loginBtn;
    private int attempts = 0;
    private static final int MAX_ATTEMPTS = 3;
    private boolean loginSuccess = false;
    private Runnable onLoginSuccess;

    public ModernLoginView() {
        this(null, null);
    }

    public ModernLoginView(JFrame parent, Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        initComponents();
    }

    private void initComponents() {
        setTitle("员工管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        // 主面板
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Theme.BG_DARK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                // 顶部渐变条
                GradientPaint gp = new GradientPaint(0, 0, Theme.ACCENT_PRIMARY, getWidth(), 0, Theme.ACCENT_SECONDARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), 4);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Theme.BG_DARK);
        setContentPane(mainPanel);

        // 关闭按钮
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 10));

        JLabel titleLabel = new JLabel("员工管理系统");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        closeBtn.setForeground(Theme.TEXT_MUTED);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(Theme.ACCENT_RED);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(Theme.TEXT_MUTED);
            }
        });

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeBtn, BorderLayout.EAST);

        // 中间内容
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // 图标
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setForeground(Theme.ACCENT_PRIMARY);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 欢迎文字
        JLabel welcomeLabel = new JLabel("欢迎登录");
        welcomeLabel.setFont(Theme.FONT_H1);
        welcomeLabel.setForeground(Theme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel subLabel = new JLabel("请输入您的账号和密码");
        subLabel.setFont(Theme.FONT_BODY);
        subLabel.setForeground(Theme.TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // 用户名
        JLabel userLabel = new JLabel("用户名");
        userLabel.setFont(Theme.FONT_BODY);
        userLabel.setForeground(Theme.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        usernameField = Theme.createTextField();
        usernameField.setMaximumSize(new Dimension(300, 38));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setText("admin");

        // 密码
        JLabel passLabel = new JLabel("密码");
        passLabel.setFont(Theme.FONT_BODY);
        passLabel.setForeground(Theme.TEXT_SECONDARY);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 5, 0));

        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.INPUT_RADIUS, Theme.INPUT_RADIUS);
                if (hasFocus()) {
                    g2d.setColor(Theme.BORDER_FOCUS);
                    g2d.setStroke(new BasicStroke(1.5f));
                } else {
                    g2d.setColor(Theme.BORDER);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.INPUT_RADIUS, Theme.INPUT_RADIUS);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        passwordField.setFont(Theme.FONT_BODY);
        passwordField.setForeground(Theme.TEXT_PRIMARY);
        passwordField.setCaretColor(Theme.ACCENT_PRIMARY);
        passwordField.setBackground(Theme.BG_INPUT);
        passwordField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        passwordField.setOpaque(false);
        passwordField.setMaximumSize(new Dimension(300, 38));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setEchoChar('●');
        passwordField.addActionListener(e -> doLogin());

        // 错误提示
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // 剩余次数
        attemptsLabel = new JLabel(" ");
        attemptsLabel.setFont(Theme.FONT_SMALL);
        attemptsLabel.setForeground(Theme.TEXT_MUTED);
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 登录按钮
        loginBtn = Theme.createButton("登  录");
        loginBtn.setMaximumSize(new Dimension(300, 42));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        loginBtn.addActionListener(e -> doLogin());

        centerPanel.add(iconLabel);
        centerPanel.add(welcomeLabel);
        centerPanel.add(subLabel);
        centerPanel.add(userLabel);
        centerPanel.add(usernameField);
        centerPanel.add(passLabel);
        centerPanel.add(passwordField);
        centerPanel.add(errorLabel);
        centerPanel.add(attemptsLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(loginBtn);

        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 回车键登录
        getRootPane().setDefaultButton(loginBtn);
    }

    /**
     * 执行登录验证
     */
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("⚠ 用户名和密码不能为空");
            return;
        }

        // 读取密码
        String correctPassword = "admin123";
        File configFile = new File("config.txt");
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile, StandardCharsets.UTF_8))) {
                String pwd = reader.readLine();
                if (pwd != null && !pwd.trim().isEmpty()) {
                    correctPassword = pwd.trim();
                }
            } catch (IOException ignored) {
            }
        }

        if (password.equals(correctPassword)) {
            loginSuccess = true;
            dispose();
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            attempts++;
            int remaining = MAX_ATTEMPTS - attempts;
            errorLabel.setText("✕ 密码错误！");
            errorLabel.setForeground(Theme.ERROR);
            passwordField.setText("");
            passwordField.requestFocus();

            if (remaining > 0) {
                attemptsLabel.setText("剩余尝试次数：" + remaining + " 次");
                attemptsLabel.setForeground(Theme.WARNING);
            } else {
                attemptsLabel.setText("登录失败次数过多，程序将退出！");
                attemptsLabel.setForeground(Theme.ERROR);
                Timer timer = new Timer(2000, e -> System.exit(0));
                timer.setRepeats(false);
                timer.start();
                loginBtn.setEnabled(false);
            }
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    /**
     * 显示登录窗口并等待登录结果
     */
    public static boolean showLogin() {
        ModernLoginView loginView = new ModernLoginView();
        loginView.setVisible(true);
        // 等待登录窗口关闭
        synchronized (loginView) {
            while (loginView.isVisible()) {
                try {
                    loginView.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        return loginView.isLoginSuccess();
    }

    @Override
    public void dispose() {
        synchronized (this) {
            notifyAll();
        }
        super.dispose();
    }
}
