package com.nomadconnection.dapp.core.domain.repository.user;

import com.nomadconnection.dapp.core.domain.user.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

	Optional<VerificationCode> findByVerificationKeyAndCode(String key, String code);
}
