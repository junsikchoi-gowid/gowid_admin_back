package com.nomadconnection.dapp.api.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@Builder
@RequiredArgsConstructor
public class BadRequestedException extends RuntimeException {

    public enum Category {
        EXCESS_UPLOAD_FILE_LENGTH,
        INVALID_UPLOAD_FILE_SIZE,
        INVALID_CEO_IDENTIFICATION,
    }

    private final Category category;
    private final String desc;
}
