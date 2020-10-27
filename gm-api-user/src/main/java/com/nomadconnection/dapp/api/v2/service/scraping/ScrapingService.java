package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto.AccountNt;
import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.api.exception.CorpAlreadyExistException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.codef.io.api.ApiCodef;
import com.nomadconnection.dapp.codef.io.dto.Common;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.codef.io.sandbox.pb.CORP_REGISTER;
import com.nomadconnection.dapp.codef.io.sandbox.pb.PROOF_ISSUE;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.CorpStatus;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchListRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResConCorpListRepository;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.res.ResBatchList;
import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.shinhan.D1510;
import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.nomadconnection.dapp.api.util.CommonUtil.replaceHyphen;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isScrapingSuccess;

@Slf4j
@Service("ScrapingV2Service")
@RequiredArgsConstructor
public class ScrapingService {

	private final ConnectedMngRepository repoConnectedMng;
	private final CorpRepository repoCorp;
	private final ResConCorpListRepository repoResConCorpList;
	private final CommonCodeDetailRepository commonCodeDetailRepository;
    private final CardIssuanceInfoRepository cardIssuanceInfoRepository;
    private final ResBatchListRepository repoResBatchList;

    private final UserService userService;
	private final ScrapingResultService scrapingResultService;
	private final ImageService imageService;
    private final FullTextService fullTextService;

    private final String URL_PATH = CommonConstant.getRequestDomain();

	@Transactional(rollbackFor = Exception.class)
    public void scrap(Long userIdx, AccountNt dto) throws Exception {
    	User user = userService.getUser(userIdx);
	    createAccount(user, dto); // 국세청 - 공인인증서 증명(계정 등록(커넥티드아이디 발급))
		addAccount(user, dto); // codef - 은행계좌 등록
		scrapCorpLicense(user);   // 국세청 - 사업자등록증
		scrapCorpRegistration(user); // 대법원 - 등기부등본
	}

	private void createAccount(User user, AccountNt dto) throws Exception{
		HashMap<String, Object> body = new HashMap<>();
		List<HashMap<String, Object>> accounts = new ArrayList<>();
		HashMap<String, Object> account;
		String createAccountUrlPath = URL_PATH + CommonConstant.CREATE_ACCOUNT;

		account = new HashMap<>();
		account.put("countryCode",	CommonConstant.COUNTRYCODE);  	// 국가코드
		account.put("businessType",	CommonConstant.REVENUETYPE);  	// 공공 국세청 업무구분
		account.put("clientType",  	CommonConstant.CLIENTTYPE_A);   	// 통합 고객구분 A
		account.put("organization",	CommonConstant.ORGANIZATION_REVENUE);	// 국세청 기관코드
		account.put("loginType",  	CommonConstant.LOGINTYPE_CERT);   	// 로그인타입 (0: 인증서, 1: ID/PW)
		account.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
		account.put("certType",     CommonConstant.CERTTYPE);
		account.put("certFile",     dto.getCertFile());
		accounts.add(account);

		body.put("accountList", accounts);

		String response = requestToCodef(createAccountUrlPath, body, user.email());

		log.info("[createAccount] $user={}, $response={}", user.email(), response);

		JSONObject[] responseCreateAccount = scrapingResultService.getApiResult(response);
		String code = scrapingResultService.getCode();
		String message = scrapingResultService.getMessage();
		String connectedId = scrapingResultService.getConnectedId();

		if(code.equals(ResponseCode.CF00000.getCode()) || code.equals(ResponseCode.CF04012.getCode())) {
			repoConnectedMng.save(ConnectedMng.builder()
					.connectedId(connectedId)
					.idxUser(user.idx())
					.name(dto.getName())
					.startDate(dto.getStartDate())
					.endDate(dto.getEndDate())
					.desc1(dto.getDesc1())
					.desc2(dto.getDesc2())
					.type(CommonConstant.REVENUETYPE)
					.build()
			);

			JSONArray successList = (JSONArray) responseCreateAccount[1].get("successList");
			saveConnectedId(successList, connectedId);
		}else {
			log.error("[createAccount] $user={}, $code={}, $message={} ", user.email(), code, message);
			throw new CodefApiException(ResponseCode.findByCode(code));
		}
	}

	@Deprecated
	public ResponseEntity deleteAccount(String connectedId, String user) throws Exception {
		HashMap<String, Object> body = new HashMap<>();
		List<HashMap<String, Object>> accounts = new ArrayList<>();
		HashMap<String, Object> account;
		String deleteAccountUrlPath = URL_PATH.concat(CommonConstant.DELETE_ACCOUNT);
		BusinessResponse.Normal normal = BusinessResponse.Normal.builder().build();

		for(String bank : CommonConstant.LISTBANK){
			account = new HashMap<>();
			account.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			account.put("businessType",	CommonConstant.BUSINESSTYPE);  // 업무구분코드
			account.put("clientType",  	CommonConstant.CLIENTTYPE_B);   // 고객구분(P: 개인, B: 기업)
			account.put("organization",	bank);// 기관코드
			account.put("loginType",  	CommonConstant.LOGINTYPE_CERT);   // 로그인타입 (0: 인증서, 1: ID/PW)
			accounts.add(account);
		}

		for(String card : CommonConstant.LISTCARD){
			account = new HashMap<>();
			account.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
			account.put("businessType",	CommonConstant.CARDTYPE);  // 업무구분코드
			account.put("clientType",  	CommonConstant.CLIENTTYPE_B);   // 고객구분(P: 개인, B: 기업)
			account.put("organization",	card);// 기관코드
			account.put("loginType",  	CommonConstant.LOGINTYPE_CERT);   // 로그인타입 (0: 인증서, 1: ID/PW)
			account.put("certType",     CommonConstant.CERTTYPE);
			accounts.add(account);
		}

		account = new HashMap<>();
		account.put("countryCode",	CommonConstant.COUNTRYCODE);  // 국가코드
		account.put("businessType",	CommonConstant.REVENUETYPE);  // 업무구분코드
		account.put("clientType",  	CommonConstant.CLIENTTYPE_A);   // "고객구분(P: 개인, B: 기업)
		account.put("organization",	CommonConstant.ORGANIZATION_REVENUE);// 기관코드
		account.put("loginType",  	CommonConstant.LOGINTYPE_CERT);   // 로그인타입 (0: 인증서, 1: ID/PW)
		accounts.add(account);

		body.put("accountList", accounts);
		body.put(CommonConstant.CONNECTED_ID, connectedId);

		String deleteAccountResult = requestToCodef(deleteAccountUrlPath, body, user);

		scrapingResultService.getApiResult(deleteAccountResult);
		String code = scrapingResultService.getCode();
		String message = scrapingResultService.getMessage();

		if (isScrapingSuccess(code)) {
			return ResponseEntity.ok().body(BusinessResponse.builder().normal(normal).build());
		} else {
			log.error("[deleteAccount] $user={}, $code={}, $message={} ", code, message);
			throw new CodefApiException(ResponseCode.findByCode(code));
		}
	}

	private void addAccount(User user, AccountNt dto) throws Exception{
		JSONObject response = requestAddAccount(dto, user.email());

        log.info("[addAccount] $user={} $response={}", user.email(), response.toString());

		JSONObject[] jsonObjectsCreateAccount = scrapingResultService.getApiResult(response);
		String code = scrapingResultService.getCode();
		String message = scrapingResultService.getMessage();
		String connectedId = scrapingResultService.getConnectedId();

		JSONArray successList = (JSONArray) jsonObjectsCreateAccount[1].get("successList");

		if(code.equals(ResponseCode.CF00000.getCode()) || code.equals(ResponseCode.CF04012.getCode())) {
			saveConnectedId(successList, connectedId);
		} else {
			if(connectedId != null) {
				repoResBatchList.save(ResBatchList.builder()
						.connectedId(connectedId)
						.errCode(code)
						.errMessage(message)
						.idxUser(user.idx())
						.build());
			}
			log.error("[addAccount] $user={}, $code={}, $message={} ", user.email(), code, message);
			throw new CodefApiException(ResponseCode.findByCode(code));
		}
	}

	private void scrapCorpLicense(User user) throws Exception{
		String response = requestScrapCorpLicense(user.email());

        log.info("[scrapCorpLicense] $user={}, $response={}", user.email(), response);

		JSONObject[] jsonObjectsCorpLicense = scrapingResultService.getApiResult(response);
		String code = scrapingResultService.getCode();
		String message = scrapingResultService.getMessage();
		String connectedId = scrapingResultService.getConnectedId();

		if (isScrapingSuccess(code)) {
			JSONObject jsonData = jsonObjectsCorpLicense[1];

			// todo 이미 가입된 회사의 경우 처리 필요
			//	중복체크 테스트 후엔 적용
			Corp corp;

			if (repoCorp.findByResCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo")).isPresent()) {
				corp = repoCorp.findByResCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo")).get();
				if (corp.user().idx() != user.idx()) {
					log.info("[scrapCorpLicense] $user={}, $message=Already exist Corp", user.email());
					throw new CorpAlreadyExistException(ErrorCode.Api.ALREADY_EXIST);
				}
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
			userService.saveUser(user);

			String licenseNo = corp.resCompanyIdentityNo();
			imageService.sendCorpLicenseImage(user.cardCompany(), response, licenseNo);
		} else {
			if(connectedId != null) {
				repoResBatchList.save(ResBatchList.builder()
						.connectedId(connectedId)
						.errCode(code)
						.errMessage(message)
						.idxUser(user.idx())
						.build());
			}
			log.error("[scrapCorpLicense] $user={}, $code={}, $message={} ", user.email(), code, message);
			throw new CodefApiException(ResponseCode.findByCode(code));
		}
	}

	private void scrapCorpRegistration(User user) throws Exception {
		Corp corp = user.corp();
		String licenseNo = corp.resCompanyIdentityNo();
		String registrationNumber = replaceHyphen(Optional.ofNullable(corp.resUserIdentiyNo()).orElse(""));

		String response = requestCorpRegistrationScraping("2", registrationNumber,"", "", user.email());
		log.info("[scrapCorpRegistration] $user={}, $response={} ", user.email(), response);

		JSONObject[] corpRegisterJson = scrapingResultService.getApiResult(response);
		String code = scrapingResultService.getCode();
		String message = scrapingResultService.getMessage();
		String connectedId = scrapingResultService.getConnectedId();
		String transactionId = scrapingResultService.getTransactionId();

		if (isScrapingSuccess(code)) {
			JSONObject jsonDataCorpRegister = corpRegisterJson[1];
			String resIssueYn = jsonDataCorpRegister.get("resIssueYN").toString();

			if (resIssueYn.equals("0")) {

				JSONArray jsonDataArraySearchList = (JSONArray) jsonDataCorpRegister.get("resSearchList");

				registrationNumber = "";
				CommonCodeDetail commCompetentRegistryOffice = new CommonCodeDetail();
				CommonCodeDetail commBranchType = new CommonCodeDetail();

				for (Object o : jsonDataArraySearchList) {
					JSONObject obj = (JSONObject) o;
					if (obj.get("commRegistryStatus").equals("살아있는 등기") && obj.get("commBranchType").equals("본점")) {    //TODO : fix
						registrationNumber = obj.get("resRegistrationNumber").toString().trim();
						commCompetentRegistryOffice = commonCodeDetailRepository.findFirstByCodeAndValue1(CommonCodeType.REG_OFFICE, obj.get("commCompetentRegistryOffice").toString());
						commBranchType = commonCodeDetailRepository.findFirstByCodeAndValue1(CommonCodeType.REG_OFFICE_TYPE, obj.get("commCompanyType").toString());
						break;
					}
				}

				// 위에서 하는 동작과의 차이점 구분 필요
				response = requestCorpRegistrationScraping("1", registrationNumber, commCompetentRegistryOffice.code1(), commBranchType.code1(), user.email());
				log.info("[scrapCorpRegistration] $user={}, $response={} ", user.email(), response);

			} else if (!resIssueYn.equals("1")) {
				if(connectedId != null) {
					repoResBatchList.save(ResBatchList.builder()
							.connectedId(connectedId)
							.transactionId(transactionId)
							.errCode(code)
							.errMessage(message)
							.idxUser(user.idx())
							.build());
				}
				log.error("[scrapCorpRegistration] $user={}, $resIssueYn={} $transactionId={} ", user.email(), resIssueYn, transactionId);
				throw new CodefApiException(ResponseCode.findByCode(code));
			}
		} else {
			if(connectedId != null) {
				repoResBatchList.save(ResBatchList.builder()
						.connectedId(connectedId)
						.transactionId(transactionId)
						.errCode(code)
						.errMessage(message)
						.idxUser(user.idx())
						.build());
			}
			log.error("[scrapCorpRegistration] $user={}, $code={}, $message={} ", user.email(), code, message);
			throw new CodefApiException(ResponseCode.findByCode(code));
		}

		corpRegisterJson = scrapingResultService.getApiResult(response);
		code = scrapingResultService.getCode();
		message = scrapingResultService.getMessage();
		transactionId = scrapingResultService.getTransactionId();

		if (isScrapingSuccess(code)) {
			JSONObject jsonDataCorpRegister = corpRegisterJson[1];

			if (!jsonDataCorpRegister.get("resIssueYN").toString().equals("1")) {
				if(connectedId != null) {
					repoResBatchList.save(ResBatchList.builder()
							.connectedId(connectedId)
							.errCode(code)
							.errMessage(message)
							.idxUser(user.idx())
							.build());
				}
				log.error("[scrapCorpRegistration] $user={}, $resIssueYn={} $transactionId={} ", user.email(),
						jsonDataCorpRegister.get("resIssueYN").toString(), transactionId);
				throw new CodefApiException(ResponseCode.findByCode(code));
			}

			JSONArray resRegisterEntriesList = (JSONArray) jsonDataCorpRegister.get("resRegisterEntriesList");
			JSONObject resRegisterEntry = (JSONObject) resRegisterEntriesList.get(0);

			JSONArray jsonArrayResCEOList = (JSONArray) resRegisterEntry.get("resCEOList");

			corp.resUserType(fullTextService.getCeoType(jsonArrayResCEOList));
			corp = fullTextService.setCeoCount(corp, jsonArrayResCEOList);

			imageService.sendCorpRegistrationImage(user.cardCompany(), response, licenseNo);

			D1000 d1000 = fullTextService.build1000(corp, jsonArrayResCEOList);
			fullTextService.save1000(d1000);

			D1510 d1510 = fullTextService.build1510(corp);
			fullTextService.save1510(d1510);

			D1530 d1530 = fullTextService.build1530(corp, resRegisterEntriesList);
			fullTextService.save1530(d1530);

			D1400 d1400 = fullTextService.build1400(corp, jsonArrayResCEOList);
			fullTextService.save1400(d1400);

			CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.getTopByUserAndDisabledFalseOrderByIdxDesc(user);
			if (!ObjectUtils.isEmpty(cardIssuanceInfo)) {
				cardIssuanceInfoRepository.save(cardIssuanceInfo.corp(corp));
			}
		} else {
			if(connectedId != null) {
				repoResBatchList.save(ResBatchList.builder()
						.connectedId(connectedId)
						.errCode(code)
						.errMessage(message)
						.idxUser(user.idx())
						.build());
			}
			log.error("[scrapCorpRegistration] $user={}, $code={}, $message={} ", user.email(), code, message);
			throw new CodefApiException(ResponseCode.findByCode(code));
		}
	}

	private String requestCorpRegistrationScraping(String inquiryType, String registrationNumber,
												  String competentRegistryOffice, String companyType, String user) throws Exception {
		try{
			return CORP_REGISTER.corp_register(
					"0002",
					"0261057000",
					RSAUtil.encryptRSA("6821", CommonConstant.PUBLIC_KEY),
					inquiryType,    // 2
					registrationNumber,
					"1",
					"T34029396293",
					"gowid99!",
					competentRegistryOffice,
					companyType,
					"",
					"",
					"0",
					"",
					"",
					"",
					"N"
			);
		} catch (RestClientResponseException e) {
			log.error("[requestCorpRegistrationScraping] $user={}, $error={}", user, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}

    private void saveConnectedId(JSONArray successList, String connectedId) {
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
                            .build()
            );
        });
    }

    private String requestToCodef(String url, HashMap<String, Object> body, String user) throws Exception {
		try {
			return ApiRequest.request(url, body);
		} catch (RestClientResponseException e) {
			log.error("[requestToCodef] $url={}, $user={}, $error={}", url, user, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}

	private JSONObject requestAddAccount(AccountNt dto, String user) throws Exception {
		try {
			return ApiCodef.registerCodef(
					Common.Account.builder()
							.certFile(dto.getCertFile())
							.password1(dto.getPassword1())
							.build()
					, scrapingResultService.getConnectedId(), CommonConstant.API_DOMAIN, CommonConstant.ADD_ACCOUNT, null, CommonConstant.BUSINESSTYPE);
		} catch (RestClientResponseException e) {
			log.error("[requestAddAccount] $user={}, $error={}", user, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}

	private String requestScrapCorpLicense(String user) throws Exception {
		try{
			return PROOF_ISSUE.proof_issue(
					"0001",
					scrapingResultService.getConnectedId(),
					"04",
					"01",
					"1",
					"0",
					"",
					"" // 사업자번호
			);
		} catch (RestClientResponseException e) {
			log.error("[requestScrapCorpLicense] $user={}, $error={}", user, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}
}
