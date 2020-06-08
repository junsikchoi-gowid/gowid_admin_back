package com.nomadconnection.dapp.codef.io.sandbox.bk;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
public class KR_BK_1_B_003 {

    public static String krbk1b003(
            String connectedId, String organization, String account, String startDate, String endDate, String orderBy, String inquiryType
    ) throws IOException, InterruptedException, ParseException {
        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.KR_BK_1_B_003;


        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("connectedId",	connectedId);	// 엔드유저의 은행/카드사 계정 등록 후 발급받은 커넥티드아이디 예시
        bodyMap.put("organization",	organization); //기관코드
        bodyMap.put("account",		account); //계좌번호
        bodyMap.put("startDate",	startDate); //조회시작일자
        bodyMap.put("endDate",		endDate);// 조회종료일자
        bodyMap.put("orderBy",		orderBy);// 정렬기준
        bodyMap.put("inquiryType",	inquiryType);// 조회구분
        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);
        return result;
    }
}
