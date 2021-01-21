package com.nomadconnection.dapp.core.domain.res;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
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
public class ResExchangeRate extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;
    private String date;

    @Column(columnDefinition = "varchar(255) COMMENT '국가'")
    private String country;

    @Column(columnDefinition = "float COMMENT '보낼때'")
    private Float sending;

    @Column(columnDefinition = "float COMMENT '받을때'")
    private Float receiving;

    @Column(columnDefinition = "float COMMENT '매매기준율'")
    private Float standard;

    @Column(columnDefinition = "float COMMENT '장부가격'")
    private Float standardPrice;
}
