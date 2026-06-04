package model;

/**
 * 绩效等级枚举
 * <p>
 * 所在层次：模型层（Model）
 * 职责：定义员工的绩效等级，每个等级对应不同的奖金比例
 *       用于 Employee 类中存储绩效信息，并在薪资计算时通过 getRate() 获取奖金比例
 * 关联类：Employee（被引用）、FullTimeEmployee（薪资计算中使用）、
 *        PartTimeEmployee（薪资计算中使用）、Manager（薪资计算中使用）
 */
public enum Performance {
    /** A级绩效，奖金比例 20%，代表优秀 */
    A(0.20),
    /** B级绩效，奖金比例 10%，代表良好 */
    B(0.10),
    /** C级绩效，奖金比例 0%，代表合格（无奖金） */
    C(0.0);

    /** 奖金比例，以小数形式存储（如 0.20 表示 20%） */
    private final double rate;

    /**
     * 枚举构造方法
     * 为每个绩效等级设置对应的奖金比例
     *
     * @param rate 奖金比例（小数形式，如 0.20 表示 20%）
     */
    Performance(double rate) {
        this.rate = rate;
    }

    /**
     * 获取对应的奖金比例
     * 在薪资计算时调用此方法，将奖金比例乘以基础薪资得到绩效奖金
     *
     * @return 奖金比例（小数形式，如 0.20 表示 20%）
     */
    public double getRate() {
        return rate;
    }
}
