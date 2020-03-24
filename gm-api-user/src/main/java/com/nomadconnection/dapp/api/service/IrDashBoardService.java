package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.IrDashboardDto;
import com.nomadconnection.dapp.core.domain.IrDashBoard;
import com.nomadconnection.dapp.core.domain.repository.IrDashBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class IrDashBoardService {

    private final IrDashBoardRepository repoIrDashboard;

    public ResponseEntity saveList(IrDashboardDto irDashBoard, Long idxUser) {
        return null;
    }

    public Page getList(Pageable page, IrDashboardDto irDashBoard, Long idxUser) {


        // Page<IrDashBoard> result = repoIrDashboard.findByIrTypeAndTitleLikeAndContentsLike(page, irDashBoard.irType, irDashBoard.title, irDashBoard.contents);
        // Page<IrDashBoard> result = repoIrDashboard.findByIrTypeAndTitleLikeAndContentsLike(page, "%c%", "%i%", "%c%");
        Page<IrDashBoard> result = repoIrDashboard.findByTitle(page, "c");
        List<IrDashBoard> list = result.getContent();

        System.out.println("PAGE SIZE: " + result.getSize());
        System.out.println("TOTAL PAGE: " + result.getTotalPages());
        System.out.println("TOTAL COUNT: " + result.getTotalElements());
        System.out.println("NEXT: " + result.nextPageable());
        System.out.println("NEXT: " + list);

        return result;
    }
}
