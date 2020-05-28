package com.nomadconnection.dapp.api.dto.shinhan.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UiResponse {
    private String code;
    private String desc;
    private String interfaceCode;
}
