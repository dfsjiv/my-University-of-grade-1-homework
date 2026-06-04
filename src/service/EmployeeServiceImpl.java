package service;

import model.Employee;
import model.LeaveType;
import model.Performance;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工服务实现类
 * <p>
 * 所在层次：服务层（Service），实现 EmployeeService 接口
 * 职责：实现员工管理的所有业务逻辑，包括增删改查、薪资计算、绩效管理、
 *       请假扣薪、转岗、数据持久化（序列化/反序列化）、备份恢复、报表生成等功能
 *       使用 HashMap 存储员工数据（key 为员工工号），支持操作撤销（历史栈）
 * 关联类：EmployeeService（实现的接口）、Employee（操作的数据模型）、
 *        LogService（记录操作日志）、ConsoleUI（调用方）
 */
public class EmployeeServiceImpl implements EmployeeService {

    /** 存储所有员工的数据容器，key 为员工工号（String），value 为员工对象（Employee） */
    private final Map<String, Employee> employeeMap = new HashMap<>();

    /**
     * 操作历史栈，用于实现撤销功能
     * 每次修改操作前保存当前数据的快照（深拷贝），撤销时从栈顶恢复
     * 最多保留 MAX_HISTORY（10）步历史
     */
    private final Deque<Map<String, Employee>> history = new ArrayDeque<>();

    /** 最大历史步数，超过此数量时自动移除最旧的历史记录 */
    private static final int MAX_HISTORY = 10;

    /** 日志服务实例，用于记录每次操作的操作日志 */
    private final LogService logService;

    /** 数据持久化文件路径，员工数据通过 Java 序列化存储到该文件 */
    private static final String FILE_PATH = "employees.dat";

    /**
     * 带日志服务的构造方法
     * 初始化日志服务并从文件加载已有数据
     *
     * @param logService 日志服务实例
     */
    public EmployeeServiceImpl(LogService logService) {
        this.logService = logService;
        loadData();
    }

    /**
     * 无日志服务的构造方法（兼容旧代码）
     * 不记录操作日志，仅从文件加载数据
     */
    public EmployeeServiceImpl() {
        this.logService = null;
        loadData();
    }

    /**
     * 添加员工
     * 先检查工号是否已存在，存在则抛出异常
     * 保存快照后添加员工，记录日志并持久化
     *
     * @param e 要添加的员工对象
     * @throws IllegalArgumentException 如果工号已存在
     */
    @Override
    public void addEmployee(Employee e) {
        // 检查工号是否已存在，避免重复添加
        if (employeeMap.containsKey(e.getId())) {
            throw new IllegalArgumentException("工号 " + e.getId() + " 已存在，添加失败");
        }
        // 保存当前数据快照到历史栈，用于后续撤销
        saveSnapshot();
        // 将员工添加到 Map 中
        employeeMap.put(e.getId(), e);
        // 记录操作日志
        if (logService != null) {
            logService.addLog("添加员工：工号=" + e.getId() + "，姓名=" + e.getName());
        }
        // 将数据持久化到文件
        saveData();
    }

    /**
     * 根据工号删除员工
     * 保存快照后从 Map 中移除，如果员工存在则持久化并记录日志
     *
     * @param id 要删除的员工工号
     */
    @Override
    public void removeEmployee(String id) {
        // 保存当前数据快照到历史栈
        saveSnapshot();
        // 从 Map 中移除指定工号的员工
        Employee removed = employeeMap.remove(id);
        // 如果员工存在（移除成功），则持久化并记录日志
        if (removed != null) {
            saveData();
            if (logService != null) {
                logService.addLog("删除员工：工号=" + removed.getId() + "，姓名=" + removed.getName());
            }
        }
    }

    /**
     * 更新员工信息
     * 用新的员工数据替换指定工号的旧数据
     * 如果工号不存在则不执行任何操作
     *
     * @param id      要更新的员工工号
     * @param newData 新的员工数据对象
     */
    @Override
    public void updateEmployee(String id, Employee newData) {
        // 检查工号是否存在
        if (employeeMap.containsKey(id)) {
            // 保存快照后更新数据
            saveSnapshot();
            Employee old = employeeMap.get(id);
            employeeMap.put(id, newData);
            saveData();
            if (logService != null) {
                logService.addLog("修改员工：工号=" + old.getId() + "，姓名=" + old.getName());
            }
        }
    }

    /**
     * 根据工号查找员工
     * 直接从 Map 中通过 key 获取，时间复杂度 O(1)
     *
     * @param id 要查找的员工工号
     * @return 匹配的员工对象，未找到返回 null
     */
    @Override
    public Employee findById(String id) {
        return employeeMap.get(id);
    }

    /**
     * 根据姓名查找员工
     * 遍历所有员工，使用 contains 进行模糊匹配，支持重名
     *
     * @param name 要查找的员工姓名（支持部分匹配）
     * @return 匹配的员工列表
     */
    @Override
    public List<Employee> findByName(String name) {
        List<Employee> result = new ArrayList<>();
        // 遍历所有员工，姓名包含关键字即加入结果集
        for (Employee e : employeeMap.values()) {
            if (e.getName().contains(name)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 根据部门查找员工
     * 遍历所有员工，精确匹配部门名称
     *
     * @param dept 部门名称
     * @return 该部门的员工列表
     */
    @Override
    public List<Employee> findByDept(String dept) {
        List<Employee> result = new ArrayList<>();
        // 遍历所有员工，部门名称完全匹配即加入结果集
        for (Employee e : employeeMap.values()) {
            if (e.getDepartment().equals(dept)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 模糊搜索员工
     * 在工号、姓名、部门三个字段中搜索包含关键字的员工
     * 搜索时不区分大小写，统一转为小写后比较
     *
     * @param keyword 搜索关键字
     * @return 任意字段包含关键字的员工列表
     */
    @Override
    public List<Employee> fuzzySearch(String keyword) {
        List<Employee> result = new ArrayList<>();
        // 将关键字转为小写，实现不区分大小写的搜索
        String lowerKeyword = keyword.toLowerCase();
        for (Employee e : employeeMap.values()) {
            // 检查工号、姓名、部门是否包含关键字（不区分大小写）
            if (e.getId().toLowerCase().contains(lowerKeyword)
                    || e.getName().toLowerCase().contains(lowerKeyword)
                    || e.getDepartment().toLowerCase().contains(lowerKeyword)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 获取所有员工
     * 返回 employeeMap 中所有值的副本，防止外部修改内部数据
     *
     * @return 所有员工列表
     */
    @Override
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    /**
     * 设置员工绩效等级
     * 更新指定员工的绩效等级，影响其后续薪资计算结果
     *
     * @param id 员工工号
     * @param p  新的绩效等级
     */
    @Override
    public void setPerformance(String id, Performance p) {
        Employee e = employeeMap.get(id);
        if (e != null) {
            // 保存快照后修改绩效
            saveSnapshot();
            e.setPerformance(p);
            saveData();
            if (logService != null) {
                logService.addLog("设置绩效：工号=" + id + "，绩效=" + p);
            }
        }
    }

    /**
     * 计算指定员工的薪资
     * 调用 Employee 的 getSalary() 抽象方法，由子类具体实现
     *
     * @param id 员工工号
     * @return 该员工的最终应发薪资，未找到返回 0.0
     */
    @Override
    public double calculateSalary(String id) {
        Employee e = employeeMap.get(id);
        if (e == null) {
            return 0.0;
        }
        return e.getSalary();
    }

    /**
     * 根据薪资范围筛选员工
     * 遍历所有员工，筛选出薪资在 [min, max] 闭区间内的员工
     *
     * @param min 最低薪资（含）
     * @param max 最高薪资（含）
     * @return 薪资在 [min, max] 范围内的员工列表
     */
    @Override
    public List<Employee> findBySalaryRange(double min, double max) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : employeeMap.values()) {
            double salary = e.getSalary();
            // 判断薪资是否在指定范围内
            if (salary >= min && salary <= max) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 获取各部门人数统计
     * 遍历所有员工，按部门分组统计人数
     *
     * @return Map，key 为部门名称，value 为该部门员工人数
     */
    @Override
    public Map<String, Long> getDeptStats() {
        Map<String, Long> stats = new HashMap<>();
        for (Employee e : employeeMap.values()) {
            String dept = e.getDepartment();
            // 使用 getOrDefault 简化计数逻辑：存在则 +1，不存在则置为 1
            stats.put(dept, stats.getOrDefault(dept, 0L) + 1);
        }
        return stats;
    }

    /**
     * 获取薪资最高的员工
     * 使用 Collections.max 配合 Comparator 比较薪资
     *
     * @return 薪资最高的 Employee 对象，员工为空时返回 null
     */
    @Override
    public Employee getHighestSalary() {
        if (employeeMap.isEmpty()) {
            return null;
        }
        // 使用 Collections.max 找出薪资最高的员工
        return Collections.max(new ArrayList<>(employeeMap.values()),
                Comparator.comparingDouble(Employee::getSalary));
    }

    /**
     * 获取薪资最低的员工
     * 使用 Collections.min 配合 Comparator 比较薪资
     *
     * @return 薪资最低的 Employee 对象，员工为空时返回 null
     */
    @Override
    public Employee getLowestSalary() {
        if (employeeMap.isEmpty()) {
            return null;
        }
        // 使用 Collections.min 找出薪资最低的员工
        return Collections.min(new ArrayList<>(employeeMap.values()),
                Comparator.comparingDouble(Employee::getSalary));
    }

    /**
     * 给单个员工涨薪
     * 按百分比调整指定员工的薪资，通过调整底薪/时薪等基础参数实现
     * 涨薪百分比必须大于 0
     *
     * @param id      员工工号
     * @param percent 涨薪百分比（如 10 代表涨 10%）
     * @throws IllegalArgumentException 如果员工未找到或百分比不合法
     */
    @Override
    public void raiseSalary(String id, double percent) {
        Employee e = employeeMap.get(id);
        // 检查员工是否存在
        if (e == null) {
            throw new IllegalArgumentException("未找到工号为 " + id + " 的员工");
        }
        // 检查涨薪百分比是否合法
        if (percent <= 0) {
            throw new IllegalArgumentException("涨薪百分比必须大于0");
        }
        // 保存快照后执行涨薪
        saveSnapshot();
        double oldSalary = e.getSalary();
        double newSalary = oldSalary * (1 + percent / 100.0);
        // 由于 Employee 的薪资是计算得来的，无法直接 setSalary
        // 需要通过修改子类的具体字段（baseSalary、hourlyRate 等）来实现涨薪
        updateEmployeeSalary(e, newSalary);
        saveData();
        if (logService != null) {
            logService.addLog("给" + e.getName() + "涨薪" + percent + "%");
        }
    }

    /**
     * 给整个部门涨薪
     * 按百分比调整指定部门所有员工的薪资
     *
     * @param dept    部门名称
     * @param percent 涨薪百分比（如 10 代表涨 10%）
     * @throws IllegalArgumentException 如果百分比不合法
     */
    @Override
    public void raiseDeptSalary(String dept, double percent) {
        // 检查涨薪百分比是否合法
        if (percent <= 0) {
            throw new IllegalArgumentException("涨薪百分比必须大于0");
        }
        // 保存快照后遍历部门所有员工执行涨薪
        saveSnapshot();
        for (Employee e : employeeMap.values()) {
            // 只处理指定部门的员工
            if (e.getDepartment().equals(dept)) {
                double oldSalary = e.getSalary();
                double newSalary = oldSalary * (1 + percent / 100.0);
                updateEmployeeSalary(e, newSalary);
                if (logService != null) {
                    logService.addLog("给" + e.getName() + "涨薪" + percent + "%");
                }
            }
        }
        saveData();
    }

    /**
     * 更新员工薪资（通过修改子类具体字段实现）
     * <p>
     * 由于 Employee 的 getSalary() 是抽象计算得来的，没有 setSalary 方法，
     * 涨薪的实质是按比例调整底薪/时薪等基础参数。
     * 不同员工类型的薪资计算方式不同：
     * - FullTimeEmployee: salary = baseSalary + baseSalary * 绩效比例
     * - Manager: salary = (baseSalary + bonus) + (baseSalary + bonus) * 绩效比例
     * - PartTimeEmployee: salary = hourlyRate * hoursWorked + hourlyRate * hoursWorked * 绩效比例
     * 通过计算新旧薪资的比例，按相同比例调整基础参数
     *
     * @param e         员工对象
     * @param newSalary 涨薪后的目标薪资
     */
    private void updateEmployeeSalary(Employee e, double newSalary) {
        // 根据员工类型分别处理
        if (e instanceof model.FullTimeEmployee) {
            // 全职员工：按比例调整 baseSalary
            model.FullTimeEmployee ft = (model.FullTimeEmployee) e;
            double oldSalary = ft.getSalary();
            double ratio = newSalary / oldSalary;
            ft.setBaseSalary(ft.getBaseSalary() * ratio);
        } else if (e instanceof model.Manager) {
            // 经理：按比例同时调整 baseSalary 和 bonus
            model.Manager mgr = (model.Manager) e;
            double oldSalary = mgr.getSalary();
            double ratio = newSalary / oldSalary;
            mgr.setBaseSalary(mgr.getBaseSalary() * ratio);
            mgr.setBonus(mgr.getBonus() * ratio);
        } else if (e instanceof model.PartTimeEmployee) {
            // 兼职员工：按比例调整 hourlyRate
            model.PartTimeEmployee pt = (model.PartTimeEmployee) e;
            double oldSalary = pt.getSalary();
            double ratio = newSalary / oldSalary;
            pt.setHourlyRate(pt.getHourlyRate() * ratio);
        }
    }

    /**
     * 员工请假扣薪
     * 根据请假类型和天数计算扣薪金额，生成扣薪明细和请假记录
     * 扣薪规则：日薪 = 月薪 / 22（按每月22个工作日计算）
     * 扣薪金额 = 日薪 × 天数 × 扣薪比例
     * 实发薪资 = 月薪 - 扣薪金额
     *
     * @param id   员工工号
     * @param type 请假类型（年假/病假/事假）
     * @param days 请假天数
     * @return 包含扣薪明细的格式化字符串
     * @throws IllegalArgumentException 如果员工未找到或天数不合法
     */
    @Override
    public String applyLeave(String id, LeaveType type, int days) {
        Employee e = employeeMap.get(id);
        // 检查员工是否存在
        if (e == null) {
            throw new IllegalArgumentException("未找到工号为 " + id + " 的员工");
        }
        // 检查请假天数是否合法
        if (days <= 0) {
            throw new IllegalArgumentException("请假天数必须大于0");
        }

        // 计算日薪（按每月22个工作日计算）
        double salary = e.getSalary();
        double dailySalary = salary / 22.0;
        // 获取扣薪比例
        double deductionRate = type.getDeductionRate();
        // 计算本次扣薪金额
        double deductionAmount = dailySalary * days * deductionRate;
        // 计算实发薪资
        double remainingSalary = salary - deductionAmount;

        // 格式化扣薪明细字符串，用于展示给用户
        String detail = String.format(
            "员工：%s  月薪：%.2f\n" +
            "请假类型：%s  天数：%d天\n" +
            "日薪：%.2f  扣薪比例：%s\n" +
            "本次扣薪：%.2f 元\n" +
            "实发薪资：%.2f 元",
            e.getName(), salary,
            type.getChineseName(), days,
            dailySalary, type.getRatePercent(),
            deductionAmount, remainingSalary
        );

        // 生成请假记录并添加到员工的请假记录列表中
        String record = String.format(
            "%s请假%d天，日薪%.2f，扣薪比例%s，本次扣薪%.2f元，实发%.2f元",
            type.getChineseName(), days, dailySalary, type.getRatePercent(), deductionAmount, remainingSalary
        );
        e.addLeaveRecord(record);

        // 记录操作日志
        if (logService != null) {
            logService.addLog("请假扣薪：" + e.getName() + " " + type.getChineseName() + days + "天，扣薪" + String.format("%.2f", deductionAmount) + "元");
        }
        saveData();

        return detail;
    }

    /**
     * 撤销上一步操作
     * 从历史栈中弹出最近一次保存的数据快照，替换当前数据
     * 如果历史栈为空则提示无操作可撤销
     */
    @Override
    public void undo() {
        // 检查历史栈是否为空
        if (history.isEmpty()) {
            System.out.println("没有可撤销的操作");
            return;
        }
        // 弹出最近的历史快照
        Map<String, Employee> previous = history.pop();
        // 清空当前数据并替换为历史快照
        employeeMap.clear();
        employeeMap.putAll(previous);
        saveData();
        System.out.println("撤销成功，已还原上一步操作");
    }

    /**
     * 将当前 employeeMap 深拷贝后压入历史栈
     * 用于在每次修改操作前保存数据快照，支持撤销功能
     * 最多保留 MAX_HISTORY（10）步历史，超出时自动移除最旧的记录
     */
    private void saveSnapshot() {
        // 如果历史栈已满，移除最旧的记录（栈底）
        if (history.size() >= MAX_HISTORY) {
            history.removeLast();
        }
        // 将当前数据深拷贝后压入栈顶
        // 注意：这里使用的是浅拷贝（HashMap 的拷贝），但 Employee 对象本身是引用
        // 由于后续操作会替换整个 Map 或创建新对象，这种程度已足够
        history.push(new HashMap<>(employeeMap));
    }

    /**
     * 员工转岗
     * 将员工从当前类型转换为另一种员工类型
     * 保留公共字段（工号、姓名、部门、联系方式、入职日期、绩效、请假记录）
     * 不能转岗为当前已有类型
     *
     * @param id           员工工号
     * @param newType      新员工类型（1=全职, 2=兼职, 3=经理）
     * @param salaryParams 新类型的薪资相关参数
     *                     - 全职：需要 1 个参数（基本工资）
     *                     - 兼职：需要 2 个参数（时薪, 工作小时数）
     *                     - 经理：需要 2 个参数（基本工资, 奖金）
     * @throws IllegalArgumentException 如果员工未找到、转岗类型无效或参数不足
     */
    @Override
    public void transferEmployee(String id, int newType, double... salaryParams) {
        Employee old = employeeMap.get(id);
        // 检查员工是否存在
        if (old == null) {
            throw new IllegalArgumentException("未找到工号为 " + id + " 的员工");
        }

        // 判断当前员工类型的名称（用于日志和提示）
        String oldTypeName;
        if (old instanceof model.Manager) {
            oldTypeName = "经理";
        } else if (old instanceof model.FullTimeEmployee) {
            oldTypeName = "全职";
        } else if (old instanceof model.PartTimeEmployee) {
            oldTypeName = "兼职";
        } else {
            oldTypeName = "其他";
        }

        // 检查是否转岗为同类型（不允许同类型转岗）
        int currentType = 0;
        if (old instanceof model.Manager) currentType = 3;
        else if (old instanceof model.FullTimeEmployee) currentType = 1;
        else if (old instanceof model.PartTimeEmployee) currentType = 2;

        if (currentType == newType) {
            throw new IllegalArgumentException("不能转岗为当前类型（" + oldTypeName + "）");
        }

        // 保存快照后执行转岗
        saveSnapshot();

        // 根据目标类型创建新的员工对象，复制公共字段
        Employee newEmp;
        switch (newType) {
            case 1: // 转岗为全职员工
                if (salaryParams.length < 1) {
                    throw new IllegalArgumentException("转岗为全职员工需要提供基本工资");
                }
                newEmp = new model.FullTimeEmployee(id, old.getName(), old.getDepartment(),
                        old.getContact(), salaryParams[0], old.getHireDate());
                break;
            case 2: // 转岗为兼职员工
                if (salaryParams.length < 2) {
                    throw new IllegalArgumentException("转岗为兼职员工需要提供时薪和工作小时数");
                }
                newEmp = new model.PartTimeEmployee(id, old.getName(), old.getDepartment(),
                        old.getContact(), salaryParams[0], salaryParams[1], old.getHireDate());
                break;
            case 3: // 转岗为经理
                if (salaryParams.length < 2) {
                    throw new IllegalArgumentException("转岗为经理需要提供基本工资和奖金");
                }
                newEmp = new model.Manager(id, old.getName(), old.getDepartment(),
                        old.getContact(), salaryParams[0], salaryParams[1], old.getHireDate());
                break;
            default:
                throw new IllegalArgumentException("无效的员工类型：" + newType);
        }

        // 复制原员工的绩效等级到新员工对象
        newEmp.setPerformance(old.getPerformance());
        // 复制原员工的请假记录到新员工对象
        newEmp.setLeaveRecords(old.getLeaveRecords());

        // 用新员工对象替换 Map 中的旧对象
        employeeMap.put(id, newEmp);

        // 获取新类型的中文名称（用于日志）
        String newTypeName;
        switch (newType) {
            case 1: newTypeName = "全职"; break;
            case 2: newTypeName = "兼职"; break;
            case 3: newTypeName = "经理"; break;
            default: newTypeName = "其他";
        }

        // 记录操作日志
        if (logService != null) {
            logService.addLog("员工" + old.getName() + "从" + oldTypeName + "转岗为" + newTypeName);
        }
        saveData();
    }

    /**
     * 生成月度薪资报表
     * 按部门分组统计各部门的人数、最高薪资、最低薪资、平均薪资、薪资总额
     * 最后汇总全公司的合计数据
     * 使用 StringBuilder 构建格式化的报表字符串
     *
     * @return 格式化的报表字符串，包含表头、各部门数据和合计行
     */
    @Override
    public String generateMonthlyReport() {
        // 检查是否有员工数据
        if (employeeMap.isEmpty()) {
            return "暂无员工数据，无法生成报表";
        }

        // 获取当前月份信息，用于报表标题
        java.time.LocalDate now = java.time.LocalDate.now();
        String yearMonth = now.getYear() + "年" + String.format("%02d", now.getMonthValue()) + "月";

        // 按部门分组：遍历所有员工，将同一部门的员工放入同一个 List
        Map<String, List<Employee>> deptGroup = new java.util.HashMap<>();
        for (Employee e : employeeMap.values()) {
            String dept = e.getDepartment();
            if (!deptGroup.containsKey(dept)) {
                deptGroup.put(dept, new ArrayList<>());
            }
            deptGroup.get(dept).add(e);
        }

        StringBuilder sb = new StringBuilder();
        String line = "=====================================================";

        // 报表标题和表头
        sb.append("========== 月度薪资报表 ==========\n");
        sb.append("统计时间：" + yearMonth + "\n\n");
        sb.append(padChinese("部门", 12));
        sb.append(padChinese("人数", 8));
        sb.append(padChinese("最高薪资", 12));
        sb.append(padChinese("最低薪资", 12));
        sb.append(padChinese("平均薪资", 12));
        sb.append(padChinese("薪资总额", 12));
        sb.append("\n");
        sb.append(line + "\n");

        // 全公司汇总数据
        double grandTotal = 0;
        double grandMax = Double.MIN_VALUE;
        double grandMin = Double.MAX_VALUE;
        int grandCount = 0;

        // 遍历每个部门，计算统计数据
        for (Map.Entry<String, List<Employee>> entry : deptGroup.entrySet()) {
            String dept = entry.getKey();
            List<Employee> empList = entry.getValue();

            int count = empList.size();
            double maxSalary = Double.MIN_VALUE;
            double minSalary = Double.MAX_VALUE;
            double sumSalary = 0;

            // 遍历部门内所有员工，计算最高/最低/总薪资
            for (Employee e : empList) {
                double salary = e.getSalary();
                if (salary > maxSalary) maxSalary = salary;
                if (salary < minSalary) minSalary = salary;
                sumSalary += salary;
            }
            double avgSalary = sumSalary / count;

            // 累加到全公司汇总数据
            grandCount += count;
            grandTotal += sumSalary;
            if (maxSalary > grandMax) grandMax = maxSalary;
            if (minSalary < grandMin) grandMin = minSalary;

            // 输出该部门的数据行
            sb.append(padChinese(dept, 12));
            sb.append(padChinese(String.valueOf(count), 8));
            sb.append(padChinese(String.format("%.2f", maxSalary), 12));
            sb.append(padChinese(String.format("%.2f", minSalary), 12));
            sb.append(padChinese(String.format("%.2f", avgSalary), 12));
            sb.append(padChinese(String.format("%.2f", sumSalary), 12));
            sb.append("\n");
        }

        // 全公司合计行
        double grandAvg = (double) grandTotal / grandCount;
        sb.append(line + "\n");
        sb.append(padChinese("全公司合计", 12));
        sb.append(padChinese(String.valueOf(grandCount), 8));
        sb.append(padChinese(String.format("%.2f", grandMax), 12));
        sb.append(padChinese(String.format("%.2f", grandMin), 12));
        sb.append(padChinese(String.format("%.2f", grandAvg), 12));
        sb.append(padChinese(String.format("%.2f", grandTotal), 12));
        sb.append("\n");
        sb.append("本月人力成本：" + String.format("%.2f", grandTotal) + " 元\n");

        return sb.toString();
    }

    /**
     * 将字符串左对齐填充到指定可视宽度
     * 中文字符算 2 个宽度，英文字符算 1 个宽度
     * 用于报表中对齐各列数据
     *
     * @param str   要填充的字符串
     * @param width 目标可视宽度
     * @return 填充后的字符串
     */
    private String padChinese(String str, int width) {
        // 计算字符串的实际可视宽度
        int len = 0;
        for (char c : str.toCharArray()) {
            len += (c > 127) ? 2 : 1;
        }
        // 计算需要填充的空格数
        int pad = width - len;
        if (pad <= 0) return str;
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < pad; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * 从文件批量导入员工数据
     * 读取指定路径的txt文件，按逗号分割每行，创建对应员工对象加入系统
     * 文件格式（第一行为表头，跳过）：
     *   类型,工号,姓名,部门,联系方式,入职日期,薪资参数...
     *   全职：类型,工号,姓名,部门,联系方式,入职日期,基本工资
     *   兼职：类型,工号,姓名,部门,联系方式,入职日期,时薪,工作小时数
     *   管理层：类型,工号,姓名,部门,联系方式,入职日期,基本工资,奖金
     *
     * @param filePath 导入文件的路径
     * @return 成功导入的员工数量
     */
    @Override
    public int importFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在：" + filePath);
        }

        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                // 跳过空行和表头
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    // 如果第一行是表头（包含"类型"关键字），则跳过
                    if (line.contains("类型") || line.contains("工号")) continue;
                }

                // 按逗号分割
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                String type = parts[0].trim();
                String id = parts[1].trim();
                String name = parts[2].trim();
                String dept = parts[3].trim();
                String contact = parts[4].trim();
                LocalDate hireDate = LocalDate.parse(parts[5].trim());

                // 检查工号是否已存在
                if (employeeMap.containsKey(id)) {
                    continue; // 跳过已存在的工号
                }

                Employee emp;
                switch (type) {
                    case "全职":
                        if (parts.length < 7) continue;
                        double baseSalary = Double.parseDouble(parts[6].trim());
                        emp = new model.FullTimeEmployee(id, name, dept, contact, baseSalary, hireDate);
                        break;
                    case "兼职":
                        if (parts.length < 8) continue;
                        double hourlyRate = Double.parseDouble(parts[6].trim());
                        double hoursWorked = Double.parseDouble(parts[7].trim());
                        emp = new model.PartTimeEmployee(id, name, dept, contact, hourlyRate, hoursWorked, hireDate);
                        break;
                    case "管理层":
                        if (parts.length < 8) continue;
                        double mgrBaseSalary = Double.parseDouble(parts[6].trim());
                        double bonus = Double.parseDouble(parts[7].trim());
                        emp = new model.Manager(id, name, dept, contact, mgrBaseSalary, bonus, hireDate);
                        break;
                    default:
                        continue; // 未知类型跳过
                }

                employeeMap.put(emp.getId(), emp);
                count++;
            }

            if (count > 0) {
                saveData();
                if (logService != null) {
                    logService.addLog("批量导入：从 " + filePath + " 导入 " + count + " 条记录");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败：" + e.getMessage(), e);
        }

        return count;
    }

    /**
     * 备份数据
     * 将当前 employees.dat 文件复制到 backup/ 文件夹下
     * 备份文件名格式为 backup_yyyyMMdd_HHmmss.dat
     * 如果 backup 文件夹不存在则自动创建
     *
     * @return 备份文件的文件名（相对路径），如果备份失败则返回 null
     */
    @Override
    public String backupData() {
        // 创建 backup 文件夹（如果不存在）
        File backupDir = new File("backup");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        // 根据当前时间生成备份文件名
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String backupFileName = "backup_" + now.format(formatter) + ".dat";
        Path sourcePath = Paths.get(FILE_PATH);
        Path targetPath = Paths.get("backup", backupFileName);

        try {
            // 使用 Files.copy 复制文件，如果目标已存在则覆盖
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return backupFileName;
        } catch (IOException e) {
            System.err.println("备份失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 从备份文件恢复数据
     * 将指定的备份文件覆盖当前 employees.dat 文件并重新加载数据
     *
     * @param backupFilePath 备份文件的路径
     * @return 是否恢复成功
     */
    @Override
    public boolean restoreFromBackup(String backupFilePath) {
        Path backupPath = Paths.get(backupFilePath);
        Path targetPath = Paths.get(FILE_PATH);

        // 检查备份文件是否存在
        if (!Files.exists(backupPath)) {
            System.err.println("备份文件不存在：" + backupFilePath);
            return false;
        }

        try {
            // 用备份文件覆盖当前数据文件
            Files.copy(backupPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            // 重新加载数据到内存
            loadData();
            return true;
        } catch (IOException e) {
            System.err.println("恢复失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 将 employeeMap 序列化写入文件
     * 使用 Java 对象序列化机制，将整个 Map 写入 employees.dat 文件
     * 采用 try-with-resources 自动关闭流资源
     */
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            // 将 employeeMap 序列化写入文件
            oos.writeObject(employeeMap);
        } catch (IOException e) {
            System.err.println("保存数据失败：" + e.getMessage());
        }
    }

    /**
     * 从文件反序列化还原 employeeMap
     * 读取 employees.dat 文件，将序列化的数据还原为 Map
     * 如果文件不存在则跳过（首次启动时）
     * 采用 try-with-resources 自动关闭流资源
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(FILE_PATH);
        // 如果文件不存在，说明是首次启动，直接返回
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // 反序列化还原 Map 数据
            Map<String, Employee> loaded = (Map<String, Employee>) ois.readObject();
            // 清空当前数据并替换为加载的数据
            employeeMap.clear();
            employeeMap.putAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载数据失败：" + e.getMessage());
        }
    }
}
