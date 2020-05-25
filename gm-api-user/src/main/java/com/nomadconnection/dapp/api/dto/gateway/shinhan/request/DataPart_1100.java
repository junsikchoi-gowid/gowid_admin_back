package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * @interfaceID : 1100
 * @description : 법인카드신청
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1100 extends CommonPart {

    @JsonAlias("D001")
    private String companyCsNo;  // 법인고객번호

    @JsonAlias("D002")
    private String memberTypeCode;      // 법인회원구분코드(01: 신용카드회원)

    @JsonAlias("D003")
    private String dupIssueTypeCode; // 중복발급구분코드

    @JsonAlias("D004")
    private String officeNo;    // 사업장번호

    @JsonAlias("D005")
    private String productNo;   // 카드상품번호

    @JsonAlias("D006")
    private String gradeCode;   // 카드등급코드

    @JsonAlias("D007")
    private String brandCode;   // 카드브랜드코드

    @JsonAlias("D008")
    private String designCode;  // 카드디자인구분코드

    @JsonAlias("D009")
    private String featureCode; // 카드외형구분코드

    @JsonAlias("D010")
    private String icPurposeCode;   // 카드IC용도구분코드

    @JsonAlias("D011")
    private String pictureCode; // 카드사진구분코드

    @JsonAlias("D012")
    private String trafficCode; //  교통기능구분코드

    @JsonAlias("D013")
    private String cardTypeCode;    // 카드유형코드

    @JsonAlias("D014")
    private String icCashAvailableCashCode;   //카드IC현금기능구분코드

    @JsonAlias("D015")
    private String availableCash;   //현금기능부여여부

    @JsonAlias("D016")
    private String memberName;  // 제휴정보명

    @JsonAlias("D017")
    private String companyProductCode;  // 법인사업장상품코드

    @JsonAlias("D018")
    private String limitModeTypeCode;   // 카드한도방식구분코드

    @JsonAlias("D019")
    private String limitYn; // 카드한도사용여부

    @JsonAlias("D020")
    private String limitAmount; //카드한도금액

    @JsonAlias("D021")
    private String password;    //비밀번호

    @JsonAlias("D022")
    private String payMethodCode;  //  결제방법코드

    @JsonAlias("D023")
    private String payDate; // 결제일

    @JsonAlias("D024")
    private String bankCode;    // 은행코드

    @JsonAlias("D025")
    private String payAccount;  // 결제계좌

    @JsonAlias("D026")
    private String holderName;  // 예금주명

    @JsonAlias("D027")
    private String holderRegisterNo;    // 예금주주민번호

    @JsonAlias("D028")
    private String holderRelateCode;    // 예금주관계코드

    @JsonAlias("D029")
    private String statementReceiveCode;    // 명세서수령방법코드

    @JsonAlias("D030")
    private String receiverTypeCode;    //법인카드수령인구분코드

    @JsonAlias("D031")
    private String receiverPostFrontNo; // 수령지우편앞번호

    @JsonAlias("D032")
    private String receiverPostBackNo;  // 수령지우편뒷번호


    @JsonAlias("D033")
    private String receiverAddress; // 카드수령지기본주소

    @JsonAlias("D034")
    private String receiverDetailAddress;   // 카드수령지상세주소

    @JsonAlias("D035")
    private String receiverIdentifyMobileNo;    // 수령인휴대전화식별번호

    @JsonAlias("D036")
    private String receiverStationMobileNo; // 수령인휴대전화국번호

    @JsonAlias("D037")
    private String receiverUniqueMobileNo;  // 수령인휴대전화고유번호

    @JsonAlias("D038")
    private String smsApplyYn;  // SMS신청여부

    @JsonAlias("D039")
    private String publicCardCount; // 공동카드매수

    @JsonAlias("D040")
    private String adviserNo;   // 권유자사번

    @JsonAlias("D041")
    private String identityCheckerNo;   // 본인확인자사번

    @JsonAlias("D042")
    private String searchAgreementYn;   // 필수조회동의여부

    @JsonAlias("D043")
    private String provisionAgreementYn;    // 필수제공동의여부

    @JsonAlias("D044")
    private String identifyAgreementYn; // 고객식별정보처리동의

    @JsonAlias("D045")
    private String collectionAgreementYn;   // 필수수집이용동의여부

    @JsonAlias("D046")
    private String receiverAddressCode; // 카드수령지주소종류코드

    @JsonAlias("D047")
    private String receiverStreetAddress;   // 카드수령지도로명참조주소

    @JsonAlias("D048")
    private String materialCardYn;  // 실물카드여부

    @JsonAlias("D049")
    private String partnerShipInfo;  // 제휴정보

}
