package com.nomadconnection.dapp.core.domain.repository.corp;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CorpRepository extends JpaRepository<Corp, Long> , CorpCustomRepository {

	Optional<Corp> findByResCompanyIdentityNo(String resCompanyIdentityNo);

	@Query(value = "select Corp.idxUser from Corp where resCompanyIdentityNo = :resCompanyIdentityNo limit 1", nativeQuery = true)
	Long searchResCompanyIdentityNo(String resCompanyIdentityNo);

	@Query(value = "select Corp.idxUser from Corp where idx = :idxCorp limit 1", nativeQuery = true)
	Long searchIdxUser(Long idxCorp);



	public static interface ScrapingResultDto {
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
			value = " select * from ("+
					" select a.idx as idxCorp, a.resCompanyNm as idxCorpName, b.createdAt, b.updatedAt, b.endFlag, a.idxUser, 0 as successAccountCnt, 0 as processAccountCnt, 0 as allAccountCnt \n" +
					" from (\n" +
					" select  a.idx ,  a.resCompanyNm ,  a.idxUser, max( b.idx) as idxResBatch\n" +
					"  from  Corp a\n" +
					"  inner join ResBatch b on b.idxUser = a.idxUser \n" +
					"  group by   a.idx ,  a.resCompanyNm ,  a.idxUser,  b.idxUser ) a inner join ResBatch b on b.idx = a.idxResBatch " +
					"  ) a " ,
			countQuery = " select * from ( "+
					" select count(*) " +
					" from (\n" +
					" select  a.idx ,  a.resCompanyNm ,  a.idxUser, max( b.idx) as idxResBatch\n" +
					"  from  Corp a\n" +
					"  inner join ResBatch b on b.idxUser = a.idxUser \n" +
					"  group by   a.idx ,  a.resCompanyNm ,  a.idxUser,  b.idxUser ) a inner join ResBatch b on b.idx = a.idxResBatch ) a ",
			nativeQuery = true
	)
	Page<ScrapingResultDto> scrapingList(Pageable pageable);
}
