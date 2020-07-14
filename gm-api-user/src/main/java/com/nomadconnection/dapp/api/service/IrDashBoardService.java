package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.IrDashBoardDto;
import com.nomadconnection.dapp.core.domain.etc.IrDashBoard;
import com.nomadconnection.dapp.core.domain.repository.etc.IrDashBoardRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class IrDashBoardService {

    private final IrDashBoardRepository repoIrDashBoard;

    public ResponseEntity saveList(IrDashBoardDto irDashBoard, Long idxUser) {

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .data(repoIrDashBoard.save(IrDashBoard.builder()
                        .irType(irDashBoard.irType)
                        .title(irDashBoard.title)
                        .contents(irDashBoard.contents)
                        .idx(irDashBoard.idx)
                        .build()))
                .build());
    }

    public Page<IrDashBoardDto> getList(Pageable pageable) {

        /*
        Page<IrDashBoardDto> result = repoIrDashBoard.findList(IrDashBoard.builder()
                .irType(irDashBoard.irType)
                .contents(irDashBoard.contents)
                .title(irDashBoard.title)
                .build(), page, sortBy).map(IrDashBoardDto::from);
         List<IrDashBoardDto> list = result.getContent();
        */
        Page<IrDashBoardDto> result = repoIrDashBoard.findAll(pageable).map(IrDashBoardDto::from);

        return result;
    }
}
