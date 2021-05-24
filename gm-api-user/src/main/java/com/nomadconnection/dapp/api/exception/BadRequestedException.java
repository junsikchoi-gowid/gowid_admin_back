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
        INVALID_CEO_IDENTIFICATION,
        NOT_ALLOWED_EXTENSION
    }

    private final Category category;
    private final String desc;
}
