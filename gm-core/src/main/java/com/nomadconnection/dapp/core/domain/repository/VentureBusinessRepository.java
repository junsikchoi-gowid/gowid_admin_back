package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.VentureBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentureBusinessRepository extends JpaRepository<VentureBusiness, Long> {
    Boolean existsByName(String name);
    List<VentureBusiness> findAllByOrderByNameAsc();
}
