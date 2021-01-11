package com.nomadconnection.dapp.core.domain.user;

@SuppressWarnings("unused")
public enum Role {

	ROLE_MASTER,
	ROLE_ADMIN,
	ROLE_MEMBER,
	GOWID_ADMIN,
	GOWID_USER,
	GOWID_EXTERNAL,
	GOWID_BENEFIT,
	GOWID_SAASTRACKER;

	public static boolean isMaster(Role role) {
		return ROLE_MASTER.equals(role);
	}

	public static boolean isAdmin(Role role) {
		return ROLE_ADMIN.equals(role);
	}

	public static boolean isGowidAdmin(Role role) {
		return GOWID_ADMIN.equals(role);
	}

	public static boolean isGowidUser(Role role) {
		return GOWID_USER.equals(role);
	}

	public static boolean isUpdatableCreditLimit(Role role) {
		return ROLE_MASTER.equals(role) || ROLE_ADMIN.equals(role);
	}

	public static boolean isUpdatableDeptName(Role role) {
		return ROLE_MASTER.equals(role) || ROLE_ADMIN.equals(role);
	}
}
