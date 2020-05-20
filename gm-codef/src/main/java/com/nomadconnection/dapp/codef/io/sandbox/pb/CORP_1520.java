package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class CORP_1520 {

    public static String copr1520(String organization, String loginType, String certFile, String keyFile, String certPassword
            , String certType, String manageNo, String managePassword, String loginIdentity, String userName
            , String startDate, String isIdentityViewYN, String usePurposes, String submitTargets, String proofType, String applicationType
            , String identity
    ) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.CORP_1520;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization", organization); //기관코드
        bodyMap.put("loginType", loginType); //인증서 로그인구분
        bodyMap.put("certFile", certFile); //인증서 der 파일
        bodyMap.put("keyFile", keyFile); //인증서 key 파일
        bodyMap.put("certPassword", certPassword); //인증서 패스워드
        bodyMap.put("certType", certType); //인증서 구분
        bodyMap.put("manageNo", manageNo); //세무대리인 관리번호
        bodyMap.put("managePassword", managePassword); //세무대리인 관리 비밀번호
        bodyMap.put("loginIdentity", loginIdentity); //사용자 주민번호
        bodyMap.put("userName", userName); //사용자 이름
        bodyMap.put("startDate", startDate); //시작일자	String
        bodyMap.put("isIdentityViewYN", isIdentityViewYN); //주민번호 뒷자리 공개여부
        bodyMap.put("usePurposes", usePurposes); //사용용도
        bodyMap.put("submitTargets", submitTargets); //제출처
        bodyMap.put("proofType", proofType); //사용목적(증명구분)
        bodyMap.put("applicationType", applicationType); //신청구분
        bodyMap.put("identity", identity); //사용자주민번호/사업자 번호

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        // 응답결과 확인
        System.out.println(result);

        return result;
    }
}
