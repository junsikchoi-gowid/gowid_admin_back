package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.v2.dto.ScrapingLogDto;
import com.nomadconnection.dapp.api.v2.dto.ScrapingResponse;
import com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.shinhan.D1520;
import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isNotAvailableFinancialStatementsScrapingTime;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isScrapingSuccess;

/**
 * 표준재무제표 스크래핑
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialStatementsService {

	private final UserService userService;
	private final CorpService corpService;
	private final FullTextService fullTextService;
	private final ImageService imageService;
	private final ConnectedMngService connectedMngService;
	private final ScrapingResultService scrapingResultService;
	private final CodefApiService codefApiService;

	@Transactional(rollbackFor = Exception.class)
	public ApiResponse.ApiResult scrapAndSaveFullText(Long userIdx, ConnectedMngDto.CorpInfo dto) throws Exception {
		User user = userService.getUser(userIdx);
		ApiResponse.ApiResult response = scrapFinancialStatements(user, dto); // 국세청 - 표준재무제표
		fullTextService.saveAfterFinancialStatements(user.corp(), dto);

		return response;
	}

	@Transactional(rollbackFor = Exception.class)
	public ApiResponse.ApiResult scrap(User user, String resClosingStandards) throws Exception {
		Corp corp = user.corp();
		ConnectedMngDto.CorpInfo dto = ConnectedMngDto.CorpInfo
			.builder()
			.resBusinessCode(corp.resBusinessCode())
			.resClosingStandards(resClosingStandards)
			.resCompanyEngNm(corp.resCompanyEngNm()).resCompanyPhoneNumber(corp.resCompanyNumber()).build();

		return scrapFinancialStatements(user, dto); // 국세청 - 표준재무제표
	}

	private ApiResponse.ApiResult scrapFinancialStatements(User user, ConnectedMngDto.CorpInfo dto) throws Exception {
		try{
			Corp corp = user.corp();
			Long corpIdx = corp.idx();
			String licenseNo = corp.resCompanyIdentityNo();
			String connectedId = connectedMngService.getConnectedId(user.idx());
			String resClosingStandards = StringUtils.isEmpty(dto.getResClosingStandards()) ? ScrapingCommonUtils.DEFAULT_CLOSING_STANDARDS_MONTH : dto.getResClosingStandards();
			List<String> listYyyyMm = ScrapingCommonUtils.getFindClosingStandards(LocalDate.now(), resClosingStandards);
			ApiResponse.ApiResult response = null;

			// 국세청 - 증명발급 표준재무재표
			for(String yyyyMm : listYyyyMm){
				String standardFinancialResult = codefApiService.requestStandardFinancialScraping(connectedId, yyyyMm, licenseNo);
				ScrapingResponse scrapingResponse = scrapingResultService.getApiResult(standardFinancialResult);
				String code = scrapingResponse.getCode();
				String message = scrapingResponse.getMessage();
				String extraMessage = scrapingResponse.getExtraMessage();
				response = scrapingResultService.getCodeAndMessage(scrapingResponse);
				ScrapingLogDto logDto = ScrapingLogDto.builder().email(user.email()).code(code).message(message).extraMessage(extraMessage).build();

				if(ScrapingCommonUtils.isNewCorp(Integer.parseInt(resClosingStandards), LocalDate.parse(corp.resOpenDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))){
					D1530 d1530 = fullTextService.findFirstByIdxCorpIn1530(corpIdx);
					fullTextService.saveDefault1520(d1530, corp);
					printScrapingErrorLog("It is a new corporation.", logDto);
					break;
				}

				if(isScrapingSuccess(code)) {
					JSONObject scrapingResult = scrapingResponse.getScrapingResponse()[1];
					D1520 d1520 = fullTextService.build1520(corp, scrapingResult);
					fullTextService.save1520(d1520);
					imageService.sendFinancialStatementsImage(user.cardCompany(), yyyyMm, standardFinancialResult, licenseNo);
				} else if(isNotAvailableFinancialStatementsScrapingTime(code)) {
					printScrapingErrorLog("It is not the time allowed for scraping.", logDto);
					break;
				} else {
					printScrapingErrorLog("scraping failed.", logDto);
					break;
				}
			}

			// TODO: 기업주소 입력변경으로 인하여 해당로직 필요여부 논의 필요
			corpService.save(user.corp()
				.resCompanyEngNm(dto.getResCompanyEngNm())
				.resCompanyNumber(dto.getResCompanyPhoneNumber())
				.resBusinessCode(dto.getResBusinessCode())
			);

			// 지급보증 파일생성 및 전송
			imageService.sendGuaranteeImage(corp, user.cardCompany(), licenseNo);
			return response;
		} catch (Exception e){
			log.error("scrapFinancialStatements {} ", e);
			throw e;
		}
	}

	private void printScrapingErrorLog(String customErrorMessage, ScrapingLogDto dto){
		StringBuilder printMessageBuilder = new StringBuilder();
		String printMessage = printMessageBuilder.append("[scrapFinancialStatements] ")
			.append(customErrorMessage)
			.append(" $user={}, ")
			.append("$code={}, ")
			.append("$message={}, ")
			.append("$extraMessage={}")
			.toString();
		log.error(printMessage, dto.getEmail(), dto.getCode(), dto.getMessage(), dto.getExtraMessage());
	}

}
