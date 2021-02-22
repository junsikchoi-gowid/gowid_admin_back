package com.nomadconnection.dapp.core.domain.saas;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.user.User;
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
public class SaasPaymentHistory extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false, columnDefinition = "VARCHAR(10) COMMENT '구매 일자'")
    private String paymentDate;

    @Column(nullable = false)
    private Integer paymentMethod;

    @Column(nullable = false)
    private Integer foreignType;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, columnDefinition = "BIGINT(20) COMMENT '구매 금액'")
    private Long paymentPrice;

    @Column(columnDefinition = "VARCHAR(30) COMMENT '결제 계좌 번호'")
    private String accountNumber;

    @Column(columnDefinition = "VARCHAR(30) COMMENT '결제 카드 번호'")
    private String cardNumber;

    @Column(columnDefinition = "VARCHAR(30) COMMENT '공급자 번호'")
    private String supplierRegNumber;

    @Column(nullable = false, columnDefinition = "VARCHAR(6) COMMENT '기관 코드'")
    private String organization;

    @Column(columnDefinition = "BIGINT(20) COMMENT '계좌 승인 정보 idx'")
    private Long idxAccountHist;

    @Column(columnDefinition = "BIGINT(20) COMMENT '세금 계산서 정보 idx'")
    private Long idxTaxInvoice;

    @Column(columnDefinition = "BIGINT(20) COMMENT '카드 승인 정보 idx'")
    private Long idxCardApprovalHist;

    @Lob
    @Column(columnDefinition = "LONGTEXT COMMENT '품목명'")
    private String item;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_SaasPaymentHistory"))
    private User user;

    @ManyToOne(targetEntity = SaasInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxSaasInfo", foreignKey = @ForeignKey(name = "FK_SaasInfo_SaasPaymentHistory"))
    private SaasInfo saasInfo;

}
