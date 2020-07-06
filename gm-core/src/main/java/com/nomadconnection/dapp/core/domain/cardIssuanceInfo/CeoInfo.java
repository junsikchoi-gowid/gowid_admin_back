package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String agencyCode; // 통신사 코드
    private Long genderCode; // 성별 1:남자 2:여자

    @Builder.Default
    private Integer ceoNumber = 0;

    @Enumerated(EnumType.STRING)
    private CertificationType certificationType; // 신분검증방법

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCardIssuanceInfo", foreignKey = @ForeignKey(name = "fk__ceoInfo_cardIssuanceInfo"))
    private CardIssuanceInfo cardIssuanceInfo;
}
