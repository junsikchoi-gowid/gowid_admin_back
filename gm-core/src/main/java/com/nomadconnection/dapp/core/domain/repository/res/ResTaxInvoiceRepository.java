package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.res.ResTaxInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResTaxInvoiceRepository extends JpaRepository<ResTaxInvoice, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ResTaxInvoice WHERE idxCorp = :idxCorp AND resIssueDate BETWEEN :startDate AND :endDate ")
    void deleteTaxInvoiceAndResIssueDate(@Param("idxCorp") Long idxCorp,
                                         @Param("startDate") String startDate,
                                         @Param("endDate") String endDate);
}