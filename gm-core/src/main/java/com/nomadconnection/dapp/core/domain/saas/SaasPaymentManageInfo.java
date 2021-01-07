package com.nomadconnection.dapp.core.domain.saas;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SaasPaymentManageInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false, columnDefinition = "VARCHAR(30) COMMENT '담당자 이름'")
    private String managerName;

    @Column(nullable = false, columnDefinition = "VARCHAR(30) COMMENT '담당자 이메일'")
    private String managerEmail;

    @Column(nullable = false, columnDefinition = "BIT COMMENT '결제 알림 여부'")
    private Boolean activeAlert;

    @OneToMany(mappedBy = "saasPaymentManageInfo")
    private List<SaasPaymentInfo> saasPaymentInfos;


}
