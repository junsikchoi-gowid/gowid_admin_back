package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.Dept;
import com.nomadconnection.dapp.core.domain.repository.querydsl.DeptCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface DeptRepository extends JpaRepository<Dept, Long>, DeptCustomRepository {

	Stream<Dept> findByCorp(Corp corp);
	Optional<Dept> findByCorpAndName(Corp corp, String name);

//	Optional<Dept> findByUserAndName(User user, String name);
//	Stream<Dept> findByUser(User user);
}
