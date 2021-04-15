package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1100;
import com.nomadconnection.dapp.core.domain.shinhan.D1200;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;


@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, of = "idx")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"idxCorp", "cardCompany", "cardType"}, name = "UK_Corp_CardCompany_CardType"),
    @UniqueConstraint(columnNames = {"idxKised"}, name = "UK_Kised")
})
@Entity
public class CardIssuanceInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_cardIssuance"))
    private Corp corp; // 소속법인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_cardIssuance"), nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="idxKised", foreignKey = @ForeignKey(name = "FK_CardIssuanceInfo_Kised"), referencedColumnName = "idx")
    private Kised kised;

    @OneToOne(mappedBy = "cardIssuanceInfo", fetch = FetchType.LAZY)
    private D1000 d1000;
    @OneToOne(mappedBy = "cardIssuanceInfo", fetch = FetchType.LAZY)
    private D1100 d1100;
    @OneToOne(mappedBy = "cardIssuanceInfo", fetch = FetchType.LAZY)
    private D1200 d1200;
    @OneToOne(mappedBy = "cardIssuanceInfo", fetch = FetchType.LAZY)
    private D1400 d1400;

    @Embedded
    private Venture venture; //벤처기업정보

    @Embedded
    private Stockholder stockholder; // 주주정보

    @Embedded
    private Card card; // 카드정보

    @Embedded
    private BankAccount bankAccount; // 결제계좌정보

    @OneToMany(mappedBy = "cardIssuanceInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Collection<CeoInfo> ceoInfos;

    @OneToMany(mappedBy = "cardIssuanceInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Collection<StockholderFile> stockholderFiles;

    @Enumerated(EnumType.STRING)
    private CardCompany cardCompany;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)  DEFAULT 'SIGNUP' COMMENT '카드발급신청 진행상황'")
    private IssuanceDepth issuanceDepth;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)  DEFAULT 'UNISSUED' COMMENT '카드발급 상태'")
    private IssuanceStatus issuanceStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) NOT NULL DEFAULT 'GOWID' COMMENT '카드 종류'")
    private CardType cardType;

    @Embedded
    private CorpExtend corpExtend;

    @OneToOne(mappedBy = "cardIssuanceInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private ManagerInfo managerInfo;

    @Embedded
    private FinancialConsumers financialConsumers;

    @Column(columnDefinition = "DATETIME default null COMMENT '신청완료일'")
    private LocalDateTime appliedAt;

    @Column(columnDefinition = "DATETIME default null COMMENT '발급완료일'")
    private LocalDateTime issuedAt;

    public void updateIssuanceDepth(IssuanceDepth issuanceDepth){
        this.issuanceDepth = issuanceDepth;
    }

    public FinancialConsumers getFinancialConsumers(){
        return this.financialConsumers == null ?
            this.financialConsumers = new FinancialConsumers() : this.financialConsumers;
    }

    public BankAccount getBankAccount(){
        return this.bankAccount == null ?
            this.bankAccount = new BankAccount() : this.bankAccount;
    }

    public void updateCardCompany(CardCompany cardCompany){
        this.cardCompany = cardCompany;
    }

    public void updateIssuanceStatus(IssuanceStatus issuanceStatus){
        this.issuanceStatus = issuanceStatus;
    }

}
