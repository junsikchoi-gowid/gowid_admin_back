package com.nomadconnection.dapp.secukeypad;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@Builder
@RequiredArgsConstructor
public class SecuKeypadException extends RuntimeException {

    public enum Category {
        VERIFY_ERROR,
        DECRYPT_ERROR,
        ;
    }

    private final Category category;
    private final Object data;
}
