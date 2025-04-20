package com.marcha.resourcepojo;

public class TotalCalcPojo {
    public String userId;
    public Long totalAllowance;
    public Long selfCount;
    public Long momCount;
    public Long buyCount;
    public Long totalCount;
    public double selfRatio;

    @Override
    public String toString() {
        return "TotalCalcPojo [userId=" + userId + ", totalAllowance=" + totalAllowance + ", selfCount=" + selfCount
                + ", momCount=" + momCount + ", buyCount=" + buyCount + ", totalCount=" + totalCount + ", selfRatio="
                + selfRatio + "]";
    }
}
