package com.nomadconnection.dapp.core.dto.limit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.limit.ContactType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LimitRecalculationRequestDto {

	private Long currentUsedAmount;
	private ContactType contactType;
	private String accountInfo;
	private String contents;

	private String companyName;
	private Long cardLimit;
	private Long hopeLimit;
}
