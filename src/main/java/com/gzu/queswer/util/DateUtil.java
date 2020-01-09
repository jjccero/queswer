package com.gzu.queswer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private final static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date getDate(){
        return new Date();
    }
    public static String getDateString(Date date){
        return simpleDateFormat.format(date);
    }
}
