package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.dto.EmailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Corp, String> {

	@Query("select new com.nomadconnection.dapp.core.dto.EmailDto(c.resCompanyIdentityNo, c.resCompanyNm, rc.hopeLimit, rc.grantLimit, u.email) " +
			" from Corp c, RiskConfig rc, User u" +
			" where replace(c.resCompanyIdentityNo,'-','') = :licenseNo " +
			"  and c.idx = rc.corp " +
			"  and c.idx = u.corp "
	)
	EmailDto findTopByLicenseNo(String licenseNo);

}
