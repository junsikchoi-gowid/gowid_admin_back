package com.nomadconnection.dapp.core.domain.repository.querydsl;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ResBatchListCustomRepository {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ErrorSearchDto {
        @ApiModelProperty("법인 idx")
        public Long idxCorp;

        @ApiModelProperty("법인명 ")
        private String corpName;

        @ApiModelProperty("transactionId")
        private String transactionId;

        @ApiModelProperty("에러코드 true/false")
        private String errorCode;

        @ApiModelProperty("금일여부 true/false")
        private String boolToday;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ErrorResultDto {
        @ApiModelProperty("법인 idx")
        public Long idxCorp;

        @ApiModelProperty("발생시간")
        private LocalDateTime updatedAt;

        @ApiModelProperty("법인명")
        private String corpName;

        @ApiModelProperty("은행")
        private String bankName;

        @ApiModelProperty("계좌번호")
        private String account;

        @ApiModelProperty("에러메세지")
        private String errMessage;

        @ApiModelProperty("에러코드")
        private String errCode;

        @ApiModelProperty("transactionId")
        private String transactionId;
    }

    Page<ErrorResultDto> errorList(ErrorSearchDto dto, Pageable pageable);
}
