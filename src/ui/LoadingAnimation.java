package ui;

import static ui.ColorUtil.*;

/**
 * 登录加载动画工具类
 * 在用户登录成功后显示一个美观的加载动画效果
 */
public class LoadingAnimation {

    /**
     * 显示登录加载动画（带进度条和旋转图标）
     */
    public static void showLoginLoading() {
        // 旋转动画字符
        String[] spinner = {"◐", "◓", "◑", "◒"};
        // 进度条填充字符
        String filled = "█";
        String empty = "░";

        int totalSteps = 20;

        System.out.println();
        System.out.println(style(" " + BOX_TL + repeat(BOX_H, 30) + BOX_TR + " ", BG_BLUE, BOLD));
        System.out.print(style(" " + BOX_V + " ", BG_BLUE, BOLD));
        System.out.print(style(centerText("🔄 正在加载系统...", 28), BG_BLUE, BOLD, BRIGHT_WHITE));
        System.out.println(style(" " + BOX_V + " ", BG_BLUE, BOLD));
        System.out.println(style(" " + BOX_L + repeat(BOX_H, 30) + BOX_R + " ", BG_BLUE, BOLD));

        for (int i = 0; i <= totalSteps; i++) {
            // 计算进度百分比
            int percent = (i * 100) / totalSteps;

            // 构建进度条
            int filledCount = i;
            int emptyCount = totalSteps - i;

            StringBuilder progressBar = new StringBuilder();
            progressBar.append(" ");
            for (int j = 0; j < filledCount; j++) {
                progressBar.append(style(filled, BRIGHT_GREEN));
            }
            for (int j = 0; j < emptyCount; j++) {
                progressBar.append(style(empty, DIM));
            }

            // 旋转图标索引
            String spinChar = spinner[i % spinner.length];

            // 加载提示文本
            String[] loadingTexts = {
                    "正在初始化模块...",
                    "正在加载数据...",
                    "正在配置参数...",
                    "正在建立连接...",
                    "正在安全检查...",
                    "正在优化性能...",
                    "正在准备界面...",
                    "即将进入系统..."
            };
            int textIndex = Math.min(i * loadingTexts.length / totalSteps, loadingTexts.length - 1);

            // 清除当前行并重新绘制
            System.out.print(style(" " + BOX_V + " ", BG_BLUE, BOLD));
            System.out.print(style(" " + spinChar + " ", BOLD, BRIGHT_CYAN));
            System.out.print(style(String.format("%3d%% ", percent), BOLD, BRIGHT_YELLOW));
            System.out.print(progressBar);
            System.out.print(" ");
            System.out.print(style(loadingTexts[textIndex], DIM, WHITE));
            // 补充空格清除残留字符
            int padding = 28 - loadingTexts[textIndex].length();
            for (int p = 0; p < padding; p++) System.out.print(" ");
            System.out.print(style(" " + BOX_V + " ", BG_BLUE, BOLD));

            // 如果是最后一步，不提行；否则回到行首
            if (i < totalSteps) {
                System.out.print("\r");
            } else {
                System.out.println();
            }

            // 模拟加载延时
            try {
                Thread.sleep(80 + (int)(Math.random() * 60));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 完成提示
        System.out.println(style(" " + BOX_BL + repeat(BOX_H, 30) + BOX_BR + " ", BG_BLUE, BOLD));
        System.out.println();
        System.out.println(style(" " + centerText("✅ 系统加载完成！", 28), BG_GREEN, BOLD, BRIGHT_WHITE));
        System.out.println();

        // 短暂停留让用户看到完成提示
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 快速加载动画（简洁版）
     */
    public static void showQuickLoading() {
        String[] dots = {".  ", ".. ", "..."};

        System.out.print(style("  加载中", BRIGHT_CYAN));

        for (int i = 0; i < 6; i++) {
            System.out.print("\r" + style("  加载中" + dots[i % dots.length], BRIGHT_CYAN));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(style("\r  " + success("加载完成！"), RESET));
    }
}
