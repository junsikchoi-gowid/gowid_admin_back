package com.nomadconnection.dapp.codef.io.api;

import com.nomadconnection.dapp.codef.io.dto.Common;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiCodef{


    public static JSONObject registerCodef(Common.Account dto,String connectedId, String apDomain, String targetUrl, List<String> listCorp, @NotNull(message = "BANK or CARD") String type) throws Exception {
        HashMap<String, Object> bodyMap = new HashMap<>();
        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> accountMap1;
        String createUrlPath = apDomain + targetUrl;

        ArrayList<String> arrayList = new ArrayList<>();
        String[] istCorpString = new String[0];

        if(listCorp != null) {
            istCorpString = arrayList.toArray(new String[listCorp.size()]);
            listCorp.forEach(l -> {arrayList.add(l.toString());});
        }

        if(type.equals(CommonConstant.BUSINESSTYPE) && listCorp == null){
            istCorpString = CommonConstant.LISTBANK;
        }else if(type.equals(CommonConstant.CARDTYPE) && listCorp == null){
            istCorpString = CommonConstant.LISTCARD;
        }else if(type.equals(CommonConstant.BUSINESSTYPE)){
            istCorpString = ArrayUtils.removeElements(CommonConstant.LISTBANK, istCorpString);
        }else if(type.equals(CommonConstant.CARDTYPE)){
            istCorpString = ArrayUtils.removeElements(CommonConstant.LISTCARD, istCorpString);
        }

        System.out.println(istCorpString);

        for( String s : istCorpString){
            accountMap1 = new HashMap<>();
            accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
            accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
            accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
            accountMap1.put("organization",	s);// 기관코드
            accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
            accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
            accountMap1.put("certType",     CommonConstant.CERTTYPE);
            accountMap1.put("certFile",     dto.getCertFile());
            list.add(accountMap1);
        }

        bodyMap.put("accountList", list);
        bodyMap.put("connectedId", connectedId);
        JSONParser jsonParse = new JSONParser();
        return (JSONObject)jsonParse.parse(ApiRequest.request(createUrlPath, bodyMap));
    }
}

