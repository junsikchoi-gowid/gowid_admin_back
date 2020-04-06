package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.IrDashBoard;
import com.nomadconnection.dapp.core.domain.repository.querydsl.IrDashBoardCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

@Repository
public interface IrDashBoardRepository extends JpaRepository<IrDashBoard, Long>, IrDashBoardCustomRepository {

}