package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto.AccountNt;
import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.codef.io.api.ApiCodef;
import com.nomadconnection.dapp.codef.io.dto.Common;
import com.nomadconnection.dapp.codef.io.helper.ApiRequest;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.RSAUtil;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
import com.nomadconnection.dapp.codef.io.sandbox.pb.CORP_REGISTER;
import com.nomadconnection.dapp.codef.io.sandbox.pb.PROOF_ISSUE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.HashMap;

import static com.nomadconnection.dapp.api.util.CommonUtil.replaceHyphen;
import static com.nomadconnection.dapp.codef.io.sandbox.pb.STANDARD_FINANCIAL.standard_financial;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefApiService {

	public String requestCorpRegistrationScraping(String inquiryType, String registrationNumber,
												  String competentRegistryOffice, String companyType, String user) throws Exception {
		try{
			String response = CORP_REGISTER.corp_register(
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
			log.info("[scrapCorpRegistration] $user={}, $response={} ", user, response);
			return response;
		} catch (RestClientResponseException e) {
			log.error("[requestCorpRegistrationScraping] $user={}, $error={}", user, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}

	public String requestToCodef(String url, HashMap<String, Object> body, String user) throws Exception {
		try {
			return ApiRequest.request(url, body);
		} catch (RestClientResponseException e) {
			log.error("[requestToCodef] $url={}, $user={}, $error={}", url, user, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}

	public JSONObject requestAddAccount(AccountNt dto,String connectedId, String email) throws Exception {
		try {
			return ApiCodef.registerCodef(
					Common.Account.builder()
							.certFile(dto.getCertFile())
							.password1(dto.getPassword1())
							.build()
					, connectedId, CommonConstant.API_DOMAIN, CommonConstant.ADD_ACCOUNT, null, CommonConstant.BUSINESSTYPE);
		} catch (RestClientResponseException e) {
			log.error("[requestAddAccount] $user={}, $error={}", email, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}

	public String requestScrapCorpLicense(String connectedId, String email) throws Exception {
		try{
			return PROOF_ISSUE.proof_issue(
					"0001",
					connectedId,
					"04",
					"01",
					"1",
					"0",
					"",
					"" // 사업자번호
			);
		} catch (RestClientResponseException e) {
			log.error("[requestScrapCorpLicense] $user={}, $error={}", email, e);
			throw new CodefApiException(ResponseCode.REQUEST_ERROR);
		}
	}

	public String requestStandardFinancialScraping(String connectedId, String yyyyMm, String licenseNo) throws InterruptedException, ParseException, IOException {
		String scrapingResult = standard_financial(
			"0001",
			connectedId,
			yyyyMm,
			"0",
			"04",
			"01",
			"40",
			"",
			replaceHyphen(licenseNo).trim()
		);
		log.info( "FinancialStatements result = {} " , scrapingResult);
		return scrapingResult;
	}

}
