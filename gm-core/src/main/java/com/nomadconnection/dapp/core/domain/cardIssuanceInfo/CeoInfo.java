package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Entity
public class CeoInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Enumerated(EnumType.STRING)
    private CeoType type;

    private String name;
    private String engName;
    private String phoneNumber;
    private String nationality;
    private Boolean isForeign;
    private String birth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCardIssuanceInfo", foreignKey = @ForeignKey(name = "fk__ceoInfo_cardIssuanceInfo"))
    private CardIssuanceInfo cardIssuanceInfo;
}
