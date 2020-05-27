package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class TAX_INVOICE {

    public static String tax_invoice(String organization, String connectedId, String inquiryType, String searchType, String startDate, String endDate
            , String sortby, String orderBy, String transeType, String identity) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.TAX_INVOICE;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization", organization); // 기관 코드
        bodyMap.put("connectedId",connectedId); //
        bodyMap.put("inquiryType",inquiryType); //
        bodyMap.put("searchType",searchType); //
        bodyMap.put("startDate",startDate); //
        bodyMap.put("endDate",endDate); //
        bodyMap.put("sortby",sortby); //
        bodyMap.put("orderBy",orderBy); //
        bodyMap.put("transeType",transeType); //
        bodyMap.put("identity",identity); //

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        // 응답결과 확인
        System.out.println(result);

        return result;
    }
}
