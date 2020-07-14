package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.etc.IrDashBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IrDashBoardCustomRepository {
	Page<IrDashBoard> findList(IrDashBoard irDashBoard, Pageable pageable, String sortBt);
}
