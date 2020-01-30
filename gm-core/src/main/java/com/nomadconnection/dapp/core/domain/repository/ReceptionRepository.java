package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Reception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ReceptionRepository extends JpaRepository<Reception, Long> {
    long deleteByReceiver(String receiver);
}