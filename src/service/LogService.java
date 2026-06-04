package service;

import ui.ColorUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ui.ColorUtil.*;

/**
 * 日志服务类
 * <p>
 * 所在层次：服务层（Service）
 * 职责：记录员工管理相关的操作日志，提供日志的添加和格式化打印功能
 *       每条日志包含时间戳和操作描述，打印时使用 ANSI 颜色和边框美化展示
 * 关联类：EmployeeServiceImpl（调用 addLog 记录操作）、ConsoleUI（调用 printLogs 展示日志）
 */
public class LogService {

    /** 日志列表，按时间顺序存储所有操作日志 */
    private final List<String> logs = new ArrayList<>();
    /** 日期时间格式化器，用于生成日志时间戳，格式：yyyy-MM-dd HH:mm:ss */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 添加操作日志
     * 自动为日志添加当前时间戳，格式为 [yyyy-MM-dd HH:mm:ss]
     *
     * @param action 操作内容描述，如"添加员工：工号=001，姓名=张三"
     */
    public void addLog(String action) {
        // 获取当前时间并格式化为时间戳字符串
        String timestamp = LocalDateTime.now().format(FORMATTER);
        logs.add("[" + timestamp + "] " + action);
    }

    /**
     * 获取所有日志
     */
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * 清空所有日志
     */
    public void clearLogs() {
        logs.clear();
    }

    /**
     * 打印所有操作日志（美化版）
     * 使用 ANSI 颜色和 Unicode 边框字符美化展示
     * 如果日志为空则显示提示信息
     * 每条日志显示序号、时间戳（青色）、操作内容（白色）
     * 日志列表用黄色边框包围
     */
    public void printLogs() {
        // 如果日志列表为空，显示提示信息并返回
        if (logs.isEmpty()) {
            System.out.println("  " + warning("暂无操作记录"));
            return;
        }

        // 显示日志总数
        System.out.println("  " + style("共 " + logs.size() + " 条操作记录", BOLD, BRIGHT_WHITE));

        // 打印上边框
        System.out.println("  " + style(LIGHT_TL, YELLOW) + style(repeat(LIGHT_H, 56), YELLOW) + style(LIGHT_TR, YELLOW));

        // 遍历每条日志，提取时间戳和操作内容分别着色显示
        for (int i = 0; i < logs.size(); i++) {
            String log = logs.get(i);
            String display = log;
            // 尝试提取方括号中的时间戳部分
            if (display.startsWith("[")) {
                int endBracket = display.indexOf("] ");
                if (endBracket > 0) {
                    // 分离时间戳和操作内容
                    String timePart = display.substring(0, endBracket + 1);
                    String actionPart = display.substring(endBracket + 2);

                    // 打印带格式的日志行：序号 + 时间戳（青色） + 操作内容（白色）
                    System.out.print("  " + style(LIGHT_V + " ", YELLOW));
                    System.out.print(style(String.format("%2d. ", i + 1), DIM, BRIGHT_WHITE));
                    System.out.print(style(timePart, DIM, BRIGHT_CYAN));
                    System.out.println(style(" " + actionPart, WHITE) + "  " + style(LIGHT_V, YELLOW));
                } else {
                    // 时间戳格式不匹配，直接显示整条日志
                    System.out.println("  " + style(LIGHT_V + " ", YELLOW) + style(display, WHITE) + "  " + style(LIGHT_V, YELLOW));
                }
            } else {
                // 不以方括号开头，直接显示整条日志
                System.out.println("  " + style(LIGHT_V + " ", YELLOW) + style(display, WHITE) + "  " + style(LIGHT_V, YELLOW));
            }
        }

        // 打印下边框
        System.out.println("  " + style(LIGHT_BL, YELLOW) + style(repeat(LIGHT_H, 56), YELLOW) + style(LIGHT_BR, YELLOW));
    }
}
