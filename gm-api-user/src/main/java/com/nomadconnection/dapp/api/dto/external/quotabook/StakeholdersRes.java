package com.nomadconnection.dapp.api.dto.external.quotabook;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StakeholdersRes {

    @ApiModelProperty("총 주식 수")
    @JsonProperty("num_shares_total")
    private Long numSharesTotal;

    @ApiModelProperty("주식 보유 목록")
    private List<Stakeholder> stakeholders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stakeholder {
        @ApiModelProperty("주주명")
        @JsonProperty("stakeholder_name")
        private String stakeholderName;

        @ApiModelProperty("소유권")
        private String ownership;

        @ApiModelProperty("보유 주식 수")
        @JsonProperty("num_shares")
        private Long numShares;
    }

    public void tailorStakeholders() {
        int TOP_N = 5;
        if(stakeholders.size() <= TOP_N) {
            return;
        }

        List<Stakeholder> top5 = new ArrayList<>(stakeholders.subList(0, TOP_N - 1));
        long etcNumShares = 0L;
        float etcOwnerShipRatio = 0;

        for(Stakeholder holder: stakeholders.subList(TOP_N, stakeholders.size()-1)) {
            etcNumShares += holder.numShares;
            etcOwnerShipRatio += Float.valueOf(holder.ownership);
        }

        Stakeholder etcStakeholder = new Stakeholder();
        etcStakeholder.setStakeholderName("기타주주");
        etcStakeholder.setNumShares(etcNumShares);
        etcStakeholder.setOwnership(Float.toString(etcOwnerShipRatio));

        top5.add(etcStakeholder);

        stakeholders = top5;
    }
}
