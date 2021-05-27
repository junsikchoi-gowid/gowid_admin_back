package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.res.BatchDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchDateRepository extends JpaRepository<BatchDate, Long> {

    Optional<BatchDate> findByAccount(String account);

}