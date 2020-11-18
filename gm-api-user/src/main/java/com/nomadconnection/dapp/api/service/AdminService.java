package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.UnauthorizedException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepositoryImpl;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchListRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.AuthorityRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nomadconnection.dapp.core.domain.common.IssuanceProgressType.LP_ZIP;
import static com.nomadconnection.dapp.core.domain.common.IssuanceProgressType.P_1800;
import static com.nomadconnection.dapp.core.domain.common.IssuanceStatusType.SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AdminService {

    private final EmailConfig config;
    private final ITemplateEngine templateEngine;
    private final JwtService jwt;
    private final JavaMailSenderImpl sender;
    private final UserService serviceUser;
    private final UserRepository repoUser;
    private final RiskRepository repoRisk;
    private final CorpRepository repoCorp;
    private final AuthorityRepository repoAuthority;
    private final ResAccountRepository repoResAccount;
    private final ResBatchRepository repoResBatch;
    private final ResBatchListRepository repoResBatchList;
    private final ResAccountHistoryRepository resAccountHistoryRepository;
    private final RiskConfigRepository repoRiskConfig;
    private final CardIssuanceInfoRepository repoCardIssuance;
    private final IssuanceProgressRepository repoIssuanceProgress;


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

    /**
     * admin page 의 risk 리스트
     *
     * @param riskDto  .
     * @param idxUser  .
     * @param pageable .
     * @return .
     */
    public ResponseEntity riskList(AdminCustomRepository.SearchRiskDto riskDto, Long idxUser, Pageable pageable) {
        Boolean isMaster = isGowidMaster(idxUser);

        if (!isMaster) {
            riskDto.setIdxCorpName("");
        }

        Page<AdminDto.RiskDto> resAccountPage = repoRisk.riskList(riskDto, idxUser, pageable).map(AdminDto.RiskDto::from);

        return ResponseEntity.ok().body(
                BusinessResponse.builder().data(resAccountPage).build()
        );
    }

    public ResponseEntity riskIdNowbalance(Long idxUser, Long idxCorp) {

        Boolean isMaster = isGowidMaster(idxUser);

        Double doubleBalance = repoResAccount.findNowBalance(idxCorp);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(
                AdminDto.RiskBalanceDto.builder().riskBalance(doubleBalance).build()
        ).build());
    }


    public ResponseEntity corpList(CorpCustomRepository.SearchCorpDto corpDto, Long idxUser, Pageable pageable) {

        Boolean isMaster = isGowidMaster(idxUser);

        Page<CorpCustomRepository.SearchCorpResultDto> page = repoCorp.corpList(corpDto, idxUser, pageable);

        page.forEach(
                searchCorpResultDto -> {
                    if(repoResBatchList.countByErrCodeNotAndResBatchTypeAndIdxResBatch(
                            "CF-00000",
                            "1",
                            repoResBatch.getMaxIdx(searchCorpResultDto.idx)) > 0 ){
                        searchCorpResultDto.setBoolError(true);
                    }else{
                        searchCorpResultDto.setBoolError(false);
                    }
                });

        return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity riskIdLevelChange(Long idxUser, RiskDto.RiskConfigDto dto) {

        Boolean isMaster = isGowidMaster(idxUser);

        RiskConfig riskConfig = repoRiskConfig.findByCorpAndEnabled(Corp.builder().idx(dto.idxCorp).build(), true)
                .orElseThrow(
                        () -> CorpNotRegisteredException.builder().account(dto.idxCorp.toString()).build()
                );

        riskConfig.enabled(true);
        riskConfig.ceoGuarantee(dto.isCeoGuarantee());
        riskConfig.ventureCertification(dto.isVentureCertification());
        riskConfig.vcInvestment(dto.isVcInvestment());
        riskConfig.depositPayment(dto.isDepositPayment());
        riskConfig.depositGuarantee(dto.getDepositGuarantee());

        return ResponseEntity.ok().body(
                BusinessResponse.builder()
                        .data(repoRiskConfig.save(riskConfig)
                ).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity saveEmergencyStop(Long idxUser, Long idxCorp, String booleanValue) {

        Boolean isMaster = isGowidMaster(idxUser);

        String calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

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

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity savePause(Long idxUser, Long idxCorp, String booleanValue) {

        Boolean isMaster = isGowidMaster(idxUser);

        String calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

        Risk risk = repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), calcDate).orElseThrow(
                () -> new RuntimeException("Empty Data")
        );

        if (booleanValue != null) {
            if (booleanValue.toLowerCase().equals("true")) {
                risk.pause(true);
            } else {
                risk.pause(false);
            }
        }

        repoRisk.save(risk);

        return ResponseEntity.ok().body(BusinessResponse.builder().build());
    }

    public ResponseEntity riskListSelected(AdminCustomRepository.SearchRiskDto riskDto, Long idx, Long idxCorp, Pageable pageable) {

        Boolean isMaster = isGowidMaster(idx);

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                () -> new RuntimeException("Bad idxCorp request.")
        );

        Page<RiskDto> result = repoRisk.findByCorp(corp, pageable).map(RiskDto::from);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(result).build());
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

        CardIssuanceInfo cardIssuance = repoCardIssuance.findTopByUserAndDisabledFalseOrderByIdxDesc(user).orElseGet(
                () -> CardIssuanceInfo.builder().issuanceDepth("0").build()
        );

        RiskDto.RiskConfigDto riskConfig = repoRiskConfig.findByCorpAndEnabled(corp, true).map(RiskDto.RiskConfigDto::from)
                .orElseGet(
                        () -> RiskDto.RiskConfigDto.builder().build()
                );


        AdminDto.corpInfoDetailId corpInfo = AdminDto.corpInfoDetailId.builder()
                .corpDto(corpDto)
                .cardCompany(user.cardCompany())
                .depth(cardIssuance.issuanceDepth())
                .hopeLimit(riskConfig.hopeLimit)
                .grantLimit(riskConfig.grantLimit)
                .userName(user.name())
                .userNumber(user.mdn())
                .userEmail(user.email())
                .smsFlag(user.isSendSms())
                .emailFlag(user.isSendEmail())
                .build();

        return ResponseEntity.ok().body(BusinessResponse.builder().data(corpInfo).build());
    }

    /**
     * 현금흐름 리스트
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity cashList(Long idx, String corpName, String updateStatus, Pageable pageable) {

        Boolean isMaster = isGowidMaster(idx);
        Page<AdminDto.CashListDto> returnData = null;

        if (isMaster) {
            if (updateStatus != null) {
                if (updateStatus.equals("true")) {
                    returnData = repoResAccount.cashList(corpName, true, pageable).map(AdminDto.CashListDto::from);
                } else if (updateStatus.equals("false")) {
                    returnData = repoResAccount.cashList(corpName, false, pageable).map(AdminDto.CashListDto::from);
                }

            } else {
                returnData = repoResAccount.cashList(corpName, null, pageable).map(AdminDto.CashListDto::from);
            }

            returnData.getContent().stream().filter(x -> x.getIdxUser() != null).forEach(this::accept);
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().data(returnData).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity cashIdList(Long idxUser, Long idxCorp) {

        Boolean isMaster = isGowidMaster(idxUser);

        List<AdminDto.CashListDetailDto> transactionList = null;

        if (idxCorp != null && isMaster) {
            Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                    () -> CorpNotRegisteredException.builder().account(idxCorp.toString()).build()
            );

            log.debug(" user idx " + corp.user().idx());
            String startDate = GowidUtils.getMonth(-11);
            String endDate = GowidUtils.getMonth(0);

            transactionList = repoResAccount.findMonthHistory(startDate, endDate, corp.user().idx()).stream().map(AdminDto.CashListDetailDto::from).collect(Collectors.toList());

        }

        return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
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

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity scrapingUpdate(Long idx, Long idxCorp) {
        Boolean isMaster = isGowidMaster(idx);
        int result = 0;
        if (isMaster) {
            Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                    () -> new RuntimeException("idxCopr Check")
            );

            result = repoResBatch.endBatchUser(idxCorp);
        }
        return ResponseEntity.ok().body(BusinessResponse.builder().data(result).build());
    }

    private void accept(AdminDto.CashListDto x) {
        List<Long> firstBalance = repoResAccount.findBalance(x.getIdxUser());
        Long longFirstBalance = firstBalance.get(0);
        Long longEndBalance = firstBalance.get(3);
        Long BurnRate = (longFirstBalance - longEndBalance) / 3;
        int intMonth = 1;
        if (BurnRate > 0) {
            intMonth = (int) Math.ceil((double) longEndBalance / BurnRate);
        }
        x.setBurnRate(BurnRate);
        x.setRunWay(intMonth);
    }

    public ResponseEntity originalList(AdminCustomRepository.RiskOriginal riskOriginal, Long idxUser, Pageable pageable) {

        Boolean isMaster = isGowidMaster(idxUser);

        Page<AdminDto.RiskNewDto> resAccountPage = repoRisk.riskList(riskOriginal, idxUser, pageable).map(AdminDto.RiskNewDto::from);

        return ResponseEntity.ok().body(
                BusinessResponse.builder().data(resAccountPage).build()
        );
    }

    public ResponseEntity cardComTransInfo(AdminCustomRepository.RiskOriginal riskOriginal, Long idxUser, Pageable pageable) {

        Boolean isMaster = isGowidMaster(idxUser);

        Page<AdminDto.RiskDto> resAccountPage = repoRisk.riskTransList(riskOriginal, idxUser, pageable).map(AdminDto.RiskTransDto::from);

        return ResponseEntity.ok().body(
                BusinessResponse.builder().data(resAccountPage).build()
        );
    }

    public ResponseEntity originalListGrant(Long idxUser, RiskDto.CardList dto) {

        Boolean isMaster = isGowidMaster(idxUser);

        if(!isMaster){
            throw UnauthorizedException.builder().build();
        }

        for( Long idxCorp : dto.getIdxCorp()){
            repoRisk.updateRiskIdxCorpCardIssuance(dto.boolCardGrant, idxCorp);
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().normal(BusinessResponse.Normal.builder().status(true).build()).build());
    }

    public ResponseEntity adminCorpList(CorpCustomRepository.SearchCorpListDto dto, Long idxUser, Pageable pageable) {
        Boolean isMaster = isGowidMaster(idxUser);

        Page<CorpCustomRepository.SearchCorpResultDto> page = repoCorp.adminCorpList(dto, idxUser, pageable);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
    }

    public ResponseEntity scrapCorpList(CorpCustomRepository.ScrapCorpDto dto, Long idxUser, Pageable pageable) {
        Boolean isMaster = isGowidMaster(idxUser);

        if(StringUtils.isEmpty(dto.getUpdatedAt())){
            dto.setUpdatedAt(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }

        Page<CorpCustomRepository.ScrapCorpListDto> page = repoCorp.scrapCorpList(dto, pageable);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
    }

    public ResponseEntity scrapCorp(Long idx, Long idxCorp) {

        // return ResponseEntity.ok().body(BusinessResponse.builder().data(list).normal(BusinessResponse.Normal.builder().status(true).build()).build());
        return null;
    }

    public ResponseEntity scrapAccountList(ResBatchListCustomRepository.ScrapAccountDto dto, Long idxUser, Pageable pageable) {
        Boolean isMaster = isGowidMaster(idxUser);

        Page<ResBatchListCustomRepository.ScrapAccountListDto> page = repoResBatchList.scrapAccountList(dto, pageable);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
    }

    public ResponseEntity scrapAccount(Long idx, String account) {
        // return ResponseEntity.ok().body(BusinessResponse.builder().data(list).normal(BusinessResponse.Normal.builder().status(true).build()).build());
        return null;
    }

    @Transactional(readOnly = true)
    public ResponseEntity corpInfo(Long idxCorp, Long idxUser) {
        Boolean isMaster = isGowidMaster(idxUser);
        AdminDto.corpInfoDetail data = new AdminDto.corpInfoDetail();

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                () -> new RuntimeException("Bad idxCorp request.")
        );

        data.setCardCompany(repoCorp.findById(idxCorp).get().user().cardCompany());

        if (isMaster) {
            String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
            repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), date).ifPresent(
                    risk -> {
                        data.setDepositGuarantee(risk.depositGuarantee());
                        data.setDma45(risk.dma45());
                        data.setDmm45(risk.dmm45());
                        data.setCashBalance(risk.cashBalance());
                        data.setCurrentBalance(risk.currentBalance());
                        data.setMinCashNeed(risk.minCashNeed());
                        data.setCardLimitNow(risk.cardLimitNow());
                        data.setCardRestartCount(risk.cardRestartCount());
                        data.setCardType(risk.cardType());
                        data.setCardIssuance(risk.cardIssuance());
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

            CardIssuanceInfo cardIssuance = repoCardIssuance.findTopByUserAndDisabledFalseOrderByIdxDesc(corp.user()).orElseGet(
                    () -> CardIssuanceInfo.builder().issuanceDepth("0").build()
            );

            if(cardIssuance.card().requestCount() != null ){
                data.setCardCount(cardIssuance.card().requestCount().toString());
            }

            if(cardIssuance.issuanceDepth() != null){
                data.setIssuanceDepth(cardIssuance.issuanceDepth());
            }

            IssuanceProgress issuanceProgress = repoIssuanceProgress.findById(corp.user().idx()).orElse(null);

            if( issuanceProgress != null ){
                data.setApplyDate(issuanceProgress.getCreatedAt());
                data.setDecisionDate(issuanceProgress.getUpdatedAt());
            }

            data.setRegisterDate(corp.resRegisterDate());
            data.setUserName(corp.user().name());
            data.setPhoneNumber(corp.user().mdn());
            data.setEmail(corp.user().email());
            data.setIsSendEmail(corp.user().isSendEmail());
            data.setIsSendSms(corp.user().isSendSms());

            data.setHopeLimit(corp.riskConfig().hopeLimit());
            data.setGrantLimit(corp.riskConfig().grantLimit());
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().data(data).build());
    }

    private boolean isIssuanceSuccess(String progress, String status){
        return (P_1800.equals(progress) || LP_ZIP.equals(progress)) && SUCCESS.equals(status);
    }

    public ResponseEntity riskConfigStop(Long idx, AdminDto.StopDto dto) {
        // return ResponseEntity.ok().body(BusinessResponse.builder().data(list).normal(BusinessResponse.Normal.builder().status(true).build()).build());
        return null;
    }

    public ResponseEntity cardComTransInfoGrant(Long idx, AdminDto.cardComTransInfoGrant dto) {
        // return ResponseEntity.ok().body(BusinessResponse.builder().data(list).normal(BusinessResponse.Normal.builder().status(true).build()).build());
        return null;
    }
}