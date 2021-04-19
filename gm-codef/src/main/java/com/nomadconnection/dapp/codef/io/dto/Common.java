package com.nomadconnection.dapp.codef.io.dto;

import com.nomadconnection.dapp.codef.io.utils.MaskingUtils;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class Common {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Account {

        @ApiModelProperty("패스워드")
        private String password1;

        @ApiModelProperty("인증서이름")
        private String name;

        @ApiModelProperty("발급일")
        private String startDate;

        @ApiModelProperty("만료일")
        private String endDate;

        @ApiModelProperty("설명1")
        private String desc1;

        @ApiModelProperty("설명2")
        private String desc2;

        @ApiModelProperty("인증서발행기관")
        private String issuer;

        @ApiModelProperty("SN 키값")
        private String serial;

        @ApiModelProperty("certFile")
        private String certFile;
    }



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResAccountDto {
        @ApiModelProperty("idx")
        private Long idx;
        private String nickName; // 별칭 시스템용
        private String connectedId; // 커넥트드 아이디
        private String organization; // 기관코드
        private String type; // 종류
        private String resAccount              ; //계좌번호
        private String resAccountDisplay       ; //계좌번호_표시용
        private Double resAccountBalance       ; //현재잔액
        private String resAccountDeposit       ; //예금구분
        private String resAccountNickName      ; //계좌별칭
        private String resAccountCurrency      ; //통화코드
        private String resAccountHolder        ; //예금주
        private String resAccountStartDate     ; //신규일
        private String resAccountEndDate       ; //만기일
        private String resLastTranDate         ; //최종거래일
        private String resAccountName          ; //계좌명(종류)
        private String resOverdraftAcctYN      ; //마이너스 통장 여부
        private String resLoanKind             ; //대출종류
        private String resLoanBalance          ; //대출잔액
        private String resLoanStartDate        ; //대출신규일
        private String resLoanEndDate          ; //대출만기일
        private String resAccountInvestedCost  ; //투자원금
        private String resEarningsRate         ; //수익률[%]
        private String resAccountLoanExecNo    ; //대출실행번호
        private String errCode    ;
        private String errMessage    ;
        private LocalDateTime scrpaingUpdateTime    ;


        public static ResAccountDto from(ResAccount resAccount, Boolean isMasking) {

            String account = resAccount.resAccount();
            String accountDisplay = resAccount.resAccountDisplay();
            if (isMasking != null && isMasking) {
                account = MaskingUtils.maskingBankAccountNumber(resAccount.resAccount());
                accountDisplay = null;
            }

            String nickName = resAccount.nickName();
            if (!StringUtils.isEmpty(resAccount.resAccountName())) {
                nickName = resAccount.resAccountName();
            }
            if (!StringUtils.isEmpty(resAccount.resAccountNickName())) {
                nickName = resAccount.resAccountNickName();
            }
            if (!StringUtils.isEmpty(resAccount.nickName())) {
                nickName = resAccount.nickName();
            }

            return ResAccountDto.builder()
                    .idx(resAccount.idx())
                    .nickName(nickName)
                    .connectedId(resAccount.connectedId())
                    .organization(resAccount.organization())
                    .type(resAccount.type())
                    .resAccount(account)
                    .resAccountHolder(resAccount.resAccountHolder())
                    .resAccountDisplay(accountDisplay)
                    .resAccountBalance(resAccount.resAccountBalance())
                    .resAccountDeposit(resAccount.resAccountDeposit())
                    .resAccountNickName(resAccount.resAccountNickName())
                    .resAccountCurrency(resAccount.resAccountCurrency())
                    .resAccountStartDate(resAccount.resAccountStartDate())
                    .resAccountEndDate(resAccount.resAccountEndDate())
                    .resLastTranDate(resAccount.resLastTranDate())
                    .resAccountName(resAccount.resAccountName())
                    .resOverdraftAcctYN(resAccount.resOverdraftAcctYN())
                    .resLoanKind(resAccount.resLoanKind())
                    .resLoanBalance(resAccount.resLoanBalance())
                    .resLoanStartDate(resAccount.resLoanStartDate())
                    .resLoanEndDate(resAccount.resLoanEndDate())
                    .resAccountInvestedCost(resAccount.resAccountInvestedCost())
                    .resEarningsRate(resAccount.resEarningsRate())
                    .resAccountLoanExecNo(resAccount.resAccountLoanExecNo())
                    .build();
        }
    }
}
