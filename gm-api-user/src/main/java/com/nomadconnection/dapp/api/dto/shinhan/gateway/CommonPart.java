package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * CommonPart
 *
 * @interfaceID : -
 * @description : 전문공통부
 */

@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonPart {

    protected String c001; // Transaction Code

    protected String c002; // TEXT 개시문자

    protected String c003;  // 전문길이

    protected String c004;  // 전문종별코드

    protected String c005;  // 송수신 flag

    protected String c006;  // 거래고유번호(Globally Unique Identifier)

    protected String c007;  // 전문전송일자

    protected String c008;  // 전문전송시각

    protected String c009;  // 응답코드

    protected String c010;  // 회원사번호

    protected String c011;  // 대외기관코드

    protected String c012;  // 조회제휴사번호

    protected String c013;  // 응답메시지

    protected String c014;  // 예비

}


