package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CeoInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.ReceiveType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

        @ApiModelProperty("사업장 전화번호")
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

        @ApiModelProperty("투자사")
        private Long investor;

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
        @NotEmpty
        private String name;

        @ApiModelProperty("주주이름(영문)")
        @NotEmpty
        private String engName;

        @ApiModelProperty("생년월일 6자리")
        @NotEmpty
        private String birth;

        @ApiModelProperty("국적")
        @NotEmpty
        private String nation;

        @ApiModelProperty("지분율")
        @NotNull
        private Long rate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCard {

        @ApiModelProperty("희망한도")
        @NotNull
        private String amount;

        @ApiModelProperty("신청수량")
        @NotNull
        private Long count;

        @ApiModelProperty("명세서 수령방법")
        private ReceiveType receiveType;

        @ApiModelProperty("기본주소")
        private String addressBasic;

        @ApiModelProperty("상세주소")
        private String addressDetail;

        @ApiModelProperty("우편번호")
        private String zipCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterAccount {

        @ApiModelProperty("은행코드")
        @NotEmpty
        private String bank;

        @ApiModelProperty("계좌번호")
        @NotEmpty
        private String accountNumber;

        @ApiModelProperty("예금주")
        @NotEmpty
        private String accountHolder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterCeo {

        @ApiModelProperty("국적(표준약어)")
        @NotNull
        private String nation;

        @ApiModelProperty("대표자(한글)")
        private String name;

        @ApiModelProperty("대표자(영문)")
        @NotEmpty
        private String engName;

        @ApiModelProperty("통신사")
        private String agency;

        @ApiModelProperty("휴대폰번호")
        @NotEmpty
        private String phoneNumber;

        @ApiModelProperty("생년월일(19930412)")
        private String birth;

        @ApiModelProperty("성별(1:남자, 2:여자)")
        private Long genderCode;

        @ApiModelProperty("신분증 종류")
        private IDType identityType;

        public enum IDType {
            RESIDENT,
            DRIVER,
            FOREIGN
        }
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
                        .businessType(corp.resBusinessItems()) //TODO: delete
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
                        // TODO: investor 정보
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
        private Long rate;

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
        private String amount;

        @ApiModelProperty("신청수량")
        private Long count;

        @ApiModelProperty("명세서 수령방법")
        private String receiveType;

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

        public static CardRes from(CardIssuanceInfo cardInfo) {
            if (cardInfo != null && cardInfo.card() != null) {
                return CardRes.builder()
                        .idx(cardInfo.idx())
                        .amount(cardInfo.card().hopeLimit())
                        .count(cardInfo.card().requestCount())
                        .addressBasic(cardInfo.card().addressBasic())
                        .addressDetail(cardInfo.card().addressDetail())
                        .zipCode(cardInfo.card().zipCode())
                        .cardName(cardInfo.card().cardName())
                        .isUnsigned(cardInfo.card().isUnsigned())
                        .isOverseas(cardInfo.card().isOverseas())
                        .paymentDay(cardInfo.card().paymentDay())
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
        @NotEmpty
        private String bank;

        @ApiModelProperty("계좌번호")
        @NotEmpty
        private String accountNumber;

        @ApiModelProperty("예금주")
        private String accountHolder;

        public static AccountRes from(CardIssuanceInfo cardInfo) {
            if (cardInfo != null && cardInfo.bankAccount() != null) {
                return AccountRes.builder()
                        .idx(cardInfo.idx())
                        .bank(cardInfo.bankAccount().getBankCode())
                        .accountNumber(cardInfo.bankAccount().getBankAccount())
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CeoRes {

        @ApiModelProperty("카드발급정보 식별자")
        private Long idx;

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

        @ApiModelProperty("생년월일(19930412)")
        private String birth;

        @ApiModelProperty("성별(1:남자, 2:여자)")
        private Long genderCode;

        public static CeoRes from(CeoInfo ceoInfo) {
            if (ceoInfo != null) {
                return CeoRes.builder()
                        .idx(ceoInfo.cardIssuanceInfo().idx())
                        .nation(ceoInfo.nationality())
                        .name(ceoInfo.name())
                        .engName(ceoInfo.engName())
                        .agency(ceoInfo.agencyCode())
                        .phoneNumber(ceoInfo.phoneNumber())
                        .birth(ceoInfo.birth())
                        .genderCode(ceoInfo.genderCode())
                        .build();
            }
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssuanceReq {

        @ApiModelProperty("카드발급정보 식별자")
        @NotEmpty
        private Long cardIssuanceInfoIdx;

        @ApiModelProperty("카드비빌번호")
        @NotEmpty
        private Long password;

        @ApiModelProperty("대표자주민등록번호1")
        @NotEmpty
        private String ceoRegisterNo1;

        @ApiModelProperty("대표자주민등록번호2")
        private String ceoRegisterNo2;

        @ApiModelProperty("대표자주민등록번호3")
        private String ceoRegisterNo3;

    }

    @NoArgsConstructor
    public static class IssuanceRes {

    }


}
