package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.CommonCodeDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Venture {

    private Boolean isVerifiedVenture; // 벤처기업확인서 보유 여부
    private Boolean isVC; // 10억이상 VC투자 여부
    private String investAmount; // 투자금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCommonCodeDetail", foreignKey = @ForeignKey(name = "fk__cardIssuanceInfo_commonCodeDetail"))
    private CommonCodeDetail commonCodeDetail;
}
