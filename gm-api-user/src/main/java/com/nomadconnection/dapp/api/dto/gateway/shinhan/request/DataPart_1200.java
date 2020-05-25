package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * @interfaceID : 1200
 * @description : 법인회원신규여부검증
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1200 extends CommonPart {

    @JsonAlias("D001")
    private String businessLicenseNo;    // 사업자등록번호

    @JsonAlias("D002")
    private String memberTypeCode;      // 법인회원구분코드(01: 신용카드회원)

    @JsonAlias("D003")
    private String isNew;               // 신규대상여부(Y/N)

    @JsonAlias("D004")
    private Long totalLimitAmount;    // 총한도금액

    @JsonAlias("D005")
    private Long specialLimitAmount;  // 특화한도금액

    @JsonAlias("D006")
    private Long approvalLimitAmount;    // 제휴약정한도금액


}
