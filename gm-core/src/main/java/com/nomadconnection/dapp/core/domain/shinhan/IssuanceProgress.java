package com.nomadconnection.dapp.core.domain.shinhan;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userIdx"}, name = "uk_userIdx"))
public class IssuanceProgress extends BaseTime {

    @Id
    @Column(nullable = false, updatable = false)
    private Long userIdx;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40) COMMENT '진행단계. NOT_SIGNED: 전자서명전, SIGNED: 서명완료, P_1200: 신규/기존 여부 체크, P_15XX: 스크래핑 전문 전송, P_AUTO_CHECK: 자동심사(1000/1400), P_IMG: 이미지전송 , P_1600: 수동심사결과(1600), P_1100: 카드신청정보 전송  , P_1800: 전자서명값 전송'")
    private IssuanceProgressType progress;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40) COMMENT '상태. DEFAULT:초기상태(실행전), SUCCESS:성공, FAILED:실패'")
    private IssuanceStatusType status;

}
