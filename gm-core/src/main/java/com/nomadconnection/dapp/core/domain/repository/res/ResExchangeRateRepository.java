package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.res.ResExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResExchangeRateRepository extends JpaRepository<ResExchangeRate, Long> {
     ResExchangeRate findByDateAndCountry(String searchdate, String cur_unit);
}