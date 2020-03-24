package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.IrDashBoard;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IrDashBoardDto {
    @ApiModelProperty("식별자")
    public Long idx;

    @ApiModelProperty("type")
    public String irType;

    @ApiModelProperty("제목")
    public String title;

    @ApiModelProperty("내용")
    public String contents;

    public static IrDashBoardDto from(IrDashBoard irDashBoard) {
        IrDashBoardDto irDashboardDto = IrDashBoardDto.builder()
                .idx(irDashBoard.idx())
                .irType(irDashBoard.irType())
                .title(irDashBoard.title())
                .contents(irDashBoard.contents())
                .build();
        return irDashboardDto;
    }
}
