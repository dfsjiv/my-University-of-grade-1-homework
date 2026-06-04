package service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 语言服务类，提供中英文双语切换功能
 * 所有提示语通过 key 获取，支持动态切换语言
 */
public class LanguageService {

    private final Map<String, String> zhMap = new HashMap<>();
    private final Map<String, String> enMap = new HashMap<>();
    private String currentLang = "zh"; // 默认中文

    public LanguageService() {
        initZhMap();
        initEnMap();
    }

    // ===================== 中文提示语 =====================
    private void initZhMap() {
        // ---- 菜单标题 ----
        zhMap.put("menu.title", "===== 员工管理系统 =====");
        zhMap.put("menu.title.en", "Employee Management System");

        // ---- 主菜单项 ----
        zhMap.put("menu.add", "1. 添加员工");
        zhMap.put("menu.delete", "2. 删除员工");
        zhMap.put("menu.update", "3. 修改员工");
        zhMap.put("menu.query", "4. 查询员工");
        zhMap.put("menu.list", "5. 浏览全部");
        zhMap.put("menu.calc_salary", "6. 计算薪资");
        zhMap.put("menu.dept_stats", "7. 部门统计");
        zhMap.put("menu.salary_sort", "8. 薪资排序");
        zhMap.put("menu.salary_filter", "9. 薪资筛选");
        zhMap.put("menu.logs", "10. 操作日志");
        zhMap.put("menu.performance", "11. 设置绩效");
        zhMap.put("menu.export_csv", "12. 导出CSV");
        zhMap.put("menu.bar_chart", "13. 薪资柱状图");
        zhMap.put("menu.undo", "14. 撤销操作");
        zhMap.put("menu.champion", "15. 薪资冠军");
        zhMap.put("menu.raise", "16. 涨薪管理");
        zhMap.put("menu.leave", "17. 请假扣薪");
        zhMap.put("menu.password", "18. 修改密码");
        zhMap.put("menu.backup", "19. 数据备份");
        zhMap.put("menu.transfer", "20. 员工转岗");
        zhMap.put("menu.restore", "21. 从备份恢复");
        zhMap.put("menu.import_emp", "22. 批量导入");
        zhMap.put("menu.report", "23. 月度报表");
        zhMap.put("menu.hire_trend", "24. 入职趋势图");
        zhMap.put("menu.switch_lang", "0. 切换语言");
        zhMap.put("menu.switch_lang_text", "切换语言 / Switch Lang");
        zhMap.put("menu.exit", "99. 退出系统");
        zhMap.put("menu.exit_text", "退出系统 / Exit");

        // ---- 输入提示 ----
        zhMap.put("prompt.choose", "请选择操作 [0-24/99]：");
        zhMap.put("prompt.choose.lang", "请选择语言 / Please select language:");
        zhMap.put("prompt.choose.lang.zh", "1. 中文");
        zhMap.put("prompt.choose.lang.en", "2. English");
        zhMap.put("prompt.choose.lang.prompt", "请选择 [1-2]：");
        zhMap.put("prompt.input.id", "请输入工号：");
        zhMap.put("prompt.input.name", "请输入姓名：");
        zhMap.put("prompt.input.dept", "请输入部门：");
        zhMap.put("prompt.input.contact", "请输入联系方式：");
        zhMap.put("prompt.input.hire_date", "请输入入职日期（格式 yyyy-MM-dd）：");
        zhMap.put("prompt.input.base_salary", "请输入基本工资：");
        zhMap.put("prompt.input.hourly_rate", "请输入时薪：");
        zhMap.put("prompt.input.hours", "请输入工作小时数：");
        zhMap.put("prompt.input.bonus", "请输入奖金：");
        zhMap.put("prompt.input.new_name", "请输入新姓名（留空不修改）：");
        zhMap.put("prompt.input.new_dept", "请输入新部门（留空不修改）：");
        zhMap.put("prompt.input.new_contact", "请输入新联系方式（留空不修改）：");
        zhMap.put("prompt.input.keyword", "请输入关键字（支持模糊匹配工号/姓名/部门）：");
        zhMap.put("prompt.input.min_salary", "请输入最低薪资：");
        zhMap.put("prompt.input.max_salary", "请输入最高薪资：");
        zhMap.put("prompt.input.percent", "请输入涨薪百分比（如输入10代表涨10%）：");
        zhMap.put("prompt.input.dept_name", "请输入部门名称：");
        zhMap.put("prompt.input.leave_days", "请输入请假天数：");
        zhMap.put("prompt.input.old_password", "请输入当前密码：");
        zhMap.put("prompt.input.new_password", "请输入新密码（至少6位）：");
        zhMap.put("prompt.input.confirm_password", "请再次输入新密码确认：");
        zhMap.put("prompt.input.file_path", "请输入导入文件路径（直接回车使用 import_template.txt）：");
        zhMap.put("prompt.input.backup_choice", "请选择要恢复的备份文件编号（输入 0 取消）：");
        zhMap.put("prompt.confirm.delete", "确认删除？(y/n)：");
        zhMap.put("prompt.confirm.raise", "确认涨薪？(y/n)：");
        zhMap.put("prompt.confirm.dept_raise", "确认给整个部门「%s」统一涨薪 %s%%？(y/n)：");
        zhMap.put("prompt.confirm.transfer", "确认转岗？(y/n)：");
        zhMap.put("prompt.confirm.restore", "确认从 %s 恢复数据？当前数据将被覆盖！(y/n)：");
        zhMap.put("prompt.choose.type", "请选择员工类型：");
        zhMap.put("prompt.choose.type.prompt", "请选择 [1-3]：");
        zhMap.put("prompt.choose.query", "请选择查询方式：");
        zhMap.put("prompt.choose.query.prompt", "请选择 [1-4]：");
        zhMap.put("prompt.choose.leave_type", "请选择请假类型：");
        zhMap.put("prompt.choose.leave.prompt", "请选择 [1-3]：");
        zhMap.put("prompt.choose.performance", "请选择绩效等级：");
        zhMap.put("prompt.choose.performance.prompt", "请选择（A/B/C）：");
        zhMap.put("prompt.choose.transfer_type", "请选择要转岗的目标类型：");
        zhMap.put("prompt.choose.transfer.prompt", "请选择 [1-%d]：");
        zhMap.put("prompt.choose.raise_menu", "请选择操作：");
        zhMap.put("prompt.choose.raise.prompt", "请选择 [0-2]：");
        zhMap.put("prompt.pause", "按 Enter 键继续...");
        zhMap.put("prompt.password", "请输入管理员密码：");

        // ---- 子页面标题 ----
        zhMap.put("title.add", "添加员工");
        zhMap.put("title.delete", "删除员工");
        zhMap.put("title.update", "修改员工");
        zhMap.put("title.query", "查询员工");
        zhMap.put("title.list", "全部员工");
        zhMap.put("title.calc_salary", "计算薪资");
        zhMap.put("title.dept_stats", "部门统计");
        zhMap.put("title.salary_sort", "薪资排序（从高到低）");
        zhMap.put("title.salary_filter", "薪资筛选");
        zhMap.put("title.logs", "操作日志");
        zhMap.put("title.performance", "设置绩效");
        zhMap.put("title.export_csv", "导出CSV");
        zhMap.put("title.bar_chart", "薪资柱状图");
        zhMap.put("title.champion", "薪资冠军");
        zhMap.put("title.raise", "涨薪管理");
        zhMap.put("title.raise_single", "单个员工涨薪");
        zhMap.put("title.raise_dept", "部门涨薪");
        zhMap.put("title.leave", "请假扣薪");
        zhMap.put("title.password", "修改密码");
        zhMap.put("title.backup", "数据备份");
        zhMap.put("title.restore", "从备份恢复");
        zhMap.put("title.transfer", "员工转岗");
        zhMap.put("title.import_emp", "批量导入");
        zhMap.put("title.report", "月度报表");

        // ---- 成功/失败/提示消息 ----
        zhMap.put("success.add", "员工添加成功！");
        zhMap.put("success.delete", "删除成功！");
        zhMap.put("success.update", "员工信息修改成功！");
        zhMap.put("success.export", "导出成功！");
        zhMap.put("success.performance", "绩效设置成功！");
        zhMap.put("success.raise", "涨薪成功！");
        zhMap.put("success.dept_raise", "部门涨薪成功！");
        zhMap.put("success.leave", "请假扣薪处理完成！");
        zhMap.put("success.password", "密码修改成功");
        zhMap.put("success.backup", "备份成功！");
        zhMap.put("success.restore", "恢复成功！");
        zhMap.put("success.transfer", "员工转岗成功！");
        zhMap.put("success.auto_backup", "已自动备份数据，文件：");
        zhMap.put("success.import_line", "导入成功：");
        zhMap.put("success.report_saved", "报表已保存至 ");

        zhMap.put("error.format", "输入格式错误，请输入数字！");
        zhMap.put("error.format.number", "数字格式错误！");
        zhMap.put("error.format.date", "日期格式错误，请重新输入（格式 yyyy-MM-dd）！");
        zhMap.put("error.invalid_choice", "无效选项，请重新选择！");
        zhMap.put("error.invalid_choice2", "无效选项！");
        zhMap.put("error.invalid_type", "无效的员工类型！");
        zhMap.put("error.invalid_leave_type", "无效的请假类型！");
        zhMap.put("error.invalid_performance", "无效选择！");
        zhMap.put("error.not_found", "未找到工号为 %s 的员工！");
        zhMap.put("error.not_found.dept", "未找到部门为「%s」的员工！");
        zhMap.put("error.not_found.employee", "未找到该员工");
        zhMap.put("error.not_found.match", "未找到匹配员工");
        zhMap.put("error.dept_empty", "该部门暂无员工");
        zhMap.put("error.employee_empty", "暂无员工信息");
        zhMap.put("error.employee_empty2", "暂无员工数据");
        zhMap.put("error.export_empty", "暂无员工信息，无法导出！");
        zhMap.put("error.min_max", "最低薪资不能高于最高薪资！");
        zhMap.put("error.percent", "涨薪百分比必须大于0！");
        zhMap.put("error.leave_days", "请假天数必须大于0！");
        zhMap.put("error.password_wrong", "当前密码错误！");
        zhMap.put("error.password_short", "密码不能少于6位，请重新输入");
        zhMap.put("error.password_mismatch", "两次密码不一致，请重新输入");
        zhMap.put("error.password_attempts", "密码错误，请重新输入（剩余 %d 次机会）");
        zhMap.put("error.password_exceed", "密码错误次数过多，系统退出");
        zhMap.put("error.backup_fail", "备份失败，请检查 employees.dat 文件是否存在");
        zhMap.put("error.restore_fail", "恢复失败，请检查备份文件是否损坏");
        zhMap.put("error.file_not_found", "文件不存在：");
        zhMap.put("error.read_fail", "文件读取失败：");
        zhMap.put("error.write_fail", "密码保存失败：");
        zhMap.put("error.export_fail", "导出失败：");
        zhMap.put("error.add_fail", "添加失败：");
        zhMap.put("error.raise_fail", "涨薪失败：");
        zhMap.put("error.leave_fail", "操作失败：");
        zhMap.put("error.transfer_fail", "转岗失败：");
        zhMap.put("error.config_read", "读取配置文件失败");
        zhMap.put("error.config_create", "创建配置文件失败");
        zhMap.put("error.backup_dir", "backup/ 文件夹不存在，暂无备份文件");
        zhMap.put("error.backup_empty", "backup/ 文件夹下没有找到备份文件");
        zhMap.put("error.cancel", "已取消删除");
        zhMap.put("error.cancel.raise", "已取消涨薪");
        zhMap.put("error.cancel.restore", "已取消恢复操作");
        zhMap.put("error.cancel.transfer", "已取消转岗");

        // ---- 信息提示 ----
        zhMap.put("info.employee_info", "员工信息：");
        zhMap.put("info.current_info", "当前信息：");
        zhMap.put("info.query_result", "查询结果：");
        zhMap.put("info.query_result.count", "查询结果（共 %d 条）：");
        zhMap.put("info.dept_count", "该部门共有 %d 名员工：");
        zhMap.put("info.fuzzy_result", "模糊搜索结果（共 %d 条）：");
        zhMap.put("info.total_employees", "当前共有 %d 名员工");
        zhMap.put("info.employee_count", "共 %d 名员工");
        zhMap.put("info.salary_range", "名员工，薪资范围：");
        zhMap.put("info.backing_up", "正在备份数据...");
        zhMap.put("info.backup_list", "可用的备份文件列表：");
        zhMap.put("info.config_created", "已创建配置文件 ");
        zhMap.put("info.import_summary", "导入汇总");
        zhMap.put("info.import_success", "成功导入：%d 条");
        zhMap.put("info.import_duplicate", "跳过（工号重复）：%d 条");
        zhMap.put("info.import_error", "失败（格式错误）：%d 条");
        zhMap.put("info.import_total", "共处理：%d 条（不含表头）");
        zhMap.put("info.page_info", "第 %d 页 / 共 %d 页");
        zhMap.put("info.nav_prompt", "p 上一页  n 下一页  q 退出");
        zhMap.put("info.welcome_login", "登录成功！欢迎使用，管理员");
        zhMap.put("info.system_title", "员工管理系统  v1.0");
        zhMap.put("info.system_title.en", "Employee Management System");

        // ---- 员工类型 ----
        zhMap.put("type.fulltime", "全职");
        zhMap.put("type.parttime", "兼职");
        zhMap.put("type.manager", "经理");
        zhMap.put("type.management", "管理层");
        zhMap.put("type.other", "其他");

        // ---- 员工类型选择 ----
        zhMap.put("type.option.fulltime", "1. 全职员工");
        zhMap.put("type.option.parttime", "2. 兼职员工");
        zhMap.put("type.option.manager", "3. 经理");

        // ---- 查询方式 ----
        zhMap.put("query.by_id", "1. 按工号查询");
        zhMap.put("query.by_name", "2. 按姓名查询");
        zhMap.put("query.by_dept", "3. 按部门查询");
        zhMap.put("query.fuzzy", "4. 模糊搜索");

        // ---- 绩效等级 ----
        zhMap.put("performance.current", "当前绩效：");
        zhMap.put("performance.a", "A. A级（奖金比例 20%）");
        zhMap.put("performance.b", "B. B级（奖金比例 10%）");
        zhMap.put("performance.c", "C. C级（奖金比例 0%）");

        // ---- 请假类型 ----
        zhMap.put("leave.annual", "1. 年假（扣薪比例 0%）");
        zhMap.put("leave.sick", "2. 病假（扣薪比例 50%）");
        zhMap.put("leave.personal", "3. 事假（扣薪比例 100%）");

        // ---- 涨薪管理子菜单 ----
        zhMap.put("raise.single", "1. 给单个员工涨薪");
        zhMap.put("raise.dept", "2. 给整个部门涨薪");
        zhMap.put("raise.back", "0. 返回主菜单");

        // ---- 薪资冠军 ----
        zhMap.put("champion.title", "薪资冠军（最高薪资）");
        zhMap.put("champion.lowest", "薪资垫底（最低薪资）");
        zhMap.put("champion.gap", "薪资差距分析");
        zhMap.put("champion.gap_value", "薪资差距：");
        zhMap.put("champion.multiple", "最高薪资是最低薪资的 ");

        // ---- 薪资柱状图 ----
        zhMap.put("chart.title", "薪资分布 (单位：元)");
        zhMap.put("chart.stats", "统计信息");
        zhMap.put("chart.max", "最高薪资：");
        zhMap.put("chart.min", "最低薪资：");
        zhMap.put("chart.avg", "平均薪资：");

        // ---- 涨薪对比 ----
        zhMap.put("raise.compare_title", "涨薪前后对比：");
        zhMap.put("raise.before", "涨薪前：");
        zhMap.put("raise.after", "涨薪后：");
        zhMap.put("raise.percent", "涨薪幅度：");
        zhMap.put("raise.amount", "涨薪金额：");
        zhMap.put("raise.dept_compare", "以下为每人涨薪前后对比：");

        // ---- 转岗 ----
        zhMap.put("transfer.compare_title", "转岗前后对比：");
        zhMap.put("transfer.before_type", "转岗前类型：");
        zhMap.put("transfer.after_type", "转岗后类型：");
        zhMap.put("transfer.before_salary", "转岗前薪资：");
        zhMap.put("transfer.after_salary", "转岗后薪资：");

        // ---- 请假 ----
        zhMap.put("leave.detail_title", "请假扣薪明细：");
        zhMap.put("leave.employee_info", "员工：");
        zhMap.put("leave.monthly_salary", "月薪：");

        // ---- 表格表头 ----
        zhMap.put("table.rank", "排名");
        zhMap.put("table.id", "工号");
        zhMap.put("table.name", "姓名");
        zhMap.put("table.dept", "部门");
        zhMap.put("table.type", "类型");
        zhMap.put("table.performance", "绩效");
        zhMap.put("table.years", "工龄");
        zhMap.put("table.salary", "薪资");
        zhMap.put("table.dept_stats", "部门");
        zhMap.put("table.count", "人数");
        zhMap.put("table.total_salary", "薪资总额");

        // ---- 退出画面 ----
        zhMap.put("exit.thanks", "感谢使用员工管理系统！");
        zhMap.put("exit.goodbye", "再 见 ！");

        // ---- 其他 ----
        zhMap.put("other.file_name", "文件名：");
        zhMap.put("other.highest_salary", "最高薪资：");
        zhMap.put("other.current_time", "当前时间：");
        zhMap.put("other.total_employees", "系统当前员工总数：");
        zhMap.put("other.unit_years", "年");
        zhMap.put("other.unit_people", "人");
        zhMap.put("other.unit_yuan", "元");
        zhMap.put("other.employee", "员工：");
        zhMap.put("other.salary", "薪资：");
        zhMap.put("other.id", "工号：");
        zhMap.put("other.name", "姓名：");
        zhMap.put("other.dept", "部门：");
        zhMap.put("other.type", "类型：");
        zhMap.put("other.performance", "绩效：");
        zhMap.put("other.years", "工龄：");
        zhMap.put("other.updated_to", " 的绩效已更新为 ");
        zhMap.put("other.salary_updated", " 的薪资已更新为 ");
        zhMap.put("other.transfer_detail", " 从 %s 转岗为 %s");
        zhMap.put("other.import_line_detail", "第 %d 行导入成功：");
        zhMap.put("other.import_line_error", "第 %d 行格式错误（字段数不足）：");
        zhMap.put("other.import_line_duplicate", "第 %d 行工号重复，已跳过：");
        zhMap.put("other.import_line_date_error", "第 %d 行日期格式错误：");
        zhMap.put("other.import_line_missing_param", "第 %d 行全职员工缺少基本工资参数");
        zhMap.put("other.import_line_missing_param2", "第 %d 行兼职员工缺少时薪或工作小时数");
        zhMap.put("other.import_line_missing_param3", "第 %d 行管理层员工缺少基本工资或奖金");
        zhMap.put("other.import_line_unknown_type", "第 %d 行未知员工类型：");
        zhMap.put("other.import_line_num_error", "第 %d 行数字格式错误：");
        zhMap.put("other.import_line_fail", "第 %d 行导入失败：");
        zhMap.put("other.import_line_unknown_error", "第 %d 行发生未知错误：");
        zhMap.put("other.leave_detail", "请假扣薪明细：");
        zhMap.put("other.employee_type", "当前类型：");
        zhMap.put("other.current_salary", "当前薪资：");
        zhMap.put("other.new_salary", "转岗后薪资：");
        zhMap.put("other.lang_switched", "语言已切换为：");
        zhMap.put("other.lang_switched.en", "Language switched to: ");
    }

    // ===================== 英文提示语 =====================
    private void initEnMap() {
        // ---- Menu Title ----
        enMap.put("menu.title", "===== Employee Management System =====");
        enMap.put("menu.title.en", "Employee Management System");

        // ---- Main Menu Items ----
        enMap.put("menu.add", "1. Add Employee");
        enMap.put("menu.delete", "2. Delete Employee");
        enMap.put("menu.update", "3. Update Employee");
        enMap.put("menu.query", "4. Query Employee");
        enMap.put("menu.list", "5. List All");
        enMap.put("menu.calc_salary", "6. Calculate Salary");
        enMap.put("menu.dept_stats", "7. Dept Statistics");
        enMap.put("menu.salary_sort", "8. Salary Sort");
        enMap.put("menu.salary_filter", "9. Salary Filter");
        enMap.put("menu.logs", "10. Operation Logs");
        enMap.put("menu.performance", "11. Set Performance");
        enMap.put("menu.export_csv", "12. Export CSV");
        enMap.put("menu.bar_chart", "13. Salary Chart");
        enMap.put("menu.undo", "14. Undo");
        enMap.put("menu.champion", "15. Salary Champion");
        enMap.put("menu.raise", "16. Salary Raise");
        enMap.put("menu.leave", "17. Leave Deduction");
        enMap.put("menu.password", "18. Change Password");
        enMap.put("menu.backup", "19. Data Backup");
        enMap.put("menu.transfer", "20. Transfer Employee");
        enMap.put("menu.restore", "21. Restore Backup");
        enMap.put("menu.import_emp", "22. Batch Import");
        enMap.put("menu.report", "23. Monthly Report");
        enMap.put("menu.hire_trend", "24. Hire Trend");
        enMap.put("menu.switch_lang", "0. Switch Language");
        enMap.put("menu.switch_lang_text", "Switch Language / 切换语言");
        enMap.put("menu.exit", "99. Exit");
        enMap.put("menu.exit_text", "Exit / 退出系统");

        // ---- Input Prompts ----
        enMap.put("prompt.choose", "Please choose [0-23]: ");
        enMap.put("prompt.choose.lang", "请选择语言 / Please select language:");
        enMap.put("prompt.choose.lang.zh", "1. 中文");
        enMap.put("prompt.choose.lang.en", "2. English");
        enMap.put("prompt.choose.lang.prompt", "Please select [1-2]: ");
        enMap.put("prompt.input.id", "Enter employee ID: ");
        enMap.put("prompt.input.name", "Enter name: ");
        enMap.put("prompt.input.dept", "Enter department: ");
        enMap.put("prompt.input.contact", "Enter contact info: ");
        enMap.put("prompt.input.hire_date", "Enter hire date (yyyy-MM-dd): ");
        enMap.put("prompt.input.base_salary", "Enter base salary: ");
        enMap.put("prompt.input.hourly_rate", "Enter hourly rate: ");
        enMap.put("prompt.input.hours", "Enter hours worked: ");
        enMap.put("prompt.input.bonus", "Enter bonus: ");
        enMap.put("prompt.input.new_name", "Enter new name (leave blank to keep): ");
        enMap.put("prompt.input.new_dept", "Enter new department (leave blank to keep): ");
        enMap.put("prompt.input.new_contact", "Enter new contact (leave blank to keep): ");
        enMap.put("prompt.input.keyword", "Enter keyword (ID/Name/Dept fuzzy search): ");
        enMap.put("prompt.input.min_salary", "Enter min salary: ");
        enMap.put("prompt.input.max_salary", "Enter max salary: ");
        enMap.put("prompt.input.percent", "Enter raise percentage (e.g. 10 for 10%): ");
        enMap.put("prompt.input.dept_name", "Enter department name: ");
        enMap.put("prompt.input.leave_days", "Enter leave days: ");
        enMap.put("prompt.input.old_password", "Enter current password: ");
        enMap.put("prompt.input.new_password", "Enter new password (at least 6 chars): ");
        enMap.put("prompt.input.confirm_password", "Confirm new password: ");
        enMap.put("prompt.input.file_path", "Enter import file path (Enter for import_template.txt): ");
        enMap.put("prompt.input.backup_choice", "Select backup file number (0 to cancel): ");
        enMap.put("prompt.confirm.delete", "Confirm delete? (y/n): ");
        enMap.put("prompt.confirm.raise", "Confirm raise? (y/n): ");
        enMap.put("prompt.confirm.dept_raise", "Confirm raise for dept「%s」by %s%%? (y/n): ");
        enMap.put("prompt.confirm.transfer", "Confirm transfer? (y/n): ");
        enMap.put("prompt.confirm.restore", "Confirm restore from %s? Current data will be overwritten! (y/n): ");
        enMap.put("prompt.choose.type", "Select employee type:");
        enMap.put("prompt.choose.type.prompt", "Choose [1-3]: ");
        enMap.put("prompt.choose.query", "Select query method:");
        enMap.put("prompt.choose.query.prompt", "Choose [1-4]: ");
        enMap.put("prompt.choose.leave_type", "Select leave type:");
        enMap.put("prompt.choose.leave.prompt", "Choose [1-3]: ");
        enMap.put("prompt.choose.performance", "Select performance level:");
        enMap.put("prompt.choose.performance.prompt", "Choose (A/B/C): ");
        enMap.put("prompt.choose.transfer_type", "Select target employee type:");
        enMap.put("prompt.choose.transfer.prompt", "Choose [1-%d]: ");
        enMap.put("prompt.choose.raise_menu", "Select operation:");
        enMap.put("prompt.choose.raise.prompt", "Choose [0-2]: ");
        enMap.put("prompt.pause", "Press Enter to continue...");
        enMap.put("prompt.password", "Enter admin password: ");

        // ---- Sub Page Titles ----
        enMap.put("title.add", "Add Employee");
        enMap.put("title.delete", "Delete Employee");
        enMap.put("title.update", "Update Employee");
        enMap.put("title.query", "Query Employee");
        enMap.put("title.list", "All Employees");
        enMap.put("title.calc_salary", "Calculate Salary");
        enMap.put("title.dept_stats", "Department Statistics");
        enMap.put("title.salary_sort", "Salary Sort (High to Low)");
        enMap.put("title.salary_filter", "Salary Filter");
        enMap.put("title.logs", "Operation Logs");
        enMap.put("title.performance", "Set Performance");
        enMap.put("title.export_csv", "Export CSV");
        enMap.put("title.bar_chart", "Salary Bar Chart");
        enMap.put("title.champion", "Salary Champion");
        enMap.put("title.raise", "Salary Raise");
        enMap.put("title.raise_single", "Raise Single Employee");
        enMap.put("title.raise_dept", "Raise Department");
        enMap.put("title.leave", "Leave Deduction");
        enMap.put("title.password", "Change Password");
        enMap.put("title.backup", "Data Backup");
        enMap.put("title.restore", "Restore from Backup");
        enMap.put("title.transfer", "Transfer Employee");
        enMap.put("title.import_emp", "Batch Import");
        enMap.put("title.report", "Monthly Report");

        // ---- Success/Fail/Info Messages ----
        enMap.put("success.add", "Employee added successfully!");
        enMap.put("success.delete", "Deleted successfully!");
        enMap.put("success.update", "Employee info updated successfully!");
        enMap.put("success.export", "Export successful!");
        enMap.put("success.performance", "Performance set successfully!");
        enMap.put("success.raise", "Salary raised successfully!");
        enMap.put("success.dept_raise", "Department salary raised successfully!");
        enMap.put("success.leave", "Leave deduction completed!");
        enMap.put("success.password", "Password changed successfully");
        enMap.put("success.backup", "Backup successful!");
        enMap.put("success.restore", "Restore successful!");
        enMap.put("success.transfer", "Employee transferred successfully!");
        enMap.put("success.auto_backup", "Auto backup saved, file: ");
        enMap.put("success.import_line", "Imported successfully: ");
        enMap.put("success.report_saved", "Report saved to ");

        enMap.put("error.format", "Invalid input format, please enter a number!");
        enMap.put("error.format.number", "Invalid number format!");
        enMap.put("error.format.date", "Invalid date format, please use yyyy-MM-dd!");
        enMap.put("error.invalid_choice", "Invalid choice, please try again!");
        enMap.put("error.invalid_choice2", "Invalid option!");
        enMap.put("error.invalid_type", "Invalid employee type!");
        enMap.put("error.invalid_leave_type", "Invalid leave type!");
        enMap.put("error.invalid_performance", "Invalid choice!");
        enMap.put("error.not_found", "Employee with ID %s not found!");
        enMap.put("error.not_found.dept", "No employees found in department「%s」!");
        enMap.put("error.not_found.employee", "Employee not found");
        enMap.put("error.not_found.match", "No matching employees found");
        enMap.put("error.dept_empty", "No employees in this department");
        enMap.put("error.employee_empty", "No employee data");
        enMap.put("error.employee_empty2", "No employee data available");
        enMap.put("error.export_empty", "No employee data to export!");
        enMap.put("error.min_max", "Min salary cannot be higher than max salary!");
        enMap.put("error.percent", "Raise percentage must be greater than 0!");
        enMap.put("error.leave_days", "Leave days must be greater than 0!");
        enMap.put("error.password_wrong", "Current password is incorrect!");
        enMap.put("error.password_short", "Password must be at least 6 characters!");
        enMap.put("error.password_mismatch", "Passwords do not match, please re-enter");
        enMap.put("error.password_attempts", "Wrong password, %d attempts remaining");
        enMap.put("error.password_exceed", "Too many incorrect attempts, system exiting");
        enMap.put("error.backup_fail", "Backup failed, check if employees.dat exists");
        enMap.put("error.restore_fail", "Restore failed, backup file may be corrupted");
        enMap.put("error.file_not_found", "File not found: ");
        enMap.put("error.read_fail", "File read failed: ");
        enMap.put("error.write_fail", "Password save failed: ");
        enMap.put("error.export_fail", "Export failed: ");
        enMap.put("error.add_fail", "Add failed: ");
        enMap.put("error.raise_fail", "Raise failed: ");
        enMap.put("error.leave_fail", "Operation failed: ");
        enMap.put("error.transfer_fail", "Transfer failed: ");
        enMap.put("error.config_read", "Failed to read config file");
        enMap.put("error.config_create", "Failed to create config file");
        enMap.put("error.backup_dir", "backup/ directory not found, no backups available");
        enMap.put("error.backup_empty", "No backup files found in backup/ directory");
        enMap.put("error.cancel", "Delete cancelled");
        enMap.put("error.cancel.raise", "Raise cancelled");
        enMap.put("error.cancel.restore", "Restore cancelled");
        enMap.put("error.cancel.transfer", "Transfer cancelled");

        // ---- Info ----
        enMap.put("info.employee_info", "Employee Info: ");
        enMap.put("info.current_info", "Current Info: ");
        enMap.put("info.query_result", "Query Result: ");
        enMap.put("info.query_result.count", "Query result (%d records): ");
        enMap.put("info.dept_count", "Department has %d employees: ");
        enMap.put("info.fuzzy_result", "Fuzzy search result (%d records): ");
        enMap.put("info.total_employees", "Total: %d employees");
        enMap.put("info.employee_count", "%d employees");
        enMap.put("info.salary_range", " employees, salary range: ");
        enMap.put("info.backing_up", "Backing up data...");
        enMap.put("info.backup_list", "Available backup files:");
        enMap.put("info.config_created", "Config file created ");
        enMap.put("info.import_summary", "Import Summary");
        enMap.put("info.import_success", "Successfully imported: %d");
        enMap.put("info.import_duplicate", "Skipped (duplicate ID): %d");
        enMap.put("info.import_error", "Failed (format error): %d");
        enMap.put("info.import_total", "Total processed: %d (excluding header)");
        enMap.put("info.page_info", "Page %d / %d");
        enMap.put("info.nav_prompt", "p Prev  n Next  q Quit");
        enMap.put("info.welcome_login", "Login successful! Welcome, Admin");
        enMap.put("info.system_title", "Employee Management System  v1.0");
        enMap.put("info.system_title.en", "Employee Management System");

        // ---- Employee Types ----
        enMap.put("type.fulltime", "Full-Time");
        enMap.put("type.parttime", "Part-Time");
        enMap.put("type.manager", "Manager");
        enMap.put("type.management", "Management");
        enMap.put("type.other", "Other");

        // ---- Employee Type Options ----
        enMap.put("type.option.fulltime", "1. Full-Time Employee");
        enMap.put("type.option.parttime", "2. Part-Time Employee");
        enMap.put("type.option.manager", "3. Manager");

        // ---- Query Methods ----
        enMap.put("query.by_id", "1. Search by ID");
        enMap.put("query.by_name", "2. Search by Name");
        enMap.put("query.by_dept", "3. Search by Department");
        enMap.put("query.fuzzy", "4. Fuzzy Search");

        // ---- Performance Levels ----
        enMap.put("performance.current", "Current Performance: ");
        enMap.put("performance.a", "A. A Level (Bonus 20%)");
        enMap.put("performance.b", "B. B Level (Bonus 10%)");
        enMap.put("performance.c", "C. C Level (Bonus 0%)");

        // ---- Leave Types ----
        enMap.put("leave.annual", "1. Annual Leave (Deduction 0%)");
        enMap.put("leave.sick", "2. Sick Leave (Deduction 50%)");
        enMap.put("leave.personal", "3. Personal Leave (Deduction 100%)");

        // ---- Salary Raise Submenu ----
        enMap.put("raise.single", "1. Raise Single Employee");
        enMap.put("raise.dept", "2. Raise Entire Department");
        enMap.put("raise.back", "0. Back to Main Menu");

        // ---- Salary Champion ----
        enMap.put("champion.title", "Salary Champion (Highest)");
        enMap.put("champion.lowest", "Salary Lowest");
        enMap.put("champion.gap", "Salary Gap Analysis");
        enMap.put("champion.gap_value", "Salary Gap: ");
        enMap.put("champion.multiple", "Highest salary is ");

        // ---- Salary Chart ----
        enMap.put("chart.title", "Salary Distribution (CNY)");
        enMap.put("chart.stats", "Statistics");
        enMap.put("chart.max", "Max Salary: ");
        enMap.put("chart.min", "Min Salary: ");
        enMap.put("chart.avg", "Avg Salary: ");

        // ---- Raise Comparison ----
        enMap.put("raise.compare_title", "Salary Comparison:");
        enMap.put("raise.before", "Before Raise: ");
        enMap.put("raise.after", "After Raise: ");
        enMap.put("raise.percent", "Raise Rate: ");
        enMap.put("raise.amount", "Raise Amount: ");
        enMap.put("raise.dept_compare", "Comparison for each employee:");

        // ---- Transfer ----
        enMap.put("transfer.compare_title", "Transfer Comparison:");
        enMap.put("transfer.before_type", "Before Type: ");
        enMap.put("transfer.after_type", "After Type: ");
        enMap.put("transfer.before_salary", "Before Salary: ");
        enMap.put("transfer.after_salary", "After Salary: ");

        // ---- Leave ----
        enMap.put("leave.detail_title", "Leave Deduction Details:");
        enMap.put("leave.employee_info", "Employee: ");
        enMap.put("leave.monthly_salary", "Monthly Salary: ");

        // ---- Table Headers ----
        enMap.put("table.rank", "Rank");
        enMap.put("table.id", "ID");
        enMap.put("table.name", "Name");
        enMap.put("table.dept", "Department");
        enMap.put("table.type", "Type");
        enMap.put("table.performance", "Perf");
        enMap.put("table.years", "Years");
        enMap.put("table.salary", "Salary");
        enMap.put("table.dept_stats", "Department");
        enMap.put("table.count", "Count");
        enMap.put("table.total_salary", "Total Salary");

        // ---- Exit Screen ----
        enMap.put("exit.thanks", "Thank you for using Employee Management System!");
        enMap.put("exit.goodbye", "Goodbye!");

        // ---- Other ----
        enMap.put("other.file_name", "File: ");
        enMap.put("other.highest_salary", "Highest Salary: ");
        enMap.put("other.current_time", "Current Time: ");
        enMap.put("other.total_employees", "Total Employees: ");
        enMap.put("other.unit_years", " years");
        enMap.put("other.unit_people", "");
        enMap.put("other.unit_yuan", " yuan");
        enMap.put("other.employee", "Employee: ");
        enMap.put("other.salary", "Salary: ");
        enMap.put("other.id", "ID: ");
        enMap.put("other.name", "Name: ");
        enMap.put("other.dept", "Dept: ");
        enMap.put("other.type", "Type: ");
        enMap.put("other.performance", "Performance: ");
        enMap.put("other.years", "Years: ");
        enMap.put("other.updated_to", "'s performance updated to ");
        enMap.put("other.salary_updated", "'s salary updated to ");
        enMap.put("other.transfer_detail", " transferred from %s to %s");
        enMap.put("other.import_line_detail", "Line %d imported successfully: ");
        enMap.put("other.import_line_error", "Line %d format error (insufficient fields): ");
        enMap.put("other.import_line_duplicate", "Line %d duplicate ID, skipped: ");
        enMap.put("other.import_line_date_error", "Line %d date format error: ");
        enMap.put("other.import_line_missing_param", "Line %d full-time employee missing base salary");
        enMap.put("other.import_line_missing_param2", "Line %d part-time employee missing hourly rate or hours");
        enMap.put("other.import_line_missing_param3", "Line %d manager missing base salary or bonus");
        enMap.put("other.import_line_unknown_type", "Line %d unknown employee type: ");
        enMap.put("other.import_line_num_error", "Line %d number format error: ");
        enMap.put("other.import_line_fail", "Line %d import failed: ");
        enMap.put("other.import_line_unknown_error", "Line %d unknown error: ");
        enMap.put("other.leave_detail", "Leave deduction details:");
        enMap.put("other.employee_type", "Current Type: ");
        enMap.put("other.current_salary", "Current Salary: ");
        enMap.put("other.new_salary", "New Salary: ");
        enMap.put("other.lang_switched", "Language switched to: ");
        enMap.put("other.lang_switched.en", "Language switched to: ");
    }

    /**
     * 获取当前语言对应的文字
     * @param key 提示语编号
     * @return 对应语言的文字，如果key不存在返回key本身
     */
    public String get(String key) {
        Map<String, String> map = "zh".equals(currentLang) ? zhMap : enMap;
        return map.getOrDefault(key, key);
    }

    /**
     * 获取当前语言对应的文字（支持格式化参数）
     * @param key 提示语编号
     * @param args 格式化参数
     * @return 格式化后的文字
     */
    public String get(String key, Object... args) {
        String text = get(key);
        return String.format(text, args);
    }

    /**
     * 切换语言（zh <-> en）
     */
    public void switchLang() {
        if ("zh".equals(currentLang)) {
            currentLang = "en";
        } else {
            currentLang = "zh";
        }
    }

    /**
     * 获取当前语言代码
     */
    public String getCurrentLang() {
        return currentLang;
    }

    /**
     * 设置语言
     * @param lang "zh" 或 "en"
     */
    public void setLang(String lang) {
        if ("zh".equals(lang) || "en".equals(lang)) {
            this.currentLang = lang;
        }
    }

    /**
     * 从配置文件加载语言设置
     * @param configFile 配置文件路径
     */
    public void loadLangFromConfig(String configFile) {
        File file = new File(configFile);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                // 第一行是密码，第二行是语言设置
                String line1 = reader.readLine(); // 密码
                String line2 = reader.readLine(); // 语言
                if (line2 != null && line2.trim().equals("en")) {
                    currentLang = "en";
                } else {
                    currentLang = "zh";
                }
            } catch (IOException e) {
                // 读取失败，使用默认语言
                currentLang = "zh";
            }
        }
    }

    /**
     * 保存语言设置到配置文件
     * @param configFile 配置文件路径
     * @param password 当前密码（需要保留）
     */
    public void saveLangToConfig(String configFile, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, StandardCharsets.UTF_8))) {
            writer.write(password);
            writer.newLine();
            writer.write(currentLang);
        } catch (IOException e) {
            // 保存失败，忽略
        }
    }
}
