package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.helper.Account;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import com.nomadconnection.dapp.codef.io.sandbox.bk.KR_BK_1_B_001;
import com.nomadconnection.dapp.core.domain.ConnectedMng;
import com.nomadconnection.dapp.core.domain.ResAccount;
import com.nomadconnection.dapp.core.domain.Role;
import com.nomadconnection.dapp.core.domain.User;
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

import java.io.IOException;
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
					// resAccount = repoResAccount.findConnectedId(idx).stream().collect(Collectors.toList());
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
		bodyMap.put(CommonConstant.PAGE_NO, 0);		// 페이지 번호(생략 가능) 생략시 1페이지 값(0) 자동 설정
		// 요청 파라미터 설정 종료

		// API 요청

		// 응답결과 확인
		return ApiRequest.request(urlPath, bodyMap);
	}
}