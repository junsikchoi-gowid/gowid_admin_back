package com.nomadconnection.dapp.core.domain.flow;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
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
public class FlowCommentStatus extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_FlowCommentStatus"))
    private User user; // 법인을 등록한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxFlowComment", foreignKey = @ForeignKey(name = "FK_FlowComment_FlowCommentStatus"))
    private FlowComment flowComment;
}
