package com.nomadconnection.dapp.core.utils;

import java.time.LocalDate;

public class DateUtils {

    public static boolean isBetweenDate(LocalDate baseDate, LocalDate startDate, LocalDate endDate){
        return  (baseDate.isEqual(startDate) || baseDate.isAfter(startDate))
            && (baseDate.isEqual(endDate) || baseDate.isBefore(endDate));
    }

}
