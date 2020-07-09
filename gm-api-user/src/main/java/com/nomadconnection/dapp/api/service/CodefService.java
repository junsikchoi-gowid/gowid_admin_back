package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.GwUploadDto;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
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
import com.nomadconnection.dapp.core.dto.ImageConvertDto;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.encryption.Seed128;
import com.nomadconnection.dapp.core.utils.ImageConverter;
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

import java.io.File;
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

	private final D1000Repository repoD1000;
	private final D1100Repository repoD1100;
	private final D1400Repository repoD1400;
	private final D1510Repository repoD1510;
	private final D1520Repository repoD1520;
	private final D1530Repository repoD1530;

	private final ImageConverter converter;
	private final AsyncService asyncService;

	private final CardIssuanceInfoRepository repoCardIssuance;
	private final GwUploadService gwUploadService;

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
						.map(account -> BankDto.ResAccountDto.from(account, false))
						.collect(Collectors.toList());
			}

		}else{
			// 삭제처리
			try {
				JSONObject JSONObjectData = (JSONObject) (jsonObject.get("data"));
				JSONArray JSONObjectErrorData = (JSONArray) JSONObjectData.get("errorList");
				connectedId = GowidUtils.getEmptyStringToString((JSONObject) JSONObjectErrorData.get(0), "extraMessage");
				deleteAccount2(connectedId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 재등록
			strObject = ApiRequest.request(createUrlPath, bodyMap);

			jsonParse = new JSONParser();
			jsonObject = (JSONObject)jsonParse.parse(strObject);
			strResultCode = jsonObject.get("result").toString();
			strResultData = jsonObject.get("data").toString();

			code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();

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

				if (getScrapingAccount(idx)) {
					resAccount = repoResAccount.findConnectedId(idx).stream()
							.map(account -> BankDto.ResAccountDto.from(account, false))
							.collect(Collectors.toList());
				}
			}else{
				try {
					JSONObject JSONObjectData = (JSONObject) (jsonObject.get("data"));
					JSONArray JSONObjectErrorData = (JSONArray) JSONObjectData.get("errorList");
					connectedId = GowidUtils.getEmptyStringToString((JSONObject) JSONObjectErrorData.get(0), "extraMessage");
					deleteAccount2(connectedId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

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
							strResult = getApiResult(KR_BK_1_B_001.krbk1b001(connId, s));
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
											.resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay"))
											.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
											.resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit"))
											.resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName"))
											.resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency"))
											.resAccountStartDate(startDate)
											.resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate"))
											.resAccountName(GowidUtils.getEmptyStringToString(obj,"resAccountName"))
											.resAccountLoanExecNo(GowidUtils.getEmptyStringToString(obj,"resAccountLoanExecNo"))
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
											.resAccountDisplay(GowidUtils.getEmptyStringToString(obj,"resAccountDisplay"))
											.resAccountBalance(GowidUtils.doubleTypeGet(obj.get("resAccountBalance").toString()))
											.resAccountDeposit(GowidUtils.getEmptyStringToString(obj,"resAccountDeposit"))
											.resAccountNickName(GowidUtils.getEmptyStringToString(obj,"resAccountNickName"))
											.resAccountCurrency(GowidUtils.getEmptyStringToString(obj,"resAccountCurrency"))
											.resAccountStartDate(startDate)
											.resAccountEndDate(GowidUtils.getEmptyStringToString(obj,"resAccountEndDate"))
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
	public ResponseEntity deleteAccount2(String connectedId){

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
		HashMap<String, Object> bodyMap = new HashMap<>();

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
			JSONObject JSONObjectData = (JSONObject)(jsonObject.get("data"));
			JSONArray JSONObjectSuccessData = (JSONArray) JSONObjectData.get("successList");
			boolean boolConId = false;

			for(Object item: JSONObjectSuccessData){
				JSONObject obj = (JSONObject) item;
				if(GowidUtils.getEmptyStringToString(obj, "clientType").equals("A")
						&& GowidUtils.getEmptyStringToString(obj, "organization").equals("0002")){
					boolConId = true;
					break;
				}
			}

			connectedId = ((JSONObject)(jsonObject.get("data"))).get("connectedId").toString();

			if(boolConId){
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
			}else{
				deleteAccount2(connectedId); // 삭제
			}
		}else {
			try {
				JSONObject JSONObjectData = (JSONObject) (jsonObject.get("data"));
				JSONArray JSONObjectErrorData = (JSONArray) JSONObjectData.get("errorList");
				connectedId = GowidUtils.getEmptyStringToString((JSONObject) JSONObjectErrorData.get(0), "extraMessage");
				deleteAccount2(connectedId); // 삭제
				log.debug("cf-04000 connectedId = {} ", connectedId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			throw new RuntimeException(code);
		}

		String strResult = null;
		// 국세청 - 증명발급 사업자등록
		strResult = PROOF_ISSUE.proof_issue(
				"0001",
				connectedId,
				"04",
				"01",
				"1",
				"0",
				"",
				"" // 사업자번호
		);

		JSONObject[] jsonObjectProofIssue = getApiResult(strResult);

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

			//파일생성 및 전송
			ImageCreateAndSend(1510, "15100001","0306", strResult,corp.resCompanyIdentityNo());

			String strResult1530 = null;

			// 대법원 - 법인등기부등본
			strResult1530 = CORP_REGISTER.corp_register(
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
			);
			JSONObject[] jsonObjectCorpRegister = getApiResult(strResult1530);

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
				JSONArray jsonArrayResOneStocAmtList = (JSONArray) jsonData2.get("resOneStocAmtList");
				JSONArray jsonArrayResTCntStockIssueList = (JSONArray) jsonData2.get("resTCntStockIssueList");
				JSONArray jsonArrayResStockList = (JSONArray) jsonData2.get("resStockList");
				JSONArray jsonArrayResCorpEstablishDateList = (JSONArray) jsonData2.get("resCorpEstablishDateList");
				JSONArray jsonArrayResCEOList = (JSONArray) jsonData2.get("resCEOList");

				List<String> listResCompanyNmList = saveJSONArray1(jsonArrayResCompanyNmList);
				List<String> listResUserAddrList = saveJSONArray2(jsonArrayResUserAddrList);
				List<String> listResOneStocAmtList = saveJSONArray4(jsonArrayResOneStocAmtList);
				List<String> listResTCntStockIssueList = saveJSONArray5(jsonArrayResTCntStockIssueList);
				List<String> listResCeoList = getJSONArrayCeo(jsonArrayResCEOList);
				List<Object> listResStockList = saveJSONArray6(jsonArrayResStockList);

				String ResCorpEstablishDate = saveJSONArray20(jsonArrayResCorpEstablishDateList);
				String d009 = getJSONArrayCeoType(jsonArrayResCEOList);

				corp.resUserType(d009);

				//파일생성 및 전송

				String strChange[] = {
						"resStockOptionList\" : [ {\n" +
								"        \"resStockOption\" : \"내용 없음\",\n" +
								"        \"resNumber\" : \"0\"\n" +
								"      } ]"
						,"resTypeStockContentList\" : [ {\n" +
						"        \"resNumber\" : \"0\",\n" +
						"        \"resTypeStockContentItemList\" : [ {\n" +
						"          \"resNumber\" : \"0\",\n" +
						"          \"resTypeStockContent\" : \"내용 없음\"\n" +
						"        } ]\n" +
						"} ]"
						,"resConvertibleBondList\" : [ {\n" +
						"        \"resNumber\" : \"0\",\n" +
						"        \"resConvertibleBondItemList\" : [ {\n" +
						"          \"resNumber\" : \"0\",\n" +
						"          \"resConvertibleBond\" : \"내용 없음\"\n" +
						"        } ]\n" +
						"} ]"
						,"resEtcList\" : [ {\n" +
						"        \"resNumber\" : \"0\",\n" +
						"        \"resEtc\" : \"내용 없음\"\n" +
						"      } ]"
				};

				strResult1530 = strResult1530.concat("");
				strResult1530 = strResult1530.replaceAll("resStockOptionList\" : \\[ \\]",strChange[0]);
				strResult1530 = strResult1530.replaceAll("resTypeStockContentList\" : \\[ \\]",strChange[1]);
				strResult1530 = strResult1530.replaceAll("resConvertibleBondList\" : \\[ \\]",strChange[2]);
				strResult1530 = strResult1530.replaceAll("resEtcList\" : \\[ \\]",strChange[3]);

				ImageCreateAndSend(1530, "15300001","0306", strResult1530, corp.resCompanyIdentityNo());

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
						.d010(listResCeoList.size()>=2?listResCeoList.get(1):"")// 대표이사_성명1
						.d011(listResCeoList.size()>=3?Seed128.encryptEcb(listResCeoList.get(2).replaceAll("-","")):"")// 대표이사_주민번호1
						.d014(listResCeoList.size()>=6?listResCeoList.get(5):"")// 대표이사_성명2
						.d015(listResCeoList.size()>=7?Seed128.encryptEcb(listResCeoList.get(6).replaceAll("-","")):"")// 대표이사_주민번호2
						.d018(listResCeoList.size()>=10?listResCeoList.get(9):"")// 대표이사_성명3
						.d019(listResCeoList.size()>=11?Seed128.encryptEcb(listResCeoList.get(10).replaceAll("-","")):"")// 대표이사_주민번호3
						.d029(null)
						.d030(null)
						.d031(null)
						.d032("대표이사")
						.d033("대표이사")
						.d034(listResCeoList.size()>=3?Seed128.encryptEcb(listResCeoList.get(2).replaceAll("-","")):"")// 대표이사_주민번호1
						.d035(listResCeoList.size()>=2?listResCeoList.get(1):"")// 대표이사_성명1
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
						.d016(StringUtils.isEmpty(listResOneStocAmtList.get(1))?ResCorpEstablishDate:listResOneStocAmtList.get(1))// 1주의금액_변경일자
						.d017(StringUtils.isEmpty(listResOneStocAmtList.get(2))?ResCorpEstablishDate:listResOneStocAmtList.get(2))// 1주의금액_등기일자
						.d018(listResTCntStockIssueList.get(0))// 발행할주식의총수
						.d019(listResTCntStockIssueList.get(1))// 발행할주식의총수_변경일자
						.d020(listResTCntStockIssueList.get(2))// 발행할주식의총수_등기일자
						.d021(listResStockList.get(0).toString())// 발행주식현황_총수
						.d022(listD.size()>=1?listD.get(0):"")// 발행주식현황_종류1
						.d023(listD.size()>=2?listD.get(1):"")// 발행주식현황_종류1_수량
						.d024(listD.size()>=3?listD.get(2):"")// 발행주식현황_종류2
						.d025(listD.size()>=4?listD.get(3):"")// 발행주식현황_종류2_수량
						.d026(listD.size()>=5?listD.get(4):"")// 발행주식현황_종류3
						.d027(listD.size()>=6?listD.get(5):"")// 발행주식현황_종류3_수량
						.d028(listD.size()>=7?listD.get(6):"")// 발행주식현황_종류4
						.d029(listD.size()>=8?listD.get(7):"")// 발행주식현황_종류4_수량
						.d030(listD.size()>=9?listD.get(8):"")// 발행주식현황_종류5
						.d031(listD.size()>=10?listD.get(9):"")// 발행주식현황_종류5_수량
						.d032(listD.size()>=11?listD.get(10):"")// 발행주식현황_종류6
						.d033(listD.size()>=12?listD.get(11):"")// 발행주식현황_종류6_수량
						.d034(listD.size()>=13?listD.get(12):"")// 발행주식현황_종류7
						.d035(listD.size()>=14?listD.get(13):"")// 발행주식현황_종류7_수량
						.d036(listD.size()>=15?listD.get(14):"")// 발행주식현황_종류8
						.d037(listD.size()>=16?listD.get(15):"")// 발행주식현황_종류8_수량
						.d038(listD.size()>=17?listD.get(16):"")// 발행주식현황_종류9
						.d039(listD.size()>=18?listD.get(17):"")// 발행주식현황_종류9_수량
						.d040(listD.size()>=19?listD.get(18):"")// 발행주식현황_종류10
						.d041(listD.size()>=20?listD.get(19):"")// 발행주식현황_종류10_수량
						.d042(listResStockList.get(1).toString())// 발행주식현황_자본금의액
						.d043(listResStockList.get(3).toString())// 발행주식현황_변경일자
						.d044(listResStockList.get(4).toString())// 발행주식현황_등기일자
						.d045(listResCeoList.size()>=1?listResCeoList.get(0):"")// 대표이사_직위1
						.d046(listResCeoList.size()>=2?listResCeoList.get(1):"")// 대표이사_성명1
						.d047(listResCeoList.size()>=3?Seed128.encryptEcb(listResCeoList.get(2).replaceAll("-","")):"")// 대표이사_주민번호1
						.d048(listResCeoList.size()>=4?listResCeoList.get(3):"")// 대표이사_주소1
						.d049(listResCeoList.size()>=5?listResCeoList.get(4):"")// 대표이사_직위2
						.d050(listResCeoList.size()>=6?listResCeoList.get(5):"")// 대표이사_성명2
						.d051(listResCeoList.size()>=7?Seed128.encryptEcb(listResCeoList.get(6).replaceAll("-","")):"")// 대표이사_주민번호2
						.d052(listResCeoList.size()>=8?listResCeoList.get(7):"")// 대표이사_주소2
						.d053(listResCeoList.size()>=9?listResCeoList.get(8):"")// 대표이사_직위3
						.d054(listResCeoList.size()>=10?listResCeoList.get(9):"")// 대표이사_성명3
						.d055(listResCeoList.size()>=11?Seed128.encryptEcb(listResCeoList.get(10).replaceAll("-","")):"")// 대표이사_주민번호3
						.d056(listResCeoList.size()>=12?listResCeoList.get(11):"")// 대표이사_주소3
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
//						.d006(listResCeoList.size()>3?Seed128.encryptEcb(listResCeoList.get(2).replaceAll("-","")):"")
//						.d007(listResCeoList.size()>2?listResCeoList.get(1):"")
						.d008("261-81-25793")
						.d009("고위드")
						.d011("")
						.d012("DAAC6F")
						.d013("12")
						.d015("GOWID1")
						.d016("GOWID1")
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

	private List<String> getJSONArrayCeo(JSONArray jsonArrayResCEOList) {
		List<String> str = new ArrayList<>();
		jsonArrayResCEOList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			str.add(GowidUtils.getEmptyStringToString(obj, "resPosition"));
			str.add(GowidUtils.getEmptyStringToString(obj, "resUserNm"));
			str.add(GowidUtils.getEmptyStringToString(obj, "resUserIdentiyNo"));
			str.add(GowidUtils.getEmptyStringToString(obj, "resUserAddr"));
		});
		return str;
	}


	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity RegisterCorpInfo(ConnectedMngDto.CorpInfo dto, Long idxUser,Long idx_CardInfo){

		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		String resCompanyIdentityNo = user.corp().resCompanyIdentityNo();

		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();
		normal.setStatus(true);

		String connectedId = null;

		List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(idxUser);
		if(connectedMng.size() < 1) {
			throw new RuntimeException("CONNECTED ID");
		} else {
			connectedId = connectedMng.get(0).connectedId();
		}

		List<String> listYyyyMm = getFindClosingStandards(dto.getResClosingStandards().trim());

		// 국세청 - 증명발급 표준재무재표
		String finalConnectedId = connectedId;
		String strResult;
		listYyyyMm.forEach(yyyyMm ->{
			JSONObject[] jsonObjectStandardFinancial = new JSONObject[0];
			String strResultTemp = null;
			try {

				strResultTemp = STANDARD_FINANCIAL.standard_financial(
						"0001",
						finalConnectedId,
						yyyyMm,
						"0",
						"04",
						"01",
						"40",
						"",
						resCompanyIdentityNo.replaceAll("-", "").trim() // 사업자번호
				);

				jsonObjectStandardFinancial = getApiResult(strResultTemp);
			} catch (Exception e) {
				e.printStackTrace();
			}

			String jsonObjectStandardFinancialCode = jsonObjectStandardFinancial[0].get("code").toString();
			if (jsonObjectStandardFinancialCode.equals("CF-00000") ) {
				JSONObject jsonData2 = jsonObjectStandardFinancial[1];

				JSONArray resBalanceSheet = (JSONArray) jsonData2.get("resBalanceSheet");
				JSONArray resIncomeStatement = (JSONArray) jsonData2.get("resIncomeStatement");

				AtomicReference<String> strCode228 = new AtomicReference<>();
				AtomicReference<String> strCode001 = new AtomicReference<>();
				AtomicReference<String> strCode334 = new AtomicReference<>();
				AtomicReference<String> strCode382 = new AtomicReference<>();

				resBalanceSheet.forEach(item -> {
					JSONObject obj = (JSONObject) item;
					if(GowidUtils.getEmptyStringToString(obj, "_code").equals("228")){
						strCode228.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
					if(GowidUtils.getEmptyStringToString(obj, "_code").equals("334")){
						strCode334.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
					if(GowidUtils.getEmptyStringToString(obj, "_code").equals("382")){
						strCode382.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
				});

				resIncomeStatement.forEach(item -> {
					JSONObject obj = (JSONObject) item;
					if(GowidUtils.getEmptyStringToString(obj, "_code").equals("001")){
						strCode001.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
					}
				});

				repoD1520.save(D1520.builder()
						.idxCorp(user.corp().idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d003(user.corp().resCompanyIdentityNo().replaceAll("-","")) // 사업자등록번호
						.d004(user.corp().resIssueNo().replaceAll("-","")) // 발급(승인)번호
						.d005(user.corp().resUserIdentiyNo().replaceAll("-","")) // 주민번호
						.d006(user.corp().resCompanyNm()) // 상호(사업장명)
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

				//파일생성 및 전송
				ImageCreateAndSend(1520, 1520+yyyyMm.substring(0,4),"0306", strResultTemp, user.corp().resCompanyIdentityNo());

			}else{
				log.debug("jsonObjectStandardFinancialCode = {} ", jsonObjectStandardFinancialCode);
				log.debug("jsonObjectStandardFinancial message = {} ", jsonObjectStandardFinancial[0].get("message").toString());

				D1530 d1530 = repoD1530.findFirstByIdxCorpOrderByUpdatedAtDesc(user.corp().idx());

				repoD1520.save(D1520.builder()
						.idxCorp(user.corp().idx())
						.c007(CommonUtil.getNowYYYYMMDD())
						.d003(user.corp().resCompanyIdentityNo().replaceAll("-","")) // 사업자등록번호
						.d004(user.corp().resIssueNo().replaceAll("-","")) // 발급(승인)번호
						.d005(user.corp().resUserIdentiyNo().replaceAll("-","")) // 주민번호
						.d006(user.corp().resCompanyNm()) // 상호(사업장명)
						.d007("Y") // 발급가능여부
						.d008("") // 시작일자
						.d009("") // 종료일자
						.d010(user.corp().resUserNm()) // 성명
						.d011(user.corp().resUserAddr()) // 주소
						.d012(user.corp().resBusinessItems()) // 종목
						.d013(user.corp().resBusinessTypes()) // 업태
						.d014(CommonUtil.getNowYYYYMMDD()) // 작성일자
						.d015("") // 귀속연도
						.d016(d1530.getD042()) // 총자산 대차대조표 상의 자본총계(없으면 등기부등본상의 자본금의 액) 희남 버그중
						.d017("0") // 매출   손익계산서 상의 매출액
						.d018("0") // 납입자본금   대차대조표 상의 자본금
						.d019("0") // 자기자본금   대차대조표 상의 자본 총계
						.d020(d1530.getD057()) // 재무조사일   종료일자 (없으면 등기부등본상의 회사성립연월일)
						.build());
			}
		});



		repoD1100.save(D1100.builder()
				.idxCorp(user.corp().idx())
				.c007(CommonUtil.getNowYYYYMMDD())
				.c007("")
				.d001(user.corp().resCompanyIdentityNo().replaceAll("-",""))
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
				.d027(user.corp().resCompanyIdentityNo().replaceAll("-", ""))
				.d028("901")
				.d029("")
				.d030("2")
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

		D1400 d1400 = repoD1400.findFirstByIdxCorpOrderByUpdatedAtDesc(user.corp().idx());
		d1400.setD011(dto.getResBusinessCode());
		repoD1400.save(d1400);

		repoCorp.save(user.corp()
				.resCompanyEngNm(dto.getResCompanyEngNm())
				.resCompanyNumber(dto.getResCompanyPhoneNumber())
				.resBusinessCode(dto.getResBusinessCode())
		);

		D1000 d1000 = repoD1000.findFirstByIdxCorpOrderByUpdatedAtDesc(user.corp().idx());
		String[] corNumber = dto.getResCompanyPhoneNumber().split("-");
		d1000.setD006(!StringUtils.hasText(d1000.getD006()) ? dto.getResCompanyEngNm() : d1000.getD006());
		d1000.setD008(!StringUtils.hasText(d1000.getD008()) ? dto.getResBusinessCode() : d1000.getD008());
		d1000.setD026(!StringUtils.hasText(d1000.getD026()) ? corNumber[0] : d1000.getD026());
		d1000.setD027(!StringUtils.hasText(d1000.getD027()) ? corNumber[1] : d1000.getD027());
		d1000.setD028(!StringUtils.hasText(d1000.getD028()) ? corNumber[2] : d1000.getD028());

		repoD1000.save(d1000);

		//파일생성 및 전송
		String strResultTemp = "{\n" +
				"\t\"data\" : {\n" +
				"\t\t\"resCompanyIdentityNo\" : \"" + user.corp().resCompanyIdentityNo()+ "\" ,\n" +
				"\t\t\"resCompanyNm\" : \""+ user.corp().resCompanyNm()+"\"\n" +
				"\t}\n" +
				"}";

		ImageCreateAndSend(9991, "99910001", "0306", strResultTemp, user.corp().resCompanyIdentityNo());


		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(normal)
				.data(null).build());
	}

	private List<String> getFindClosingStandards(String Mm) {

		List<String> returnYyyyMm = new ArrayList<>();
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

	private List saveJSONArray1(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resCompanyNm"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	private List saveJSONArray2(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resUserAddr"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	private List saveJSONArray3(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	private List saveJSONArray4(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resOneStockAmt"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	private List saveJSONArray5(JSONArray jsonArray) {
		List<String> str = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			str.add(GowidUtils.getEmptyStringToString(obj, "resTCntStockIssue"));
			str.add(saveResChangeDateList(jsonArrayResChangeDateList));
			str.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList));
		});
		return str;
	}

	private List<Object> saveJSONArray6(JSONArray jsonArray) {
		List<Object> returnObj = new ArrayList<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			JSONArray jsonArrayResStockItemList = (JSONArray) obj.get("resStockItemList");
			JSONArray jsonArrayResChangeDateList = (JSONArray) obj.get("resChangeDateList");
			JSONArray jsonArrayResRegistrationDateList = (JSONArray) obj.get("resRegistrationDateList");

			returnObj.add(GowidUtils.getEmptyStringToString(obj, "resTCntIssuedStock")); // 발행주식의 총수
			returnObj.add(GowidUtils.getEmptyStringToString(obj, "resCapital")); // 총액정보
			returnObj.add(jsonArrayResStockItemList); //주식 리스트
			returnObj.add(saveResChangeDateList(jsonArrayResChangeDateList)); // 변경일자
			returnObj.add(saveResRegistrationDateList(jsonArrayResRegistrationDateList)); //등기일자
		});
		return returnObj;
	}

	private String saveJSONArray20(JSONArray jsonArray) {
		AtomicReference<String> str = new AtomicReference<>();
		jsonArray.forEach(item -> {
			JSONObject obj = (JSONObject) item;

			str.set(GowidUtils.getEmptyStringToString(obj, "resCorpEstablishDate"));
		});
		return str.get();
	}

	private String getJSONArrayCeoType(JSONArray jsonArray) {
		String str = null;

		// 1: 단일대표 2: 개별대표 3: 공동대표
		for(Object item: jsonArray){
			JSONObject obj = (JSONObject) item;

			log.debug("resPosition = [{}]", GowidUtils.getEmptyStringToString(obj, "resPosition"));
			if(GowidUtils.getEmptyStringToString(obj, "resPosition").equals("공동대표이사")) {
				str = "3";
				break;
			}else if(jsonArray.size() < 2){
				str = "1";
				break;
			}else {
				str = "2";
				break;
			}
		}
		return str;
	}

	private String saveResRegistrationDateList(JSONArray jsonArrayResRegistrationDateList) {
		AtomicReference<String> str = new AtomicReference<>();

		jsonArrayResRegistrationDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			str.set(GowidUtils.getEmptyStringToString(obj, "resRegistrationDate"));
		});
		return str.get();
	}

	private String saveResChangeDateList(JSONArray jsonArrayResChangeDateList) {
		AtomicReference<String> str = new AtomicReference<>();
		jsonArrayResChangeDateList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			str.set(GowidUtils.getEmptyStringToString(obj, "resChangeDate"));
		});
		return str.get();
	}

	private boolean ImageCreateAndSend(Integer fileCode, String fileName, String cardCode, String jsonStringData, String corpIdNo)
	{
		boolean boolConverter = false;
		ImageConvertDto param =
				ImageConvertDto.builder()
						.mrdType(fileCode)
						.data(jsonStringData)
						.fileName(corpIdNo.replaceAll("-", "").concat(fileName))
						.build();
		try {
			String resultConverter = converter.convertJsonToImage(param);
			if(!resultConverter.isEmpty()){
				boolConverter = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}

		//todo 파일전송
		CardIssuanceInfo cardInfo = null;
		log.debug("boolConverter = {}" , boolConverter);
		if(boolConverter){
			File file = new File(Const.REPORTING_SERVER + param.getFileName() + ".tif");
			log.debug("$file.getName = {}", file.getName());
			try {
				log.debug("$file.getName = {}", file.getName());
				GwUploadDto.Response response;

				for(int i = 0; i < 3 ; i++){
					Thread.sleep(500);
					response = gwUploadService.upload(file, cardCode, fileCode.toString(), corpIdNo.replaceAll("-", ""));
					if(response.getResult().getCode().equals("200")){
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			}
		}
		return boolConverter;
	}
}
