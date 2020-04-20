package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Risk;
import com.nomadconnection.dapp.core.domain.RiskConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskDto {
    @ApiModelProperty("이용약관(식별자)")
    public Long idx;

    public String date;
    public boolean ceoGuarantee;
    public double depositGuarantee;
    public boolean depositPayment;
    public boolean cardIssuance;
    public boolean ventureCertification;
    public boolean vcInvestment;
    public String grade;
    public Integer gradeLimitPercentage;
    public double minStartCash;
    public double minCashNeed;
    public double currentBalance;
    public Integer error;
    public double dma45;
    public double dmm45;
    public double actualBalance;
    public double cashBalance;
    public Boolean cardAvailable;
    public double cardLimitCalculation;
    public double realtimeLimit;
    public double cardLimit;
    public double cardLimitNow;
    public boolean emergencyStop;
    public Integer cardRestartCount;
    public boolean cardRestart;
    public LocalDateTime updatedAt;
    public boolean pause;
    public double recentBalance;

    public Long idxUser;

    public static RiskDto from(Risk risk){
        RiskDto riskDto = RiskDto.builder()
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
                .pause(risk.pause())
                .updatedAt(risk.getUpdatedAt())
                .recentBalance(risk.recentBalance())
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
        public Long idxCorp;
        public String date;
        public boolean ceoGuarantee;
        public double depositGuarantee;
        public boolean depositPayment;
        public boolean cardIssuance;
        public boolean ventureCertification;
        public boolean vcInvestment;
        public boolean enabled;
    }
}
