package ui.modern;

import model.*;
import service.EmployeeService;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * 添加/修改员工对话框
 * 根据员工类型动态显示不同的薪资输入字段
 * 输入校验失败时输入框变红色提示
 */
public class EmployeeDialog extends JDialog {

    private final EmployeeService service;
    private final LogService logService;
    private final Employee editEmployee;

    private JTextField idField, nameField, contactField;
    private JComboBox<String> deptCombo, typeCombo, perfCombo;
    private JSpinner yearSpinner, monthSpinner, daySpinner;
    private JPanel salaryFieldsPanel;
    private JTextField field1, field2, field3;
    private JLabel label1, label2, label3;

    private boolean success = false;

    private static final String[] DEPARTMENTS = {"技术部", "市场部", "财务部", "人事部", "运营部", "研发部", "销售部"};
    private static final String[] TYPES = {"全职", "兼职", "管理层"};
    private static final String[] PERFS = {"A", "B", "C"};

    public EmployeeDialog(Window owner, EmployeeService service, LogService logService, Employee editEmployee) {
        super(owner, editEmployee == null ? "添加员工" : "编辑员工", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.logService = logService;
        this.editEmployee = editEmployee;
        initUI();
        if (editEmployee != null) {
            populateFields();
        }
    }

    private void initUI() {
        setSize(520, 580);
        setLocationRelativeTo(getParent());
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);

        // 标题栏
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.BG_SIDEBAR);
        titleBar.setPreferredSize(new Dimension(getWidth(), 44));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JLabel titleLabel = new JLabel("  " + (editEmployee == null ? "➕ 添加员工" : "✏️ 编辑员工"));
        titleLabel.setFont(Theme.FONT_H2);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleBar.add(titleLabel, BorderLayout.WEST);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        closeBtn.setForeground(Theme.TEXT_SECONDARY);
        closeBtn.setBackground(Theme.BG_SIDEBAR);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setPreferredSize(new Dimension(44, 44));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        titleBar.add(closeBtn, BorderLayout.EAST);

        root.add(titleBar, BorderLayout.NORTH);

        // 表单
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_MAIN);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        int row = 0;

        // 工号
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel idLabel = new JLabel("工号 *");
        idLabel.setFont(Theme.FONT_H3);
        idLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(idLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        idField = Theme.createTextField();
        form.add(idField, gbc);

        // 姓名
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel nameLabel = new JLabel("姓名 *");
        nameLabel.setFont(Theme.FONT_H3);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        nameField = Theme.createTextField();
        form.add(nameField, gbc);

        // 部门
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel deptLabel = new JLabel("部门");
        deptLabel.setFont(Theme.FONT_H3);
        deptLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(deptLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        deptCombo = new JComboBox<>(DEPARTMENTS);
        styleCombo(deptCombo);
        form.add(deptCombo, gbc);

        // 联系方式
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel contactLabel = new JLabel("联系方式");
        contactLabel.setFont(Theme.FONT_H3);
        contactLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(contactLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        contactField = Theme.createTextField();
        form.add(contactField, gbc);

        // 员工类型
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel typeLabel = new JLabel("员工类型 *");
        typeLabel.setFont(Theme.FONT_H3);
        typeLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(typeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        typeCombo = new JComboBox<>(TYPES);
        styleCombo(typeCombo);
        typeCombo.addActionListener(e -> updateSalaryFields());
        form.add(typeCombo, gbc);

        // 入职日期
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel hireLabel = new JLabel("入职日期");
        hireLabel.setFont(Theme.FONT_H3);
        hireLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(hireLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        datePanel.setOpaque(false);
        SpinnerNumberModel yearModel = new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2030, 1);
        yearSpinner = new JSpinner(yearModel);
        yearSpinner.setPreferredSize(new Dimension(80, 34));
        styleSpinner(yearSpinner);
        datePanel.add(yearSpinner);
        datePanel.add(new JLabel("年") {{ setForeground(Theme.TEXT_SECONDARY); setFont(Theme.FONT_BODY); }});

        SpinnerNumberModel monthModel = new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1);
        monthSpinner = new JSpinner(monthModel);
        monthSpinner.setPreferredSize(new Dimension(60, 34));
        styleSpinner(monthSpinner);
        datePanel.add(monthSpinner);
        datePanel.add(new JLabel("月") {{ setForeground(Theme.TEXT_SECONDARY); setFont(Theme.FONT_BODY); }});

        SpinnerNumberModel dayModel = new SpinnerNumberModel(LocalDate.now().getDayOfMonth(), 1, 31, 1);
        daySpinner = new JSpinner(dayModel);
        daySpinner.setPreferredSize(new Dimension(60, 34));
        styleSpinner(daySpinner);
        datePanel.add(daySpinner);
        datePanel.add(new JLabel("日") {{ setForeground(Theme.TEXT_SECONDARY); setFont(Theme.FONT_BODY); }});

        form.add(datePanel, gbc);

        // 绩效
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel perfLabel = new JLabel("绩效");
        perfLabel.setFont(Theme.FONT_H3);
        perfLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(perfLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        perfCombo = new JComboBox<>(PERFS);
        styleCombo(perfCombo);
        form.add(perfCombo, gbc);

        // 动态薪资字段
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.gridwidth = 2;
        salaryFieldsPanel = new JPanel(new GridBagLayout());
        salaryFieldsPanel.setOpaque(false);
        form.add(salaryFieldsPanel, gbc);

        root.add(form, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomPanel.setBackground(Theme.BG_SIDEBAR);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));

        JButton cancelBtn = Theme.createSecondaryButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        bottomPanel.add(cancelBtn);

        JButton saveBtn = Theme.createButton("💾 保存");
        saveBtn.addActionListener(e -> saveEmployee());
        bottomPanel.add(saveBtn);

        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);

        // 初始化薪资字段
        updateSalaryFields();
    }

    /**
     * 根据员工类型动态显示不同的薪资输入字段
     */
    private void updateSalaryFields() {
        salaryFieldsPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);

        String type = (String) typeCombo.getSelectedItem();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label1.setFont(Theme.FONT_H3);
        label1.setForeground(Theme.TEXT_PRIMARY);
        label2.setFont(Theme.FONT_H3);
        label2.setForeground(Theme.TEXT_PRIMARY);
        label3.setFont(Theme.FONT_H3);
        label3.setForeground(Theme.TEXT_PRIMARY);

        field1 = Theme.createTextField();
        field2 = Theme.createTextField();
        field3 = Theme.createTextField();

        if ("全职".equals(type)) {
            label1.setText("基本工资 *");
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            salaryFieldsPanel.add(label1, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            salaryFieldsPanel.add(field1, gbc);
            field2.setVisible(false);
            field3.setVisible(false);
        } else if ("兼职".equals(type)) {
            label1.setText("时薪 *");
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            salaryFieldsPanel.add(label1, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            salaryFieldsPanel.add(field1, gbc);

            label2.setText("工作小时数 *");
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            salaryFieldsPanel.add(label2, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            salaryFieldsPanel.add(field2, gbc);
            field3.setVisible(false);
        } else if ("管理层".equals(type)) {
            label1.setText("基本工资 *");
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            salaryFieldsPanel.add(label1, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            salaryFieldsPanel.add(field1, gbc);

            label2.setText("奖金 *");
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            salaryFieldsPanel.add(label2, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            salaryFieldsPanel.add(field2, gbc);
            field3.setVisible(false);
        }

        salaryFieldsPanel.revalidate();
        salaryFieldsPanel.repaint();
    }

    private void populateFields() {
        idField.setText(editEmployee.getId());
        idField.setEnabled(false);
        nameField.setText(editEmployee.getName());
        deptCombo.setSelectedItem(editEmployee.getDepartment());
        contactField.setText(editEmployee.getContact());

        if (editEmployee instanceof FullTimeEmployee) {
            typeCombo.setSelectedItem("全职");
            field1.setText(String.valueOf(((FullTimeEmployee) editEmployee).getBaseSalary()));
        } else if (editEmployee instanceof PartTimeEmployee) {
            typeCombo.setSelectedItem("兼职");
            field1.setText(String.valueOf(((PartTimeEmployee) editEmployee).getHourlyRate()));
            field2.setText(String.valueOf(((PartTimeEmployee) editEmployee).getHoursWorked()));
        } else if (editEmployee instanceof Manager) {
            typeCombo.setSelectedItem("管理层");
            field1.setText(String.valueOf(((Manager) editEmployee).getBaseSalary()));
            field2.setText(String.valueOf(((Manager) editEmployee).getBonus()));
        }

        if (editEmployee.getHireDate() != null) {
            yearSpinner.setValue(editEmployee.getHireDate().getYear());
            monthSpinner.setValue(editEmployee.getHireDate().getMonthValue());
            daySpinner.setValue(editEmployee.getHireDate().getDayOfMonth());
        }

        if (editEmployee.getPerformance() != null) {
            perfCombo.setSelectedItem(editEmployee.getPerformance().name());
        }
    }

    private void saveEmployee() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();

        // 校验必填字段
        boolean valid = true;
        if (id.isEmpty()) {
            idField.setBackground(new Color(0x4A, 0x1C, 0x1C));
            valid = false;
        } else {
            idField.setBackground(Theme.BG_INPUT);
        }
        if (name.isEmpty()) {
            nameField.setBackground(new Color(0x4A, 0x1C, 0x1C));
            valid = false;
        } else {
            nameField.setBackground(Theme.BG_INPUT);
        }

        if (!valid) {
            JOptionPane.showMessageDialog(this, "请填写所有必填字段（红色标记）", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String dept = (String) deptCombo.getSelectedItem();
            String contact = contactField.getText().trim();
            int year = (Integer) yearSpinner.getValue();
            int month = (Integer) monthSpinner.getValue();
            int day = (Integer) daySpinner.getValue();
            LocalDate hireDate = LocalDate.of(year, month, day);
            Performance perf = Performance.valueOf((String) perfCombo.getSelectedItem());

            String type = (String) typeCombo.getSelectedItem();
            Employee emp;

            if ("全职".equals(type)) {
                double baseSalary = Double.parseDouble(field1.getText().trim());
                emp = new FullTimeEmployee(id, name, dept, contact, baseSalary, hireDate);
            } else if ("兼职".equals(type)) {
                double rate = Double.parseDouble(field1.getText().trim());
                double hours = Double.parseDouble(field2.getText().trim());
                emp = new PartTimeEmployee(id, name, dept, contact, rate, hours, hireDate);
            } else {
                double baseSalary = Double.parseDouble(field1.getText().trim());
                double bonus = Double.parseDouble(field2.getText().trim());
                emp = new Manager(id, name, dept, contact, baseSalary, bonus, hireDate);
            }

            emp.setPerformance(perf);

            if (editEmployee != null) {
                // 编辑模式 - 保留请假记录
                for (String record : editEmployee.getLeaveRecords()) {
                    emp.addLeaveRecord(record);
                }
                service.updateEmployee(id, emp);
                logService.addLog("修改员工：工号=" + id + "，姓名=" + name);
            } else {
                service.addEmployee(emp);
                logService.addLog("添加员工：工号=" + id + "，姓名=" + name);
            }

            success = true;
            dispose();
        } catch (NumberFormatException e) {
            // 输入框变红
            if (field1.isVisible()) field1.setBackground(new Color(0x4A, 0x1C, 0x1C));
            if (field2.isVisible()) field2.setBackground(new Color(0x4A, 0x1C, 0x1C));
            JOptionPane.showMessageDialog(this, "薪资字段必须为数字", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "保存失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
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

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(Theme.FONT_BODY);
        spinner.setBackground(Theme.BG_INPUT);
        spinner.setForeground(Theme.TEXT_PRIMARY);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.NumberEditor) {
            JSpinner.NumberEditor numEditor = (JSpinner.NumberEditor) editor;
            numEditor.getTextField().setBackground(Theme.BG_INPUT);
            numEditor.getTextField().setForeground(Theme.TEXT_PRIMARY);
            numEditor.getTextField().setFont(Theme.FONT_BODY);
            numEditor.getTextField().setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        }
    }
}
