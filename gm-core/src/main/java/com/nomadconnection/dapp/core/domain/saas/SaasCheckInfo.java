package com.nomadconnection.dapp.core.domain.saas;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.user.User;
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
public class SaasCheckInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "idxSaasCheckCategory")
    private SaasCheckCategory saasCheckCategory;

    @ManyToOne
    @JoinColumn(name = "idxSaasPaymentInfo")
    private SaasPaymentInfo saasPaymentInfo;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_SaasCheckInfo"))
    private User user;

    @Builder.Default
    private Boolean checked = false; // 체크 여부

    private String mom;

    private Long amountPrice;

    private Long amountIncreasePrice;

    private Integer increaseMonth;
}
