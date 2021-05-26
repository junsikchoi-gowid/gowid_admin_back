package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasCheckCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaasCheckCategoryRepository extends JpaRepository<SaasCheckCategory, Long> {
}
