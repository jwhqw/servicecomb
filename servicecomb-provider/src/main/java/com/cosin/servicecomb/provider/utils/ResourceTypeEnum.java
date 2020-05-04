package com.cosin.servicecomb.provider.utils;

/**
 * <p>ClassName: haihe-identity</p>
 * <p>Description: ${TODO} </p>
 * <p>Company:华云数据 </p>
 * <p>@Author: wanqian </p>
 * <p>@Date:  Created in 12:46 2019/5/31</p>
 */
public enum ResourceTypeEnum {

    /**
     *  CPU
     */
    CPU("CPU","CPU"),

    /**
     *  存储
     */
    DISK("DISK","存储"),

    /**
     *  浮动IP
     */
    ELASTIC_IP("ELASTIC_IP","浮动IP"),

    /**
     *  内存
     */
    MEMORY("MEMORY","内存"),

    /**
     *  网络IP
     */
    NETWORK_IP("NETWORK_IP","网络IP"),

    /**
     *  裸金属服务器
     */
    BARE_MACHINE("BARE_MACHINE","裸金属服务器"),

    /**
     *  负载均衡器
     */
    LOADBALANCER("LOADBALANCER","负载均衡器"),


    ;
    String id;
    String value;

    ResourceTypeEnum(String id, String value){
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getResourceTypeEnum(String id){
        String value;
        switch(id){
            case "CPU": value = ResourceTypeEnum.CPU.getValue(); break;
            case "DISK": value = ResourceTypeEnum.DISK.getValue(); break;
            case "ELASTIC_IP": value = ResourceTypeEnum.ELASTIC_IP.getValue(); break;
            case "MEMORY": value = ResourceTypeEnum.MEMORY.getValue(); break;
            case "NETWORK_IP": value = ResourceTypeEnum.NETWORK_IP.getValue(); break;
            case "BARE_MACHINE": value = ResourceTypeEnum.BARE_MACHINE.getValue(); break;
            case "LOADBALANCER": value = ResourceTypeEnum.LOADBALANCER.getValue(); break;
            default: value = null;
        }
        return value;
    }
}
