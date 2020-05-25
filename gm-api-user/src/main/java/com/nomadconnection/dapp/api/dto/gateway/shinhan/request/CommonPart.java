package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

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

    protected String c001; // Transaction Code

    protected String c002; // TEXT 개시문자

    protected String c003; // 전문종별코드

    protected String c004; // 송수신 flag

    protected String c005; // 거래고유번호(Globally Unique Identifier)

    protected String c006; // 전문전송일자

    protected String c007; // 전문전송시각

    protected String c008; // 응답코드

    protected String c009; // 회원사번호

    protected String c010; // 대외기관코드

    protected String c011; // 조회제휴사번호

    protected String c012;

    protected String c013;

    protected String c014;

}
