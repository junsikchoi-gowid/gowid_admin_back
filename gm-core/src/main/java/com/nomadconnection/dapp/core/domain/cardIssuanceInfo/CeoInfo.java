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
    private CeoType type; // 대표자 구분

    private String name; // 한글명
    private String engName; // 영문명
    private String phoneNumber; // 휴대폰번호
    private String nationality; // 국적
    private Boolean isForeign; // 외국인여부
    private String birth; // 생년월일
    private String agencyCode; // 통신사 코드
    private Long genderCode; // 성별 1:남자 2:여자

    @Builder.Default
    private Integer ceoNumber = 0; // 전문에 입력되는 대표자 순번

    @Enumerated(EnumType.STRING)
    private CertificationType certificationType; // 신분검증방법

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCardIssuanceInfo", foreignKey = @ForeignKey(name = "fk__ceoInfo_cardIssuanceInfo"))
    private CardIssuanceInfo cardIssuanceInfo;
}
