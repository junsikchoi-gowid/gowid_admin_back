package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import com.nomadconnection.dapp.api.dto.shinhan.gateway.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 3000
 * @description : BPR데이터 존재여부 확인 response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BprTransferRes {

    private ApiResponse.ApiResult result;

}
