package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * CommonPart
 *
 * @interfaceID : -
 * @description : 전문공통부
 */

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class CommonPart {

    @JsonAlias("C001")
    protected String transactionCode; // Transaction Code

    @JsonAlias("C002")
    protected String initialText; // TEXT 개시문자

    @JsonAlias("C003")
    protected String protocolCode; // 전문종별코드

    @JsonAlias("C004")
    protected String transferFlag; // 송수신 flag

    @JsonAlias("C005")
    protected String guid; // 거래고유번호(Globally Unique Identifier)

    @JsonAlias("C006")
    protected String transferDate; // 전문전송일자

    @JsonAlias("C007")
    protected String transferTime; // 전문전송시각

    @JsonAlias("C008")
    protected String responseCode; // 응답코드

    @JsonAlias("C009")
    protected String memberNo; // 회원사번호

    @JsonAlias("C010")
    protected String memberCode; // 대외기관코드

    @JsonAlias("C011")
    protected String searchMemberNo; // 조회제휴사번호

    @JsonAlias("C012")
    protected String fullTextLength;

    @JsonAlias("C013")
    protected String responseMessage;

    @JsonAlias("C014")
    protected String spare;

}
