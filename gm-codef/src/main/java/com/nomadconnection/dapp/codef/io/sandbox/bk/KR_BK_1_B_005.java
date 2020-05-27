package com.nomadconnection.dapp.codef.io.sandbox.bk;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;

public class KR_BK_1_B_005 {

    public static String krbk1b005(
            String connectedId, String organization, String account, String startDate, String endDate, String orderBy, String currency
    ) throws IOException, InterruptedException, ParseException {
        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.KR_BK_1_B_005;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("connectedId",	connectedId);	// 엔드유저의 은행/카드사 계정 등록 후 발급받은 커넥티드아이디 예시
        bodyMap.put("organization",	organization);
        bodyMap.put("account",		account);
        bodyMap.put("startDate",	startDate);
        bodyMap.put("endDate",		endDate);
        bodyMap.put("orderBy",		orderBy);
        bodyMap.put("currency",		currency);
        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);
        return result;
    }
}
