package com.jfnice.kit;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期辅助类-暂不用到
 */
public class DateKit {

    public static Date zeroClockOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date zeroClockOfDay(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return zeroClockOfDay(new Date(timestamp));
    }

    public static Date zeroClockOfNextDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);//把日期往后增加一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date zeroClockOfNextDay(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return zeroClockOfNextDay(new Date(timestamp));
    }

    public static Integer getDays(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }

        date1 = zeroClockOfDay(date1);
        date2 = zeroClockOfDay(date2);
        if (date2.before(date1)) {
            Date date = new Date(date1.getTime());
            date1 = date2;
            date2 = date;
        }

        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long betweenDays = (time2 - time1) / (1000 * 3600 * 24) + 1L;

        return Integer.parseInt(String.valueOf(betweenDays));
    }

    public static Integer getWeeks(Date date1, Date date2) {
        Integer days = getDays(date1, date2);
        if (days == null) {
            return null;
        }

        if (days % 7 == 0) {
            return days / 7;
        }

        int d = days / 7;
        if (d == 0) {
            return 1;
        }
        return d + 1;
    }

}
