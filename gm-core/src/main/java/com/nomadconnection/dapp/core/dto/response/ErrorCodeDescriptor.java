package com.nomadconnection.dapp.core.dto.response;

@SuppressWarnings("unused")
public interface ErrorCodeDescriptor {

	default String category() {
		return getClass().getSimpleName();
	}

	default String error() {
		if (getClass().isEnum()) {
			return ((Enum)this).name();
		}
		return toString();
	}

	default String description() {
		return error();
	}
}
