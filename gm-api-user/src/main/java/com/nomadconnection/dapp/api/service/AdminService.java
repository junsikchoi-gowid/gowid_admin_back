package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.v2.service.card.LotteCardServiceV2;
import com.nomadconnection.dapp.api.v2.service.card.ShinhanCardServiceV2;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceDepth;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchListRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repoUser;
    private final RiskRepository repoRisk;
    private final CorpRepository repoCorp;
    private final ResBatchRepository repoResBatch;
    private final ResBatchListRepository repoResBatchList;
    private final RiskConfigRepository repoRiskConfig;
    private final CardIssuanceInfoRepository repoCardIssuance;

    private final ShinhanCardServiceV2 shinhanCardService;
    private final LotteCardServiceV2 lotteCardService;

    private Boolean isGowidMaster(Long idxUser) {

        boolean boolV = false;

        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );

        if (user.authorities().stream().noneMatch(o ->
                (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))) {
            throw UserNotFoundException.builder().build();
        }

        if (user.authorities().stream().anyMatch(o -> o.role().equals(Role.GOWID_ADMIN))) {
            boolV = true;
        }

        return boolV;
    }

    public boolean isGowidAdmin(long idxUser) {
        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );
        return user.authorities().stream()
                .anyMatch(o -> o.role().equals(Role.GOWID_ADMIN));
    }

    public boolean hasAdminAuth(long idxUser, Role role) {
        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );
        return user.authorities().stream()
                .anyMatch(o -> o.role().equals(role));
    }

    private Integer intGowidMaster(Long idxUser) {

        int iReturn = 0;

        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );

        if (user.authorities().stream().noneMatch(o ->
                (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))) {
            iReturn = 1;
        }

        if (user.authorities().stream().anyMatch(o -> o.role().equals(Role.GOWID_ADMIN))) {
            iReturn = 2;
        }

        return iReturn;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity riskIdLevelChange(Long idxUser, RiskDto.RiskConfigDto dto) {

        Boolean isMaster = isGowidMaster(idxUser);
        double deposit = dto.getDepositGuarantee();
        RiskConfig riskConfig = repoRiskConfig.findByCorpAndEnabled(Corp.builder().idx(dto.idxCorp).build(), true)
            .orElseThrow(
                () -> CorpNotRegisteredException.builder().account(dto.idxCorp.toString()).build()
            );

        if(deposit > 0){
            updateDeposit(riskConfig, dto);
        }

        riskConfig.enabled(true);
        riskConfig.ceoGuarantee(dto.isCeoGuarantee());
        riskConfig.ventureCertification(dto.isVentureCertification());
        riskConfig.vcInvestment(dto.isVcInvestment());

        CardIssuanceInfo cardIssuanceInfo = repoCardIssuance.findByUserAndCardType(riskConfig.user(), CardType.GOWID).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CardIssuanceInfo")
                .build()
        );

        cardIssuanceInfo.venture()
            .isVC(dto.vcInvestment)
            .isVerifiedVenture(dto.ventureCertification);

        if (CardCompany.LOTTE.equals(cardIssuanceInfo.cardCompany())) {
            lotteCardService.updateD1100Venture(dto.idxCorp, CardIssuanceDto.RegisterVenture.builder()
                .isVerifiedVenture(dto.isVentureCertification())
                .isVC(dto.isVcInvestment())
                .build()
            );
        }

        return ResponseEntity.ok().body(
            BusinessResponse.builder()
                .data(repoRiskConfig.save(riskConfig)
                ).build());
    }

    private void updateDeposit(RiskConfig riskConfig, RiskDto.RiskConfigDto dto){
        Corp corp = repoCorp.findById(dto.getIdxCorp()).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );

        CardIssuanceInfo cardIssuanceInfo = repoCardIssuance.findByCorpAndCardType(corp, CardType.GOWID).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );

        User user = corp.user();
        double deposit = dto.getDepositGuarantee();
        String depositString = NumberUtils.doubleToString(deposit);

        cardIssuanceInfo.card().grantLimit(depositString);
        riskConfig.depositPayment(dto.isDepositPayment());
        riskConfig.depositGuarantee(dto.depositGuarantee);
        riskConfig.grantLimit(depositString);


        if(CardCompany.SHINHAN.equals(user.cardCompany())){
            shinhanCardService.updateShinhanFulltextLimit(cardIssuanceInfo, depositString);
        } else if(CardCompany.LOTTE.equals(user.cardCompany())){
            lotteCardService.updateD1100Limit(user, depositString, riskConfig.hopeLimit());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity saveEmergencyStop(Long idxUser, Long idxCorp, String booleanValue) {

        Boolean isMaster = isGowidMaster(idxUser);

        String calcDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        Risk risk = repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), calcDate).orElseThrow(
                () -> new RuntimeException("Empty Data")
        );

        if (booleanValue != null) {
            if (booleanValue.toLowerCase().equals("true")) {
                risk.emergencyStop(true);
            } else {
                risk.emergencyStop(false);
            }
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().data(repoRisk.save(risk)).build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity corpId(Long idx, Long idxCorp) {

        CorpDto corpDto = repoCorp.findById(idxCorp).map(CorpDto::from).orElseThrow(
                () -> CorpNotRegisteredException.builder().build()
        );

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                () -> CorpNotRegisteredException.builder().build());

        User user = repoUser.findTopByCorp(corp).orElseThrow(
                () -> CorpNotRegisteredException.builder().build());

        RiskDto.RiskConfigDto riskConfig = repoRiskConfig.findByCorpAndEnabled(corp, true).map(RiskDto.RiskConfigDto::from)
                .orElseGet(
                        () -> RiskDto.RiskConfigDto.builder().build()
                );


        AdminDto.corpInfoDetailId corpInfo = AdminDto.corpInfoDetailId.builder()
                .corpDto(corpDto)
                .cardCompany(user.cardCompany())
                .hopeLimit(riskConfig.hopeLimit)
                .grantLimit(riskConfig.grantLimit)
                .userName(user.name())
                .userNumber(user.mdn())
                .userEmail(user.email())
                .smsFlag(user.reception().getIsSendSms())
                .emailFlag(user.reception().getIsSendEmail())
                .build();

        return ResponseEntity.ok().body(BusinessResponse.builder().data(corpInfo).build());
    }

    public ResponseEntity scrapingList(Long idx, Pageable pageable) {
        int intMaster = intGowidMaster(idx);
        Page<AdminDto.ScrapingListDto> resAccountPage = null;
        if (intMaster > 0) {
            resAccountPage = repoCorp.scrapingList(pageable).map(AdminDto.ScrapingListDto::from);

            resAccountPage.getContent().forEach(
                    o -> {
                        List<ResBatchRepository.CResBatchDto> data = repoResBatch.findRefresh(o.idxUser);
                        o.setSuccessAccountCnt(String.valueOf(Integer.parseInt(data.get(0).getTotal()) - Integer.parseInt(data.get(0).getErrorCnt())));
                        o.setAllAccountCnt(data.get(0).getTotal());
                        o.setProcessAccountCnt(data.get(0).getProgressCnt());
                    }
            );

            if (intMaster < 2) {
                for (AdminDto.ScrapingListDto x : resAccountPage.getContent()) {
                    x.setIdxCorpName("#" + x.idxCorp);
                }
            }
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().data(resAccountPage).build());
    }

    public ResponseEntity originalList(AdminCustomRepository.RiskOriginal riskOriginal, Long idxUser, Pageable pageable) {

        Boolean isMaster = isGowidMaster(idxUser);

        Page<AdminDto.RiskNewDto> resAccountPage = repoRisk.riskList(riskOriginal, idxUser, pageable).map(AdminDto.RiskNewDto::from);

        return ResponseEntity.ok().body(
                BusinessResponse.builder().data(resAccountPage).build()
        );
    }

    public ResponseEntity scrapCorpList(CorpCustomRepository.ScrapCorpDto dto, Long idxUser, Pageable pageable) {
        Boolean isMaster = isGowidMaster(idxUser);

        if(StringUtils.isEmpty(dto.getUpdatedAt())){
            dto.setUpdatedAt(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }

        Page<CorpCustomRepository.ScrapCorpListDto> page = repoCorp.scrapCorpList(dto, pageable);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
    }

    public ResponseEntity scrapAccountList(ResBatchListCustomRepository.ScrapAccountDto dto, Long idxUser, Pageable pageable) {
        Boolean isMaster = isGowidMaster(idxUser);

        Page<ResBatchListCustomRepository.ScrapAccountListDto> page = repoResBatchList.scrapAccountList(dto, pageable);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity corpInfo(Long idxCorp, Long idxUser) {
        Boolean isMaster = isGowidMaster(idxUser);
        AdminDto.corpInfoDetail data = new AdminDto.corpInfoDetail();

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                () -> new RuntimeException("Bad idxCorp request.")
        );

        data.setCardCompany(repoCorp.findById(idxCorp).get().user().cardCompany());

        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), date).ifPresent(
                risk -> {
                    data.setDepositGuarantee(risk.depositGuarantee());
                    data.setIsVerifiedVenture(risk.ventureCertification());
                    data.setIsVC(risk.vcInvestment());
                    data.setDma45(risk.dma45());
                    data.setDmm45(risk.dmm45());
                    data.setCashBalance(risk.cashBalance());
                    data.setCurrentBalance(risk.currentBalance());
                    data.setMinCashNeed(risk.minCashNeed());
                    data.setCardLimitNow(risk.cardLimitNow());
                    data.setCardRestartCount(risk.cardRestartCount());
                    data.setCardType(risk.cardType());
                    data.setGrade(risk.grade());
                    data.setEmergencyStop(risk.emergencyStop());
                    data.setRealtimeLimit(risk.realtimeLimit());
                }
        );

        repoRiskConfig.findByCorpAndEnabled(Corp.builder().idx(idxCorp).build(),true).ifPresent(
                riskConfig -> {
                    data.setHopeLimit(riskConfig.hopeLimit());
                    data.setCalculatedLimit(riskConfig.calculatedLimit());
                    data.setGrantLimit(riskConfig.grantLimit());
                }
        );

        // 어드민페이지에서는 GOWID카드 기준으로만 노출
        // 창진원 적용후 차후 개선 필요
        // 아래의 값들에 대한 데이터 저장 필요 -> cardIssuanceInfo
        // private LocalDateTime applyDate;
        // private LocalDateTime decisionDate;
        CardIssuanceInfo cardIssuance = repoCardIssuance.findByUserAndCardType(corp.user(), CardType.GOWID).orElseGet(
                () -> CardIssuanceInfo.builder().issuanceDepth(IssuanceDepth.SIGNUP).build()
        );

        if(cardIssuance.card().requestCount() != null ){
            data.setCardCount(cardIssuance.card().requestCount().toString());
        }

        if(cardIssuance.issuanceDepth() != null){
            data.setIssuanceDepth(cardIssuance.issuanceDepth().toString());
        }

        if(cardIssuance.issuanceStatus() != null){
            data.setCardIssuance(cardIssuance.issuanceStatus());
        }

        data.setRegisterDate(corp.resRegisterDate());
        data.setUserName(corp.user().name());
        data.setPhoneNumber(corp.user().mdn());
        data.setEmail(corp.user().email());
        data.setIsSendEmail(corp.user().reception().getIsSendEmail());
        data.setIsSendSms(corp.user().reception().getIsSendSms());
        data.setHopeLimit(corp.riskConfig().hopeLimit());
        data.setGrantLimit(corp.riskConfig().grantLimit());

        return ResponseEntity.ok().body(BusinessResponse.builder().data(data).build());
    }
}