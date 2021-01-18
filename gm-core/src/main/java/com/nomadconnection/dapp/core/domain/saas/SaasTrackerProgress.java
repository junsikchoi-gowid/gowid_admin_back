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
public class SaasTrackerProgress extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Integer step;

    @Column
    private String processDate;

    @Column
    private Long idxCurrentCardApprovalHist;

    @Column
    private Long idxCurrentAccountHist;

    @Column
    private String cardStatus;

    @Lob
    @Column(columnDefinition = "LONGTEXT COMMENT '카드 추출 에러 메세지'")
    private String cardErrMsg;

    @Column
    private String accountStatus;

    @Lob
    @Column(columnDefinition = "LONGTEXT COMMENT '계좌 추출 에러 메세지'")
    private String accountErrMsg;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", nullable = false, foreignKey = @ForeignKey(name = "FK_SaasTrackerProgress_User"))
    private User user;
}
