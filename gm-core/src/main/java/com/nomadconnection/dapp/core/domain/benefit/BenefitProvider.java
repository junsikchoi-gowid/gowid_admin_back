package com.nomadconnection.dapp.core.domain.benefit;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BenefitProvider extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @ManyToOne(targetEntity = Benefit.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxBenefit", foreignKey = @ForeignKey(name = "FK_Benefit_BenefitProvider"))
    private Benefit benefit;

    private String name;

    private String email;

    private String sendOrderEmail;

    private String tel;

    private String channel;

    private String applyLabel;

    private String applyUrl;
}
