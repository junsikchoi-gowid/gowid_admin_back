package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.BenefitCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitCategoryRepository extends JpaRepository<BenefitCategory, Long> {
    List<BenefitCategory> findAllByCategoryGroupCodeIsNullOrderByPriorityAsc();
}
