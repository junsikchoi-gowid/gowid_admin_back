package com.nomadconnection.dapp.codef.io.sandbox.bk;

import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import org.json.simple.parser.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

public class KR_BK_1_B_007 {

    public static String krbk1b007(

    ) throws IOException, InterruptedException, ParseException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        // 요청 URL 설정
        String urlPath = CommonConstant.getRequestDomain() + CommonConstant.KR_BK_1_B_007;

        // 요청 파라미터 설정 시작 - 은행사별로 요구되는 요청 파라미터가 상이합니다. 비밀번호 관련 필드가 사용되는 경우 RSA 암호화가 필요합니다.
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();

        bodyMap.put("id", "인터넷뱅킹 아이디");
        bodyMap.put("password", RSAUtil.encryptRSA("인터넷뱅킹 비밀번호", CommonConstant.PUBLIC_KEY));
        bodyMap.put("fastId", "빠른계좌조회 아이디");
        bodyMap.put("fastPassword", RSAUtil.encryptRSA("빠른계좌조회 비밀번호", CommonConstant.PUBLIC_KEY));

        bodyMap.put("organization",	"기관코드");

        bodyMap.put("account",		"계좌번호");
        bodyMap.put("accountPassword", RSAUtil.encryptRSA("계좌 비밀번호", CommonConstant.PUBLIC_KEY));
        bodyMap.put("startDate",	"조회시작일자");
        bodyMap.put("endDate",		"조회종료일자");
        bodyMap.put("orderBy",		"정렬기준");

        bodyMap.put("identity", "사업자번호/주민번호");

        bodyMap.put("smsAuthNo", "SMS문자인증번호");

        // 요청 파라미터 설정 종료

        // API 요청
        String result = ApiRequest.request(urlPath, bodyMap);
        return result;
    }
}
