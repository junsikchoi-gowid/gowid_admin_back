package com.nomadconnection.dapp.core.domain.repository.querydsl;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

public interface ResAccountCustomRepository {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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

        @ApiModelProperty(value = "계좌종류", example = "예적금")
        private String type;

        @ApiModelProperty(value = "은행종류", example = "신한은행")
        private String organization;

        @ApiModelProperty(value = "계좌번호", example = "123333-42-123122")
        private String resAccount;

        @ApiModelProperty(value = "현재잔액", example = "123333-42-123122")
        private Double resAccountBalance;

        @ApiModelProperty(value = "마이너스 통장 여부", example = "1")
        private String resOverdraftAcctYN;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CorpAccountHistoryDto{
        @ApiModelProperty(value = "거래일자 거래시각", example = "")
        private String resAccountTrDateTime;

        @ApiModelProperty(value = "입금", example = "2000000")
        private String resAccountOut;

        @ApiModelProperty(value = "출금", example = "2000000")
        private String resAccountIn;

        @ApiModelProperty(value = "적요1", example = "")
        private String resAccountDesc1;

        @ApiModelProperty(value = "적요2", example = "")
        private String resAccountDesc2;

        @ApiModelProperty(value = "적요3", example = "")
        private String resAccountDesc3;

        @ApiModelProperty(value = "적요4", example = "")
        private String resAccountDesc4;

        @ApiModelProperty(value = "계정과목", example = "")
        private String tagCode;

        @ApiModelProperty(value = "계정과목코드", example = "")
        private String tagValue;

        @ApiModelProperty(value = "메모", example = "")
        private String memo;
    }

    List<CorpAccountDto> FlowAccountList(Long idxCorp, Boolean favorite);
}
