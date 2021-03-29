package com.nomadconnection.dapp.core.domain.saas;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SaasInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT 'SaaS 이름'")
    private String name;

    @Column(columnDefinition = "VARCHAR(100) COMMENT 'SaaS 한글 이름'")
    private String korName;

    @Column(columnDefinition = "VARCHAR(100) COMMENT '이미지 이름'")
    private String imageName;

    @Lob
    private String homepageUrl;

    @Lob
    private String priceUrl;

    @Lob
    private String description;

    @ManyToOne(targetEntity = SaasCategory.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxSaasCategory", foreignKey = @ForeignKey(name = "FK_SaasCategory_SaasInfo"))
    private SaasCategory saasCategory;

    @OneToMany(mappedBy = "saasInfo")
    private List<SaasPaymentInfo> saasPaymentInfos;

    @OneToMany(mappedBy = "saasInfo")
    private List<SaasPaymentHistory> saasPaymentHistories;

}
