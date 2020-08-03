package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.res.ResBatch;
import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ResConCorpListRepository extends JpaRepository<ResConCorpList, Long> {
    List<ResConCorpList> findByBusinessTypeAndIdxCorp(String BusinessType, Long IdxCorp);
}