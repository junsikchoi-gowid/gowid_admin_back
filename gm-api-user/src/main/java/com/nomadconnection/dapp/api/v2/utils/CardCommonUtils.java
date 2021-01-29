package com.nomadconnection.dapp.api.v2.utils;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CeoInfo;
import org.springframework.util.ObjectUtils;

public class CardCommonUtils {

	public static boolean isRealOwnerConvertCeo(CardIssuanceInfo cardInfo, CeoInfo ceoInfo) {
		return isStockholderUpdateCeo(cardInfo) && !ObjectUtils.isEmpty(ceoInfo);
	}

	public static boolean isStockholderUpdateCeo(CardIssuanceInfo cardInfo) {
		return !ObjectUtils.isEmpty(cardInfo.stockholder())
			&& Boolean.FALSE.equals(cardInfo.stockholder().isStockHold25())
			&& Boolean.FALSE.equals(cardInfo.stockholder().isStockholderPersonal());
	}
}
