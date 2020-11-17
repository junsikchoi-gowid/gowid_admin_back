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
public class ManagerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    private String name; // 한글명
    private String engName; // 영문명
    private String phoneNumber; // 휴대폰번호
    private String nation; // 국적
    private String birth; // 생년월일
    private Long genderCode; // 성별 1:남자 2:여자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCardIssuanceInfo", foreignKey = @ForeignKey(name = "fk__managerInfo_cardIssuanceInfo"))
    private CardIssuanceInfo cardIssuanceInfo;
}