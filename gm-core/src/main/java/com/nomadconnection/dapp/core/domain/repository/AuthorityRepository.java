package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Authority;
import com.nomadconnection.dapp.core.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Optional<Authority> findByRole(Role role);
}
