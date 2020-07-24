package com.nomadconnection.dapp.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1600;
import com.nomadconnection.dapp.api.util.MaskingUtils;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UserCorporationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCorporation {

        @ApiModelProperty("법인명(영문)")
        @NotEmpty
        private String engCorName;

        @ApiModelProperty("업종코드")
        @NotNull
        private String businessCode;

        @ApiModelProperty("사업장 전화번호 (ex. 00-000-0000)")
        @NotEmpty
        private String corNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterVenture {

        @ApiModelProperty("벤처기업확인서 보유 여부")
        @NotNull
        private Boolean isVerifiedVenture;

        @ApiModelProperty("10억이상 VC투자 여부")
        @NotNull
        private Boolean isVC;

        @ApiModelProperty("투자사명")
        private String investorName;

        @ApiModelProperty("누적투자금액")
        private String amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterStockholder {

        @ApiModelProperty("25%이상의 지분을 보유한 개인여부")
        @NotNull
        private Boolean isHold25;

        @ApiModelProperty("1대주주 개인여부")
        private Boolean isPersonal;

        @ApiModelProperty("1대주주 법인의 주주명부 보유여부")
        private Boolean isStockholderList;

        @ApiModelProperty("주주이름(한글)")
        private String name;

        @ApiModelProperty("주주이름(영문)")
        private String engName;

        @ApiModelProperty("생년월일 6자리")
        private String birth;

        @ApiModelProperty("국적")
        private String nation;

        @ApiModelProperty("지분율(앞자리 3 : 뒷자리 2)")
        private String rate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCard {

        @ApiModelProperty("희망한도")
        @NotEmpty
        private String amount;

        @ApiModelProperty("신청수량")
        @NotNull
        private Long count;

        @ApiModelProperty("명세서 수령방법")
        private ReceiveType receiveType;

        @ApiModelProperty("기본주소")
        @NotEmpty
        private String addressBasic;

        @ApiModelProperty("상세주소")
        @NotEmpty
        private String addressDetail;

        @ApiModelProperty("우편번호")
        @NotEmpty
        @Length(max = 5)
        private String zipCode;

        @ApiModelProperty("도로명 참조키캆")
        @NotEmpty
        private String addressKey;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterAccount {

        @ApiModelProperty("계좌번호 IDX")
        @NotNull
        private Long accountIdx;

        @ApiModelProperty("예금주")
        @NotEmpty
        private String accountHolder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentificationReq {

        @ApiModelProperty("카드발급정보 식별자")
        @NotNull
        private Long cardIssuanceInfoIdx;

        @ApiModelProperty("대표자시퀀스번호(1, 2, 3)")
        @NotEmpty
        private String ceoSeqNo;

        @ApiModelProperty("신분증검증방법코드")
        @NotEmpty
        private String idCode;

        @ApiModelProperty("고객한글명")
        @NotEmpty
        private String korName;

        @ApiModelProperty("주민등록번호-앞(성별포함)")
        private String identificationNumberFront;

        @ApiModelProperty("암호화 대상(주민번호뒷자리, 운전면허번호)")
        private String encryptData;

        @ApiModelProperty("발급일")
        private String issueDate;

        @ApiModelProperty("운전면허지역코드")
        private String driverLocal;

        @ApiModelProperty("일련번호 : 본인신분증위조방지코드")
        private String driverCode;

        @ApiModelProperty("신분증종류 (ID_CARD, DRIVE_LICENCE)")
        @NotNull
        private IDType idType;

        public enum IDType {
            ID_CARD,
            DRIVE_LICENCE
        }

//        @Getter
//        public enum CeoSeqType {
//            CEO_1("1"),
//            CEO_2("2"),
//            CEO_3("3");
//
//            private final String code;
//
//            CeoSeqType(String code) {
//                this.code = code;
//            }
//        }

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCeo {

        @ApiModelProperty("대표자 식별자")
        private Long ceoIdx;

        @ApiModelProperty("국적(표준약어)")
        @NotNull
        private String nation;

        @ApiModelProperty("대표자(한글)")
        private String name;

        @ApiModelProperty("대표자(영문)")
        @NotEmpty
        private String engName;

        @ApiModelProperty("통신사(SKT:01, KT:02, LG U+:03, SKT알뜰폰:04, KT알뜰폰:05, LG알뜰폰:06)")
        private String agency;

        @ApiModelProperty("휴대폰번호")
        @NotEmpty
        private String phoneNumber;

        @ApiModelProperty("생년월일(yyMMdd)")
        private String birth;

        @ApiModelProperty("성별(1:남자, 2:여자)")
        private Long genderCode;

        @ApiModelProperty("신분증 종류 (RESIDENT, DRIVER, FOREIGN")
        private CertificationType identityType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorporationRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

        @ApiModelProperty("법인명")
        private String name;

        @ApiModelProperty("법인명(영문)")
        private String engCorName;

        @ApiModelProperty("사업자등록번호")
        private String companyIdentityNo;

        @ApiModelProperty("법인등록번호")
        private String corporationNo;

        @ApiModelProperty("업종")
        private String businessType;

        @ApiModelProperty("업종코드")
        private String businessCode;

        @ApiModelProperty("업종코드정보")
        private String businessCodeValue;

        @ApiModelProperty("사업장전화번호")
        private String companyNumber;

        @ApiModelProperty("소재지")
        private String address;

        @ApiModelProperty("대표자성명")
        private String ceo;

        @ApiModelProperty("대표자종류")
        private String ceoType;

        public static CorporationRes from(Corp corp, Long idxCardInfo) {
            if (corp != null) {
                return CorporationRes.builder()
                        .idx(idxCardInfo)
                        .name(corp.resCompanyNm())
                        .engCorName(corp.resCompanyEngNm())
                        .companyIdentityNo(corp.resCompanyIdentityNo())
                        .corporationNo(corp.resUserIdentiyNo())
                        .businessType(corp.resBusinessItems())
                        .businessCode(corp.resBusinessCode())
                        .companyNumber(corp.resCompanyNumber())
                        .address(corp.resUserAddr())
                        .ceo(corp.resUserNm())
                        .ceoType(corp.resUserType())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentureRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

        @ApiModelProperty("벤처기업확인서 보유 여부")
        private Boolean isVerifiedVenture;

        @ApiModelProperty("10억이상 VC투자 여부")
        private Boolean isVC;

        @ApiModelProperty("투자사")
        private String investor;

        @ApiModelProperty("누적투자금액")
        private String amount;

        public static VentureRes from(CardIssuanceInfo cardInfo) {
            if (cardInfo != null && cardInfo.venture() != null) {
                return VentureRes.builder()
                        .idx(cardInfo.idx())
                        .isVerifiedVenture(cardInfo.venture().isVerifiedVenture())
                        .isVC(cardInfo.venture().isVC())
                        .amount(cardInfo.venture().investAmount())
                        .investor(cardInfo.venture().investor())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockholderRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

        @ApiModelProperty("25%이상의 지분을 보유한 개인여부")
        private Boolean isHold25;

        @ApiModelProperty("1대주주 개인여부")
        private Boolean isPersonal;

        @ApiModelProperty("1대주주 법인의 주주명부 보유여부")
        private Boolean isStockholderList;

        @ApiModelProperty("주주이름(한글)")
        private String name;

        @ApiModelProperty("주주이름(영문)")
        private String engName;

        @ApiModelProperty("생년월일 6자리")
        private String birth;

        @ApiModelProperty("국적")
        private String nation;

        @ApiModelProperty("지분율")
        private String rate;

        public static StockholderRes from(CardIssuanceInfo cardInfo) {
            if (cardInfo != null && cardInfo.stockholder() != null) {
                return StockholderRes.builder()
                        .idx(cardInfo.idx())
                        .isHold25(cardInfo.stockholder().isStockHold25())
                        .isPersonal(cardInfo.stockholder().isStockholderPersonal())
                        .isStockholderList(cardInfo.stockholder().isStockholderList())
                        .name(cardInfo.stockholder().stockholderName())
                        .engName(cardInfo.stockholder().stockholderEngName())
                        .birth(cardInfo.stockholder().stockholderBirth())
                        .nation(cardInfo.stockholder().stockholderNation())
                        .rate(cardInfo.stockholder().stockRate())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

        @ApiModelProperty("희망한도")
        private String hopeAmount;

        @ApiModelProperty("계산한도")
        private String calAmount;

        @ApiModelProperty("부여한도")
        private String grantAmount;

        @ApiModelProperty("신청수량")
        private Long count;

        @ApiModelProperty("명세서 수령방법")
        private ReceiveType receiveType;

        @ApiModelProperty("기본주소")
        private String addressBasic;

        @ApiModelProperty("상세주소")
        private String addressDetail;

        @ApiModelProperty("우편번호")
        private String zipCode;

        @ApiModelProperty("카드명")
        private String cardName;

        @ApiModelProperty("무기명여부")
        private Boolean isUnsigned;

        @ApiModelProperty("해외결제여부")
        private Boolean isOverseas;

        @ApiModelProperty("결제일")
        private Integer paymentDay;

        @ApiModelProperty("도로명 참조키캆")
        private String addressKey;

        public static CardRes from(CardIssuanceInfo cardInfo) {
            if (cardInfo != null && cardInfo.card() != null) {
                return CardRes.builder()
                        .idx(cardInfo.idx())
                        .hopeAmount(cardInfo.card().hopeLimit())
                        .calAmount(cardInfo.card().calculatedLimit())
                        .grantAmount(cardInfo.card().grantLimit())
                        .count(cardInfo.card().requestCount())
                        .addressBasic(cardInfo.card().addressBasic())
                        .addressDetail(cardInfo.card().addressDetail())
                        .addressKey(cardInfo.card().addressKey())
                        .zipCode(cardInfo.card().zipCode())
                        .cardName(cardInfo.card().cardName())
                        .isUnsigned(cardInfo.card().isUnsigned())
                        .isOverseas(cardInfo.card().isOverseas())
                        .paymentDay(cardInfo.card().paymentDay())
                        .receiveType(cardInfo.card().receiveType())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

        @ApiModelProperty("은행명")
        private String bank;

        @ApiModelProperty("은행코드")
        private String bankCode;

        @ApiModelProperty("계좌번호")
        private String accountNumber;

        @ApiModelProperty("예금주")
        private String accountHolder;

        public static AccountRes from(CardIssuanceInfo cardInfo, String bankName) {
            if (cardInfo != null && cardInfo.bankAccount() != null) {
                return AccountRes.builder()
                        .idx(cardInfo.idx())
                        .bank(bankName)
                        .bankCode(cardInfo.bankAccount().getBankCode())
                        .accountNumber(MaskingUtils.maskingBankAccountNumber(cardInfo.bankAccount().getBankAccount()))
                        .accountHolder(cardInfo.bankAccount().getBankAccountHolder())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CeoTypeRes {

        @ApiModelProperty("대표종류(1:단일, 2:개별, 3:공동)")
        private String type;

        @ApiModelProperty("대표수")
        private Integer count;
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CeoRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

        @ApiModelProperty("대표자 식별자")
        private Long ceoIdx;

        @ApiModelProperty("국적(표준약어)")
        private String nation;

        @ApiModelProperty("대표자(한글)")
        private String name;

        @ApiModelProperty("대표자(영문)")
        private String engName;

        @ApiModelProperty("통신사")
        private String agency;

        @ApiModelProperty("휴대폰번호")
        private String phoneNumber;

        @ApiModelProperty("생년월일(yyMMdd)")
        private String birth;

        @ApiModelProperty("성별(1:남자, 2:여자)")
        private Long genderCode;

        @ApiModelProperty("대표종류(1:단일, 2:개별, 3:공동)")
        private String ceoType;

        @ApiModelProperty("휴대폰인증 고유아이디")
        private String deviceId;

        public static CeoRes from(CeoInfo ceoInfo) {
            if (ceoInfo != null) {
                return CeoRes.builder()
                        .idx(ceoInfo.cardIssuanceInfo().idx())
                        .ceoIdx(ceoInfo.idx())
                        .nation(ceoInfo.nationality())
                        .name(ceoInfo.name())
                        .engName(ceoInfo.engName())
                        .agency(ceoInfo.agencyCode())
                        .phoneNumber(ceoInfo.phoneNumber())
                        .birth(ceoInfo.birth())
                        .genderCode(ceoInfo.genderCode())
                        .ceoType(ceoInfo.type() != null ? ceoInfo.type().getCode() : null)
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsentRes {

        @ApiModelProperty("이용약관 식별자")
        private Long consentIdx;

        @ApiModelProperty("이용약관제목")
        private String title;

        @ApiModelProperty("동의여부")
        private Boolean boolConsent;

        @ApiModelProperty("타입")
        private String consentType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssuanceReq {

        @ApiModelProperty("카드발급정보 식별자")
        private Long cardIssuanceInfoIdx;

        @ApiModelProperty("서명파일 바이너리 스트링")
        @NotEmpty
        private String signedBinaryString;

        @JsonIgnore
        private String payAccount;  // 결제 계좌번호

        @JsonIgnore
        private Long userIdx;

    }

    @Data
    @NoArgsConstructor
    public static class IssuanceRes {

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardIssuanceInfoRes {
        private List<ConsentRes> consentRes;
        private CorporationRes corporationRes;
        private VentureRes ventureRes;
        private StockholderRes stockholderRes;
        private CardRes cardRes;
        private AccountRes accountRes;
        private List<CeoRes> ceoRes;
        private List<StockholderFileRes> stockholderFileRes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockholderFileRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

        @ApiModelProperty("파일 식별자")
        private Long fileIdx;

        @ApiModelProperty("파일명")
        private String name;

        @ApiModelProperty("파일명(원본)")
        private String orgName;

        @ApiModelProperty("파일크기")
        private Long size;

        @ApiModelProperty("s3링크")
        private String s3Link;

        @ApiModelProperty("파일타입")
        private String type;

        @ApiModelProperty("gw전송여부")
        private Boolean isTransferToGw;

        public static StockholderFileRes from(StockholderFile file, Long cardIssuanceInfoIdx) {
            if (file != null) {
                return StockholderFileRes.builder()
                        .idx(cardIssuanceInfoIdx)
                        .fileIdx(file.idx())
                        .name(file.fname())
                        .orgName(file.orgfname())
                        .size(file.size())
                        .s3Link(file.s3Link())
                        .type(file.type().name())
                        .isTransferToGw(file.isTransferToGw())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessType {

        @ApiModelProperty("업종코드")
        private String code;

        @ApiModelProperty("업종명")
        private String name;

        public static BusinessType from(CommonCodeDetail code) {
            if (code != null) {
                return BusinessType.builder()
                        .code(code.code1() + code.code5())
                        .name(code.value5())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardType {

        @ApiModelProperty("코드")
        private String code;

        @ApiModelProperty("이름")
        private String name;

        public static BusinessType from(CommonCodeDetail code) {
            if (code != null) {
                return BusinessType.builder()
                        .code(code.code1())
                        .name(code.value1())
                        .build();
            }
            return null;
        }
    }



    // 1600
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class ResumeReq extends DataPart1600 {

    }

    @NoArgsConstructor
    public static class ResumeRes extends DataPart1600 {

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShinhanDriverLocalCode {

        @ApiModelProperty("지역코드")
        private String code;

        @ApiModelProperty("지역명")
        private String name;

        public static ShinhanDriverLocalCode from(CommonCodeDetail code) {
            if (code != null) {
                return ShinhanDriverLocalCode.builder()
                        .code(code.code1())
                        .name(code.value1())
                        .build();
            }
            return null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CeoValidReq {

        @ApiModelProperty("휴대폰번호('-' 제거)")
        @NotEmpty
        private String phoneNumber;

        @ApiModelProperty("대표자명")
        @NotEmpty
        private String name;

        @ApiModelProperty("주민등록번호 앞자리")
        @NotEmpty
        private String identificationNumberFront;
    }

}
