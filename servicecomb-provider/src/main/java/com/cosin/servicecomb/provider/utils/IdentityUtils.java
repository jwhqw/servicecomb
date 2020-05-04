package com.cosin.servicecomb.provider.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Xie Yifeng
 * @date 2018/4/23 10:47
 */
public class IdentityUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(IdentityUtils.class);

    /**
     * 根据hh:mm:ss时间字符串生成每天定点时间触发的cron表达式
     * @param timeWithHhMmSs
     * @return
     */
    public static String generateDayTriggerCronFromTime(String timeWithHhMmSs){
        String[] hms = timeWithHhMmSs.split(":");
        String hour = hms[0];
        String minute = hms[1];
        String seconds = hms[2];

        StringBuilder cronSB = new StringBuilder();
        //cron表达式, 秒.分.时.日.月.星期.年
        String[] cronArray = new String[7];

        if(hour.indexOf("0") == 0){
            hour = hour.substring(1);
        }
        if(minute.indexOf("0") == 0){
            minute = minute.substring(1);
        }
        if(seconds.indexOf("0") == 0){
            seconds = seconds.substring(1);
        }
        //秒
        cronArray[0] = seconds;
        //分
        cronArray[1] = minute;
        //小时
        cronArray[2] = hour;
        //天
        cronArray[3] = "*";
        //月
        cronArray[4] = "*";
        //星期
        cronArray[5] = "?";
        //年
        cronArray[6] = "*";
        //不处理年
        for(int i = 0; i < cronArray.length-1; i++){
            if(cronSB.length() > 0){
                cronSB.append(" ");
            }
            cronSB.append(cronArray[i]);
        }
        return cronSB.toString();
    }

    /**
     * 
     * 生成uuid，用于主键id，
     * 后期需要修改uuid生成规则时，改此方法
     * @author xieyifeng
     * @date 2018年10月11日 上午10:17:13
     * @return
     */
    public static String generateUuid() {
      return UUID.randomUUID().toString();
    }


    /**
     * 实体类转Map
     * @param object
     * @return
     */
    public static Map<String, Object> entityToMap(Object object) {
        Map<String, Object> map = new HashMap();
        for (Field field : object.getClass().getDeclaredFields()){
            try {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(object);
                map.put(field.getName(), o);
                field.setAccessible(flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
