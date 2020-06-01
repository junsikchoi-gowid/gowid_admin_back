package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class REPORT_WITHHOLDING {

    public static String report_withholding(String organization, String connectedId, String startDate, String endDate, String identity
            , String inquiryType, String receiptNo, String manageNo, String managePassword) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.REPORT_WITHHOLDING;

        // 요청 파라미터 설정 시작 - 신고서 원천징수 이행상황 신고서
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization",organization);
        bodyMap.put("connectedId",connectedId);
        bodyMap.put("startDate",startDate);
        bodyMap.put("endDate",endDate);
        bodyMap.put("identity",identity);
        bodyMap.put("inquiryType",inquiryType);
        bodyMap.put("receiptNo",receiptNo);
        bodyMap.put("manageNo",manageNo);
        bodyMap.put("managePassword",managePassword);

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        // 응답결과 확인
        System.out.println(result);

        return result;
    }
}
