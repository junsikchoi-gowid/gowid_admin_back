package com.nomadconnection.dapp.api.util;

import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.yettiesoft.vestsign.base.code.CommonConst;
import com.yettiesoft.vestsign.external.SignVerifier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignVerificationUtil {

    public static SignVerifier verifySignedBinaryString(String signedString) {
        SignVerifier signVerifier;
        try {
            signVerifier = new SignVerifier(signedString, CommonConst.CERT_STATUS_NONE, CommonConst.ENCODE_HEX);
            signVerifier.verify();
            loggingSignVerifier(signVerifier);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "signed binary string");
        }

        if (signVerifier.getLastErrorCode() != 0) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "signed binary string. errorCode=" + signVerifier.getLastErrorCode());
        }

        return signVerifier;
    }

    public static String verifySignedBinaryStringAndGetPlainString(String signedString) {
        SignVerifier signVerifier = verifySignedBinaryString(signedString);
        return signVerifier.getSignedMessageText();
    }

    private static void loggingSignVerifier(SignVerifier signVerifier) {
        log.debug("### 에러 코드 : " + signVerifier.getLastErrorCode());
        log.debug("### 검증 결과 : " + signVerifier.getLastErrorMsg());
        log.debug("### 전자 서명 원문 : " + signVerifier.getSignedMessageText());
        // CertificateInfo cert = signVerifier.getSignerCertificate();
        // log.debug("### 사용자 인증서 정책 : " + cert.getPolicyIdentifier());
        // log.debug("### 사용자 인증서 DN : " + cert.getSubject());
        // log.debug("### 사용자 인증서 serial : " + cert.getSerial());
    }
}
