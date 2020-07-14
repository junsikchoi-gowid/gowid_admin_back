package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.etc.IrDashBoard;
import com.nomadconnection.dapp.core.domain.etc.QIrDashBoard;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class IrDashBoardCustomRepositoryImpl extends QuerydslRepositorySupport implements IrDashBoardCustomRepository{

	private final QIrDashBoard irDashBoard = QIrDashBoard.irDashBoard;

	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 */
	public IrDashBoardCustomRepositoryImpl() {
		super(IrDashBoard.class);
	}

	@Override
	public Page<IrDashBoard> findList(IrDashBoard dto, Pageable pageable, String sortBy) {

		JPQLQuery<IrDashBoard> query = from(irDashBoard);
		if (dto.irType() != null) {
			query.where(irDashBoard.irType.eq(dto.irType()));
		} else if (dto.title() != null) {
			query.where(irDashBoard.title.contains(dto.title()));
		} else if (dto.contents() != null) {
			query.where(irDashBoard.contents.contains(dto.contents()));
		} else if (dto.idx() != null) {
			query.where(irDashBoard.idx.eq(dto.idx()));
		}

		if (sortBy != null) {
			if (sortBy.toLowerCase().equals("asc")) {
				query.orderBy(QIrDashBoard.irDashBoard.createdAt.asc());
			} else if (sortBy.toLowerCase().equals("desc")) {
				query.orderBy(QIrDashBoard.irDashBoard.createdAt.desc());
			}
		}

		List<IrDashBoard> irDashBoardList = getQuerydsl().applyPagination(pageable, query).fetch();
		return new PageImpl<>(irDashBoardList, pageable, query.fetchCount());
	}
}
