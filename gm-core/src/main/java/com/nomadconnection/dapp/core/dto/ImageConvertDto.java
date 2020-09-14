package com.nomadconnection.dapp.core.dto;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ImageConvertDto {

	@Builder
    public ImageConvertDto(Integer mrdType, String fileName, String data, CardCompany cardCompany) {
        this.mrdType = mrdType;
        this.fileName = fileName;
        this.data = data;
        this.cardCompany = cardCompany;
    }

	@Setter
	@ApiModelProperty("1510:사업자등록증, 1520:재무제표, 1530:법인등기부등본, 9991: 지급보증")
	private Integer mrdType;

	@ApiModelProperty("파일명")
	private String fileName;

    @ApiModelProperty("이미지 변환 대상 JSON 데이터")
    private String data;

	@ApiModelProperty("카드회사")
	private CardCompany cardCompany;

	@Setter
	private String exportType = "tif";

	private String protocol = "sync";

	private String opCode = "500";

}
