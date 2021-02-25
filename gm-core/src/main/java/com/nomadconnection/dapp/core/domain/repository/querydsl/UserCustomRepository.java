package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceDepth;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.embed.ExpenseStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface UserCustomRepository {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UserListDto {
        @ApiModelProperty("idxUser")
        public Long idxUser;

        @ApiModelProperty("idxCorp")
        public Long idxCorp;

        @ApiModelProperty("idxCardIssuanceInfo")
        public Long idxCardIssuanceInfo;

        @ApiModelProperty("담당자")
        private String userName;

        @ApiModelProperty("이메일주소")
        private String email;

        @ApiModelProperty("직책")
        public String position;

        @ApiModelProperty("카드사")
        public CardCompany cardCompany;

        @ApiModelProperty("법인명")
        public String resCompanyNm;

        @ApiModelProperty("마지막 신청 단계")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty("신청 상태")
        public IssuanceStatus issuanceStatus;

        @ApiModelProperty("가입 경로")
        public String surveyAnswer;

        @ApiModelProperty("회원가입일")
        private LocalDateTime signUpDate;

        @ApiModelProperty("인증서등록일")
        private LocalDateTime certRegisterDate;
    }

    Page<UserListDto> userList(String keyWord, Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UserInfoDto {
        @ApiModelProperty("idxUser")
        public Long idxUser;

        @ApiModelProperty("이름")
        private String userName;

        @ApiModelProperty("휴대폰 번호")
        private String phone;

        @ApiModelProperty("회사명")
        private String corpName;

        @ApiModelProperty("직책")
        private String position;

        @ApiModelProperty("sms 수신여부")
        private Boolean smsReception;

        @ApiModelProperty("email 수신여부")
        private Boolean emailReception;

        @ApiModelProperty("지출관리 상태")
        private ExpenseStatus expenseStatus;

        @ApiModelProperty("SaaS 사용여부")
        private Boolean saasUsage;

        @ApiModelProperty("이메일주소")
        private String email;

        @ApiModelProperty("설문조사 답변")
        private String surveyAnswer;

        @ApiModelProperty("답변 상세 내용")
        private String surveyAnswerDetail;

        @ApiModelProperty("회원가입일")
        private LocalDateTime signUpDate;
    }

    UserInfoDto userInfo(Long idxUser);
}
