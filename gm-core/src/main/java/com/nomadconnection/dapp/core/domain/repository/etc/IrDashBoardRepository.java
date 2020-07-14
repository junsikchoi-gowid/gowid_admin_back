package com.nomadconnection.dapp.core.domain.repository.etc;

import com.nomadconnection.dapp.core.domain.etc.IrDashBoard;
import com.nomadconnection.dapp.core.domain.repository.querydsl.IrDashBoardCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrDashBoardRepository extends JpaRepository<IrDashBoard, Long>, IrDashBoardCustomRepository {

}