package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasTrackerProgress;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaasTrackerProgressRepository extends JpaRepository<SaasTrackerProgress, Long> {

    Optional<SaasTrackerProgress> findByUser(User user);
}
