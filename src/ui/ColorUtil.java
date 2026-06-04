package ui;

/**
 * 控制台颜色与格式化工具类
 * 提供 ANSI 转义码的封装，用于美化控制台输出
 */
public class ColorUtil {

    // ANSI 转义码
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    public static final String REVERSE = "\u001B[7m";

    // 前景色
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // 背景色
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";

    // 亮色前景
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_PURPLE = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // 亮色背景
    public static final String BG_BRIGHT_BLACK = "\u001B[100m";
    public static final String BG_BRIGHT_RED = "\u001B[101m";
    public static final String BG_BRIGHT_GREEN = "\u001B[102m";
    public static final String BG_BRIGHT_YELLOW = "\u001B[103m";
    public static final String BG_BRIGHT_BLUE = "\u001B[104m";
    public static final String BG_BRIGHT_PURPLE = "\u001B[105m";
    public static final String BG_BRIGHT_CYAN = "\u001B[106m";
    public static final String BG_BRIGHT_WHITE = "\u001B[107m";

    // 粗边框字符
    public static final String BOX_H = "━";
    public static final String BOX_V = "┃";
    public static final String BOX_TL = "┏";
    public static final String BOX_TR = "┓";
    public static final String BOX_BL = "┗";
    public static final String BOX_BR = "┛";
    public static final String BOX_T = "┳";
    public static final String BOX_B = "┻";
    public static final String BOX_L = "┣";
    public static final String BOX_R = "┫";
    public static final String BOX_C = "╋";

    // 轻量边框
    public static final String LIGHT_H = "─";
    public static final String LIGHT_V = "│";
    public static final String LIGHT_TL = "┌";
    public static final String LIGHT_TR = "┐";
    public static final String LIGHT_BL = "└";
    public static final String LIGHT_BR = "┘";
    public static final String LIGHT_T = "┬";
    public static final String LIGHT_B = "┴";
    public static final String LIGHT_L = "├";
    public static final String LIGHT_R = "┤";
    public static final String LIGHT_C = "┼";

    // 双线边框
    public static final String D_H = "═";
    public static final String D_V = "║";
    public static final String D_TL = "╔";
    public static final String D_TR = "╗";
    public static final String D_BL = "╚";
    public static final String D_BR = "╝";
    public static final String D_T = "╦";
    public static final String D_B = "╩";
    public static final String D_L = "╠";
    public static final String D_R = "╣";
    public static final String D_C = "╬";

    // 进度条字符
    public static final String PROGRESS_FULL = "█";
    public static final String PROGRESS_DARK = "▓";
    public static final String PROGRESS_MED = "▒";
    public static final String PROGRESS_LIGHT = "░";

    // 图标字符
    public static final String ICON_USER = "👤";
    public static final String ICON_TIME = "🕐";
    public static final String ICON_PEOPLE = "👥";
    public static final String ICON_STAR = "★";
    public static final String ICON_ARROW = "▶";
    public static final String ICON_SQUARE = "■";
    public static final String ICON_CIRCLE = "●";
    public static final String ICON_DIAMOND = "◆";
    public static final String ICON_CHECK = "✔";
    public static final String ICON_CROSS = "✘";
    public static final String ICON_WARN = "⚠";
    public static final String ICON_INFO = "ℹ";
    public static final String ICON_MENU = "►";
    public static final String ICON_SETTING = "⚙";
    public static final String ICON_DB = "🗄";
    public static final String ICON_MONEY = "💰";
    public static final String ICON_CHART = "📊";
    public static final String ICON_EXIT = "🚪";

    /**
     * 获取带颜色的字符串
     */
    public static String color(String text, String colorCode) {
        return colorCode + text + RESET;
    }

    /**
     * 获取带颜色和样式的字符串
     */
    public static String style(String text, String... codes) {
        StringBuilder sb = new StringBuilder();
        for (String code : codes) {
            sb.append(code);
        }
        sb.append(text).append(RESET);
        return sb.toString();
    }

    /**
     * 生成粗体标题
     */
    public static String title(String text) {
        return style(" " + text + " ", BOLD, CYAN);
    }

    /**
     * 生成成功消息（绿色）
     */
    public static String success(String text) {
        return style("✔ " + text, BRIGHT_GREEN);
    }

    /**
     * 生成错误消息（红色）
     */
    public static String error(String text) {
        return style("✘ " + text, BRIGHT_RED);
    }

    /**
     * 生成警告消息（黄色）
     */
    public static String warning(String text) {
        return style("⚠ " + text, BRIGHT_YELLOW);
    }

    /**
     * 生成信息消息（蓝色）
     */
    public static String info(String text) {
        return style("● " + text, BRIGHT_BLUE);
    }

    /**
     * 生成提示文字
     */
    public static String prompt(String text) {
        return style(text, BRIGHT_CYAN);
    }

    /**
     * 生成菜单项（带编号）
     */
    public static String menuItem(int number, String text) {
        return style(" " + number + ". ", BRIGHT_YELLOW) + style(text, WHITE);
    }

    /**
     * 重复字符串
     */
    public static String repeat(String s, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 清除屏幕
     */
    public static void clearScreen() {
        System.out.print("\u001B[2J\u001B[H");
        System.out.flush();
    }

    /**
     * 打印分隔线（带颜色）
     */
    public static void printLine(String color) {
        System.out.println(color + repeat(BOX_H, 60) + RESET);
    }

    /**
     * 打印轻量分隔线
     */
    public static void printLightLine(String color) {
        System.out.println(color + repeat(LIGHT_H, 60) + RESET);
    }

    /**
     * 获取字符串的可见长度（中文字符算2，英文字符算1）
     */
    public static int getVisibleLength(String s) {
        if (s == null) return 0;
        int len = 0;
        for (char c : s.toCharArray()) {
            if (c >= '\u4e00' && c <= '\u9fff') {
                len += 2;
            } else if (c > 127) {
                len += 2; // 其他非ASCII字符也算2
            } else {
                len += 1;
            }
        }
        return len;
    }

    /**
     * 在表格中居中显示文本（考虑中文字符宽度）
     */
    public static String centerText(String text, int width) {
        int visLen = getVisibleLength(text);
        int padding = width - visLen;
        if (padding <= 0) return text;
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPad; i++) sb.append(' ');
        sb.append(text);
        for (int i = 0; i < rightPad; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * 左对齐填充到指定宽度
     */
    public static String padLeft(String text, int width) {
        int visLen = getVisibleLength(text);
        if (visLen >= width) return text;
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < width - visLen; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * 右对齐填充到指定宽度
     */
    public static String padRight(String text, int width) {
        int visLen = getVisibleLength(text);
        if (visLen >= width) return text;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width - visLen; i++) sb.append(' ');
        sb.append(text);
        return sb.toString();
    }

    /**
     * 格式化金额
     */
    public static String formatMoney(double amount) {
        return style(String.format("%.2f", amount), BRIGHT_GREEN);
    }

    /**
     * 格式化员工类型标签
     */
    public static String formatType(String type) {
        switch (type) {
            case "管理层": return style("管理层", BOLD, BRIGHT_PURPLE);
            case "全职": return style("全职", BOLD, BRIGHT_BLUE);
            case "兼职": return style("兼职", BOLD, BRIGHT_CYAN);
            default: return type;
        }
    }

    /**
     * 格式化绩效等级
     */
    public static String formatPerformance(Enum<?> p) {
        if (p == null) return "N/A";
        String name = p.name();
        switch (name) {
            case "A": return style(" A ", BG_GREEN, BOLD, WHITE);
            case "B": return style(" B ", BG_YELLOW, BOLD, BLACK);
            case "C": return style(" C ", BG_RED, BOLD, WHITE);
            default: return name;
        }
    }

    /**
     * 根据部门名称返回对应的柱状图颜色
     */
    public static String getDeptBarColor(String dept) {
        if (dept == null) return BRIGHT_GREEN;
        switch (dept) {
            case "技术部":
            case "技术研发部":
            case "Engineering":
            case "Technology":
                return BRIGHT_CYAN;
            case "市场部":
            case "Marketing":
                return BRIGHT_PURPLE;
            case "销售部":
            case "Sales":
                return BRIGHT_RED;
            case "财务部":
            case "Finance":
                return BRIGHT_GREEN;
            case "人事部":
            case "人力资源部":
            case "HR":
            case "Human Resources":
                return BRIGHT_YELLOW;
            case "行政部":
            case "Administration":
                return BRIGHT_BLUE;
            case "运营部":
            case "Operations":
                return BRIGHT_WHITE;
            default:
                int hash = dept.hashCode() & 0x7FFFFFFF;
                String[] colors = {BRIGHT_CYAN, BRIGHT_PURPLE, BRIGHT_RED, BRIGHT_GREEN, BRIGHT_YELLOW, BRIGHT_BLUE, BRIGHT_WHITE};
                return colors[hash % colors.length];
        }
    }

    /**
     * 生成动态进度条字符串
     * @param progress 0-100 的进度值
     * @param width 进度条总宽度（字符数）
     * @return 带颜色的进度条字符串
     */
    public static String getProgressBar(int progress, int width) {
        int filled = progress * width / 100;
        int remaining = width - filled;
        
        StringBuilder sb = new StringBuilder();
        // 已填充部分用 █
        sb.append(style(repeat(PROGRESS_FULL, filled), BRIGHT_CYAN));
        // 未填充部分用 ░
        sb.append(style(repeat(PROGRESS_LIGHT, remaining), DIM));
        
        return sb.toString();
    }

    /**
     * 生成带百分比的进度条
     */
    public static String getProgressBarWithPercent(int progress, int width) {
        String bar = getProgressBar(progress, width);
        String percent = style(String.format(" %3d%%", progress), BRIGHT_WHITE, BOLD);
        return bar + percent;
    }

    /**
     * 生成带渐变效果的进度条（从青色渐变到紫色）
     */
    public static String getGradientProgressBar(int progress, int width) {
        int filled = progress * width / 100;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < filled; i++) {
            double ratio = (double) i / width;
            if (ratio < 0.33) {
                sb.append(style(PROGRESS_FULL, BRIGHT_CYAN));
            } else if (ratio < 0.66) {
                sb.append(style(PROGRESS_FULL, BRIGHT_BLUE));
            } else {
                sb.append(style(PROGRESS_FULL, BRIGHT_PURPLE));
            }
        }
        sb.append(style(repeat(PROGRESS_LIGHT, width - filled), DIM));
        
        return sb.toString();
    }

    /**
     * 生成 ASCII 艺术字标题（FIGlet 风格）
     */
    public static String getAsciiTitle() {
        return 
            "  ███████╗███╗   ███╗██████╗ ██╗      ██████╗ ██╗   ██╗███████╗███████╗\n" +
            "  ██╔════╝████╗ ████║██╔══██╗██║     ██╔═══██╗╚██╗ ██╔╝██╔════╝██╔════╝\n" +
            "  █████╗  ██╔████╔██║██████╔╝██║     ██║   ██║ ╚████╔╝ █████╗  ███████╗\n" +
            "  ██╔══╝  ██║╚██╔╝██║██╔═══╝ ██║     ██║   ██║  ╚██╔╝  ██╔══╝  ╚════██║\n" +
            "  ███████╗██║ ╚═╝ ██║██║     ███████╗╚██████╔╝   ██║   ███████╗███████║\n" +
            "  ╚══════╝╚═╝     ╚═╝╚═╝     ╚══════╝ ╚═════╝    ╚═╝   ╚══════╝╚══════╝";
    }

    /**
     * 生成小号 ASCII 标题
     */
    public static String getSmallAsciiTitle() {
        return 
            "  ╔══════════════════════════════════════════════════════════════╗\n" +
            "  ║  ███████╗███╗   ███╗██████╗ ██╗      ██████╗ ██╗   ██╗   ║\n" +
            "  ║  ██╔════╝████╗ ████║██╔══██╗██║     ██╔═══██╗╚██╗ ██╔╝   ║\n" +
            "  ║  █████╗  ██╔████╔██║██████╔╝██║     ██║   ██║ ╚████╔╝    ║\n" +
            "  ║  ██╔══╝  ██║╚██╔╝██║██╔═══╝ ██║     ██║   ██║  ╚██╔╝     ║\n" +
            "  ║  ███████╗██║ ╚═╝ ██║██║     ███████╗╚██████╔╝   ██║      ║\n" +
            "  ║  ╚══════╝╚═╝     ╚═╝╚═╝     ╚══════╝ ╚═════╝    ╚═╝      ║\n" +
            "  ╚══════════════════════════════════════════════════════════════╝";
    }

    /**
     * 生成 "MANAGEMENT SYSTEM" 副标题
     */
    public static String getSubtitle() {
        return 
            "  ███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗███╗   ███╗███████╗███╗   ██╗████████╗\n" +
            "  ████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝████╗ ████║██╔════╝████╗  ██║╚══██╔══╝\n" +
            "  ██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗  ██╔████╔██║█████╗  ██╔██╗ ██║   ██║   \n" +
            "  ██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝  ██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║   \n" +
            "  ██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗██║ ╚═╝ ██║███████╗██║ ╚████║   ██║   \n" +
            "  ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝   \n" +
            "  ███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗                                        \n" +
            "  ██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║                                        \n" +
            "  █████╗   ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║                                        \n" +
            "  ██╔══╝    ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║                                        \n" +
            "  ███████╗   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║                                        \n" +
            "  ╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝                                        ";
    }

    /**
     * 生成面板标题行
     */
    public static String panelHeader(String title, int width) {
        String top = D_TL + repeat(D_H, width - 2) + D_TR;
        String mid = D_V + style(centerText(title, width - 2), BOLD, BRIGHT_CYAN) + D_V;
        return top + "\n" + mid;
    }

    /**
     * 生成面板底部
     */
    public static String panelFooter(int width) {
        return D_BL + repeat(D_H, width - 2) + D_BR;
    }

    /**
     * 生成面板内容行
     */
    public static String panelRow(String content, int width) {
        int visLen = getVisibleLength(content);
        int padding = width - 2 - visLen;
        if (padding < 0) padding = 0;
        return D_V + content + repeat(" ", padding) + D_V;
    }

    /**
     * 生成带图标的菜单项
     */
    public static String menuItemWithIcon(String icon, String text, String color, boolean selected) {
        String prefix = selected ? style("▸ ", BRIGHT_CYAN, BOLD) : "  ";
        String iconStr = style(icon + " ", color);
        String textStr = selected ? style(text, BOLD, BRIGHT_WHITE) : style(text, WHITE);
        return prefix + iconStr + textStr;
    }

    /**
     * 生成分类标题
     */
    public static String categoryTitle(String icon, String text) {
        return style(" " + icon + " " + text + " ", BOLD, BRIGHT_CYAN, BG_BRIGHT_BLACK);
    }
}
