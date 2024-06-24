package vn.com.ecommerceapi.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String getDate(String format) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat(format);
        return ft.format(date);
    }

    public static String getExpireDate(String date, String format, int minute) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        LocalDateTime newDateTime = dateTime.plusMinutes(minute);
        return newDateTime.format(formatter);
    }
}
