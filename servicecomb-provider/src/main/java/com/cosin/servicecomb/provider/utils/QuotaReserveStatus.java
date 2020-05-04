package com.cosin.servicecomb.provider.utils;

/**
 * 
 * @author caizhiyang
 *
 */
public enum QuotaReserveStatus {
    /**
     * 正常状态
     */
	Uncommitted("未提交"),
    /**
     * 延期状态
     */
	Committed("已提交"),
    /**
     * 结束状态
     */
	Canceled("已取消");
	
    String value;
    QuotaReserveStatus(String value){
        this.value = value;
    }
    public static boolean check(String name){
        try{
            valueOf(name);
        }catch (Exception ex){
            return false;
        }
        return true;
    }
}
