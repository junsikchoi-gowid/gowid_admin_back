package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.core.domain.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentService {


    /**
    이용약관 현재 사용여부 등
    이용약관 목록
    */
//    @Transactional(rollbackFor = Exception.class)
//    public List<BrandConsentDto> consents {
//
//        return repo.findByAll();
//    }
}
