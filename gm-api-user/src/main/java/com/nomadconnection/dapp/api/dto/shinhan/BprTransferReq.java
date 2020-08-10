package com.nomadconnection.dapp.api.dto.shinhan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @interfaceID : 3000
 * @description : BPR데이터 존재여부 확인 response
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BprTransferReq extends DataPart3000 {

    public BprTransferReq(DataPart3000 dataPart3000Req) {
        BeanUtils.copyProperties(dataPart3000Req, this);
    }
}
