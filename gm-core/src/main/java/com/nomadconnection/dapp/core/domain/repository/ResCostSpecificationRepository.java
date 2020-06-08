package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCostSpecification;
import com.nomadconnection.dapp.core.domain.ResCostSpecificationList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResCostSpecificationRepository extends JpaRepository<ResCostSpecification, Long> {

}