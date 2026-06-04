package ui.modern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * 全局主题配置 - 深色主题
 * 背景色 #1E1E1E，前景色 #FFFFFF
 * 所有字体统一用"微软雅黑" 13号
 */
public class Theme {

    // ==================== 颜色 ====================
    public static final Color BG_DARK = new Color(0x1E, 0x1E, 0x1E);
    public static final Color BG_SIDEBAR = new Color(0x25, 0x25, 0x25);
    public static final Color BG_MAIN = new Color(0x1E, 0x1E, 0x1E);
    public static final Color BG_CARD = new Color(0x2D, 0x2D, 0x2D);
    public static final Color BG_CARD_HOVER = new Color(0x3A, 0x3A, 0x3A);
    public static final Color BG_INPUT = new Color(0x33, 0x33, 0x33);
    public static final Color BG_INPUT_FOCUS = new Color(0x3D, 0x3D, 0x3D);
    public static final Color BG_TABLE_ROW = new Color(0x28, 0x28, 0x28);
    public static final Color BG_TABLE_ALT = new Color(0x30, 0x30, 0x30);

    // 强调色
    public static final Color ACCENT_PRIMARY = new Color(0x00, 0x78, 0xD4);
    public static final Color ACCENT_HOVER = new Color(0x1E, 0x90, 0xFF);
    public static final Color ACCENT_GLOW = new Color(0x00, 0x78, 0xD4, 60);
    public static final Color ACCENT_SECONDARY = new Color(0x7C, 0x4D, 0xFF);
    public static final Color ACCENT_CYAN = new Color(0x00, 0xBC, 0xC1);
    public static final Color ACCENT_GREEN = new Color(0x4C, 0xAF, 0x50);
    public static final Color ACCENT_ORANGE = new Color(0xFF, 0x98, 0x00);
    public static final Color ACCENT_RED = new Color(0xF4, 0x43, 0x36);
    public static final Color ACCENT_PINK = new Color(0xE9, 0x1E, 0x63);

    // 文字色
    public static final Color TEXT_PRIMARY = new Color(0xFF, 0xFF, 0xFF);
    public static final Color TEXT_SECONDARY = new Color(0xB0, 0xB0, 0xB0);
    public static final Color TEXT_MUTED = new Color(0x80, 0x80, 0x80);
    public static final Color TEXT_WHITE = new Color(0xFF, 0xFF, 0xFF);

    // 边框色
    public static final Color BORDER = new Color(0x3C, 0x3C, 0x3C);
    public static final Color BORDER_FOCUS = new Color(0x00, 0x78, 0xD4, 120);
    public static final Color BORDER_LIGHT = new Color(0x33, 0x33, 0x33);

    // 状态色
    public static final Color SUCCESS = new Color(0x4C, 0xAF, 0x50);
    public static final Color WARNING = new Color(0xFF, 0xC1, 0x07);
    public static final Color ERROR = new Color(0xF4, 0x43, 0x36);
    public static final Color INFO = new Color(0x21, 0x96, 0xF3);

    // ==================== 字体 ====================
    public static final Font FONT_TITLE = new Font("微软雅黑", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("微软雅黑", Font.PLAIN, 13);
    public static final Font FONT_H1 = new Font("微软雅黑", Font.BOLD, 18);
    public static final Font FONT_H2 = new Font("微软雅黑", Font.BOLD, 15);
    public static final Font FONT_H3 = new Font("微软雅黑", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("微软雅黑", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("微软雅黑", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("微软雅黑", Font.BOLD, 13);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR = new Font("微软雅黑", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_SELECTED = new Font("微软雅黑", Font.BOLD, 13);
    public static final Font FONT_TABLE = new Font("微软雅黑", Font.PLAIN, 13);
    public static final Font FONT_TABLE_HEADER = new Font("微软雅黑", Font.BOLD, 13);

    // ==================== 尺寸 ====================
    public static final int SIDEBAR_WIDTH = 200;
    public static final int HEADER_HEIGHT = 50;
    public static final int CARD_RADIUS = 8;
    public static final int BUTTON_RADIUS = 6;
    public static final int INPUT_RADIUS = 6;
    public static final int PANEL_PADDING = 16;

    // ==================== 阴影 ====================
    public static final int SHADOW_SIZE = 10;

    /**
     * 应用全局 UI 样式
     */
    public static void applyGlobalStyles() {
        UIManager.put("ToolTip.background", BG_CARD);
        UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(BORDER, 1));
        UIManager.put("ToolTip.font", FONT_SMALL);
    }

    /**
     * 创建圆角面板
     */
    public static JPanel createCard() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_RADIUS, CARD_RADIUS);
                g2d.setColor(BORDER);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
                g2d.dispose();
            }
        };
    }

    /**
     * 创建现代化按钮
     */
    public static JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isPressed();
                if (pressed) {
                    g2d.setColor(ACCENT_PRIMARY.darker());
                } else if (hover) {
                    g2d.setColor(ACCENT_HOVER);
                } else {
                    g2d.setColor(ACCENT_PRIMARY);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_RADIUS, BUTTON_RADIUS);
                g2d.setColor(TEXT_WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * 创建次要按钮（带边框）
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isPressed();
                if (pressed) {
                    g2d.setColor(BG_CARD_HOVER);
                } else if (hover) {
                    g2d.setColor(BG_CARD_HOVER);
                } else {
                    g2d.setColor(BG_CARD);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_RADIUS, BUTTON_RADIUS);
                g2d.setColor(hover ? ACCENT_HOVER : BORDER);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BUTTON_RADIUS, BUTTON_RADIUS);
                g2d.setColor(hover ? ACCENT_HOVER : TEXT_PRIMARY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_PRIMARY);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * 创建危险按钮（红色）
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                if (hover) {
                    g2d.setColor(ACCENT_RED);
                } else {
                    g2d.setColor(new Color(0xC6, 0x28, 0x28));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_RADIUS, BUTTON_RADIUS);
                g2d.setColor(TEXT_WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * 创建现代化输入框
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), INPUT_RADIUS, INPUT_RADIUS);
                if (hasFocus()) {
                    g2d.setColor(BORDER_FOCUS);
                    g2d.setStroke(new BasicStroke(1.5f));
                } else {
                    g2d.setColor(BORDER);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, INPUT_RADIUS, INPUT_RADIUS);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        field.setOpaque(false);
        return field;
    }

    /**
     * 创建美化滚动面板
     */
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_MAIN);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.getViewport().setBorder(null);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0x3C, 0x3C, 0x3C);
                this.trackColor = BG_MAIN;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                return btn;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(thumbColor);
                g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                        thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);
                g2d.dispose();
            }
        });

        return scrollPane;
    }

    /**
     * 创建标签
     */
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /**
     * 创建标题标签
     */
    public static JLabel createTitle(String text) {
        return createLabel(text, FONT_H1, TEXT_PRIMARY);
    }
}
