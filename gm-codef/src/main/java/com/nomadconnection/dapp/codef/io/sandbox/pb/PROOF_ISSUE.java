package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class PROOF_ISSUE {

    public static String proof_issue(String organization, String connectedId, String usePurposes, String submitTargets, String isIdentityViewYn
            , String originDataYN, String applicationType, String identity ) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.PROOF_ISSUE;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization",organization);
        bodyMap.put("connectedId",connectedId);
        bodyMap.put("usePurposes",usePurposes);
        bodyMap.put("submitTargets",submitTargets);
        bodyMap.put("isIdentityViewYN",isIdentityViewYn);
        bodyMap.put("originDataYN",originDataYN);
        bodyMap.put("applicationType",applicationType);
        bodyMap.put("identity",identity);
        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        // 응답결과 확인
        System.out.println(result);

        return result;
    }
}
