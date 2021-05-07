package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.res.ResAccountStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

public interface ResAccountCustomRepository {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude
    class CorpAccountDto{
        @ApiModelProperty(value = "idxAccount", example = "1")
        private Long idxResAccount;

        @ApiModelProperty(value = "즐겨찾기여부", example = "true")
        private Boolean favorite;

        @ApiModelProperty(value = "별칭", example = "마이너스 통장")
        private String nickName;

        @ApiModelProperty(value = "별칭", example = "마이너스 통장")
        private String resAccountNickName;

        @ApiModelProperty(value = "별칭", example = "마이너스 통장")
        private String resAccountName;

        @ApiModelProperty(value = "통화", example = "KRW")
        private String currency;

        @ApiModelProperty(value = "은행종류", example = "신한은행")
        private String organization;

        @ApiModelProperty(value = "계좌번호", example = "123333-42-123122")
        private String resAccount;

        @ApiModelProperty(value = "현재잔액", example = "123333-42-123122")
        private Double resAccountBalance;

        @ApiModelProperty(value = "마이너스 통장 여부", example = "1")
        private String resOverdraftAcctYN;

        @ApiModelProperty(value = "계좌종류", example = "예적금 / 입출금 / 외화 / 펀드")
        private String type;

        @ApiModelProperty(value = "계좌번호 Display", example = "123333-42-123122")
        private String resAccountDisplay;

        @ApiModelProperty(value = "상태", example = "Normal")
        private ResAccountStatus status;

        @ApiModelProperty(value = "상태 내역", example = "연동이 중단된 계좌입니다.")
        private String statusDesc;

    }

    List<CorpAccountDto> FlowAccountList(Long idxCorp, Boolean favorite);
}
