package com.nomadconnection.dapp.codef.io.sandbox.pb;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class CORP_REGISTER {

    public static String corp_register(String organization, String phoneNo, String password, String inquiryType, String searchWord
            , String useType, String ePrepayNo, String ePrepayPass, String competentRegistryOffice, String companyType
            , String registryStatus, String branchType, String issueType, String originData, String originDataYN
            , String companyNm, String cancelYN
    ) throws InterruptedException, ParseException, IOException {

        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.CORP_REGISTER;

        // 요청 파라미터 설정 시작 - 법인등기부등본
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("organization",organization);
        bodyMap.put("phoneNo",phoneNo);
        bodyMap.put("password",password);
        bodyMap.put("inquiryType",inquiryType);
        bodyMap.put("searchWord",searchWord);
        bodyMap.put("useType",useType);
        bodyMap.put("ePrepayNo",ePrepayNo);
        bodyMap.put("ePrepayPass",ePrepayPass);
        bodyMap.put("competentRegistryOffice",competentRegistryOffice);
        bodyMap.put("companyType",companyType);
        bodyMap.put("registryStatus",registryStatus);
        bodyMap.put("branchType",branchType);
        bodyMap.put("issueType",issueType);
        bodyMap.put("originData",originData);
        bodyMap.put("originDataYN",originDataYN);
        bodyMap.put("companyNm",companyNm);
        bodyMap.put("cancelYN",cancelYN);

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);


        return result;
    }
}
