package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.dto.EmailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Corp, String> {

	@Query("select new com.nomadconnection.dapp.core.dto.EmailDto(c.resCompanyIdentityNo, c.resCompanyNm, ci.hopeLimit, ci.grantLimit, u.email) " +
			" from Corp c, CardIssuanceInfo ci, User u" +
			" where replace(c.resCompanyIdentityNo,'-','') = :licenseNo " +
			"  and c.idx = ci.corp " +
			"  and c.idx = u.corp "
	)
	EmailDto findTopByLicenseNo(@Param("licenseNo") String licenseNo);

}
