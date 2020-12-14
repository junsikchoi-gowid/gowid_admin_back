package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.Benefit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    // Benefit 목록 조회
    Page<Benefit> findAllByOrderByPriority(Pageable pageable);

    // Benefit 목록 조회(disable: false)
	Page<Benefit> findAllByDisabledFalseOrderByPriority(Pageable pageable);

    // 우선 순위값 조회(max + 1)
    @Query(value = "select max(priority) + 1 from gowid.Benefit where disabled = false", nativeQuery = true)
    Integer getMaxPriority();
}
