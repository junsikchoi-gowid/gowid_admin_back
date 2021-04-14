package com.nomadconnection.dapp.core.domain.user;

import java.util.Set;

public enum MemberAuthority {
	MASTER, // 마스터
	ADMIN, // 어드민
	REGULAR, // 일반사용자
	GOWID_ADMIN, // 운영 마스터
	GOWID_USER, // 운영 USER
	GOWID_EXTERNAL, // 운영 EXTERNAL
	GOWID_BENEFIT, // 운영 BENEFIT
	;
	public static MemberAuthority from(Set<Authority> authorities) {
		if (authorities != null) {
			if (authorities.stream().map(Authority::role).anyMatch(Role::isMaster)) {
				return MASTER;
			}
			if (authorities.stream().map(Authority::role).anyMatch(Role::isViewer)) {
				return ADMIN;
			}
			if (authorities.stream().map(Authority::role).anyMatch(Role::isGowidAdmin)) {
				return GOWID_ADMIN;
			}
			if (authorities.stream().map(Authority::role).anyMatch(Role::isGowidUser)) {
				return GOWID_USER;
			}
		}
		return REGULAR;
	}
}