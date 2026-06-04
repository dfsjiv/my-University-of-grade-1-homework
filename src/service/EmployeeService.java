package service;

import model.Employee;
import model.LeaveType;
import model.Performance;
import java.util.List;
import java.util.Map;

/**
 * 员工服务接口
 * <p>
 * 所在层次：服务层（Service），定义员工管理系统的业务逻辑接口
 * 职责：定义员工管理相关的所有操作方法，包括增删改查、薪资计算、绩效管理、
 *       请假扣薪、转岗、数据备份恢复、报表生成等功能
 * 关联类：Employee（操作的数据模型）、EmployeeServiceImpl（实现类）、
 *        ConsoleUI（调用方）、LeaveType（请假类型）、Performance（绩效等级）
 */
public interface EmployeeService {

    /**
     * 添加员工
     * 将新员工对象存入系统，如果工号已存在则抛出异常
     *
     * @param e 要添加的员工对象
     * @throws IllegalArgumentException 如果工号已存在
     */
    void addEmployee(Employee e);

    /**
     * 根据工号删除员工
     * 从系统中移除指定工号的员工记录
     *
     * @param id 要删除的员工工号
     */
    void removeEmployee(String id);

    /**
     * 更新员工信息
     * 用新的员工数据替换指定工号的旧数据
     *
     * @param id      要更新的员工工号
     * @param newData 新的员工数据对象
     */
    void updateEmployee(String id, Employee newData);

    /**
     * 根据工号查找员工
     * 精确匹配工号，返回对应的员工对象
     *
     * @param id 要查找的员工工号
     * @return 匹配的员工对象，如果未找到则返回 null
     */
    Employee findById(String id);

    /**
     * 根据姓名查找员工
     * 支持模糊匹配（姓名中包含关键字即匹配），支持重名情况
     *
     * @param name 要查找的员工姓名（支持部分匹配）
     * @return 匹配的员工列表，可能包含多个同名员工
     */
    List<Employee> findByName(String name);

    /**
     * 根据部门查找员工
     * 精确匹配部门名称，返回该部门的所有员工
     *
     * @param dept 部门名称
     * @return 该部门的员工列表
     */
    List<Employee> findByDept(String dept);

    /**
     * 模糊搜索员工
     * 在工号、姓名、部门三个字段中搜索包含关键字的员工
     * 搜索时不区分大小写
     *
     * @param keyword 搜索关键字
     * @return 任意字段包含关键字的员工列表
     */
    List<Employee> fuzzySearch(String keyword);

    /**
     * 获取所有员工
     * 返回系统中所有员工的列表
     *
     * @return 所有员工列表（副本，不影响内部数据）
     */
    List<Employee> getAllEmployees();

    /**
     * 计算指定员工的薪资
     * 根据员工类型调用对应的薪资计算逻辑
     *
     * @param id 员工工号
     * @return 该员工的最终应发薪资
     */
    double calculateSalary(String id);

    /**
     * 根据薪资范围筛选员工
     * 返回薪资在指定闭区间 [min, max] 内的所有员工
     *
     * @param min 最低薪资（含）
     * @param max 最高薪资（含）
     * @return 薪资在 [min, max] 范围内的员工列表
     */
    List<Employee> findBySalaryRange(double min, double max);

    /**
     * 设置员工绩效等级
     * 更新指定员工的绩效等级，影响其薪资计算结果
     *
     * @param id 员工工号
     * @param p  新的绩效等级
     */
    void setPerformance(String id, Performance p);

    /**
     * 获取各部门人数统计
     * 按部门分组统计每个部门的员工数量
     *
     * @return Map，key 为部门名称，value 为该部门员工人数
     */
    Map<String, Long> getDeptStats();

    /**
     * 获取薪资最高的员工
     * 遍历所有员工，比较薪资找出最高者
     *
     * @return 薪资最高的 Employee 对象，如果系统为空则返回 null
     */
    Employee getHighestSalary();

    /**
     * 获取薪资最低的员工
     * 遍历所有员工，比较薪资找出最低者
     *
     * @return 薪资最低的 Employee 对象，如果系统为空则返回 null
     */
    Employee getLowestSalary();

    /**
     * 给单个员工涨薪
     * 按百分比调整指定员工的薪资（调整底薪/时薪等基础参数）
     *
     * @param id      员工工号
     * @param percent 涨薪百分比（如 10 代表涨 10%）
     */
    void raiseSalary(String id, double percent);

    /**
     * 给整个部门涨薪
     * 按百分比调整指定部门所有员工的薪资
     *
     * @param dept    部门名称
     * @param percent 涨薪百分比（如 10 代表涨 10%）
     */
    void raiseDeptSalary(String dept, double percent);

    /**
     * 撤销上一步操作
     * 从历史栈中恢复上一次操作前的数据快照
     */
    void undo();

    /**
     * 员工请假扣薪
     * 根据请假类型和天数计算扣薪金额，并生成扣薪明细
     *
     * @param id   员工工号
     * @param type 请假类型（年假/病假/事假）
     * @param days 请假天数
     * @return 包含扣薪明细的格式化字符串
     * @throws IllegalArgumentException 如果员工未找到或天数不合法
     */
    String applyLeave(String id, LeaveType type, int days);

    /**
     * 员工转岗
     * 将员工从当前类型转换为另一种员工类型，保留公共字段信息
     *
     * @param id           员工工号
     * @param newType      新员工类型（1=全职, 2=兼职, 3=经理）
     * @param salaryParams 新类型的薪资相关参数，变长参数
     *                     - 全职：需要 1 个参数（基本工资）
     *                     - 兼职：需要 2 个参数（时薪, 工作小时数）
     *                     - 经理：需要 2 个参数（基本工资, 奖金）
     * @throws IllegalArgumentException 如果员工未找到或转岗类型无效
     */
    void transferEmployee(String id, int newType, double... salaryParams);

    /**
     * 备份数据
     * 将当前 employees.dat 文件复制到 backup/ 文件夹下
     * 备份文件名格式为 backup_yyyyMMdd_HHmmss.dat
     *
     * @return 备份文件的文件名（相对路径），如果备份失败则返回 null
     */
    String backupData();

    /**
     * 从备份文件恢复数据
     * 将指定的备份文件覆盖当前数据文件并重新加载
     *
     * @param backupFilePath 备份文件的路径
     * @return 是否恢复成功
     */
    boolean restoreFromBackup(String backupFilePath);

    /**
     * 从文件批量导入员工数据
     * 读取指定路径的txt文件，按逗号分割每行，创建对应员工对象加入系统
     *
     * @param filePath 导入文件的路径
     * @return 成功导入的员工数量
     */
    int importFromFile(String filePath);

    /**
     * 生成月度薪资报表
     * 按部门分组统计各部门的人数、最高薪资、最低薪资、平均薪资、薪资总额
     * 最后汇总全公司的合计数据
     *
     * @return 格式化的报表字符串，包含表头、各部門数据和合计行
     */
    String generateMonthlyReport();
}
