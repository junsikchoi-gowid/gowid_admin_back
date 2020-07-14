package com.nomadconnection.dapp.core.domain.repository.user;

import com.nomadconnection.dapp.core.domain.user.Authority;
import com.nomadconnection.dapp.core.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Optional<Authority> findByRole(Role role);
}
