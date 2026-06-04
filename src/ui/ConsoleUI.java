package ui;

import model.Employee;
import model.FullTimeEmployee;
import model.LeaveType;
import model.Manager;
import model.PartTimeEmployee;
import model.Performance;
import service.EmployeeService;
import service.LanguageService;
import service.LogService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static ui.ColorUtil.*;

/**
 * 控制台交互界面（美化版）
 * <p>
 * 所在层次：用户界面层（UI），作为系统的前端交互入口
 * 职责：提供员工管理系统的菜单式命令行操作界面，处理用户输入并调用服务层方法
 *       使用 ANSI 颜色和 Unicode 边框字符美化展示，支持中英文双语切换
 * 关联类：EmployeeService（调用业务逻辑）、LogService（展示操作日志）、
 *        LanguageService（多语言支持）、ColorUtil（颜色工具）、
 *        Employee 及其子类（数据展示）
 */
public class ConsoleUI {

    /** 员工服务实例，用于调用所有业务逻辑方法 */
    private final EmployeeService service;
    /** 日志服务实例，用于记录和展示操作日志 */
    private final LogService logService;
    /** 扫描器实例，用于读取用户键盘输入 */
    private final Scanner sc;
    /** 语言服务实例，用于支持中英文双语切换 */
    private final LanguageService lang;

    /**
     * 构造方法
     * 初始化控制台界面所需的各个服务组件
     *
     * @param service    员工服务实例
     * @param logService 日志服务实例
     * @param sc         键盘输入扫描器
     * @param lang       语言服务实例
     */
    public ConsoleUI(EmployeeService service, LogService logService, Scanner sc, LanguageService lang) {
        this.service = service;
        this.logService = logService;
        this.sc = sc;
        this.lang = lang;
    }

    /**
     * 启动主菜单循环
     * 持续显示主菜单并处理用户选择，直到用户选择退出（99号选项）
     * 每次选择后调用对应的功能方法，执行完毕后暂停等待用户按回车继续
     * 退出时自动备份数据
     */
    public void start() {
        while (true) {
            // 打印主菜单界面
            printMainMenu();

            int choice;
            try {
                // 读取用户输入的菜单选项编号
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                // 输入不是数字，提示格式错误
                System.out.println(error(lang.get("error.format")));
                pause();
                continue;
            }

            // 特殊处理：0号菜单项用于切换语言
            if (choice == 0) {
                String switchLangText = lang.get("menu.switch_lang");
                // 检查菜单文本是否以"0."开头，确认是切换语言选项
                if (switchLangText.startsWith("0.")) {
                    // 执行语言切换
                    lang.switchLang();
                    System.out.println(success(lang.get("other.lang_switched") +
                            ("zh".equals(lang.getCurrentLang()) ? "中文" : "English")));
                    // 将语言设置保存到 config.txt 配置文件
                    saveLanguageConfig();
                    pause();
                    continue;
                }
            }

            // 根据用户选择的菜单编号执行对应功能
            switch (choice) {
                case 1:
                    addEmployee();          // 添加员工
                    break;
                case 2:
                    removeEmployee();       // 删除员工
                    break;
                case 3:
                    updateEmployee();       // 修改员工信息
                    break;
                case 4:
                    queryEmployee();        // 查询员工
                    break;
                case 5:
                    listAllEmployees();     // 浏览全部员工（分页）
                    break;
                case 6:
                    calculateSalary();      // 计算薪资
                    break;
                case 7:
                    deptStats();            // 部门统计
                    break;
                case 8:
                    salarySort();           // 薪资排序
                    break;
                case 9:
                    salaryFilter();         // 薪资筛选
                    break;
                case 10:
                    printLogs();            // 打印操作日志
                    break;
                case 11:
                    setPerformance();       // 设置绩效
                    break;
                case 12:
                    exportCsv();            // 导出 CSV
                    break;
                case 13:
                    salaryBarChart();       // 薪资柱状图
                    break;
                case 14:
                    service.undo();         // 撤销操作
                    break;
                case 15:
                    salaryChampion();       // 薪资冠军
                    break;
                case 16:
                    salaryManagement();     // 涨薪管理
                    break;
                case 17:
                    applyLeave();           // 请假扣薪
                    break;
                case 18:
                    changePassword();       // 修改密码
                    break;
                case 19:
                    backupData();           // 数据备份
                    break;
                case 20:
                    transferEmployee();     // 员工转岗
                    break;
                case 21:
                    restoreFromBackup();    // 从备份恢复
                    break;
                case 22:
                    importEmployee();       // 批量导入员工
                    break;
                case 23:
                    generateMonthlyReportUI(); // 生成月度报表
                    break;
                case 24:
                    hireTrendChart();       // 入职趋势图
                    break;
                case 99:
                    // 退出系统：退出前自动备份数据
                    String backupFile = service.backupData();
                    if (backupFile != null) {
                        System.out.println(success(lang.get("success.auto_backup") + backupFile));
                    }
                    // 打印退出画面
                    printExitScreen();
                    return;

                default:
                    // 无效的菜单选项
                    System.out.println(error(lang.get("error.invalid_choice")));
            }
            // 非退出选项执行完毕后暂停，等待用户按回车继续
            if (choice != 0 && choice != 99) {
                pause();
            }
        }
    }

    /**
     * 保存语言设置到配置文件
     * 读取 config.txt 中的密码，然后调用 LanguageService 保存语言设置
     * 如果读取密码失败则使用默认密码 "admin123"
     */
    private void saveLanguageConfig() {
        try {
            // 默认密码
            String password = "admin123";
            File configFile = new File("config.txt");
            // 如果配置文件存在，读取其中的密码
            if (configFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile, StandardCharsets.UTF_8))) {
                    String pwd = reader.readLine();
                    if (pwd != null && !pwd.trim().isEmpty()) {
                        password = pwd.trim();
                    }
                } catch (IOException e) {
                    // 读取失败则使用默认密码
                }
            }
            // 保存语言设置到配置文件
            lang.saveLangToConfig("config.txt", password);
        } catch (Exception e) {
            // 保存失败则静默忽略
        }
    }

    /**
     * 打印主菜单 - Dashboard 风格
     * 使用双线边框、卡片化布局、分类菜单
     * 顶部：ASCII 艺术字大标题
     * 中部：状态信息面板（用户/时间/人数）
     * 底部：分类菜单区域（左右分栏）
     */
    private void printMainMenu() {
        // 清屏
        clearScreen();

        // ==================== 顶部：ASCII 艺术字大标题 ====================
        System.out.println();
        System.out.println(style(getAsciiTitle(), BOLD, BRIGHT_CYAN));
        System.out.println(style(getSubtitle(), BOLD, BRIGHT_BLUE));
        System.out.println();

        // ==================== 中部：状态信息面板 ====================
        int panelWidth = 74;
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int totalEmployees = service.getAllEmployees().size();
        String adminName = "Administrator";

        // 状态面板顶部
        System.out.println("  " + style(D_TL + repeat(D_H, panelWidth - 2) + D_TR, BRIGHT_CYAN));
        
        // 状态面板标题行
        String statusTitle = style(" 📊 ", BOLD) + style("SYSTEM DASHBOARD", BOLD, BRIGHT_WHITE) + style(" 系统状态面板", BOLD, BRIGHT_CYAN);
        System.out.println("  " + style(D_V + "  ", BRIGHT_CYAN) + 
                style(padLeft(statusTitle, panelWidth - 6), BRIGHT_WHITE) + 
                style("  " + D_V, BRIGHT_CYAN));

        // 分隔线
        System.out.println("  " + style(D_L + repeat(D_H, panelWidth - 2) + D_R, BRIGHT_CYAN));

        // 状态信息行 - 三列布局
        String userInfo = style("👤 ", BRIGHT_WHITE) + style("用户: ", WHITE) + style(adminName, BOLD, BRIGHT_GREEN);
        String timeInfo = style("🕐 ", BRIGHT_WHITE) + style("时间: ", WHITE) + style(currentTime, BOLD, BRIGHT_YELLOW);
        String empInfo = style("👥 ", BRIGHT_WHITE) + style("员工: ", WHITE) + style(totalEmployees + " 人", BOLD, BRIGHT_GREEN);

        // 计算每列宽度
        int colWidth = (panelWidth - 8) / 3;
        String col1 = padLeft(userInfo, colWidth);
        String col2 = padLeft(timeInfo, colWidth);
        String col3 = padLeft(empInfo, colWidth);

        System.out.println("  " + style(D_V + "  ", BRIGHT_CYAN) + 
                col1 + style("  " + D_V + "  ", BRIGHT_CYAN) + 
                col2 + style("  " + D_V + "  ", BRIGHT_CYAN) + 
                col3 + style("  " + D_V, BRIGHT_CYAN));

        // 状态面板底部
        System.out.println("  " + style(D_BL + repeat(D_H, panelWidth - 2) + D_BR, BRIGHT_CYAN));
        System.out.println();

        // ==================== 底部：分类菜单区域 ====================
        // 定义菜单分类
        String[][] menuCategories = {
            {"👥 员工管理", "EMPLOYEE MANAGEMENT", BRIGHT_GREEN},
            {"💰 薪资模块", "SALARY MODULE", BRIGHT_YELLOW},
            {"⚙ 系统功能", "SYSTEM FUNCTIONS", BRIGHT_BLUE},
            {"📊 高级功能", "ADVANCED FEATURES", BRIGHT_PURPLE}
        };

        // 每个分类的菜单项
        String[][][] categoryItems = {
            // 员工管理
            {
                {"1", "添加员工", "Add Employee", BRIGHT_CYAN},
                {"2", "删除员工", "Delete Employee", BRIGHT_RED},
                {"3", "修改员工", "Update Employee", BRIGHT_YELLOW},
                {"4", "查询员工", "Query Employee", BRIGHT_BLUE},
                {"5", "浏览全部", "List All", BRIGHT_GREEN}
            },
            // 薪资模块
            {
                {"6", "计算薪资", "Calculate Salary", BRIGHT_CYAN},
                {"8", "薪资排序", "Salary Sort", BRIGHT_YELLOW},
                {"9", "薪资筛选", "Salary Filter", BRIGHT_BLUE},
                {"15", "薪资冠军", "Salary Champion", BRIGHT_PURPLE},
                {"16", "涨薪管理", "Salary Raise", BRIGHT_GREEN}
            },
            // 系统功能
            {
                {"10", "操作日志", "Operation Logs", BRIGHT_CYAN},
                {"18", "修改密码", "Change Password", BRIGHT_YELLOW},
                {"19", "数据备份", "Data Backup", BRIGHT_BLUE},
                {"21", "从备份恢复", "Restore Backup", BRIGHT_PURPLE},
                {"12", "导出CSV", "Export CSV", BRIGHT_GREEN}
            },
            // 高级功能
            {
                {"13", "薪资柱状图", "Salary Chart", BRIGHT_CYAN},
                {"24", "入职趋势图", "Hire Trend", BRIGHT_YELLOW},
                {"7", "部门统计", "Dept Statistics", BRIGHT_BLUE},
                {"22", "批量导入", "Batch Import", BRIGHT_PURPLE},
                {"23", "月度报表", "Monthly Report", BRIGHT_GREEN}
            }
        };

        // 打印菜单面板 - 两行两列（2x2 卡片布局）
        int cardWidth = 36;
        int cardHeight = 8;

        for (int row = 0; row < 2; row++) {
            // 打印两个并排的卡片
            for (int cardRow = 0; cardRow < 2; cardRow++) {
                // 左卡片
                int leftIdx = row * 2;
                int rightIdx = row * 2 + 1;

                if (cardRow == 0) {
                    // 卡片顶部边框
                    System.out.print("  " + style(D_TL + repeat(D_H, cardWidth) + D_T, BRIGHT_CYAN));
                    System.out.println(style(repeat(D_H, cardWidth) + D_TR, BRIGHT_CYAN));
                } else {
                    // 卡片分隔线
                    System.out.print("  " + style(D_L + repeat(D_H, cardWidth) + D_C, BRIGHT_CYAN));
                    System.out.println(style(repeat(D_H, cardWidth) + D_R, BRIGHT_CYAN));
                }

                // 卡片标题行
                String leftTitle = style(menuCategories[leftIdx][0], BOLD, menuCategories[leftIdx][2]);
                String rightTitle = style(menuCategories[rightIdx][0], BOLD, menuCategories[rightIdx][2]);
                System.out.print("  " + style(D_V + " ", BRIGHT_CYAN) + 
                        style(padLeft(leftTitle, cardWidth - 1), BRIGHT_WHITE) + style(D_V + " ", BRIGHT_CYAN));
                System.out.println(style(padLeft(rightTitle, cardWidth - 1), BRIGHT_WHITE) + style(D_V, BRIGHT_CYAN));

                // 卡片内容行
                for (int itemRow = 0; itemRow < 5; itemRow++) {
                    // 左卡片菜单项
                    String[] leftItem = categoryItems[leftIdx][itemRow];
                    String leftItemStr = style(leftItem[0] + ".", BOLD, BRIGHT_YELLOW) + 
                            style(" " + leftItem[1], leftItem[3]);
                    // 英文名用小字显示
                    String leftEnStr = style(leftItem[2], DIM, WHITE);
                    String leftContent = leftItemStr + " " + leftEnStr;
                    
                    // 右卡片菜单项
                    String[] rightItem = categoryItems[rightIdx][itemRow];
                    String rightItemStr = style(rightItem[0] + ".", BOLD, BRIGHT_YELLOW) + 
                            style(" " + rightItem[1], rightItem[3]);
                    String rightEnStr = style(rightItem[2], DIM, WHITE);
                    String rightContent = rightItemStr + " " + rightEnStr;

                    System.out.print("  " + style(D_V + " ", BRIGHT_CYAN) + 
                            style(padLeft(leftContent, cardWidth - 1), WHITE) + style(D_V + " ", BRIGHT_CYAN));
                    System.out.println(style(padLeft(rightContent, cardWidth - 1), WHITE) + style(D_V, BRIGHT_CYAN));
                }
            }

            // 卡片底部边框
            System.out.print("  " + style(D_BL + repeat(D_H, cardWidth) + D_B, BRIGHT_CYAN));
            System.out.println(style(repeat(D_H, cardWidth) + D_BR, BRIGHT_CYAN));
            System.out.println();
        }

        // ==================== 底部操作栏 ====================
        System.out.println("  " + style(D_TL + repeat(D_H, panelWidth - 2) + D_TR, BRIGHT_CYAN));
        
        // 快捷操作行
        String switchLang = style(" 0. ", BOLD, BRIGHT_YELLOW) + style(lang.get("menu.switch_lang_text"), BRIGHT_CYAN);
        String exit = style(" 99. ", BOLD, BRIGHT_RED) + style(lang.get("menu.exit_text"), BRIGHT_RED);
        String prompt = style(" " + lang.get("prompt.choose"), BOLD, BRIGHT_WHITE);
        
        String bottomContent = padLeft(switchLang + "    " + exit, 50) + prompt;
        System.out.println("  " + style(D_V + " ", BRIGHT_CYAN) + 
                style(padLeft(bottomContent, panelWidth - 4), WHITE) + 
                style(" " + D_V, BRIGHT_CYAN));
        
        System.out.println("  " + style(D_BL + repeat(D_H, panelWidth - 2) + D_BR, BRIGHT_CYAN));
        
        // 打印提示
        System.out.print(style("\n  " + lang.get("prompt.choose"), BOLD, BRIGHT_CYAN));
    }
    // ====================== 表格列宽常量 ======================
    // 以下常量定义了表格各列的内容可见宽度和边框宽度（内容宽度 + 左右各1个空格）

    /** 排名列的内容可见宽度 */
    private static final int W_RANK   = 5;
    /** 工号列的内容可见宽度 */
    private static final int W_ID     = 10;
    /** 姓名列的内容可见宽度 */
    private static final int W_NAME   = 8;
    /** 部门列的内容可见宽度 */
    private static final int W_DEPT   = 12;
    /** 类型列的内容可见宽度 */
    private static final int W_TYPE   = 6;
    /** 绩效列的内容可见宽度 */
    private static final int W_PERF   = 4;
    /** 工龄列的内容可见宽度 */
    private static final int W_YEARS  = 6;
    /** 薪资列的内容可见宽度 */
    private static final int W_SALARY = 10;
    /** 排名列的边框宽度（内容宽度 + 左右各1个空格） */
    private static final int B_RANK   = W_RANK   + 2;
    /** 工号列的边框宽度 */
    private static final int B_ID     = W_ID     + 2;
    /** 姓名列的边框宽度 */
    private static final int B_NAME   = W_NAME   + 2;
    /** 部门列的边框宽度 */
    private static final int B_DEPT   = W_DEPT   + 2;
    /** 类型列的边框宽度 */
    private static final int B_TYPE   = W_TYPE   + 2;
    /** 绩效列的边框宽度 */
    private static final int B_PERF   = W_PERF   + 2;
    /** 工龄列的边框宽度 */
    private static final int B_YEARS  = W_YEARS  + 2;
    /** 薪资列的边框宽度 */
    private static final int B_SALARY = W_SALARY + 2;
    /** 分页大小，每页显示的员工数量 */
    private static final int PAGE_SIZE = 10;

    /**
     * 获取字符串的可见长度
     * 中文字符算 2 个宽度，英文字符算 1 个宽度
     * 用于表格对齐计算
     *
     * @param s 要计算长度的字符串
     * @return 字符串的可视宽度
     */
    private int getVisibleLength(String s) {
        int len = 0;
        for (char c : s.toCharArray()) {
            if (c >= '\u4e00' && c <= '\u9fff') {
                len += 2;
            } else {
                len += 1;
            }
        }
        return len;
    }

    /**
     * 将字符串左对齐填充到指定可视宽度
     * 中文字符算 2 个宽度，英文字符算 1 个宽度
     * 用于表格中对齐各列数据
     *
     * @param str   要填充的字符串
     * @param width 目标可视宽度
     * @return 填充后的字符串
     */
    private String padChinese(String str, int width) {
        int len = 0;
        for (char c : str.toCharArray()) {
            len += (c > 127) ? 2 : 1;
        }
        int pad = width - len;
        if (pad <= 0) return str;
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < pad; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * 将字符串左对齐填充到指定可视宽度
     * 使用 getVisibleLength 计算实际宽度
     *
     * @param text  要填充的字符串
     * @param width 目标可视宽度
     * @return 填充后的字符串
     */
    private String padToWidth(String text, int width) {
        int visLen = getVisibleLength(text);
        if (visLen >= width) return text;
        int padding = width - visLen;
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < padding; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * 将字符串右对齐填充到指定可视宽度
     * 用于薪资等数字列的右对齐显示
     *
     * @param text  要填充的字符串
     * @param width 目标可视宽度
     * @return 填充后的字符串（左侧补空格）
     */
    private String padToWidthRight(String text, int width) {
        int visLen = getVisibleLength(text);
        if (visLen >= width) return text;
        int padding = width - visLen;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) sb.append(' ');
        sb.append(text);
        return sb.toString();
    }

    /**
     * 填充员工类型并应用样式（左对齐）
     * 根据员工类型使用不同的颜色：
     * - 管理层：紫色
     * - 全职：蓝色
     * - 兼职：青色
     *
     * @param type  员工类型字符串
     * @param width 目标宽度
     * @return 带颜色样式的填充字符串
     */
    private String formatTypePadded(String type, int width) {
        String padded = padChinese(type, width);
        switch (type) {
            case "管理层": return style(padded, BOLD, BRIGHT_PURPLE);
            case "全职":   return style(padded, BOLD, BRIGHT_BLUE);
            case "兼职":   return style(padded, BOLD, BRIGHT_CYAN);
            case "Management": return style(padded, BOLD, BRIGHT_PURPLE);
            case "Full-Time":   return style(padded, BOLD, BRIGHT_BLUE);
            case "Part-Time":   return style(padded, BOLD, BRIGHT_CYAN);
            default:       return padded;
        }
    }

    /**
     * 填充绩效并应用样式（居中）
     * 根据绩效等级使用不同的背景色：
     * - A级：绿色背景
     * - B级：黄色背景
     * - C级：红色背景
     *
     * @param p     绩效枚举值
     * @param width 目标宽度
     * @return 带颜色样式的填充字符串
     */
    private String formatPerformancePadded(Enum<?> p, int width) {
        if (p == null) return padToWidth("N/A", width);
        String name = p.name();
        String base = " " + name + " ";
        String padded = padToWidth(base, width);
        switch (name) {
            case "A": return style(padded, BG_GREEN, BOLD, WHITE);
            case "B": return style(padded, BG_YELLOW, BOLD, BLACK);
            case "C": return style(padded, BG_RED, BOLD, WHITE);
            default:  return padded;
        }
    }

    /**
     * 格式化工龄为 "X年" 并应用样式
     *
     * @param e     员工对象
     * @param width 目标宽度
     * @return 带黄色样式的工龄字符串
     */
    private String formatYearsPadded(Employee e, int width) {
        long years = e.getYearsOfService();
        String text = years + lang.get("other.unit_years");
        return style(padChinese(text, width), BRIGHT_YELLOW);
    }

    /**
     * 填充薪资并应用样式（右对齐）
     * 薪资显示为绿色，保留两位小数
     *
     * @param amount 薪资金额
     * @param width  目标宽度
     * @return 带绿色样式的右对齐薪资字符串
     */
    private String formatMoneyPadded(double amount, int width) {
        String value = String.format("%.2f", amount);
        String padded = padToWidthRight(value, width);
        return style(padded, BRIGHT_GREEN);
    }

    /**
     * 暂停等待用户按回车
     * 在每个功能执行完毕后调用，让用户有时间查看结果
     */
    private void pause() {
        System.out.print(style("\n" + lang.get("prompt.pause"), DIM, CYAN));
        sc.nextLine();
    }

    /**
     * 打印子页面标题
     * 使用青色边框包围标题文字
     *
     * @param title 子页面标题文字
     */
    private void printSubTitle(String title) {
        System.out.println();
        System.out.println(style(" " + LIGHT_TL + repeat(LIGHT_H, 50) + LIGHT_TR + " ", CYAN));
        System.out.println(style(" " + LIGHT_V + "  " + title, BOLD, BRIGHT_CYAN));
        System.out.println(style(" " + LIGHT_L + repeat(LIGHT_H, 50) + LIGHT_R + " ", CYAN));
    }

    /**
     * 打印表头行（7列：工号/姓名/部门/类型/绩效/工龄/薪资）
     * 表头文字使用粗体白色显示
     */
    private void printTableHeader() {
        String hId   = padChinese(lang.get("table.id"), W_ID);
        String hName = padChinese(lang.get("table.name"), W_NAME);
        String hDept = padChinese(lang.get("table.dept"), W_DEPT);
        String hType = padChinese(lang.get("table.type"), W_TYPE);
        String hPerf = padChinese(lang.get("table.performance"), W_PERF);
        String hYear = padChinese(lang.get("table.years"), W_YEARS);
        String hSal  = padChinese(lang.get("table.salary"), W_SALARY);

        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hId, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hName, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hDept, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hType, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hPerf, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hYear, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hSal, BOLD, BRIGHT_WHITE) + " ");
        System.out.println(style(LIGHT_V, CYAN));
    }

    /**
     * 打印带排名的表头行（8列：排名/工号/姓名/部门/类型/绩效/工龄/薪资）
     * 用于薪资排序等需要显示排名的场景
     */
    private void printTableHeaderWithRank() {
        String hRank = padChinese(lang.get("table.rank"), W_RANK);
        String hId   = padChinese(lang.get("table.id"), W_ID);
        String hName = padChinese(lang.get("table.name"), W_NAME);
        String hDept = padChinese(lang.get("table.dept"), W_DEPT);
        String hType = padChinese(lang.get("table.type"), W_TYPE);
        String hPerf = padChinese(lang.get("table.performance"), W_PERF);
        String hYear = padChinese(lang.get("table.years"), W_YEARS);
        String hSal  = padChinese(lang.get("table.salary"), W_SALARY);

        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hRank, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hId, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hName, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hDept, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hType, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hPerf, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hYear, BOLD, BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(hSal, BOLD, BRIGHT_WHITE) + " ");
        System.out.println(style(LIGHT_V, CYAN));
    }

    /**
     * 打印员工表格行（带排名）
     * 显示排名、工号、姓名、部门、类型、绩效、工龄、薪资
     * 各列使用不同的颜色样式
     *
     * @param rank 排名序号
     * @param e    员工对象
     */
    private void printTableRow(int rank, Employee e) {
        String type = getEmployeeType(e);
        String r = padChinese(String.valueOf(rank), W_RANK);
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(r, BRIGHT_YELLOW) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(padChinese(e.getId(), W_ID), WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(padChinese(e.getName(), W_NAME), BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(padChinese(e.getDepartment(), W_DEPT), WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatTypePadded(type, W_TYPE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatPerformancePadded(e.getPerformance(), W_PERF) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatYearsPadded(e, W_YEARS) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatMoneyPadded(e.getSalary(), W_SALARY) + " ");
        System.out.println(style(LIGHT_V, CYAN));
    }

    /**
     * 打印简单员工行（无排名）
     * 显示工号、姓名、部门、类型、绩效、工龄、薪资
     *
     * @param e 员工对象
     */
    private void printSimpleRow(Employee e) {
        String type = getEmployeeType(e);
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(padChinese(e.getId(), W_ID), WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(padChinese(e.getName(), W_NAME), BRIGHT_WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(style(padChinese(e.getDepartment(), W_DEPT), WHITE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatTypePadded(type, W_TYPE) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatPerformancePadded(e.getPerformance(), W_PERF) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatYearsPadded(e, W_YEARS) + " ");
        System.out.print(style(LIGHT_V, CYAN));
        System.out.print(formatMoneyPadded(e.getSalary(), W_SALARY) + " ");
        System.out.println(style(LIGHT_V, CYAN));
    }

    /**
     * 获取员工类型的中文/英文名称
     * 通过 instanceof 判断员工的具体子类类型
     *
     * @param e 员工对象
     * @return 员工类型字符串（管理层/全职/兼职）
     */
    private String getEmployeeType(Employee e) {
        if (e instanceof Manager) return lang.get("type.management");
        if (e instanceof FullTimeEmployee) return lang.get("type.fulltime");
        if (e instanceof PartTimeEmployee) return lang.get("type.parttime");
        return lang.get("type.other");
    }

    /**
     * 打印员工列表（表格形式，7列：工号/姓名/部门/类型/绩效/工龄/薪资）
     * 使用 Unicode 边框字符绘制表格，各列使用不同颜色
     *
     * @param employees 要展示的员工列表
     */
    private void printEmployeeList(List<Employee> employees) {
        // 打印上边框
        System.out.println(style(LIGHT_TL, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_TR, CYAN));

        // 打印表头
        printTableHeader();

        // 打印分隔线
        System.out.println(style(LIGHT_L, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_R, CYAN));

        // 逐行打印员工数据
        for (Employee emp : employees) {
            printSimpleRow(emp);
        }

        // 打印下边框
        System.out.println(style(LIGHT_BL, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_BR, CYAN));
    }

    /**
     * 添加员工
     * 引导用户选择员工类型（全职/兼职/经理），输入基本信息后调用服务层添加
     * 支持输入入职日期，格式为 yyyy-MM-dd
     */
    private void addEmployee() {
        printSubTitle(lang.get("title.add"));

        // 显示员工类型选择菜单
        System.out.println("  " + lang.get("prompt.choose.type"));
        System.out.println("    " + style(lang.get("type.option.fulltime"), BRIGHT_BLUE));
        System.out.println("    " + style(lang.get("type.option.parttime"), BRIGHT_CYAN));
        System.out.println("    " + style(lang.get("type.option.manager"), BRIGHT_PURPLE));
        System.out.print(prompt("  " + lang.get("prompt.choose.type.prompt")));
        int type;
        try {
            type = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(error(lang.get("error.format.number")));
            return;
        }

        // 输入员工基本信息
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        System.out.print(prompt("  " + lang.get("prompt.input.name")));
        String name = sc.nextLine();
        System.out.print(prompt("  " + lang.get("prompt.input.dept")));
        String dept = sc.nextLine();
        System.out.print(prompt("  " + lang.get("prompt.input.contact")));
        String contact = sc.nextLine();

        // 输入入职日期，循环直到输入合法格式
        LocalDate hireDate = null;
        while (hireDate == null) {
            System.out.print(prompt("  " + lang.get("prompt.input.hire_date")));
            String dateStr = sc.nextLine();
            try {
                hireDate = LocalDate.parse(dateStr);
            } catch (Exception ex) {
                System.out.println(error(lang.get("error.format.date")));
            }
        }

        // 根据选择的类型创建对应的员工对象
        Employee e;
        try {
            switch (type) {
                case 1:
                    // 全职员工：需要输入基本工资
                    System.out.print(prompt("  " + lang.get("prompt.input.base_salary")));
                    double baseSalary = Double.parseDouble(sc.nextLine());
                    e = new FullTimeEmployee(id, name, dept, contact, baseSalary, hireDate);
                    break;
                case 2:
                    // 兼职员工：需要输入时薪和工作小时数
                    System.out.print(prompt("  " + lang.get("prompt.input.hourly_rate")));
                    double hourlyRate = Double.parseDouble(sc.nextLine());
                    System.out.print(prompt("  " + lang.get("prompt.input.hours")));
                    double hoursWorked = Double.parseDouble(sc.nextLine());
                    e = new PartTimeEmployee(id, name, dept, contact, hourlyRate, hoursWorked, hireDate);
                    break;
                case 3:
                    // 经理：需要输入基本工资和奖金
                    System.out.print(prompt("  " + lang.get("prompt.input.base_salary")));
                    double mgrBase = Double.parseDouble(sc.nextLine());
                    System.out.print(prompt("  " + lang.get("prompt.input.bonus")));
                    double bonus = Double.parseDouble(sc.nextLine());
                    e = new Manager(id, name, dept, contact, mgrBase, bonus, hireDate);
                    break;
                default:
                    System.out.println(error(lang.get("error.invalid_type")));
                    return;
            }
        } catch (NumberFormatException ex) {
            System.out.println(error(lang.get("error.format.number")));
            return;
        }

        // 调用服务层添加员工
        try {
            service.addEmployee(e);
            System.out.println(success(lang.get("success.add")));
        } catch (IllegalArgumentException ex) {
            System.out.println(error(lang.get("error.add_fail") + ex.getMessage()));
        }
    }

    /**
     * 删除员工
     * 先根据工号查找员工并显示信息，二次确认后执行删除
     * 防止误操作
     */
    private void removeEmployee() {
        printSubTitle(lang.get("title.delete"));
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        // 先查找员工，确认是否存在
        Employee e = service.findById(id);
        if (e == null) {
            System.out.println(error(lang.get("error.not_found")));
            return;
        }
        // 显示员工信息供用户确认
        System.out.println("  " + lang.get("info.found") + e.getName());
        System.out.print(prompt("  " + lang.get("prompt.confirm.delete")));
        String confirm = sc.nextLine();
        // 用户输入 y/yes 确认删除
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            service.removeEmployee(id);
            System.out.println(success(lang.get("success.delete")));
        } else {
            System.out.println(info(lang.get("info.cancelled")));
        }
    }

    /**
     * 修改员工信息
     * 先根据工号查找员工，然后引导用户输入新的信息
     * 支持修改姓名、部门、联系方式、入职日期
     * 根据员工类型不同，还可以修改对应的薪资参数
     */
    private void updateEmployee() {
        printSubTitle(lang.get("title.update"));
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        // 查找要修改的员工
        Employee old = service.findById(id);
        if (old == null) {
            System.out.println(error(lang.get("error.not_found")));
            return;
        }

        // 显示当前员工信息
        System.out.println("  " + lang.get("info.current") + old.getName());
        System.out.print(prompt("  " + lang.get("prompt.input.new_name")));
        String name = sc.nextLine();
        System.out.print(prompt("  " + lang.get("prompt.input.new_dept")));
        String dept = sc.nextLine();
        System.out.print(prompt("  " + lang.get("prompt.input.new_contact")));
        String contact = sc.nextLine();

        // 输入新的入职日期
        LocalDate hireDate = null;
        while (hireDate == null) {
            System.out.print(prompt("  " + lang.get("prompt.input.hire_date")));
            String dateStr = sc.nextLine();
            try {
                hireDate = LocalDate.parse(dateStr);
            } catch (Exception ex) {
                System.out.println(error(lang.get("error.format.date")));
            }
        }

        // 根据员工类型创建新的员工对象
        Employee newEmp;
        try {
            if (old instanceof FullTimeEmployee) {
                // 全职员工：需要输入新的基本工资
                System.out.print(prompt("  " + lang.get("prompt.input.base_salary")));
                double baseSalary = Double.parseDouble(sc.nextLine());
                newEmp = new FullTimeEmployee(id, name, dept, contact, baseSalary, hireDate);
            } else if (old instanceof PartTimeEmployee) {
                // 兼职员工：需要输入新的时薪和工作小时数
                System.out.print(prompt("  " + lang.get("prompt.input.hourly_rate")));
                double hourlyRate = Double.parseDouble(sc.nextLine());
                System.out.print(prompt("  " + lang.get("prompt.input.hours")));
                double hoursWorked = Double.parseDouble(sc.nextLine());
                newEmp = new PartTimeEmployee(id, name, dept, contact, hourlyRate, hoursWorked, hireDate);
            } else if (old instanceof Manager) {
                // 经理：需要输入新的基本工资和奖金
                System.out.print(prompt("  " + lang.get("prompt.input.base_salary")));
                double baseSalary = Double.parseDouble(sc.nextLine());
                System.out.print(prompt("  " + lang.get("prompt.input.bonus")));
                double bonus = Double.parseDouble(sc.nextLine());
                newEmp = new Manager(id, name, dept, contact, baseSalary, bonus, hireDate);
            } else {
                System.out.println(error(lang.get("error.invalid_type")));
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println(error(lang.get("error.format.number")));
            return;
        }

        // 复制原员工的绩效等级和请假记录到新对象
        newEmp.setPerformance(old.getPerformance());
        newEmp.setLeaveRecords(old.getLeaveRecords());

        // 调用服务层更新员工
        service.updateEmployee(id, newEmp);
        System.out.println(success(lang.get("success.update")));
    }

    /**
     * 查询员工
     * 提供多种查询方式：按工号、按姓名、按部门、模糊搜索
     * 查询结果以表格形式展示
     */
    private void queryEmployee() {
        printSubTitle(lang.get("title.query"));
        // 显示查询方式菜单
        System.out.println("  " + lang.get("prompt.query.method"));
        System.out.println("    " + style("1. " + lang.get("query.by_id"), BRIGHT_CYAN));
        System.out.println("    " + style("2. " + lang.get("query.by_name"), BRIGHT_CYAN));
        System.out.println("    " + style("3. " + lang.get("query.by_dept"), BRIGHT_CYAN));
        System.out.println("    " + style("4. " + lang.get("query.fuzzy"), BRIGHT_CYAN));
        System.out.print(prompt("  " + lang.get("prompt.choose")));
        int method;
        try {
            method = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(error(lang.get("error.format.number")));
            return;
        }

        List<Employee> result = new ArrayList<>();
        switch (method) {
            case 1:
                // 按工号精确查询
                System.out.print(prompt("  " + lang.get("prompt.input.id")));
                String id = sc.nextLine();
                Employee e = service.findById(id);
                if (e != null) result.add(e);
                break;
            case 2:
                // 按姓名模糊查询
                System.out.print(prompt("  " + lang.get("prompt.input.name")));
                String name = sc.nextLine();
                result = service.findByName(name);
                break;
            case 3:
                // 按部门精确查询
                System.out.print(prompt("  " + lang.get("prompt.input.dept")));
                String dept = sc.nextLine();
                result = service.findByDept(dept);
                break;
            case 4:
                // 模糊搜索（工号/姓名/部门）
                System.out.print(prompt("  " + lang.get("prompt.input.keyword")));
                String keyword = sc.nextLine();
                result = service.fuzzySearch(keyword);
                break;
            default:
                System.out.println(error(lang.get("error.invalid_choice")));
                return;
        }

        // 展示查询结果
        if (result.isEmpty()) {
            System.out.println(warning(lang.get("info.no_result")));
        } else {
            System.out.println("  " + lang.get("info.found_count") + result.size() + lang.get("info.records"));
            printEmployeeList(result);
        }
    }

    /**
     * 设置员工绩效等级
     * 先根据工号查找员工，然后选择绩效等级（A/B/C）
     * 绩效等级会影响薪资计算结果
     */
    private void setPerformance() {
        printSubTitle(lang.get("title.performance"));
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        // 检查员工是否存在
        Employee e = service.findById(id);
        if (e == null) {
            System.out.println(error(lang.get("error.not_found")));
            return;
        }
        // 显示当前绩效
        System.out.println("  " + lang.get("info.current_perf") + (e.getPerformance() == null ? "N/A" : e.getPerformance().name()));
        // 选择新的绩效等级
        System.out.println("  " + lang.get("prompt.choose.perf"));
        System.out.println("    " + style("A - " + lang.get("perf.a"), BG_GREEN, BOLD, WHITE));
        System.out.println("    " + style("B - " + lang.get("perf.b"), BG_YELLOW, BOLD, BLACK));
        System.out.println("    " + style("C - " + lang.get("perf.c"), BG_RED, BOLD, WHITE));
        System.out.print(prompt("  " + lang.get("prompt.choose")));
        String perfStr = sc.nextLine().toUpperCase();
        Performance p;
        switch (perfStr) {
            case "A": p = Performance.A; break;
            case "B": p = Performance.B; break;
            case "C": p = Performance.C; break;
            default:
                System.out.println(error(lang.get("error.invalid_choice")));
                return;
        }
        // 调用服务层设置绩效
        service.setPerformance(id, p);
        System.out.println(success(lang.get("success.perf_set")));
    }

    /**
     * 打印员工分页
     * 将员工列表按页显示，每页 PAGE_SIZE（10）条记录
     * 支持翻页操作（n 下一页 / p 上一页 / q 退出）
     *
     * @param employees 员工列表
     * @param pageSize  每页显示条数
     * @param total     总记录数
     * @param page      当前页码（从1开始）
     * @param totalPages 总页数
     */
    private void printEmployeePage(List<Employee> employees, int pageSize, int total, int page, int totalPages) {
        // 打印上边框
        System.out.println(style(LIGHT_TL, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_TR, CYAN));

        // 打印表头
        printTableHeader();

        // 打印分隔线
        System.out.println(style(LIGHT_L, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_R, CYAN));

        // 计算当前页的起始和结束索引
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        // 打印当前页的员工数据
        for (int i = start; i < end; i++) {
            printSimpleRow(employees.get(i));
        }

        // 打印下边框
        System.out.println(style(LIGHT_BL, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_BR, CYAN));

        // 打印分页信息
        System.out.println("  " + style(lang.get("info.page") + page + "/" + totalPages +
                "  " + lang.get("info.total") + total + lang.get("info.records"), DIM, CYAN));
        System.out.print(prompt("  " + lang.get("prompt.page.nav")));
    }

    /**
     * 浏览全部员工（分页）
     * 获取所有员工列表，按分页方式展示
     * 支持 n（下一页）、p（上一页）、q（退出）操作
     */
    private void listAllEmployees() {
        printSubTitle(lang.get("title.list"));
        List<Employee> all = service.getAllEmployees();
        if (all.isEmpty()) {
            System.out.println(warning(lang.get("info.no_employee")));
            return;
        }

        int total = all.size();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        int page = 1;

        while (true) {
            printEmployeePage(all, PAGE_SIZE, total, page, totalPages);
            String cmd = sc.nextLine().trim().toLowerCase();
            if (cmd.equals("n") && page < totalPages) {
                page++; // 下一页
            } else if (cmd.equals("p") && page > 1) {
                page--; // 上一页
            } else if (cmd.equals("q")) {
                break; // 退出分页
            } else {
                // 无效输入时提示
                if (!cmd.isEmpty()) {
                    System.out.println(error(lang.get("error.invalid_choice")));
                }
            }
        }
    }

    /**
     * 计算并显示员工薪资
     * 根据工号查找员工，调用服务层计算薪资并展示
     * 同时显示员工的基本信息和薪资明细
     */
    private void calculateSalary() {
        printSubTitle(lang.get("title.calc_salary"));
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        Employee e = service.findById(id);
        if (e == null) {
            System.out.println(error(lang.get("error.not_found")));
            return;
        }
        // 计算薪资
        double salary = service.calculateSalary(id);
        // 显示薪资明细
        System.out.println("  " + lang.get("info.employee") + e.getName());
        System.out.println("  " + lang.get("info.dept") + e.getDepartment());
        System.out.println("  " + lang.get("info.type") + getEmployeeType(e));
        System.out.println("  " + lang.get("info.salary") + style(String.format("%.2f", salary), BRIGHT_GREEN, BOLD));
    }

    /**
     * 部门统计
     * 按部门分组统计各部门的员工人数
     * 以表格形式展示部门名称和对应人数
     */
    private void deptStats() {
        printSubTitle(lang.get("title.dept_stats"));
        Map<String, Long> stats = service.getDeptStats();
        if (stats.isEmpty()) {
            System.out.println(warning(lang.get("info.no_employee")));
            return;
        }
        // 打印统计表格
        System.out.println("  " + style(LIGHT_TL + repeat(LIGHT_H, 20) + LIGHT_T + repeat(LIGHT_H, 10) + LIGHT_TR, CYAN));
        System.out.println("  " + style(LIGHT_V + " ", CYAN) + style(padChinese(lang.get("table.dept"), 18), BOLD, BRIGHT_WHITE) +
                " " + style(LIGHT_V, CYAN) + " " + style(padChinese(lang.get("table.count"), 8), BOLD, BRIGHT_WHITE) + " " + style(LIGHT_V, CYAN));
        System.out.println("  " + style(LIGHT_L + repeat(LIGHT_H, 20) + LIGHT_C + repeat(LIGHT_H, 10) + LIGHT_R, CYAN));
        // 遍历每个部门输出统计结果
        for (Map.Entry<String, Long> entry : stats.entrySet()) {
            System.out.println("  " + style(LIGHT_V + " ", CYAN) + style(padChinese(entry.getKey(), 18), WHITE) +
                    " " + style(LIGHT_V, CYAN) + " " + style(padChinese(String.valueOf(entry.getValue()), 8), BRIGHT_YELLOW) + " " + style(LIGHT_V, CYAN));
        }
        System.out.println("  " + style(LIGHT_BL + repeat(LIGHT_H, 20) + LIGHT_B + repeat(LIGHT_H, 10) + LIGHT_BR, CYAN));
    }

    /**
     * 薪资排序
     * 将所有员工按薪资从高到低排序并展示
     * 显示排名、工号、姓名、部门、类型、绩效、工龄、薪资
     */
    private void salarySort() {
        printSubTitle(lang.get("title.salary_sort"));
        List<Employee> all = service.getAllEmployees();
        if (all.isEmpty()) {
            System.out.println(warning(lang.get("info.no_employee")));
            return;
        }
        // 按薪资从高到低排序
        Collections.sort(all, (e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()));

        // 打印带排名的表格
        System.out.println(style(LIGHT_TL, CYAN) + style(repeat(LIGHT_H, B_RANK), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_T, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_TR, CYAN));

        printTableHeaderWithRank();

        System.out.println(style(LIGHT_L, CYAN) + style(repeat(LIGHT_H, B_RANK), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_C, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_R, CYAN));

        // 逐行打印带排名的员工数据
        int rank = 1;
        for (Employee e : all) {
            printTableRow(rank++, e);
        }

        System.out.println(style(LIGHT_BL, CYAN) + style(repeat(LIGHT_H, B_RANK), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_ID), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_NAME), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_DEPT), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_TYPE), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_PERF), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_YEARS), CYAN) +
                style(LIGHT_B, CYAN) + style(repeat(LIGHT_H, B_SALARY), CYAN) +
                style(LIGHT_BR, CYAN));
    }

    /**
     * 打印操作日志
     * 调用 LogService 的 printLogs 方法展示所有操作记录
     * 日志包含时间戳和操作描述
     */
    private void printLogs() {
        printSubTitle(lang.get("title.logs"));
        logService.printLogs();
    }

    /**
     * 薪资筛选
     * 根据用户输入的最低和最高薪资范围筛选员工
     * 展示薪资在指定范围内的所有员工
     */
    private void salaryFilter() {
        printSubTitle(lang.get("title.salary_filter"));
        try {
            System.out.print(prompt("  " + lang.get("prompt.input.min_salary")));
            double min = Double.parseDouble(sc.nextLine());
            System.out.print(prompt("  " + lang.get("prompt.input.max_salary")));
            double max = Double.parseDouble(sc.nextLine());
            // 调用服务层按薪资范围筛选
            List<Employee> result = service.findBySalaryRange(min, max);
            if (result.isEmpty()) {
                System.out.println(warning(lang.get("info.no_result")));
            } else {
                System.out.println("  " + lang.get("info.found_count") + result.size() + lang.get("info.records"));
                printEmployeeList(result);
            }
        } catch (NumberFormatException e) {
            System.out.println(error(lang.get("error.format.number")));
        }
    }

    /**
     * 导出 CSV 文件
     * 将所有员工数据导出为 CSV 格式文件
     * 文件名格式为 employees_yyyyMMdd_HHmmss.csv
     * 包含工号、姓名、部门、联系方式、类型、基本工资、时薪、工作小时数、奖金、入职日期、绩效、请假记录
     */
    private void exportCsv() {
        printSubTitle(lang.get("title.export_csv"));
        List<Employee> all = service.getAllEmployees();
        if (all.isEmpty()) {
            System.out.println(warning(lang.get("info.no_employee")));
            return;
        }

        // 生成带时间戳的文件名
        String fileName = "employees_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            // 写入 BOM 头，确保 Excel 正确识别 UTF-8 编码
            pw.write('\uFEFF');
            // 写入 CSV 表头
            pw.println("工号,姓名,部门,联系方式,类型,基本工资,时薪,工作小时数,奖金,入职日期,绩效,请假记录");
            // 遍历所有员工，写入 CSV 行
            for (Employee e : all) {
                StringBuilder sb = new StringBuilder();
                sb.append(e.getId()).append(",");
                sb.append(e.getName()).append(",");
                sb.append(e.getDepartment()).append(",");
                sb.append(e.getContact()).append(",");
                sb.append(getEmployeeType(e)).append(",");
                // 根据员工类型写入不同的薪资字段
                if (e instanceof FullTimeEmployee) {
                    sb.append(((FullTimeEmployee) e).getBaseSalary()).append(",0,0,0,");
                } else if (e instanceof PartTimeEmployee) {
                    sb.append("0,").append(((PartTimeEmployee) e).getHourlyRate()).append(",");
                    sb.append(((PartTimeEmployee) e).getHoursWorked()).append(",0,");
                } else if (e instanceof Manager) {
                    sb.append(((Manager) e).getBaseSalary()).append(",0,0,");
                    sb.append(((Manager) e).getBonus()).append(",");
                } else {
                    sb.append("0,0,0,0,");
                }
                sb.append(e.getHireDate()).append(",");
                sb.append(e.getPerformance() == null ? "" : e.getPerformance().name()).append(",");
                // 将请假记录列表用分号连接
                sb.append(String.join("; ", e.getLeaveRecords()));
                pw.println(sb.toString());
            }
            System.out.println(success(lang.get("success.export") + fileName));
        } catch (IOException ex) {
            System.out.println(error(lang.get("error.export_fail") + ex.getMessage()));
        }
    }

    /**
     * 薪资柱状图
     * 以 ASCII 柱状图的形式展示所有员工的薪资对比
     * 每个员工用一行彩色柱状条表示，柱长与薪资成正比
     * 不同部门的柱状条使用不同的颜色，便于区分部门归属
     * 显示员工姓名、部门、柱状条和具体薪资
     */
    private void salaryBarChart() {
        printSubTitle(lang.get("title.bar_chart"));
        List<Employee> all = service.getAllEmployees();
        if (all.isEmpty()) {
            System.out.println(warning(lang.get("info.no_employee")));
            return;
        }
        // 找出最高薪资，用于计算柱状图比例
        double maxSalary = all.stream().mapToDouble(Employee::getSalary).max().orElse(1);
        int maxBarWidth = 40; // 柱状图最大宽度

        System.out.println("  " + lang.get("info.salary_chart"));
        // 遍历每个员工绘制柱状条
        for (Employee e : all) {
            double salary = e.getSalary();
            String dept = e.getDepartment();
            // 按比例计算柱状条长度
            int barLen = (int) (salary / maxSalary * maxBarWidth);
            if (barLen < 1) barLen = 1;
            // 构建柱状条字符串
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < barLen; i++) bar.append("█");
            // 根据部门获取对应的颜色
            String deptColor = getDeptBarColor(dept);
            // 显示员工姓名、部门、柱状条和薪资
            System.out.print("  " + style(padChinese(e.getName(), 8), BRIGHT_WHITE) + " ");
            System.out.print(style(padChinese(dept, 10), deptColor) + " ");
            System.out.print(style(bar.toString(), deptColor) + " ");
            System.out.println(style(String.format("%.2f", salary), BRIGHT_YELLOW));
            // 每个员工之间留一个空行作为缝隙，便于区分不同员工的柱状条
            System.out.println();
        }
    }

    /**
     * 薪资冠军
     * 展示薪资最高和最低的员工信息
     * 分别显示冠军和垫底员工的姓名、部门和薪资
     */
    private void salaryChampion() {
        printSubTitle(lang.get("title.champion"));
        Employee highest = service.getHighestSalary();
        Employee lowest = service.getLowestSalary();
        if (highest == null || lowest == null) {
            System.out.println(warning(lang.get("info.no_employee")));
            return;
        }
        // 显示薪资最高的员工（冠军）
        System.out.println("  " + style(lang.get("info.highest"), BOLD, BRIGHT_GREEN));
        System.out.println("    " + lang.get("info.name") + highest.getName());
        System.out.println("    " + lang.get("info.dept") + highest.getDepartment());
        System.out.println("    " + lang.get("info.salary") + style(String.format("%.2f", highest.getSalary()), BRIGHT_GREEN, BOLD));
        System.out.println();
        // 显示薪资最低的员工（垫底）
        System.out.println("  " + style(lang.get("info.lowest"), BOLD, BRIGHT_RED));
        System.out.println("    " + lang.get("info.name") + lowest.getName());
        System.out.println("    " + lang.get("info.dept") + lowest.getDepartment());
        System.out.println("    " + lang.get("info.salary") + style(String.format("%.2f", lowest.getSalary()), BRIGHT_RED, BOLD));
    }

    /**
     * 涨薪管理
     * 提供两种涨薪方式：给单个员工涨薪或给整个部门涨薪
     * 输入涨薪百分比后调用服务层执行
     */
    private void salaryManagement() {
        printSubTitle(lang.get("title.raise"));
        System.out.println("  " + lang.get("prompt.raise.method"));
        System.out.println("    " + style("1. " + lang.get("raise.single"), BRIGHT_CYAN));
        System.out.println("    " + style("2. " + lang.get("raise.dept"), BRIGHT_CYAN));
        System.out.print(prompt("  " + lang.get("prompt.choose")));
        int method;
        try {
            method = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(error(lang.get("error.format.number")));
            return;
        }
        switch (method) {
            case 1:
                raiseSingleSalary(); // 给单个员工涨薪
                break;
            case 2:
                raiseDeptSalary();   // 给整个部门涨薪
                break;
            default:
                System.out.println(error(lang.get("error.invalid_choice")));
        }
    }

    /**
     * 给单个员工涨薪
     * 输入员工工号和涨薪百分比，调用服务层执行涨薪
     */
    private void raiseSingleSalary() {
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        System.out.print(prompt("  " + lang.get("prompt.input.raise_percent")));
        try {
            double percent = Double.parseDouble(sc.nextLine());
            service.raiseSalary(id, percent);
            System.out.println(success(lang.get("success.raise")));
        } catch (NumberFormatException e) {
            System.out.println(error(lang.get("error.format.number")));
        } catch (IllegalArgumentException e) {
            System.out.println(error(e.getMessage()));
        }
    }

    /**
     * 给整个部门涨薪
     * 输入部门名称和涨薪百分比，调用服务层执行部门涨薪
     */
    private void raiseDeptSalary() {
        System.out.print(prompt("  " + lang.get("prompt.input.dept")));
        String dept = sc.nextLine();
        System.out.print(prompt("  " + lang.get("prompt.input.raise_percent")));
        try {
            double percent = Double.parseDouble(sc.nextLine());
            service.raiseDeptSalary(dept, percent);
            System.out.println(success(lang.get("success.raise")));
        } catch (NumberFormatException e) {
            System.out.println(error(lang.get("error.format.number")));
        } catch (IllegalArgumentException e) {
            System.out.println(error(e.getMessage()));
        }
    }

    /**
     * 请假扣薪
     * 输入员工工号、请假类型和天数，计算扣薪金额并展示明细
     * 支持年假（不扣薪）、病假（扣50%）、事假（扣100%）
     */
    private void applyLeave() {
        printSubTitle(lang.get("title.leave"));
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        // 检查员工是否存在
        Employee e = service.findById(id);
        if (e == null) {
            System.out.println(error(lang.get("error.not_found")));
            return;
        }
        // 选择请假类型
        System.out.println("  " + lang.get("prompt.leave.type"));
        System.out.println("    " + style("1. " + lang.get("leave.annual"), BRIGHT_GREEN));
        System.out.println("    " + style("2. " + lang.get("leave.sick"), BRIGHT_YELLOW));
        System.out.println("    " + style("3. " + lang.get("leave.personal"), BRIGHT_RED));
        System.out.print(prompt("  " + lang.get("prompt.choose")));
        int typeChoice;
        try {
            typeChoice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException ex) {
            System.out.println(error(lang.get("error.format.number")));
            return;
        }
        LeaveType leaveType;
        switch (typeChoice) {
            case 1: leaveType = LeaveType.ANNUAL; break;
            case 2: leaveType = LeaveType.SICK; break;
            case 3: leaveType = LeaveType.PERSONAL; break;
            default:
                System.out.println(error(lang.get("error.invalid_choice")));
                return;
        }
        // 输入请假天数
        System.out.print(prompt("  " + lang.get("prompt.input.leave_days")));
        try {
            int days = Integer.parseInt(sc.nextLine());
            // 调用服务层执行请假扣薪
            String detail = service.applyLeave(id, leaveType, days);
            System.out.println(success(lang.get("success.leave")));
            System.out.println(detail);
        } catch (NumberFormatException ex) {
            System.out.println(error(lang.get("error.format.number")));
        } catch (IllegalArgumentException ex) {
            System.out.println(error(ex.getMessage()));
        }
    }

    /**
     * 修改密码
     * 验证旧密码后，输入新密码并保存到 config.txt 文件
     * 默认密码为 "admin123"
     */
    private void changePassword() {
        printSubTitle(lang.get("title.password"));
        try {
            // 读取当前密码
            String currentPassword = "admin123";
            File configFile = new File("config.txt");
            if (configFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile, StandardCharsets.UTF_8))) {
                    String pwd = reader.readLine();
                    if (pwd != null && !pwd.trim().isEmpty()) {
                        currentPassword = pwd.trim();
                    }
                }
            }
            // 验证旧密码
            System.out.print(prompt("  " + lang.get("prompt.input.old_password")));
            String oldPwd = sc.nextLine();
            if (!oldPwd.equals(currentPassword)) {
                System.out.println(error(lang.get("error.wrong_password")));
                return;
            }
            // 输入新密码
            System.out.print(prompt("  " + lang.get("prompt.input.new_password")));
            String newPwd = sc.nextLine();
            // 确认新密码
            System.out.print(prompt("  " + lang.get("prompt.confirm.password")));
            String confirmPwd = sc.nextLine();
            if (!newPwd.equals(confirmPwd)) {
                System.out.println(error(lang.get("error.password_mismatch")));
                return;
            }
            // 保存新密码到文件
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("config.txt"), StandardCharsets.UTF_8))) {
                pw.println(newPwd);
            }
            System.out.println(success(lang.get("success.password_changed")));
        } catch (IOException e) {
            System.out.println(error(lang.get("error.password_fail") + e.getMessage()));
        }
    }

    /**
     * 数据备份
     * 调用服务层备份当前数据文件到 backup/ 文件夹
     * 备份文件名格式为 backup_yyyyMMdd_HHmmss.dat
     */
    private void backupData() {
        printSubTitle(lang.get("title.backup"));
        String backupFile = service.backupData();
        if (backupFile != null) {
            System.out.println(success(lang.get("success.backup") + backupFile));
        } else {
            System.out.println(error(lang.get("error.backup_fail")));
        }
    }

    /**
     * 从备份文件恢复数据
     * 列出 backup/ 文件夹下所有备份文件供用户选择
     * 用户输入编号后调用服务层执行恢复
     */
    private void restoreFromBackup() {
        printSubTitle(lang.get("title.restore"));
        File backupDir = new File("backup");
        if (!backupDir.exists() || backupDir.listFiles() == null || backupDir.listFiles().length == 0) {
            System.out.println(warning(lang.get("info.no_backup")));
            return;
        }
        // 列出所有备份文件
        File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".dat"));
        if (backups == null || backups.length == 0) {
            System.out.println(warning(lang.get("info.no_backup")));
            return;
        }
        System.out.println("  " + lang.get("info.backup_list"));
        for (int i = 0; i < backups.length; i++) {
            System.out.println("    " + style((i + 1) + ". " + backups[i].getName(), BRIGHT_CYAN));
        }
        System.out.print(prompt("  " + lang.get("prompt.choose")));
        try {
            int choice = Integer.parseInt(sc.nextLine());
            if (choice < 1 || choice > backups.length) {
                System.out.println(error(lang.get("error.invalid_choice")));
                return;
            }
            // 调用服务层从备份恢复
            boolean success = service.restoreFromBackup(backups[choice - 1].getPath());
            if (success) {
                System.out.println(success(lang.get("success.restore")));
            } else {
                System.out.println(error(lang.get("error.restore_fail")));
            }
        } catch (NumberFormatException e) {
            System.out.println(error(lang.get("error.format.number")));
        }
    }

    /**
     * 员工转岗
     * 将员工从当前类型转换为另一种员工类型
     * 输入员工工号、目标类型和对应的薪资参数
     * 保留公共字段（工号、姓名、部门、联系方式、入职日期、绩效、请假记录）
     */
    private void transferEmployee() {
        printSubTitle(lang.get("title.transfer"));
        System.out.print(prompt("  " + lang.get("prompt.input.id")));
        String id = sc.nextLine();
        // 检查员工是否存在
        Employee e = service.findById(id);
        if (e == null) {
            System.out.println(error(lang.get("error.not_found")));
            return;
        }
        // 显示当前员工信息
        System.out.println("  " + lang.get("info.current") + e.getName() + " (" + getEmployeeType(e) + ")");
        // 选择目标类型
        System.out.println("  " + lang.get("prompt.choose.target_type"));
        System.out.println("    " + style("1. " + lang.get("type.option.fulltime"), BRIGHT_BLUE));
        System.out.println("    " + style("2. " + lang.get("type.option.parttime"), BRIGHT_CYAN));
        System.out.println("    " + style("3. " + lang.get("type.option.manager"), BRIGHT_PURPLE));
        System.out.print(prompt("  " + lang.get("prompt.choose")));
        int newType;
        try {
            newType = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException ex) {
            System.out.println(error(lang.get("error.format.number")));
            return;
        }
        // 根据目标类型输入薪资参数
        try {
            switch (newType) {
                case 1:
                    System.out.print(prompt("  " + lang.get("prompt.input.base_salary")));
                    double baseSalary = Double.parseDouble(sc.nextLine());
                    service.transferEmployee(id, newType, baseSalary);
                    break;
                case 2:
                    System.out.print(prompt("  " + lang.get("prompt.input.hourly_rate")));
                    double hourlyRate = Double.parseDouble(sc.nextLine());
                    System.out.print(prompt("  " + lang.get("prompt.input.hours")));
                    double hoursWorked = Double.parseDouble(sc.nextLine());
                    service.transferEmployee(id, newType, hourlyRate, hoursWorked);
                    break;
                case 3:
                    System.out.print(prompt("  " + lang.get("prompt.input.base_salary")));
                    double mgrBase = Double.parseDouble(sc.nextLine());
                    System.out.print(prompt("  " + lang.get("prompt.input.bonus")));
                    double bonus = Double.parseDouble(sc.nextLine());
                    service.transferEmployee(id, newType, mgrBase, bonus);
                    break;
                default:
                    System.out.println(error(lang.get("error.invalid_type")));
                    return;
            }
            System.out.println(success(lang.get("success.transfer")));
        } catch (NumberFormatException ex) {
            System.out.println(error(lang.get("error.format.number")));
        } catch (IllegalArgumentException ex) {
            System.out.println(error(ex.getMessage()));
        }
    }

    /**
     * 批量导入员工
     * 从 CSV 文件批量导入员工数据
     * 支持导入模板格式，文件编码为 UTF-8
     * 导入成功时显示成功导入的数量
     */
    private void importEmployee() {
        printSubTitle(lang.get("title.import_emp"));
        System.out.print(prompt("  " + lang.get("prompt.input.file_path")));
        String filePath = sc.nextLine();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println(error(lang.get("error.file_not_found")));
            return;
        }
        int successCount = 0;
        int failCount = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            // 跳过 CSV 表头行
            br.readLine();
            // 逐行读取并解析员工数据
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 6) {
                    failCount++;
                    continue;
                }
                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String dept = parts[2].trim();
                    String contact = parts[3].trim();
                    String typeStr = parts[4].trim();
                    LocalDate hireDate = LocalDate.parse(parts[9].trim());
                    // 根据类型创建员工对象
                    Employee emp;
                    switch (typeStr) {
                        case "全职":
                        case "Full-Time":
                            double baseSalary = Double.parseDouble(parts[5].trim());
                            emp = new FullTimeEmployee(id, name, dept, contact, baseSalary, hireDate);
                            break;
                        case "兼职":
                        case "Part-Time":
                            double hourlyRate = Double.parseDouble(parts[6].trim());
                            double hoursWorked = Double.parseDouble(parts[7].trim());
                            emp = new PartTimeEmployee(id, name, dept, contact, hourlyRate, hoursWorked, hireDate);
                            break;
                        case "管理层":
                        case "Management":
                            double mgrBase = Double.parseDouble(parts[5].trim());
                            double bonus = Double.parseDouble(parts[8].trim());
                            emp = new Manager(id, name, dept, contact, mgrBase, bonus, hireDate);
                            break;
                        default:
                            failCount++;
                            continue;
                    }
                    // 设置绩效等级
                    if (parts.length > 10 && !parts[10].trim().isEmpty()) {
                        try {
                            Performance perf = Performance.valueOf(parts[10].trim().toUpperCase());
                            emp.setPerformance(perf);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    // 设置请假记录
                    if (parts.length > 11 && !parts[11].trim().isEmpty()) {
                        String[] records = parts[11].split("; ");
                        for (String record : records) {
                            if (!record.trim().isEmpty()) {
                                emp.addLeaveRecord(record.trim());
                            }
                        }
                    }
                    service.addEmployee(emp);
                    successCount++;
                } catch (Exception ex) {
                    failCount++;
                }
            }
            System.out.println(success(lang.get("success.import") + successCount + lang.get("info.records")));
            if (failCount > 0) {
                System.out.println(warning(failCount + lang.get("info.import_fail")));
            }
        } catch (IOException e) {
            System.out.println(error(lang.get("error.import_fail") + e.getMessage()));
        }
    }

    /**
     * 生成月度薪资报表
     * 调用服务层生成月度薪资报表并展示
     * 报表按部门分组统计人数、最高薪资、最低薪资、平均薪资、薪资总额
     */
    private void generateMonthlyReportUI() {
        printSubTitle(lang.get("title.report"));
        String report = service.generateMonthlyReport();
        System.out.println(report);
        // 将报表保存到文件
        String fileName = "report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + ".txt";
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            pw.println(report);
            System.out.println(success(lang.get("success.report_saved") + fileName));
        } catch (IOException e) {
            System.out.println(error(lang.get("error.report_fail") + e.getMessage()));
        }
    }

    /**
     * 入职趋势图
     * 以 ASCII 柱状图的形式展示各年份入职员工数量
     * 统计所有员工的入职年份，按年份分组统计人数
     * 柱状条使用青色，显示年份和对应人数
     */
    private void hireTrendChart() {
        printSubTitle(lang.get("title.hire_trend"));
        List<Employee> all = service.getAllEmployees();
        if (all.isEmpty()) {
            System.out.println(warning(lang.get("info.no_employee")));
            return;
        }
        // 按年份统计入职人数
        Map<Integer, Integer> yearCount = new java.util.HashMap<>();
        for (Employee e : all) {
            int year = e.getHireDate().getYear();
            yearCount.put(year, yearCount.getOrDefault(year, 0) + 1);
        }
        // 找出最大人数，用于计算柱状图比例
        int maxCount = yearCount.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int maxBarWidth = 30;

        System.out.println("  " + lang.get("info.hire_chart"));
        // 按年份排序输出
        List<Integer> years = new ArrayList<>(yearCount.keySet());
        Collections.sort(years);
        for (int year : years) {
            int count = yearCount.get(year);
            // 按比例计算柱状条长度
            int barLen = (int) ((double) count / maxCount * maxBarWidth);
            if (barLen < 1) barLen = 1;
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < barLen; i++) bar.append("█");
            // 显示年份、柱状条和人数
            System.out.print("  " + style(String.valueOf(year), BRIGHT_WHITE) + " ");
            System.out.println(style(bar.toString(), BRIGHT_CYAN) + " " + style(String.valueOf(count), BRIGHT_YELLOW));
        }
    }

    /**
     * 打印退出画面
     * 使用 ANSI 颜色和 Unicode 边框字符美化展示退出界面
     * 显示感谢使用信息和退出提示
     */
    private void printExitScreen() {
        System.out.println();
        System.out.println(style("  ╔══════════════════════════════════════╗", BRIGHT_GREEN));
        System.out.println(style("  ║                                      ║", BRIGHT_GREEN));
        System.out.println(style("  ║    " + lang.get("exit.thanks"), BRIGHT_GREEN, BOLD));
        System.out.println(style("  ║    " + lang.get("exit.goodbye"), BRIGHT_GREEN, BOLD));
        System.out.println(style("  ║                                      ║", BRIGHT_GREEN));
        System.out.println(style("  ╚══════════════════════════════════════╝", BRIGHT_GREEN));
        System.out.println();
    }
}
