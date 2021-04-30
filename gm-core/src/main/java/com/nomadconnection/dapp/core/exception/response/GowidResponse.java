package com.nomadconnection.dapp.core.exception.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.exception.BaseException;
import com.nomadconnection.dapp.core.exception.result.ResultType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class GowidResponse<T> implements Serializable {

    private ApiResult result;

    private T data;

    public GowidResponse(ApiResult result) {
        this.result = result;
    }

    public static GowidResponse ok() {
        return new GowidResponse(ApiResult.getSuccess());
    }

    public static <T> GowidResponse ok(T data) {
        return new GowidResponse(ApiResult.getSuccess(), data);
    }

    public GowidResponse(BaseException ex) {
        this.result = new ApiResult(ex);
    }


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class ApiResult implements Serializable {
        @ApiModelProperty(value = "코드", example = "6000")
        public String code ;

        @ApiModelProperty(value = "설명", example = "system error")
        public String desc ;

        @ApiModelProperty(value = "추가 메세지", example = "Text '1' could not be parsed at index 0")
        public String extraMessage ;

        public ApiResult(ResultType resultType) {
            this.code = resultType.getCode();
            this.desc = resultType.getDesc();
        }

        public ApiResult(ResultType resultType, String extraMsg) {
            this.code = resultType.getCode();
            this.desc = resultType.getDesc();
            this.extraMessage = extraMsg;
        }

        public static ApiResult getSuccess() {

            return new ApiResult(ResultType.SUCCESS);
        }

        public ApiResult(BaseException ex) {
            this.code = ex.getCode();
            this.desc = ex.getDesc();
            this.extraMessage = ex.getExtraMessage();
        }

    }


}
