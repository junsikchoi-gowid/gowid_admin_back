package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class CASH_PURCHASE {

    public static String cash_purchase(String organization, String connectedId, String identity, String startDate, String endDate, String orderBy
            , String inquiryType) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.CASH_PURCHASE;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization", organization ); // 은행 코드
        bodyMap.put("connectedId", connectedId);
        bodyMap.put("identity",identity);
        bodyMap.put("startDate",startDate);
        bodyMap.put("endDate",endDate);
        bodyMap.put("orderBy",orderBy);
        bodyMap.put("inquiryType",inquiryType);

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        return result;
    }
}
