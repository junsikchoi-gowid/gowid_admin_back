package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto.AccountNt;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.api.exception.CorpAlreadyExistException;
import com.nomadconnection.dapp.api.exception.CorpNotMatchedException;
import com.nomadconnection.dapp.api.exception.survey.CorpNotBusinessException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.CardIssuanceInfoService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.service.notification.SlackNotiService;
import com.nomadconnection.dapp.api.v2.dto.ScrapingResponse;
import com.nomadconnection.dapp.api.v2.enums.ScrapingType;
import com.nomadconnection.dapp.api.v2.utils.FullTextJsonParser;
import com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.CorpStatus;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchListRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResConCorpListRepository;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.res.ResBatchList;
import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto.ScrapingNotiReq.getScrapingSlackMessage;
import static com.nomadconnection.dapp.api.util.CommonUtil.replaceHyphen;
import static com.nomadconnection.dapp.api.v2.enums.CorpRegistration.InquiryType.ENROLL_NO;
import static com.nomadconnection.dapp.api.v2.enums.CorpRegistration.InquiryType.REGISTERED_NO;
import static com.nomadconnection.dapp.api.v2.enums.CorpRegistration.Issue.MULTIPLE_RESULT;
import static com.nomadconnection.dapp.api.v2.enums.CorpRegistration.Issue.SUCCESS;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.ifNotAvailableCorpRegistrationScrapingTime;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isScrapingSuccess;

@Slf4j
@Service("ScrapingV2Service")
@RequiredArgsConstructor
public class ScrapingService {

	private final ConnectedMngRepository repoConnectedMng;
	private final CorpRepository repoCorp;
	private final ResConCorpListRepository repoResConCorpList;
	private final CommonCodeDetailRepository commonCodeDetailRepository;
	private final ResBatchListRepository repoResBatchList;

	private final UserService userService;
	private final ImageService imageService;
	private final FullTextService fullTextService;
	private final SlackNotiService slackNotiService;
	private final ScrapingResultService scrapingResultService;
	private final CodefApiService codefApiService;
	private final CardIssuanceInfoService cardIssuanceInfoService;

	private final String URL_PATH = CommonConstant.getRequestDomain();

	@Transactional(rollbackFor = Exception.class)
	public void scrap(Long userIdx, AccountNt dto) throws Exception {
		User user = userService.getUser(userIdx);
		createAccount(user, dto); // 국세청 - 공인인증서 증명(계정 등록(커넥티드아이디 발급))
		addAccount(user, dto); // codef - 은행계좌 등록
		scrapCorpLicense(user, dto.getCardType());   // 국세청 - 사업자등록증
		scrapCorpRegistration(user, dto.getCardType()); // 대법원 - 등기부등본
	}

	private void createAccount(User user, AccountNt dto) throws Exception {
		HashMap<String, Object> body = new HashMap<>();
		List<HashMap<String, Object>> accounts = new ArrayList<>();
		HashMap<String, Object> account;
		String createAccountUrlPath = URL_PATH + CommonConstant.CREATE_ACCOUNT;

		account = new HashMap<>();
		account.put("countryCode", CommonConstant.COUNTRYCODE);    // 국가코드
		account.put("businessType", CommonConstant.REVENUETYPE);    // 공공 국세청 업무구분
		account.put("clientType", CommonConstant.CLIENTTYPE_A);    // 통합 고객구분 A
		account.put("organization", CommonConstant.ORGANIZATION_REVENUE);    // 국세청 기관코드
		account.put("loginType", CommonConstant.LOGINTYPE_CERT);    // 로그인타입 (0: 인증서, 1: ID/PW)
		account.put("password", RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
		account.put("certType", CommonConstant.CERTTYPE);
		account.put("certFile", dto.getCertFile());
		accounts.add(account);

		body.put("accountList", accounts);

		String response = codefApiService.requestToCodef(createAccountUrlPath, body, user.email());

		log.info("[createAccount] $user={}, $response={}", user.email(), response);

		ScrapingResponse scrapingResponse = scrapingResultService.getApiResult(response);
		String code = scrapingResponse.getCode();
		String message = scrapingResponse.getMessage();
		String connectedId = scrapingResponse.getConnectedId();

		if (code.equals(ResponseCode.CF00000.getCode()) || code.equals(ResponseCode.CF04012.getCode())) {
			repoConnectedMng.save(ConnectedMng.builder()
				.connectedId(connectedId)
				.idxUser(user.idx())
				.name(dto.getName())
				.startDate(dto.getStartDate())
				.endDate(dto.getEndDate())
				.desc1(dto.getDesc1())
				.desc2(dto.getDesc2())
				.issuer(dto.getIssuer())
				.serialNumber(dto.getSerial())
				.type(CommonConstant.REVENUETYPE)
				.status(ConnectedMngStatus.NORMAL)
				.build()
			);

			JSONArray successList = (JSONArray) scrapingResponse.getScrapingResponse()[1].get("successList");
			saveConnectedId(successList, connectedId);
		} else {
			ApiResponse.ApiResult result = scrapingResultService.getCodeAndMessage(scrapingResponse);
			log.error("[createAccount] $user={}, $code={}, $message={} ", user.email(), code, message);
			slackNotiService.sendSlackNotification(getScrapingSlackMessage(user, result, ScrapingType.CREATE_ACCOUNT),
				(CardType.GOWID.equals(dto.getCardType()) ? slackNotiService.getSlackProgressUrl() : slackNotiService.getSlackKisedUrl()));
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

		for (String bank : CommonConstant.LISTBANK) {
			account = new HashMap<>();
			account.put("countryCode", CommonConstant.COUNTRYCODE);  // 국가코드
			account.put("businessType", CommonConstant.BUSINESSTYPE);  // 업무구분코드
			account.put("clientType", CommonConstant.CLIENTTYPE_B);   // 고객구분(P: 개인, B: 기업)
			account.put("organization", bank);// 기관코드
			account.put("loginType", CommonConstant.LOGINTYPE_CERT);   // 로그인타입 (0: 인증서, 1: ID/PW)
			accounts.add(account);
		}

		for (String card : CommonConstant.LISTCARD) {
			account = new HashMap<>();
			account.put("countryCode", CommonConstant.COUNTRYCODE);  // 국가코드
			account.put("businessType", CommonConstant.CARDTYPE);  // 업무구분코드
			account.put("clientType", CommonConstant.CLIENTTYPE_B);   // 고객구분(P: 개인, B: 기업)
			account.put("organization", card);// 기관코드
			account.put("loginType", CommonConstant.LOGINTYPE_CERT);   // 로그인타입 (0: 인증서, 1: ID/PW)
			account.put("certType", CommonConstant.CERTTYPE);
			accounts.add(account);
		}

		account = new HashMap<>();
		account.put("countryCode", CommonConstant.COUNTRYCODE);  // 국가코드
		account.put("businessType", CommonConstant.REVENUETYPE);  // 업무구분코드
		account.put("clientType", CommonConstant.CLIENTTYPE_A);   // "고객구분(P: 개인, B: 기업)
		account.put("organization", CommonConstant.ORGANIZATION_REVENUE);// 기관코드
		account.put("loginType", CommonConstant.LOGINTYPE_CERT);   // 로그인타입 (0: 인증서, 1: ID/PW)
		accounts.add(account);

		body.put("accountList", accounts);
		body.put(CommonConstant.CONNECTED_ID, connectedId);

		String deleteAccountResult = codefApiService.requestToCodef(deleteAccountUrlPath, body, user);

		ScrapingResponse scrapingResponse = scrapingResultService.getApiResult(deleteAccountResult);
		String code = scrapingResponse.getCode();
		String message = scrapingResponse.getMessage();

		if (isScrapingSuccess(code)) {
			return ResponseEntity.ok().body(BusinessResponse.builder().normal(normal).build());
		} else {
			log.error("[deleteAccount] $user={}, $code={}, $message={} ", code, message);
			throw new CodefApiException(ResponseCode.findByCode(code));
		}
	}

	private void addAccount(User user, AccountNt dto) {
		String connectedId = scrapingResultService.getResponseDto().getConnectedId();
		JSONObject response = codefApiService.requestAddAccount(dto, connectedId, user.email());

		log.info("[addAccount] $user={} $response={}", user.email(), response.toString());

		ScrapingResponse scrapingResponse = scrapingResultService.getApiResult(response);
		String code = scrapingResponse.getCode();
		String message = scrapingResponse.getMessage();
		connectedId = scrapingResponse.getConnectedId();

		JSONArray successList = (JSONArray) scrapingResponse.getScrapingResponse()[1].get("successList");

		if (code.equals(ResponseCode.CF00000.getCode()) || code.equals(ResponseCode.CF04012.getCode())) {
			saveConnectedId(successList, connectedId);
		} else {
			if (connectedId != null) {
				repoResBatchList.save(ResBatchList.builder()
					.connectedId(connectedId)
					.errCode(code)
					.errMessage(message)
					.idxUser(user.idx())
					.build());
			}
			log.error("[addAccount] $user={}, $code={}, $message={} ", user.email(), code, message);
		}
	}

	public void scrapCorpLicense(User user, CardType cardType) throws Exception {
		try {
			String connectedId = scrapingResultService.getResponseDto().getConnectedId();
			String response = codefApiService.requestScrapCorpLicense(connectedId, user.email());
			ScrapingResponse scrapingResponse = scrapingResultService.getApiResult(response);
			String code = scrapingResponse.getCode();
			String message = scrapingResponse.getMessage();
			connectedId = scrapingResponse.getConnectedId();

			if (isScrapingSuccess(code)) {
				JSONObject jsonData = scrapingResponse.getScrapingResponse()[1];
				String resCompanyIdentityNo = GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo");
				Corp corp;

				if (CardType.KISED.equals(cardType)) {
					if (!ScrapingCommonUtils.isKisedCorp(resCompanyIdentityNo)) {
						throw new CodefApiException(ResponseCode.KISED);
					}
				}

				if (user.corp() != null) {
					if (!resCompanyIdentityNo.equals(user.corp().resCompanyIdentityNo())) {
						log.info("[scrapCorpLicense] $user={}, $present={}, $request={}, $message=resCompanyIdentityNo is not matched",
							user.email(), user.corp().resCompanyIdentityNo(), resCompanyIdentityNo);
						throw new CorpNotMatchedException(ErrorCode.Api.CORP_NOT_MATCHED);
					}
				}

				if (repoCorp.findByResCompanyIdentityNo(resCompanyIdentityNo).isPresent()) {
					corp = repoCorp.findByResCompanyIdentityNo(resCompanyIdentityNo).get();
					// Todo : 유저:법인:발급상태 - N:1:N 으로 변경시 해당 로직 변경 필요
					if (corp.user().idx() != user.idx()) {
						log.info("[scrapCorpLicense] $user={}, $cardType={}, $message=Already exist Corp, User is not matched", user.email(), cardType);
						throw new CorpAlreadyExistException(ErrorCode.Api.ALREADY_EXIST);
					}
				} else if (!ScrapingCommonUtils.isCorporateBusiness(jsonData)) {
					log.info("[scrapCorpLicense] $user={}, $message=Corp is not Business", user.email());
					throw new CorpNotBusinessException(ErrorCode.Api.CORP_NOT_BUSINESS);
				} else {
					corp = repoCorp.findByResCompanyIdentityNo(GowidUtils.getEmptyStringToString(jsonData, "resCompanyIdentityNo")).orElseGet(
						() -> Corp.builder()
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
					repoCorp.save(corp);
				}

				user.corp(corp);
				userService.saveUser(user);

				String licenseNo = corp.resCompanyIdentityNo();
				imageService.sendCorpLicenseImage(user.cardCompany(), response, licenseNo);
			} else {
				if (connectedId != null) {
					repoResBatchList.save(ResBatchList.builder()
						.connectedId(connectedId)
						.errCode(code)
						.errMessage(message)
						.idxUser(user.idx())
						.build());
				}
				log.error("[scrapCorpLicense] $user={}, $code={}, $message={} ", user.email(), code, message);
				slackNotiService.sendSlackNotification(getScrapingSlackMessage(user, scrapingResultService.getCodeAndMessage(scrapingResponse), ScrapingType.CORP_LICENSE),
					(CardType.GOWID.equals(cardType) ? slackNotiService.getSlackProgressUrl() : slackNotiService.getSlackKisedUrl()));
				if (code.equals(ResponseCode.CF12100.getCode()) && scrapingResponse.getExtraMessage().contains("개인납세자")) {
					throw new CodefApiException(ResponseCode.INDIVIDUAL);
				}
				throw new CodefApiException(ResponseCode.findByCode(code));
			}
		} catch (Exception e) {
			log.error("scrapCorpLicense {}", e);
			throw e;
		}
	}

	public void scrapCorpRegistration(User user, CardType cardType) throws Exception {
		try {
			Corp corp = user.corp();
			String email = user.email();
			String licenseNo = corp.resCompanyIdentityNo();
			String registrationNumber = replaceHyphen(OptionalUtil.getOrEmptyString(corp.resUserIdentiyNo()));

			String response = codefApiService.requestCorpRegistrationScraping(ENROLL_NO.getCode(), registrationNumber, "", "", email); // 등록번호 스크래핑
			ScrapingResponse scrapingResponse = scrapingResultService.getApiResult(response);

			if (isScrapingSuccess(scrapingResponse.getCode())) {
				response = retryScrapingWhenMultipleResult(scrapingResponse, user, response);
				scrapingResponse = scrapingResultService.getApiResult(response);
				cardIssuanceInfoService.updateCorpByUser(user, corp, cardType);

				if (isFinalSuccess(user, scrapingResponse)) {
					if (ScrapingCommonUtils.isLimitedCompany(scrapingResponse.getScrapingResponse()[1])) {
						throw new CodefApiException(ResponseCode.findByCode("LIMITED"));
					}
					fullTextService.saveAfterCorpRegistration(scrapingResponse.getScrapingResponse()[1], corp, cardType);
					if (!ScrapingCommonUtils.isNonProfitCorp(licenseNo)) {
						fullTextService.save1530(scrapingResponse.getScrapingResponse()[1], corp);
						imageService.sendCorpRegistrationImage(user.cardCompany(), response, licenseNo);
					}
				}
			} else {
				saveResBatchListAndPrintErrorLog(user, scrapingResponse, "");
				slackNotiService.sendSlackNotification(getScrapingSlackMessage(user, scrapingResultService.getCodeAndMessage(scrapingResponse), ScrapingType.CORP_REGISTRATION), slackNotiService.getSlackProgressUrl());
				ifNotAvailableCorpRegistrationScrapingTime(scrapingResponse.getCode(), scrapingResponse.getExtraMessage());
				throw new CodefApiException(ResponseCode.findByCode(scrapingResponse.getCode()));
			}
		}  catch (Exception e){
			log.error("scrapCorpRegistration {}", e);
			throw e;
		}
	}

		private String retryScrapingWhenMultipleResult (ScrapingResponse scrapingResponse, User user, String response) throws
		Exception {
			JSONObject jsonDataCorpRegister = scrapingResponse.getScrapingResponse()[1];
			String email = user.email();
			String code = scrapingResponse.getCode();
			String resIssueYn = FullTextJsonParser.getResIssueYn(scrapingResponse.getScrapingResponse());

			if (MULTIPLE_RESULT.getCode().equals(resIssueYn)) {
				response = scrapCorpRegistrationByRegisteredNo(jsonDataCorpRegister, email); // 등가번호 스크래핑
			} else if (!SUCCESS.getCode().equals(resIssueYn)) {
				saveResBatchListAndPrintErrorLog(user, scrapingResponse, resIssueYn);
				throw new CodefApiException(ResponseCode.findByCode(code));
			}

			return response;
		}

		private boolean isFinalSuccess (User user, ScrapingResponse scrapingResponse){
			String resIssueYn = FullTextJsonParser.getResIssueYn(scrapingResponse.getScrapingResponse());

			if (isScrapingSuccess(scrapingResponse.getCode())) {
				if (!SUCCESS.getCode().equals(resIssueYn)) {
					saveResBatchListAndPrintErrorLog(user, scrapingResponse, resIssueYn);
					throw new CodefApiException(ResponseCode.findByCode(scrapingResponse.getCode()));
				}
				return true;
			}
			return false;
		}

		private String scrapCorpRegistrationByRegisteredNo (JSONObject jsonDataCorpRegister, String email) throws
		Exception {
			JSONArray jsonDataArraySearchList = (JSONArray) jsonDataCorpRegister.get("resSearchList");
			String registrationNumber = "";
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
			return codefApiService.requestCorpRegistrationScraping(REGISTERED_NO.getCode(), registrationNumber, commCompetentRegistryOffice.code1(), commBranchType.code1(), email);
		}

		private void saveResBatchList (User user, ScrapingResponse scrapingResponse){
			if (scrapingResponse.getConnectedId() != null) {
				repoResBatchList.save(ResBatchList.builder()
					.connectedId(scrapingResponse.getConnectedId())
					.transactionId(scrapingResponse.getTransactionId())
					.errCode(scrapingResponse.getCode())
					.errMessage(scrapingResponse.getMessage())
					.idxUser(user.idx())
					.build());
			}
		}

		private void saveConnectedId (JSONArray successList, String connectedId){
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
						.status(ConnectedMngStatus.NORMAL)
						.build()
				);
			});
		}

		private void saveResBatchListAndPrintErrorLog (User user, ScrapingResponse scrapingResponse, String resIssueYn){
			saveResBatchList(user, scrapingResponse);
			log.error("[scrapCorpRegistration] $user={}, $code={}, $message={}, $resIssueYn={} $transactionId={} "
				, user.email(), scrapingResponse.getCode(), scrapingResponse.getMessage(), resIssueYn, scrapingResponse.getTransactionId());
		}


}
