package com.nomadconnection.dapp.api.v2.dto.cardissuanceinfo;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceDepth;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IssuanceDepthResponseDto {

	private Long cardIssuanceInfoIdx;

	private IssuanceDepth depthKey;

	private CardType cardType;

	public static IssuanceDepthResponseDto from(CardIssuanceInfo cardIssuanceInfo){
		return IssuanceDepthResponseDto
			.builder()
			.cardIssuanceInfoIdx(cardIssuanceInfo.idx())
			.depthKey(cardIssuanceInfo.issuanceDepth())
			.cardType(cardIssuanceInfo.cardType())
			.build();
	}

}
