package com.nomadconnection.dapp.core.domain.repository.user;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.Dept;
import com.nomadconnection.dapp.core.domain.repository.querydsl.UserCustomRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

	Optional<User> findByEmail(String email);
	Optional<User> findByIdxNotAndEmailAndAuthentication_Enabled(Long idx, String email, boolean enabled);

	Optional<User> findByAuthentication_EnabledAndEmail(boolean enabled, String email);

	Stream<User> findByNameAndMdn(String name, String mdn);
	Stream<User> findByCorp(Corp corp);

	List<User> findFirstByCorp(Long idxCorp);

	List<User> findByAuthentication_Enabled(Boolean enabled);

	@Modifying
	@Query("UPDATE User u SET u.dept = NULL WHERE u.dept = :dept")
	int clearDept(Dept dept);
}
