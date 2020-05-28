package com.nomadconnection.dapp.api.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CommonUtil {

    public static String getNowYYYYMMDD() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getNowHHMMSS() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    }
}
