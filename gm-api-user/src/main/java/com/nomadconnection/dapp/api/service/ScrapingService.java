package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.sandbox.bk.*;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ScrapingService {


    private final EmailConfig config;
    private final ITemplateEngine templateEngine;
    private final JavaMailSenderImpl sender;
	private final CorpRepository repoCorp;
    private final UserRepository repoUser;
    private final ResAccountRepository repoResAccount;
    private final ResAccountHistoryRepository repoResAccountHistory;
    private final ResBatchListRepository repoResBatchList;
    private final ResBatchRepository repoResBatch;
    private final ConnectedMngRepository repoConnectedMng;

    private final RiskService serviceRisk;

    private final PasswordEncoder encoder;
    private final VerificationCodeRepository repoVerificationCode;

    private final String urlPath = CommonConstant.getRequestDomain();

    /**
     * find connectedId
     *
     * @param idx
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ConnectedMng> getConnectedMng(Long idx) {
        return repoConnectedMng.findByIdxUser(idx);
    }


    @Transactional(rollbackFor = Exception.class)
    void saveAccount(int iType, JSONObject jsonData, JSONArray jsonArrayResTrHistoryList, String connectedId,  BankDto.AccountBatch dto, String nowFlag) {
        String strDefault = null;
        repoResAccountHistory.deleteResAccountTrDate(jsonData.get("resAccount").toString(), dto.getStartDate(), dto.getEndDate());

        //   10 :실시간 적금  40:대출  20:외화  30:펀드
        if (iType == 10) {
            ResAccount resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString()).get();

            // 해당 달일경우 계좌에 안들어오는 정보를 넣어주기위한 로직
            if (nowFlag.equals("1")) {
                if (!jsonData.get("resAccountStartDate").toString().isEmpty()) {
                    resAccount.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                }
                double balance = 0;
                if (jsonData.get("resAccountBalance") != "")
                    balance = Double.parseDouble(jsonData.get("resAccountBalance").toString());
                resAccount.resAccountBalance(balance);
                resAccount.resAccountRiskBalance(balance);

                repoResAccount.save(resAccount);
            }

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate(GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"))
                                    .resAccountTrTime(GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"))
                                    .resAccountOut(GowidUtils.getEmptyStringToString(obj, "resAccountOut"))
                                    .resAccountIn(GowidUtils.getEmptyStringToString(obj, "resAccountIn"))
                                    .resAccountDesc1(GowidUtils.getEmptyStringToString(obj, "resAccountDesc1"))
                                    .resAccountDesc2(GowidUtils.getEmptyStringToString(obj, "resAccountDesc2"))
                                    .resAccountDesc3(GowidUtils.getEmptyStringToString(obj, "resAccountDesc3"))
                                    .resAccountDesc4(GowidUtils.getEmptyStringToString(obj, "resAccountDesc4"))
                                    .resAfterTranBalance(GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"))
                                    .resAccount(resAccount.resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 12) {
            ResAccount resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString()).get();
            if (nowFlag.equals("1")) {
                if (!jsonData.get("resAccountStartDate").toString().isEmpty()) {
                    resAccount.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                }
                double balance = 0;
                if (jsonData.get("resAccountBalance") != "")
                    balance = Double.parseDouble(jsonData.get("resAccountBalance").toString());
                resAccount.resAccountBalance(balance);
                resAccount.resAccountRiskBalance(balance);
                repoResAccount.save(resAccount);
            }

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resRoundNo(GowidUtils.getEmptyStringToString(obj, "resRoundNo"))
                                    .resMonth(GowidUtils.getEmptyStringToString(obj, "resMonth"))
                                    .resAccountTrDate( GowidUtils.getEmptyStringToString(obj, "resAccountTrDate").toString())
                                    .resAccountIn( GowidUtils.getEmptyStringToString(obj, "resAccountIn").toString())
                                    .resAccountDesc1( GowidUtils.getEmptyStringToString(obj, "resAccountDesc1").toString())
                                    .resAccountDesc2( GowidUtils.getEmptyStringToString(obj, "resAccountDesc2").toString())
                                    .resAccountDesc3( GowidUtils.getEmptyStringToString(obj, "resAccountDesc3").toString())
                                    .resAccountDesc4( GowidUtils.getEmptyStringToString(obj, "resAccountDesc4").toString())
                                    .resAfterTranBalance( GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance").toString())
                                    .resAccount(resAccount.resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 40) {
            ResAccount resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString()).get();
            if (nowFlag.equals("1")) {
                if (!jsonData.get("resAccountStartDate").toString().isEmpty()) {
                    resAccount.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                    repoResAccount.save(resAccount);
                }
            }

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate( GowidUtils.getEmptyStringToString(obj, "resAccountTrDate").toString())
                                    .resTransTypeNm( GowidUtils.getEmptyStringToString(obj, "resTransTypeNm").toString())
                                    .resType( GowidUtils.getEmptyStringToString(obj, "resType").toString())
                                    .resTranAmount( GowidUtils.getEmptyStringToString(obj, "resTranAmount").toString())
                                    .resPrincipal( GowidUtils.getEmptyStringToString(obj, "resPrincipal").toString())
                                    .resInterest( GowidUtils.getEmptyStringToString(obj, "resInterest").toString())
                                    // .resOverdueInterest(GowidUtils.getEmptyStringToString(obj,"resOverdueInterest").toString())
                                    // .resReturnInterest(GowidUtils.getEmptyStringToString(obj,"resReturnInterest").toString())
                                    // .resFee(GowidUtils.getEmptyStringToString(obj,"resFee").toString())
                                    .commStartDate( GowidUtils.getEmptyStringToString(obj, "commStartDate").toString())
                                    .commEndDate( GowidUtils.getEmptyStringToString(obj, "commEndDate").toString())
                                    .resLoanBalance( GowidUtils.getEmptyStringToString(obj, "resLoanBalance").toString())
                                    .resInterestRate( GowidUtils.getEmptyStringToString(obj, "resInterestRate").toString())
                                    .resAccount(resAccount.resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 30) {
            ResAccount resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString()).get();
            if (nowFlag.equals("1")) {
                if (!jsonData.get("resAccountStartDate").toString().isEmpty()) {
                    resAccount.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                }
                double balance = 0;
                if (jsonData.get("resAccountBalance") != null ||  jsonData.get("resAccountBalance") != "") {
                    try {
                        log.debug("resAccountBalance $resAccountBalance='{}'" + jsonData.get("resAccountBalance"));
                        balance = Double.parseDouble(jsonData.get("resAccountBalance").toString());
                    }catch (Exception e){
                        log.debug("ScrapingServer saveAccount >");
                        e.printStackTrace();
                    }
                }
                resAccount.resAccountBalance(balance);
                resAccount.resAccountRiskBalance(balance);
                repoResAccount.save(resAccount);
            }

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate( GowidUtils.getEmptyStringToString(obj, "resAccountTrDate").toString())
                                    .resAccountTrTime( GowidUtils.getEmptyStringToString(obj, "resAccountTrTime").toString())
                                    .resTranAmount( GowidUtils.getEmptyStringToString(obj, "resTranAmount").toString())
                                    .resTranNum( GowidUtils.getEmptyStringToString(obj, "resTranNum").toString())
                                    .resBasePrice( GowidUtils.getEmptyStringToString(obj, "resBasePrice").toString())
                                    .resBalanceNum( GowidUtils.getEmptyStringToString(obj, "resBalanceNum").toString())
                                    .resAccountDesc1( GowidUtils.getEmptyStringToString(obj, "resAccountDesc1").toString())
                                    .resAccountDesc2( GowidUtils.getEmptyStringToString(obj, "resAccountDesc2").toString())
                                    .resAccountDesc3( GowidUtils.getEmptyStringToString(obj, "resAccountDesc3").toString())
                                    .resAccountDesc4( GowidUtils.getEmptyStringToString(obj, "resAccountDesc4").toString())
                                    .resAfterTranBalance( GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance").toString())
                                    .resValuationAmt("" + GowidUtils.getEmptyStringToString(obj, "resValuationAmt") )
                                    .resAccount(resAccount.resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 20) {
            // 외화 20
            ResAccount resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString()).get();
            if (nowFlag.equals("1")) {
                resAccount.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                double balance = 0;
                if (jsonData.get("resAccountBalance") != "")
                    balance = Double.parseDouble(jsonData.get("resAccountBalance").toString());
                resAccount.resAccountBalance(balance);
                resAccount.resAccountRiskBalance(balance);
                repoResAccount.save(resAccount);
            }

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate( GowidUtils.getEmptyStringToString(obj, "resAccountTrDate").toString())
                                    .resAccountTrTime( GowidUtils.getEmptyStringToString(obj, "resAccountTrTime").toString())
                                    .resAccountOut( GowidUtils.getEmptyStringToString(obj, "resAccountOut").toString())
                                    .resAccountIn( GowidUtils.getEmptyStringToString(obj, "resAccountIn").toString())
                                    .resAccountDesc1( GowidUtils.getEmptyStringToString(obj, "resAccountDesc1").toString())
                                    .resAccountDesc2( GowidUtils.getEmptyStringToString(obj, "resAccountDesc2").toString())
                                    .resAccountDesc3( GowidUtils.getEmptyStringToString(obj, "resAccountDesc3").toString())
                                    .resAccountDesc4( GowidUtils.getEmptyStringToString(obj, "resAccountDesc4").toString())
                                    .resAfterTranBalance( GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance").toString())
                                    .resAccount(resAccount.resAccount())
                                    .build()
                    );
                });
            }
        }
    }


    /**
     * scraping Data
     *
     * @param account
     * @param connectedId
     * @param resBatchType
     * @param idxResBatch
     * @param idxUser
     * @return
     */
    @Transactional
    public Long startLog(String account, String connectedId, ResBatchType resBatchType, Long idxResBatch, Long idxUser) {
        ResBatchList result = repoResBatchList.save(
                ResBatchList.builder()
                        .idxUser(idxUser)
                        .account(account)
                        .connectedId(connectedId)
                        .resBatchType(resBatchType)
                        .idxResBatch(idxResBatch)
                        .build());
        return result.idx();
    }

    @Transactional
    public void endLog(ResBatchList resBatchList) {
        repoResBatchList.findById(resBatchList.idx()).ifPresent(resBatch -> {
            repoResBatchList.save(
                    ResBatchList.builder()
                            .idx(resBatchList.idx())
                            .account(resBatchList.account())
                            .bank(resBatchList.bank())
                            .errCode(resBatchList.errCode())
                            .errMessage(resBatchList.errMessage())
                            .startDate(resBatchList.startDate())
                            .endDate(resBatchList.endDate())
                            .transactionId(resBatchList.transactionId())
                            .idxUser(resBatch.idxUser())
                            .connectedId(resBatch.connectedId())
                            .resBatchType(resBatch.resBatchType())
                            .idxResBatch(resBatch.idxResBatch())
                            .build());
        });
    }

    private JSONObject[] getApiResult(String str) throws ParseException {
        JSONObject[] result = new JSONObject[2];

        JSONParser jsonParse = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParse.parse(str);

        result[0] = (JSONObject) jsonObject.get("result");
        result[1] = (JSONObject) jsonObject.get("data");

        return result;
    }


    /**
     * (기간별) 일별 입출금 잔고
     *
     * @param idx 엔터티(사용자)
     * @param dto 보유정보
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity dayBalance(BankDto.DayBalance dto, Long idx) {

        String strDate = dto.getDay();
        if (strDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Calendar c1 = Calendar.getInstance();
            strDate = sdf.format(c1.getTime());
        }

        List<ResAccountRepository.CaccountCountDto> transactionList = repoResAccount.findDayHistory(strDate.substring(0, 6) + "00", strDate.substring(0, 6) + "32", idx);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
    }

    /**
     * (기간별) 월별 입출금 잔고
     *
     * @param idx 엔터티(사용자)
     * @param dto 보유정보
     */
    public ResponseEntity monthBalance(BankDto.MonthBalance dto, Long idx) {

        String startDate = dto.getMonth();
        String endDate = dto.getMonth();
        if (startDate == null) startDate = GowidUtils.getMonth(-11);
        if (endDate == null) endDate = GowidUtils.getMonth(0);

        List<ResAccountRepository.CaccountMonthDto> transactionList = repoResAccount.findMonthHistory(startDate, endDate, idx);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
    }

    /**
     * 거래내역
     *
     * @param dto 보유정보
     * @param idx 엔터티(사용자)
     */
    public ResponseEntity transactionList(BankDto.TransactionList dto, Long idx, Integer page, Integer pageSize) {
        String strDate = dto.getSearchDate();

        List<ResAccountRepository.CaccountHistoryDto> transactionList;

        Integer intIn = 0, intOut = 0, booleanForeign = 0;
        if (dto.getResInOut() != null) {
            if (dto.getResInOut().toLowerCase().equals("in")) {
                intIn = 1;
                intOut = 0;
            } else if (dto.getResInOut().toLowerCase().equals("out")) {
                intIn = 0;
                intOut = 1;
            }
        }
        if (dto.getBoolForeign() != null) {
            if (dto.getBoolForeign()) {
                booleanForeign = 1;
            }
        }


        if (strDate != null && strDate.length() == 6) {
            transactionList = repoResAccount.findAccountHistory(strDate + "00", strDate + "32", dto.getResAccount(), idx, pageSize, pageSize * (page - 1), intIn, intOut, booleanForeign);
        } else {
            transactionList = repoResAccount.findAccountHistory(strDate, strDate, dto.getResAccount(), idx, pageSize, pageSize * (page - 1), intIn, intOut, booleanForeign);
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
    }

    final Thread currentThread = Thread.currentThread();


    @Async
    public void scrapingRegister1YearAll(Long idxUser, Long idxCorp) {
        log.debug("scrapingRegister");
        ResBatch idxLog = startBatchLog(idxUser);
        try {
            scrapingRegister1YearAll(idxUser, idxLog.idx(), null );
        } finally {
            endBatchLog(idxLog.idx());
        }
    }


    public boolean aWaitcrapingRegister1YearAll(Long idxUser, Long idxCorp) {
        log.debug("scrapingRegister");
        ResBatch idxLog = startBatchLog(idxUser);
        try {
            scrapingRegister1YearAll(idxUser, idxLog.idx(), idxCorp );
        } finally {
            endBatchLog(idxLog.idx());
        }
        return true;
    }


    /**
     * 새로고침 계좌 + 거래내역
     *
     * @param idx .
     * @param idxResBatchParent .
     */
    @Async
    public void scrapingRegister1YearAll(Long idx, Long idxResBatchParent, Long idxCorp) {

        //todo auth
        if( idxCorp != null ){
            log.debug("1");
            if(repoUser.findById(idx).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN)))){
                Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                        () -> CorpNotRegisteredException.builder().account(idxCorp.toString()).build()
                );
                idx = repoCorp.searchIdxUser(idxCorp);
            }
        }

        List<ConnectedMng> connectedMng = getConnectedMng(idx);

        log.debug("start scrapingRegister1Year $idxResBatchParent={}", idxResBatchParent);

        Long finalIdx = idx;
        connectedMng.forEach(mngItem -> {
            String connId = mngItem.connectedId();
            JSONParser jsonParse = new JSONParser();

            for (String strBank : CommonConstant.LISTBANK) {

                String code = null, message = null;
                JSONObject[] strResult = new JSONObject[0];
                Long idxResBatchList = startLog(null, connId, ResBatchType.BANK, idxResBatchParent, finalIdx);

                if (repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                    throw new RuntimeException("process kill");
                }

                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLog(ResBatchList.builder()
                            .idx(idxResBatchList)
                            .bank(strBank)
                            .errCode(strResult[0].get("code").toString())
                            .errMessage(strResult[0].get("message").toString())
                            .transactionId(strResult[0].get("transactionId").toString())
                            .build());
                }

                if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("message").toString().equals("CF-04012")) {

                    JSONObject jsonData = strResult[1];
                    JSONArray jsonArrayResDepositTrust = (JSONArray) jsonData.get("resDepositTrust");
                    JSONArray jsonArrayResForeignCurrency = (JSONArray) jsonData.get("resForeignCurrency");
                    JSONArray jsonArrayResFund = (JSONArray) jsonData.get("resFund");
                    JSONArray jsonArrayResLoan = (JSONArray) jsonData.get("resLoan");

                    jsonArrayResDepositTrust.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("DepositTrust")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .build()
                        );
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("Loan")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .resAccountLoanExecNo( GowidUtils.getEmptyStringToString(obj, "resAccountLoanExecNo").toString())
                                .build()
                        );
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("ResForeignCurrency")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .build()
                        );
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("ResFund")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resAccountInvestedCost( GowidUtils.getEmptyStringToString(obj, "resAccountInvestedCost").toString())
                                .resEarningsRate( GowidUtils.getEmptyStringToString(obj, "resEarningsRate").toString())
                                .build()
                        );
                    });
                }
            }
        });

        // user ConnectedId List
        List<ResBatchRepository.CResYears> list = repoResBatch.findStartDateMonth(idx);

        // ConnId 의 계좌분류별 스크랩
        for (ResBatchRepository.CResYears resData : list) {

            int iType = 0;
            String strType = resData.getResAccountDeposit();

            String strStart, strEnd;

            strStart = resData.getStartDay();
            strEnd = resData.getEndDay();

            if (strType.equals("10") || strType.equals("11")) {
                iType = 10;
            } else if (strType.equals("12") || strType.equals("13") || strType.equals("14")) {
                iType = 12;
            } else if (strType.equals("40")) {
                iType = 40;
            } else if (strType.equals("20")) {
                iType = 20;
            } else if (strType.equals("30")) {
                iType = 30;
            }

            JSONParser jsonParse = new JSONParser();
            JSONObject[] strResult = new JSONObject[0];

            if (iType == 0) continue;
            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

            if (repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                throw new RuntimeException("process kill");
            }

            try {
                if (strType.equals("10") || strType.equals("11")) {
                    strResult = this.getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , "1"));
                } else if (strType.equals("12") || strType.equals("13") || strType.equals("14")) {
                    strResult = this.getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , "1"));
                } else if (strType.equals("40")) {
                    strResult = this.getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , ""));
                } else if (strType.equals("30")) {
                    strResult = this.getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , "1"));
                } else if (strType.equals("20")) {
                    strResult = this.getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , resData.getResAccountCurrency()
                    ));
                }

                log.debug("([scrapingAccountHistory10year ]) $strResult='{}'", strResult.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            log.debug("([scrapingAccountHistory10year ]) $resData.getConnectedId()='{}'", resData.getConnectedId());

            if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                saveAccount(iType
                        , strResult[1]
                        , (JSONArray) strResult[1].get("resTrHistoryList")
                        , resData.getConnectedId()
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build()
                        , resData.getNowMonth()
                );
            } else {
                if (resData.getNowMonth().equals("1")) {
                    saveAccountError(resData.getResAccount());
                }
            }

            // 에러 상황에 대해 2번 반복 확인
            for (int i = 0; i < 2; i++) {
                saveAccountProcessBatchRetry(idx, idxResBatchParent);
            }
            // 리스크 데이터 저장
            //serviceRisk.saveRisk(idx, "");

            endLog(ResBatchList.builder()
                    .idx(idxResBatch)
                    .startDate(resData.getStartDay())
                    .endDate(resData.getEndDay())
                    .account(resData.getResAccount())
                    .bank(resData.getOrganization())
                    .errCode(strResult[0].get("code").toString())
                    .transactionId(strResult[0].get("transactionId").toString())
                    .errMessage(strResult[0].get("message").toString())
                    .build());
        }
    }

    @Async
    public void scrapingRegister1YearList(Long idxUser) {
        log.debug("scrapingRegister");
        ResBatch idxLog = startBatchLog(idxUser);
        try {
            scrapingRegisterAccount(idxUser, idxLog.idx());
        } finally {
            endBatchLog(idxLog.idx());
        }
    }

    @Async
    public void scrapingRegisterAccount(Long idx, Long idxResBatch) {

        log.debug("start scrapingAccountHistoryList $idxResBatch={}", idxResBatch);
        List<ResBatchRepository.CResYears> list = repoResBatch.findStartDateMonth(idx);

        // ConnId 의 계좌분류별 스크랩
        for (ResBatchRepository.CResYears resData : list) {
            int iType = 0;
            String strType = resData.getResAccountDeposit();
            String strStart = resData.getStartDay();
            String strEnd = resData.getEndDay();


            log.debug(" scrapingAccountHistoryList $account={}, $strStart={} , $strEnd={} ", resData.getResAccount(), strStart, strEnd);

            switch (strType) {
                case "10":
                case "11":
                    iType = 10;
                    break;
                case "12":
                case "13":
                case "14":
                    iType = 12;
                    break;
                case "40":
                    iType = 40;
                    break;
                case "20":
                    iType = 20;
                    break;
                case "30":
                    iType = 30;
                    break;
            }

            JSONParser jsonParse = new JSONParser();
            JSONObject[] strResult = new JSONObject[0];

            if (iType == 0) continue;
            Long idxResBatchList = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatch, idx);

            if (repoResBatch.findById(idxResBatch).get().endFlag()) {
                throw new RuntimeException("process kill");
            }

            try {
                switch (strType) {
                    case "10":
                    case "11":
                        strResult = this.getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "12":
                    case "13":
                    case "14":
                        strResult = this.getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "40":
                        strResult = this.getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , ""));
                        break;
                    case "30":
                        strResult = this.getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "20":
                        strResult = this.getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , resData.getResAccountCurrency()
                        ));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLog(ResBatchList.builder()
                        .idx(idxResBatchList)
                        .startDate(resData.getStartDay())
                        .endDate(resData.getEndDay())
                        .account(resData.getResAccount())
                        .bank(resData.getOrganization())
                        .errCode(strResult[0].get("code").toString())
                        .transactionId(strResult[0].get("transactionId").toString())
                        .errMessage(strResult[0].get("message").toString())
                        .build());
            }

            if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                saveAccount(iType
                        , strResult[1]
                        , (JSONArray) strResult[1].get("resTrHistoryList")
                        , resData.getConnectedId()
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build()
                        , resData.getNowMonth()
                );
            } else {
                if (resData.getNowMonth().equals("1")) {
                    saveAccountError(resData.getResAccount());
                }
            }
        }
    }


    @Transactional
    public ResBatch startBatchLog(Long userIdx) {
        return repoResBatch.save(ResBatch.builder()
                .idxUser(userIdx)
                .endFlag(false)
                .build());
    }

    @Transactional
    public void endBatchLog(Long idx) {
        repoResBatch.findById(idx).ifPresent(resBatch -> {
            repoResBatch.save(
                    ResBatch.builder()
                            .idx(idx)
                            .idxUser(resBatch.idxUser())
                            .endFlag(true)
                            .build());
        });
    }

    @Async
    public void scraping10Years(Long idx) {
        log.debug("scraping10Years");
        log.debug("scraping10Years parallelStream() $user={}", idx);
        ResBatch idxLog = startBatchLog(idx);
        try {
            scrapingAccountHistory10year(idx, idxLog.idx());
        } catch (Exception e) {
            log.debug("scraping10Years Exception");
            e.printStackTrace();
        } finally {
            endBatchLog(idxLog.idx());
        }
    }


    /**
     * 매일 밤 스크립트 처리
     *
     * @param idx
     * @param idxResBatchParent
     * @return
     */
    @Async
    public boolean scrapingAccountHistory10year(Long idx, Long idxResBatchParent) {

        log.debug("start scrapingAccountHistory10year $idxResBatchParent={}", idxResBatchParent);

        List<ConnectedMng> connectedMng = getConnectedMng(idx);

        connectedMng.forEach(mngItem -> {
            String connId = mngItem.connectedId();
            JSONParser jsonParse = new JSONParser();

            for (String strBank : CommonConstant.LISTBANK) {

                String code = null, message = null;
                JSONObject[] strResult = new JSONObject[0];
                Long idxResBatchList = startLog(null, connId, ResBatchType.BANK, idxResBatchParent, idx);

                if (repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                    throw new RuntimeException("process kill");
                }

                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLog(ResBatchList.builder()
                            .idx(idxResBatchList)
                            .bank(strBank)
                            .errCode(strResult[0].get("code").toString())
                            .transactionId(strResult[0].get("transactionId").toString())
                            .errMessage(strResult[0].get("message").toString())
                            .build());
                }

                if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("message").toString().equals("CF-04012")) {

                    JSONObject jsonData = strResult[1];
                    JSONArray jsonArrayResDepositTrust = (JSONArray) jsonData.get("resDepositTrust");
                    JSONArray jsonArrayResForeignCurrency = (JSONArray) jsonData.get("resForeignCurrency");
                    JSONArray jsonArrayResFund = (JSONArray) jsonData.get("resFund");
                    JSONArray jsonArrayResLoan = (JSONArray) jsonData.get("resLoan");

                    jsonArrayResDepositTrust.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("DepositTrust")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountHolder( GowidUtils.getEmptyStringToString(obj, "resAccountHolder").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .build()
                        );
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("Loan")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountHolder( GowidUtils.getEmptyStringToString(obj, "resAccountHolder").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .resAccountLoanExecNo( GowidUtils.getEmptyStringToString(obj, "resAccountLoanExecNo").toString())
                                .build()
                        );
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("ResForeignCurrency")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountHolder( GowidUtils.getEmptyStringToString(obj, "resAccountHolder").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .build()
                        );
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("ResFund")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountHolder( GowidUtils.getEmptyStringToString(obj, "resAccountHolder").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resAccountInvestedCost( GowidUtils.getEmptyStringToString(obj, "resAccountInvestedCost").toString())
                                .resEarningsRate( GowidUtils.getEmptyStringToString(obj, "resEarningsRate").toString())
                                .build()
                        );
                    });
                }
            }
        });

        saveAccountProcessBatch(idx, idxResBatchParent, true);

        return true;
    }

    private void saveAccountProcess(Long idx, Long idxResBatchParent, boolean boolRetry) {
        // user ConnectedId List
        List<ResBatchRepository.CResYears> list;
        if (!boolRetry) {
            list = repoResBatch.find10yearMonth(idx, true);
        } else {
            list = repoResBatch.find10yearMonth(idx, false);
        }

        // ConnId 의 계좌분류별 스크랩
        for (ResBatchRepository.CResYears resData : list) {
            int iType = 0;
            String strType = resData.getResAccountDeposit();

            String strStart, strEnd;

            strStart = resData.getStartDay();
            strEnd = resData.getEndDay();

            switch (strType) {
                case "10":
                case "11":
                    iType = 10;
                    break;
                case "12":
                case "13":
                case "14":
                    iType = 12;
                    break;
                case "40":
                    iType = 40;
                    break;
                case "20":
                    iType = 20;
                    break;
                case "30":
                    iType = 30;
                    break;
            }

            JSONParser jsonParse = new JSONParser();
            JSONObject[] strResult = new JSONObject[0];

            if (iType == 0) continue;
            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

            if (!repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                try {

                    switch (strType) {
                        case "10":
                        case "11":
                            strResult = this.getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "12":
                        case "13":
                        case "14":
                            strResult = this.getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "40":
                            strResult = this.getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , ""));
                            break;
                        case "30":
                            strResult = this.getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "20":
                            strResult = this.getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , resData.getResAccountCurrency()
                            ));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                    saveAccount(iType
                            , strResult[1]
                            , (JSONArray) strResult[1].get("resTrHistoryList")
                            , resData.getConnectedId()
                            , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build()
                            , resData.getNowMonth()
                    );
                } else {
                    repoResAccount.findByResAccount(resData.getResAccount());
                }

                endLog(ResBatchList.builder()
                        .idx(idxResBatch)
                        .startDate(resData.getStartDay())
                        .endDate(resData.getEndDay())
                        .account(resData.getResAccount())
                        .bank(resData.getOrganization())
                        .errCode(strResult[0].get("code").toString())
                        .transactionId(strResult[0].get("transactionId").toString())
                        .errMessage(strResult[0].get("message").toString())
                        .build());
            } else {
                throw new RuntimeException("process kill");
            }

        }

    }

    /**
     * 에러가 났을경우 2번 더 작업
     * 리스크 작업 추가
     *
     * @param idx
     * @param idxResBatchParent
     * @param boolRetry
     */
    private void saveAccountProcessBatch(Long idx, Long idxResBatchParent, boolean boolRetry) {
        // user ConnectedId List
        List<ResBatchRepository.CResYears> list;
        list = repoResBatch.find10yearMonth(idx, true);
        // ConnId 의 계좌분류별 스크랩
        for (ResBatchRepository.CResYears resData : list) {
            int iType = 0;
            String strType = resData.getResAccountDeposit();

            String strStart, strEnd;

            strStart = resData.getStartDay();
            strEnd = resData.getEndDay();

            switch (strType) {
                case "10":
                case "11":
                    iType = 10;
                    break;
                case "12":
                case "13":
                case "14":
                    iType = 12;
                    break;
                case "40":
                    iType = 40;
                    break;
                case "20":
                    iType = 20;
                    break;
                case "30":
                    iType = 30;
                    break;
            }

            JSONParser jsonParse = new JSONParser();
            JSONObject[] strResult = new JSONObject[0];

            if (iType == 0) continue;
            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

            if (!repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                try {

                    switch (strType) {
                        case "10":
                        case "11":
                            strResult = this.getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "12":
                        case "13":
                        case "14":
                            strResult = this.getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "40":
                            strResult = this.getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , ""));
                            break;
                        case "30":
                            strResult = this.getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "20":
                            strResult = this.getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , resData.getResAccountCurrency()
                            ));
                            break;
                    }

                    log.debug("([scrapingAccountHistory10year ]) $strResult='{}'", strResult.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                    saveAccount(iType
                            , strResult[1]
                            , (JSONArray) strResult[1].get("resTrHistoryList")
                            , resData.getConnectedId()
                            , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build()
                            , resData.getNowMonth()
                    );
                } else {
                    if (resData.getNowMonth().equals("1")) {
                        saveAccountError(resData.getResAccount());
                    }
                }

                endLog(ResBatchList.builder()
                        .idx(idxResBatch)
                        .startDate(resData.getStartDay())
                        .endDate(resData.getEndDay())
                        .account(resData.getResAccount())
                        .bank(resData.getOrganization())
                        .errCode(strResult[0].get("code").toString())
                        .transactionId(strResult[0].get("transactionId").toString())
                        .errMessage(strResult[0].get("message").toString())
                        .build());
            } else {
                throw new RuntimeException("process kill");
            }
        }

        // 에러 상황에 대해 2번 반복 확인
        for (int i = 0; i < 2; i++) {
            saveAccountProcessBatchRetry(idx, idxResBatchParent);
        }
        // 리스크 데이터 저장
        serviceRisk.saveRisk(idx, null,"");

    }

    private void saveAccountProcessBatchRetry(Long idx, Long idxResBatchParent) {
        List<ResBatchRepository.CResYears> list;
        list = repoResBatch.find10yearMonth(idx, false);
        // ConnId 의 계좌분류별 스크랩
        for (ResBatchRepository.CResYears resData : list) {
            int iType = 0;
            String strType = resData.getResAccountDeposit();
            String strStart, strEnd;
            strStart = resData.getStartDay();
            strEnd = resData.getEndDay();

            switch (strType) {
                case "10":
                case "11":
                    iType = 10;
                    break;
                case "12":
                case "13":
                case "14":
                    iType = 12;
                    break;
                case "40":
                    iType = 40;
                    break;
                case "20":
                    iType = 20;
                    break;
                case "30":
                    iType = 30;
                    break;
            }

            JSONParser jsonParse = new JSONParser();
            JSONObject[] strResult = new JSONObject[0];

            if (iType == 0) continue;
            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

            if (iType == 0) {
                endLog(ResBatchList.builder()
                        .idx(idxResBatch)
                        .startDate(resData.getStartDay())
                        .endDate(resData.getEndDay())
                        .account(resData.getResAccount())
                        .bank(resData.getOrganization())
                        .transactionId(strResult[0].get("transactionId").toString())
                        .errCode(strResult[0].get("code").toString())
                        .errMessage(strResult[0].get("message").toString())
                        .build());
                return;
            }

            if (!repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                try {
                    switch (strType) {
                        case "10":
                        case "11":
                            strResult = this.getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "12":
                        case "13":
                        case "14":
                            strResult = this.getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "40":
                            strResult = this.getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , ""));
                            break;
                        case "30":
                            strResult = this.getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , "1"));
                            break;
                        case "20":
                            strResult = this.getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
                                    , resData.getOrganization()
                                    , resData.getResAccount()
                                    , strStart
                                    , strEnd
                                    , "0"
                                    , resData.getResAccountCurrency()
                            ));
                            break;
                    }

                    log.debug("([scrapingAccountHistory10year ]) $strResult='{}'", strResult.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                log.debug("([scrapingAccountHistory10year ]) $resData.getConnectedId()='{}'", resData.getConnectedId());

                if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                    saveAccount(iType
                            , strResult[1]
                            , (JSONArray) strResult[1].get("resTrHistoryList")
                            , resData.getConnectedId()
                            , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build()
                            , resData.getNowMonth()
                    );
                } else {
                    if (resData.getNowMonth().equals("1")) {
                        saveAccountError(resData.getResAccount());
                    }
                }

                endLog(ResBatchList.builder()
                        .idx(idxResBatch)
                        .startDate(resData.getStartDay())
                        .endDate(resData.getEndDay())
                        .account(resData.getResAccount())
                        .bank(resData.getOrganization())
                        .errCode(strResult[0].get("code").toString())
                        .transactionId(strResult[0].get("transactionId").toString())
                        .errMessage(strResult[0].get("message").toString())
                        .build());
            } else {
                throw new RuntimeException("process kill");
            }
        }
    }


    public boolean aWaitScraping10Years(Long aLong, Long idx) {
        log.debug("scraping10Years");
        log.debug("scraping10Years parallelStream() $user={}", idx);
        ResBatch idxLog = startBatchLog(idx);
        try {
            scrapingAccountHistory10year(idx, idxLog.idx());
        } catch (Exception e) {
            log.debug("scraping10Years Exception");
            e.printStackTrace();
            return false;
        } finally {
            endBatchLog(idxLog.idx());
        }
        return true;
    }


    public boolean aWaitJScraping10Years(Long idx) {
        log.debug("scraping10Years");
        log.debug("scraping10Years parallelStream() $user={}", idx);
        ResBatch idxLog = startBatchLog(idx);
        try {
            awaitScrapingAccountHistory10year(idx, idxLog.idx());
        } catch (Exception e) {
            log.debug("scraping10Years Exception");
            e.printStackTrace();
            return false;
        } finally {
            endBatchLog(idxLog.idx());
        }
        return true;
    }

    private void awaitScrapingAccountHistory10year(Long idx, Long idxResBatchParent) {

        log.debug("start scrapingAccountHistory10year $idxResBatchParent={}", idxResBatchParent);

        List<ConnectedMng> connectedMng = getConnectedMng(idx);

        connectedMng.forEach(mngItem -> {
            String connId = mngItem.connectedId();
            JSONParser jsonParse = new JSONParser();

            for (String strBank : CommonConstant.LISTBANK) {

                String code = null, message = null;
                JSONObject[] strResult = new JSONObject[0];
                Long idxResBatchList = startLog(null, connId, ResBatchType.BANK, idxResBatchParent, idx);

                if (repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                    throw new RuntimeException("process kill");
                }

                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLog(ResBatchList.builder()
                            .idx(idxResBatchList)
                            .errCode(strResult[0].get("code").toString())
                            .bank(strBank)
                            .transactionId(strResult[0].get("transactionId").toString())
                            .errMessage(strResult[0].get("message").toString())
                            .build());
                }

                if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("message").toString().equals("CF-04012")) {

                    JSONObject jsonData = strResult[1];
                    JSONArray jsonArrayResDepositTrust = (JSONArray) jsonData.get("resDepositTrust");
                    JSONArray jsonArrayResForeignCurrency = (JSONArray) jsonData.get("resForeignCurrency");
                    JSONArray jsonArrayResFund = (JSONArray) jsonData.get("resFund");
                    JSONArray jsonArrayResLoan = (JSONArray) jsonData.get("resLoan");

                    jsonArrayResDepositTrust.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }

                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("DepositTrust")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .build()
                        );
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("Loan")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .resAccountLoanExecNo( GowidUtils.getEmptyStringToString(obj, "resAccountLoanExecNo").toString())
                                .build()
                        );
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("ResForeignCurrency")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate").toString())
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName").toString())
                                .build()
                        );
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        if (!obj.get("resAccountStartDate").toString().isEmpty()) {
                            startDate = obj.get("resAccountStartDate").toString();
                        }
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type("ResFund")
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount").toString())
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay").toString())
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit").toString())
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName").toString())
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").toString())
                                .resAccountStartDate(startDate)
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate").toString())
                                .resAccountInvestedCost( GowidUtils.getEmptyStringToString(obj, "resAccountInvestedCost").toString())
                                .resEarningsRate( GowidUtils.getEmptyStringToString(obj, "resEarningsRate").toString())
                                .build()
                        );
                    });
                }
            }
        });


        // user ConnectedId List
        List<ResBatchRepository.CResYears> list = repoResBatch.find10yearMonth(idx, true);

        // ConnId 의 계좌분류별 스크랩
        for (ResBatchRepository.CResYears resData : list) {
            int iType = 0;
            String strType = resData.getResAccountDeposit();

            String strStart, strEnd;

            strStart = resData.getStartDay();
            strEnd = resData.getEndDay();

            if (strType.equals("10") || strType.equals("11")) {
                iType = 10;
            } else if (strType.equals("12") || strType.equals("13") || strType.equals("14")) {
                iType = 12;
            } else if (strType.equals("40")) {
                iType = 40;
            } else if (strType.equals("20")) {
                iType = 20;
            } else if (strType.equals("30")) {
                iType = 30;
            }

            JSONParser jsonParse = new JSONParser();
            JSONObject[] strResult = new JSONObject[0];

            if (iType == 0) continue;
            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

            if (repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                throw new RuntimeException("process kill");
            }

            try {
                switch (strType) {
                    case "10":
                    case "11":
                        strResult = this.getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "12":
                    case "13":
                    case "14":
                        strResult = this.getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "40":
                        strResult = this.getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , ""));
                        break;
                    case "30":
                        strResult = this.getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "20":

                        strResult = this.getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , resData.getResAccountCurrency()
                        ));

                        log.debug("([scrapingAccountHistory10year ]) $resData='{}'", strResult.toString());
                        break;
                }

                log.debug("([scrapingAccountHistory10year ]) $strResult='{}'", strResult.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                saveAccount(iType
                        , strResult[1]
                        , (JSONArray) strResult[1].get("resTrHistoryList")
                        , resData.getConnectedId()
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build()
                        , resData.getNowMonth()
                );
            } else {
                if (resData.getNowMonth().equals("1")) {
                    saveAccountError(resData.getResAccount());
                }
            }

            endLog(ResBatchList.builder()
                    .idx(idxResBatch)
                    .startDate(resData.getStartDay())
                    .endDate(resData.getEndDay())
                    .account(resData.getResAccount())
                    .bank(resData.getOrganization())
                    .errCode(strResult[0].get("code").toString())
                    .transactionId(strResult[0].get("transactionId").toString())
                    .errMessage(strResult[0].get("message").toString())
                    .build());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    void saveAccountError(String strResAccount) {
        ResAccount resAccount = repoResAccount.findByResAccount(strResAccount).get();
        resAccount.resAccountRiskBalance(0.0);
        repoResAccount.save(resAccount);
    }

    public ResponseEntity scrapingProcessKill(Long idx) {
        return ResponseEntity.ok().body(BusinessResponse.builder().data(repoResBatch.updateProcessIdx(idx)).build());
    }

    public ResponseEntity scrapingAccount(Long idxUser,String resAccount,String strStart, String strEnd) throws ParseException {

        //todo auth
        if(repoUser.findById(idxUser).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))){

            ResAccount resAccountData = repoResAccount.findByResAccount(resAccount).orElseThrow(
                    () -> new RuntimeException("Waring Account")
            );

            JSONObject[] strResult = this.getApiResult(KR_BK_1_B_002.krbk1b002(resAccountData.connectedId()
                    , resAccountData.organization()
                    , resAccountData.resAccount()
                    , strStart
                    , strEnd
                    , "0"
                    , "1"));

            if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                saveAccount(Integer.parseInt(resAccountData.resAccountDeposit())
                        , strResult[1]
                        , (JSONArray) strResult[1].get("resTrHistoryList")
                        , resAccountData.connectedId()
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build()
                        , null);
            }
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().build());
    }
}