package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.UserDto;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ScrapingService {


    private final EmailConfig config;
    private final ITemplateEngine templateEngine;

    private final JavaMailSenderImpl sender;

    private final UserRepository repoUser;
    private final ResAccountRepository repoResAccount;
    private final ResAccountHistoryRepository repoResAccountHistory;

    private final ResBatchListRepository repoResBatchList;
    private final ResBatchRepository repoResBatch;

    private final ConnectedMngRepository repoConnectedMng;
    private final PasswordEncoder encoder;
    private final VerificationCodeRepository repoVerificationCode;

    private final String urlPath = CommonConstant.getRequestDomain();


    /**
     * 1. 은행 기업 보유계좌
     *
     * @param idx 엔터티(사용자)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean scrapingAccount(Long idx, Long idxResBatch) {
        log.debug("start scrapingAccount $idxResBatch={}", idxResBatch);

        List<ConnectedMng> connectedMng = getConnectedMng(idx);

        connectedMng.forEach(mngItem -> {
            String connId = mngItem.connectedId();
            JSONParser jsonParse = new JSONParser();

            for (String strBank : CommonConstant.LISTBANK) {

                String code = null, message = null;
                JSONObject[] strResult = new JSONObject[0];
                Long idxResBatchList = startLog(null, connId, ResBatchType.BANK, idxResBatch, idx);
                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLog(ResBatchList.builder()
                            .idx(idxResBatchList)
                            .errCode(strResult[0].get("code").toString())
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
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("DepositTrust")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("Loan")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .resAccountLoanExecNo(""+obj.get("resAccountLoanExecNo").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResForeignCurrency")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResFund")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountInvestedCost(""+obj.get("resAccountInvestedCost").toString())
                                    .resEarningsRate(""+obj.get("resEarningsRate").toString())
                                    .build()
                            );
                        }
                    });
                }
            }
        });
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ConnectedMng> getConnectedMng(Long idx) {
        return repoConnectedMng.findByIdxUser(idx);
    }

    @Transactional(rollbackFor = Exception.class)
    List<ResAccount> getFindByConnectedIdAndResAccountDepositIn(String connectedId, List<String> asList) {
        return repoResAccount.findByConnectedIdAndResAccountDepositIn(connectedId, asList);
    }

    @Transactional(rollbackFor = Exception.class)
    void saveAccount(int iType, JSONObject jsonData, JSONArray jsonArrayResTrHistoryList, String connectedId, Long idx, BankDto.AccountBatch dto) {

        String strDefault = null;
        // iType 별로 데이터 가져오는데 문제확인 필요 ex 대출에는 resAccountName 없음

        repoResAccountHistory.deleteResAccountTrDate(jsonData.get("resAccount").toString(), dto.getStartDate(), dto.getEndDate());

        //   0 실시간 적금   40:대출  20:외화 30:펀드
        if (iType == 10) {

            Optional<ResAccount> resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString());

            resAccount.ifPresent(
                    account -> {
                        account.resAccount(jsonData.get("resAccount").toString());
                        account.resAccountName("" + jsonData.get("resAccountName").toString());
                        account.resAccountHolder("" + jsonData.get("resAccountHolder").toString());
                        account.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                        account.resAccountEndDate("" + jsonData.get("resAccountEndDate").toString());
                        account.resManagementBranch("" + jsonData.get("resManagementBranch").toString());
                        account.resAccountStatus("" + jsonData.get("resAccountStatus").toString());
                        account.resLastTranDate("" + jsonData.get("resLastTranDate").toString());
                        account.resAccountCurrency("" + jsonData.get("resAccountCurrency").toString());
                        account.resAccountBalance("" + jsonData.get("resAccountBalance").toString());
                        account.resWithdrawalAmt("" + jsonData.get("resWithdrawalAmt").toString());
                        ResAccount newResAccount = repoResAccount.save(account);
                    }
            );

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate(""+obj.get("resAccountTrDate").toString())
                                    .resAccountTrTime(""+obj.get("resAccountTrTime").toString())
                                    .resAccountOut(""+obj.get("resAccountOut").toString())
                                    .resAccountIn(""+obj.get("resAccountIn").toString())
                                    .resAccountDesc1(""+obj.get("resAccountDesc1").toString())
                                    .resAccountDesc2(""+obj.get("resAccountDesc2").toString())
                                    .resAccountDesc3(""+obj.get("resAccountDesc3").toString())
                                    .resAccountDesc4(""+obj.get("resAccountDesc4").toString())
                                    .resAfterTranBalance(""+obj.get("resAfterTranBalance").toString())
                                    .resAccount(resAccount.get().resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 12) {
            Optional<ResAccount> resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString());

            resAccount.ifPresent(
                    account -> {
                        account.resAccount(jsonData.get("resAccount").toString());
                        account.resAccountName("" + jsonData.get("resAccountName").toString());
                        account.resAccountNickName("" + jsonData.get("resAccountNickName").toString());
                        account.resAccountHolder("" + jsonData.get("resAccountHolder").toString());
                        account.resFinalRoundNo("" + jsonData.get("resFinalRoundNo").toString());
                        account.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                        account.resAccountEndDate("" + jsonData.get("resAccountEndDate").toString());
                        account.resAccountBalance("" + jsonData.get("resAccountBalance").toString());
                        account.resMonthlyPayment("" + jsonData.get("resMonthlyPayment").toString());
                        account.resValidPeriod("" + jsonData.get("resValidPeriod").toString());
                        account.resType("" + jsonData.get("resType").toString());
                        account.resManagementBranch("" + jsonData.get("resManagementBranch").toString());
                        account.resRate("" + jsonData.get("resRate").toString());
                        account.resAccountStatus("" + jsonData.get("resAccountStatus").toString());
                        account.resContractAmount("" + jsonData.get("resContractAmount").toString());
                        account.resPaymentMethods("" + jsonData.get("resPaymentMethods").toString());
                        account.resLastTranDate("" + jsonData.get("resLastTranDate").toString());
                        repoResAccount.save(account);
                    }
            );

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resRoundNo(""+obj.get("resRoundNo").toString())
                                    .resMonth(""+obj.get("resMonth").toString())
                                    .resAccountTrDate(""+obj.get("resAccountTrDate").toString())
                                    .resAccountIn(""+obj.get("resAccountIn").toString())
                                    .resAccountDesc1(""+obj.get("resAccountDesc1").toString())
                                    .resAccountDesc2(""+obj.get("resAccountDesc2").toString())
                                    .resAccountDesc3(""+obj.get("resAccountDesc3").toString())
                                    .resAccountDesc4(""+obj.get("resAccountDesc4").toString())
                                    .resAfterTranBalance(""+obj.get("resAfterTranBalance").toString())
                                    .resAccount(resAccount.get().resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 40) {
            Optional<ResAccount> resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString());

            resAccount.ifPresent(
                    account -> {
                        account.resAccount(jsonData.get("resAccount").toString());
                        account.resLoanKind("" + jsonData.get("resLoanKind").toString());
                        account.resAccountHolder("" + jsonData.get("resAccountHolder").toString());
                        account.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                        account.resAccountEndDate("" + jsonData.get("resAccountEndDate").toString());
                        account.resLoanBalance("" + jsonData.get("resLoanBalance").toString());
                        account.resPrincipal("" + jsonData.get("resPrincipal").toString());
                        account.resRate("" + jsonData.get("resRate").toString());
                        account.resDatePayment("" + jsonData.get("resDatePayment").toString());
                        account.resState("" + jsonData.get("resState").toString());
                        account.commStartDate("" + jsonData.get("commStartDate").toString());
                        account.commEndDate("" + jsonData.get("commEndDate").toString());
                    }
            );

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate(""+obj.get("resAccountTrDate").toString())
                                    .resTransTypeNm(""+obj.get("resTransTypeNm").toString())
                                    .resType(""+obj.get("resType").toString())
                                    .resTranAmount(""+obj.get("resTranAmount").toString())
                                    .resPrincipal(""+obj.get("resPrincipal").toString())
                                    .resInterest(""+obj.get("resInterest").toString())
                                    // .resOverdueInterest(""+obj.get("resOverdueInterest").toString())
                                    // .resReturnInterest(""+obj.get("resReturnInterest").toString())
                                    // .resFee(""+obj.get("resFee").toString())
                                    .commStartDate(""+obj.get("commStartDate").toString())
                                    .commEndDate(""+obj.get("commEndDate").toString())
                                    .resLoanBalance(""+obj.get("resLoanBalance").toString())
                                    .resInterestRate(""+obj.get("resInterestRate").toString())
                                    .resAccount(resAccount.get().resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 30) {
            Optional<ResAccount> resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString());

            resAccount.ifPresent(
                    account -> {
                        account.resAccount(jsonData.get("resAccount").toString());
                        account.resAccountName("" + jsonData.get("resAccountName").toString());
                        account.resAccountHolder("" + jsonData.get("resAccountHolder").toString());
                        account.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                        account.resAccountEndDate("" + jsonData.get("resAccountEndDate").toString());
                        account.resAccountBalance("" + jsonData.get("resAccountBalance").toString());
                        account.resAccountInvestedCost("" + jsonData.get("resAccountInvestedCost").toString());
                        account.resEarningsRate("" + jsonData.get("resEarningsRate").toString());
                        account.resType("" + jsonData.get("resType").toString());
                        account.resBalanceNum("" + jsonData.get("resBalanceNum").toString());
                        account.resAccountCurrency("" + jsonData.get("resAccountCurrency").toString());
                        account.commStartDate("" + jsonData.get("commStartDate").toString());
                        account.commEndDate("" + jsonData.get("commEndDate").toString());
                        ResAccount newResAccount = repoResAccount.save(account);
                    }
            );

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate(""+obj.get("resAccountTrDate").toString())
                                    .resAccountTrTime(""+obj.get("resAccountTrTime").toString())
                                    .resTranAmount(""+obj.get("resTranAmount").toString())
                                    .resTranNum(""+obj.get("resTranNum").toString())
                                    .resBasePrice(""+obj.get("resBasePrice").toString())
                                    .resBalanceNum(""+obj.get("resBalanceNum").toString())
                                    .resAccountDesc1(""+obj.get("resAccountDesc1").toString())
                                    .resAccountDesc2(""+obj.get("resAccountDesc2").toString())
                                    .resAccountDesc3(""+obj.get("resAccountDesc3").toString())
                                    .resAccountDesc4(""+obj.get("resAccountDesc4").toString())
                                    .resAfterTranBalance(""+obj.get("resAfterTranBalance").toString())
                                    .resValuationAmt(""+obj.get("resValuationAmt").toString())
                                    .resAccount(resAccount.get().resAccount())
                                    .build()
                    );
                });
            }
        } else if (iType == 20) {
            // 외화 20
            Optional<ResAccount> resAccount = repoResAccount.findByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString());

            resAccount.ifPresent(
                    account -> {
                        account.resAccount(jsonData.get("resAccount").toString());
                        account.resAccountName("" + jsonData.get("resAccountName").toString());
                        account.resAccountHolder("" + jsonData.get("resAccountHolder").toString());
                        account.resAccountStartDate("" + jsonData.get("resAccountStartDate").toString());
                        account.resAccountEndDate("" + jsonData.get("resAccountEndDate").toString());
                        account.resManagementBranch("" + jsonData.get("resManagementBranch").toString());
                        account.resAccountStatus("" + jsonData.get("resAccountStatus").toString());
                        account.resLastTranDate("" + jsonData.get("resLastTranDate").toString());
                        account.resAccountCurrency("" + jsonData.get("resAccountCurrency").toString());
                        account.resAccountBalance("" + jsonData.get("resAccountBalance").toString());
                        account.resWithdrawalAmt("" + jsonData.get("resWithdrawalAmt").toString());
                        account.commStartDate("" + jsonData.get("commStartDate").toString());
                        account.commEndDate("" + jsonData.get("commEndDate").toString());
                        ResAccount newResAccount = repoResAccount.save(account);
                    }
            );

            if (!jsonArrayResTrHistoryList.isEmpty()) {
                jsonArrayResTrHistoryList.forEach(item2 -> {
                    JSONObject obj = (JSONObject) item2;
                    repoResAccountHistory.save(
                            ResAccountHistory.builder()
                                    .resAccountTrDate(""+obj.get("resAccountTrDate").toString())
                                    .resAccountTrTime(""+obj.get("resAccountTrTime").toString())
                                    .resAccountOut(""+obj.get("resAccountOut").toString())
                                    .resAccountIn(""+obj.get("resAccountIn").toString())
                                    .resAccountDesc1(""+obj.get("resAccountDesc1").toString())
                                    .resAccountDesc2(""+obj.get("resAccountDesc2").toString())
                                    .resAccountDesc3(""+obj.get("resAccountDesc3").toString())
                                    .resAccountDesc4(""+obj.get("resAccountDesc4").toString())
                                    .resAfterTranBalance(""+obj.get("resAfterTranBalance").toString())
                                    .resAccount(resAccount.get().resAccount())
                                    .build()
                    );
                });
            }
        }
    }


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
                            .errCode(resBatchList.errCode())
                            .errMessage(resBatchList.errMessage())
                            .startDate(resBatchList.startDate())
                            .endDate(resBatchList.endDate())
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
        if (startDate == null) startDate = getMonth(-11);
        if (endDate == null) endDate = getMonth(0);

        List<ResAccountRepository.CaccountMonthDto> transactionList = repoResAccount.findMonthHistory(startDate, endDate, idx);

        return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
    }


    private String getMonth(int i) {
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MONDAY, i);
        return df.format(cal.getTime());
    }


    /**
     * 유저의 계좌정보
     *
     * @param idx 엔터티(사용자)
     */
    public ResponseEntity accountList(Long idx) {

        List<BankDto.ResAccountDto> resAccount = repoResAccount.findConnectedId(idx)
                .map(BankDto.ResAccountDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(BusinessResponse.builder().data(resAccount).build());
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
        if( dto.getBoolForeign() != null){
            if(dto.getBoolForeign()) {
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
    public void scrapingRegister1YearAll(Long idxUser) {
        log.debug("scrapingRegister");
        ResBatch idxLog = startBatchLog(idxUser);
        try {
			scrapingRegister1YearAll(idxUser, idxLog.idx());
        } finally {
            endBatchLog(idxLog.idx());
        }
    }

    public boolean aWaitcrapingRegister1YearAll(Long idxUser) {
        log.debug("scrapingRegister");
        ResBatch idxLog = startBatchLog(idxUser);
        try {
            scrapingRegister1YearAll(idxUser, idxLog.idx());
        } finally {
            endBatchLog(idxLog.idx());
        }
        return true;
    }


    /**
     * 매일 밤 스크립트 처리
     *
     * @param idx
     * @param idxResBatchParent
     * @return
     */
    @Async
    public boolean scrapingRegister1YearAll(Long idx, Long idxResBatchParent) {

        log.debug("start scrapingRegister1Year $idxResBatchParent={}", idxResBatchParent);
        List<ConnectedMng> connectedMng = getConnectedMng(idx);

        connectedMng.forEach(mngItem -> {
            String connId = mngItem.connectedId();
            JSONParser jsonParse = new JSONParser();

            for (String strBank : CommonConstant.LISTBANK) {

                String code = null, message = null;
                JSONObject[] strResult = new JSONObject[0];
                Long idxResBatchList = startLog(null, connId, ResBatchType.BANK, idxResBatchParent, idx);
                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLog(ResBatchList.builder()
                            .idx(idxResBatchList)
                            .errCode(strResult[0].get("code").toString())
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
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("DepositTrust")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("Loan")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .resAccountLoanExecNo(""+obj.get("resAccountLoanExecNo").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResForeignCurrency")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResFund")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountInvestedCost(""+obj.get("resAccountInvestedCost").toString())
                                    .resEarningsRate(""+obj.get("resEarningsRate").toString())
                                    .build()
                            );
                        }
                    });
                }
            }
        });

        // user ConnectedId List
        List<ResBatchRepository.CResYears> list = repoResBatch.findStartDateMonth(idx);

        // ConnId 의 계좌분류별 스크랩
        list.forEach(resData -> {

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

            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

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
                            , "KRW"));
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
                        , idx
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build());
            }

            endLog(ResBatchList.builder()
                    .idx(idxResBatch)
                    .startDate(resData.getStartDay())
                    .endDate(resData.getEndDay())
                    .account(resData.getResAccount())
                    .errCode(strResult[0].get("code").toString())
                    .errMessage(strResult[0].get("message").toString())
                    .build());
        });

        // 실패한 목록을 검색후 재시도함

        return true;
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


    public void asyncAwaitScrapingRegister(Long idxUser) {
        log.debug("asyncAwaitScrapingRegister");
        ResBatch idxLog = startBatchLog(idxUser);
        try {
            scrapingRegisterAccount(idxUser, idxLog.idx());
        } finally {
            endBatchLog(idxLog.idx());
        }
    }

    private void scrapingRegisterAccount(Long idx, Long idxResBatch) {

        log.debug("start scrapingAccountHistoryList $idxResBatch={}", idxResBatch);
        List<ResBatchRepository.CResYears> list = repoResBatch.findStartDateMonth(idx);

        // ConnId 의 계좌분류별 스크랩
        list.forEach(resData -> {

            int iType = 0;
            String strType = resData.getResAccountDeposit();
            String strStart = resData.getStartDay();
            String strEnd = resData.getEndDay();


            log.debug(" scrapingAccountHistoryList $account={}, $strStart={} , $strEnd={} ", resData.getResAccount(), strStart, strEnd);

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

            Long idxResBatchList = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatch, idx);

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
                            , "KRW"));
                }

                log.debug("([scrapingAccountHistoryList ]) $strResult='{}'", strResult.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLog(ResBatchList.builder()
                        .idx(idxResBatchList)
                        .startDate(resData.getStartDay())
                        .endDate(resData.getEndDay())
                        .account(resData.getResAccount())
                        .errCode(strResult[0].get("code").toString())
                        .errMessage(strResult[0].get("message").toString())
                        .build());
            }

            if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                saveAccount(iType
                        , strResult[1]
                        , (JSONArray) strResult[1].get("resTrHistoryList")
                        , resData.getConnectedId()
                        , idx
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build());
            }

        });
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
                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLog(ResBatchList.builder()
                            .idx(idxResBatchList)
                            .errCode(strResult[0].get("code").toString())
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
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("DepositTrust")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("Loan")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .resAccountLoanExecNo(""+obj.get("resAccountLoanExecNo").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResForeignCurrency")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResFund")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountInvestedCost(""+obj.get("resAccountInvestedCost").toString())
                                    .resEarningsRate(""+obj.get("resEarningsRate").toString())
                                    .build()
                            );
                        }
                    });
                }
            }
        });


        // user ConnectedId List
        List<ResBatchRepository.CResYears> list = repoResBatch.find10yearMonth(idx);

        // ConnId 의 계좌분류별 스크랩
        list.forEach(resData -> {

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

            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

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
                            , "KRW"));
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
                        , idx
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build());
            }

            endLog(ResBatchList.builder()
                    .idx(idxResBatch)
                    .startDate(resData.getStartDay())
                    .endDate(resData.getEndDay())
                    .account(resData.getResAccount())
                    .errCode(strResult[0].get("code").toString())
                    .errMessage(strResult[0].get("message").toString())
                    .build());
        });
        return true;
    }


    public boolean aWaitScraping10Years(Long idx) {
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

    public boolean awaitScrapingAccountHistory10year(Long idx, Long idxResBatchParent) {

        log.debug("start scrapingAccountHistory10year $idxResBatchParent={}", idxResBatchParent);

        List<ConnectedMng> connectedMng = getConnectedMng(idx);

        connectedMng.forEach(mngItem -> {
            String connId = mngItem.connectedId();
            JSONParser jsonParse = new JSONParser();

            for (String strBank : CommonConstant.LISTBANK) {

                String code = null, message = null;
                JSONObject[] strResult = new JSONObject[0];
                Long idxResBatchList = startLog(null, connId, ResBatchType.BANK, idxResBatchParent, idx);
                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLog(ResBatchList.builder()
                            .idx(idxResBatchList)
                            .errCode(strResult[0].get("code").toString())
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
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("DepositTrust")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("Loan")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .resAccountLoanExecNo(""+obj.get("resAccountLoanExecNo").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResForeignCurrency")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resLastTranDate(""+obj.get("resLastTranDate").toString())
                                    .resAccountName(""+obj.get("resAccountName").toString())
                                    .build()
                            );
                        }
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        if (!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                            repoResAccount.save(ResAccount.builder()
                                    .connectedId(connId)
                                    .organization(strBank)
                                    .type("ResFund")
                                    .resAccount(""+obj.get("resAccount").toString())
                                    .resAccountDisplay(""+obj.get("resAccountDisplay").toString())
                                    .resAccountBalance(""+obj.get("resAccountBalance").toString())
                                    .resAccountDeposit(""+obj.get("resAccountDeposit").toString())
                                    .resAccountNickName(""+obj.get("resAccountNickName").toString())
                                    .resAccountCurrency(""+obj.get("resAccountCurrency").toString())
                                    .resAccountStartDate(""+obj.get("resAccountStartDate").toString())
                                    .resAccountEndDate(""+obj.get("resAccountEndDate").toString())
                                    .resAccountInvestedCost(""+obj.get("resAccountInvestedCost").toString())
                                    .resEarningsRate(""+obj.get("resEarningsRate").toString())
                                    .build()
                            );
                        }
                    });
                }
            }
        });


        // user ConnectedId List
        List<ResBatchRepository.CResYears> list = repoResBatch.find10yearMonth(idx);

        // ConnId 의 계좌분류별 스크랩
        list.forEach(resData -> {

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

            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

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
                            , "KRW"));
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
                        , idx
                        , BankDto.AccountBatch.builder().startDate(strStart).endDate(strEnd).build());
            }

            endLog(ResBatchList.builder()
                    .idx(idxResBatch)
                    .startDate(resData.getStartDay())
                    .endDate(resData.getEndDay())
                    .account(resData.getResAccount())
                    .errCode(strResult[0].get("code").toString())
                    .errMessage(strResult[0].get("message").toString())
                    .build());
        });
        return true;
    }
}