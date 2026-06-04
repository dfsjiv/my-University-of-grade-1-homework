package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 全职员工类
 * <p>
 * 所在层次：模型层（Model），继承自 Employee 抽象类
 * 职责：表示全职员工，在 Employee 基础上新增 baseSalary（基本工资）字段，
 *       实现 getSalary() 方法返回"基本工资 + 绩效奖金"的计算结果
 * 关联类：Employee（父类）、Manager（子类继承此类）、Performance（用于计算绩效奖金）
 */
public class FullTimeEmployee extends Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 基本工资，全职员工的固定月薪基数 */
    private double baseSalary;

    /**
     * 全参构造方法
     * 调用父类构造方法初始化基本信息，并设置基本工资
     *
     * @param id         员工工号
     * @param name       员工姓名
     * @param department 所属部门
     * @param contact    联系方式
     * @param baseSalary 基本工资（月薪基数）
     * @param hireDate   入职日期
     */
    public FullTimeEmployee(String id, String name, String department, String contact, double baseSalary, LocalDate hireDate) {
        super(id, name, department, contact, hireDate);
        this.baseSalary = baseSalary;
    }

    /**
     * 实现抽象方法 getSalary()
     * 薪资计算规则：基本工资 + 基本工资 × 绩效奖金比例
     * 绩效奖金比例由 Performance 枚举的 getRate() 方法提供
     *
     * @return 全职员工的最终应发薪资（基本工资 + 绩效奖金）
     */
    @Override
    public double getSalary() {
        double base = baseSalary;
        return base + base * getPerformance().getRate();
    }

    // ==================== Getter 和 Setter 方法 ====================

    /**
     * 获取基本工资
     * @return 基本工资数值
     */
    public double getBaseSalary() {
        return baseSalary;
    }

    /**
     * 设置基本工资
     * @param baseSalary 新的基本工资数值
     */
    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    /**
     * 重写 toString() 方法
     * 格式化输出全职员工的完整信息，包括从父类继承的字段和本类特有的基本工资
     *
     * @return 包含工号、姓名、部门、联系方式、基本工资的格式化字符串
     */
    @Override
    public String toString() {
        return "FullTimeEmployee{" +
                "工号='" + getId() + '\'' +
                ", 姓名='" + getName() + '\'' +
                ", 部门='" + getDepartment() + '\'' +
                ", 联系方式='" + getContact() + '\'' +
                ", 基本工资=" + baseSalary +
                '}';
    }
}
