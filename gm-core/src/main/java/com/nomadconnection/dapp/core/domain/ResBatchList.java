package com.nomadconnection.dapp.core.domain;


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
public class ResBatchList extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;
    private String errCode;
    private String errMessage;
    private String account;
    private String connectedId;
    private Long idxUser;
    private Long idxResBatch;
    private ResBatchType resBatchType; // account, history
    private String startDate;
    private String endDate;
    private String transactionId;
}
