package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * 现代化深色主题登录界面
 * 风格参考 Claude/ChatGPT 的简洁高级 UI
 */
public class LoginUI {

    private JFrame frame;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel progressPanel;
    private JPanel progressBar;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel successLabel;

    private Timer progressTimer;
    private int progressValue = 0;
    private volatile boolean loginSuccess = false;
    private volatile boolean loginComplete = false;

    // 颜色常量 - 深色主题
    private static final Color BG_DARK = new Color(18, 18, 24);
    private static final Color BG_CARD = new Color(28, 28, 38);
    private static final Color BG_INPUT = new Color(38, 38, 50);
    private static final Color BG_INPUT_FOCUS = new Color(45, 45, 60);
    private static final Color ACCENT_COLOR = new Color(99, 102, 241);
    private static final Color ACCENT_HOVER = new Color(129, 132, 255);
    private static final Color ACCENT_GLOW = new Color(99, 102, 241, 60);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 240);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 180);
    private static final Color TEXT_PLACEHOLDER = new Color(100, 100, 120);
    private static final Color BORDER_COLOR = new Color(50, 50, 65);
    private static final Color BORDER_FOCUS = new Color(99, 102, 241, 120);
    private static final Color PROGRESS_START = new Color(99, 102, 241);
    private static final Color PROGRESS_END = new Color(139, 92, 246);
    private static final Color SUCCESS_COLOR = new Color(52, 211, 153);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);

    // 字体
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SUCCESS = new Font("Segoe UI", Font.BOLD, 42);
    private static final Font FONT_SUCCESS_SUB = new Font("Segoe UI", Font.PLAIN, 18);

    // 回调接口 - 登录成功后调用
    private final Runnable onLoginSuccess;

    /**
     * 构造方法
     * @param onLoginSuccess 登录成功后的回调
     */
    public LoginUI(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    /**
     * 显示登录界面
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            frame = new JFrame("员工管理系统 - 登录");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(480, 620);
            frame.setLocationRelativeTo(null);
            frame.setUndecorated(true);
            frame.setBackground(BG_DARK);

            // 主面板
            mainPanel = createMainPanel();
            frame.setContentPane(mainPanel);

            // 支持拖动
            addDragSupport(mainPanel);

            // 键盘事件
            setupKeyboardEvents();

            frame.setVisible(true);
            frame.requestFocus();

            // 用户名输入框自动获取焦点
            SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
        });
    }

    /**
     * 关闭登录界面
     */
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * 创建主面板
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // 背景
                g2d.setColor(BG_DARK);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 装饰性光晕 - 左上角
                g2d.setColor(new Color(99, 102, 241, 20));
                g2d.fillOval(-100, -100, 400, 400);

                // 装饰性光晕 - 右下角
                g2d.setColor(new Color(139, 92, 246, 15));
                g2d.fillOval(getWidth() - 300, getHeight() - 300, 400, 400);

                g2d.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setBackground(BG_DARK);

        // 卡片面板
        JPanel card = createCardPanel();
        panel.add(card);

        return panel;
    }

    /**
     * 创建卡片面板
     */
    private JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 卡片背景
                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                // 边框
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);

                g2d.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(400, 480));
        card.setMaximumSize(new Dimension(400, 520));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // 关闭按钮
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setOpaque(false);
        closePanel.setMaximumSize(new Dimension(400, 30));
        JLabel closeBtn = new JLabel("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeBtn.setForeground(TEXT_SECONDARY);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setForeground(TEXT_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setForeground(TEXT_SECONDARY);
            }
        });
        closePanel.add(closeBtn);
        card.add(closePanel);

        card.add(Box.createVerticalStrut(10));

        // 标题
        titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(8));

        // 子标题
        subtitleLabel = new JLabel("请输入您的凭证以继续");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);

        card.add(Box.createVerticalStrut(35));

        // 用户名输入框
        usernameField = createStyledTextField("用户名");
        card.add(usernameField);

        card.add(Box.createVerticalStrut(16));

        // 密码输入框
        passwordField = createStyledPasswordField("密码");
        card.add(passwordField);

        card.add(Box.createVerticalStrut(28));

        // 登录按钮
        loginButton = createStyledButton();
        card.add(loginButton);

        card.add(Box.createVerticalStrut(16));

        // 进度条面板
        progressPanel = createProgressPanel();
        card.add(progressPanel);
        progressPanel.setVisible(false);

        card.add(Box.createVerticalStrut(8));

        // 状态标签
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(statusLabel);

        // 成功标签（初始隐藏）
        successLabel = createSuccessLabel();
        successLabel.setVisible(false);
        card.add(successLabel);

        return card;
    }

    /**
     * 创建统一样式的输入框
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 背景
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // 边框
                if (hasFocus()) {
                    g2d.setColor(BORDER_FOCUS);
                    g2d.setStroke(new BasicStroke(1.5f));
                } else {
                    g2d.setColor(BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_COLOR);
        field.setBackground(BG_INPUT);
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setOpaque(false);
        field.setMaximumSize(new Dimension(320, 46));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 占位符文本
        field.putClientProperty("placeholder", placeholder);

        // 焦点高亮动画
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                animateInputFocus(field, true);
            }
            @Override
            public void focusLost(FocusEvent e) {
                animateInputFocus(field, false);
            }
        });

        return field;
    }

    /**
     * 创建统一样式的密码输入框
     */
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                if (hasFocus()) {
                    g2d.setColor(BORDER_FOCUS);
                    g2d.setStroke(new BasicStroke(1.5f));
                } else {
                    g2d.setColor(BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_COLOR);
        field.setBackground(BG_INPUT);
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setOpaque(false);
        field.setMaximumSize(new Dimension(320, 46));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.putClientProperty("placeholder", placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                animateInputFocus(field, true);
            }
            @Override
            public void focusLost(FocusEvent e) {
                animateInputFocus(field, false);
            }
        });

        return field;
    }

    /**
     * 输入框焦点动画
     */
    private void animateInputFocus(JComponent field, boolean focused) {
        Color targetBg = focused ? BG_INPUT_FOCUS : BG_INPUT;
        Color targetBorder = focused ? BORDER_FOCUS : BORDER_COLOR;

        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            int steps = 0;
            final int totalSteps = 8;
            final int startR = field.getBackground().getRed();
            final int startG = field.getBackground().getGreen();
            final int startB = field.getBackground().getBlue();

            @Override
            public void actionPerformed(ActionEvent e) {
                steps++;
                float ratio = (float) steps / totalSteps;
                int r = (int) (startR + (targetBg.getRed() - startR) * ratio);
                int g = (int) (startG + (targetBg.getGreen() - startG) * ratio);
                int b = (int) (startB + (targetBg.getBlue() - startB) * ratio);
                field.setBackground(new Color(
                        Math.min(255, Math.max(0, r)),
                        Math.min(255, Math.max(0, g)),
                        Math.min(255, Math.max(0, b))
                ));
                field.repaint();
                if (steps >= totalSteps) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    /**
     * 创建登录按钮
     */
    private JButton createStyledButton() {
        JButton button = new JButton("登 录") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                // 按钮背景
                if (pressed) {
                    g2d.setColor(ACCENT_COLOR.darker());
                } else if (hover) {
                    g2d.setColor(ACCENT_HOVER);
                } else {
                    g2d.setColor(ACCENT_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // hover 发光效果
                if (hover && !pressed) {
                    g2d.setColor(ACCENT_GLOW);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 14, 14);
                }

                // 按钮文字
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(text, textX, textY);

                g2d.dispose();
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(320, 50));
        button.setMaximumSize(new Dimension(320, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // hover 效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        // 点击事件
        button.addActionListener(e -> performLogin());

        return button;
    }

    /**
     * 创建进度条面板
     */
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 不绘制背景
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(320, 30));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 进度条轨道
        progressBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 轨道背景
                g2d.setColor(new Color(40, 40, 55));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                // 进度条 - 渐变色
                if (progressValue > 0) {
                    int w = (int) (getWidth() * progressValue / 100.0);
                    GradientPaint gradient = new GradientPaint(
                            0, 0, PROGRESS_START,
                            w, 0, PROGRESS_END
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, w, getHeight(), 6, 6);

                    // 光泽效果
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillRoundRect(0, 0, w, getHeight() / 2, 6, 6);
                }

                g2d.dispose();
            }
        };
        progressBar.setOpaque(false);
        progressBar.setPreferredSize(new Dimension(320, 6));
        progressBar.setMaximumSize(new Dimension(320, 6));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(progressBar);

        return panel;
    }

    /**
     * 创建成功标签
     */
    private JLabel createSuccessLabel() {
        JLabel label = new JLabel("<html><div style='text-align: center;'>"
                + "<span style='font-size:48px; color:#34D399;'>✓</span><br>"
                + "<span style='font-size:24px; color:#E6E6F0;'>Login Success</span>"
                + "</div></html>", SwingConstants.CENTER);
        label.setFont(FONT_SUCCESS);
        label.setForeground(SUCCESS_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * 执行登录
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // 验证输入
        if (username.isEmpty()) {
            shakeField(usernameField);
            statusLabel.setForeground(ERROR_COLOR);
            statusLabel.setText("请输入用户名");
            return;
        }
        if (password.isEmpty()) {
            shakeField(passwordField);
            statusLabel.setForeground(ERROR_COLOR);
            statusLabel.setText("请输入密码");
            return;
        }

        // 验证密码（从 config.txt 读取）
        String storedPassword = readPasswordFromConfig();
        if (!password.equals(storedPassword)) {
            shakeField(passwordField);
            statusLabel.setForeground(ERROR_COLOR);
            statusLabel.setText("密码错误，请重试");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }

        // 登录成功，开始进度动画
        startLoginAnimation();
    }

    /**
     * 从配置文件读取密码
     */
    private String readPasswordFromConfig() {
        String password = "admin123";
        File configFile = new File("config.txt");
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"))) {
                String pwd = reader.readLine();
                if (pwd != null && !pwd.trim().isEmpty()) {
                    password = pwd.trim();
                }
            } catch (IOException e) {
                // 使用默认密码
            }
        }
        return password;
    }

    /**
     * 启动登录动画
     */
    private void startLoginAnimation() {
        // 禁用输入
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
        loginButton.setEnabled(false);
        loginButton.setText("登录中...");

        // 显示进度条
        progressPanel.setVisible(true);
        progressValue = 0;
        statusLabel.setForeground(TEXT_SECONDARY);

        // 强制重新计算布局并重绘，防止组件显示异常
        mainPanel.revalidate();
        mainPanel.repaint();

        // 进度条动画
        progressTimer = new Timer(25, null);
        progressTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressValue += 1 + (int)(Math.random() * 2);
                if (progressValue > 100) {
                    progressValue = 100;
                    ((Timer) e.getSource()).stop();
                    onLoginComplete();
                }
                progressBar.repaint();

                // 更新状态文字
                if (progressValue < 30) {
                    statusLabel.setText("正在验证身份...");
                } else if (progressValue < 60) {
                    statusLabel.setText("正在加载数据...");
                } else if (progressValue < 85) {
                    statusLabel.setText("正在初始化系统...");
                } else {
                    statusLabel.setText("即将进入系统...");
                }
            }
        });
        progressTimer.start();
    }

    /**
     * 登录完成
     */
    private void onLoginComplete() {
        loginComplete = true;

        // 隐藏输入控件
        usernameField.setVisible(false);
        passwordField.setVisible(false);
        loginButton.setVisible(false);
        progressPanel.setVisible(false);
        statusLabel.setVisible(false);
        titleLabel.setText("Login Success");
        titleLabel.setForeground(SUCCESS_COLOR);

        // 显示成功标签 - 淡入动画
        successLabel.setVisible(true);
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 强制重新计算布局并重绘，防止组件显示异常
        mainPanel.revalidate();
        mainPanel.repaint();

        // 淡入动画
        Timer fadeTimer = new Timer(15, null);
        final float[] opacity = {0.0f};
        fadeTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity[0] += 0.05f;
                if (opacity[0] >= 1.0f) {
                    opacity[0] = 1.0f;
                    ((Timer) e.getSource()).stop();
                    // 延迟后关闭登录界面，进入主程序
                    Timer closeTimer = new Timer(1200, evt -> {
                        closeAndProceed();
                    });
                    closeTimer.setRepeats(false);
                    closeTimer.start();
                }
                successLabel.setForeground(new Color(
                        SUCCESS_COLOR.getRed(),
                        SUCCESS_COLOR.getGreen(),
                        SUCCESS_COLOR.getBlue(),
                        (int)(opacity[0] * 255)
                ));
            }
        });
        fadeTimer.start();
    }

    /**
     * 关闭登录界面并进入主程序
     */
    private void closeAndProceed() {
        close();
        if (onLoginSuccess != null) {
            onLoginSuccess.run();
        }
    }

    /**
     * 输入框抖动效果（登录失败时）
     * 使用边框边距模拟抖动，避免直接 setLocation 与布局管理器冲突
     */
    private void shakeField(JComponent field) {
        final int[] count = {0};
        Timer timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                count[0]++;
                if (count[0] >= 8) {
                    ((Timer) e.getSource()).stop();
                    field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                    return;
                }
                int offset = count[0] % 2 == 0 ? 4 : -4;
                field.setBorder(BorderFactory.createEmptyBorder(12, 16 + offset, 12, 16 - offset));
            }
        });
        timer.start();
    }

    /**
     * 添加窗口拖动支持
     */
    private void addDragSupport(JPanel panel) {
        final Point[] dragOffset = {null};
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragOffset[0] = e.getPoint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dragOffset[0] = null;
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragOffset[0] != null) {
                    Point current = e.getLocationOnScreen();
                    frame.setLocation(current.x - dragOffset[0].x, current.y - dragOffset[0].y);
                }
            }
        });
    }

    /**
     * 设置键盘事件
     */
    private void setupKeyboardEvents() {
        // Enter 键登录
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Escape 键退出
        mainPanel.registerKeyboardAction(
                e -> System.exit(0),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
}
