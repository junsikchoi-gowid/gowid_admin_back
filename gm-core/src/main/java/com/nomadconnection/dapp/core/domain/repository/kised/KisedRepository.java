package com.nomadconnection.dapp.core.domain.repository.kised;

import com.nomadconnection.dapp.core.domain.kised.Kised;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KisedRepository extends JpaRepository<Kised, Long> {

	Optional<Kised> findByLicenseNoAndProjectId(String licenseNo, String projectId);

}
