package com.nomadconnection.dapp.core.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

@Getter
@NoArgsConstructor
public class ImageConvertDto {

	@Builder
	public ImageConvertDto(Integer mrdType, String fileName, JSONObject data) {
		this.mrdType = mrdType;
		this.fileName = fileName;
		this.data = data;
	}

	@ApiModelProperty("솔루션 code(default: 500)")
	private String opCode = "500";

	@ApiModelProperty("1510:사업자등록증, 1520:재무제표, 1530:법인등기부등본")
	private Integer mrdType;

	@ApiModelProperty("파일명")
	private String fileName;

	@ApiModelProperty("이미지 변환 대상 JSON 데이터")
	private JSONObject data;

	@ApiModelProperty("확장자")
	private String exportType = "tif";

	@ApiModelProperty("프로토콜")
	private String protocol = "async";

}
