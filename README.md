# 🏢 员工管理系统 (Employee Management System)

一个基于 Java Swing 的现代化员工管理系统，支持中英文双语切换，拥有美观的深色主题 GUI 界面。

## ✨ 功能特性

### 👥 员工管理
- **添加员工**：支持全职、兼职、经理三种员工类型
- **删除/修改员工**：灵活管理员工信息
- **查询员工**：支持按工号、姓名、部门查询，以及模糊搜索
- **员工转岗**：可在不同员工类型之间转换，保留绩效和请假记录

### 💰 薪资管理
- **薪资计算**：根据员工类型自动计算薪资（全职=底薪+绩效，兼职=时薪×工时+绩效，经理=底薪+奖金+绩效）
- **涨薪管理**：支持单个员工涨薪和部门统一涨薪
- **薪资排序与筛选**：按薪资高低排序，按范围筛选
- **薪资冠军/垫底**：自动统计最高和最低薪资

### 📊 统计报表
- **部门统计**：各部门人数统计
- **月度薪资报表**：按部门统计人数、最高/最低/平均薪资、薪资总额
- **薪资柱状图**：可视化展示薪资分布
- **入职趋势图**：展示员工入职时间分布

### 🏖️ 请假管理
- 支持年假（带薪）、病假（扣50%）、事假（无薪）
- 自动计算扣薪明细和实发薪资

### ⭐ 绩效管理
- A级（奖金20%）、B级（奖金10%）、C级（无奖金）
- 绩效自动影响薪资计算结果

### 🔧 系统功能
- **中英文双语切换**：一键切换界面语言
- **数据持久化**：自动保存到 `employees.dat`
- **数据备份与恢复**：支持多版本备份
- **操作撤销**：最多撤销10步操作
- **批量导入**：从 CSV/TXT 文件批量导入员工
- **导出 CSV**：导出员工数据
- **操作日志**：记录所有操作历史

## 🖥️ 界面展示

- **深色主题**：精心设计的暗色 UI，护眼且美观
- **现代化登录**：带渐变效果的登录窗口，3次密码尝试机会
- **侧边栏导航**：功能分组清晰，操作便捷
- **实时时钟**：顶部显示当前时间
- **状态栏**：显示员工总数和操作提示

## 🛠️ 技术栈

- **语言**：Java
- **UI 框架**：Java Swing（自定义深色主题）
- **数据存储**：Java 对象序列化（`employees.dat`）
- **构建工具**：批处理脚本编译打包

## 🚀 快速开始

### 编译运行

```bash
# 编译
javac -encoding UTF-8 -d bin src\model\*.java src\service\*.java src\ui\*.java src\ui\modern\*.java src\Main.java

# 运行
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp bin Main
```

或者直接双击运行 `run.bat`。

### 打包为可执行文件

运行 `package.bat` 即可打包为 JAR 和 EXE。

### 默认密码

```
用户名：admin
密码：admin123
```

## 📁 项目结构

```
src/
├── Main.java                    # 程序入口
├── model/
│   ├── Employee.java            # 员工抽象类
│   ├── FullTimeEmployee.java    # 全职员工
│   ├── PartTimeEmployee.java    # 兼职员工
│   ├── Manager.java             # 经理
│   ├── Performance.java         # 绩效枚举
│   └── LeaveType.java           # 请假类型枚举
├── service/
│   ├── EmployeeService.java     # 员工服务接口
│   ├── EmployeeServiceImpl.java # 员工服务实现
│   ├── LanguageService.java     # 语言服务
│   └── LogService.java          # 日志服务
└── ui/
    ├── ColorUtil.java           # 控制台颜色工具
    ├── ConsoleUI.java           # 控制台界面
    ├── LoadingAnimation.java    # 加载动画
    ├── LoginUI.java             # 控制台登录
    └── modern/
        ├── Theme.java           # 深色主题配置
        ├── ModernLoginView.java # 现代化登录窗口
        ├── MainDashboard.java   # 主仪表盘
        ├── EmployeePanel.java   # 员工管理面板
        ├── SalaryPanel.java     # 薪资管理面板
        ├── AnalyticsPanel.java  # 统计报表面板
        └── SettingsPanel.java   # 系统设置面板
```

## 📄 数据导入格式

支持从 TXT 文件批量导入员工数据，格式如下：

```
类型,工号,姓名,部门,联系方式,入职日期,薪资参数
全职,E001,张三,技术部,13800000000,2020-01-01,12000
兼职,E002,李四,市场部,13900000000,2022-06-01,50,160
管理层,E003,王总,管理层,13700000000,2018-03-01,20000,5000
```

## 📜 开源协议

本项目仅供学习交流使用。
