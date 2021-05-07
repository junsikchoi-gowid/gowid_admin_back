package com.nomadconnection.dapp.core.domain.res;
import lombok.*;

@Getter
@AllArgsConstructor
public enum ResAccountStatus{
    NORMAL("정상"),
    ERROR("오류"),
    DELETE("삭제"),
    STOP("중지");

    private String status;
}