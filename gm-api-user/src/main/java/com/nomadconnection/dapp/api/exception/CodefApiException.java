package com.nomadconnection.dapp.api.exception;

import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.codef.io.helper.ScrapingMessageGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CodefApiException extends RuntimeException{

    private final String code;
    private final String message;

    public CodefApiException(ResponseCode responseCode) {
        code = responseCode.getCode();
        message = responseCode.getScrapingMessageGroup().getMessage();
    }

    public CodefApiException(ResponseCode responseCode, ScrapingMessageGroup responseGroup) {
        code = responseCode.getCode();
        message = responseGroup.getMessage();
    }

}
