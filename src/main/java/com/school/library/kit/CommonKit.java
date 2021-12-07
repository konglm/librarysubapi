package com.school.library.kit;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.StrKit;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @Description 公共工具类
 * @Author jsy
 * @Date 2020/3/16
 * @Version V1.0
 **/

public class CommonKit {

    /**
     * 星期几的简单写法
     */
    private static final String[] simpleDaysOfWeek = {"日", "一", "二", "三", "四", "五", "六"};

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

    /**
     * 获取指定年月的开始日期
     * @param year
     * @param month
     * @return
     */
    public static String getBeginTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate localDate = yearMonth.atDay(1);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.of("Asia/Shanghai"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(Date.from(zonedDateTime.toInstant()));
    }

    /**
     * 获取指定年月的结束日期
     * @param year
     * @param month
     * @return
     */
    public static String getEndTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        LocalDateTime localDateTime = endOfMonth.atTime(23, 59, 59, 999);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(Date.from(zonedDateTime.toInstant()));
    }

    /**
     * 计算百分比
     * @param numerator
     * @param denominator
     * @return
     */
    public static String getRatio(int numerator, int denominator) {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float)  numerator/ (float)denominator* 100);//所占百分比
        return result;
    }

    /**
     *
     * @Description 将日期字符串转换成简洁版的星期几
     * @author jinshiye
     * @date 2018年7月2日下午5:19:59
     * @Title getSimpleDayOfWeek
     * @param date
     * @return
     */
    public static String getSimpleDayOfWeek(Date date, String dateStr){
        String week = "";
        if(null!= date){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0){
                w = 0;
            }
            week = simpleDaysOfWeek[w];
        }else if(StrKit.notBlank(dateStr)){
            Date dateTime = DateKit.toDate(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0){
                w = 0;
            }
            week = simpleDaysOfWeek[w];
        }
        return week;
    }

}
