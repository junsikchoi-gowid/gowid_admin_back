package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.config.KoreaeximConfig;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.ProcessKillException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.codef.io.sandbox.bk.*;
import com.nomadconnection.dapp.codef.io.sandbox.pb.TAX_INVOICE;
import com.nomadconnection.dapp.codef.io.sandbox.st.KR_ST_1_S_001;
import com.nomadconnection.dapp.codef.io.sandbox.st.KR_ST_1_S_002;
import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.*;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasTrackerProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.*;
import com.nomadconnection.dapp.core.domain.saas.SaasTrackerProgress;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.Math.round;

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
    private final ResConCorpListRepository repoResConCorpList;
    private final ResBatchRepository repoResBatch;
    private final ConnectedMngRepository repoConnectedMng;
    private final RiskService serviceRisk;
    private final PasswordEncoder encoder;
    private final BatchDateRepository repoBatchDate;
    private final ResTaxInvoiceRepository repoResTaxInvoice;
    private final SaasTrackerProgressRepository repoSaasTrackerProgress;
    private final ResExchangeRateRepository repoExchangeRate;
    private final KoreaeximConfig koreaeximConfig;

    private final String urlPath = CommonConstant.getRequestDomain();
    final Thread currentThread = Thread.currentThread();

    private final List<ConnectedMngStatus> connectedMngStatusList
            = Arrays.asList(ConnectedMngStatus.NORMAL, ConnectedMngStatus.ERROR);
    private final List<ResConCorpListStatus> resConCorpListStatuses
            = Arrays.asList(ResConCorpListStatus.NORMAL, ResConCorpListStatus.ERROR);


    @Transactional(rollbackFor = Exception.class)
    public List<ConnectedMng> getConnectedMng(Long idx) {
        return repoConnectedMng.findByIdxUser(idx);
    }


    @Transactional(rollbackFor = Exception.class)
    void saveAccount(int iType, JSONObject jsonData, JSONArray jsonArrayResTrHistoryList, String connectedId,  BankDto.AccountBatch dto, String nowFlag) {
        String strDefault = null;
        String strAccount = GowidUtils.getEmptyStringToString(jsonData,"resAccount");

        if( !ObjectUtils.isEmpty(strAccount)) {
            Optional<ResAccount> optResAccount = repoResAccount.findTopByConnectedIdAndResAccount(connectedId, strAccount);
            String account = GowidUtils.getEmptyStringToString(jsonData, "resAccount");

            if (!StringUtils.isEmpty(account) && optResAccount.isPresent()) {
                String organization = optResAccount.get().organization();
                String accountCurrency = GowidUtils.getEmptyStringToString(jsonData, "resAccountCurrency");
                //   10 :????????? ??????  40:??????  20:??????  30:??????
                try {
                    if (iType == 10) {
                        if (!jsonArrayResTrHistoryList.isEmpty()) {
                            ((List<?>) jsonArrayResTrHistoryList).forEach(item2 -> {
                                JSONObject obj = (JSONObject) item2;

                                ResAccountHistory accountHistory = repoResAccountHistory.findTopByResAccountAndResAccountInEqualsAndResAccountOutAndResAccountTrDateAndResAccountTrTimeAndResAfterTranBalanceAndResAccountCurrency(
                                        jsonData.get("resAccount").toString(),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountIn"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountOut"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"),
                                        GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"),
                                        accountCurrency
                                );

                                List<String> desc = Arrays.asList(
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc1"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc2"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc3"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc4"));

                                desc = changeDesc(organization, desc);

                                if (ObjectUtils.isEmpty(accountHistory)) {
                                    repoResAccountHistory.save(
                                            ResAccountHistory.builder()
                                                    .resAccountTrDate(GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"))
                                                    .resAccountTrTime(GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"))
                                                    .resAccountOut(GowidUtils.getEmptyStringToString(obj, "resAccountOut"))
                                                    .resAccountIn(GowidUtils.getEmptyStringToString(obj, "resAccountIn"))
                                                    .resAccountDesc1(desc.get(3))
                                                    .resAccountDesc2(desc.get(1))
                                                    .resAccountDesc3(desc.get(0))
                                                    .resAccountDesc4(desc.get(2))
                                                    .resAfterTranBalance(GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"))
                                                    .resAccountCurrency(accountCurrency)
                                                    .resAccount(account)
                                                    .build());
                                }
                            });
                        }
                    } else if (iType == 12) {
                        for (Object objectJson : jsonArrayResTrHistoryList) {
                            JSONObject obj = (JSONObject) objectJson;
                            ResAccountHistory accountHistory = repoResAccountHistory.findTopByResAccountAndResAccountInEqualsAndResAccountOutAndResAccountTrDateAndResAccountTrTimeAndResAfterTranBalanceAndResAccountCurrency(
                                    jsonData.get("resAccount").toString(),
                                    GowidUtils.getEmptyStringToString(obj, "resAccountIn"),
                                    GowidUtils.getEmptyStringToString(obj, "resAccountOut"),
                                    GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"),
                                    GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"),
                                    GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"),
                                    accountCurrency
                            );

                            List<String> desc = Arrays.asList(
                                    GowidUtils.getEmptyStringToString(obj, "resAccountDesc1"),
                                    GowidUtils.getEmptyStringToString(obj, "resAccountDesc2"),
                                    GowidUtils.getEmptyStringToString(obj, "resAccountDesc3"),
                                    GowidUtils.getEmptyStringToString(obj, "resAccountDesc4"));

                            desc = changeDesc(organization, desc);

                            if (ObjectUtils.isEmpty(accountHistory)) {
                                repoResAccountHistory.save(
                                        ResAccountHistory.builder()
                                                .resRoundNo(GowidUtils.getEmptyStringToString(obj, "resRoundNo"))
                                                .resMonth(GowidUtils.getEmptyStringToString(obj, "resMonth"))
                                                .resAccountTrDate(GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"))
                                                .resAccountIn(GowidUtils.getEmptyStringToString(obj, "resAccountIn"))
                                                .resAccountDesc1(desc.get(3))
                                                .resAccountDesc2(desc.get(1))
                                                .resAccountDesc3(desc.get(0))
                                                .resAccountDesc4(desc.get(2))
                                                .resAfterTranBalance(GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"))
                                                .resAccountCurrency(accountCurrency)
                                                .resAccount(account)
                                                .build());
                            }
                        }
                    } else if (iType == 40) {

                        if (!jsonArrayResTrHistoryList.isEmpty()) {
                            jsonArrayResTrHistoryList.forEach(item2 -> {
                                JSONObject obj = (JSONObject) item2;

                                ResAccountHistory accountHistory = repoResAccountHistory.findTopByResAccountAndResAccountInEqualsAndResAccountOutAndResAccountTrDateAndResAccountTrTimeAndResAfterTranBalanceAndResAccountCurrency(
                                        jsonData.get("resAccount").toString(),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountIn"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountOut"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"),
                                        GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"),
                                        accountCurrency
                                );

                                List<String> desc = Arrays.asList(
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc1"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc2"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc3"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc4"));

                                desc = changeDesc(organization, desc);

                                if (ObjectUtils.isEmpty(accountHistory)) {
                                    repoResAccountHistory.save(
                                            ResAccountHistory.builder()
                                                    .resAccountTrDate(GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"))
                                                    .resTransTypeNm(GowidUtils.getEmptyStringToString(obj, "resTransTypeNm"))
                                                    .resType(GowidUtils.getEmptyStringToString(obj, "resType"))
                                                    .resTranAmount(GowidUtils.getEmptyStringToString(obj, "resTranAmount"))
                                                    .resPrincipal(GowidUtils.getEmptyStringToString(obj, "resPrincipal"))
                                                    .resInterest(GowidUtils.getEmptyStringToString(obj, "resInterest"))
                                                    .resOverdueInterest(GowidUtils.getEmptyStringToString(obj, "resOverdueInterest"))
                                                    .resReturnInterest(GowidUtils.getEmptyStringToString(obj, "resReturnInterest"))
                                                    .resFee(GowidUtils.getEmptyStringToString(obj, "resFee"))
                                                    .commStartDate(GowidUtils.getEmptyStringToString(obj, "commStartDate"))
                                                    .commEndDate(GowidUtils.getEmptyStringToString(obj, "commEndDate"))
                                                    .resLoanBalance(GowidUtils.getEmptyStringToString(obj, "resLoanBalance"))
                                                    .resInterestRate(GowidUtils.getEmptyStringToString(obj, "resInterestRate"))
                                                    .resAccountCurrency(accountCurrency)
                                                    .resAccount(account)
                                                    .build());
                                }
                            });
                        }
                    } else if (iType == 30) {
                        if (!jsonArrayResTrHistoryList.isEmpty()) {
                            jsonArrayResTrHistoryList.forEach(item2 -> {
                                JSONObject obj = (JSONObject) item2;
                                ResAccountHistory accountHistory = repoResAccountHistory.findTopByResAccountAndResAccountInEqualsAndResAccountOutAndResAccountTrDateAndResAccountTrTimeAndResAfterTranBalanceAndResAccountCurrency(
                                        jsonData.get("resAccount").toString(),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountIn"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountOut"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"),
                                        GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"),
                                        accountCurrency
                                );

                                List<String> desc = Arrays.asList(
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc1"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc2"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc3"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc4"));

                                desc = changeDesc(organization, desc);

                                if (ObjectUtils.isEmpty(accountHistory)) {
                                    repoResAccountHistory.save(
                                            ResAccountHistory.builder()
                                                    .resAccountTrDate(GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"))
                                                    .resAccountTrTime(GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"))
                                                    .resTranAmount(GowidUtils.getEmptyStringToString(obj, "resTranAmount"))
                                                    .resTranNum(GowidUtils.getEmptyStringToString(obj, "resTranNum"))
                                                    .resBasePrice(GowidUtils.getEmptyStringToString(obj, "resBasePrice"))
                                                    .resBalanceNum(GowidUtils.getEmptyStringToString(obj, "resBalanceNum"))
                                                    .resAccountDesc1(desc.get(3))
                                                    .resAccountDesc2(desc.get(1))
                                                    .resAccountDesc3(desc.get(0))
                                                    .resAccountDesc4(desc.get(2))
                                                    .resAfterTranBalance(GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"))
                                                    .resValuationAmt("" + GowidUtils.getEmptyStringToString(obj, "resValuationAmt"))
                                                    .resAccountCurrency(accountCurrency)
                                                    .resAccount(account)
                                                    .build());
                                }
                            });
                        }
                    } else if (iType == 20) {
                        if (!jsonArrayResTrHistoryList.isEmpty()) {
                            jsonArrayResTrHistoryList.forEach(item2 -> {
                                JSONObject obj = (JSONObject) item2;
                                ResAccountHistory accountHistory = repoResAccountHistory.findTopByResAccountAndResAccountInEqualsAndResAccountOutAndResAccountTrDateAndResAccountTrTimeAndResAfterTranBalanceAndResAccountCurrency(
                                        jsonData.get("resAccount").toString(),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountIn"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountOut"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"),
                                        GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"),
                                        accountCurrency
                                );

                                List<String> desc = Arrays.asList(
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc1"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc2"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc3"),
                                        GowidUtils.getEmptyStringToString(obj, "resAccountDesc4"));

                                desc = changeDesc(organization, desc);

                                if (ObjectUtils.isEmpty(accountHistory)) {
                                    repoResAccountHistory.save(
                                            ResAccountHistory.builder()
                                                    .resAccountTrDate(GowidUtils.getEmptyStringToString(obj, "resAccountTrDate"))
                                                    .resAccountTrTime(GowidUtils.getEmptyStringToString(obj, "resAccountTrTime"))
                                                    .resAccountOut(GowidUtils.getEmptyStringToString(obj, "resAccountOut"))
                                                    .resAccountIn(GowidUtils.getEmptyStringToString(obj, "resAccountIn"))
                                                    .resAccountDesc1(desc.get(3))
                                                    .resAccountDesc2(desc.get(1))
                                                    .resAccountDesc3(desc.get(0))
                                                    .resAccountDesc4(desc.get(2))
                                                    .resAfterTranBalance(GowidUtils.getEmptyStringToString(obj, "resAfterTranBalance"))
                                                    .resAccountCurrency(accountCurrency)
                                                    .resAccount(account)
                                                    .build());
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.error("[saveAccount] $jsonData = {}, $jsonArrayResTrHistoryList = {}, $connectedId = {}, $dto= {}, $nowFlag = {} ", jsonData, jsonArrayResTrHistoryList, connectedId, dto, nowFlag);
            }
        }
    }

    /**
    desc 1 (?????????/?????????)
    desc 2 (????????????)
    desc 3 (??????)
    desc 4 (?????????)
     **/
    private List<String> changeDesc(String organization, List<String> desc) {

        List<String> descList = Arrays.asList(new String[]{"", "", "", ""});

        HashMap<String, int[]> hashMap = new HashMap<>();
        hashMap.put("0002", new int[]{3, 1, 0, 2});
        hashMap.put("0003", new int[]{2, 1, 0, 3});
        hashMap.put("0004", new int[]{3, 0, 1, 2});
        hashMap.put("0007", new int[]{3, 1, 0, 2});
        hashMap.put("0011", new int[]{3, 1, 0, 2});
        hashMap.put("0020", new int[]{3, 0, 1, 2});
        hashMap.put("0023", new int[]{3, 1, 0, 2});
        hashMap.put("0027", new int[]{3, 1, 0, 2});
        hashMap.put("0031", new int[]{3, 1, 0, 2});
        hashMap.put("0032", new int[]{3, 0, 1, 2});
        hashMap.put("0034", new int[]{3, 1, 0, 2});
        hashMap.put("0035", new int[]{3, 0, 1, 2});
        hashMap.put("0037", new int[]{3, 1, 0, 2});
        hashMap.put("0039", new int[]{3, 1, 0, 2});
        hashMap.put("0045", new int[]{3, 0, 1, 2});
        hashMap.put("0048", new int[]{3, 1, 0, 2});
        hashMap.put("0071", new int[]{3, 0, 1, 2});
        hashMap.put("0081", new int[]{3, 1, 0, 2});
        hashMap.put("0088", new int[]{3, 0, 1, 2});
        hashMap.put("0089", new int[]{3, 1, 0, 2});

        for(Map.Entry<String, int[]> map: hashMap.entrySet()) {
            int[] i = map.getValue();

            if (organization.equals(map.getKey())) {
                descList.set(i[0],desc.get(0));
                descList.set(i[1],desc.get(1));
                descList.set(i[2],desc.get(2));
                descList.set(i[3],desc.get(3));
                break;
            }
        }

        return descList;
    }

    @Transactional
    public Long startLog(String account, String connectedId, ResBatchType resBatchType, Long idxResBatch, Long idxUser) {

        Long idxCorp = repoCorp.searchIdxCorp(idxUser);

        ResBatchList result = repoResBatchList.save(
                ResBatchList.builder()
                        .idxUser(idxUser)
                        .idxCorp(idxCorp)
                        .account(account)
                        .connectedId(connectedId)
                        .resBatchType(resBatchType)
                        .idxResBatch(idxResBatch)
                        .build());
        return result.idx();
    }

    @Transactional
    public void endLog(ResBatchList resBatchList) {
        repoResBatchList.findById(resBatchList.idx()).ifPresent(resBatch -> repoResBatchList.save(
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
                        .idxCorp(resBatch.idxCorp())
                        .connectedId(resBatch.connectedId())
                        .resBatchType(resBatch.resBatchType())
                        .idxResBatch(resBatch.idxResBatch())
                        .build()));
    }

    private JSONObject[] getApiResult(String str) throws ParseException {
        JSONObject[] result = new JSONObject[2];

        JSONParser jsonParse = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParse.parse(str);

        try{
            result[0] = (JSONObject) jsonObject.get("result");
//            result[1] = (JSONObject) jsonObject.get("data");

            JSONArray jsonData = new JSONArray();
            if (jsonObject.get("data") instanceof List){
                JSONObject data = new JSONObject();
                data.put("data",jsonObject.get("data"));
                result[1] = data;
            }else{
                result[1] = (JSONObject) jsonObject.get("data");
            }

        }catch (Exception e){
            log.error("[getApiResult] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
        }

        return result;
    }

    private JSONObject[] getApiResultStock(String str) throws ParseException {
        JSONObject[] result = new JSONObject[2];

        JSONParser jsonParse = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParse.parse(str);

        try{
            result[0] = (JSONObject) jsonObject.get("result");

            JSONArray jsonData = new JSONArray();
            if (jsonObject.get("data") instanceof List){
                JSONObject data = new JSONObject();
                data.put("data",jsonObject.get("data"));
                result[1] = data;
            }else{
                result[1] = (JSONObject) jsonObject.get("data");
            }

        }catch (Exception e){
            log.error("[getApiResult] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
        }

        return result;
    }

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

    /**
     * ???????????? ?????? + ????????????
     *
     * @param idx .
     * @param idxResBatchParent .
     */
    @Async
    @Deprecated
    public void scrapingRegister1YearAll(Long idx, Long idxResBatchParent, Long idxCorp) {

        //todo auth
        if( idxCorp != null ){
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
                    log.error("[scrapingRegister1YearAll] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
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
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");

                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.DepositTrust.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount(GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay(GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit(GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName(GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency(GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate"))
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName"))
                                .build()
                        );
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.Loan.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount(GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName"))
                                .resAccountLoanExecNo( GowidUtils.getEmptyStringToString(obj, "resAccountLoanExecNo"))
                                .build()
                        );
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.ForeignCurrency.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate"))
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName"))
                                .build()
                        );
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.Fund.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resAccountInvestedCost( GowidUtils.getEmptyStringToString(obj, "resAccountInvestedCost"))
                                .resEarningsRate( GowidUtils.getEmptyStringToString(obj, "resEarningsRate"))
                                .build()
                        );
                    });
                }
            }
        });

        // user ConnectedId List
        List<ResBatchRepository.CResYears> list = repoResBatch.findStartDateMonth(idx);

        // ConnId ??? ??????????????? ?????????
        for (ResBatchRepository.CResYears resData : list) {

            int iType = 0;
            String strType = resData.getResAccountDeposit();

            String strStart, strEnd;

            strStart = resData.getStartDay();
            strEnd = resData.getEndDay();

            boolean b = strType.equals("12") || strType.equals("13") || strType.equals("14");

            if (strType.equals("10") || strType.equals("11")) {
                iType = 10;
            } else if (b) {
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

            if (iType == 0) {
                continue;
            }
            Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idx);

            if (repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                throw new RuntimeException("process kill");
            }

            try {
                if (strType.equals("10") || strType.equals("11")) {
                    strResult = getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , "1"));
                } else if (b) {
                    strResult = getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , "1"));
                } else if (strType.equals("40")) {
                    strResult = getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , resData.getResAccountLoanExecNo()));
                } else if (strType.equals("30")) {
                    strResult = getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                            , resData.getOrganization()
                            , resData.getResAccount()
                            , strStart
                            , strEnd
                            , "0"
                            , "1"));
                } else if (strType.equals("20")) {
                    strResult = getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
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
                log.error("[scrapingAccountHistory10year] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
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
        }
    }

    @Async("executor1")
    public void scrapingRegister1YearList(Long idxUser) {
        log.debug("scrapingRegister");
        ResBatch idxLog = startBatchLog(idxUser);
        try {
            scrapingRegisterAccount(idxUser, idxLog.idx());
        } finally {
            endBatchLog(idxLog.idx());
        }
    }

    @Async("executor1")
    public void scrapingBankN45DayDataList(Long idxUser) {
        log.debug("scrapingBankN45DayDataList");
        ResBatch idxLog = startBatchLog(idxUser);

        try {
            scrapingBatchAccount(idxUser, idxLog.idx());
        } catch (Exception e) {
            log.error("[scrapingBankN45DayDataList] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
        } finally {
            endBatchLog(idxLog.idx());
        }
    }



    @Async
    public void scrapingRegisterAccount45(Long idx, Long idxResBatch, Long idxCorp){

        //todo auth
        if( idxCorp != null ){
            if(repoUser.findById(idx).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN)))){
                Corp corp = repoCorp.findById(idxCorp).orElseThrow(
                        () -> CorpNotRegisteredException.builder().account(idxCorp.toString()).build()
                );
                idx = repoCorp.searchIdxUser(idxCorp);
            }
        }

        List<ConnectedMng> connectedMng = getConnectedMng(idx);
        Long finalIdx = idx;
        connectedMng.forEach(mngItem -> {
            String connId = mngItem.connectedId();
            JSONParser jsonParse = new JSONParser();

            for (String strBank : CommonConstant.LISTBANK) {

                String code = null, message = null;
                JSONObject[] strResult = new JSONObject[0];
                Long idxResBatchList = startLog(null, connId, ResBatchType.BANK, idxResBatch, finalIdx);

                if (repoResBatch.findById(idxResBatch).get().endFlag()) {
                    throw new RuntimeException("process kill");
                }

                try {
                    strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, strBank));
                } catch (Exception e) {
                    log.error("[scrapingRegisterAccount45] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
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
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.DepositTrust.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate"))
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName"))
                                .build()
                        );
                    });

                    jsonArrayResLoan.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.Loan.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName"))
                                .resAccountLoanExecNo( GowidUtils.getEmptyStringToString(obj, "resAccountLoanExecNo"))
                                .build()
                        );
                    });

                    jsonArrayResForeignCurrency.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.ForeignCurrency.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resLastTranDate( GowidUtils.getEmptyStringToString(obj, "resLastTranDate"))
                                .resAccountName( GowidUtils.getEmptyStringToString(obj, "resAccountName"))
                                .build()
                        );
                    });

                    jsonArrayResFund.forEach(item -> {
                        JSONObject obj = (JSONObject) item;
                        Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                        Long idxTemp = null;
                        if (idxLongTemp.isPresent()) {
                            idxTemp = idxLongTemp.get().idx();
                        }
                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(strBank)
                                .type(ResAccountTypeStatus.Fund.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount( GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay( GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit( GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"))
                                .resAccountNickName( GowidUtils.getEmptyStringToString(obj, "resAccountNickName"))
                                .resAccountCurrency( GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"))
                                .resAccountEndDate( GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"))
                                .resAccountInvestedCost( GowidUtils.getEmptyStringToString(obj, "resAccountInvestedCost"))
                                .resEarningsRate( GowidUtils.getEmptyStringToString(obj, "resEarningsRate"))
                                .build()
                        );
                    });
                }
            }
        });

        log.debug("start scrapingAccountHistoryList $idxResBatch={}", idxResBatch);
        List<ResBatchRepository.CResYears> list = repoResBatch.findStart45DateMonth(idx);

        // ConnId ??? ??????????????? ?????????
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

            if (iType == 0) {
                continue;
            }
            Long idxResBatchList = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatch, idx);

            if (repoResBatch.findById(idxResBatch).get().endFlag()) {
                throw new RuntimeException("process kill");
            }

            try {
                switch (strType) {
                    case "10":
                    case "11":
                        strResult = getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
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
                        strResult = getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "40":
                        strResult = getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , resData.getResAccountLoanExecNo()));
                        break;
                    case "30":
                        strResult = getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "20":
                        strResult = getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
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
                log.error("[scrapingRegisterAccount45] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
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
        serviceRisk.saveRisk45(idx, null,"");
    }

    @Async
    public void scrapingRegisterAccount(Long idx, Long idxResBatch){
        User user = repoUser.findById(idx).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );

        log.debug("start scrapingAccountHistoryList $idxResBatch={}", idxResBatch);
        List<ResBatchRepository.CResYears> list = repoResBatch.findStartDateMonth(idx);

        // ConnId ??? ??????????????? ?????????
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

            if (iType == 0) {
                continue;
            }
            Long idxResBatchList = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatch, idx);

            if (repoResBatch.findById(idxResBatch).get().endFlag()) {
                throw new RuntimeException("process kill");
            }

            try {
                switch (strType) {
                    case "10":
                    case "11":
                        strResult = getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
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
                        strResult = getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "40":
                        strResult = getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , resData.getResAccountLoanExecNo()));
                        break;
                    case "30":
                        strResult = getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                , resData.getOrganization()
                                , resData.getResAccount()
                                , strStart
                                , strEnd
                                , "0"
                                , "1"));
                        break;
                    case "20":
                        strResult = getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
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
                log.error("[scrapingService] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
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

    @Transactional(rollbackFor = Exception.class)
    void saveAccountError(String strResAccount) {
        ResAccount resAccount = repoResAccount.findTopByResAccount(strResAccount).get();
        resAccount.resAccountRiskBalance(0.0);
        repoResAccount.save(resAccount);
    }

    public ResponseEntity scrapingProcessKill(Long idxUser, Long idx, String strType) {

        boolean isMaster = isGowidMaster(idxUser);
        int returnInt = 0;
        BusinessResponse.Normal responseStatus = BusinessResponse.Normal.builder().status(false).build();

        //todo auth master
        if( strType.equals("corp") && isMaster ){
            if(repoUser.findById(idxUser).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN)))){
                Corp corp = repoCorp.findById(idx).orElseThrow(
                        () -> CorpNotRegisteredException.builder().account(idx.toString()).build()
                );
                returnInt = repoResBatch.updateProcessIdx(repoCorp.searchIdxUser(idx));
                responseStatus = BusinessResponse.Normal.builder().status(true).build();
            }
        }else if( strType.equals("user") && idxUser.equals(idx)){
            returnInt = repoResBatch.updateProcessIdx(idxUser);
            responseStatus = BusinessResponse.Normal.builder().status(true).build();
        }else{
            responseStatus = BusinessResponse.Normal.builder()
                    .value("DOES NOT HAVE GOWID-ADMIN")
                    .status(false).build();
        }

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .data(returnInt)
                .normal(responseStatus)
                .build());
    }

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

    public ResponseEntity scrapingAccount(Long idxUser,String resAccount,String strStart, String strEnd) throws ParseException {

        //todo auth
        if(repoUser.findById(idxUser).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))){

            ResAccount resAccountData = repoResAccount.findTopByResAccount(resAccount).orElseThrow(
                    () -> new RuntimeException("Waring Account")
            );

            JSONObject[] strResult = getApiResult(KR_BK_1_B_002.krbk1b002(resAccountData.connectedId()
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


    public ResponseEntity scrapingBank(String connId) {

        for (String s : CommonConstant.LISTBANK) {
            JSONObject[] strResult = new JSONObject[0];
            try {
                strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, s));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String code = strResult[0].get("code").toString();
            if (code.equals("CF-00000") || code.equals("CF-04012")) {

                JSONObject jsonData = strResult[1];
                JSONArray jsonArrayResDepositTrust = (JSONArray) jsonData.get("resDepositTrust");
                JSONArray jsonArrayResForeignCurrency = (JSONArray) jsonData.get("resForeignCurrency");
                JSONArray jsonArrayResFund = (JSONArray) jsonData.get("resFund");
                JSONArray jsonArrayResLoan = (JSONArray) jsonData.get("resLoan");

                for(Object objTrust : jsonArrayResDepositTrust){
                    JSONObject obj = (JSONObject) objTrust;
                    Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                    Long idxTemp = null;
                    if(idxLongTemp.isPresent()){
                        idxTemp = idxLongTemp.get().idx();
                    }
                    String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0,6).concat("01");

                    if(!obj.get("resAccountStartDate").toString().isEmpty()) {
                        startDate = obj.get("resAccountStartDate").toString();
                    }

                    if(!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()){
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(s)
                                .type(ResAccountTypeStatus.DepositTrust.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount(GowidUtils.getEmptyStringToString(obj, "resAccount"))
                                .resAccountDisplay(GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit"))
                                .resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName"))
                                .resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency"))
                                .resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate"))
                                .resLastTranDate(GowidUtils.getEmptyStringToString(obj,"resLastTranDate"))
                                .resAccountName(GowidUtils.getEmptyStringToString(obj,"resAccountName"))
                                .build()
                        );
                    }
                }

                jsonArrayResLoan.forEach(item -> {
                    JSONObject obj = (JSONObject) item;
                    Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                    Long idxTemp = null;
                    if(idxLongTemp.isPresent()){
                        idxTemp = idxLongTemp.get().idx();
                    }
                    String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0,6).concat("01");
                    if(!obj.get("resAccountStartDate").toString().isEmpty()) {
                        startDate = obj.get("resAccountStartDate").toString();
                    }
                    if(!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(s)
                                .type(ResAccountTypeStatus.Loan.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount(obj.get("resAccount").toString())
                                .resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit"))
                                .resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName"))
                                .resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency"))
                                .resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate"))
                                .resAccountName(GowidUtils.getEmptyStringToString(obj,"resAccountName"))
                                .resAccountLoanExecNo(GowidUtils.getEmptyStringToString(obj,"resAccountLoanExecNo"))
                                .build()
                        );
                    }
                });

                jsonArrayResForeignCurrency.forEach(item -> {
                    JSONObject obj = (JSONObject) item;
                    Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                    Long idxTemp = null;
                    if(idxLongTemp.isPresent()){
                        idxTemp = idxLongTemp.get().idx();
                    }
                    String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0,6).concat("01");
                    if(!obj.get("resAccountStartDate").toString().isEmpty()) {
                        startDate = obj.get("resAccountStartDate").toString();
                    }
                    if(!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()) {
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(s)
                                .type(ResAccountTypeStatus.ForeignCurrency.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount(obj.get("resAccount").toString())
                                .resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit"))
                                .resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName"))
                                .resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency"))
                                .resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate"))
                                .resLastTranDate(GowidUtils.getEmptyStringToString(obj,"resLastTranDate"))
                                .resAccountName(GowidUtils.getEmptyStringToString(obj,"resAccountName"))
                                .build()
                        );
                    }
                });

                jsonArrayResFund.forEach(item -> {
                    JSONObject obj = (JSONObject) item;
                    Optional<ResAccount> idxLongTemp = repoResAccount.findTopByResAccount(obj.get("resAccount").toString());
                    Long idxTemp = null;
                    if(idxLongTemp.isPresent()){
                        idxTemp = idxLongTemp.get().idx();
                    }
                    String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0,6).concat("01");
                    if(!obj.get("resAccountStartDate").toString().isEmpty()) {
                        startDate = obj.get("resAccountStartDate").toString();
                    }
                    if(!repoResAccount.findByConnectedIdAndResAccount(connId, obj.get("resAccount").toString()).isPresent()){
                        repoResAccount.save(ResAccount.builder()
                                .idx(idxTemp)
                                .connectedId(connId)
                                .organization(s)
                                .type(ResAccountTypeStatus.Fund.getStatus())
                                .searchStartDate(startDate)
                                .resAccountStartDate(GowidUtils.getEmptyStringToString(obj,"resAccountStartDate"))
                                .resAccount(obj.get("resAccount").toString())
                                .resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay"))
                                .resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
                                .resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit"))
                                .resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName"))
                                .resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency"))
                                .resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate"))
                                .resAccountInvestedCost(GowidUtils.getEmptyStringToString(obj,"resAccountInvestedCost"))
                                .resEarningsRate(GowidUtils.getEmptyStringToString(obj,"resEarningsRate"))
                                .build()
                        );
                    }
                });
            }
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().build());
    }

    @Async("executor1")
    public void runExecutor(Long idxCorp){
        // scraping10Years(idxUser);
        scraping3Years(null, null, idxCorp);
    }

    @Async("executor1")
    public void runExecutorRisk(Long idxCorp){

        serviceRisk.saveRisk45(null, idxCorp,"");
    }

    /**
     * ?????? ??? ???????????? ?????? Version-2
     * 2021.01.15
     * @param user, idxUser
     * @return
     */
    @Async
    public void scraping3Years(CustomUser user, Long idxUser, Long idxCorp) {

        if(idxCorp != null ){
            idxUser = repoCorp.searchIdxUser(idxCorp);
        }

        ResBatch idxLog = startBatchLog(idxUser);
        try {
            scrapingBatch(idxUser, idxCorp, idxLog.idx());
        } catch (Exception e) {
            log.error("[scraping3Years] $user: {}  $corp : {}  $ERROR({}): {}", user.idx(),  idxCorp, e.getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        } finally {
            endBatchLog(idxLog.idx());
        }
    }


    public boolean scrapingBatch(Long idxUser, Long idxCorp, Long idxResBatchParent){

        // ???????????? ????????? Error??? ??????
        scrapingAccountError(idxUser);

        // ??????????????????
        scrapingBatchAccount(idxUser,idxResBatchParent);

        // ????????????
        scrapingBatchStock(idxUser,idxResBatchParent);

        // ????????? ???????????????
        scrapingBatchTaxInvoice(idxUser, idxResBatchParent);

        // ???????????? ??? ????????????
        // todo ???????????? ???????????? ?????? ??????
        // scrapingBatchHistory_v2(idxUser, idxCorp, idxResBatchParent);
        scrapingBatchHistory(idxUser, idxResBatchParent, repoResBatch.find10yearMonth(idxUser, true));

        return true;
    }

    private void scrapingAccountError(Long idxUser) {
        Corp corp = repoCorp.findById(repoCorp.searchIdxCorp(idxUser)).get();
        repoResAccount.accountStatusError(corp.idx());
    }

    void scrapingBatchTaxInvoice(Long idxUser, Long idxResBatchParent) {
        User user = repoUser.findById(idxUser).get();
        Corp corp = repoCorp.findById(repoCorp.searchIdxCorp(idxUser)).get();

        SaasTrackerProgress progress = repoSaasTrackerProgress.findByUser(user).orElse(null);
        Boolean boolSaasExec = progress != null && progress.status() > 0;

        if( boolSaasExec ){
            ResConCorpList resConCorpList = getSaasTaxInvoice(user.idx());

            //??????????????? ?????? ?????? 2?????? ??????????????? ??????
            if(user != null && corp != null){

                for(int i = 24 ; i >= 0 ; i--) {
                    YearMonth ym = YearMonth.from(LocalDate.now().minusMonths(i));
                    String startDate = ym.format(DateTimeFormatter.ofPattern("yyyyMM")) + "01";
                    String endDate = ym.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                    if (i == 0) {
                        endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    }

                    if (resConCorpList != null) {
                        JSONObject[] strResult = getBatchTaxInvoice(
                                resConCorpList.connectedId(),
                                idxResBatchParent,
                                startDate,
                                endDate,
                                corp.resCompanyIdentityNo());

                        if (strResult[0].get("code").toString().equals("CF-00000")) {

                            log.debug(" save befo {}, {}", startDate, endDate);
                            JSONArray jsonData = new JSONArray();
                            if (strResult[1].get("data") instanceof List){
                                jsonData = (JSONArray) strResult[1].get("data");
                            }else{
                                jsonData.add(strResult[1].get("data"));
                            }

                            saveTaxInvoice( jsonData
                                    , idxUser
                                    , corp.idx()
                                    , startDate
                                    , endDate
                            );
                        }

                        if(strResult[0] != null ){

                            String code = GowidUtils.getEmptyStringToString(strResult[0], "code");
                            String transactionId = GowidUtils.getEmptyStringToString(strResult[0], "transactionId");
                            String message = GowidUtils.getEmptyStringToString(strResult[0], "message");

                            repoResBatchList.save(
                                    ResBatchList.builder()
                                            .idxUser(idxUser)
                                            .idxCorp(corp.idx())
                                            .idxResBatch(idxResBatchParent)
                                            .resBatchType(ResBatchType.NT)
                                            .connectedId(resConCorpList.connectedId())
                                            .startDate(startDate)
                                            .endDate(endDate)
                                            .errCode(code)
                                            .transactionId(transactionId)
                                            .errMessage(message)
                                            .build());
                        }
                    }
                }
            }
        }
    }

    private ResConCorpList getSaasTaxInvoice(Long idxUser) {
        List<String> connectedIdList = new ArrayList<>();
        ResConCorpList resConCorpList = new ResConCorpList();
        List<ConnectedMngDto> connectedMngList = repoConnectedMng.findByIdxUserAndStatusInOrderByCreatedAtDesc(idxUser, connectedMngStatusList)
                .stream().map(ConnectedMngDto::from).collect(Collectors.toList());

        for(ConnectedMngDto obj : connectedMngList){
            connectedIdList.add(obj.getConnectedId());
        }

        if( connectedMngList.size() > 0 ){
            resConCorpList = repoResConCorpList.findTopByConnectedIdInAndStatusInAndBusinessTypeAndOrganizationOrderByCreatedAtDesc(
                    connectedIdList, resConCorpListStatuses, "NT", "0002"
            );
        }

        return resConCorpList;
    }


    void saveTaxInvoice(JSONArray data, Long idxUser, Long idxCorp, String startDate, String endDate) {
        String strDefault = null;
        repoResTaxInvoice.deleteTaxInvoiceAndResIssueDate(idxCorp , startDate, endDate);
        if(data != null){
            for( Object item :data ) {
                JSONObject obj = (JSONObject) item;

                repoResTaxInvoice.save(
                        ResTaxInvoice.builder()
                                .idxUser(idxUser)
                                .idxCorp(idxCorp)
                                .resIssueNm(GowidUtils.getEmptyStringToString(obj, "resIssueNm"))
                                .resTaxAmt(GowidUtils.getEmptyStringToString(obj, "resTaxAmt"))
                                .resIssueDate(GowidUtils.getEmptyStringToString(obj, "resIssueDate"))
                                .resApprovalNo(GowidUtils.getEmptyStringToString(obj, "resApprovalNo"))
                                .resSupplyValue(GowidUtils.getEmptyStringToString(obj, "resSupplyValue"))
                                .resReportingDate(GowidUtils.getEmptyStringToString(obj, "resReportingDate"))
                                .resTransferDate(GowidUtils.getEmptyStringToString(obj, "resTransferDate"))
                                .resSupplierRegNumber(GowidUtils.getEmptyStringToString(obj, "resSupplierRegNumber"))
                                .resSupplierEstablishNo(GowidUtils.getEmptyStringToString(obj, "resSupplierEstablishNo"))
                                .resSupplierCompanyName( GowidUtils.getEmptyStringToString(obj, "resSupplierCompanyName"))
                                .resSupplierName( GowidUtils.getEmptyStringToString(obj, "resSupplierName"))
                                .resContractorRegNumber( GowidUtils.getEmptyStringToString(obj, "resContractorRegNumber"))
                                .resContractorEstablishNo( GowidUtils.getEmptyStringToString(obj, "resContractorEstablishNo"))
                                .resContractorCompanyName( GowidUtils.getEmptyStringToString(obj, "resContractorCompanyName"))
                                .resContractorName( GowidUtils.getEmptyStringToString(obj, "resContractorName"))
                                .resTotalAmount( GowidUtils.getEmptyStringToString(obj, "resTotalAmount"))
                                .resETaxInvoiceType( GowidUtils.getEmptyStringToString(obj, "resETaxInvoiceType"))
                                .resNote( GowidUtils.getEmptyStringToString(obj, "resNote"))
                                .resReceiptOrCharge( GowidUtils.getEmptyStringToString(obj, "resReceiptOrCharge"))
                                .resEmail( GowidUtils.getEmptyStringToString(obj, "resEmail"))
                                .resEmail1( GowidUtils.getEmptyStringToString(obj, "resEmail1"))
                                .resEmail2( GowidUtils.getEmptyStringToString(obj, "resEmail2"))
                                .resRepItems( GowidUtils.getEmptyStringToString(obj, "resRepItems"))
                                .build());
            }
        }
    }

    private void scrapingBatchStock(Long idxUser, Long idxResBatchParent) {
        try{

            for( ConnectedMng connectedMng : getConnectedMng(idxUser)){
                if(connectedMng.status().equals(ConnectedMngStatus.NORMAL)){
                    for( ResConCorpList resConCorpList : repoResConCorpList.findByConnectedId(connectedMng.connectedId())){
                        if(resConCorpList.status().equals(ConnectedMngStatus.NORMAL) && resConCorpList.businessType().equals("ST")){
                            Long idxResBatchList = checkProcess(connectedMng.connectedId(), idxResBatchParent, idxUser);

                            log.debug("scrapingBatchStock $organization='{}'",resConCorpList);
                            JSONObject[] strResult = getBatchStockList(
                                    connectedMng.connectedId(),
                                    resConCorpList.organization(),
                                    idxResBatchParent
                            );

                            if(strResult[0] != null && checkCode(strResult[0])){
                                JSONArray jsonData = new JSONArray();
                                if (strResult[1].get("data") instanceof List){
                                    jsonData = (JSONArray) strResult[1].get("data");
                                }else{
                                    jsonData.add(strResult[1].get("data"));
                                }

                                String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");
                                saveJsonDataToResAccount(ResAccountTypeStatus.Stock.getStatus(), jsonData,connectedMng.connectedId(),resConCorpList.organization(),startDate);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            log.debug("void [scrapingBatchStock] e = {}", e);
        }finally {
            log.debug("end [scrapingBatchStock] idxUser = {}", idxUser);
        }
    }

    private void scrapingBatchAccount(Long idxUser, Long idxResBatchParent) {

        User user = findUserInfo(idxUser);

        for( ConnectedMng connectedMng : repoConnectedMng.findByCorpAndStatusIn(user.corp(), connectedMngStatusList )){
            for( ResConCorpList resConCorpList : repoResConCorpList.findByConnectedIdAndStatusIn(connectedMng.connectedId(), resConCorpListStatuses)){
                if(resConCorpList.businessType().equals("BK")){
                    Long idxResBatchList = checkProcess(connectedMng.connectedId(), idxResBatchParent, idxUser);

                    JSONObject[] strResult = getBatchAccountList(
                            connectedMng.connectedId(),
                            resConCorpList.organization(),
                            idxResBatchParent
                    );

                    if(checkCode(strResult[0])) {
                        JSONObject jsonData = strResult[1];
                        JSONArray jsonArrayResDepositTrust = (JSONArray) jsonData.get("resDepositTrust");
                        JSONArray jsonArrayResForeignCurrency = (JSONArray) jsonData.get("resForeignCurrency");
                        JSONArray jsonArrayResFund = (JSONArray) jsonData.get("resFund");
                        JSONArray jsonArrayResLoan = (JSONArray) jsonData.get("resLoan");

                        String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE).substring(0, 6).concat("01");

                        if (jsonArrayResDepositTrust.size() > 0) {
                            saveJsonDataToResAccount(ResAccountTypeStatus.DepositTrust.getStatus(), jsonArrayResDepositTrust, connectedMng.connectedId(), resConCorpList.organization(), startDate);
                        }

                        if (jsonArrayResForeignCurrency.size() > 0) {
                            saveJsonDataToResAccount(ResAccountTypeStatus.ForeignCurrency.getStatus(), jsonArrayResForeignCurrency, connectedMng.connectedId(), resConCorpList.organization(), startDate);
                        }

                        if (jsonArrayResFund.size() > 0) {
                            saveJsonDataToResAccount(ResAccountTypeStatus.Fund.getStatus(), jsonArrayResFund, connectedMng.connectedId(), resConCorpList.organization(), startDate);
                        }

                        if(jsonArrayResLoan.size() > 0) {
                            saveJsonDataToResAccount(ResAccountTypeStatus.Loan.getStatus(), jsonArrayResLoan, connectedMng.connectedId(), resConCorpList.organization(), startDate);
                        }
                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    User findUserInfo(Long idxUser) {
        return repoUser.findById(idxUser).get();
    }

    void scrapingBatchHistory_v2(Long idxUser, Long idxCorp, Long idxResBatchParent) throws Exception {
        User user = repoUser.findById(idxUser).get();
        Corp corp = repoCorp.findById(idxCorp).get();

        String toDay = CommonUtil.getNowYYYYMMDD(); // ??????
        String pastDay = CommonUtil.getMinusDay(toDay,365*3).replaceAll("-",""); // 3??????
        List<ConnectedMng> connectedMngList = repoConnectedMng.findByIdxUser(idxUser);
        ArrayList<String> connectedIdArray = new ArrayList<>();

        for(ConnectedMng connetedMng :connectedMngList ) {
            if (connetedMng.status().equals(ConnectedMngStatus.NORMAL)) {
                connectedIdArray.add(connetedMng.connectedId());
            }
        }

        if( connectedIdArray != null ){
            List<ResAccount> resAccountList = repoResAccount.findByConnectedIdInAndUpdatedAtAfter( connectedIdArray, LocalDateTime.now().minusDays(1) );

            for (ResAccount resAccount : resAccountList) {
                BatchDate batchDate = repoBatchDate.findByAccount(resAccount.resAccount()).orElseGet(
                        () -> BatchDate.builder()
                                .account(resAccount.resAccount())
                                .connectedId(resAccount.connectedId())
                                .idxCorp(corp.idx())
                                .idxUser(user.idx())
                                .startDate(pastDay)
                                .endDate(toDay)
                                .build()
                );

                // ???????????? ????????????
                String startDate = procScrapingPeriod(pastDay, batchDate.endDate(), resAccount, corp, user, idxResBatchParent);
                log.debug("[procAccountHistory3year] $startDate = {}",startDate);
                batchDate.startDate(startDate);

                // ???????????? ????????????
                String endDate = procScrapingPeriod( batchDate.endDate(), toDay, resAccount, corp, user, idxResBatchParent);
                log.debug("[procAccountHistory3year] $endDate = {}",endDate);
                batchDate.endDate(endDate);

                repoBatchDate.save(batchDate);
            }
        }
    }


    private void scrapingBatchHistory(Long idxUser, Long idxResBatchParent, List<ResBatchRepository.CResYears> list) {

        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );

        Long idxCorp = repoCorp.searchIdxCorp(user.idx());

            // ConnId ??? ??????????????? ?????????
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

                if (iType == 0) {
                    continue;
                }
                Long idxResBatch = startLog(resData.getResAccount(), resData.getConnectedId(), ResBatchType.ACCOUNT, idxResBatchParent, idxUser);

                if (!repoResBatch.findById(idxResBatchParent).get().endFlag()) {
                    try {

                        switch (strType) {
                            case "10":
                            case "11":
                                strResult = getApiResult(KR_BK_1_B_002.krbk1b002(resData.getConnectedId()
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
                                strResult = getApiResult(KR_BK_1_B_003.krbk1b003(resData.getConnectedId()
                                        , resData.getOrganization()
                                        , resData.getResAccount()
                                        , strStart
                                        , strEnd
                                        , "0"
                                        , "1"));
                                break;
                            case "40":
                                strResult = getApiResult(KR_BK_1_B_004.krbk1b004(resData.getConnectedId()
                                        , resData.getOrganization()
                                        , resData.getResAccount()
                                        , strStart
                                        , strEnd
                                        , "0"
                                        , resData.getResAccountLoanExecNo()));
                                break;
                            case "30":
                                strResult = getApiResult(KR_BK_1_B_006.krbk1b006(resData.getConnectedId()
                                        , resData.getOrganization()
                                        , resData.getResAccount()
                                        , strStart
                                        , strEnd
                                        , "0"
                                        , "1"));
                                break;
                            case "20":
                                strResult = getApiResult(KR_BK_1_B_005.krbk1b005(resData.getConnectedId()
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
                        log.error("[scrapingService] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
                    }

                    if (strResult[1] != null && (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012"))) {
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


    private void saveJsonDataToResAccount(String accountType, JSONArray jsonArray, String connectedId, String bank, String startDate) {

        List<String> accountList = new ArrayList<>();
        List<String> deleteAccountList = new ArrayList<>();

        for(Object objJson : jsonArray) {
            JSONObject o = (JSONObject) objJson;
            String account = GowidUtils.getEmptyStringToString(o, "resAccount");
            accountList.add(account);

            if( accountList.stream().filter(a -> a.equals(account)).count() > 1 ){
                repoResAccount.deleteByResAccount(account);
            }
        }

        for( Object item :jsonArray ){
            try {
                JSONObject obj = (JSONObject) item;
                String resAccountCurrency = "";

                if (!GowidUtils.getEmptyStringToString(obj, "resAccountCurrency").isEmpty()) {
                    resAccountCurrency = obj.get("resAccountCurrency").toString();
                }

                ResAccount resAccount = ResAccount.builder()
                        .connectedId(connectedId)
                        .organization(bank)
                        .type(accountType)
                        .build();

                if (accountList.stream().filter(a -> a.equals(obj.get("resAccount"))).count() < 2) {
                    resAccount = repoResAccount.findTopByResAccountAndResAccountCurrency(obj.get("resAccount").toString(), resAccountCurrency).orElse(
                            ResAccount.builder()
                                    .connectedId(connectedId)
                                    .organization(bank)
                                    .type(accountType)
                                    .build()
                    );
                }

                resAccount.status(ResAccountStatus.NORMAL);
                resAccount.connectedId(connectedId);
                resAccount.searchStartDate(startDate);
                resAccount.resAccountStartDate(GowidUtils.getEmptyStringToString(obj, "resAccountStartDate"));
                resAccount.resAccount(GowidUtils.getEmptyStringToString(obj, "resAccount"));
                resAccount.resAccountDisplay(GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"));

                if (accountType.equals(ResAccountTypeStatus.Stock.getStatus())) {
                    resAccount.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resValuationAmt").toString()));
                } else {
                    resAccount.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()));
                }

                resAccount.resAccountDeposit(GowidUtils.getEmptyStringToString(obj, "resAccountDeposit"));
                resAccount.resAccountNickName(GowidUtils.getEmptyStringToString(obj, "resAccountNickName"));
                resAccount.resAccountCurrency(GowidUtils.getEmptyStringToString(obj, "resAccountCurrency"));
                resAccount.resAccountEndDate(GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"));
                resAccount.resLastTranDate(GowidUtils.getEmptyStringToString(obj, "resLastTranDate"));
                resAccount.resAccountName(GowidUtils.getEmptyStringToString(obj, "resAccountName"));
                resAccount.resOverdraftAcctYN(GowidUtils.getEmptyStringToString(obj, "resOverdraftAcctYN"));
                resAccount.resAccountInvestedCost(GowidUtils.getEmptyStringToString(obj, "resAccountInvestedCost"));
                resAccount.resEarningsRate(GowidUtils.getEmptyStringToString(obj, "resEarningsRate"));
                resAccount.resAccountLoanExecNo(GowidUtils.getEmptyStringToString(obj, "resAccountLoanExecNo"));
                resAccount.resOverdraftAcctYN(GowidUtils.getEmptyStringToString(obj, "resOverdraftAcctYN"));
                resAccount.resAccountEndDate(GowidUtils.getEmptyStringToString(obj, "resAccountEndDate"));

                repoResAccount.save(resAccount);
            }catch (Exception e){
                log.debug("account save error = " + e.getMessage());
            }
        }
    }

    private boolean checkCode(JSONObject jsonObject) {
        return ResponseCode.CF00000.getCode().equals(jsonObject.get("code").toString())
            || ResponseCode.CF04012.getCode().equals(jsonObject.get("code").toString());
    }

    private JSONObject[] getBatchAccountList(String connectedId, String organization, Long idxResBatchList) {
        JSONObject[] strResult = new JSONObject[0];
        try {
            strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connectedId, organization));
        } catch (Exception e) {
            log.debug("scrapingAccountHistory10year $message={}" , e.getMessage());
        } finally {
            endLog(ResBatchList.builder()
                    .idx(idxResBatchList)
                    .bank(organization)
                    .errCode(strResult[0].get("code").toString())
                    .transactionId(strResult[0].get("transactionId").toString())
                    .errMessage(strResult[0].get("message").toString())
                    .build());
        }
        return strResult;
    }

    private JSONObject[] getBatchStockList(String connectedId, String organization, Long idxResBatchList) {
        JSONObject[] strResult = new JSONObject[1];
        try {
            strResult = getApiResultStock(KR_ST_1_S_001.krst1a001(connectedId, organization));
        } catch (Exception e) {
            log.debug("scrapingAccountHistory10year $message={}" , e.getMessage());
        } finally {
            endLog(ResBatchList.builder()
                    .idx(idxResBatchList)
                    .bank(organization)
                    .errCode(GowidUtils.getEmptyStringToString(strResult[0], "code"))
                    .transactionId(GowidUtils.getEmptyStringToString(strResult[0], "transactionId"))
                    .errMessage(GowidUtils.getEmptyStringToString(strResult[0], "message"))
                    .build());
        }
        return strResult;
    }



    private JSONObject[] getBatchTaxInvoice(String connectedId,Long idxResBatchList,String startDate,String endDate,String identity) {
        JSONObject[] result = new JSONObject[2];
        try {
            String str = TAX_INVOICE.tax_invoice(
                    "0002",
                    connectedId,
                    "01",
                    "02",
                    startDate,
                    endDate,
                    "02",
                    "02",
                    "02",
                    identity.replaceAll("-", "")
            );

            JSONParser jsonParse = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParse.parse(str);

            result[0] = (JSONObject) jsonObject.get("result");
            result[1] = jsonObject;

        } catch (Exception e) {
            log.debug("getBatchTaxInvoice $message={}" , e.getMessage());
        } finally {
            endLog(ResBatchList.builder()
                    .idx(idxResBatchList)
                    .bank("0002")
                    .errCode(result[0].get("code").toString())
                    .transactionId(result[0].get("transactionId").toString())
                    .errMessage(result[0].get("message").toString())
                    .build());
        }
        return result;
    }

    private Long checkProcess(String connectedId,Long idxResBatchParent,Long idxUser) {
        Long idxResBatchList = startLog(null, connectedId, ResBatchType.BANK, idxResBatchParent, idxUser);

        if (repoResBatch.findById(idxResBatchParent).get().endFlag()) {
            throw new RuntimeException("process kill");
        }
        return idxResBatchParent;
    }

    private Integer getServerIp() throws UnknownHostException {
        String serverIp = InetAddress.getLocalHost().getHostAddress();
        String iServerIp = serverIp.substring(serverIp.length()-1);
        return Integer.parseInt(serverIp)/2;
    }

    private String procScrapingPeriod(String startDate, String endDate, ResAccount resAccount, Corp corp, User user, Long idxResBatchParent) throws Exception {
        Long idxCorp = corp.idx();
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String baseStartDate = startDate;
        String baseEndDate = endDate;
        String findEndData = endDate;
        Boolean boolProc = false; // ????????????
        int diffDay = 0;

        while(true){
            if( findEndData.equals(baseStartDate) ){
                getCodefData(startDate, endDate, diffDay, resAccount, corp, user, idxResBatchParent);
                return startDate;
            }

            endDate = findEndData;
            cal.setTime(df.parse(endDate));
            diffDay = diffDays(startDate, endDate);

            if (diffDay < 0) {
                return startDate;
            }

            // getCodefData
            boolProc = getCodefData(startDate, endDate, diffDay, resAccount, corp, user, idxResBatchParent);
            if (!boolProc && diffDay < 1){
                return startDate;
            }

            // ??????????????? loop Data ??????
            if (boolProc) {
                baseEndDate = getPlusDate(endDate, -(diffDay+1));
                cal.setTime(df.parse(baseEndDate));
                diffDay = round((diffDay+1) * 5);
                cal.add(Calendar.DATE, -diffDay);
            } else {
                diffDay = -round(diffDay / 3);
                cal.add(Calendar.DATE, diffDay);
            }

            startDate = df.format(cal.getTime());

            if (Integer.parseInt(baseStartDate) > Integer.parseInt(startDate)) {
                startDate = baseStartDate;
            }
        }
    }

    public static String getPlusDate(String startDate, int i) throws java.text.ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(startDate));
        cal.add(Calendar.DATE,i);
        return df.format(cal.getTime());
    }

    public boolean getCodefData(String startDate, String endDate, int diffDay, ResAccount resAccount, Corp corp, User user, Long idxResBatchParent) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable task = () -> procGetPeriodData(startDate, endDate, resAccount, corp, user, idxResBatchParent);

        Future future = executor.submit(task);
        return future.get(1000 * 60 * 5, TimeUnit.MILLISECONDS).equals(null);
    }
    public static int diffDays(String startDate, String endDate) throws java.text.ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date sDate = df.parse(startDate);
        Date eDate = df.parse(endDate);
        System.out.println("diffday - " + (eDate.getTime() - sDate.getTime()) / (24*60*60*1000));
        return round((eDate.getTime() - sDate.getTime()) / (24*60*60*1000));
    }

    public boolean procGetPeriodData(String startDate, String endDate, ResAccount resAccount, Corp corp, User user, Long idxResBatchParent) {

        boolean endFlag = true;
        JSONObject[] strResult = new JSONObject[0];

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            Optional<ResBatch> resBatch = repoResBatch.findById(idxResBatchParent);
            if ( resBatch.isPresent() && !repoResBatch.findById(idxResBatchParent).get().endFlag()){

                try{
                    switch (resAccount.resAccountDeposit()) {
                        case "10":
                        case "11":
                            strResult = getApiResult(KR_BK_1_B_002.krbk1b002(resAccount.connectedId()
                                    , resAccount.organization()
                                    , resAccount.resAccount()
                                    , startDate
                                    , endDate
                                    , "0"
                                    , "1"));
                            break;
                        case "12":
                        case "13":
                        case "14":
                            strResult = getApiResult(KR_BK_1_B_003.krbk1b003(resAccount.connectedId()
                                    , resAccount.organization()
                                    , resAccount.resAccount()
                                    , startDate
                                    , endDate
                                    , "0"
                                    , "1"));
                            break;
                        case "40":
                            strResult = getApiResult(KR_BK_1_B_004.krbk1b004(resAccount.connectedId()
                                    , resAccount.organization()
                                    , resAccount.resAccount()
                                    , startDate
                                    , endDate
                                    , "0"
                                    , resAccount.resAccountLoanExecNo()));
                            break;
                        case "30":
                            strResult = getApiResult(KR_BK_1_B_006.krbk1b006(resAccount.connectedId()
                                    , resAccount.organization()
                                    , resAccount.resAccount()
                                    , startDate
                                    , endDate
                                    , "0"
                                    , "1"));
                            break;
                        case "20":
                            strResult = getApiResult(KR_BK_1_B_005.krbk1b005(resAccount.connectedId()
                                    , resAccount.organization()
                                    , resAccount.resAccount()
                                    , startDate
                                    , endDate
                                    , "0"
                                    , resAccount.resAccountCurrency()
                            ));
                            break;
                        default:
                            strResult = getApiResultStock(KR_ST_1_S_002.krst1a002(resAccount.connectedId()
                                    , resAccount.organization()
                                    , resAccount.resAccount()
                                    , startDate
                                    , endDate
                                    , "0"
                                    , ""
                            ));
                            break;
                    }

                    if (strResult[0].get("code").toString().equals("CF-00000") || strResult[0].get("code").toString().equals("CF-04012")) {
                        saveAccount(Integer.parseInt(resAccount.resAccountDeposit())
                                , strResult[1]
                                , (JSONArray) strResult[1].get("resTrHistoryList")
                                , resAccount.connectedId()
                                , BankDto.AccountBatch.builder().startDate(startDate).endDate(endDate).build()
                        );
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    endFlag = false;
                }finally {

                    user = findUserInfo(user.idx());

                    repoResBatchList.save(
                            ResBatchList.builder()
                                    .idxUser(user.idx())
                                    .idxCorp(user.corp().idx())
                                    .idxResBatch(idxResBatchParent)
                                    .resBatchType(ResBatchType.ACCOUNT)
                                    .bank(resAccount.organization())
                                    .connectedId(resAccount.connectedId())
                                    .account(resAccount.resAccount())
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .errCode(strResult[0].get("code").toString())
                                    .transactionId(strResult[0].get("transactionId").toString())
                                    .errMessage(strResult[0].get("message").toString())
                                    .build());
                }
            } else {
                throw ProcessKillException.builder().build();
            }
        }

        return endFlag;
    }

    @Transactional(rollbackFor = Exception.class)
    void saveAccount(int iType, JSONObject jsonData, JSONArray jsonArrayResTrHistoryList, String connectedId,  BankDto.AccountBatch dto) {
        String strDefault = null;
        repoResAccountHistory.deleteResAccountTrDate(jsonData.get("resAccount").toString(), dto.getStartDate(), dto.getEndDate());

        repoResAccount.findTopByConnectedIdAndResAccount(connectedId, jsonData.get("resAccount").toString()).ifPresent(
                resAccount -> {
                    if (!jsonArrayResTrHistoryList.isEmpty()) {
                        for( Object item :jsonArrayResTrHistoryList ) {
                            JSONObject obj = (JSONObject) item;

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
                                            .resRoundNo(GowidUtils.getEmptyStringToString(obj, "resRoundNo"))
                                            .resMonth(GowidUtils.getEmptyStringToString(obj, "resMonth"))
                                            .resTransTypeNm( GowidUtils.getEmptyStringToString(obj, "resTransTypeNm"))
                                            .resType( GowidUtils.getEmptyStringToString(obj, "resType"))
                                            .resTranAmount( GowidUtils.getEmptyStringToString(obj, "resTranAmount"))
                                            .resPrincipal( GowidUtils.getEmptyStringToString(obj, "resPrincipal"))
                                            .resInterest( GowidUtils.getEmptyStringToString(obj, "resInterest"))
                                            .commStartDate( GowidUtils.getEmptyStringToString(obj, "commStartDate"))
                                            .commEndDate( GowidUtils.getEmptyStringToString(obj, "commEndDate"))
                                            .resLoanBalance( GowidUtils.getEmptyStringToString(obj, "resLoanBalance"))
                                            .resInterestRate( GowidUtils.getEmptyStringToString(obj, "resInterestRate"))
                                            .resTranNum( GowidUtils.getEmptyStringToString(obj, "resTranNum"))
                                            .resBasePrice( GowidUtils.getEmptyStringToString(obj, "resBasePrice"))
                                            .resBalanceNum( GowidUtils.getEmptyStringToString(obj, "resBalanceNum"))
                                            .resValuationAmt("" + GowidUtils.getEmptyStringToString(obj, "resValuationAmt") )
                                            .resAccount(resAccount.resAccount())
                                            .build());
                        }
                    }
                });
    }



    @SneakyThrows
    public void scrapExchange(){
        String exchangeKey = koreaeximConfig.getDomainUrl();
        String searchdate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String urlExchangePath = exchangeKey.concat("?data=AP01&authkey=").concat(koreaeximConfig.getApiKey())
                .concat("&searchdate=").concat(searchdate);

        URL url = new URL(urlExchangePath);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONParser jsonParse = new JSONParser();
        JSONArray jsonObject = (JSONArray) jsonParse.parse(response.toString());

        if(ObjectUtils.isEmpty(jsonObject)){
            throw new SystemException(ErrorCode.Api.NOT_FOUND);
        }

        for (Object item : jsonObject) {
            JSONObject obj = (JSONObject) item;

            ResExchangeRate resExchangeRate = repoExchangeRate.findByDateAndCountry(searchdate,
                    GowidUtils.getEmptyStringToString(obj, "cur_unit"));

            if(GowidUtils.getEmptyStringToString(obj, "result").equals("1")
                    && ObjectUtils.isEmpty(resExchangeRate)) {
                repoExchangeRate.save(
                        ResExchangeRate.builder()
                                .country(GowidUtils.getEmptyStringToString(obj, "cur_unit"))
                                .date(searchdate)
                                .standardPrice(Float.valueOf(GowidUtils.getEmptyStringToString(obj, "deal_bas_r").replaceAll(",","")))
                                .standard(Float.valueOf(GowidUtils.getEmptyStringToString(obj, "bkpr").replaceAll(",","")))
                                .sending(Float.valueOf(GowidUtils.getEmptyStringToString(obj, "tts").replaceAll(",","")))
                                .receiving(Float.valueOf(GowidUtils.getEmptyStringToString(obj, "ttb").replaceAll(",","")))
                        .build()
                );
            }
        }
    }
}