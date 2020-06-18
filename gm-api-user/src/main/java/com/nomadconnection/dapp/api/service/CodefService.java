package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.codef.io.helper.Account;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
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
	private final D1400Repository repoD1400;
	private final D1510Repository repoD1510;
	private final D1520Repository repoD1520;
	private final D1530Repository repoD1530;

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
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
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
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
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
		accountMap1.put("clientType",  	"A");   // 고객구분(P: 개인, B: 기업)
		accountMap1.put("organization",	"0002");// 기관코드
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
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
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
				accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
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
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			list.add(accountMap1);
		}

		for( String s : CommonConstant.LISTCARD){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.CARDTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			accountMap1.put("certType",     CommonConstant.CERTTYPE);
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
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			list.add(accountMap1);
		}

		for( String s : CommonConstant.LISTCARD){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.CARDTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
			accountMap1.put("organization",	s);// 기관코드
			accountMap1.put("loginType",  	"0");   // 로그인타입 (0: 인증서, 1: ID/PW)
			accountMap1.put("certType",     CommonConstant.CERTTYPE);
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
	public String list(String connectedId) throws IOException, InterruptedException, ParseException {
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

		return result;
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

		//	사용자 조회
		User user = repoUser.findById(idxUser).orElseThrow(
				() -> new RuntimeException("UserNotFound")
		);

		for( String s : CommonConstant.LISTBANK){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
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
			accountMap1.put("clientType",  	"B");   // 고객구분(P: 개인, B: 기업)
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
		accountMap1.put("clientType",  	"A");   	// 통합 고객구분 A
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
								.user(user)
								.build()
				);
			}

			user.corp(corp);
			repoUser.save(user);

			// 대법원 - 법인등기부등본
			JSONObject[] jsonObjectCorpRegister = getApiResult(CORP_REGISTER.corp_register(
					"0002",
					"0261057000",
					RSAUtil.encryptRSA("6821", CommonConstant.PUBLIC_KEY),
					"2",
					GowidUtils.getEmptyStringToString(jsonData, "resUserIdentiyNo").replaceAll("-","").trim(),
					"1",
					"T34029396293",
					"gowid99!",
					"",
					"",
					"",
					"",
					"1",
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

				List<String> listResCompanyNmList = saveJSONArray1(jsonArrayResCompanyNmList, parent.idx());
				List<String> listResUserAddrList = saveJSONArray2(jsonArrayResUserAddrList, parent.idx());
				List<String> listResNoticeMethodList = saveJSONArray3(jsonArrayResNoticeMethodList, parent.idx());
				List<String> listResOneStocAmtList = saveJSONArray4(jsonArrayResOneStocAmtList, parent.idx());
				List<String> listResTCntStockIssueList = saveJSONArray5(jsonArrayResTCntStockIssueList, parent.idx());
				List<Object> listResStockList = saveJSONArray6(jsonArrayResStockList, parent.idx());
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
				String ResCorpEstablishDate = saveJSONArray20(jsonArrayResCorpEstablishDateList, parent.idx());
				saveJSONArray21(jsonArrayResRegistrationRecReasonList, parent.idx());
				String d009 = saveJSONArray22(jsonArrayResCEOList, parent.idx());

				corp.resUserType(d009);


				repoD1000.save(D1000.builder()
						.idxCorp(corp.idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d001(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo").replaceAll("-",""))
						.d002(GowidUtils.getEmptyStringToString(jsonData, "resUserIdentiyNo").replaceAll("-",""))
						.d003(GowidUtils.getEmptyStringToString(jsonData, "resCompanyNm"))
						.d004("400")
						.d005("06")
						.d007(GowidUtils.getEmptyStringToString(jsonData, "resRegisterDate"))
						.d009(d009) // 1: 단일대표 2: 개별대표 3: 공동대표
						.d029(null)
						.d030(null)
						.d031(null)
						.d032("대표이사")
						.d033("대표이사")
						.d044("0113")
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
						.d058(null) // 001 IFRS, 002 외감, 003 비외감, 004 비일반공공
						.d063(null)
						.d067(null)
						.d068(null)
						.d069(null)
						.d070(null)
						.build());

				repoD1510.save(D1510.builder()
						.idxCorp(corp.idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d003(corp.resIssueNo().replaceAll("-","")) // 발급번호
						.d004(corp.resCompanyNm()) // 법인명(상호)
						.d005(corp.resCompanyIdentityNo().replaceAll("-","")) // 사업자등록번호
						.d006(corp.resBusinessmanType()) // 사업자종류
						.d007(corp.resUserNm()) // 성명(대표자)
						.d008(corp.resUserAddr()) // 사업장소재지(주소)
						.d009(corp.resUserIdentiyNo().replaceAll("-","")) // 주민등록번호
						.d010(corp.resOpenDate()) // 개업일
						.d011(corp.resRegisterDate()) // 사업자등록일
						.d012(corp.resIssueOgzNm()) // 발급기관
						.d013(corp.resBusinessTypes()) // 업태
						.d014(corp.resBusinessItems()) // 종목
						.build());


				JSONArray jsonArrayResStockItemList = (JSONArray)listResStockList.get(2);
				List<String> listD = new ArrayList<>(20);
				jsonArrayResStockItemList.forEach(item -> {
					JSONObject obj = (JSONObject) item;
					listD.add(GowidUtils.getEmptyStringToString(obj, "resStockType"));
					listD.add(GowidUtils.getEmptyStringToString(obj, "resStockCnt"));
				});

				repoD1530.save(D1530.builder()
						.idxCorp(corp.idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d003("등기사항전부증명서")// 문서제목
						.d004(GowidUtils.getEmptyStringToString(jsonData2, "resRegistrationNumber").replaceAll("-",""))// 등기번호
						.d005(GowidUtils.getEmptyStringToString(jsonData2, "resRegNumber").replaceAll("-",""))// 등록번호
						.d006(GowidUtils.getEmptyStringToString(jsonData2, "commCompetentRegistryOffice"))// 관할등기소
						.d007(GowidUtils.getEmptyStringToString(jsonData2, "resPublishRegistryOffice"))// 발행등기소
						.d008(GowidUtils.getEmptyStringToString(jsonData2, "resPublishDate"))// 발행일자
						.d009(listResCompanyNmList.get(0))// 상호
						.d010(listResCompanyNmList.get(1))// 상호_변경일자
						.d011(listResCompanyNmList.get(2))// 상호_등기일자
						.d012(listResUserAddrList.get(0))// 본점주소
						.d013(listResUserAddrList.get(1))// 본점주소_변경일자
						.d014(listResUserAddrList.get(2))// 본점주소_등기일자
						.d015(listResOneStocAmtList.get(0))// 1주의금액
						.d016(listResOneStocAmtList.get(1))// 1주의금액_변경일자
						.d017(listResOneStocAmtList.get(2))// 1주의금액_등기일자
						.d018(listResTCntStockIssueList.get(0))// 발행할주식의총수
						.d019(listResTCntStockIssueList.get(1))// 발행할주식의총수_변경일자
						.d020(listResTCntStockIssueList.get(2))// 발행할주식의총수_등기일자
						.d021(listResStockList.get(0).toString())// 발행주식현황_총수
						.d022(listD.size()>1?listD.get(0):"")// 발행주식현황_종류1
						.d023(listD.size()>2?listD.get(1):"")// 발행주식현황_종류1_수량
						.d024(listD.size()>3?listD.get(2):"")// 발행주식현황_종류2
						.d025(listD.size()>4?listD.get(3):"")// 발행주식현황_종류2_수량
						.d026(listD.size()>5?listD.get(4):"")// 발행주식현황_종류3
						.d027(listD.size()>6?listD.get(5):"")// 발행주식현황_종류3_수량
						.d028(listD.size()>7?listD.get(6):"")// 발행주식현황_종류4
						.d029(listD.size()>8?listD.get(7):"")// 발행주식현황_종류4_수량
						.d030(listD.size()>9?listD.get(8):"")// 발행주식현황_종류5
						.d031(listD.size()>10?listD.get(9):"")// 발행주식현황_종류5_수량
						.d032(listD.size()>11?listD.get(10):"")// 발행주식현황_종류6
						.d033(listD.size()>12?listD.get(11):"")// 발행주식현황_종류6_수량
						.d034(listD.size()>13?listD.get(12):"")// 발행주식현황_종류7
						.d035(listD.size()>14?listD.get(13):"")// 발행주식현황_종류7_수량
						.d036(listD.size()>15?listD.get(14):"")// 발행주식현황_종류8
						.d037(listD.size()>16?listD.get(15):"")// 발행주식현황_종류8_수량
						.d038(listD.size()>17?listD.get(16):"")// 발행주식현황_종류9
						.d039(listD.size()>18?listD.get(17):"")// 발행주식현황_종류9_수량
						.d040(listD.size()>19?listD.get(18):"")// 발행주식현황_종류10
						.d041(listD.size()>20?listD.get(19):"")// 발행주식현황_종류10_수량
						.d042(listResStockList.get(1).toString())// 발행주식현황_자본금의액
						.d043(listResStockList.get(3).toString())// 발행주식현황_변경일자
						.d044(listResStockList.get(4).toString())// 발행주식현황_등기일자
						// 대표이사_직위1
						// 대표이사_성명1
						// 대표이사_주민번호1
						// 대표이사_주소1
						// 대표이사_직위2
						// 대표이사_성명2
						// 대표이사_주민번호2
						// 대표이사_주소2
						// 대표이사_직위3
						// 대표이사_성명3
						// 대표이사_주민번호3
						// 대표이사_주소3
						.d057(ResCorpEstablishDate)// 법인성립연월일
						.build());

				repoD1400.save(D1400.builder()
						.idxCorp(corp.idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d001("2")
						.d002(corp.resCompanyIdentityNo().replaceAll("-",""))
						.d003("01")
						.d004(corp.resCompanyNm().replaceAll("-",""))
						.d005("06")
						.d008("261-81-25793")
						.d009("고위드")
						.d011("")
						.d012("DAAC6F")
						.d013("12")
						.build());

				repoCardIssuance.save(CardIssuanceInfo.builder().corp(corp).build());
			}else{
				normal.setStatus(false);
				normal.setKey(jsonObjectCorpRegisterCode);
				normal.setValue(jsonObjectCorpRegister[0].get("message").toString());
			}
		}else{
		normal.setStatus(false);
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
				.resUserType(d1000 != null ? d1000.getD009() : null)
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
						.setD006(!StringUtils.hasText(d1000.getD006()) ? dto.getEngCorName() : d1000.getD006())
						.setD008(!StringUtils.hasText(d1000.getD008()) ? dto.getBusinessCode() : d1000.getD008())
						.setD026(!StringUtils.hasText(d1000.getD026()) ? corNumber[0] : d1000.getD026())
						.setD027(!StringUtils.hasText(d1000.getD027()) ? corNumber[1] : d1000.getD027())
						.setD028(!StringUtils.hasText(d1000.getD028()) ? corNumber[2] : d1000.getD028())
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

				saveJSONArrayResBalanceSheet(resBalanceSheet, parentStandardFinancial.idx()); // 표준대차대조표
				saveJSONArrayResIncomeStatement(resIncomeStatement, parentStandardFinancial.idx()); // 표준손익계산서
				saveJSONArrayResCostSpecificationList(resCostSpecificationList, parentStandardFinancial.idx()); // 표준원가명세서
				saveJSONArrayResFinancialStatementList(resFinancialStatementList, parentStandardFinancial.idx()); // 제조원가명세서, 공사원가명세서, 임대원가명세서, 분양원가명세서, 운송원가명세서, 기타원가명세서

				AtomicReference<String> strCode228 = new AtomicReference<>();
				AtomicReference<String> strCode001 = new AtomicReference<>();
				AtomicReference<String> strCode334 = new AtomicReference<>();
				AtomicReference<String> strCode382 = new AtomicReference<>();

				resBalanceSheet.forEach(item -> {
					JSONObject obj = (JSONObject) item;
					if(GowidUtils.getEmptyStringToString(obj, "code").equals("228")){
						strCode228.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
					if(GowidUtils.getEmptyStringToString(obj, "code").equals("334")){
						strCode334.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
					if(GowidUtils.getEmptyStringToString(obj, "code").equals("382")){
						strCode382.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
				});

				resIncomeStatement.forEach(item -> {
					JSONObject obj = (JSONObject) item;
					if(GowidUtils.getEmptyStringToString(obj, "code").equals("001")){
						strCode001.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
				});

				repoD1520.save(D1520.builder()
						.idxCorp(user.get().corp().idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d003(user.get().corp().resCompanyIdentityNo().replaceAll("-","")) // 사업자등록번호
						.d004(user.get().corp().resIssueNo().replaceAll("-","")) // 발급(승인)번호
						.d005(user.get().corp().resUserIdentiyNo().replaceAll("-","")) // 주민번호
						.d006(user.get().corp().resCompanyNm()) // 상호(사업장명)
						.d007("Y") // 발급가능여부
						.d008(GowidUtils.getEmptyStringToString(jsonData2, "commStartDate")) // 시작일자
						.d009(GowidUtils.getEmptyStringToString(jsonData2, "commEndDate")) // 종료일자
						.d010(GowidUtils.getEmptyStringToString(jsonData2, "resUserNm")) // 성명
						.d011(GowidUtils.getEmptyStringToString(jsonData2, "resUserAddr")) // 주소
						.d012(GowidUtils.getEmptyStringToString(jsonData2, "resBusinessItems")) // 종목
						.d013(GowidUtils.getEmptyStringToString(jsonData2, "resBusinessTypes")) // 업태
						.d014(GowidUtils.getEmptyStringToString(jsonData2, "resReportingDate")) // 작성일자
						.d015(GowidUtils.getEmptyStringToString(jsonData2, "resAttrYear")) // 귀속연도
						.d016(strCode228.get()) // 총자산   대차대조표 상의 자본총계(없으면 등기부등본상의 자본금의 액)
						.d017(strCode001.get()) // 매출   손익계산서 상의 매출액
						.d018(strCode334.get()) // 납입자본금   대차대조표 상의 자본금
						.d019(strCode382.get()) // 자기자본금   대차대조표 상의 자본 총계
						.d020(GowidUtils.getEmptyStringToString(jsonData2, "commEndDate")) // 재무조사일   종료일자 (없으면 등기부등본상의 회사성립연월일)
						.build());

			}else{
				log.debug("jsonObjectStandardFinancialCode = {} ", jsonObjectStandardFinancialCode);
				log.debug("jsonObjectStandardFinancial message = {} ", jsonObjectStandardFinancial[0].get("message").toString());

				Optional<ResRegisterEntriesList> optResRegisterEntriesList = repoResRegisterEntriesList.findTopByIdxCorpOrderByIdxDesc(user.get().corp().idx());
				Optional<ResStockList> otpRepoResStockList = repoResStockList.findTopByIdxParentOrderByIdxDesc(optResRegisterEntriesList.get().idx());
				Optional<ResCorpEstablishDateList> otpResCorpEstablishDateList = repoResCorpEstablishDateList.findTopByIdxParentOrderByIdxDesc(optResRegisterEntriesList.get().idx());

				repoD1520.save(D1520.builder()
						.idxCorp(user.get().corp().idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d003(user.get().corp().resCompanyIdentityNo().replaceAll("-","")) // 사업자등록번호
						.d004(user.get().corp().resIssueNo().replaceAll("-","")) // 발급(승인)번호
						.d005(user.get().corp().resUserIdentiyNo().replaceAll("-","")) // 주민번호
						.d006(user.get().corp().resCompanyNm()) // 상호(사업장명)
						.d007("Y") // 발급가능여부
						.d008("") // 시작일자
						.d009("") // 종료일자
						.d010(user.get().corp().resUserNm()) // 성명
						.d011(user.get().corp().resUserAddr()) // 주소
						.d012(user.get().corp().resBusinessItems()) // 종목
						.d013(user.get().corp().resBusinessTypes()) // 업태
						.d014(CommonUtil.getNowYYYYMMDD()) // 작성일자
						.d015("") // 귀속연도
						.d016(otpRepoResStockList.get().resCapital()) // 총자산   대차대조표 상의 자본총계(없으면 등기부등본상의 자본금의 액) 희남 버그중
						.d017("0") // 매출   손익계산서 상의 매출액
						.d018("0") // 납입자본금   대차대조표 상의 자본금
						.d019("0") // 자기자본금   대차대조표 상의 자본 총계
						.d020(otpResCorpEstablishDateList.get().resCorpEstablishDate()) // 재무조사일   종료일자 (없으면 등기부등본상의 회사성립연월일)
						.build());
			}
		});

		repoD1100.save(D1100.builder()
				.idxCorp(user.get().corp().idx())
				.c007(CommonUtil.getNowYYYYMMDD())
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
				.d024("")
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

		D1400 d1400 = repoD1400.findFirstByIdxCorpOrderByUpdatedAtDesc(user.get().corp().idx());
		d1400.setD011(dto.getResBusinessCode());
		repoD1400.save(d1400);

		repoCorp.save(user.get().corp()
				.resCompanyEngNm(dto.getResCompanyEngNm())
				.resCompanyNumber(dto.getResCompanyPhoneNumber())
				.resBusinessCode(dto.getResBusinessCode())
		);

		CardIssuanceInfo cardInfo;
		try {
			cardInfo = findCardIssuanceInfo(user.get().corp());
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

	private List saveJSONArray1(JSONArray jsonArray, Long idx ) {
		List<String> str = new ArrayList<>();
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

			str.add(GowidUtils.getEmptyStringToString(obj, "resCompanyNm"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList, parent.idx(), "ResCompanyNmList"));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx(), "ResCompanyNmList"));
		});
		return str;
	}

	private List saveJSONArray2(JSONArray jsonArray, Long idx ) {
		List<String> str = new ArrayList<>();
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

			str.add(GowidUtils.getEmptyStringToString(obj, "resUserAddr"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList, parent.idx(),"ResUserAddrList"));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx(),"ResUserAddrList"));
		});
		return str;
	}

	private List saveJSONArray3(JSONArray jsonArray, Long idx ) {
		List<String> str = new ArrayList<>();
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

			str.add(saveResChangeDateList(jsonArrayResChangeDateList, parent.idx(), "ResNoticeMethodList" ));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx(), "ResNoticeMethodList"));
		});
		return str;
	}

	private List saveJSONArray4(JSONArray jsonArray, Long idx ) {
		List<String> str = new ArrayList<>();
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

			str.add(GowidUtils.getEmptyStringToString(obj, "resOneStockAmt"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList, parent.idx(), "ResOneStocAmtList"));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx(), "ResOneStocAmtList"));
		});
		return str;
	}

	private List saveJSONArray5(JSONArray jsonArray, Long idx ) {
		List<String> str = new ArrayList<>();
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

			str.add(GowidUtils.getEmptyStringToString(obj, "resTCntStockIssue"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList, parent.idx(), "ResTCntStockIssueList"));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx(), "ResTCntStockIssueList"));
		});
		return str;
	}

	private List<Object> saveJSONArray6(JSONArray jsonArray, Long idx ) {
		List<Object> returnObj = new ArrayList<>();
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

			saveResStockItemList(jsonArrayResStockItemList, parent.idx());

			returnObj.add(GowidUtils.getEmptyStringToString(obj, "resTCntIssuedStock")); // 발행주식의 총수
			returnObj.add(GowidUtils.getEmptyStringToString(obj, "resCapital")); // 총액정보
			returnObj.add(jsonArrayResStockItemList); //주식 리스트
			returnObj.add(saveResChangeDateList(jsonArrayResChangeDateList, parent.idx(),"ResStockList")); // 변경일자
			returnObj.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList, parent.idx(),"ResStockList")); //등기일자
		});
		return returnObj;
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

	private String saveJSONArray20(JSONArray jsonArray, Long idx ) {
		AtomicReference<String> str = new AtomicReference<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			str.set(GowidUtils.getEmptyStringToString(obj, "resCorpEstablishDate"));

			repoResCorpEstablishDateList.save(ResCorpEstablishDateList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resCorpEstablishDate(GowidUtils.getEmptyStringToString(obj, "resCorpEstablishDate"))
					.build()
			);
		});
		return str.get();
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
		AtomicReference<String> returnStr =  new AtomicReference<String>("");

		// 1: 단일대표 2: 개별대표 3: 공동대표
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			if(GowidUtils.getEmptyStringToString(obj, "resPosition").equals("공동대표이사")) {
				returnStr.set("3");
			}else if(jsonArray.size() < 2 && GowidUtils.getEmptyStringToString(obj, "resPosition").equals("대표이사")){
				returnStr.set("1");
			}else {
				returnStr.set("2");
			}

			repoResCEOList.save(ResCEOList.builder()
					.idxParent(idx)
					.resPosition(GowidUtils.getEmptyStringToString(obj, "resPosition"))
					.resUserNm(GowidUtils.getEmptyStringToString(obj, "resUserNm"))
					.resUserIdentiyNo(GowidUtils.getEmptyStringToString(obj, "resUserIdentiyNo"))
					.resUserAddr(GowidUtils.getEmptyStringToString(obj, "resUserAddr"))
					.build()
			);
		});
		return returnStr.get();
	}

	private String saveResRegistrationDateList(JSONArray jsonArrayResRegistrationDateList, Long idx,String Type) {
		AtomicReference<String> str = new AtomicReference<>();

		jsonArrayResRegistrationDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			str.set(GowidUtils.getEmptyStringToString(obj, "resRegistrationDate"));

			repoResRegistrationDateList.save(ResRegistrationDateList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resRegistrationDate(GowidUtils.getEmptyStringToString(obj, "resRegistrationDate"))
					.resNote(GowidUtils.getEmptyStringToString(obj, "resNote"))
					.type(Type)
					.build()
			);
		});
		return str.get();
	}

	private String saveResChangeDateList(JSONArray jsonArrayResChangeDateList, Long idx, String Type) {
		AtomicReference<String> str = new AtomicReference<>();
		jsonArrayResChangeDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			str.set(GowidUtils.getEmptyStringToString(obj, "resChangeDate"));

			repoResChangeDateList.save(ResChangeDateList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resChangeDate(GowidUtils.getEmptyStringToString(obj, "resChangeDate"))
					.resNote(GowidUtils.getEmptyStringToString(obj, "resNote"))
					.type(Type)
					.build()
			);
		});
		return str.get();
	}

	private List<String> saveResStockItemList(JSONArray jsonArrayResStockItemList, Long idx) {
		List<String> returnStr =  new ArrayList<>();

		jsonArrayResStockItemList.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			returnStr.add(GowidUtils.getEmptyStringToString(obj, "resTCntIssuedStock"));
			returnStr.add(GowidUtils.getEmptyStringToString(obj, "resCapital"));

			repoResStockItemList.save(ResStockItemList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resStockType(GowidUtils.getEmptyStringToString(obj, "resStockType"))
					.resStockCnt(GowidUtils.getEmptyStringToString(obj, "resStockCnt"))
					.resTCntIssuedStock(GowidUtils.getEmptyStringToString(obj, "resTCntIssuedStock"))
					.resCapital(GowidUtils.getEmptyStringToString(obj, "resCapital"))
					.build()
			);
		});

		return returnStr;
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
