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
public class SaasIssueReport extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_SaasIssueReport"))
    private User user;

    @Column(nullable = false, columnDefinition = "TINYINT(1) COMMENT '제보 타입'")
    private Integer reportType;

    @Column(columnDefinition = "VARCHAR(100) COMMENT 'SaaS 이름'")
    private String saasName;

    @Column(columnDefinition = "TINYINT(1) COMMENT '결제 수단'")
    private Integer paymentMethod;

    @Column(columnDefinition = "BIGINT(20) COMMENT '결제 금액'")
    private Long paymentPrice;

    @Column(columnDefinition = "VARCHAR(10) COMMENT '무료 사용 만료일'")
    private String experationDate;

    @Column(columnDefinition = "TINYINT(1) COMMENT '무료 사용 만료 알림 여부'")
    private Boolean activeExperationAlert;

    @Lob
    @Column(columnDefinition = "LONGTEXT COMMENT '제보 내용'")
    private String issue;

}
