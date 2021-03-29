package com.nomadconnection.dapp.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Slf4j
public class DateUtils {

    public static final String DATE_FORMAT_YYYY_MM_DD = "YYYY-MM-DD";

    public static boolean isBetweenDate(LocalDate baseDate, LocalDate startDate, LocalDate endDate){
        return  (baseDate.isEqual(startDate) || baseDate.isAfter(startDate))
            && (baseDate.isEqual(endDate) || baseDate.isBefore(endDate));
    }

    public static String convertDateFormat(String dateStr, String dateFormat) {

        SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyymmdd");
        SimpleDateFormat afterFormat = new SimpleDateFormat(dateFormat);

        try {
            // 현재 yyyymmdd로된 날짜 형식으로 java.util.Date객체를 만든다.
            Date tempDate = beforeFormat.parse(dateStr);
            return afterFormat.format(tempDate);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return dateStr;
        }
    }
}
