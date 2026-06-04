package ui.modern;

import model.Employee;
import model.LeaveType;
import service.EmployeeService;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 薪资管理面板
 * 包含计算薪资、薪资排序、薪资筛选、薪资冠军、涨薪管理、请假扣薪
 */
public class SalaryPanel extends JPanel {

    private final EmployeeService service;
    private final LogService logService;
    private JPanel contentArea;
    private JLabel titleLabel;

    public SalaryPanel(EmployeeService service, LogService logService) {
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

        JLabel iconLabel = new JLabel("💰");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        titlePanel.add(iconLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("薪资管理");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        textPanel.add(titleLabel);

        JLabel subtitle = new JLabel("薪资计算、排序、筛选与管理");
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
     * 显示计算薪资
     */
    public void showCalculateSalary() {
        titleLabel.setText("计算薪资");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel prompt = new JLabel("请输入员工工号计算薪资：");
        prompt.setFont(Theme.FONT_H2);
        prompt.setForeground(Theme.TEXT_PRIMARY);
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(prompt);
        form.add(Box.createVerticalStrut(16));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inputPanel.setOpaque(false);

        JTextField idField = Theme.createTextField();
        idField.setPreferredSize(new Dimension(200, 38));
        inputPanel.add(idField);

        JButton calcBtn = Theme.createButton("💵 计算");
        calcBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入工号", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Employee emp = service.findById(id);
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "未找到该员工！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double salary = service.calculateSalary(id);
            logService.addLog("计算薪资：工号=" + id + "，薪资=" + String.format("%.2f", salary));

            String msg = String.format(
                "员工：%s\n部门：%s\n类型：%s\n━━━━━━━━━━━━━━━\n薪资：¥%.2f",
                emp.getName(), emp.getDepartment(), getEmployeeType(emp), salary
            );
            JOptionPane.showMessageDialog(this, msg, "薪资计算结果", JOptionPane.INFORMATION_MESSAGE);
        });
        inputPanel.add(calcBtn);

        form.add(inputPanel);

        // 显示所有员工薪资列表
        form.add(Box.createVerticalStrut(24));
        JLabel listTitle = new JLabel("所有员工薪资一览：");
        listTitle.setFont(Theme.FONT_H3);
        listTitle.setForeground(Theme.TEXT_SECONDARY);
        listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(listTitle);
        form.add(Box.createVerticalStrut(12));

        JPanel salaryList = new JPanel();
        salaryList.setOpaque(false);
        salaryList.setLayout(new BoxLayout(salaryList, BoxLayout.Y_AXIS));

        List<Employee> all = service.getAllEmployees();
        if (all.isEmpty()) {
            JLabel empty = new JLabel("暂无员工数据");
            empty.setForeground(Theme.TEXT_MUTED);
            empty.setFont(Theme.FONT_BODY);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            salaryList.add(empty);
        } else {
            for (Employee emp : all) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);
                row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

                JLabel nameLabel = new JLabel(emp.getId() + " - " + emp.getName() + " (" + emp.getDepartment() + ")");
                nameLabel.setFont(Theme.FONT_BODY);
                nameLabel.setForeground(Theme.TEXT_PRIMARY);
                row.add(nameLabel, BorderLayout.WEST);

                JLabel salaryLabel = new JLabel(String.format("¥%.2f", emp.getSalary()));
                salaryLabel.setFont(Theme.FONT_H3);
                salaryLabel.setForeground(Theme.ACCENT_GREEN);
                row.add(salaryLabel, BorderLayout.EAST);

                salaryList.add(row);
                salaryList.add(Box.createVerticalStrut(2));
            }
        }

        JScrollPane scroll = Theme.createScrollPane(salaryList);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        form.add(scroll);

        card.add(form, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示薪资排序
     */
    public void showSalarySort() {
        titleLabel.setText("薪资排序");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("📈 薪资排行榜（从高到低）");
        title.setFont(Theme.FONT_H2);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(16));

        List<Employee> all = service.getAllEmployees();
        Collections.sort(all, (e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()));

        if (all.isEmpty()) {
            JLabel empty = new JLabel("暂无员工数据");
            empty.setForeground(Theme.TEXT_MUTED);
            empty.setFont(Theme.FONT_BODY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(empty);
        } else {
            int rank = 1;
            for (Employee emp : all) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);
                row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                row.setBackground(rank == 1 ? new Color(0x4A, 0x3A, 0x00) : Theme.BG_CARD);

                JLabel rankLabel = new JLabel(getRankEmoji(rank) + " #" + rank);
                rankLabel.setFont(Theme.FONT_H3);
                rankLabel.setForeground(rank == 1 ? Theme.ACCENT_ORANGE : Theme.TEXT_PRIMARY);
                row.add(rankLabel, BorderLayout.WEST);

                JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
                centerPanel.setOpaque(false);
                centerPanel.add(new JLabel(emp.getId()) {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
                centerPanel.add(new JLabel(emp.getName()) {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_PRIMARY); }});
                centerPanel.add(new JLabel(emp.getDepartment()) {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
                row.add(centerPanel, BorderLayout.CENTER);

                JLabel salaryLabel = new JLabel(String.format("¥%.2f", emp.getSalary()));
                salaryLabel.setFont(Theme.FONT_H3);
                salaryLabel.setForeground(rank == 1 ? Theme.ACCENT_ORANGE : Theme.ACCENT_GREEN);
                row.add(salaryLabel, BorderLayout.EAST);

                content.add(row);
                content.add(Box.createVerticalStrut(4));
                rank++;
            }
        }

        JScrollPane scroll = Theme.createScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.add(scroll, BorderLayout.CENTER);

        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private String getRankEmoji(int rank) {
        switch (rank) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return "  ";
        }
    }

    /**
     * 显示薪资筛选
     */
    public void showSalaryFilter() {
        titleLabel.setText("薪资筛选");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel prompt = new JLabel("按薪资范围筛选员工：");
        prompt.setFont(Theme.FONT_H2);
        prompt.setForeground(Theme.TEXT_PRIMARY);
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(prompt);
        form.add(Box.createVerticalStrut(16));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inputPanel.setOpaque(false);

        JLabel minLabel = new JLabel("最低薪资：");
        minLabel.setFont(Theme.FONT_BODY);
        minLabel.setForeground(Theme.TEXT_SECONDARY);
        inputPanel.add(minLabel);

        JTextField minField = Theme.createTextField();
        minField.setPreferredSize(new Dimension(120, 36));
        inputPanel.add(minField);

        JLabel maxLabel = new JLabel("最高薪资：");
        maxLabel.setFont(Theme.FONT_BODY);
        maxLabel.setForeground(Theme.TEXT_SECONDARY);
        inputPanel.add(maxLabel);

        JTextField maxField = Theme.createTextField();
        maxField.setPreferredSize(new Dimension(120, 36));
        inputPanel.add(maxField);

        JButton filterBtn = Theme.createButton("🔍 筛选");
        filterBtn.addActionListener(e -> {
            try {
                double min = Double.parseDouble(minField.getText().trim());
                double max = Double.parseDouble(maxField.getText().trim());
                List<Employee> result = service.findBySalaryRange(min, max);
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到符合条件的员工", "筛选结果", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("筛选结果（" + result.size() + "人）：\n");
                    for (Employee emp : result) {
                        sb.append(emp.getId()).append(" - ").append(emp.getName())
                          .append(" - ¥").append(String.format("%.2f", emp.getSalary())).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, sb.toString(), "筛选结果", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(filterBtn);

        form.add(inputPanel);
        card.add(form, BorderLayout.NORTH);

        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示薪资冠军
     */
    public void showChampion() {
        titleLabel.setText("薪资冠军");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        Employee highest = service.getHighestSalary();
        Employee lowest = service.getLowestSalary();

        if (highest == null || lowest == null) {
            JLabel empty = new JLabel("暂无员工数据");
            empty.setForeground(Theme.TEXT_MUTED);
            empty.setFont(Theme.FONT_H2);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(empty);
        } else {
            // 冠军
            JPanel championCard = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0x4A, 0x3A, 0x00));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2d.setColor(new Color(0xFF, 0xC1, 0x07, 60));
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2d.dispose();
                }
            };
            championCard.setOpaque(false);
            championCard.setLayout(new BoxLayout(championCard, BoxLayout.Y_AXIS));
            championCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

            JLabel champTitle = new JLabel("🏆 薪资冠军");
            champTitle.setFont(Theme.FONT_H1);
            champTitle.setForeground(Theme.ACCENT_ORANGE);
            champTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            championCard.add(champTitle);
            championCard.add(Box.createVerticalStrut(16));

            championCard.add(createInfoRow("姓名", highest.getName()));
            championCard.add(createInfoRow("部门", highest.getDepartment()));
            championCard.add(createInfoRow("薪资", String.format("¥%.2f", highest.getSalary())));

            content.add(championCard);
            content.add(Box.createVerticalStrut(24));

            // 垫底
            JPanel lowestCard = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0x3A, 0x1C, 0x1C));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2d.setColor(new Color(0xF4, 0x43, 0x36, 40));
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2d.dispose();
                }
            };
            lowestCard.setOpaque(false);
            lowestCard.setLayout(new BoxLayout(lowestCard, BoxLayout.Y_AXIS));
            lowestCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

            JLabel lowTitle = new JLabel("📉 薪资最低");
            lowTitle.setFont(Theme.FONT_H1);
            lowTitle.setForeground(Theme.ACCENT_RED);
            lowTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            lowestCard.add(lowTitle);
            lowestCard.add(Box.createVerticalStrut(16));

            lowestCard.add(createInfoRow("姓名", lowest.getName()));
            lowestCard.add(createInfoRow("部门", lowest.getDepartment()));
            lowestCard.add(createInfoRow("薪资", String.format("¥%.2f", lowest.getSalary())));

            content.add(lowestCard);
        }

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel labelComp = new JLabel(label + "：");
        labelComp.setFont(Theme.FONT_BODY);
        labelComp.setForeground(Theme.TEXT_SECONDARY);
        row.add(labelComp, BorderLayout.WEST);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(Theme.FONT_H3);
        valueComp.setForeground(Theme.TEXT_PRIMARY);
        row.add(valueComp, BorderLayout.EAST);

        return row;
    }

    /**
     * 显示涨薪管理
     */
    public void showRaiseManagement() {
        titleLabel.setText("涨薪管理");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel prompt = new JLabel("📊 涨薪管理");
        prompt.setFont(Theme.FONT_H2);
        prompt.setForeground(Theme.TEXT_PRIMARY);
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(prompt);
        content.add(Box.createVerticalStrut(20));

        // 单个员工涨薪
        JPanel singlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        singlePanel.setOpaque(false);

        JLabel singleTitle = new JLabel("给单个员工涨薪：");
        singleTitle.setFont(Theme.FONT_H3);
        singleTitle.setForeground(Theme.TEXT_PRIMARY);
        singlePanel.add(singleTitle);

        JTextField idField = Theme.createTextField();
        idField.setPreferredSize(new Dimension(100, 36));
        idField.setToolTipText("工号");
        singlePanel.add(idField);

        JTextField percentField = Theme.createTextField();
        percentField.setPreferredSize(new Dimension(80, 36));
        percentField.setToolTipText("涨薪百分比");
        singlePanel.add(percentField);

        JLabel pctLabel = new JLabel("%");
        pctLabel.setFont(Theme.FONT_BODY);
        pctLabel.setForeground(Theme.TEXT_SECONDARY);
        singlePanel.add(pctLabel);

        JButton singleBtn = Theme.createButton("执行涨薪");
        singleBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                double percent = Double.parseDouble(percentField.getText().trim());
                service.raiseSalary(id, percent);
                logService.addLog("涨薪：工号=" + id + "，涨幅=" + percent + "%");
                JOptionPane.showMessageDialog(this, "涨薪成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        singlePanel.add(singleBtn);

        content.add(singlePanel);
        content.add(Box.createVerticalStrut(20));

        // 部门涨薪
        JPanel deptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        deptPanel.setOpaque(false);

        JLabel deptTitle = new JLabel("给整个部门涨薪：");
        deptTitle.setFont(Theme.FONT_H3);
        deptTitle.setForeground(Theme.TEXT_PRIMARY);
        deptPanel.add(deptTitle);

        JTextField deptField = Theme.createTextField();
        deptField.setPreferredSize(new Dimension(100, 36));
        deptField.setToolTipText("部门名称");
        deptPanel.add(deptField);

        JTextField deptPctField = Theme.createTextField();
        deptPctField.setPreferredSize(new Dimension(80, 36));
        deptPctField.setToolTipText("涨薪百分比");
        deptPanel.add(deptPctField);

        JLabel deptPctLabel = new JLabel("%");
        deptPctLabel.setFont(Theme.FONT_BODY);
        deptPctLabel.setForeground(Theme.TEXT_SECONDARY);
        deptPanel.add(deptPctLabel);

        JButton deptBtn = Theme.createButton("部门涨薪");
        deptBtn.addActionListener(e -> {
            try {
                String dept = deptField.getText().trim();
                double percent = Double.parseDouble(deptPctField.getText().trim());
                service.raiseDeptSalary(dept, percent);
                logService.addLog("部门涨薪：部门=" + dept + "，涨幅=" + percent + "%");
                JOptionPane.showMessageDialog(this, "部门涨薪成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        deptPanel.add(deptBtn);

        content.add(deptPanel);

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示请假扣薪
     */
    public void showLeave() {
        titleLabel.setText("请假扣薪");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel prompt = new JLabel("🏖 请假扣薪");
        prompt.setFont(Theme.FONT_H2);
        prompt.setForeground(Theme.TEXT_PRIMARY);
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(prompt);
        content.add(Box.createVerticalStrut(20));

        // 工号
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        idPanel.setOpaque(false);
        idPanel.add(new JLabel("员工工号：") {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
        JTextField idField = Theme.createTextField();
        idField.setPreferredSize(new Dimension(150, 36));
        idPanel.add(idField);
        content.add(idPanel);
        content.add(Box.createVerticalStrut(12));

        // 请假类型
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        typePanel.setOpaque(false);
        typePanel.add(new JLabel("请假类型：") {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"年假（不扣薪）", "病假（扣50%）", "事假（扣100%）"});
        styleCombo(typeCombo);
        typePanel.add(typeCombo);
        content.add(typePanel);
        content.add(Box.createVerticalStrut(12));

        // 天数
        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dayPanel.setOpaque(false);
        dayPanel.add(new JLabel("请假天数：") {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
        JTextField dayField = Theme.createTextField();
        dayField.setPreferredSize(new Dimension(80, 36));
        dayPanel.add(dayField);
        content.add(dayPanel);
        content.add(Box.createVerticalStrut(20));

        JButton applyBtn = Theme.createButton("📋 提交请假");
        applyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                int typeIdx = typeCombo.getSelectedIndex();
                int days = Integer.parseInt(dayField.getText().trim());

                LeaveType leaveType;
                switch (typeIdx) {
                    case 0: leaveType = LeaveType.ANNUAL; break;
                    case 1: leaveType = LeaveType.SICK; break;
                    default: leaveType = LeaveType.PERSONAL; break;
                }

                String detail = service.applyLeave(id, leaveType, days);
                logService.addLog("请假：工号=" + id + "，类型=" + typeCombo.getSelectedItem() + "，天数=" + days);
                JOptionPane.showMessageDialog(this, "请假处理完成！\n" + detail, "请假结果", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的天数", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(applyBtn);

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private String getEmployeeType(Employee e) {
        if (e instanceof model.FullTimeEmployee) return "全职";
        if (e instanceof model.PartTimeEmployee) return "兼职";
        if (e instanceof model.Manager) return "管理层";
        return "未知";
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(Theme.FONT_BODY);
        combo.setBackground(Theme.BG_INPUT);
        combo.setForeground(Theme.TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        ((JLabel) combo.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }
}
