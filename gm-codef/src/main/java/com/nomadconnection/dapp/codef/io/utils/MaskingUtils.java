package com.nomadconnection.dapp.codef.io.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class MaskingUtils {

    public static String maskingBankAccountNumber(String bankAccountNumber) {
        if (!StringUtils.hasText(bankAccountNumber) || bankAccountNumber.length() < 10) {
            return bankAccountNumber;
        }
        StringBuilder stringBuilder = new StringBuilder(bankAccountNumber);
        stringBuilder.setCharAt(5, '*');
        stringBuilder.setCharAt(6, '*');
        stringBuilder.setCharAt(7, '*');
        stringBuilder.setCharAt(8, '*');
        stringBuilder.setCharAt(9, '*');
        return stringBuilder.toString();
    }
}
