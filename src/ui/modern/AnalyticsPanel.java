package ui.modern;

import model.Employee;
import service.EmployeeService;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计报表面板
 * 部门统计、薪资柱状图、入职趋势、月度报表
 */
public class AnalyticsPanel extends JPanel {

    private final EmployeeService service;
    private final LogService logService;
    private JPanel contentArea;
    private JLabel titleLabel;

    public AnalyticsPanel(EmployeeService service, LogService logService) {
        this.service = service;
        this.logService = logService;
        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(Theme.PANEL_PADDING, Theme.PANEL_PADDING,
                Theme.PANEL_PADDING, Theme.PANEL_PADDING));

        // 顶部标题
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        titlePanel.add(iconLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("统计报表");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        textPanel.add(titleLabel);

        JLabel subtitle = new JLabel("数据统计与可视化图表");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        textPanel.add(subtitle);

        titlePanel.add(textPanel);
        header.add(titlePanel, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // 内容区域
        contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        add(contentArea, BorderLayout.CENTER);
    }

    /**
     * 显示部门统计（饼图）
     */
    public void showDeptStats() {
        titleLabel.setText("部门统计");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel chartTitle = new JLabel("🏢 部门人数占比");
        chartTitle.setFont(Theme.FONT_H2);
        chartTitle.setForeground(Theme.TEXT_PRIMARY);
        topPanel.add(chartTitle, BorderLayout.WEST);
        card.add(topPanel, BorderLayout.NORTH);

        // 饼图
        Map<String, Long> deptStats = service.getDeptStats();
        long total = deptStats.values().stream().mapToLong(Long::longValue).sum();

        // 饼图 + 图例左右布局
        JPanel chartWrapper = new JPanel(new BorderLayout(10, 0));
        chartWrapper.setOpaque(false);

        JPanel chartPanel = new DeptPieChart(deptStats);
        chartWrapper.add(chartPanel, BorderLayout.CENTER);

        // 图例 - 右侧竖向排列
        JPanel legendPanel = new JPanel();
        legendPanel.setOpaque(false);
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        Color[] legendColors = {
            new Color(0x4E, 0x9A, 0xF1), // 技术部 - 蓝
            new Color(0xF1, 0xC9, 0x4E), // 市场部 - 黄
            new Color(0x4E, 0xF1, 0xA0), // 人事部 - 绿
            new Color(0xF1, 0x7B, 0x4E), // 运营部 - 橙
            new Color(0xC4, 0x4E, 0xF1), // 管理层 - 紫
        };

        int idx = 0;
        for (Map.Entry<String, Long> entry : deptStats.entrySet()) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            item.setOpaque(false);
            item.setAlignmentX(Component.LEFT_ALIGNMENT);

            // 彩色方块
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(14, 14));
            colorBox.setBackground(legendColors[idx % legendColors.length]);
            item.add(colorBox);

            // 图例文字：部门名 + 人数 + 百分比
            double pct = 100.0 * entry.getValue() / total;
            String legendText = entry.getKey() + "  " + entry.getValue() + "人  " + String.format("%.0f", pct) + "%";
            JLabel label = new JLabel(legendText);
            label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            label.setForeground(Theme.TEXT_SECONDARY);
            item.add(label);

            legendPanel.add(item);
            legendPanel.add(Box.createVerticalStrut(10)); // 行间距
            idx++;
        }

        chartWrapper.add(legendPanel, BorderLayout.EAST);

        card.add(chartWrapper, BorderLayout.CENTER);

        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示薪资柱状图
     */
    public void showBarChart() {
        titleLabel.setText("薪资柱状图");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel chartTitle = new JLabel("📊 薪资分布柱状图");
        chartTitle.setFont(Theme.FONT_H2);
        chartTitle.setForeground(Theme.TEXT_PRIMARY);
        chartTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        card.add(chartTitle, BorderLayout.NORTH);

        List<Employee> employees = service.getAllEmployees();
        JPanel chartPanel = new SalaryBarChart(employees);
        card.add(chartPanel, BorderLayout.CENTER);

        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示入职趋势
     */
    public void showHireTrend() {
        titleLabel.setText("入职趋势");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel chartTitle = new JLabel("📈 入职趋势");
        chartTitle.setFont(Theme.FONT_H2);
        chartTitle.setForeground(Theme.TEXT_PRIMARY);
        chartTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        card.add(chartTitle, BorderLayout.NORTH);

        List<Employee> employees = service.getAllEmployees();
        JPanel chartPanel = new HireTrendChart(employees);
        card.add(chartPanel, BorderLayout.CENTER);

        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示月度报表
     */
    public void showReport() {
        titleLabel.setText("月度报表");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel reportTitle = new JLabel("📄 月度报表");
        reportTitle.setFont(Theme.FONT_H2);
        reportTitle.setForeground(Theme.TEXT_PRIMARY);
        reportTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(reportTitle);
        content.add(Box.createVerticalStrut(20));

        List<Employee> all = service.getAllEmployees();

        if (all.isEmpty()) {
            JLabel empty = new JLabel("暂无员工数据");
            empty.setForeground(Theme.TEXT_MUTED);
            empty.setFont(Theme.FONT_H2);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(empty);
        } else {
            // 生成报表
            String report = service.generateMonthlyReport();
            JTextArea reportArea = new JTextArea(report);
            reportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
            reportArea.setForeground(Theme.TEXT_PRIMARY);
            reportArea.setBackground(Theme.BG_CARD);
            reportArea.setEditable(false);
            reportArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            reportArea.setLineWrap(true);
            reportArea.setWrapStyleWord(true);

            JScrollPane scroll = Theme.createScrollPane(reportArea);
            scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            content.add(scroll);
        }

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ==================== 部门饼图（优化版） ====================
    private static class DeptPieChart extends JPanel {
        private final Map<String, Long> data;
        // 高对比度配色
        private static final Color[] COLORS = {
            new Color(0x4E, 0x9A, 0xF1), // 技术部 - 蓝
            new Color(0xF1, 0xC9, 0x4E), // 市场部 - 黄
            new Color(0x4E, 0xF1, 0xA0), // 人事部 - 绿
            new Color(0xF1, 0x7B, 0x4E), // 运营部 - 橙
            new Color(0xC4, 0x4E, 0xF1), // 管理层 - 紫
        };

        // 鼠标交互状态
        private int hoveredIndex = -1;
        private static final int EXPLODE_OFFSET = 10;

        DeptPieChart(Map<String, Long> data) {
            this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(500, 350));
            // 鼠标移动监听实现悬停突出
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    int oldHover = hoveredIndex;
                    hoveredIndex = getHoveredSector(e.getX(), e.getY());
                    if (oldHover != hoveredIndex) {
                        repaint();
                    }
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (hoveredIndex != -1) {
                        hoveredIndex = -1;
                        repaint();
                    }
                }
            });
        }

        /**
         * 计算鼠标位置对应的扇形索引
         */
        private int getHoveredSector(int mx, int my) {
            long total = data.values().stream().mapToLong(Long::longValue).sum();
            if (total == 0) return -1;

            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            // 计算鼠标相对于圆心的距离和角度
            double dx = mx - cx;
            double dy = my - cy;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > diameter / 2.0 + EXPLODE_OFFSET) return -1;

            double mouseAngle = Math.toDegrees(Math.atan2(dy, dx));
            if (mouseAngle < 0) mouseAngle += 360;

            double startAngle = 0;
            int idx = 0;
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                double angle = 360.0 * entry.getValue() / total;
                double endAngle = startAngle + angle;
                if (mouseAngle >= startAngle && mouseAngle < endAngle) {
                    return idx;
                }
                startAngle = endAngle;
                idx++;
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (data.isEmpty()) {
                g2d.setColor(Theme.TEXT_MUTED);
                g2d.setFont(Theme.FONT_BODY);
                String msg = "暂无数据";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                g2d.dispose();
                return;
            }

            long total = data.values().stream().mapToLong(Long::longValue).sum();
            if (total == 0) { g2d.dispose(); return; }

            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int x = cx - diameter / 2;
            int y = cy - diameter / 2;

            // 收集扇形数据用于标签防重叠
            List<SectorInfo> sectors = new ArrayList<>();
            double startAngle = 0;
            int colorIdx = 0;

            for (Map.Entry<String, Long> entry : data.entrySet()) {
                double angle = 360.0 * entry.getValue() / total;
                Color color = COLORS[colorIdx % COLORS.length];
                sectors.add(new SectorInfo(entry.getKey(), entry.getValue(), total,
                        startAngle, angle, color, colorIdx));
                startAngle += angle;
                colorIdx++;
            }

            // 第一步：绘制扇形
            for (int i = 0; i < sectors.size(); i++) {
                SectorInfo si = sectors.get(i);
                boolean isHovered = (i == hoveredIndex);

                // 计算偏移
                int offsetX = 0, offsetY = 0;
                if (isHovered) {
                    double midRad = Math.toRadians(si.startAngle + si.angle / 2);
                    offsetX = (int) (EXPLODE_OFFSET * Math.cos(midRad));
                    offsetY = (int) (EXPLODE_OFFSET * Math.sin(midRad));
                }

                // 绘制扇形
                g2d.setColor(si.color);
                g2d.fillArc(x + offsetX, y + offsetY, diameter, diameter,
                        (int) si.startAngle, (int) Math.max(si.angle, 0.5));

                // 绘制扇形之间的白色间隔线（2像素）
                g2d.setColor(new Color(0x1E, 0x1E, 0x1E)); // 背景色作为间隔
                g2d.setStroke(new BasicStroke(2));
                double lineAngle = Math.toRadians(si.startAngle);
                int innerR = diameter / 2 - 1;
                int outerR = diameter / 2 + 1;
                int lx1 = cx + offsetX + (int) (innerR * Math.cos(lineAngle));
                int ly1 = cy + offsetY + (int) (innerR * Math.sin(lineAngle));
                int lx2 = cx + offsetX + (int) (outerR * Math.cos(lineAngle));
                int ly2 = cy + offsetY + (int) (outerR * Math.sin(lineAngle));
                g2d.drawLine(lx1, ly1, lx2, ly2);
            }

            // 绘制最后一个扇形的结束分隔线
            if (!sectors.isEmpty()) {
                SectorInfo last = sectors.get(sectors.size() - 1);
                boolean isHovered = (sectors.size() - 1 == hoveredIndex);
                int offsetX = 0, offsetY = 0;
                if (isHovered) {
                    double midRad = Math.toRadians(last.startAngle + last.angle / 2);
                    offsetX = (int) (EXPLODE_OFFSET * Math.cos(midRad));
                    offsetY = (int) (EXPLODE_OFFSET * Math.sin(midRad));
                }
                double endAngleRad = Math.toRadians(last.startAngle + last.angle);
                int innerR = diameter / 2 - 1;
                int outerR = diameter / 2 + 1;
                int lx1 = cx + offsetX + (int) (innerR * Math.cos(endAngleRad));
                int ly1 = cy + offsetY + (int) (innerR * Math.sin(endAngleRad));
                int lx2 = cx + offsetX + (int) (outerR * Math.cos(endAngleRad));
                int ly2 = cy + offsetY + (int) (outerR * Math.sin(endAngleRad));
                g2d.setColor(new Color(0x1E, 0x1E, 0x1E));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(lx1, ly1, lx2, ly2);
            }

            // 第二步：绘制中心文字 "共X人"
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 16));
            g2d.setColor(Theme.TEXT_PRIMARY);
            String centerText = "共" + total + "人";
            FontMetrics fmCenter = g2d.getFontMetrics();
            g2d.drawString(centerText,
                    cx - fmCenter.stringWidth(centerText) / 2,
                    cy + fmCenter.getAscent() / 3);

            // 第三步：绘制标签（外侧 + 引导线）
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int labelLineLength = 20; // 引导线水平段长度
            int labelGap = 8;         // 标签与引导线端点间距

            // 收集所有标签位置用于防重叠
            List<LabelPosition> labelPositions = new ArrayList<>();

            for (int i = 0; i < sectors.size(); i++) {
                SectorInfo si = sectors.get(i);
                // 占比小于5%不在饼图上显示标签
                if (si.percentage < 5.0) continue;

                double midRad = Math.toRadians(si.startAngle + si.angle / 2);
                int labelRadius = diameter / 2 + 8;

                // 引导线起点（扇形边缘）
                int lineStartX = cx + (int) (labelRadius * Math.cos(midRad));
                int lineStartY = cy + (int) (labelRadius * Math.sin(midRad));

                // 引导线折点（向外延伸）
                int lineBendX = cx + (int) ((labelRadius + 15) * Math.cos(midRad));
                int lineBendY = cy + (int) ((labelRadius + 15) * Math.sin(midRad));

                // 标签文字
                String labelText = si.name + " " + si.value + "人 " + String.format("%.0f", si.percentage) + "%";
                int textWidth = fm.stringWidth(labelText);

                // 判断标签在左侧还是右侧
                boolean isRight = Math.cos(midRad) >= 0;
                int labelX, labelY, lineEndX;
                int lineEndY = lineBendY;

                if (isRight) {
                    lineEndX = lineBendX + labelLineLength;
                    labelX = lineEndX + labelGap;
                } else {
                    lineEndX = lineBendX - labelLineLength;
                    labelX = lineEndX - labelGap - textWidth;
                }
                labelY = lineBendY;

                // 防重叠处理
                labelY = adjustLabelY(labelY, labelPositions, isRight, textWidth, fm);

                // 记录标签位置
                labelPositions.add(new LabelPosition(labelX, labelY, textWidth, fm.getHeight(), isRight));

                // 绘制引导线（使用扇形颜色）
                g2d.setColor(si.color);
                g2d.setStroke(new BasicStroke(1.5f));
                // 从扇形边缘到折点
                g2d.drawLine(lineStartX, lineStartY, lineBendX, lineBendY);
                // 从折点到水平端点
                g2d.drawLine(lineBendX, lineBendY, lineEndX, lineEndY);

                // 绘制标签文字（白色）
                g2d.setColor(Color.WHITE);
                g2d.drawString(labelText, labelX, labelY + fm.getAscent() / 2);
            }

            g2d.dispose();
        }

        /**
         * 标签防重叠：检测重叠并自动上下错开
         */
        private int adjustLabelY(int proposedY, List<LabelPosition> existing, boolean isRight, int textWidth, FontMetrics fm) {
            int y = proposedY;
            int labelHeight = fm.getHeight() + 4; // 加上间距
            int maxAttempts = 50;
            int attempt = 0;

            while (attempt < maxAttempts) {
                boolean overlap = false;
                for (LabelPosition lp : existing) {
                    // 检查是否在同一侧
                    if (lp.isRight != isRight) continue;
                    // 检查垂直重叠
                    if (Math.abs(lp.y - y) < (lp.height + labelHeight) / 2) {
                        // 检查水平是否有重叠可能
                        if (isRight) {
                            // 右侧：检查水平范围是否有重叠
                            if (Math.abs(lp.x - (y == proposedY ? proposedY : 0)) < 200) {
                                overlap = true;
                                break;
                            }
                        } else {
                            if (Math.abs(lp.x - (y == proposedY ? proposedY : 0)) < 200) {
                                overlap = true;
                                break;
                            }
                        }
                    }
                }
                if (!overlap) break;
                // 上下交替错开
                if (attempt % 2 == 0) {
                    y = proposedY + (attempt / 2 + 1) * labelHeight;
                } else {
                    y = proposedY - (attempt / 2 + 1) * labelHeight;
                }
                attempt++;
            }
            return y;
        }

        /**
         * 扇形信息
         */
        private static class SectorInfo {
            final String name;
            final long value;
            final double percentage;
            final double startAngle;
            final double angle;
            final Color color;
            final int index;

            SectorInfo(String name, long value, long total, double startAngle, double angle, Color color, int index) {
                this.name = name;
                this.value = value;
                this.percentage = 100.0 * value / total;
                this.startAngle = startAngle;
                this.angle = angle;
                this.color = color;
                this.index = index;
            }
        }

        /**
         * 标签位置信息（用于防重叠检测）
         */
        private static class LabelPosition {
            final int x;
            final int y;
            final int width;
            final int height;
            final boolean isRight;

            LabelPosition(int x, int y, int width, int height, boolean isRight) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.isRight = isRight;
            }
        }
    }

    // ==================== 薪资柱状图 ====================
    private static class SalaryBarChart extends JPanel {
        private final List<Employee> employees;

        SalaryBarChart(List<Employee> employees) {
            this.employees = employees;
            setOpaque(false);
            setPreferredSize(new Dimension(400, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (employees.isEmpty()) {
                g2d.setColor(Theme.TEXT_MUTED);
                g2d.setFont(Theme.FONT_BODY);
                String msg = "暂无数据";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                g2d.dispose();
                return;
            }

            // 按薪资范围分组
            double[] ranges = {0, 5000, 10000, 15000, 20000, 30000, 50000};
            String[] labels = {"0-5K", "5-10K", "10-15K", "15-20K", "20-30K", "30K+"};
            int[] counts = new int[labels.length];

            for (Employee emp : employees) {
                double salary = emp.getSalary();
                for (int i = 0; i < ranges.length - 1; i++) {
                    if (salary >= ranges[i] && salary < ranges[i + 1]) {
                        counts[i]++;
                        break;
                    }
                    if (i == ranges.length - 2 && salary >= ranges[i + 1]) {
                        counts[i]++;
                    }
                }
            }

            int maxCount = Arrays.stream(counts).max().orElse(1);
            int padding = 50;
            int chartWidth = getWidth() - 2 * padding;
            int chartHeight = getHeight() - 60;
            int barWidth = chartWidth / labels.length - 12;

            for (int i = 0; i < labels.length; i++) {
                int barHeight = (int) ((double) counts[i] / maxCount * (chartHeight - 20));
                int barX = padding + i * (chartWidth / labels.length) + 6;
                int barY = chartHeight - barHeight;

                // 柱体渐变
                GradientPaint gradient = new GradientPaint(
                        barX, barY, Theme.ACCENT_PRIMARY,
                        barX, chartHeight, Theme.ACCENT_SECONDARY
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(barX, barY, barWidth, barHeight, 6, 6);

                // 数值
                g2d.setColor(Theme.TEXT_PRIMARY);
                g2d.setFont(Theme.FONT_SMALL);
                String countStr = String.valueOf(counts[i]);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(countStr, barX + (barWidth - fm.stringWidth(countStr)) / 2, barY - 5);

                // 标签
                g2d.setColor(Theme.TEXT_SECONDARY);
                g2d.setFont(new Font("微软雅黑", Font.PLAIN, 10));
                fm = g2d.getFontMetrics();
                g2d.drawString(labels[i], barX + (barWidth - fm.stringWidth(labels[i])) / 2, chartHeight + 15);
            }

            g2d.dispose();
        }
    }

    // ==================== 入职趋势折线图 ====================
    private static class HireTrendChart extends JPanel {
        private final List<Employee> employees;

        HireTrendChart(List<Employee> employees) {
            this.employees = employees;
            setOpaque(false);
            setPreferredSize(new Dimension(400, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (employees.isEmpty()) {
                g2d.setColor(Theme.TEXT_MUTED);
                g2d.setFont(Theme.FONT_BODY);
                String msg = "暂无数据";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                g2d.dispose();
                return;
            }

            // 按年份统计入职人数
            Map<Integer, Long> byYear = employees.stream()
                    .filter(e -> e.getHireDate() != null)
                    .collect(Collectors.groupingBy(
                            e -> e.getHireDate().getYear(),
                            TreeMap::new,
                            Collectors.counting()
                    ));

            if (byYear.isEmpty()) {
                g2d.dispose();
                return;
            }

            int padding = 50;
            int chartWidth = getWidth() - 2 * padding;
            int chartHeight = getHeight() - 60;

            int[] years = byYear.keySet().stream().mapToInt(Integer::intValue).toArray();
            long maxCount = byYear.values().stream().mapToLong(Long::longValue).max().orElse(1);

            // 绘制折线
            Path2D.Double path = new Path2D.Double();
            int[] xPoints = new int[years.length];
            int[] yPoints = new int[years.length];

            for (int i = 0; i < years.length; i++) {
                int x = padding + (int) ((double) i / (years.length - 1) * chartWidth);
                int y = chartHeight - (int) ((double) byYear.get(years[i]) / maxCount * (chartHeight - 20));
                xPoints[i] = x;
                yPoints[i] = y;

                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.curveTo(
                            (xPoints[i - 1] + x) / 2, yPoints[i - 1],
                            (xPoints[i - 1] + x) / 2, y,
                            x, y
                    );
                }
            }

            // 填充区域
            g2d.setColor(new Color(99, 102, 241, 30));
            Path2D.Double fillPath = new Path2D.Double(path);
            fillPath.lineTo(xPoints[years.length - 1], chartHeight);
            fillPath.lineTo(xPoints[0], chartHeight);
            fillPath.closePath();
            g2d.fill(fillPath);

            // 绘制线条
            g2d.setColor(Theme.ACCENT_PRIMARY);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(path);

            // 绘制数据点
            for (int i = 0; i < years.length; i++) {
                g2d.setColor(Theme.ACCENT_PRIMARY);
                g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                g2d.setColor(Theme.BG_CARD);
                g2d.fillOval(xPoints[i] - 2, yPoints[i] - 2, 4, 4);

                // 年份标签
                g2d.setColor(Theme.TEXT_SECONDARY);
                g2d.setFont(new Font("微软雅黑", Font.PLAIN, 10));
                FontMetrics fm = g2d.getFontMetrics();
                String yearStr = String.valueOf(years[i]);
                g2d.drawString(yearStr, xPoints[i] - fm.stringWidth(yearStr) / 2, chartHeight + 15);

                // 数值标签
                g2d.setColor(Theme.TEXT_PRIMARY);
                g2d.setFont(Theme.FONT_SMALL);
                String countStr = String.valueOf(byYear.get(years[i]));
                fm = g2d.getFontMetrics();
                g2d.drawString(countStr, xPoints[i] - fm.stringWidth(countStr) / 2, yPoints[i] - 10);
            }

            g2d.dispose();
        }
    }
}
