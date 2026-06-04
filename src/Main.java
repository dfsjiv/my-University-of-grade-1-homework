import model.Employee;
import service.EmployeeService;
import service.EmployeeServiceImpl;
import service.LanguageService;
import service.LogService;
import ui.ConsoleUI;
import ui.LoginUI;
import ui.modern.MainDashboard;
import ui.modern.ModernLoginView;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static ui.ColorUtil.*;

/**
 * 员工管理系统主入口类
 * 所在层次：应用入口层，作为整个系统的启动点
 * 职责：初始化系统所需的各种服务组件，显示现代化登录界面，
 *       验证密码后展示动态进度条加载动画，最后启动主菜单
 */
public class Main {

    public static void main(String[] args) {
        // 设置系统外观为跨平台外观
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化日志服务
        LogService logService = new LogService();
        // 初始化语言服务
        LanguageService lang = new LanguageService();
        // 初始化员工服务
        EmployeeService service = new EmployeeServiceImpl(logService);
        // 创建键盘输入扫描器
        Scanner sc = new Scanner(System.in);

        // 加载语言设置
        loadLanguageConfig(lang);

        // 启动现代化 GUI
        SwingUtilities.invokeLater(() -> {
            // 显示现代化登录界面
            ModernLoginView loginView = new ModernLoginView(new JFrame(), () -> {
                // 登录成功后启动主仪表盘
                SwingUtilities.invokeLater(() -> {
                    MainDashboard dashboard = new MainDashboard(service, logService);
                    dashboard.setVisible(true);
                });
            });
            loginView.show();
        });
    }

    /**
     * 加载语言设置
     */
    private static void loadLanguageConfig(LanguageService lang) {
        File configFile = new File("config.txt");
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile, StandardCharsets.UTF_8))) {
                String password = reader.readLine();
                String langSetting = reader.readLine();
                if ("en".equals(langSetting)) {
                    lang.switchLang();
                }
            } catch (IOException e) {
                // 读取失败则使用默认语言（中文）
            }
        }
    }
}
