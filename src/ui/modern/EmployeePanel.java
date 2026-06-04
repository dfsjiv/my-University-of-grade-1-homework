package ui.modern;

import model.Employee;
import model.FullTimeEmployee;
import model.Manager;
import model.PartTimeEmployee;
import model.Performance;
import service.EmployeeService;
import service.LogService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 员工管理面板
 * JTable 显示所有员工，支持排序、搜索、详情显示
 */
public class EmployeePanel extends JPanel {

    private final EmployeeService service;
    private final LogService logService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;
    private JTextArea detailArea;
    private List<Employee> currentData;

    // 表格列名 - 对应工号、姓名、部门、类型、绩效、工龄、薪资
    private static final String[] COLUMNS = {
        "工号", "姓名", "部门", "类型", "绩效", "工龄(年)", "薪资"
    };

    public EmployeePanel(EmployeeService service, LogService logService) {
        this.service = service;
        this.logService = logService;
        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(Theme.PANEL_PADDING, Theme.PANEL_PADDING,
                Theme.PANEL_PADDING, Theme.PANEL_PADDING));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("👥");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        titlePanel.add(iconLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("员工列表");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        textPanel.add(title);

        JLabel subtitle = new JLabel("查看和管理所有员工信息");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        textPanel.add(subtitle);

        titlePanel.add(textPanel);
        header.add(titlePanel, BorderLayout.WEST);

        // 搜索框
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        searchField = Theme.createTextField();
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.putClientProperty("JTextField.placeholder", "🔍 搜索工号/姓名/部门...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        actionPanel.add(searchField);

        JButton addBtn = Theme.createButton("➕ 添加员工");
        addBtn.addActionListener(e -> showAddDialog());
        actionPanel.add(addBtn);

        JButton editBtn = Theme.createSecondaryButton("✏️ 编辑");
        editBtn.addActionListener(e -> showEditDialog());
        actionPanel.add(editBtn);

        JButton deleteBtn = Theme.createDangerButton("🗑️ 删除");
        deleteBtn.addActionListener(e -> deleteEmployee());
        actionPanel.add(deleteBtn);

        JButton refreshBtn = Theme.createSecondaryButton("🔄 刷新");
        refreshBtn.addActionListener(e -> refreshData());
        actionPanel.add(refreshBtn);

        header.add(actionPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        // 左侧表格
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable();

        JScrollPane tableScroll = Theme.createScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        JPanel tableCard = Theme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tableCard.add(tableScroll, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(tableCard, gbc);

        // 右侧详情面板
        JPanel detailCard = Theme.createCard();
        detailCard.setLayout(new BorderLayout());
        detailCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        detailCard.setPreferredSize(new Dimension(280, 0));

        JLabel detailTitle = new JLabel("📋 员工详情");
        detailTitle.setFont(Theme.FONT_H2);
        detailTitle.setForeground(Theme.TEXT_PRIMARY);
        detailTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        detailCard.add(detailTitle, BorderLayout.NORTH);

        detailArea = new JTextArea();
        detailArea.setFont(Theme.FONT_BODY);
        detailArea.setForeground(Theme.TEXT_PRIMARY);
        detailArea.setBackground(Theme.BG_CARD);
        detailArea.setEditable(false);
        detailArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        detailArea.setText("请选择一名员工查看详情");
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);

        JScrollPane detailScroll = Theme.createScrollPane(detailArea);
        detailCard.add(detailScroll, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        main.add(detailCard, gbc);

        return main;
    }

    private void styleTable() {
        table.setFont(Theme.FONT_TABLE);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setBackground(Theme.BG_TABLE_ROW);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(0x00, 0x78, 0xD4, 60));
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(null);

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_TABLE_HEADER);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBackground(Theme.BG_CARD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        header.setPreferredSize(new Dimension(0, 36));

        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        // 支持排序
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // 自定义渲染器
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Theme.BG_TABLE_ROW : Theme.BG_TABLE_ALT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // 选中行显示详情
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetail();
            }
        });

        // 双击编辑
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditDialog();
                }
            }
        });
    }

    private JPanel createBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 4, 0, 4));

        statusLabel = new JLabel("共 0 条记录");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.TEXT_MUTED);
        bottom.add(statusLabel, BorderLayout.WEST);

        return bottom;
    }

    /**
     * 显示选中员工的详情
     */
    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            detailArea.setText("请选择一名员工查看详情");
            return;
        }

        // 转换排序后的行号
        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        Employee emp = service.findById(id);
        if (emp == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("━━━ 员工详情 ━━━\n\n");
        sb.append("工号：").append(emp.getId()).append("\n");
        sb.append("姓名：").append(emp.getName()).append("\n");
        sb.append("部门：").append(emp.getDepartment()).append("\n");
        sb.append("联系方式：").append(emp.getContact()).append("\n");
        sb.append("类型：").append(getEmployeeType(emp)).append("\n");
        sb.append("入职日期：").append(emp.getHireDate() != null ? emp.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A").append("\n");
        sb.append("工龄：").append(emp.getYearsOfService()).append(" 年\n");
        sb.append("绩效：").append(emp.getPerformance() != null ? emp.getPerformance().name() : "C").append("\n");
        sb.append("薪资：¥").append(String.format("%.2f", emp.getSalary())).append("\n");

        if (emp instanceof FullTimeEmployee) {
            sb.append("基本工资：¥").append(String.format("%.2f", ((FullTimeEmployee) emp).getBaseSalary())).append("\n");
        } else if (emp instanceof PartTimeEmployee) {
            sb.append("时薪：¥").append(String.format("%.2f", ((PartTimeEmployee) emp).getHourlyRate())).append("\n");
            sb.append("工作小时数：").append(((PartTimeEmployee) emp).getHoursWorked()).append("\n");
        } else if (emp instanceof Manager) {
            sb.append("基本工资：¥").append(String.format("%.2f", ((Manager) emp).getBaseSalary())).append("\n");
            sb.append("奖金：¥").append(String.format("%.2f", ((Manager) emp).getBonus())).append("\n");
        }

        if (!emp.getLeaveRecords().isEmpty()) {
            sb.append("\n请假记录：\n");
            for (String record : emp.getLeaveRecords()) {
                sb.append("  • ").append(record).append("\n");
            }
        }

        detailArea.setText(sb.toString());
        detailArea.setCaretPosition(0);
    }

    private String getEmployeeType(Employee e) {
        if (e instanceof FullTimeEmployee) return "全职";
        if (e instanceof PartTimeEmployee) return "兼职";
        if (e instanceof Manager) return "管理层";
        return "未知";
    }

    public void refreshData() {
        currentData = service.getAllEmployees();
        updateTable(currentData);
    }

    public void showSearchResult(List<Employee> result) {
        currentData = result;
        updateTable(result);
    }

    private void updateTable(List<Employee> employees) {
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                emp.getId(),
                emp.getName(),
                emp.getDepartment(),
                getEmployeeType(emp),
                emp.getPerformance() != null ? emp.getPerformance().name() : "C",
                emp.getYearsOfService(),
                String.format("¥%.2f", emp.getSalary())
            });
        }
        updateStatus();
    }

    private void updateStatus() {
        int total = service.getAllEmployees().size();
        int showing = tableModel.getRowCount();
        statusLabel.setText(String.format("共 %d 条记录 | 显示 %d 条", total, showing));
    }

    private void filterTable() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }

        List<Employee> all = service.getAllEmployees();
        tableModel.setRowCount(0);
        for (Employee emp : all) {
            if (emp.getId().toLowerCase().contains(keyword) ||
                emp.getName().toLowerCase().contains(keyword) ||
                emp.getDepartment().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getName(),
                    emp.getDepartment(),
                    getEmployeeType(emp),
                    emp.getPerformance() != null ? emp.getPerformance().name() : "C",
                    emp.getYearsOfService(),
                    String.format("¥%.2f", emp.getSalary())
                });
            }
        }
        updateStatus();
    }

    private void showAddDialog() {
        EmployeeDialog dialog = new EmployeeDialog(
                SwingUtilities.getWindowAncestor(this), service, logService, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            refreshData();
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的员工", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        Employee emp = service.findById(id);
        if (emp == null) return;

        EmployeeDialog dialog = new EmployeeDialog(
                SwingUtilities.getWindowAncestor(this), service, logService, emp);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            refreshData();
        }
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的员工", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        String name = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("确定要删除员工「%s」(ID: %s) 吗？\n此操作不可撤销！", name, id),
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            service.removeEmployee(id);
            logService.addLog("删除员工：工号=" + id + "，姓名=" + name);
            refreshData();
            JOptionPane.showMessageDialog(this, "✅ 员工已删除！", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
