package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.dto.shinhan.DataPart1710;
import com.nomadconnection.dapp.api.service.shinhan.handler.ShinhanResponseHandler;
import com.nomadconnection.dapp.api.v2.dto.kised.KisedRequestDto;
import com.nomadconnection.dapp.api.v2.dto.kised.KisedResponseDto;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.domain.repository.kised.KisedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisedService {

    private final IssuanceService issuanceService;

    private final KisedRepository kisedRepository;

    public KisedResponseDto verify(Long idxUser, KisedRequestDto requestDto) throws Exception {
        DataPart1710 dataPart1710 = issuanceService.proc1710(idxUser, requestDto);
        String responseCode = dataPart1710.getResponseCode();
        String responseMessage = dataPart1710.getResponseMessage();

        ShinhanResponseHandler.handleResponse1710(responseCode, responseMessage);
        return KisedResponseDto.from(dataPart1710);
    }

    @Transactional(rollbackFor = Exception.class)
    public Kised save(KisedResponseDto dto){
        Kised kised = kisedRepository.findByLicenseNoAndProjectId(dto.getLicenseNo(), dto.getProjectId()).orElseGet(
            () -> kisedRepository.save(
                Kised.builder()
                    .licenseNo(dto.getLicenseNo())
                    .projectId(dto.getProjectId())
                    .projectName(dto.getProjectName())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .orgName(dto.getOrgName())
                    .accountHolder(dto.getAccountHolder())
                    .accountNo(dto.getAccountNo())
                    .bankCode(dto.getBankCode())
                    .cash(dto.getCash())
                    .spot(dto.getSpot())
                    .build())
        );

        return kised;
    }

}
