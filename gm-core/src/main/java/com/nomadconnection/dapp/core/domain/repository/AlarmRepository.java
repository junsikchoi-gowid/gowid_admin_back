package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Alarm;
import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.Dept;
import com.nomadconnection.dapp.core.domain.repository.querydsl.DeptCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long>{

}
