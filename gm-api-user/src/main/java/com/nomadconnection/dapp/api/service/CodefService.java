package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.dto.Common;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.codef.io.sandbox.bk.KR_BK_1_B_001;
import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResConCorpListRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.res.ResAccountTypeStatus;
import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import com.nomadconnection.dapp.core.domain.res.ResConCorpListStatus;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.exception.NotFoundException;
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
	private final ResConCorpListRepository repoResConCorpList;

	private final String urlPath = CommonConstant.getRequestDomain();

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity findConnectedIdList(Long idx) {

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				repoConnectedMng.findIdxUser(idx)
		).build());
	}


	@Transactional(rollbackFor = Exception.class)
	public boolean getScrapingAccount(Long idx) {
		List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(idx);

		connectedMng.forEach(mngItem->{
					String connId = mngItem.connectedId();
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


	/**
	 * ?????? ????????????
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public String list(String connectedId) throws IOException, InterruptedException, ParseException {
		// ?????? URL ??????
		String urlPath = CommonConstant.getRequestDomain() + CommonConstant.GET_ACCOUNTS;

		// ?????? ???????????? ?????? ??????
		HashMap<String, Object> bodyMap = new HashMap<>();

		// String connectedId = "45t4DJOD44M9uwH7zxSgBg";	// ??????????????? ??????/????????? ?????? ?????? ??? ???????????? ????????????????????? ??????
		bodyMap.put(CommonConstant.CONNECTED_ID, connectedId);
		// ?????? ???????????? ?????? ??????

		// API ??????
		String result = ApiRequest.request(urlPath, bodyMap);

		// ???????????? ??????
		System.out.println(result);

		return result;
	}


	/**
	 * connectedId ????????????
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public String connectedIdList() throws IOException, InterruptedException, ParseException {
		// ?????? URL ??????
		String urlPath = CommonConstant.getRequestDomain() + CommonConstant.GET_CONNECTED_IDS;

		// ?????? ???????????? ?????? ??????
		HashMap<String, Object> bodyMap = new HashMap<>();
		bodyMap.put(CommonConstant.PAGE_NO, 0);        // ????????? ??????(?????? ??????) ????????? 1????????? ???(0) ?????? ??????
		// ?????? ???????????? ?????? ??????

		// API ??????

		// ???????????? ??????
		return ApiRequest.request(urlPath, bodyMap);
	}


	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public void ProcAddConnectedId(JSONObject jsonObject, String connectedId, Long idxCorp) {
		JSONParser jsonParse = new JSONParser();
		String strResultCode = jsonObject.get("result").toString();
		String strResultData = jsonObject.get("data").toString();

		String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();
		if(code.equals("CF-00000") || code.equals("CF-04012")) {
			JSONArray successList = (JSONArray)(((JSONObject) jsonParse.parse(strResultData)).get("successList"));
			successList.forEach(object -> {
				JSONObject obj = (JSONObject) object;
				repoResConCorpList.save(
						ResConCorpList.builder()
								.organization(GowidUtils.getEmptyStringToString(obj, "organization"))
								.businessType(GowidUtils.getEmptyStringToString(obj, "businessType"))
								.clientType(GowidUtils.getEmptyStringToString(obj, "clientType"))
								.code(GowidUtils.getEmptyStringToString(obj, "code"))
								.countryCode(GowidUtils.getEmptyStringToString(obj, "countryCode"))
								.extraMessage(GowidUtils.getEmptyStringToString(obj, "extraMessage"))
								.loginType(GowidUtils.getEmptyStringToString(obj, "loginType"))
								.message(GowidUtils.getEmptyStringToString(obj, "message"))
								.connectedId(connectedId)
								.status(ResConCorpListStatus.NORMAL)
								.idxCorp(idxCorp)
								.build()
				);
			});
		}
	}


	@Transactional(rollbackFor = Exception.class)
	public List<BankDto.ResAccountDto> registerAccountAddCreate(Common.Account dto, Long idxUser) throws Exception{

		User user = repoUser.findById(idxUser).orElseThrow(() -> UserNotFoundException.builder().build());

		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();
		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.CREATE_ACCOUNT;
		List<BankDto.ResAccountDto> resAccount = null;

		for( String bank : CommonConstant.LISTBANK){
			accountMap1 = new HashMap<>();
			accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);
			accountMap1.put("businessType",	CommonConstant.BUSINESSTYPE);
			accountMap1.put("clientType",  	"B");
			accountMap1.put("organization",	bank);
			accountMap1.put("loginType",  	"0");
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

		String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();
		String connectedId;

		if(code.equals("CF-00000") || code.equals("CF-04012")) {
			connectedId = (((JSONObject) jsonParse.parse(strResultData)).get("connectedId")).toString();

			repoConnectedMng.save(ConnectedMng.builder()
					.connectedId(connectedId)
					.idxUser(idxUser)
					.name(dto.getName())
					.startDate(dto.getStartDate())
					.endDate(dto.getEndDate())
					.desc1(dto.getDesc1())
					.desc2(dto.getDesc2())
					.corp(user.corp())
					.issuer(dto.getIssuer())
					.serialNumber(dto.getSerial())
					.status(ConnectedMngStatus.NORMAL)
					.build()
			);

			if(getScrapingAccount(idxUser)){
				resAccount = repoResAccount.findResAccount(idxUser).stream()
						.map(account -> BankDto.ResAccountDto.from(account, true))
						.collect(Collectors.toList());
			}

			// ?????? ??????
			ProcAddConnectedId(jsonObject, connectedId, user.corp().idx());
		}else {
			throw new NotFoundException(ResponseCode.findByCode(code).getScrapingMessageGroup().getMessage());
		}

		return resAccount;
	}


	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity registerAccountReferenceAdd(Common.Account dto, Long idxUser, String connectedId,
													  String sourceOrganization,String targetBusiness,
													  String targetOrganization,String sourceBusiness
	) {

		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();
		HashMap<String, Object> bodyMap = new HashMap<>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> accountMap1;
		String createUrlPath = urlPath + CommonConstant.REFERENCE_ADD_ACCOUNT;
		List<BankDto.ResAccountDto> resAccount = null;

		// ????????? ??????
		accountMap1 = new HashMap<>();
		accountMap1.put("countryCode", CommonConstant.COUNTRYCODE);  // ????????????
		accountMap1.put("businessType", targetBusiness);  // ??????????????????
		accountMap1.put("organization", targetOrganization);// ????????????
		accountMap1.put("clientType", "B");   // ????????????(P: ??????, B: ??????)
		accountMap1.put("birthDate", "");
		accountMap1.put("identity", "");
		accountMap1.put("userName", "");
		accountMap1.put("loginTypeLevel", "");
		accountMap1.put("clientTypeLevel", "");
		accountMap1.put("withdrawAccountNo", "");
		accountMap1.put("withdrawAccountPassword", "");
		list.add(accountMap1);

		// ?????? ??????
		bodyMap.put("connectedId", connectedId);
		bodyMap.put("countryCode", CommonConstant.COUNTRYCODE);  // ????????????
		bodyMap.put("businessType", sourceBusiness);  // ??????????????????
		bodyMap.put("organization", sourceOrganization);// ????????????
		bodyMap.put("clientType", "B");   // ????????????(P: ??????, B: ??????)
		bodyMap.put("accountList", list);

		String strObject = ApiRequest.request(createUrlPath, bodyMap);

		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParse.parse(strObject);

		String strResultCode = jsonObject.get("result").toString();
		String strResultData = jsonObject.get("data").toString();

		String code = (((JSONObject) jsonParse.parse(strResultCode)).get("code")).toString();
		normal.setKey(code);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(normal)
				.data(resAccount).build());
	}
}
