package com.nomadconnection.dapp.codef.io.sandbox.st;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import lombok.SneakyThrows;

import java.util.HashMap;

public class KR_ST_1_S_002 {

    @SneakyThrows
    public static String krst1a002(
            String connectedId, String organization, String account, String startDate, String endDate, String orderBy, String accountPassword
    ){
        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.KR_ST_1_S_002;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("connectedId", connectedId);        // 커넥티드 아이디
        bodyMap.put("organization", organization);        // 기관코드
        bodyMap.put("account", account);        // 계좌번호
        bodyMap.put("accountPassword", accountPassword);        // 계좌 비밀번호
        bodyMap.put("startDate", startDate);    // 조회시작일자
        bodyMap.put("endDate", endDate);    // 조회종료일자
        bodyMap.put("orderBy", orderBy);    // 정렬기준

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        return result;
    }
}
