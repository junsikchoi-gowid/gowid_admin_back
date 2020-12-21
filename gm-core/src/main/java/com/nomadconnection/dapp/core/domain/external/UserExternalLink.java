package com.nomadconnection.dapp.core.domain.external;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UserExternalLink_uk_1", columnNames = {"idxUser", "externalCompanyType"})
})
public class UserExternalLink extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", nullable = false, foreignKey = @ForeignKey(name = "FK_UserExternalLink_User"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40) not null  COMMENT  '외부 연계 업체 코드' ")
    private ExternalCompanyType externalCompanyType;

    @Column(columnDefinition = "varchar(60) not null  COMMENT  '외부 연계 키' ")
    private String externalKey;
}
