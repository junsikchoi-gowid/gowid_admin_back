package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.helper.Account;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import com.nomadconnection.dapp.codef.io.sandbox.bk.KR_BK_1_B_001;
import com.nomadconnection.dapp.codef.io.sandbox.pb.CORP_REGISTER;
import com.nomadconnection.dapp.codef.io.sandbox.pb.PROOF_ISSUE;
import com.nomadconnection.dapp.codef.io.sandbox.pb.STANDARD_FINANCIAL;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefService {

	private final UserRepository repoUser;
	private final ResAccountRepository repoResAccount;
	private final ConnectedMngRepository repoConnectedMng;
	private final CorpRepository repoCorp;
	private final ResBatchRepository repoResBatch;
	private final ScrapingService serviceScraping;

	private final ResRegisterEntriesListRepository repoResRegisterEntriesList;
	private final ResCompanyNmListRepository repoResCompanyNmList;
	private final ResUserAddrListRepository repoResUserAddrList;
	private final ResNoticeMethodListRepository repoResNoticeMethodList;
	private final ResOneStocAmtListRepository repoResOneStocAmtList;
	private final ResTCntStockIssueListRepository repoResTCntStockIssueList;
	private final ResStockListRepository repoResStockList;
	private final ResPurposeListRepository repoResPurposeList;
	private final ResRegistrationHisListRepository repoResRegistrationHisList;
	private final ResBranchListRepository repoResBranchList;
	private final ResIncompetenceReasonListRepository repoResIncompetenceReasonList;
	private final ResJointPartnerListRepository repoResJointPartnerList;
	private final ResManagerListRepository repoResManagerList;
	private final ResConvertibleBondListRepository repoResConvertibleBondList;
	private final ResWarrantBondListRepository repoResWarrantBondList;
	private final ResParticipatingBondListRepository repoResParticipatingBondList;
	private final ResStockOptionListRepository repoResStockOptionList;
	private final ResTypeStockContentListRepository repoResTypeStockContentList;
	private final ResCCCapitalStockListRepository repoResCCCapitalStockList;
	private final ResEtcListRepository repoResEtcList;
	private final ResCorpEstablishDateListRepository repoResCorpEstablishDateList;
	private final ResRegistrationRecReasonListRepository repoResRegistrationRecReasonList;
	private final ResCEOListRepository repoResCEOList;
	private final ResChangeDateListRepository repoResChangeDateList;
	private final ResStockItemListRepository repoResStockItemList;
	private final ResRegistrationDateListRepository repoResRegistrationDateList;
	private final ResConvertibleBondItemListRepository repoResConvertibleBondItemList;
	private final ResWarrantBondItemListRepository repoResWarrantBondItemList;
	private final ResParticipatingBondItemListRepository repoResParticipatingBondItemList;
	private final ResTypeStockContentItemListRepository repoResTypeStockContentItemList;
	private final ResCCCapitalStockItemListRepository repoResCCCapitalStockItemList;

	private final ResStandardFinancialListRepository repoResStandardFinancialList;
	private final ResBalanceSheetRepository repoResBalanceSheet;
	private final ResIncomeStatementRepository repoResIncomeStatement;
	private final ResCostSpecificationListRepository repoResCostSpecificationList;
	private final ResFinancialStatementListRepository repoResFinancialStatementList;
	private final ResCostSpecificationRepository repoResCostSpecification;

	private final D1000Repository repoD1000;
	private final D1100Repository repoD1100;
	private final D1200Repository repoD1200;
	private final D1300Repository repoD1300;
	private final D1400Repository repoD1400;
	private final D1510Repository repoD1510;

	private final CardIssuanceInfoRepository repoCardIssuance;

	private final String urlPath = CommonConstant.getRequestDomain();

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity findConnectedIdList(Long idx) {

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				repoConnectedMng.findIdxUser(idx)
		).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity findConnectedIdListCorp(Long idxUser, Long idxCorp) {

		if(idxCorp != null){
			if(repoUser.findById(idxUser).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))){
				idxUser = repoCorp.searchIdxUser(idxCorp);
			}
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				repoConnectedMng.findIdxUser(idxUser)
		).build());
	}


	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity registerAccount(ConnectedMngDto.Account dto, Long idx) {
		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.CREATE_ACCOUNT;
		List<BankDto.ResAccountDto> resAccount = null;

		for( String s : CommonConstant.LISTBANK){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
			accountMap1.put("certType",     CommonConstant.CERTTYPE);
			accountMap1.put("certFile",     dto.getCertFile());
			list.add(accountMap1);
		}

		for( String s : CommonConstant.LISTCARD){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.CARDTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
			accountMap1.put("certType",     CommonConstant.CERTTYPE);
			accountMap1.put("certFile",     dto.getCertFile());
			list.add(accountMap1);
		}

		accountMap1 = new HashMap<>();
		accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
		accountMap1.put("businessType",	CommonConstant.REVENUETYPE);  // 업무구분코드
		accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
		accountMap1.put("organization",	CommonConstant.REVENUE);// 기관코드
		accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
		accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
		accountMap1.put("certType",     CommonConstant.CERTTYPE);
		accountMap1.put("certFile",     dto.getCertFile());

		list.add(accountMap1);

		bodyMap.put("accountList", list);
		String strObject = ApiRequest.request(createUrlPath, bodyMap);

		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParse.parse(strObject);

		String strResultCode = jsonObject.get("result").toString();
		String strResultData = jsonObject.get("data").toString();

		// insert user table - connectedId save
		User user = repoUser.findById(idx).orElseThrow(
				() -> new RuntimeException("UserNotFound")
		);

		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();

		String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();
		String connectedId;

		log.error("json data $data={}", strResultData);
		System.out.println(strResultData);

		if(code.equals("CF-00000") || code.equals("CF-04012")) {
			connectedId = (((JSONObject) jsonParse.parse(strResultData)).get("connectedId")).toString();

			repoConnectedMng.save(ConnectedMng.builder()
					.connectedId(connectedId)
					.idxUser(idx)
					.name(dto.getName())
					.startDate(dto.getStartDate())
					.endDate(dto.getEndDate())
					.desc1(dto.getDesc1())
					.desc2(dto.getDesc2())
					.build()
			);

			if(getScrapingAccount(idx)){
				resAccount = repoResAccount.findConnectedId(idx).stream()
						.map(BankDto.ResAccountDto::from)
						.collect(Collectors.toList());
			}

		}else if(code.equals("CF-04004")){
			connectedId = (((JSONObject) jsonParse.parse(strResultData)).get("connectedId")).toString();

			if(!repoConnectedMng.findByConnectedIdAndIdxUser(connectedId,idx).isPresent()){
				repoConnectedMng.save(ConnectedMng.builder()
						.connectedId(connectedId)
						.idxUser(idx)
						.name(dto.getName())
						.startDate(dto.getStartDate())
						.endDate(dto.getEndDate())
						.desc1(dto.getDesc1())
						.desc2(dto.getDesc2())
						.build()
				);

				if(getScrapingAccount(idx)){
					resAccount = repoResAccount.findConnectedId(idx).stream()
							.map(BankDto.ResAccountDto::from)
							.collect(Collectors.toList());
				}

			}else{
				normal.setStatus(false);
				normal.setKey(code);
				normal.setValue((((JSONObject)jsonParse.parse(strResultCode)).get("message")).toString());
			}
		}else{
			normal.setStatus(false);
			normal.setKey(code);
			normal.setValue((((JSONObject)jsonParse.parse(strResultCode)).get("message")).toString());
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(normal)
				.data(resAccount).build());
	}


	@Transactional(rollbackFor = Exception.class)
	public boolean getScrapingAccount(Long idx) {
		List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(idx);

		connectedMng.forEach(mngItem->{
					String connId = mngItem.connectedId();

					JSONParser jsonParse = new JSONParser();

					for (String s : CommonConstant.LISTBANK) {
						JSONObject[] strResult = new JSONObject[0];
						try {
							strResult = this.getApiResult(KR_BK_1_B_001.krbk1b001(connId, s));
						} catch (ParseException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						String code = strResult[0].get("code").toString();

						if (code.equals("CF-00000") || code.equals("CF-04012")) {

							JSONObject jsonData = strResult[1];
							JSONArray jsonArrayResDepositTrust = (JSONArray) jsonData.get("resDepositTrust");
							JSONArray jsonArrayResForeignCurrency = (JSONArray) jsonData.get("resForeignCurrency");
							JSONArray jsonArrayResFund = (JSONArray) jsonData.get("resFund");
							JSONArray jsonArrayResLoan = (JSONArray) jsonData.get("resLoan");

							jsonArrayResDepositTrust.forEach(item -> {
								JSONObject obj = (JSONObject) item;
								Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
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
											.type("DepositTrust")
											.resAccount(GowidUtils.getEmptyStringToString(obj, "resAccount"))
											.resAccountDisplay(GowidUtils.getEmptyStringToString(obj, "resAccountDisplay"))
											.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
											.resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit"))
											.resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName"))
											.resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency"))
											.resAccountStartDate(startDate)
											.resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate"))
											.resLastTranDate(GowidUtils.getEmptyStringToString(obj,"resLastTranDate"))
											.resAccountName(GowidUtils.getEmptyStringToString(obj,"resAccountName"))
											.build()
									);
								}
							});

							jsonArrayResLoan.forEach(item -> {
								JSONObject obj = (JSONObject) item;
								Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
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
											.type("Loan")
											.resAccount(obj.get("resAccount").toString())
											.resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay").toString())
											.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
											.resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit").toString())
											.resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName").toString())
											.resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency").toString())
											.resAccountStartDate(startDate)
											.resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate").toString())
											.resAccountName(GowidUtils.getEmptyStringToString(obj,"resAccountName").toString())
											.resAccountLoanExecNo(GowidUtils.getEmptyStringToString(obj,"resAccountLoanExecNo").toString())
											.build()
									);
								}
							});

							jsonArrayResForeignCurrency.forEach(item -> {
								JSONObject obj = (JSONObject) item;
								Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
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
											.type("ResForeignCurrency")
											.resAccount(obj.get("resAccount").toString())
											.resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay").toString())
											.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
											.resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit").toString())
											.resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName").toString())
											.resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency").toString())
											.resAccountStartDate(startDate)
											.resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate").toString())
											.resLastTranDate(GowidUtils.getEmptyStringToString(obj,"resLastTranDate").toString())
											.resAccountName(GowidUtils.getEmptyStringToString(obj,"resAccountName").toString())
											.build()
									);
								}
							});

							jsonArrayResFund.forEach(item -> {
								JSONObject obj = (JSONObject) item;
								Optional<ResAccount> idxLongTemp = repoResAccount.findByResAccount(obj.get("resAccount").toString());
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
											.type("ResFund")
											.resAccount(obj.get("resAccount").toString())
											.resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay").toString())
											.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
											.resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit").toString())
											.resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName").toString())
											.resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency").toString())
											.resAccountStartDate(startDate)
											.resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate").toString())
											.resAccountInvestedCost(GowidUtils.getEmptyStringToString(obj,"resAccountInvestedCost").toString())
											.resEarningsRate(GowidUtils.getEmptyStringToString(obj,"resEarningsRate").toString())
											.build()
									);
								}
							});
						}
					}
				}

		);

		// scraping start


		return true;
	}

	private JSONObject[] getApiResult(String str) throws ParseException {
		JSONObject[] result = new JSONObject[2];

		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParse.parse(str);

		result[0] = (JSONObject) jsonObject.get("result");
		result[1] = (JSONObject) jsonObject.get("data");

		return result;
	}


	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity registerAccount2(ConnectedMngDto.Account2 dto, Long idx) {
		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.CREATE_ACCOUNT;
		List<ResAccount> resAccount = null;

		for( String s : CommonConstant.LISTBANK){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));

			accountMap1.put("keyFile",      Account.getBase64FromCertFile(dto.getKeyPath()));
			accountMap1.put("derFile",      Account.getBase64FromCertFile(dto.getDerPath()));

			list.add(accountMap1);
		}

		bodyMap.put("accountList", list);
		String strObject = ApiRequest.request(createUrlPath, bodyMap);

		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParse.parse(strObject);

		String strResultCode = jsonObject.get("result").toString();
		String strResultData = jsonObject.get("data").toString();

		// insert user table - connectedId save
		repoUser.findById(idx).orElseThrow(
				() -> new RuntimeException("UserNotFound")
		);

		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();
		String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();

		log.debug("json data $data={}", strResultData);

		if(code.equals("CF-00000") || code.equals("CF-04012")) {
			String connectedId = (((JSONObject) jsonParse.parse(strResultData)).get("connectedId")).toString();

			repoConnectedMng.save(ConnectedMng.builder()
					.connectedId(connectedId)
					.idxUser(idx)
					.name(dto.getName())
					.startDate(dto.getStartDate())
					.endDate(dto.getEndDate())
					.desc1(dto.getDesc1())
					.desc2(dto.getDesc2())
					.build()
			);

			if(getScrapingAccount(idx)){
				resAccount = repoResAccount.findConnectedId(idx).stream().collect(Collectors.toList());
			}

		}else if(code.equals("CF-04004")){
			String connectedId = (((JSONObject) jsonParse.parse(strResultData)).get("connectedId")).toString();

			if(!repoConnectedMng.findByConnectedIdAndIdxUser(connectedId,idx).isPresent()){
				repoConnectedMng.save(ConnectedMng.builder()
						.connectedId(connectedId)
						.idxUser(idx)
						.name(dto.getName())
						.startDate(dto.getStartDate())
						.endDate(dto.getEndDate())
						.desc1(dto.getDesc1())
						.desc2(dto.getDesc2())
						.build()
				);

				if(getScrapingAccount(idx)){
					resAccount = repoResAccount.findConnectedId(idx).stream().collect(Collectors.toList());
				}

			}else{
				normal.setStatus(false);
				normal.setKey(code);
				normal.setValue((((JSONObject)jsonParse.parse(strResultCode)).get("message")).toString());
			}
		}else{
			normal.setStatus(false);
			normal.setKey(code);
			normal.setValue((((JSONObject)jsonParse.parse(strResultCode)).get("message")).toString());
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(normal)
				.data(resAccount).build());
	}

	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity updateAccount(ConnectedMngDto.Account dto, Long idx) {
		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.UPDATE_ACCOUNT;
		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();

		List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(idx);

		for( ConnectedMng mng : connectedMng){
			for( String s : CommonConstant.LISTBANK){
				accountMap1 = new HashMap<>();
				accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
				accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
				accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
				accountMap1.put("organization",	s);// 기관코드
				accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)

				accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
				accountMap1.put("certType",     CommonConstant.CERTTYPE);
				accountMap1.put("certFile",     dto.getCertFile());
				list.add(accountMap1);
			}

			bodyMap.put("accountList", list);
			bodyMap.put(CommonConstant.CONNECTED_ID, mng.connectedId());
			String strObject = ApiRequest.request(createUrlPath, bodyMap);
			JSONParser jsonParse = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonParse.parse(strObject);
			String strResultCode = jsonObject.get("result").toString();
			String strResultData = jsonObject.get("data").toString();

			String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();

			if(code.equals("CF-00000") || code.equals("CF-04012")){
				String connectedId = (((JSONObject)jsonParse.parse(strResultData)).get("connectedId")).toString();

				repoConnectedMng.save(ConnectedMng.builder()
						.connectedId(connectedId)
						.idxUser(idx)
						.name(dto.getName())
						.startDate(dto.getStartDate())
						.endDate(dto.getEndDate())
						.desc1(dto.getDesc1())
						.desc2(dto.getDesc2())
						.build()
				);
			}else{
				normal.setStatus(false);
				normal.setKey(code);
				normal.setValue((((JSONObject)jsonParse.parse(strResultCode)).get("message")).toString());
			}
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().normal(normal).build());
	}

	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity deleteAccount(ConnectedMngDto.DeleteAccount dto, Long idx){

		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.DELETE_ACCOUNT;
		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();

		ConnectedMng connectedMng = repoConnectedMng.findById(dto.getIdxConnectedId()).orElseThrow(
				() -> new RuntimeException("EMPTY DATA")
		);


		for( String s : CommonConstant.LISTBANK){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			list.add(accountMap1);
		}

		bodyMap.put("accountList", list);
		bodyMap.put(CommonConstant.CONNECTED_ID, connectedMng.connectedId());
		String strObject = ApiRequest.request(createUrlPath, bodyMap);
		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParse.parse(strObject);
		String strResultCode = jsonObject.get("result").toString();
		String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();

		if(code.equals("CF-00000")){
			repoConnectedMng.deleteConnectedQuery(connectedMng.connectedId());
		}else{
			normal.setStatus(false);
			normal.setKey(code);
			normal.setValue((((JSONObject)jsonParse.parse(strResultCode)).get("message")).toString());
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().normal(normal).build());
	}

	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity deleteAccount2(String connectedId, Long idx){

		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.DELETE_ACCOUNT;
		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();


		for( String s : CommonConstant.LISTBANK){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			list.add(accountMap1);
		}

		accountMap1 = new HashMap<>();
		accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
		accountMap1.put("businessType",	CommonConstant.REVENUETYPE);  // 업무구분코드
		accountMap1.put("clientType",  	"A");   // "고객구분(P: 개인, B: 기업)
		accountMap1.put("organization",	"0002");// 기관코드
		accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
		list.add(accountMap1);


		bodyMap.put("accountList", list);
		bodyMap.put(CommonConstant.CONNECTED_ID, connectedId );
		String strObject = ApiRequest.request(createUrlPath, bodyMap);
		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParse.parse(strObject);
		String strResultCode = jsonObject.get("result").toString();
		String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();

		return ResponseEntity.ok().body(BusinessResponse.builder().normal(normal).build());
	}


	/**
	 * 계정 목록조회
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void list(String connectedId) throws IOException, InterruptedException, ParseException {
		// 요청 URL 설정
		String urlPath = CommonConstant.getRequestDomain() + CommonConstant.GET_ACCOUNTS;

		// 요청 파라미터 설정 시작
		HashMap<String, Object> bodyMap = new HashMap<String, Object>();

		// String connectedId = "45t4DJOD44M9uwH7zxSgBg";	// 엔드유저의 은행/카드사 계정 등록 후 발급받은 커넥티드아이디 예시
		bodyMap.put(CommonConstant.CONNECTED_ID, connectedId);
		// 요청 파라미터 설정 종료

		// API 요청
		String result = ApiRequest.request(urlPath, bodyMap);

		// 응답결과 확인
		System.out.println(result);
	}

	/**
	 * connectedId 목록조회
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public String connectedIdList() throws IOException, InterruptedException, ParseException {
		// 요청 URL 설정
		String urlPath = CommonConstant.getRequestDomain() + CommonConstant.GET_CONNECTED_IDS;

		// 요청 파라미터 설정 시작
		HashMap<String, Object> bodyMap = new HashMap<>();
		bodyMap.put(CommonConstant.PAGE_NO, 0);        // 페이지 번호(생략 가능) 생략시 1페이지 값(0) 자동 설정
		// 요청 파라미터 설정 종료

		// API 요청

		// 응답결과 확인
		return ApiRequest.request(urlPath, bodyMap);
	}

	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity RegisterAccountNt(ConnectedMngDto.AccountNt dto, Long idxUser){

		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();
		// 국세청 - ConId Start
		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.CREATE_ACCOUNT;

		for( String s : CommonConstant.LISTBANK){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
			accountMap1.put("certType",     CommonConstant.CERTTYPE);
			accountMap1.put("certFile",     dto.getCertFile());
			list.add(accountMap1);
		}

		for( String s : CommonConstant.LISTCARD){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.CARDTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
			accountMap1.put("certType",     CommonConstant.CERTTYPE);
			accountMap1.put("certFile",     dto.getCertFile());
			list.add(accountMap1);
		}

		accountMap1 = new HashMap<>();
		accountMap1.put("countryCode",	"KR");  	// 국가코드
		accountMap1.put("businessType",	"NT");  	// 공공 국세청 업무구분
		accountMap1.put("clientType",  	CommonConstant.CLIENTTYPE);   	// 통합 고객구분 A
		accountMap1.put("organization",	"0002");	// 국세청 기관코드
		accountMap1.put("loginType",  	"0");   	// 로그인타입 (0: 인증서, 1: ID/PW)
		accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
		accountMap1.put("certType",     CommonConstant.CERTTYPE);
		accountMap1.put("certFile",     dto.getCertFile());
		list.add(accountMap1);

		bodyMap.put("accountList", list);
		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParse.parse(ApiRequest.request(createUrlPath, bodyMap));

		String code = ((JSONObject)(jsonObject.get("result"))).get("code").toString();
		String connectedId = null;

		if(code.equals("CF-00000") || code.equals("CF-04012")) {
			connectedId = ((JSONObject)(jsonObject.get("data"))).get("connectedId").toString();

			repoConnectedMng.save(ConnectedMng.builder()
					.connectedId(connectedId)
					.idxUser(idxUser)
					.name(dto.getName())
					.startDate(dto.getStartDate())
					.endDate(dto.getEndDate())
					.desc1(dto.getDesc1())
					.desc2(dto.getDesc2())
					.type("NT")
					.build()
			);
		}else if(code.equals("CF-04000")){
			JSONObject JSONObjectData = ((JSONObject)(jsonObject.get("data")));
			JSONArray JSONObjectErrorData = (JSONArray) JSONObjectData.get("errorList");
			connectedId = GowidUtils.getEmptyStringToString((JSONObject) JSONObjectErrorData.get(0), "extraMessage");
			log.debug( "cf-04000 connectedId = {} ", connectedId );
		}else{
			throw new RuntimeException(code);
		}

		// 국세청 - 증명발급 사업자등록
		JSONObject[] jsonObjectProofIssue = getApiResult(PROOF_ISSUE.proof_issue(
				"0001",
				connectedId,
				"04",
				"01",
				"1",
				"0",
				"",
				"" // 사업자번호
		));

		String jsonObjectProofIssueCode = jsonObjectProofIssue[0].get("code").toString();
		if (jsonObjectProofIssueCode.equals("CF-00000") ) {
			JSONObject jsonData = jsonObjectProofIssue[1];

			// JSONObject jsonData = (JSONObject) jsonDataYn.get("resRegisterEntriesList");

			// todo 이미 가입된 회사의 경우 처리 필요
			//	중복체크 테스트 후엔 적용
			Corp corp = null;

			if (repoCorp.findByResCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo")).isPresent()) {
				corp = repoCorp.findByResCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo")).get();
			} else {
				//	사용자 조회
				Optional<User> user = repoUser.findById(idxUser);
				corp = repoCorp.save(
						Corp.builder()
								.resJointRepresentativeNm(GowidUtils.getEmptyStringToString(jsonData, "resJointRepresentativeNm"))
								.resIssueOgzNm(GowidUtils.getEmptyStringToString(jsonData, "resIssueOgzNm"))
								.resCompanyNm(GowidUtils.getEmptyStringToString(jsonData, "resCompanyNm"))
								.resBusinessTypes(GowidUtils.getEmptyStringToString(jsonData, "resBusinessTypes"))
								.resBusinessItems(GowidUtils.getEmptyStringToString(jsonData, "resBusinessItems"))
								.resBusinessmanType(GowidUtils.getEmptyStringToString(jsonData, "resBusinessmanType"))
								.resCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo"))
								.resIssueNo(GowidUtils.getEmptyStringToString(jsonData, "resIssueNo"))
								.resJointIdentityNo(GowidUtils.getEmptyStringToString(jsonData, "resJointIdentityNo"))
								.resOpenDate(GowidUtils.getEmptyStringToString(jsonData, "resOpenDate"))
								.resOriGinalData(GowidUtils.getEmptyStringToString(jsonData, "resOriGinalData"))
								.resRegisterDate(GowidUtils.getEmptyStringToString(jsonData, "resRegisterDate"))
								.resUserAddr(GowidUtils.getEmptyStringToString(jsonData, "resUserAddr"))
								.resUserIdentiyNo(GowidUtils.getEmptyStringToString(jsonData, "resUserIdentiyNo"))
								.resUserNm(GowidUtils.getEmptyStringToString(jsonData, "resUserNm"))
								.status(CorpStatus.PENDING)
								.user(user.get())
								.build()
				);
			}

			log.debug("corp.idx() = {} ", corp.idx());

			// 국세청 - 법인등기부등본
			JSONObject[] jsonObjectCorpRegister = getApiResult(CORP_REGISTER.corp_register(
					"0002",
					"01050619746",
					RSAUtil.encryptRSA("1234", CommonConstant.PUBLIC_KEY),
					"2",
					GowidUtils.getEmptyStringToString(jsonData, "resUserIdentiyNo").replaceAll("-","").trim(),
					"1",
					"T34029396293",
					"gowid99!",
					"",
					"",
					"",
					"",
					"0",
					"",
					"",
					"",
					"N"
			));

			String jsonObjectCorpRegisterCode = jsonObjectCorpRegister[0].get("code").toString();
			if (jsonObjectCorpRegisterCode.equals("CF-00000")) {
				JSONObject jsonDataCorpRegister = jsonObjectCorpRegister[1];

				if (!jsonDataCorpRegister.get("resIssueYN").toString().equals("1")) {
					throw new RuntimeException("발행실패");
				}

				JSONArray jsonDataArrayList = (JSONArray) jsonDataCorpRegister.get("resRegisterEntriesList");
				JSONObject jsonData2 = (JSONObject) jsonDataArrayList.get(0);

				JSONArray jsonArrayResCompanyNmList = (JSONArray) jsonData2.get("resCompanyNmList");
				JSONArray jsonArrayResUserAddrList = (JSONArray) jsonData2.get("resUserAddrList");
				JSONArray jsonArrayResNoticeMethodList = (JSONArray) jsonData2.get("resNoticeMethodList");
				JSONArray jsonArrayResOneStocAmtList = (JSONArray) jsonData2.get("resOneStocAmtList");
				JSONArray jsonArrayResTCntStockIssueList = (JSONArray) jsonData2.get("resTCntStockIssueList");
				JSONArray jsonArrayResStockList = (JSONArray) jsonData2.get("resStockList");
				JSONArray jsonArrayResPurposeList = (JSONArray) jsonData2.get("resPurposeList");
				JSONArray jsonArrayResRegistrationHisList = (JSONArray) jsonData2.get("resRegistrationHisList");
				JSONArray jsonArrayResBranchList = (JSONArray) jsonData2.get("resBranchList");
				JSONArray jsonArrayResIncompetenceReasonList = (JSONArray) jsonData2.get("resIncompetenceReasonList");
				JSONArray jsonArrayResJointPartnerList = (JSONArray) jsonData2.get("resJointPartnerList");
				JSONArray jsonArrayResManagerList = (JSONArray) jsonData2.get("resManagerList");
				JSONArray jsonArrayResConvertibleBondList = (JSONArray) jsonData2.get("resConvertibleBondList");
				JSONArray jsonArrayResWarrantBondList = (JSONArray) jsonData2.get("resWarrantBondList");
				JSONArray jsonArrayResParticipatingBondList = (JSONArray) jsonData2.get("resParticipatingBondList");
				JSONArray jsonArrayResStockOptionList = (JSONArray) jsonData2.get("resStockOptionList");
				JSONArray jsonArrayResTypeStockContentList = (JSONArray) jsonData2.get("resTypeStockContentList");
				JSONArray jsonArrayResCCCapitalStockList = (JSONArray) jsonData2.get("resCCCapitalStockList");
				JSONArray jsonArrayResEtcList = (JSONArray) jsonData2.get("resEtcList");
				JSONArray jsonArrayResCorpEstablishDateList = (JSONArray) jsonData2.get("resCorpEstablishDateList");
				JSONArray jsonArrayResRegistrationRecReasonList = (JSONArray) jsonData2.get("resRegistrationRecReasonList");
				JSONArray jsonArrayResCEOList = (JSONArray) jsonData2.get("resCEOList");


				ResRegisterEntriesList parent = repoResRegisterEntriesList.save(
						ResRegisterEntriesList.builder()
								.idxCorp(corp.idx())
								.resDocTitle(GowidUtils.getEmptyStringToString(jsonData2, "resAccountDisplay"))
								.resRegistrationNumber(GowidUtils.getEmptyStringToString(jsonData2, "resRegistrationNumber"))
								.resRegNumber(GowidUtils.getEmptyStringToString(jsonData2, "resRegNumber"))
								.commCompetentRegistryOffice(GowidUtils.getEmptyStringToString(jsonData2, "commCompetentRegistryOffice"))
								.resPublishRegistryOffice(GowidUtils.getEmptyStringToString(jsonData2, "resPublishRegistryOffice"))
								.resPublishDate(GowidUtils.getEmptyStringToString(jsonData2, "resPublishDate"))
								.resIssueNo(GowidUtils.getEmptyStringToString(jsonData2, "resIssueNo"))
								.build()
				);

				saveJSONArray1(jsonArrayResCompanyNmList, parent.idx());
				saveJSONArray2(jsonArrayResUserAddrList, parent.idx());
				saveJSONArray3(jsonArrayResNoticeMethodList, parent.idx());
				saveJSONArray4(jsonArrayResOneStocAmtList, parent.idx());
				saveJSONArray5(jsonArrayResTCntStockIssueList, parent.idx());
				saveJSONArray6(jsonArrayResStockList, parent.idx());
				saveJSONArray7(jsonArrayResPurposeList, parent.idx());
				saveJSONArray8(jsonArrayResRegistrationHisList, parent.idx());
				saveJSONArray9(jsonArrayResBranchList, parent.idx());
				saveJSONArray10(jsonArrayResIncompetenceReasonList, parent.idx());
				saveJSONArray11(jsonArrayResJointPartnerList, parent.idx());
				saveJSONArray12(jsonArrayResManagerList, parent.idx());
				saveJSONArray13(jsonArrayResConvertibleBondList, parent.idx());
				saveJSONArray14(jsonArrayResWarrantBondList, parent.idx());
				saveJSONArray15(jsonArrayResParticipatingBondList, parent.idx());
				saveJSONArray16(jsonArrayResStockOptionList, parent.idx());
				saveJSONArray17(jsonArrayResTypeStockContentList, parent.idx());
				saveJSONArray18(jsonArrayResCCCapitalStockList, parent.idx());
				saveJSONArray19(jsonArrayResEtcList, parent.idx());
				saveJSONArray20(jsonArrayResCorpEstablishDateList, parent.idx());
				saveJSONArray21(jsonArrayResRegistrationRecReasonList, parent.idx());
				String d009 = saveJSONArray22(jsonArrayResCEOList, parent.idx());


				repoD1000.save(D1000.builder()
						.idxCorp(corp.idx())
						.c007("")
						.d001(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo").replaceAll("-",""))
						.d002(GowidUtils.getEmptyStringToString(jsonData, "resUserIdentiyNo").replaceAll("-",""))
						.d003(GowidUtils.getEmptyStringToString(jsonData, "resCompanyNm"))
						.d004("400")
						.d005("06")
						.d007(GowidUtils.getEmptyStringToString(jsonData, "resRegisterDate"))
						.d009("1") // 1: 단일대표 2: 개별대표 3: 공동대표
						.d029(null)
						.d030(null)
						.d031(null)
						.d032("대표이사")
						.d033("대표이사")
						.d044(null)
						.d045("5")
						.d046("Y")
						.d047("Y")
						.d048("09")
						.d049("DAAC6F")
						.d051("10")
						.d052("N")
						.d053("고위드제휴카드신규입회")
						.d054("1")
						.d056("N")
						.d057("N")
						.d058(null)
						.d063(null)
						.d067(null)
						.d068(null)
						.d069(null)
						.d070(null)
						.build());

			}else{
				normal.setStatus(false);
				normal.setKey(jsonObjectCorpRegisterCode);
				normal.setValue(jsonObjectCorpRegister[0].get("message").toString());
			}
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(normal)
				.data(null).build());
	}

	/**
	 * 법인정보 등록
	 *
	 * @param idx_user 등록하는 User idx
	 * @param dto      등록정보
	 * @param idx_CardInfo CardIssuanceInfo idx
	 */
	@Transactional(rollbackFor = Exception.class)
	UserCorporationDto.CorporationRes codeRegisterCorporation(Long idx_user, UserCorporationDto.RegisterCorporation dto, Long idx_CardInfo) {
		User user = findUser(idx_user);

		D1000 d1000 = repoD1000.getTopByIdxCorpOrderByIdxDesc(user.corp().idx());
		Corp corp = repoCorp.save(user.corp()
				.resCompanyEngNm(dto.getEngCorName())
				.resCompanyNumber(dto.getCorNumber())
				.resBusinessCode(dto.getBusinessCode())
				.resUserType(d1000 != null ? d1000.d009() : null)
		);

		CardIssuanceInfo cardInfo;
		try {
			cardInfo = findCardIssuanceInfo(user.corp());
			if (!cardInfo.idx().equals(idx_CardInfo)) {
				throw MismatchedException.builder().build();
			}

		} catch (EntityNotFoundException e) {
			cardInfo = repoCardIssuance.save(CardIssuanceInfo.builder().corp(corp).build());
			if (d1000 != null) {
				String[] corNumber = dto.getCorNumber().split("-");
				repoD1000.save(d1000
						.d006(!StringUtils.hasText(d1000.d006()) ? dto.getEngCorName() : d1000.d006())
						.d008(!StringUtils.hasText(d1000.d008()) ? dto.getBusinessCode() : d1000.d008())
						.d026(!StringUtils.hasText(d1000.d026()) ? corNumber[0] : d1000.d026())
						.d027(!StringUtils.hasText(d1000.d027()) ? corNumber[1] : d1000.d027())
						.d028(!StringUtils.hasText(d1000.d028()) ? corNumber[2] : d1000.d028())
				);
			}
		}
		return UserCorporationDto.CorporationRes.from(corp, cardInfo.idx());
	}

	private CardIssuanceInfo findCardIssuanceInfo(Corp corp) {
		return repoCardIssuance.findTopByCorpAndDisabledFalseOrderByIdxDesc(corp).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CardIssuanceInfo")
						.build()
		);
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity RegisterCorpInfo(ConnectedMngDto.CorpInfo dto, Long idxUser,Long idx_CardInfo){

		Optional<User> user = repoUser.findById(idxUser);

		String resCompanyIdentityNo = user.get().corp().resCompanyIdentityNo();

		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();
		String connectedId = null;

		List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(idxUser);
		if(connectedMng.size() < 1) throw new RuntimeException("CONNECTED ID");
		else connectedId = connectedMng.get(0).connectedId();

		List<String> listYyyyMm = getFindClosingStandards(dto.getResClosingStandards().trim());

		// 국세청 - 증명발급 표준재무재표
		String finalConnectedId = connectedId;

		listYyyyMm.forEach(yyyyMm ->{
			JSONObject[] jsonObjectStandardFinancial = new JSONObject[0];
			try {
				jsonObjectStandardFinancial = getApiResult(STANDARD_FINANCIAL.standard_financial(
						"0001",
						finalConnectedId,
						yyyyMm,
						"0",
						"04",
						"01",
						"40",
						"",
						resCompanyIdentityNo.replaceAll("-","").trim() // 사업자번호
				));
			} catch (Exception e) {
				log.debug(e.toString());
			}

			String jsonObjectStandardFinancialCode = jsonObjectStandardFinancial[0].get("code").toString();
			if (jsonObjectStandardFinancialCode.equals("CF-00000") ) {
				JSONObject jsonData2 = jsonObjectStandardFinancial[1];

				JSONArray resBalanceSheet = (JSONArray) jsonData2.get("resBalanceSheet");
				JSONArray resIncomeStatement = (JSONArray) jsonData2.get("resIncomeStatement");
				JSONArray resCostSpecificationList = (JSONArray) jsonData2.get("resCostSpecificationList");
				JSONArray resFinancialStatementList = (JSONArray) jsonData2.get("resFinancialStatementList");

				ResStandardFinancialList parentStandardFinancial = repoResStandardFinancialList.save(
						ResStandardFinancialList.builder()
								.idxCorp(user.get().corp().idx())
								.commStartDate(GowidUtils.getEmptyStringToString(jsonData2, "commStartDate"))
								.commEndDate(GowidUtils.getEmptyStringToString(jsonData2, "commEndDate"))
								.resUserNm(GowidUtils.getEmptyStringToString(jsonData2, "resUserNm"))
								.resIssueNo(GowidUtils.getEmptyStringToString(jsonData2, "resIssueNo"))
								.resUserAddr(GowidUtils.getEmptyStringToString(jsonData2, "resUserAddr"))
								.resUserIdentiyNo(GowidUtils.getEmptyStringToString(jsonData2, "resUserIdentiyNo"))
								.resCompanyNm(GowidUtils.getEmptyStringToString(jsonData2, "resCompanyNm"))
								.resCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData2, "resCompanyIdentityNo"))
								.resBusinessItems(GowidUtils.getEmptyStringToString(jsonData2, "resBusinessItems"))
								.resBusinessTypes(GowidUtils.getEmptyStringToString(jsonData2, "resBusinessTypes"))
								.resAttachDocument(GowidUtils.getEmptyStringToString(jsonData2, "resAttachDocument"))
								.resReportingDate(GowidUtils.getEmptyStringToString(jsonData2, "resReportingDate"))
								.resAttrYear(GowidUtils.getEmptyStringToString(jsonData2, "resAttrYear"))
								.build()
				);

				saveJSONArrayResBalanceSheet(resBalanceSheet, parentStandardFinancial.idx());
				saveJSONArrayResIncomeStatement(resIncomeStatement, parentStandardFinancial.idx());
				saveJSONArrayResCostSpecificationList(resCostSpecificationList, parentStandardFinancial.idx());
				saveJSONArrayResFinancialStatementList(resFinancialStatementList, parentStandardFinancial.idx());

				repoD1100.save(D1100.builder()
						.idxCorp(user.get().corp().idx())
						.c007("")
						.d001(user.get().corp().resCompanyIdentityNo().replaceAll("-",""))
						.d002("01")
						.d003("3")
						.d004(null)
						.d005("DAAC6F")
						.d006("G1")
						.d007("1")
						.d008("00")
						.d009("A")
						.d010("3")
						.d011("0")
						.d012("0")
						.d013("0")
						.d014("1")
						.d015("N")
						.d016("고위드 스타트업 T&E")
						.d017("10")
						.d018("01")
						.d019("Y")
						.d020("")
						.d021("")
						.d022("2")
						.d023("15")
						.d024("")// 사업장 번호
						.d025("")
						.d026("")
						.d027(user.get().corp().resCompanyIdentityNo().replaceAll("-",""))
						.d028("901")
						.d029("")
						.d030("1")
						.d031("")
						.d032("")
						.d033("")
						.d034("")
						.d035("")
						.d036("")
						.d037("")
						.d038("N")
						.d039("")
						.d040(null)
						.d041(null)
						.d042("Y")
						.d043("Y")
						.d044("")
						.d045("")
						.d046("")
						.d047("")
						.d048("Y")
						.d049(null)
						.build());
			}else{
				log.debug("jsonObjectStandardFinancialCode = {} ", jsonObjectStandardFinancialCode);
				log.debug("jsonObjectStandardFinancial message = {} ", jsonObjectStandardFinancial[0].get("message").toString());
			}
		});

		repoCorp.save(user.get().corp()
				.resCompanyEngNm(dto.getResCompanyEngNm())
				.resCompanyNumber(dto.getResCompanyPhoneNumber())
				.resBusinessCode(dto.getResBusinessCode())
				.resUserType("1") // 공동대표
		);

		CardIssuanceInfo cardInfo;
		try {
			cardInfo = findCardIssuanceInfo(user.get().corp());
			if (!cardInfo.idx().equals(idx_CardInfo)) {
				throw MismatchedException.builder().build();
			}
		} catch (EntityNotFoundException e) {
			cardInfo = repoCardIssuance.save(CardIssuanceInfo.builder().corp(user.get().corp()).build());
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(normal)
				.data(UserCorporationDto.CorporationRes.from(user.get().corp(), cardInfo.idx())).build());
	}

	private List<String> getFindClosingStandards(String Mm) {

		List<String> returnYyyyMm = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy");
		Date date = new Date();
		cal.setTime(date);
		cal.add(Calendar.YEAR, -1);
		returnYyyyMm.add(df.format(cal.getTime()) + Mm);
		cal.add(Calendar.YEAR, -1);
		returnYyyyMm.add(df.format(cal.getTime()) + Mm);

		return returnYyyyMm;
	}

	private void saveJSONArray1(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			ResCompanyNmList parent = repoResCompanyNmList.save(ResCompanyNmList.builder()
					.idxParent(idx)
					.resCompanyNm(GowidUtils.getEmptyStringToString(obj, "resCompanyNm"))
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.build()
			);

			saveResChangeDateList(jsonArrayResChangeDateList, parent.idx() );
			saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx());
		});
	}

	private void saveJSONArray2(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			ResUserAddrList parent = repoResUserAddrList.save(ResUserAddrList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resUserAddr(GowidUtils.getEmptyStringToString(obj, "resUserAddr"))
					.build()
			);

			saveResChangeDateList(jsonArrayResChangeDateList, parent.idx() );
			saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx());
		});
	}

	private void saveJSONArray3(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			ResNoticeMethodList parent = repoResNoticeMethodList.save(ResNoticeMethodList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resNoticeMethod(GowidUtils.getEmptyStringToString(obj, "resNoticeMethod"))
					.build()
			);

			saveResChangeDateList(jsonArrayResChangeDateList, parent.idx() );
			saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx());
		});
	}

	private void saveJSONArray4(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			ResOneStocAmtList parent = repoResOneStocAmtList.save(ResOneStocAmtList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resOneStockAmt(GowidUtils.getEmptyStringToString(obj, "resOneStockAmt"))
					.build()
			);

			saveResChangeDateList(jsonArrayResChangeDateList, parent.idx() );
			saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx());
		});
	}

	private void saveJSONArray5(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			ResTCntStockIssueList parent = repoResTCntStockIssueList.save(ResTCntStockIssueList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resTCntStockIssue(GowidUtils.getEmptyStringToString(obj, "resTCntStockIssue"))
					.build()
			);

			saveResChangeDateList(jsonArrayResChangeDateList, parent.idx() );
			saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx());
		});
	}

	private void saveJSONArray6(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResStockItemList = (JSONArray) obj.get("resStockItemList");
			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");


			ResStockList parent = repoResStockList.save(ResStockList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.build()
			);

			saveResStockItemList(jsonArrayResStockItemList, parent.idx() );
			saveResChangeDateList(jsonArrayResChangeDateList, parent.idx() );
			saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx());

		});
	}

	private void saveJSONArray7(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResPurposeList parent = repoResPurposeList.save(ResPurposeList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resPurpose(GowidUtils.getEmptyStringToString(obj, "resPurpose"))
					.build()
			);
		});
	}

	private void saveJSONArray8(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResRegistrationHisList parent = repoResRegistrationHisList.save(ResRegistrationHisList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resRegistrationHis(GowidUtils.getEmptyStringToString(obj, "resRegistrationHis"))
					.build()
			);
		});
	}

	private void saveJSONArray9(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResBranchList parent = repoResBranchList.save(ResBranchList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resBranch(GowidUtils.getEmptyStringToString(obj, "resBranch"))
					.build()
			);
		});
	}

	private void saveJSONArray10(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResIncompetenceReasonList parent = repoResIncompetenceReasonList.save(ResIncompetenceReasonList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resIncompetenceReason(GowidUtils.getEmptyStringToString(obj, "resIncompetenceReason"))
					.build()
			);
		});
	}

	private void saveJSONArray11(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResJointPartnerList parent = repoResJointPartnerList.save(ResJointPartnerList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resJointPartner(GowidUtils.getEmptyStringToString(obj, "resJointPartner"))
					.build()
			);
		});
	}

	private void saveJSONArray12(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResManagerList parent = repoResManagerList.save(ResManagerList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resManager(GowidUtils.getEmptyStringToString(obj, "resManager"))
					.build()
			);
		});
	}

	private void saveJSONArray13(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			JSONArray jsonArrayItem = (JSONArray) obj.get("resConvertibleBondItemList");

			ResConvertibleBondList parent = repoResConvertibleBondList.save(ResConvertibleBondList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.build()
			);

			jsonArrayItem.forEach( item2 -> {
				repoResConvertibleBondItemList.save(ResConvertibleBondItemList.builder()
						.idxParent(parent.idx())
						.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
						.resConvertibleBond(GowidUtils.getEmptyStringToString(obj, "resConvertibleBond"))
						.build()
				);
			});
		});
	}

	private void saveJSONArray14(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			JSONArray jsonArrayItem = (JSONArray) obj.get("resWarrantBondItemList");

			ResWarrantBondList parent = repoResWarrantBondList.save(ResWarrantBondList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.build()
			);

			jsonArrayItem.forEach( item2 -> {
				repoResWarrantBondItemList.save(ResWarrantBondItemList.builder()
						.idxParent(parent.idx())
						.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
						.resWarrantBond(GowidUtils.getEmptyStringToString(obj, "resWarrantBond"))
						.build()
				);
			});
		});
	}

	private void saveJSONArray15(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			JSONArray jsonArrayItem = (JSONArray) obj.get("resParticipatingBondItemList");

			ResParticipatingBondList parent = repoResParticipatingBondList.save(ResParticipatingBondList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.build()
			);

			jsonArrayItem.forEach( item2 -> {
				repoResParticipatingBondItemList.save(ResParticipatingBondItemList.builder()
						.idxParent(parent.idx())
						.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
						.resParticipatingBond(GowidUtils.getEmptyStringToString(obj, "resParticipatingBond"))
						.build()
				);
			});
		});
	}

	private void saveJSONArray16(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResStockOptionList parent = repoResStockOptionList.save(ResStockOptionList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resStockOption(GowidUtils.getEmptyStringToString(obj, "resStockOption"))
					.build()
			);
		});
	}

	private void saveJSONArray17(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			JSONArray jsonArrayItem = (JSONArray) obj.get("resTypeStockContentItemList");

			ResTypeStockContentList parent = repoResTypeStockContentList.save(ResTypeStockContentList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.build()
			);

			jsonArrayItem.forEach( item2 -> {
				repoResTypeStockContentItemList.save(ResTypeStockContentItemList.builder()
						.idxParent(parent.idx())
						.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
						.resTypeStockContent(GowidUtils.getEmptyStringToString(obj, "resParticipatingBond"))
						.build()
				);
			});
		});
	}

	private void saveJSONArray18(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			JSONArray jsonArrayItem = (JSONArray) obj.get("resCCCapitalStockItemList");

			ResCCCapitalStockList parent = repoResCCCapitalStockList.save(ResCCCapitalStockList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.build()
			);

			jsonArrayItem.forEach( item2 -> {
				repoResCCCapitalStockItemList.save(ResCCCapitalStockItemList.builder()
						.idxParent(parent.idx())
						.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
						.resCCCapitalStock(GowidUtils.getEmptyStringToString(obj, "resCCCapitalStock"))
						.build()
				);
			});
		});
	}

	private void saveJSONArray19(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			repoResEtcList.save(ResEtcList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resEtc(GowidUtils.getEmptyStringToString(obj, "resEtc"))
					.build()
			);
		});
	}

	private void saveJSONArray20(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			repoResCorpEstablishDateList.save(ResCorpEstablishDateList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resCorpEstablishDate(GowidUtils.getEmptyStringToString(obj, "resCorpEstablishDate"))
					.build()
			);
		});
	}

	private void saveJSONArray21(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResRegistrationRecReasonList parent = repoResRegistrationRecReasonList.save(ResRegistrationRecReasonList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resRegistrationRecReason(GowidUtils.getEmptyStringToString(obj, "resRegistrationRecReason"))
					.resRegistrationRecDate(GowidUtils.getEmptyStringToString(obj, "resRegistrationRecDate"))
					.build()
			);
		});
	}

	private String saveJSONArray22(JSONArray jsonArray, Long idx ) {
		String returnStr = null;
		returnStr = "사내이사";

		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			log.debug(GowidUtils.getEmptyStringToString(obj, "resPosition"));

			repoResCEOList.save(ResCEOList.builder()
					.idxParent(idx)
					.resPosition(GowidUtils.getEmptyStringToString(obj, "resPosition"))
					.resUserNm(GowidUtils.getEmptyStringToString(obj, "resUserNm"))
					.resUserIdentiyNo(GowidUtils.getEmptyStringToString(obj, "resUserIdentiyNo"))
					.resUserAddr(GowidUtils.getEmptyStringToString(obj, "resUserAddr"))
					.build()
			);
		});
		return returnStr;
	}

	private void saveResRegistrationDateList(JSONArray jsonArrayResRegistrationDateList, Long idx) {
		jsonArrayResRegistrationDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			repoResRegistrationDateList.save(ResRegistrationDateList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resRegistrationDate(GowidUtils.getEmptyStringToString(obj, "resRegistrationDate"))
					.resNote(GowidUtils.getEmptyStringToString(obj, "resNote"))
					.build()
			);
		});
	}

	private void saveResChangeDateList(JSONArray jsonArrayResChangeDateList, Long idx) {
		jsonArrayResChangeDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			repoResChangeDateList.save(ResChangeDateList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resChangeDate(GowidUtils.getEmptyStringToString(obj, "resChangeDate"))
					.resNote(GowidUtils.getEmptyStringToString(obj, "resNote"))
					.build()
			);
		});
	}

	private void saveResStockItemList(JSONArray jsonArrayResStockItemList, Long idx) {
		jsonArrayResStockItemList.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			repoResStockItemList.save(ResStockItemList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resStockType(GowidUtils.getEmptyStringToString(obj, "resStockType"))
					.resStockCnt(GowidUtils.getEmptyStringToString(obj, "resStockCnt"))
					.resTCntIssuedStock(GowidUtils.getEmptyStringToString(obj, "resTCntIssuedStock"))
					.resCapital(GowidUtils.getEmptyStringToString(obj, "resStockCnt"))
					.build()
			);
		});
	}

	private void saveJSONArrayResBalanceSheet(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			repoResBalanceSheet.save(ResBalanceSheet.builder()
					.idxParent(idx)
					.title(GowidUtils.getEmptyStringToString(obj, "title"))
					.amt(GowidUtils.getEmptyStringToString(obj, "amt"))
					.code(GowidUtils.getEmptyStringToString(obj, "code"))
					.number(GowidUtils.getEmptyStringToString(obj, "number"))
					.build()
			);
		});
	}

	private void saveJSONArrayResIncomeStatement(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			repoResIncomeStatement.save(ResIncomeStatement.builder()
					.idxParent(idx)
					.title(GowidUtils.getEmptyStringToString(obj, "title"))
					.amt(GowidUtils.getEmptyStringToString(obj, "amt"))
					.code(GowidUtils.getEmptyStringToString(obj, "code"))
					.number(GowidUtils.getEmptyStringToString(obj, "number"))
					.build()
			);
		});
	}

	private void saveJSONArrayResCostSpecificationList(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			JSONArray jsonArrayItem = (JSONArray) obj.get("resCostSpecification");

			ResCostSpecificationList parent = repoResCostSpecificationList.save(
					ResCostSpecificationList.builder()
							.idxParent(idx)
							.resDocTitle(GowidUtils.getEmptyStringToString(obj, "resDocTitle"))
							.build()
			);

			jsonArrayItem.forEach( item2 -> {
				repoResCostSpecification.save(ResCostSpecification.builder()
						.idxParent(parent.idx())
						.title(GowidUtils.getEmptyStringToString(obj, "title"))
						.amt(GowidUtils.getEmptyStringToString(obj, "amt"))
						.code(GowidUtils.getEmptyStringToString(obj, "code"))
						.number(GowidUtils.getEmptyStringToString(obj, "number"))
						.build()
				);
			});
		});
	}

	private void saveJSONArrayResFinancialStatementList(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			JSONArray jsonArrayItem = (JSONArray) obj.get("resFinancialStatement");

			ResFinancialStatementList parent = repoResFinancialStatementList.save(
					ResFinancialStatementList.builder()
							.idxParent(idx)
							.resDocTitle(GowidUtils.getEmptyStringToString(obj, "resDocTitle"))
							.build()
			);

			jsonArrayItem.forEach( item2 -> {
				repoResCostSpecification.save(ResCostSpecification.builder()
						.idxParent(parent.idx())
						.title(GowidUtils.getEmptyStringToString(obj, "title"))
						.amt(GowidUtils.getEmptyStringToString(obj, "amt"))
						.code(GowidUtils.getEmptyStringToString(obj, "code"))
						.number(GowidUtils.getEmptyStringToString(obj, "number"))
						.build()
				);
			});
		});
	}

	private Long getaLong(Long idxUser, Long idxCorp) {
		if(idxCorp != null){
			if(repoUser.findById(idxUser).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))){
				idxUser = repoCorp.searchIdxUser(idxCorp)
				;
			}
		}
		return idxUser;
	}

	private User findUser(Long idx_user) {
		return repoUser.findById(idx_user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("User")
						.idx(idx_user)
						.build()
		);
	}
}
