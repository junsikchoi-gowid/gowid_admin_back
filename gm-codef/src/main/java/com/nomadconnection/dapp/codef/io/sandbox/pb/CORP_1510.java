package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class CORP_1510 {

    public static String copr1510(String phoneNo, String password, String inquiryType, String searchWord, String useType
            , String ePrepayNo, String ePrepayPass, String competentRegistryOffice, String companyType, String registryStatus
            , String branchType, String issueType, String originData, String originDataYN, String companyNm, String cancelYN
    ) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.CORP_1510;

        // 요청 파라미터 설정 시작
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization", "0002"); // 은행 코드
        bodyMap.put("phoneNo", "01000000000" ); // 전화번호
        bodyMap.put("password", "gowid" ); // 비밀번호
        bodyMap.put("inquiryType", inquiryType ); // 검색구분
        bodyMap.put("searchWord", searchWord ); //검색어
        bodyMap.put("useType", useType ); // 등기사항증명서종류
        bodyMap.put("ePrepayNo", ePrepayNo ); // 선불전자지급수단 번호
        bodyMap.put("ePrepayPass", ePrepayPass ); // 선불전자지급수단 비밀번호
        bodyMap.put("competentRegistryOffice", competentRegistryOffice ); // 관할등기소
        bodyMap.put("companyType", companyType ); // 법인구분
        bodyMap.put("registryStatus", registryStatus ); // 등기부상태
        bodyMap.put("branchType", branchType ); // 본지점구분
        bodyMap.put("issueType", issueType ); // 발행구분
        bodyMap.put("originData", originData ); // 원문Data
        bodyMap.put("originDataYN", originDataYN ); // 원문Data 포함 여부
        bodyMap.put("companyNm", companyNm ); // 상호
        bodyMap.put("cancelYN", cancelYN ); // 주말여부
        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);

        // 응답결과 확인
        System.out.println(result);

        return result;
    }
}
