package model;

/**
 * 请假类型枚举
 * <p>
 * 所在层次：模型层（Model）
 * 职责：定义员工可申请的请假类型，每种假期对应不同的扣薪比例
 *       用于 EmployeeServiceImpl 的 applyLeave() 方法中计算请假扣薪金额
 * 关联类：EmployeeServiceImpl（在请假扣薪逻辑中使用）
 */
public enum LeaveType {
    /** 年假，扣薪比例 0%，即带薪休假不扣工资 */
    ANNUAL(0.0),
    /** 病假，扣薪比例 50%，即只发一半日薪 */
    SICK(0.5),
    /** 事假，扣薪比例 100%，即无薪休假 */
    PERSONAL(1.0);

    /** 扣薪比例，以小数形式存储（如 0.5 表示扣 50% 日薪） */
    private final double deductionRate;

    /**
     * 枚举构造方法
     * 为每种请假类型设置对应的扣薪比例
     *
     * @param deductionRate 扣薪比例（小数形式，如 0.5 表示扣 50%）
     */
    LeaveType(double deductionRate) {
        this.deductionRate = deductionRate;
    }

    /**
     * 获取扣薪比例
     * 在计算请假扣薪时调用此方法，乘以日薪和天数得到扣薪金额
     *
     * @return 扣薪比例（小数形式，如 0.5 表示扣 50%）
     */
    public double getDeductionRate() {
        return deductionRate;
    }

    /**
     * 获取请假类型的中文名称
     * 用于在控制台界面和请假记录中显示可读性更好的类型名称
     *
     * @return 中文名称字符串（"年假"/"病假"/"事假"）
     */
    public String getChineseName() {
        switch (this) {
            case ANNUAL:   return "年假";
            case SICK:     return "病假";
            case PERSONAL: return "事假";
            default:       return "未知";
        }
    }

    /**
     * 获取扣薪比例的百分比显示形式
     * 将小数形式的扣薪比例转换为百分比字符串，便于界面展示
     *
     * @return 百分比字符串（如 "0%"/"50%"/"100%"）
     */
    public String getRatePercent() {
        return String.format("%.0f%%", deductionRate * 100);
    }
}
