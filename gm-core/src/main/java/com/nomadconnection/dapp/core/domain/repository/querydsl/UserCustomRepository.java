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
import java.util.List;

public interface UserCustomRepository {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SearchUserListDto {
        @ApiModelProperty(value = "담당자", example = "류제용")
        private String userName;

        @ApiModelProperty(value = "이메일주소", example = "angryong@gowid.com")
        private String email;

        @ApiModelProperty(value = "카드사", example = "SHINHAN")
        public CardCompany cardCompany;

        @ApiModelProperty(value = "법인명", example = "고위드")
        public String resCompanyNm;

        @ApiModelProperty(value = "마지막 신청 단계", example = "SIGN_SIGNATURE")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty(value = "신청 상태", example = "ISSUED")
        public List<IssuanceStatus> issuanceStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UserListDto {
        @ApiModelProperty(value = "idxUser", example = "1")
        public Long idxUser;

        @ApiModelProperty(value = "idxCorp", example = "1")
        public Long idxCorp;

        @ApiModelProperty(value = "idxCardIssuanceInfo", example = "1")
        public Long idxCardIssuanceInfo;

        @ApiModelProperty(value = "담당자", example = "류제용")
        private String userName;

        @ApiModelProperty(value = "이메일주소", example = "angryong@gowid.com")
        private String email;

        @ApiModelProperty(value = "직책", example = "사원")
        public String position;

        @ApiModelProperty(value = "카드사", example = "SHINHAN")
        public CardCompany cardCompany;

        @ApiModelProperty(value = "법인명", example = "고위드")
        public String resCompanyNm;

        @ApiModelProperty(value = "마지막 신청 단계", example = "SIGN_SIGNATURE")
        private IssuanceDepth issuanceDepth;

        @ApiModelProperty(value = "신청 상태", example = "ISSUED")
        public IssuanceStatus issuanceStatus;

        @ApiModelProperty(value = "가입 경로", example = "포잉")
        public String surveyAnswer;

        @ApiModelProperty(value = "회원가입일", example = "9999-99-99 99:99:99")
        private LocalDateTime signUpDate;

        @ApiModelProperty(value = "법인등록일(Gowid)", example = "9999-99-99 99:99:99")
        private LocalDateTime corpRegisterDate;
    }

    Page<UserListDto> userList(SearchUserListDto dto, Pageable pageable);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UserInfoDto {
        @ApiModelProperty(value = "idxUser", example = "1")
        public Long idxUser;

        @ApiModelProperty(value = "이름", example = "류제용")
        private String userName;

        @ApiModelProperty(value = "휴대폰 번호", example = "010-1234-1234")
        private String phone;

        @ApiModelProperty(value = "회사명", example = "고위드")
        private String corpName;

        @ApiModelProperty(value = "직책", example = "사원")
        private String position;

        @ApiModelProperty(value = "sms 수신여부", example = "true")
        private Boolean smsReception;

        @ApiModelProperty(value = "email 수신여부", example = "true")
        private Boolean emailReception;

        @ApiModelProperty(value = "지출관리 상태", example = "SETUP_COMPANY")
        private ExpenseStatus expenseStatus;

        @ApiModelProperty(value = "SaaS 사용여부", example = "0")
        private Integer saasStatus;

        @ApiModelProperty(value = "이메일주소", example = "angryong@gowid.com")
        private String email;

        @ApiModelProperty(value = "설문조사 답변", example = "SNS")
        private String surveyAnswer;

        @ApiModelProperty(value = "답변 상세 내용", example = "페이스북")
        private String surveyAnswerDetail;

        @ApiModelProperty(value = "회원가입일", example = "9999-99-99 99:99:99")
        private LocalDateTime signUpDate;
    }

    UserInfoDto userInfo(Long idxUser);
}
