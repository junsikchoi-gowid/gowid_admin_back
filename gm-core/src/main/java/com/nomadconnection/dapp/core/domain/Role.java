package com.nomadconnection.dapp.core.domain;

@SuppressWarnings("unused")
public enum Role {

	ROLE_MASTER,
	ROLE_ADMIN,
	ROLE_MEMBER,
	;

	public static boolean isMaster(Role role) {
		return ROLE_MASTER.equals(role);
	}

	public static boolean isAdmin(Role role) {
		return ROLE_ADMIN.equals(role);
	}

	public static boolean isUpdatableCreditLimit(Role role) {
		return ROLE_MASTER.equals(role) || ROLE_ADMIN.equals(role);
	}

	public static boolean isUpdatableDeptName(Role role) {
		return ROLE_MASTER.equals(role) || ROLE_ADMIN.equals(role);
	}
}
