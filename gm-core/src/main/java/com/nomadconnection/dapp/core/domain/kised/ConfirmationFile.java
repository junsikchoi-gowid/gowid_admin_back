package com.nomadconnection.dapp.core.domain.kised;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class ConfirmationFile extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @OneToOne(mappedBy = "confirmationFile")
    private Kised kised;

    private String orgFileName;

    private String fileName;

    private Long size;

    private String s3Link;

    private String s3Key;

    private Boolean isTransferToGw;

}
