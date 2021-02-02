package com.nomadconnection.dapp.core.domain.saas;

import com.nomadconnection.dapp.core.utils.EnumUtils;
import com.nomadconnection.dapp.core.utils.EnumValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Benefit 결제 메일 관련 Enum
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SaasOrganizationType implements EnumValue<Object> {

	// Bank
	CODE_0002("0002", "산업이체"),
	CODE_0003("0003", "기업이체"),
	CODE_0004("0004", "국민이체"),
	CODE_0007("0007", "수협이체"),
	CODE_0011("0011", "농협이체"),
	CODE_0020("0020", "우리이체"),
	CODE_0023("0023", "SC이체"),
	CODE_0027("0027", "씨티이체"),
	CODE_0031("0031", "대구이체"),
	CODE_0032("0032", "부산이체"),
	CODE_0034("0034", "광주이체"),
	CODE_0035("0035", "제주이체"),
	CODE_0037("0037", "전북이체"),
	CODE_0039("0039", "경남이체"),
	CODE_0045("0045", "새마을금고"),
	CODE_0048("0048", "신협이체"),
	CODE_0071("0071", "우체국"),
	CODE_0081("0081", "KEB하나이체"),
	CODE_0088("0088", "신한이체"),
	CODE_0089("0089", "K뱅크"),

	// Card
	CODE_0301("0301", "KB카드"),
	CODE_0302("0302", "현대카드"),
	CODE_0303("0303", "삼성카드"),
	CODE_0304("0304", "NH카드"),
	CODE_0305("0305", "BC카드"),
	CODE_0306("0306", "신한카드"),
	CODE_0307("0307", "씨티카드"),
	CODE_0309("0309", "우리카드"),
	CODE_0311("0311", "롯데카드"),
	CODE_0313("0313", "하나카드"),
	CODE_0315("0315", "전북카드"),
	CODE_0316("0316", "광주카드"),
	CODE_0320("0320", "수협카드"),
	CODE_0321("0321", "제주카드"),
	;

	private String value;
	private String orgName;

	private static final Map<Object, SaasOrganizationType> map = EnumUtils.getMap(SaasOrganizationType.class);
	public static final SaasOrganizationType getType(Object value) {
		return map.get(value);
	}
}