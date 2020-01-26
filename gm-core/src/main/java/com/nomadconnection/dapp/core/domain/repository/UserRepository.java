package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.Dept;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.repository.querydsl.UserCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

	Optional<User> findByEmail(String email);
	Optional<User> findByIdxNotAndEmailAndAuthentication_Enabled(Long idx, String email, boolean enabled);

	Optional<User> findByAuthentication_EnabledAndEmail(boolean enabled, String email);

	Stream<User> findByNameAndMdn(String name, String mdn);
	Stream<User> findByCorp(Corp corp);

	@Modifying
	@Query("UPDATE User u SET u.dept = NULL WHERE u.dept = :dept")
	int clearDept(Dept dept);
}
