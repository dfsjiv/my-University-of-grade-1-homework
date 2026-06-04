package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 兼职员工类
 * <p>
 * 所在层次：模型层（Model），继承自 Employee 抽象类
 * 职责：表示兼职员工，在 Employee 基础上新增 hourlyRate（时薪）和 hoursWorked（工作小时数）字段，
 *       实现 getSalary() 方法返回"时薪 × 工时 + 绩效奖金"的计算结果
 * 关联类：Employee（父类）、Performance（用于计算绩效奖金）
 */
public class PartTimeEmployee extends Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 时薪，兼职员工每小时的薪资标准 */
    private double hourlyRate;
    /** 工作小时数，兼职员工本月实际工作总时长 */
    private double hoursWorked;

    /**
     * 全参构造方法
     * 调用父类构造方法初始化基本信息，并设置时薪和工作小时数
     *
     * @param id          员工工号
     * @param name        员工姓名
     * @param department  所属部门
     * @param contact     联系方式
     * @param hourlyRate  时薪（每小时薪资）
     * @param hoursWorked 工作小时数（本月总工时）
     * @param hireDate    入职日期
     */
    public PartTimeEmployee(String id, String name, String department, String contact, double hourlyRate, double hoursWorked, LocalDate hireDate) {
        super(id, name, department, contact, hireDate);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    /**
     * 实现抽象方法 getSalary()
     * 薪资计算规则：时薪 × 工作小时数 + (时薪 × 工作小时数) × 绩效奖金比例
     * 先计算基础薪资（时薪 × 工时），再在此基础上加上绩效奖金
     *
     * @return 兼职员工的最终应发薪资（基础薪资 + 绩效奖金）
     */
    @Override
    public double getSalary() {
        double base = hourlyRate * hoursWorked;
        return base + base * getPerformance().getRate();
    }

    // ==================== Getter 和 Setter 方法 ====================

    /**
     * 获取时薪
     * @return 时薪数值
     */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * 设置时薪
     * @param hourlyRate 新的时薪数值
     */
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    /**
     * 获取工作小时数
     * @return 本月工作总小时数
     */
    public double getHoursWorked() {
        return hoursWorked;
    }

    /**
     * 设置工作小时数
     * @param hoursWorked 新的工作小时数
     */
    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    /**
     * 重写 toString() 方法
     * 格式化输出兼职员工的完整信息，包括从父类继承的字段和本类特有的时薪、工作小时数
     *
     * @return 包含工号、姓名、部门、联系方式、时薪、工作小时数的格式化字符串
     */
    @Override
    public String toString() {
        return "PartTimeEmployee{" +
                "工号='" + getId() + '\'' +
                ", 姓名='" + getName() + '\'' +
                ", 部门='" + getDepartment() + '\'' +
                ", 联系方式='" + getContact() + '\'' +
                ", 时薪=" + hourlyRate +
                ", 工作小时数=" + hoursWorked +
                '}';
    }
}
