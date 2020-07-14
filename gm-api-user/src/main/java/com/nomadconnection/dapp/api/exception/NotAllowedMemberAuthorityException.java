package com.nomadconnection.dapp.api.exception;

import com.nomadconnection.dapp.core.domain.user.MemberAuthority;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class NotAllowedMemberAuthorityException extends RuntimeException {

	private final String account;
	private final MemberAuthority authority;
}
