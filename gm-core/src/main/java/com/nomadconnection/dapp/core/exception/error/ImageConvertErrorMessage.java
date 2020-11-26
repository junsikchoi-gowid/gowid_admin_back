package com.nomadconnection.dapp.core.exception.error;

public enum ImageConvertErrorMessage implements ErrorMessage {

	//TODO : error code 명세
	INTERNAL_ERROR("INTERNAL_ERROR", "An error occurred while creating the image."),
	CONNECTION_FAILED("CONNECTION_FAILED", "Unable to connect to image server.");

	private String code;
	private String desc;

	ImageConvertErrorMessage(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDesc() {
		return desc;
	}
}
