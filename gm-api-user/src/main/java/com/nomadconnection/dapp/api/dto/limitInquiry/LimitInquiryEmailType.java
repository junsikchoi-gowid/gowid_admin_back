package com.nomadconnection.dapp.api.dto.limitInquiry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LimitInquiryEmailType {
    SUBJECT("[Gowid] 한도 상담 요청 안내"),
    TEMPLATE_NAME("limit-inquiry");

    String value;
}
