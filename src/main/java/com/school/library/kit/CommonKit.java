package com.school.library.kit;

import com.jfinal.kit.StrKit;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description 公共工具类
 * @Author jsy
 * @Date 2020/3/16
 * @Version V1.0
 **/

public class CommonKit {

    /**
     * 格式化金额，将分转换成元
     * @param money
     * @return
     */
    public static String formatMoney(Integer money){
        if(null == money){
            return null;
        }
        return BigDecimal.valueOf(money).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 格式化金额，将分转换成元
     * @param money
     * @return
     */
    public static Integer formatMoneyToFen(Double money){
        if(null == money){
            return null;
        }

        BigDecimal b1=new BigDecimal(money.toString());//把double的v1转换成string
        BigDecimal b2=new BigDecimal(100);
        BigDecimal multiply = b1.multiply(b2);
        return multiply.intValue();
    }

    /**
     * 用apache commons对数据进行MD5签名
     * @param data
     * @return
     */
    public static String MD5(String data){
        return DigestUtils.md5Hex(data);
    }

    /**
     * 计算两个日期相差的天数
     * @param beginDate
     * @param endDate
     * @return
     */
    public static long differDays(Date beginDate, Date endDate){
        if(beginDate == null){
            return 0;
        }
        if(endDate == null){
            return 0;
        }

        long day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 日期加上天数之后的日期
     * @param target 目标日期
     * @param days 增加的天数（可以为负数）
     * @return
     */
    public static Date addDays(Date target, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(target);
        c.add(Calendar.DAY_OF_YEAR, days);
        return c.getTime();
    }

    /**
     * 日期加上月份之后的日期
     * @param target 目标日期
     * @param months 增加的月份（可以为负数）
     * @return
     */
    public static Date addMonths(Date target, int months){
        Calendar c = Calendar.getInstance();
        c.setTime(target);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    /**
     * 日期加上年份之后的日期
     * @param target 目标日期
     * @param years 增加的年份（可以为负数）
     * @return
     */
    public static Date addYears(Date target, int years){
        Calendar c = Calendar.getInstance();
        c.setTime(target);
        c.add(Calendar.YEAR, years);
        return c.getTime();
    }

    /**
     * 处理开始时间
     *
     * @param startTime 开始时间
     * @return String
     */
    public static String dealStartTime(String startTime) {
        if (StrKit.notBlank(startTime) && startTime.length() == 10) {
            startTime += " 00:00:00";
        }
        return startTime;
    }

    /**
     * 处理结束时间
     *
     * @param endTime 结束时间
     * @return String
     */
    public static String dealEndTime(String endTime) {
        if (StrKit.notBlank(endTime) && endTime.length() == 10) {
            endTime += " 23:59:59";
        }
        return endTime;
    }

    /**
     * 格式化字符串成为日期
     * @param dateStr
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateStr, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if(StrKit.notBlank(dateStr) && dateStr.length() == pattern.length()){
            return sdf.parse(dateStr);
        }
        return null;
    }

}
