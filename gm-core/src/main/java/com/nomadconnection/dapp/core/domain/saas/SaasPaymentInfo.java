package com.nomadconnection.dapp.core.domain.saas;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.user.User;
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
public class SaasPaymentInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(columnDefinition = "VARCHAR(10) COMMENT '최근 결제 일'")
    private String currentPaymentDate;

    @Column(columnDefinition = "BIGINT(20) COMMENT '최근 결제 금액'")
    private Long currentPaymentPrice;

    @Column(nullable = false)
    private Integer paymentMethod;

    @Column(nullable = false, columnDefinition = "VARCHAR(6) COMMENT '기관 코드'")
    private String organization;

    @Column(columnDefinition = "VARCHAR(30) COMMENT '결제 계좌 번호'")
    private String accountNumber;

    @Column(columnDefinition = "VARCHAR(30) COMMENT '결제 카드 번호'")
    private String cardNumber;

    @Column(nullable = false)
    private Integer paymentType;

    @Column(columnDefinition = "VARCHAR(10) COMMENT '다음 결제 예정일'")
    private String paymentScheduleDate;

    @Column(nullable = false, columnDefinition = "BIT COMMENT '구독 여부'")
    private Boolean activeSubscription;

    @Column(columnDefinition = "BIT COMMENT '새로 구독한 SaaS'")
    private Boolean isNew;

    @Column(nullable = false, columnDefinition = "BIT COMMENT '중복 결제 여부'")
    private Boolean isDup;

    @Column(columnDefinition = "VARCHAR(8) COMMENT '이용기한'")
    private String expirationDate;

    @Lob
    private String memo;

    @Column(nullable = false, columnDefinition = "BIT COMMENT '결제수단 사용 여부'")
    private Boolean disabled;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_SaaSPaymentInfo"))
    private User user;

    @ManyToOne(targetEntity = SaasInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxSaasInfo", foreignKey = @ForeignKey(name = "FK_SaasInfo_SaasPaymentInfo"))
    private SaasInfo saasInfo;

    @ManyToOne(targetEntity = SaasPaymentManageInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxSaasPaymentManageInfo", foreignKey = @ForeignKey(name = "FK_SaasPaymentManageInfo_SaasPaymentInfo"))
    private SaasPaymentManageInfo saasPaymentManageInfo;

    @OneToMany(mappedBy = "saasPaymentInfo")
    private List<SaasCheckInfo> saasCheckInfos;
}
