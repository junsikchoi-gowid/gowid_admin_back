package com.nomadconnection.dapp.api.v2.exception.error;

public enum ImageConvertErrorMessage implements ErrorMessage {

	//TODO : error code 명세
	IMAGE_CONVERT_INTERNAL_ERROR("500", "An error occurred while creating the image.");

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
