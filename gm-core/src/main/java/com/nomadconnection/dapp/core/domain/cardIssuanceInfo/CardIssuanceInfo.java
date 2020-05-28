package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, of = "idx")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Entity
public class CardIssuanceInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Builder.Default
    private Boolean disabled = true; // 해당정보가 유효한 정보인지 아닌지 확인 (false 이면 폐기된 정보)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_cardIssuance"))
    private Corp corp; // 소속법인

    @Embedded
    private Venture venture; //벤처기업정보

    @Embedded
    private Stockholder stockholder; // 주주정보

    @Embedded
    private Card card; // 카드정보

    @Embedded
    private BankAccount bankAccount; // 결제계좌정보
}
