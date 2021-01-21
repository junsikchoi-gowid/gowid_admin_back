package com.nomadconnection.dapp.codef.io.sandbox.st;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class KR_ST_1_S_001 {

    public static String krst1a001(String connectedId, String bankCode) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.KR_ST_1_S_001;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("connectedId", connectedId);    // 엔드유저의 은행/카드사 계정 등록 후 발급받은 커넥티드아이디 예시
        bodyMap.put("organization", bankCode); // 은행 코드
        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        return result;
    }
}
