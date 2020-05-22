package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    private Boolean isGowidMaster(Long idxUser) {

        boolean boolV = false;

        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );

        if (user.authorities().stream().noneMatch(o ->
                (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER))))
            throw UserNotFoundException.builder().build();

        if (user.authorities().stream().anyMatch(o -> o.role().equals(Role.GOWID_ADMIN))) boolV = true;

        return boolV;
    }

    private Integer intGowidMaster(Long idxUser) {

        int iReturn = 0;

        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );

        if (user.authorities().stream().noneMatch(o ->
                (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))) iReturn = 1;

        if (user.authorities().stream().anyMatch(o -> o.role().equals(Role.GOWID_ADMIN))) iReturn = 2;

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

        if (!isMaster) riskDto.setIdxCorpName("");

        Page<AdminDto.RiskDto> resAccountPage = repoRisk.riskList(riskDto, idxUser, pageable).map(AdminDto.RiskDto::from);

        if (!isMaster)
            for (AdminDto.RiskDto x : resAccountPage.getContent()) x.setIdxCorpName("#" + x.idxCorp);

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

        if (!isMaster)
            for (CorpCustomRepository.SearchCorpResultDto x : page.getContent()) x.setResCompanyNm("#" + x.idx);

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

        return ResponseEntity.ok().body(BusinessResponse.builder().data(repoRiskConfig.save(riskConfig)).build());
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

        repoRisk.save(risk);

        return ResponseEntity.ok().body(BusinessResponse.builder().build());
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

    public ResponseEntity corpId(Long idx, Long idxCorp) {

        CorpDto corp = repoCorp.findById(idxCorp).map(CorpDto::from).orElseThrow(
                () -> CorpNotRegisteredException.builder().build()
        );

        //todo 권한별 데이터 변경
        System.out.println( intGowidMaster(idx) );
        System.out.println( intGowidMaster(idx) < 2 );
        System.out.println( idxCorp != null );
        if (intGowidMaster(idx) < 2 && idxCorp != null) {
            corp.setResCompanyNm(null);
            corp.setResCompanyIdentityNo(null);
            corp.setResUserNm(null);
            //todo 공동사업자 1 , 주민(사업자) 등록번호
            //todo 공동사업자 2 , 주민(사업자) 등록번호
            //todo 발급번호
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().data(corp).build());
    }

    /**
     * 현금흐름 리스트
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity cashList(Long idx, String corpName, String updateStatus, Pageable pageable) {

        Boolean isMaster = isGowidMaster(idx);
        Page<AdminDto.CashListDto> returnData = null;

        if (isMaster) {
            if(updateStatus != null ){
                if(updateStatus.equals("true")) returnData = repoResAccount.cashList(corpName, true, pageable).map(AdminDto.CashListDto::from);
                else if(updateStatus.equals("false")) returnData = repoResAccount.cashList(corpName, false, pageable).map(AdminDto.CashListDto::from);

            }else {
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

            if ( intMaster < 2 )
                for (AdminDto.ScrapingListDto x : resAccountPage.getContent()) x.setIdxCorpName("#" + x.idxCorp);
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

    public ResponseEntity errorList(Long idx, Pageable pageable, ResBatchListCustomRepository.ErrorSearchDto dto) {
        Boolean isMaster = isGowidMaster(idx);

        String toDay = dto.getBoolToday();
        if(toDay != null && toDay.equals("true")){
            toDay = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }else{
            toDay = "20100000";
        }

        Page<AdminDto.ErrorResultDto> list = repoResBatchList.errorList(dto.getCorpName(), dto.getErrorCode() ,dto.getTransactionId(), toDay, dto.getIdxCorp(), pageable).map(AdminDto.ErrorResultDto::from);

        if (!isMaster)
            for (AdminDto.ErrorResultDto errorResultDto : list)
                errorResultDto.setCorpName("#" + errorResultDto.idxCorp);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(list).normal(BusinessResponse.Normal.builder().status(true).build()).build());
    }

    private void accept(AdminDto.CashListDto x) {
        List<Long> firstBalance = repoResAccount.findBalance(x.getIdxUser());
        Long longFirstBalance = firstBalance.get(0);
        Long longEndBalance = firstBalance.get(3);
        Long BurnRate = (longFirstBalance - longEndBalance) / 3;
        int intMonth = 1;
        if (BurnRate > 0) intMonth = (int) Math.ceil((double) longEndBalance / BurnRate);
        x.setBurnRate(BurnRate);
        x.setRunWay(intMonth);
    }
}