package com.nomadconnection.dapp.api.dto.external;

import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.core.domain.external.ExternalCompanyType;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalLinkReq {

    @ApiModelProperty("외부 연계 업체 코드")
    private ExternalCompanyType externalCompanyType;

    @ApiModelProperty("외부 연계 키")
    private String externalKey;

    public void validation() {
        if (externalCompanyType == null || ExternalCompanyType.getType(externalCompanyType) == null) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "externalCompanyType");
        }
        if (StringUtils.isEmpty(externalKey)) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "externalKey");
        }
    }

}
