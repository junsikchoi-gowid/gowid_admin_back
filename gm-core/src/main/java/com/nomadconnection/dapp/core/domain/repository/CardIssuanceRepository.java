package com.nomadconnection.dapp.core.domain.repository;


import com.nomadconnection.dapp.core.domain.CardIssuance;
import com.nomadconnection.dapp.core.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardIssuanceRepository extends JpaRepository<CardIssuance, Long> {

	Page<CardIssuance> findByUser(User user, Pageable pageable);
}
