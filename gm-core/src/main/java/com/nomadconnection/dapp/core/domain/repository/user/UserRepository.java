package com.nomadconnection.dapp.core.domain.repository.user;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.querydsl.UserCustomRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

	Optional<User> findByIdxNotAndEmailAndAuthentication_Enabled(Long idx, String email, boolean enabled);

	Optional<User> findByAuthentication_EnabledAndEmail(boolean enabled, String email);

	Optional<User> findByAuthentication_EnabledAndEmailAndIdxNot(boolean enabled, String email, Long idx);

	@Override
	Optional<User> findById(Long idxUser);

	Stream<User> findByNameAndMdn(String name, String mdn);
	Stream<User> findByCorp(Corp corp);
	Optional<User> findByExternalId(String externalId);

	Optional<User> findTopByCorp(Corp corp);

	List<User> findByAuthentication_Enabled(Boolean enabled);

}
