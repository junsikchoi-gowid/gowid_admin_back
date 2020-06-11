package com.nomadconnection.dapp.api.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
public class CommonUtil {

    public static String getNowYYYYMMDD() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getNowHHMMSS() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    }

    public static String getRandom5Num() {
        Random r=new Random();
        String randomNum=""+r.nextInt(10000);
        if (randomNum.length()!=5){
            int addNum=5-randomNum.length();
            if (addNum>0){
                for (int i=0;i<addNum;i++){
                    randomNum="0"+randomNum;
                }
            }
        }
        return randomNum;
    }
}
