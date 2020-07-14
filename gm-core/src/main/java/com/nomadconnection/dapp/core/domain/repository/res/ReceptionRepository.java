package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.user.Reception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionRepository extends JpaRepository<Reception, Long> {
    long deleteByReceiver(String receiver);
}