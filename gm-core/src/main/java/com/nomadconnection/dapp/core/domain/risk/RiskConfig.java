package com.nomadconnection.dapp.core.domain.risk;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class RiskConfig extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_RiskConfig_Corp"))
    private Corp corp; // 소속법인

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_RiskConfig_User"))
    private User user; // 유저정보

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '대표이사 연대보증 여부'")
    private boolean ceoGuarantee;

    @Column(columnDefinition = "double DEFAULT NULL COMMENT '요구보증금'")
    private double depositGuarantee;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '보증금 납입 여부'")
    private boolean depositPayment;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '카드발급여부'")
    private boolean cardIssuance;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '벤처인증여부 (벤처기업확인서 보유여부)'")
    private boolean ventureCertification;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '10억원 이상의 vc투자여부'")
    private boolean vcInvestment;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '사용유무'")
    private boolean enabled;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '25%이상의 지분을 보유한 개인여부'")
    private boolean isStockHold25;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '1대주주 개인여부'")
    private boolean isStockholderPersonal;

    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '1대주주 법인의 주주명부 보유여부'")
    private boolean isStockholderList;

    @Column(columnDefinition = "varchar(255) NOT NULL COMMENT '희망한도'")
	private String hopeLimit;

    @Column(columnDefinition = "varchar(255) NOT NULL COMMENT '계산한도'")
	private String calculatedLimit;

    @Column(columnDefinition = "varchar(255) NOT NULL COMMENT '부여한도'")
	private String grantLimit;
}
