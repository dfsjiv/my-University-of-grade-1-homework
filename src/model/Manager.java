package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 经理类
 * <p>
 * 所在层次：模型层（Model），继承自 FullTimeEmployee
 * 职责：表示管理层员工，在 FullTimeEmployee 基础上新增 bonus（奖金）字段，
 *       重写 getSalary() 方法返回"基本工资 + 奖金 + 绩效奖金"的计算结果
 * 关联类：FullTimeEmployee（父类）、Employee（间接父类）、Performance（用于计算绩效奖金）
 */
public class Manager extends FullTimeEmployee implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 奖金，经理在基本工资之外额外获得的固定奖金金额 */
    private double bonus;

    /**
     * 全参构造方法
     * 调用父类构造方法初始化基本信息（含基本工资），并设置奖金
     *
     * @param id          员工工号
     * @param name        员工姓名
     * @param department  所属部门
     * @param contact     联系方式
     * @param baseSalary  基本工资（月薪基数）
     * @param bonus       奖金金额
     * @param hireDate    入职日期
     */
    public Manager(String id, String name, String department, String contact, double baseSalary, double bonus, LocalDate hireDate) {
        super(id, name, department, contact, baseSalary, hireDate);
        this.bonus = bonus;
    }

    /**
     * 重写 getSalary() 方法
     * 薪资计算规则：(基本工资 + 奖金) + (基本工资 + 奖金) × 绩效奖金比例
     * 先计算基础部分（基本工资 + 奖金），再在此基础上加上绩效奖金
     * 与 FullTimeEmployee 的区别在于多了奖金部分作为计算基数
     *
     * @return 经理的最终应发薪资（基本工资 + 奖金 + 绩效奖金）
     */
    @Override
    public double getSalary() {
        double base = getBaseSalary() + bonus;
        return base + base * getPerformance().getRate();
    }

    // ==================== Getter 和 Setter 方法 ====================

    /**
     * 获取奖金金额
     * @return 奖金数值
     */
    public double getBonus() {
        return bonus;
    }

    /**
     * 设置奖金金额
     * @param bonus 新的奖金数值
     */
    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    /**
     * 重写 toString() 方法
     * 格式化输出经理的完整信息，包括从父类继承的字段和本类特有的奖金
     *
     * @return 包含工号、姓名、部门、联系方式、基本工资、奖金的格式化字符串
     */
    @Override
    public String toString() {
        return "Manager{" +
                "工号='" + getId() + '\'' +
                ", 姓名='" + getName() + '\'' +
                ", 部门='" + getDepartment() + '\'' +
                ", 联系方式='" + getContact() + '\'' +
                ", 基本工资=" + getBaseSalary() +
                ", 奖金=" + bonus +
                '}';
    }
}
