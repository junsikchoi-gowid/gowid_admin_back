package com.nomadconnection.dapp.api.util;

import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.secukeypad.SecuKeypad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class CommonUtil {

    public static String getNowYYYYMMDD() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getNowHHMMSS() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    }

    public static String getNowYYYYMM() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    public static String getRandom5Num() {
        Random r = new Random();
        StringBuilder randomNum = new StringBuilder("" + r.nextInt(10000));
        if (randomNum.length() != 5) {
            int addNum = 5 - randomNum.length();
            if (addNum > 0) {
                for (int i = 0; i < addNum; i++) {
                    randomNum.insert(0, "0");
                }
            }
        }
        return randomNum.toString();
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

    // 길이가 length 넘어가면, length 길이 만큼 남기고 컷
    public static String cutString(String source, int length) {
        if (StringUtils.isEmpty(source)) {
            return source;
        }
        if (source.length() > length) {
            return source.substring(0, length);
        }
        return source;
    }

    public static String divisionString(String source, int division) {
        if (StringUtils.isEmpty(source)) {
            return source;
        }
        BigDecimal convertNumber = new BigDecimal(source);
        return convertNumber.divide(BigDecimal.valueOf(division)).toPlainString();
    }

    public static String replaceHyphen(String target) {
        return target.replaceAll("-", "");
    }

    public static String birthLenConvert6To8(String yymmdd) {
        if (!StringUtils.hasText(yymmdd) || yymmdd.length() != 6) {
            return yymmdd;
        }
        int currentYear = Integer.parseInt(String.valueOf(LocalDate.now().getYear()).substring(2));
        int year = Integer.parseInt(yymmdd.substring(0, 2));
        if (year >= currentYear) {
            return "19" + yymmdd;
        } else {
            return "20" + yymmdd;
        }
    }

    public static String getLowerStringNumber(String num1, String num2) {
        if (!StringUtils.hasText(num1) || !StringUtils.hasText(num2)) {
            return "0";
        }
        Long number1 = Long.parseLong(num1);
        Long number2 = Long.parseLong(num2);
        switch (number1.compareTo(number2)) {
            case 1:
                return num2;
            case 0:
            case -1:
                return num1;
        }
        return "0";
    }

    public static boolean isBetweenDate(LocalDate baseDate, LocalDate startDate, LocalDate endDate){
        return  (baseDate.isEqual(startDate) || baseDate.isAfter(startDate))
            && (baseDate.isEqual(endDate) || baseDate.isBefore(endDate));
    }

    public static String getValueOrDefault(String value, String defaultValue) {
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public static String sumOfDates(int i) {
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.DATE, i);
        return df.format(cal.getTime());
    }

    /**
     * Add Month
     *
     * @param dateStr 년월 String (ex. YYYYMM)
     * @param addMonths 추가할 월 수
     * @return
     */
    public static String addMonths(String dateStr, int addMonths) throws ParseException {
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMM");

        try {
            Date date = df.parse(dateStr);

            cal.setTime(date);
            cal.add(Calendar.MONTH, addMonths);

            return df.format(cal.getTime());
        }catch(ParseException pe) {
            log.error(pe.getMessage(), pe);
            throw pe;
        }
    }

    /**
     * Subtract Month
     *
     * @param fromDate 시작 년월 String (ex. YYYYMM)
     * @param toDate 종료 년월 String (ex. YYYYMM)
     * @return
     */
    public static Integer subtractMonth(String fromDate, String toDate) {
        Integer fromMonth = Integer.parseInt(fromDate.substring(4));
        Integer toMonth = Integer.parseInt(toDate.substring(4));
        if (fromMonth < toMonth) {
            return toMonth - fromMonth;
        }else {
            return (toMonth + 12) - fromMonth;
        }
    }

    public static String get4DigitRandomNumber(){
        return String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * Convert LocalDateTime to String
     * (parse없이 변환 시 시간에 'T'가 붙음)
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String getLocalDateTimeToString(LocalDateTime localDateTime, String pattern) {
        return LocalDateTime.parse(localDateTime.toString()).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Text의 마지막부터 index만큼을 추출한다.
     *
     * @param str      추출할 문자열
     * @param index    추출 범위
     * @return
     */
    public static String extractTextFromLast(String str, int index) {
        return StringUtils.isEmpty(str) ? null : str.substring(str.length() - index);
    }
}
