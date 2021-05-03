package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.flow.FlowCommentStatus;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlowCommentStatusRepository extends JpaRepository<FlowCommentStatus, Long> {


    Optional<FlowCommentStatus> findTopByUser(User user);
}
