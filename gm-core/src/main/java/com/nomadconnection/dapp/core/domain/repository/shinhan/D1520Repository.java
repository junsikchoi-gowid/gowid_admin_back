package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.D1520;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface D1520Repository extends JpaRepository<D1520, Long> {
    List<D1520> findTop2ByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
