package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.LimitInquiryDto;
import com.nomadconnection.dapp.core.domain.etc.LimitInInquiry;
import com.nomadconnection.dapp.core.domain.repository.etc.LimitInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LimitInquiryService {

    private final LimitInquiryRepository limitInquiryRepo;

    public void saveLimitInquiry(LimitInquiryDto request) {
        LimitInInquiry limitInInquiry = new LimitInInquiry();
        BeanUtils.copyProperties(request, limitInInquiry);
        limitInquiryRepo.save(limitInInquiry);
    }
}
