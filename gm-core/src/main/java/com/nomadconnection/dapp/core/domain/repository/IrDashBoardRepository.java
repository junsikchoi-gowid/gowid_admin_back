package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.IrDashBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrDashBoardRepository extends JpaRepository<IrDashBoard, Long> {

	Page<IrDashBoard> findByIrTypeAndTitleLikeAndContentsLike(Pageable pageable, String irType, String title, String contents);

	Page<IrDashBoard> findByTitle(Pageable page, String s);
}