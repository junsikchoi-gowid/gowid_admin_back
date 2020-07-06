package com.nomadconnection.dapp.api.util;

import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.secukeypad.SecuKeypad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
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
                    randomNum = "0" + randomNum;
                }
            }
        }
        return randomNum;
    }

    // 카드발급 백엔드 연동 관련 예외처
    public static void throwBusinessException(ErrorCode.External externalErrorType, String msg) {
        log.error(msg);
        throw new SystemException(externalErrorType, msg);
    }

    // 키패드 복호화
    public static String getDecryptKeypad(HttpServletRequest httpServletRequest, String paramName, String fieldName) {
        Map<String, String> decryptData = SecuKeypad.decrypt(httpServletRequest, paramName, new String[]{fieldName});
        return decryptData.get(fieldName);
    }

    public static String getDecryptKeypad(HttpServletRequest httpServletRequest, String fieldName) {
        return getDecryptKeypad(httpServletRequest, fieldName, fieldName);
    }

    public static String getDecryptKeypad(HttpServletRequest httpServletRequest, String fieldName, boolean ENC_KEYPAD_ENABLE) {
        String encString = httpServletRequest.getParameter(fieldName);
        log.debug("keypad encrypted string : {}", encString);
        if (!ENC_KEYPAD_ENABLE) {
            return encString;
        }

        return getDecryptKeypad(httpServletRequest, fieldName, fieldName);
    }

    // 1800(전자서명값 전송)에 사용될 전자서명식별번호
    public static String getDigitalSignatureIdNumber(String bizNo, Long count) {
        if (ObjectUtils.isEmpty(count)) {
            count = 0L;
        }
        String countString = (count < 10) ? "0" + count : String.valueOf(count);
        return "GWD" + getNowYYYYMMDD() + bizNo + countString;
    }

    public static String encodeBase64(String toEncString) {
        if (StringUtils.isEmpty(toEncString)) {
            return null;
        }
        byte[] signatureString = toEncString.getBytes();
        return Base64.getEncoder().encodeToString(signatureString);
    }
}
