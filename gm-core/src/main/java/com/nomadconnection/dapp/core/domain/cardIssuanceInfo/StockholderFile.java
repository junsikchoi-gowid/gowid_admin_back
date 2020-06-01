package com.nomadconnection.dapp.core.domain.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, of = "idx")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicUpdate
@SuppressWarnings("SpellCheckingInspection")
public class StockholderFile extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCardIssuanceInfo", foreignKey = @ForeignKey(name = "fk__stockholder_file__cardIssuanceInfo"))
    private CardIssuanceInfo cardIssuanceInfo;

    private String orgfname; // 파일명(원본)

    private String fname; // 파일명

    private Long size;     // 파일사이즈

    private String s3Link; // s3주소

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "fk__stockholder_file__corp"))
    private Corp corp;
}
