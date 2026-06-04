package ui.modern;

import model.Employee;
import service.EmployeeService;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统功能面板
 * 操作日志、导出CSV、批量导入、数据备份、恢复备份、修改密码
 */
public class SettingsPanel extends JPanel {

    private final EmployeeService service;
    private final LogService logService;
    private JPanel contentArea;
    private JLabel titleLabel;

    public SettingsPanel(EmployeeService service, LogService logService) {
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

        JLabel iconLabel = new JLabel("⚙️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        titlePanel.add(iconLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("系统功能");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        textPanel.add(titleLabel);

        JLabel subtitle = new JLabel("系统维护、数据管理和安全设置");
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
     * 显示操作日志
     */
    public void showLogs() {
        titleLabel.setText("操作日志");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logTitle = new JLabel("📝 系统操作日志");
        logTitle.setFont(Theme.FONT_H2);
        logTitle.setForeground(Theme.TEXT_PRIMARY);
        logTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        card.add(logTitle, BorderLayout.NORTH);

        List<String> logs = logService.getLogs();
        JTextArea logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setForeground(Theme.TEXT_PRIMARY);
        logArea.setBackground(Theme.BG_CARD);
        logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        if (logs.isEmpty()) {
            logArea.setText("暂无操作日志");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = logs.size() - 1; i >= 0; i--) {
                sb.append(logs.get(i)).append("\n");
            }
            logArea.setText(sb.toString());
        }

        JScrollPane scroll = Theme.createScrollPane(logArea);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.add(scroll, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bottomPanel.setOpaque(false);

        JButton refreshBtn = Theme.createSecondaryButton("🔄 刷新");
        refreshBtn.addActionListener(e -> showLogs());
        bottomPanel.add(refreshBtn);

        JButton clearBtn = Theme.createDangerButton("🗑️ 清空日志");
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "确定要清空所有日志吗？",
                    "确认清空", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                logService.clearLogs();
                showLogs();
            }
        });
        bottomPanel.add(clearBtn);

        card.add(bottomPanel, BorderLayout.SOUTH);

        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示导出CSV
     */
    public void showExport() {
        titleLabel.setText("导出CSV");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel exportTitle = new JLabel("📤 导出员工数据为 CSV 格式");
        exportTitle.setFont(Theme.FONT_H2);
        exportTitle.setForeground(Theme.TEXT_PRIMARY);
        exportTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(exportTitle);
        content.add(Box.createVerticalStrut(12));

        JLabel desc = new JLabel("将当前所有员工数据导出为 CSV 文件，可用 Excel 打开");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.TEXT_SECONDARY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(8));

        JLabel countLabel = new JLabel("当前员工总数：" + service.getAllEmployees().size() + " 人");
        countLabel.setFont(Theme.FONT_H3);
        countLabel.setForeground(Theme.ACCENT_PRIMARY);
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(countLabel);
        content.add(Box.createVerticalStrut(24));

        JButton exportBtn = Theme.createButton("📊 导出 CSV 文件");
        exportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportBtn.addActionListener(e -> exportCSV());
        content.add(exportBtn);

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void exportCSV() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("导出 CSV 文件");
        fileChooser.setSelectedFile(new java.io.File("employees_export.csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), StandardCharsets.UTF_8))) {

                // BOM for Excel
                writer.write('\uFEFF');
                writer.write("工号,姓名,部门,联系方式,入职日期,薪资,绩效");
                writer.newLine();

                for (Employee emp : service.getAllEmployees()) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%.2f,%s",
                            emp.getId(),
                            emp.getName(),
                            emp.getDepartment(),
                            emp.getContact(),
                            emp.getHireDate() != null ? emp.getHireDate().toString() : "",
                            emp.getSalary(),
                            emp.getPerformance() != null ? emp.getPerformance().name() : "C"
                    ));
                    writer.newLine();
                }

                logService.addLog("导出CSV：" + path);
                JOptionPane.showMessageDialog(this, "✅ CSV 导出成功！\n文件: " + path, "导出成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 显示批量导入
     */
    public void showImport() {
        titleLabel.setText("批量导入");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel importTitle = new JLabel("📥 批量导入员工数据");
        importTitle.setFont(Theme.FONT_H2);
        importTitle.setForeground(Theme.TEXT_PRIMARY);
        importTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(importTitle);
        content.add(Box.createVerticalStrut(12));

        JLabel desc = new JLabel("从 CSV 或 TXT 文件批量导入员工数据");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.TEXT_SECONDARY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(8));

        JLabel formatLabel = new JLabel("格式要求：工号,姓名,部门,联系方式,入职日期,绩效,类型,薪资参数...");
        formatLabel.setFont(Theme.FONT_SMALL);
        formatLabel.setForeground(Theme.TEXT_MUTED);
        formatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(formatLabel);
        content.add(Box.createVerticalStrut(24));

        JButton importBtn = Theme.createButton("📥 选择文件导入");
        importBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        importBtn.addActionListener(e -> importData());
        content.add(importBtn);

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void importData() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("选择导入文件");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "数据文件 (*.csv, *.txt)", "csv", "txt"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                int count = service.importFromFile(path);
                logService.addLog("批量导入：" + path + "，导入" + count + "条");
                JOptionPane.showMessageDialog(this, "✅ 成功导入 " + count + " 条员工记录！", "导入成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 显示数据备份
     */
    public void showBackup() {
        titleLabel.setText("数据备份");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel backupTitle = new JLabel("💾 数据备份");
        backupTitle.setFont(Theme.FONT_H2);
        backupTitle.setForeground(Theme.TEXT_PRIMARY);
        backupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(backupTitle);
        content.add(Box.createVerticalStrut(12));

        JLabel desc = new JLabel("将当前数据备份到 backup 目录，备份文件以时间戳命名");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.TEXT_SECONDARY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(8));

        JLabel countLabel = new JLabel("当前员工总数：" + service.getAllEmployees().size() + " 人");
        countLabel.setFont(Theme.FONT_H3);
        countLabel.setForeground(Theme.ACCENT_PRIMARY);
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(countLabel);
        content.add(Box.createVerticalStrut(24));

        JButton backupBtn = Theme.createButton("📦 执行备份");
        backupBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backupBtn.addActionListener(e -> {
            String result = service.backupData();
            if (result != null) {
                logService.addLog("数据备份：" + result);
                JOptionPane.showMessageDialog(this, "✅ 数据备份成功！\n备份文件: " + result, "备份成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ 备份失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(backupBtn);

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * 显示恢复备份
     */
    public void showRestore() {
        titleLabel.setText("恢复备份");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel restoreTitle = new JLabel("🔄 恢复备份");
        restoreTitle.setFont(Theme.FONT_H2);
        restoreTitle.setForeground(Theme.TEXT_PRIMARY);
        restoreTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(restoreTitle);
        content.add(Box.createVerticalStrut(12));

        JLabel desc = new JLabel("从备份文件恢复数据，当前数据将被覆盖");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.ACCENT_RED);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(8));

        // 列出备份文件
        File backupDir = new File("backup");
        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".dat"));
            if (backups != null && backups.length > 0) {
                JLabel listTitle = new JLabel("可用的备份文件：");
                listTitle.setFont(Theme.FONT_H3);
                listTitle.setForeground(Theme.TEXT_SECONDARY);
                listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(listTitle);
                content.add(Box.createVerticalStrut(8));

                JPanel listPanel = new JPanel();
                listPanel.setOpaque(false);
                listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

                for (File f : backups) {
                    JLabel fileLabel = new JLabel("  📄 " + f.getName() + " (" + f.length() + " bytes)");
                    fileLabel.setFont(Theme.FONT_SMALL);
                    fileLabel.setForeground(Theme.TEXT_MUTED);
                    fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    listPanel.add(fileLabel);
                }

                content.add(listPanel);
                content.add(Box.createVerticalStrut(16));
            }
        }

        JButton restoreBtn = Theme.createDangerButton("📂 选择备份文件恢复");
        restoreBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        restoreBtn.addActionListener(e -> restoreData());
        content.add(restoreBtn);

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void restoreData() {
        JFileChooser fileChooser = new JFileChooser("backup");
        fileChooser.setDialogTitle("选择备份文件");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("备份文件 (*.dat)", "dat"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (service.restoreFromBackup(path)) {
                logService.addLog("恢复备份：" + path);
                JOptionPane.showMessageDialog(this, "✅ 数据恢复成功！", "恢复成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ 数据恢复失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 显示修改密码
     */
    public void showPassword() {
        titleLabel.setText("修改密码");
        contentArea.removeAll();

        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel pwdTitle = new JLabel("🔑 修改登录密码");
        pwdTitle.setFont(Theme.FONT_H2);
        pwdTitle.setForeground(Theme.TEXT_PRIMARY);
        pwdTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(pwdTitle);
        content.add(Box.createVerticalStrut(20));

        // 当前密码
        JPanel oldPwdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        oldPwdPanel.setOpaque(false);
        oldPwdPanel.add(new JLabel("当前密码：") {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
        JPasswordField oldPwdField = new JPasswordField(15);
        stylePasswordField(oldPwdField);
        oldPwdPanel.add(oldPwdField);
        content.add(oldPwdPanel);
        content.add(Box.createVerticalStrut(12));

        // 新密码
        JPanel newPwdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        newPwdPanel.setOpaque(false);
        newPwdPanel.add(new JLabel("新密码：") {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
        JPasswordField newPwdField = new JPasswordField(15);
        stylePasswordField(newPwdField);
        newPwdPanel.add(newPwdField);
        content.add(newPwdPanel);
        content.add(Box.createVerticalStrut(12));

        // 确认新密码
        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        confirmPanel.setOpaque(false);
        confirmPanel.add(new JLabel("确认密码：") {{ setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_SECONDARY); }});
        JPasswordField confirmField = new JPasswordField(15);
        stylePasswordField(confirmField);
        confirmPanel.add(confirmField);
        content.add(confirmPanel);
        content.add(Box.createVerticalStrut(24));

        JButton saveBtn = Theme.createButton("💾 保存密码");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String oldPwd = new String(oldPwdField.getPassword());
            String newPwd = new String(newPwdField.getPassword());
            String confirmPwd = new String(confirmField.getPassword());

            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                JOptionPane.showMessageDialog(this, "两次输入的新密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 验证旧密码
            String currentPwd = readPasswordFromConfig();
            if (!oldPwd.equals(currentPwd)) {
                JOptionPane.showMessageDialog(this, "当前密码错误", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 保存新密码
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("config.txt"), StandardCharsets.UTF_8))) {
                writer.write(newPwd);
                writer.newLine();
                writer.write("zh");
                logService.addLog("修改密码");
                JOptionPane.showMessageDialog(this, "✅ 密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                oldPwdField.setText("");
                newPwdField.setText("");
                confirmField.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(saveBtn);

        card.add(content, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(Theme.FONT_BODY);
        field.setForeground(Theme.TEXT_PRIMARY);
        field.setBackground(Theme.BG_INPUT);
        field.setCaretColor(Theme.ACCENT_PRIMARY);
        field.setEchoChar('●');
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private String readPasswordFromConfig() {
        String password = "admin123";
        File configFile = new File("config.txt");
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(configFile), StandardCharsets.UTF_8))) {
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
}
