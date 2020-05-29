package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.VentureBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentureBusinessRepository extends JpaRepository<VentureBusiness, Long> {
}
