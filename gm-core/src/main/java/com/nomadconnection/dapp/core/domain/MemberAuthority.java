package com.nomadconnection.dapp.core.domain;

import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public enum MemberAuthority {
	MASTER, // 마스터
	ADMIN, // 어드민
	REGULAR, // 일반사용자
	;
	public static MemberAuthority from(Set<Authority> authorities) {
		if (authorities != null) {
			if (authorities.stream().map(Authority::role).anyMatch(Role::isMaster)) {
				return MASTER;
			}
			if (authorities.stream().map(Authority::role).anyMatch(Role::isAdmin)) {
				return ADMIN;
			}
		}
		return REGULAR;
	}
}
