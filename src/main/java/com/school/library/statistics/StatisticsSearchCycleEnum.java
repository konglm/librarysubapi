package com.school.library.statistics;

/**
 * 统计检索词周期
 */
public enum StatisticsSearchCycleEnum {
    LAST_MONTH("last_month", "近一个月"),
    ONE_YEAR("one_year", "近一年"),
    TWO_YEAR("two_year", "近两年"),
    ALL("all", "全部");

    StatisticsSearchCycleEnum(String cycle, String label){
        this.cycle = cycle;
        this.label = label;
    }

    private String cycle;

    private String label;

    public String getCycle() {
        return cycle;
    }

    public String getLabel() {
        return label;
    }
}
