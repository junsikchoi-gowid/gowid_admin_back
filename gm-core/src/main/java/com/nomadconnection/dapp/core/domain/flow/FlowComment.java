package com.nomadconnection.dapp.core.domain.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
public class FlowComment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_FlowComment"), columnDefinition = "bigint(20) COMMENT '법인 idx'", nullable = false)
    private Corp corp;

    @Column(length = 65535, columnDefinition = "Text COMMENT '내용'")
    private String comment;

    @Column(columnDefinition = "VARCHAR(255) COMMENT 'orgFileName'")
    private String orgFileName;

    @Column(columnDefinition = "VARCHAR(255) COMMENT 'fileName'")
    private String fileName;

    @Column(columnDefinition = "BIGINT(20) COMMENT 'fileSize'")
    private Long fileSize;

    @Column(columnDefinition = "VARCHAR(200) COMMENT 's3Link'")
    private String s3Link;

    @Column(columnDefinition = "VARCHAR(200) COMMENT 's3Key'")
    private String s3Key;

    @Column(columnDefinition = "BIT(1) DEFAULT NULL COMMENT '사용유무'")
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_FlowComment"), columnDefinition = "bigint(20) COMMENT '유저 idx'", nullable = false)
    private User user;

    public static FlowComment of(FlowComment dto, User user){
        return FlowComment.builder()
                .corp(dto.corp())
                .comment(dto.comment())
                .orgFileName(dto.orgFileName())
                .fileName(dto.fileName())
                .fileSize(dto.fileSize())
                .s3Link(dto.s3Link())
                .s3Key(dto.s3Key())
                .enabled(dto.enabled())
                .user(user)
                .build();
    }

}
