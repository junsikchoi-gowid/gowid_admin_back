package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SignatureHistory extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private Long corpIdx;

    @Column(nullable = false)
    private Long userIdx;

    @Column(columnDefinition = "blob not null comment '전자서명파일'")
    private String signedBinaryString;

    @Column(columnDefinition = "varchar(8)  COMMENT '신청접수일자'")
    private String applicationDate;    //신청접수일자

    @Column(columnDefinition = "varchar(5)  COMMENT '신청접수순번'")
    private String applicationNum;    //신청접수순번


}
