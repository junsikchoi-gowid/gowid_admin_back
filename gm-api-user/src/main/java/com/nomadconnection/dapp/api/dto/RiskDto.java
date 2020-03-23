package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Risk;
import com.nomadconnection.dapp.core.domain.RiskConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskDto {
    @ApiModelProperty("이용약관(식별자)")
    public Long idx;
    public Long idxUser;
    private String date;
    private boolean ceoGuarantee;
    private float depositGuarantee;
    private boolean depositPayment;
    private boolean cardIssuance;
    private boolean ventureCertification;
    private boolean vcInvestment;
    private String grade;
    private Integer gradeLimitPercentage;
    private float minStartCash;
    private float minCashNeed;
    private float currentBalance;
    private Integer error;
    private float dma45;
    private float dmm45;
    private Float actualBalance;
    private float cashBalance;
    private Boolean cardAvailable;
    private float cardLimitCalculation;
    private float realtimeLimit;
    private float cardLimit;
    private float cardLimitNow;
    private boolean emergencyStop;
    private Integer cardRestartCount;
    private boolean cardRestart;

    public static RiskDto from(Risk risk){
        RiskDto riskDto = RiskDto.builder()
                .idxUser(risk.idxUser())
                .date(risk.date())
                .ceoGuarantee(risk.ceoGuarantee())
                .depositGuarantee(risk.depositGuarantee())
                .depositPayment(risk.depositPayment())
                .cardIssuance(risk.cardIssuance())
                .ventureCertification(risk.ventureCertification())
                .vcInvestment(risk.vcInvestment())
                .grade(risk.grade())
                .gradeLimitPercentage(risk.gradeLimitPercentage())
                .minStartCash(risk.minStartCash())
                .minCashNeed(risk.minCashNeed())
                .currentBalance(risk.currentBalance())
                .error(risk.error())
                .dma45(risk.dma45())
                .dmm45(risk.dmm45())
                .actualBalance(risk.actualBalance())
                .cashBalance(risk.cashBalance())
                .cardAvailable(risk.cardAvailable())
                .cardLimitCalculation(risk.cardLimitCalculation())
                .realtimeLimit(risk.realtimeLimit())
                .cardLimit(risk.cardLimit())
                .cardLimitNow(risk.cardLimitNow())
                .emergencyStop(risk.emergencyStop())
                .cardRestartCount(risk.cardRestartCount())
                .cardRestart(risk.cardRestart())
                .build();
        return riskDto;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskConfigDto {
        @ApiModelProperty("이용약관(식별자)")
        public Long idx;
        public Long idxUser;
        private String date;
        private boolean ceoGuarantee;
        private float depositGuarantee;
        private boolean depositPayment;
        private boolean cardIssuance;
        private boolean ventureCertification;
        private boolean vcInvestment;
    }
}
