package com.nomadconnection.dapp.core.domain.repository.corp;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorpRepository extends JpaRepository<Corp, Long> , CorpCustomRepository {

	Optional<Corp> findByResCompanyIdentityNo(String resCompanyIdentityNo);

	@Override
	Optional<Corp> findById(Long corpIdx);

	@Query(value = "SELECT Corp.idxUser FROM Corp WHERE resCompanyIdentityNo = :resCompanyIdentityNo limit 1", nativeQuery = true)
	Long searchResCompanyIdentityNo(String resCompanyIdentityNo);

	@Query(value = "SELECT Corp.idxUser FROM Corp WHERE idx = :idxCorp limit 1", nativeQuery = true)
	Long searchIdxUser(@Param("idxCorp") Long idxCorp);
 
	@Query(value = "select Corp.idx from Corp where idxUser = :idxUser limit 1", nativeQuery = true)
	Long searchIdxCorp(@Param("idxUser") Long idxUser);

	@Query(value = "SELECT c.* FROM Corp AS c" +
			" LEFT JOIN CardIssuanceInfo AS ci ON ci.idxCorp = c.idx" +
			" WHERE ci.issuanceStatus IN (:issuanceStatus)", nativeQuery = true)
	List<Corp> findCorpByIssuanceStatus(@Param("issuanceStatus") List<String> issuanceStatus);

	@Transactional
	@Modifying
	@Query("DELETE FROM Corp  WHERE idx = :idxCorp")
	void deleteCorpByIdx(@Param("idxCorp") Long idxCorp);

	interface ScrapingResultDto {
		Long getIdxCorp();
		String getIdxCorpName();
		String getSuccessAccountCnt();
		String getProcessAccountCnt();
		String getAllAccountCnt();
		LocalDateTime getCreatedAt();
		LocalDateTime getUpdatedAt();
		boolean getEndFlag();
		Long getIdxUser();
	}

	@Query(
			value = " SELECT * FROM ("+
					" SELECT a.idx AS idxCorp, a.resCompanyNm AS idxCorpName, b.createdAt, b.updatedAt, b.endFlag, a.idxUser, 0 AS successAccountCnt, 0 AS processAccountCnt, 0 AS allAccountCnt \n" +
					" FROM (\n" +
					" SELECT a.idx, a.resCompanyNm, a.idxUser, max( b.idx) AS idxResBatch\n" +
					" FROM Corp a\n" +
					" INNER JOIN ResBatch b ON b.idxUser = a.idxUser \n" +
					" GROUP BY a.idx, a.resCompanyNm, a.idxUser, b.idxUser ) a INNER JOIN ResBatch b ON b.idx = a.idxResBatch " +
					"  ) a " ,
			countQuery = " SELECT * FROM ( "+
					" SELECT count(*) " +
					" FROM (\n" +
					" SELECT a.idx, a.resCompanyNm, a.idxUser, max( b.idx) AS idxResBatch\n" +
					" FROM Corp a\n" +
					" INNER JOIN ResBatch b ON b.idxUser = a.idxUser \n" +
					" GROUP BY a.idx, a.resCompanyNm, a.idxUser, b.idxUser ) a INNER JOIN ResBatch b ON b.idx = a.idxResBatch ) a ",
			nativeQuery = true
	)
	Page<ScrapingResultDto> scrapingList(Pageable pageable);


}
