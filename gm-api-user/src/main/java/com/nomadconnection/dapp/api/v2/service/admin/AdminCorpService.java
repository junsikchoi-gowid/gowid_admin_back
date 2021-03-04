package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.v2.dto.AdminDto.CorpDto;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCorpService {
    private final CorpRepository corpRepository;

    @Transactional(readOnly = true)
    public Page<CorpCustomRepository.CorpListDto> getCorpList(CorpCustomRepository.CorpListDto dto, Pageable pageable) {
        return corpRepository.adminCorpListV2(dto, pageable);
    }

    @Transactional(readOnly = true)
    public CorpDto getCorpInfo(Long idxCorp) {
        return corpRepository.findById(idxCorp).map(CorpDto::from).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );
    }
}
