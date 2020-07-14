package com.nomadconnection.dapp.core.domain.repository.corp;

import com.nomadconnection.dapp.core.domain.corp.VentureBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentureBusinessRepository extends JpaRepository<VentureBusiness, Long> {
    List<VentureBusiness> findAllByOrderByNameAsc();

    /**
     * 벤처기업이름 찾기
     *
     * @param investorName 벤처기업사명
     * @return 동일한 벤처이름
     */
    @Query(nativeQuery = true, value = "" +
            "SELECT name " +
            "FROM VentureBusiness " +
            "WHERE replace(name, ' ', '') = replace(:investorName, ' ', '')")
    String findEqualsName(String investorName);
}
