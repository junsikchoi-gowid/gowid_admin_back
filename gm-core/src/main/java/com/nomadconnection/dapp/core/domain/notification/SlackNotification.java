package com.nomadconnection.dapp.core.domain.notification;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SlackNotification extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(columnDefinition = "varchar(30)  DEFAULT '' COMMENT   '고객이메일'")
    private String user;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '페이지이름'")
    private String screenName;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '카드사'")
    private String cardIssuer;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '희망한도'")
    private String hopeLimit;

    @Column(columnDefinition = "varchar(20)  DEFAULT '' COMMENT   '회사명'")
    private String company;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '대표종류'")
    private String ceoType;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '대표수'")
    private String ceoAccount;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '총잔고'")
    private String totalBalance;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '계산한도'")
    private String calculatedLimit;
}
