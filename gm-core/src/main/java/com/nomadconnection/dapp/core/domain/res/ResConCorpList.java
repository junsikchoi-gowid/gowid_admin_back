package com.nomadconnection.dapp.core.domain.res;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ResConCorpList extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private Long idxCorp;

    @Column(nullable = false)
    private String connectedId;

    @Column(columnDefinition = "varchar(2) COMMENT '고객 구분 개인 : P / 기업, 법인 : B / 통합 : A'")
    private String clientType;

    @Column(columnDefinition = "varchar(20) COMMENT '결과코드'")
    private String code;

    @Column(columnDefinition = "varchar(10) COMMENT '로그인구분'")
    private String loginType;

    @Column(columnDefinition = "varchar(10) COMMENT '국가코드'")
    private String countryCode;

    @Column(columnDefinition = "varchar(10) COMMENT '기관코드'")
    private String organization;

    @Column(columnDefinition = "varchar(500) COMMENT '메시지'")
    private String extraMessage;

    @Column(columnDefinition = "varchar(10) COMMENT '업무구분 은행, 저축은행 : BK, 카드 : CD , 증권 : ST ,보험 : IS'")
    private String businessType;

    @Column(columnDefinition = "varchar(500) COMMENT '메시지'")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)  DEFAULT 'NORMAL' COMMENT '카드발급 상태'")
    private ConnectedMngStatus status;

}
