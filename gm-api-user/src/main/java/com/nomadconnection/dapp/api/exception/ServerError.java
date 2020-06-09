package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@Builder
@RequiredArgsConstructor
public class ServerError extends RuntimeException {

    public enum Category {
        KCB_SERVER_ERROR,
        S3_SERVER_ERROR,
        GW_UPLOAD_SERVER_ERROR,
        GW_DELETE_SERVER_ERROR,
        ;
    }

    private final Category category;
    private final Object data;
}
