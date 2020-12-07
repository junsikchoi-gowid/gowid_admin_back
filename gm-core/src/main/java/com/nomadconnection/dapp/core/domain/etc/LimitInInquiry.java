package com.nomadconnection.dapp.core.domain.etc;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitInInquiry extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(columnDefinition = "varchar(20) NOT NULL COMMENT '희망한도'")
    private Long hopeLimit;

    @Column(columnDefinition = "varchar(50) NOT NULL COMMENT '이메일'")
    private String contact;

    @Column(columnDefinition = "varchar(50) NOT NULL COMMENT '법인명'")
    private String corporationName;

    @Column(columnDefinition = "varchar(200) COMMENT '메시지'")
    private String content;

}
