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
public class ResBatch extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;
    private Long idxUser;
    private boolean endFlag;
}
