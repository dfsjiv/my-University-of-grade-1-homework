package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 员工抽象类
 * <p>
 * 所在层次：模型层（Model），作为所有员工类型的基类
 * 职责：定义员工的基本属性（工号、姓名、部门、联系方式、入职日期、绩效、请假记录）
 *       以及抽象方法 getSalary()，由子类具体实现薪资计算逻辑
 * 关联类：FullTimeEmployee（直接继承）、PartTimeEmployee（直接继承）、
 *        Manager（通过 FullTimeEmployee 间接继承）、Performance（绩效枚举）、LeaveType（请假类型枚举）
 */
public abstract class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 员工工号，唯一标识每位员工 */
    private String id;
    /** 员工姓名 */
    private String name;
    /** 所属部门名称 */
    private String department;
    /** 联系方式（电话/邮箱等） */
    private String contact;
    /** 入职日期，用于计算工龄 */
    private LocalDate hireDate;
    /** 绩效等级，默认为 C 级（无奖金） */
    private Performance performance = Performance.C;
    /** 请假记录列表，每条记录包含请假类型、天数、扣薪明细等信息 */
    private List<String> leaveRecords = new ArrayList<>();

    /**
     * 全参构造方法
     * 初始化员工的基本信息字段
     *
     * @param id         员工工号，全局唯一
     * @param name       员工姓名
     * @param department 所属部门
     * @param contact    联系方式
     * @param hireDate   入职日期
     */
    public Employee(String id, String name, String department, String contact, LocalDate hireDate) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.contact = contact;
        this.hireDate = hireDate;
    }

    /**
     * 抽象方法：获取员工薪资
     * 由子类根据各自的薪资计算规则实现
     *
     * @return 该员工的最终应发薪资
     */
    public abstract double getSalary();

    // ==================== Getter 和 Setter 方法 ====================

    /**
     * 获取员工工号
     * @return 工号字符串
     */
    public String getId() {
        return id;
    }

    /**
     * 设置员工工号
     * @param id 新的工号
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取员工姓名
     * @return 姓名字符串
     */
    public String getName() {
        return name;
    }

    /**
     * 设置员工姓名
     * @param name 新的姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取所属部门
     * @return 部门名称
     */
    public String getDepartment() {
        return department;
    }

    /**
     * 设置所属部门
     * @param department 新的部门名称
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * 获取联系方式
     * @return 联系方式字符串
     */
    public String getContact() {
        return contact;
    }

    /**
     * 设置联系方式
     * @param contact 新的联系方式
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * 获取绩效等级
     * @return Performance 枚举值（A/B/C）
     */
    public Performance getPerformance() {
        return performance;
    }

    /**
     * 设置绩效等级
     * @param performance 新的绩效等级枚举值
     */
    public void setPerformance(Performance performance) {
        this.performance = performance;
    }

    /**
     * 获取入职日期
     * @return 入职日期 LocalDate 对象
     */
    public LocalDate getHireDate() {
        return hireDate;
    }

    /**
     * 设置入职日期
     * @param hireDate 新的入职日期
     */
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    /**
     * 计算工龄（入职年限）
     * 使用 ChronoUnit.YEARS.between 计算从入职日期到当前日期的整年数
     * 如果入职日期为空则返回 0，避免空指针异常
     *
     * @return 从入职日期到今天的整年数
     */
    public long getYearsOfService() {
        if (hireDate == null) {
            return 0;
        }
        return ChronoUnit.YEARS.between(hireDate, LocalDate.now());
    }

    /**
     * 获取请假记录列表
     * 返回的是内部列表的引用，外部可以直接操作该列表
     *
     * @return 请假记录列表，每条记录为字符串格式的明细
     */
    public List<String> getLeaveRecords() {
        return leaveRecords;
    }

    /**
     * 添加一条请假记录
     * 在现有请假记录列表末尾追加新记录
     *
     * @param record 请假记录字符串，包含请假类型、天数、扣薪明细等信息
     */
    public void addLeaveRecord(String record) {
        this.leaveRecords.add(record);
    }

    /**
     * 设置请假记录列表
     * 用于替换整个请假记录列表（例如从备份恢复或转岗时复制记录）
     *
     * @param leaveRecords 新的请假记录列表
     */
    public void setLeaveRecords(List<String> leaveRecords) {
        this.leaveRecords = leaveRecords;
    }

    /**
     * 重写 toString() 方法
     * 格式化输出员工的基本信息，便于调试和展示
     *
     * @return 包含工号、姓名、部门、联系方式、绩效的格式化字符串
     */
    @Override
    public String toString() {
        return "Employee{" +
                "工号='" + id + '\'' +
                ", 姓名='" + name + '\'' +
                ", 部门='" + department + '\'' +
                ", 联系方式='" + contact + '\'' +
                ", 绩效=" + performance +
                '}';
    }
}
