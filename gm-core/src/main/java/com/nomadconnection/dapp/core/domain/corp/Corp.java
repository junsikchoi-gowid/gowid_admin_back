package com.nomadconnection.dapp.core.domain.corp;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class Corp extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_Corp"))
    private User user; // 법인을 등록한 사용자

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idxRiskConfig", foreignKey = @ForeignKey(name = "FK_Corp_RiskConfig"))
    private RiskConfig riskConfig; // 법인 리스크 정보

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "corp")
    private LimitRecalculation limitRecalculation;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "corp")
    private List<CardIssuanceInfo> cardIssuanceInfo = new ArrayList<>();

    private String resBusinessItems; // 종목
    private String resBusinessTypes; // 업태
    private String resBusinessmanType; // 사업자종류
    private String resBusinessCode; // 업종코드

    @EqualsAndHashCode.Include
    private String resCompanyIdentityNo; // 사업자등록번호

    private String resCompanyNm; // 법인명
    private String resCompanyEngNm; // 법인명(영문)
    private String resCompanyNumber; // 사업장전화번호
    private String resCompanyZipCode; // 사업장우편번호
    private String resCompanyAddr; // 사업장우편번호주소
    private String resCompanyAddrDt; // 사업장우편번호외주소
    private String resCompanyBuildingCode; // 도로명참조키값
    private String resIssueNo; // 발급(승인)번호
    private String resIssueOgzNm; // 발급기관
    private String resJointIdentityNo; //공동사업자 주민번호
    private String resJointRepresentativeNm; // 공동사업자 성명(법인명)
    private String resOpenDate; // 개업일
    private String resOriGinalData; // 원문 DATA
    private String resRegisterDate; // 사업자등록일
    private String resUserAddr; // 사업장소재지(주소)
    private String resUserIdentiyNo; // 주민(법인)등록번호
    private String resUserNm; // 성명(대표자)
    private String resUserType; // 대표자 종류 (1: 개별, 2:각자, 3:공동)
    private Integer ceoCount; // 대표자 수

    @Enumerated(EnumType.STRING)
    private CorpStatus status; // pending/denied/approved

}
