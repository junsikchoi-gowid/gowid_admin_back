package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.helper.Account;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import com.nomadconnection.dapp.codef.io.sandbox.bk.KR_BK_1_B_001;
import com.nomadconnection.dapp.codef.io.sandbox.pb.CORP_REGISTER;
import com.nomadconnection.dapp.codef.io.sandbox.pb.PROOF_ISSUE;
import com.nomadconnection.dapp.codef.io.sandbox.pb.STANDARD_FINANCIAL;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
	private final ResRegistrationDateListRepository repoResRegistrationDateList;
	private final ResConvertibleBondItemListRepository repoResConvertibleBondItemList;
	private final ResWarrantBondItemListRepository repoResWarrantBondItemList;
	private final ResParticipatingBondItemListRepository repoResParticipatingBondItemList;
	private final ResTypeStockContentItemListRepository repoResTypeStockContentItemList;
	private final ResCCCapitalStockItemListRepository repoResCCCapitalStockItemList;



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
		// List<ResAccount> resAccount = null;
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
		String connectedId = "7S8fv4cNQauaRk-22.l5kD";

		/*
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
				connectedId = ((JSONObject)(jsonObject.get("data"))).get("connectedId").toString();

				if(!repoConnectedMng.findByConnectedIdAndIdxUser(connectedId,idxUser).isPresent()) {
					repoConnectedMng.save(ConnectedMng.builder()
							.connectedId(connectedId)
							.idxUser(idxUser)
							.name(dto.getName())
							.startDate(dto.getStartDate())
							.endDate(dto.getEndDate())
							.desc1(dto.getDesc1())
							.desc2(dto.getDesc2())
							.build()
					);
				}
		}else{
			throw new RuntimeException(code);
		}
		*/

		// 국세청 - 증명발급 사업자등록
		JSONObject[] jsonObjectProofIssue = getApiResult(PROOF_ISSUE.proof_issue(
				"0001",
				connectedId,
				"04",
				"01",
				"1",
				"0",
				"01",
				dto.getIdentity()
		));

		String jsonObjectProofIssueCode = jsonObjectProofIssue[0].get("code").toString();
		if (jsonObjectProofIssueCode.equals("CF-00000") ) {
			JSONObject jsonData = jsonObjectProofIssue[1];

			// JSONObject jsonData = (JSONObject) jsonDataYn.get("resRegisterEntriesList");

			// todo 이미 가입된 회사의 경우 처리 필요 - 안되게 막음
			//	중복체크
//			if (repoCorp.findByResCompanyIdentityNo(dto.getIdentity()).isPresent()) {
//				if (log.isDebugEnabled()) {
//					log.debug("([ registerBrandCorp ]) registerBrandCorp ALREADY EXIST, $idxUser='{}', $resCompanyIdentityNo='{}'", idxUser, dto.getIdentity());
//				}
//				throw AlreadyExistException.builder()
//						.resource(dto.getIdentity())
//						.build();
//			}

			//	사용자 조회
			Optional<User> user = repoUser.findById(idxUser);

			repoCorp.save(
					Corp.builder()
							.resJointRepresentativeNm(GowidUtils.getEmptyStringToString(jsonData,"resJointRepresentativeNm"))
							.resIssueOgzNm(GowidUtils.getEmptyStringToString(jsonData,"resIssueOgzNm"))
							.resCompanyNm(GowidUtils.getEmptyStringToString(jsonData,"resCompanyNm"))
							.resBusinessTypes(GowidUtils.getEmptyStringToString(jsonData,"resBusinessTypes"))
							.resBusinessItems(GowidUtils.getEmptyStringToString(jsonData,"resBusinessItems"))
							.resBusinessmanType(GowidUtils.getEmptyStringToString(jsonData,"resBusinessmanType"))
							.resCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData,"resCompanyIdentityNo"))
							.resIssueNo(GowidUtils.getEmptyStringToString(jsonData,"resIssueNo"))
							.resJointIdentityNo(GowidUtils.getEmptyStringToString(jsonData,"resJointIdentityNo"))
							.resOpenDate(GowidUtils.getEmptyStringToString(jsonData,"resOpenDate"))
							.resOriGinalData(GowidUtils.getEmptyStringToString(jsonData,"resOriGinalData"))
							.resRegisterDate(GowidUtils.getEmptyStringToString(jsonData,"resRegisterDate"))
							.resUserAddr(GowidUtils.getEmptyStringToString(jsonData,"resUserAddr"))
							.resUserIdentiyNo(GowidUtils.getEmptyStringToString(jsonData,"resUserIdentiyNo"))
							.resUserNm(GowidUtils.getEmptyStringToString(jsonData,"resUserNm"))
							.status(CorpStatus.PENDING)
							.user(user.get())
							.build()
			);


			// 국세청 - 법인등기부등본
			JSONObject[] jsonObjectCorpRegister = getApiResult(CORP_REGISTER.corp_register(
					"0002",
					"01000000000",
					"YZUWGj6ZYnK",
					"0",
					"주식회사 데일리금융그룹(DAYLI Financial Group Inc)", // GowidUtils.getEmptyStringToString(jsonData,"resCompanyNm"),
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
					""
			));

			String jsonObjectCorpRegisterCode = jsonObjectCorpRegister[0].get("code").toString();
			if (jsonObjectCorpRegisterCode.equals("CF-00000") ) {
				JSONObject jsonDataCorpRegister = jsonObjectCorpRegister[1];

			if(!jsonDataCorpRegister.get("resIssueYN").toString().equals("1")){
				throw new RuntimeException("발행실패");
			}

				JSONObject jsonData2 = (JSONObject) jsonDataCorpRegister.get("resRegisterEntriesList");

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

				ResRegisterEntriesList parent = repoResRegisterEntriesList.save(
						ResRegisterEntriesList.builder()
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

			}

			// 국세청 - 증명발급 표준재무재표
			JSONObject[] jsonObjectStandardFinancial = getApiResult(STANDARD_FINANCIAL.standard_financial(
					"0001",
					connectedId,
					"2000",
					"0",
					"04",
					"01",
					"40",
					"",
					dto.getIdentity() // GowidUtils.getEmptyStringToString(jsonData,"resUserIdentiyNo")
					));
		}

		// 은행정보가 있는지 체크
		// serviceScraping.scrapingBankN45DayDataList(dto, idxUser);
		// Thread.sleep(1000);
		// return refresh(idxUser,null);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(normal)
				.data(null).build());
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

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			ResStockList parent = repoResStockList.save(ResStockList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resStockItemList(GowidUtils.getEmptyStringToString(obj, "resStockItemList"))
					.build()
			);

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

	private void saveJSONArray22(JSONArray jsonArray, Long idx ) {
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			ResCEOList parent = repoResCEOList.save(ResCEOList.builder()
					.idxParent(idx)
					.resNumber(GowidUtils.getEmptyStringToString(obj, "resNumber"))
					.resUserNm(GowidUtils.getEmptyStringToString(obj, "resUserNm"))
					.resUserIdentiyNo(GowidUtils.getEmptyStringToString(obj, "resUserIdentiyNo"))
					.resUserAddr(GowidUtils.getEmptyStringToString(obj, "resUserAddr"))
					.build()
			);
		});
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

	public ResponseEntity refresh(Long idxUser, Long idxCorp) {

		idxUser = getaLong(idxUser, idxCorp);

		List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(idxUser);
		return ResponseEntity.ok().body(BusinessResponse.builder().data(returnData).build());
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
}
