package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.FinancialConsumersResponseDto;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialConsumersService {

    private final CardIssuanceInfoService cardIssuanceInfoService;

    @Transactional(rollbackFor = Exception.class)
    public FinancialConsumersResponseDto updateOverFiveEmployees(User user, CardType cardType, boolean overFiveEmployees){
        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findByUserAndCardType(user, cardType);
        cardIssuanceInfo.getFinancialConsumers().updateOverFiveEmployees(overFiveEmployees);

        return FinancialConsumersResponseDto.from(cardIssuanceInfo);
    }

}
