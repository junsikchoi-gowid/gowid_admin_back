package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class STANDARD_FINANCIAL {

    public static String standard_financial(String organization, String connectedId, String startDate, String isIdentityViewYn, String usePurposes
            , String submitTargets, String proofType, String applicationType, String identity
    ) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.STANDARD_FINANCIAL;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization",organization);
        bodyMap.put("connectedId",connectedId);
        bodyMap.put("startDate",startDate);
        bodyMap.put("isIdentityViewYn",isIdentityViewYn);
        bodyMap.put("usePurposes",usePurposes);
        bodyMap.put("submitTargets",submitTargets);
        bodyMap.put("proofType",proofType);
        bodyMap.put("applicationType",applicationType);
        bodyMap.put("identity",identity);

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        return result;
    }
}
