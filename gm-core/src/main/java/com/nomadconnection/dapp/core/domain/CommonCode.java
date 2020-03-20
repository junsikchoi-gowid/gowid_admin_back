package com.nomadconnection.dapp.core.domain;
        import com.nomadconnection.dapp.core.domain.audit.BaseTime;
        import lombok.*;
        import lombok.experimental.Accessors;
        import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCode extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long idx;
    private String code;
    private String codeDesc;
}
