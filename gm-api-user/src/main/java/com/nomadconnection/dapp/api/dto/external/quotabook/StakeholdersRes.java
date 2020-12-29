package com.nomadconnection.dapp.api.dto.external.quotabook;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StakeholdersRes{

    @ApiModelProperty("총 주식 수")
    @JsonProperty("num_shares_total")
    private Long numSharesTotal;

    @ApiModelProperty("주식 보유 목록")
    private List<Stakeholder> stakeholders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stakeholder implements Comparable<Stakeholder>{
        @ApiModelProperty("주주명")
        @JsonProperty("stakeholder_name")
        private String stakeholderName;

        @ApiModelProperty("소유권")
        private String ownership;

        @ApiModelProperty("보유 주식 수")
        @JsonProperty("num_shares")
        private Long numShares;

        @Override
        public int compareTo(Stakeholder obj) {
            if (numShares == obj.numShares) {
                return 0;
            } else if(numShares < obj.numShares) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public void tailorStakeholders() {
        int TOP_N = 5;
        Collections.sort(stakeholders);
        if(stakeholders.size() <= TOP_N) {
            return;
        }

        List<Stakeholder> top5 = new ArrayList<>(stakeholders.subList(0, TOP_N));
        long etcNumShares = 0L;
        float etcOwnerShipRatio = 0;

        for(Stakeholder holder: stakeholders.subList(TOP_N, stakeholders.size())) {
            etcNumShares += holder.numShares;
            etcOwnerShipRatio += Float.valueOf(holder.ownership);
        }

        Stakeholder etcStakeholder = new Stakeholder();
        etcStakeholder.setStakeholderName("기타");
        etcStakeholder.setNumShares(etcNumShares);
        etcStakeholder.setOwnership(Float.toString(etcOwnerShipRatio));

        top5.add(etcStakeholder);

        stakeholders = top5;
    }
}
