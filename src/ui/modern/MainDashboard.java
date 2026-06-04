package ui.modern;

import model.Employee;
import service.EmployeeService;
import service.EmployeeServiceImpl;
import service.LogService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 主仪表盘窗口
 * 左侧侧边栏放功能按钮，按分组显示：员工管理、薪资管理、统计报表、系统功能
 * 右侧主区域显示操作内容和结果
 * 顶部显示系统标题和当前时间
 * 底部状态栏显示员工总数和当前操作提示
 */
public class MainDashboard extends JFrame {

    private final EmployeeService service;
    private final LogService logService;
    private JPanel contentPanel;
    private JLabel timeLabel;
    private JLabel statusLabel;
    private JLabel totalLabel;
    private JPanel sidebarPanel;
    private JButton activeButton;
    private Timer clockTimer;

    // 各功能面板
    private EmployeePanel employeePanel;
    private SalaryPanel salaryPanel;
    private AnalyticsPanel analyticsPanel;
    private SettingsPanel settingsPanel;

    public MainDashboard(EmployeeService service, LogService logService) {
        this.service = service;
        this.logService = logService;
        initComponents();
        startClock();
        showPanel("employee");
    }

    private void initComponents() {
        setTitle("员工管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);

        // 主布局
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BG_DARK);
        setContentPane(mainPanel);

        // ===== 顶部标题栏 =====
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // ===== 中间区域 =====
        JPanel centerArea = new JPanel(new BorderLayout());
        centerArea.setBackground(Theme.BG_DARK);

        // 左侧侧边栏
        sidebarPanel = createSidebar();
        centerArea.add(sidebarPanel, BorderLayout.WEST);

        // 右侧内容区
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.BG_MAIN);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        centerArea.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(centerArea, BorderLayout.CENTER);

        // ===== 底部状态栏 =====
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);
    }

    /**
     * 创建顶部标题栏
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_SIDEBAR);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        header.setPreferredSize(new Dimension(0, 55));

        // 左侧标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel titleLabel = new JLabel("员工管理系统");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // 右侧时间
        timeLabel = new JLabel();
        timeLabel.setFont(Theme.FONT_BODY);
        timeLabel.setForeground(Theme.TEXT_SECONDARY);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(timeLabel, BorderLayout.EAST);

        return header;
    }

    /**
     * 创建左侧侧边栏
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));

        // 分组标题样式
        Font groupFont = new Font("微软雅黑", Font.BOLD, 11);
        Color groupColor = Theme.TEXT_MUTED;

        // ===== 员工管理 =====
        sidebar.add(createGroupLabel("👥 员工管理", groupFont, groupColor));
        sidebar.add(createSidebarButton("📋 员工列表", "employee", true));
        sidebar.add(createSidebarButton("➕ 添加员工", "add", false));
        sidebar.add(createSidebarButton("🔍 查询员工", "search", false));
        sidebar.add(createSidebarButton("🔄 员工转岗", "transfer", false));
        sidebar.add(Box.createVerticalStrut(8));

        // ===== 薪资管理 =====
        sidebar.add(createGroupLabel("💰 薪资管理", groupFont, groupColor));
        sidebar.add(createSidebarButton("💵 计算薪资", "calc_salary", false));
        sidebar.add(createSidebarButton("📈 薪资排序", "salary_sort", false));
        sidebar.add(createSidebarButton("🔎 薪资筛选", "salary_filter", false));
        sidebar.add(createSidebarButton("🏆 薪资冠军", "champion", false));
        sidebar.add(createSidebarButton("📊 涨薪管理", "raise", false));
        sidebar.add(createSidebarButton("🏖 请假扣薪", "leave", false));
        sidebar.add(Box.createVerticalStrut(8));

        // ===== 统计报表 =====
        sidebar.add(createGroupLabel("📊 统计报表", groupFont, groupColor));
        sidebar.add(createSidebarButton("🏢 部门统计", "dept_stats", false));
        sidebar.add(createSidebarButton("📊 薪资柱状图", "bar_chart", false));
        sidebar.add(createSidebarButton("📈 入职趋势", "hire_trend", false));
        sidebar.add(createSidebarButton("📄 月度报表", "report", false));
        sidebar.add(Box.createVerticalStrut(8));

        // ===== 系统功能 =====
        sidebar.add(createGroupLabel("⚙ 系统功能", groupFont, groupColor));
        sidebar.add(createSidebarButton("📝 操作日志", "logs", false));
        sidebar.add(createSidebarButton("📤 导出CSV", "export", false));
        sidebar.add(createSidebarButton("📥 批量导入", "import", false));
        sidebar.add(createSidebarButton("💾 数据备份", "backup", false));
        sidebar.add(createSidebarButton("🔄 恢复备份", "restore", false));
        sidebar.add(createSidebarButton("🔑 修改密码", "password", false));

        // 弹性空间
        sidebar.add(Box.createVerticalGlue());

        // 退出按钮
        sidebar.add(Box.createVerticalStrut(8));
        JButton exitBtn = createSidebarButton("🚪 退出系统", "exit", false);
        exitBtn.setForeground(Theme.ACCENT_RED);
        sidebar.add(exitBtn);
        sidebar.add(Box.createVerticalStrut(12));

        return sidebar;
    }

    /**
     * 创建分组标签
     */
    private JLabel createGroupLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * 创建侧边栏按钮
     */
    private JButton createSidebarButton(String text, String actionCommand, boolean isDefault) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isPressed();
                if (this == activeButton) {
                    g2d.setColor(new Color(0x00, 0x78, 0xD4, 40));
                    g2d.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 6, 6);
                    g2d.setColor(Theme.ACCENT_PRIMARY);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(4, 6, 4, getHeight() - 6);
                } else if (pressed) {
                    g2d.setColor(new Color(0x3A, 0x3A, 0x3A));
                    g2d.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 6, 6);
                } else if (hover) {
                    g2d.setColor(new Color(0x33, 0x33, 0x33));
                    g2d.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 6, 6);
                }
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_SIDEBAR);
        btn.setForeground(Theme.TEXT_PRIMARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 36));
        btn.setActionCommand(actionCommand);
        btn.addActionListener(this::onSidebarClick);

        if (isDefault) {
            activeButton = btn;
        }

        return btn;
    }

    /**
     * 侧边栏按钮点击事件
     */
    private void onSidebarClick(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String cmd = source.getActionCommand();

        if ("exit".equals(cmd)) {
            int result = JOptionPane.showConfirmDialog(this,
                    "确定要退出系统吗？", "退出确认",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            return;
        }

        // 更新激活按钮
        if (activeButton != null) {
            activeButton.repaint();
        }
        activeButton = source;
        activeButton.repaint();

        showPanel(cmd);
    }

    /**
     * 显示对应的功能面板
     */
    private void showPanel(String cmd) {
        contentPanel.removeAll();

        switch (cmd) {
            case "employee":
                if (employeePanel == null) {
                    employeePanel = new EmployeePanel(service, logService);
                }
                employeePanel.refreshData();
                contentPanel.add(employeePanel, BorderLayout.CENTER);
                setStatus("员工列表 - 共 " + service.getAllEmployees().size() + " 名员工");
                break;
            case "add":
                showAddEmployeeDialog();
                return;
            case "search":
                showSearchDialog();
                return;
            case "transfer":
                showTransferDialog();
                return;
            case "calc_salary":
                if (salaryPanel == null) {
                    salaryPanel = new SalaryPanel(service, logService);
                }
                salaryPanel.showCalculateSalary();
                contentPanel.add(salaryPanel, BorderLayout.CENTER);
                setStatus("计算薪资");
                break;
            case "salary_sort":
                if (salaryPanel == null) {
                    salaryPanel = new SalaryPanel(service, logService);
                }
                salaryPanel.showSalarySort();
                contentPanel.add(salaryPanel, BorderLayout.CENTER);
                setStatus("薪资排序");
                break;
            case "salary_filter":
                if (salaryPanel == null) {
                    salaryPanel = new SalaryPanel(service, logService);
                }
                salaryPanel.showSalaryFilter();
                contentPanel.add(salaryPanel, BorderLayout.CENTER);
                setStatus("薪资筛选");
                break;
            case "champion":
                if (salaryPanel == null) {
                    salaryPanel = new SalaryPanel(service, logService);
                }
                salaryPanel.showChampion();
                contentPanel.add(salaryPanel, BorderLayout.CENTER);
                setStatus("薪资冠军");
                break;
            case "raise":
                if (salaryPanel == null) {
                    salaryPanel = new SalaryPanel(service, logService);
                }
                salaryPanel.showRaiseManagement();
                contentPanel.add(salaryPanel, BorderLayout.CENTER);
                setStatus("涨薪管理");
                break;
            case "leave":
                if (salaryPanel == null) {
                    salaryPanel = new SalaryPanel(service, logService);
                }
                salaryPanel.showLeave();
                contentPanel.add(salaryPanel, BorderLayout.CENTER);
                setStatus("请假扣薪");
                break;
            case "dept_stats":
                if (analyticsPanel == null) {
                    analyticsPanel = new AnalyticsPanel(service, logService);
                }
                analyticsPanel.showDeptStats();
                contentPanel.add(analyticsPanel, BorderLayout.CENTER);
                setStatus("部门统计");
                break;
            case "bar_chart":
                if (analyticsPanel == null) {
                    analyticsPanel = new AnalyticsPanel(service, logService);
                }
                analyticsPanel.showBarChart();
                contentPanel.add(analyticsPanel, BorderLayout.CENTER);
                setStatus("薪资柱状图");
                break;
            case "hire_trend":
                if (analyticsPanel == null) {
                    analyticsPanel = new AnalyticsPanel(service, logService);
                }
                analyticsPanel.showHireTrend();
                contentPanel.add(analyticsPanel, BorderLayout.CENTER);
                setStatus("入职趋势");
                break;
            case "report":
                if (analyticsPanel == null) {
                    analyticsPanel = new AnalyticsPanel(service, logService);
                }
                analyticsPanel.showReport();
                contentPanel.add(analyticsPanel, BorderLayout.CENTER);
                setStatus("月度报表");
                break;
            case "logs":
                if (settingsPanel == null) {
                    settingsPanel = new SettingsPanel(service, logService);
                }
                settingsPanel.showLogs();
                contentPanel.add(settingsPanel, BorderLayout.CENTER);
                setStatus("操作日志");
                break;
            case "export":
                if (settingsPanel == null) {
                    settingsPanel = new SettingsPanel(service, logService);
                }
                settingsPanel.showExport();
                contentPanel.add(settingsPanel, BorderLayout.CENTER);
                setStatus("导出CSV");
                break;
            case "import":
                if (settingsPanel == null) {
                    settingsPanel = new SettingsPanel(service, logService);
                }
                settingsPanel.showImport();
                contentPanel.add(settingsPanel, BorderLayout.CENTER);
                setStatus("批量导入");
                break;
            case "backup":
                if (settingsPanel == null) {
                    settingsPanel = new SettingsPanel(service, logService);
                }
                settingsPanel.showBackup();
                contentPanel.add(settingsPanel, BorderLayout.CENTER);
                setStatus("数据备份");
                break;
            case "restore":
                if (settingsPanel == null) {
                    settingsPanel = new SettingsPanel(service, logService);
                }
                settingsPanel.showRestore();
                contentPanel.add(settingsPanel, BorderLayout.CENTER);
                setStatus("恢复备份");
                break;
            case "password":
                if (settingsPanel == null) {
                    settingsPanel = new SettingsPanel(service, logService);
                }
                settingsPanel.showPassword();
                contentPanel.add(settingsPanel, BorderLayout.CENTER);
                setStatus("修改密码");
                break;
        }

        contentPanel.revalidate();
        contentPanel.repaint();
        updateTotalLabel();
    }

    /**
     * 显示添加员工对话框
     */
    private void showAddEmployeeDialog() {
        EmployeeDialog dialog = new EmployeeDialog(this, service, logService, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            if (employeePanel != null) {
                employeePanel.refreshData();
            }
            updateTotalLabel();
            setStatus("已添加员工");
        }
    }

    /**
     * 显示查询对话框
     */
    private void showSearchDialog() {
        // 使用简单的输入对话框
        String[] options = {"按工号", "按姓名", "按部门", "模糊搜索"};
        int choice = JOptionPane.showOptionDialog(this,
                "请选择查询方式：", "查询员工",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (choice < 0) return;

        String keyword = JOptionPane.showInputDialog(this,
                "请输入" + options[choice].substring(2) + "：", "查询员工",
                JOptionPane.QUESTION_MESSAGE);

        if (keyword == null || keyword.trim().isEmpty()) return;

        List<Employee> result;
        switch (choice) {
            case 0:
                Employee e = service.findById(keyword.trim());
                result = e != null ? List.of(e) : List.of();
                break;
            case 1:
                result = service.findByName(keyword.trim());
                break;
            case 2:
                result = service.findByDept(keyword.trim());
                break;
            default:
                result = service.fuzzySearch(keyword.trim());
                break;
        }

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到匹配的员工记录",
                    "查询结果", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 显示在员工面板中
            if (employeePanel == null) {
                employeePanel = new EmployeePanel(service, logService);
            }
            employeePanel.showSearchResult(result);
            contentPanel.removeAll();
            contentPanel.add(employeePanel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            setStatus("查询结果：找到 " + result.size() + " 条记录");
        }
    }

    /**
     * 显示转岗对话框
     */
    private void showTransferDialog() {
        String id = JOptionPane.showInputDialog(this,
                "请输入员工工号：", "员工转岗",
                JOptionPane.QUESTION_MESSAGE);
        if (id == null || id.trim().isEmpty()) return;

        Employee e = service.findById(id.trim());
        if (e == null) {
            JOptionPane.showMessageDialog(this, "未找到该员工！",
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] types = {"全职", "兼职", "管理层"};
        String typeStr = (String) JOptionPane.showInputDialog(this,
                "员工：" + e.getName() + "\n当前类型：" + getEmployeeType(e) + "\n请选择目标类型：",
                "员工转岗", JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        if (typeStr == null) return;

        int newType;
        if ("全职".equals(typeStr)) newType = 1;
        else if ("兼职".equals(typeStr)) newType = 2;
        else newType = 3;

        try {
            switch (newType) {
                case 1:
                    String baseStr = JOptionPane.showInputDialog(this, "请输入基本工资：");
                    if (baseStr == null) return;
                    service.transferEmployee(id.trim(), newType, Double.parseDouble(baseStr));
                    break;
                case 2:
                    String rateStr = JOptionPane.showInputDialog(this, "请输入时薪：");
                    if (rateStr == null) return;
                    String hoursStr = JOptionPane.showInputDialog(this, "请输入工作小时数：");
                    if (hoursStr == null) return;
                    service.transferEmployee(id.trim(), newType, Double.parseDouble(rateStr), Double.parseDouble(hoursStr));
                    break;
                case 3:
                    String mgrBaseStr = JOptionPane.showInputDialog(this, "请输入基本工资：");
                    if (mgrBaseStr == null) return;
                    String bonusStr = JOptionPane.showInputDialog(this, "请输入奖金：");
                    if (bonusStr == null) return;
                    service.transferEmployee(id.trim(), newType, Double.parseDouble(mgrBaseStr), Double.parseDouble(bonusStr));
                    break;
            }
            JOptionPane.showMessageDialog(this, "转岗成功！");
            logService.addLog("员工转岗：工号=" + id + "，目标类型=" + typeStr);
            if (employeePanel != null) employeePanel.refreshData();
            updateTotalLabel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "转岗失败：" + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getEmployeeType(Employee e) {
        if (e instanceof model.FullTimeEmployee) return "全职";
        if (e instanceof model.PartTimeEmployee) return "兼职";
        if (e instanceof model.Manager) return "管理层";
        return "未知";
    }

    /**
     * 创建底部状态栏
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Theme.BG_SIDEBAR);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        statusBar.setPreferredSize(new Dimension(0, 32));

        // 左侧员工总数
        totalLabel = new JLabel();
        totalLabel.setFont(Theme.FONT_SMALL);
        totalLabel.setForeground(Theme.TEXT_SECONDARY);

        // 右侧操作提示
        statusLabel = new JLabel("就绪");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.TEXT_MUTED);

        statusBar.add(totalLabel, BorderLayout.WEST);
        statusBar.add(statusLabel, BorderLayout.EAST);

        updateTotalLabel();

        return statusBar;
    }

    /**
     * 更新员工总数显示
     */
    private void updateTotalLabel() {
        int count = service.getAllEmployees().size();
        totalLabel.setText("👥 员工总数：" + count + " 人");
    }

    /**
     * 设置状态栏提示
     */
    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    /**
     * 启动时钟
     */
    private void startClock() {
        clockTimer = new Timer(1000, e -> {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            timeLabel.setText("🕐 " + time);
        });
        clockTimer.start();
    }

    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
}
