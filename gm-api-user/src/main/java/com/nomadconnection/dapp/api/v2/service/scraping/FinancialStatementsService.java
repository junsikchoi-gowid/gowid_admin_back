package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.v2.dto.ScrapingLogDto;
import com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.shinhan.D1520;
import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.nomadconnection.dapp.api.util.CommonUtil.replaceHyphen;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isNotAvailableScrapingTime;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.isScrapingSuccess;
import static com.nomadconnection.dapp.codef.io.sandbox.pb.STANDARD_FINANCIAL.standard_financial;

/**
 * 표준재무제표 스크래핑
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialStatementsService {
	//TODO: lhjang - 재무제표 결산월 개선

	private final UserService userService;
	private final CorpService corpService;
	private final FullTextService fullTextService;
	private final ImageService imageService;
	private final ConnectedMngService connectedMngService;
	private final ScrapingResultService scrapingResultService;

	@Transactional(rollbackFor = Exception.class)
	public ApiResponse.ApiResult scrapAndSaveFullText(Long userIdx, ConnectedMngDto.CorpInfo dto) throws Exception {
		User user = userService.getUser(userIdx);
		ApiResponse.ApiResult response = scrapFinancialStatements(user, dto); // 국세청 - 표준재무제표
		saveFullText(user.corp(), dto);

		return response;
	}

	@Transactional(rollbackFor = Exception.class)
	public ApiResponse.ApiResult scrap(Long userIdx, String resClosingStandards) throws Exception {
		User user = userService.getUser(userIdx);
		Corp corp = user.corp();
		ConnectedMngDto.CorpInfo dto = ConnectedMngDto.CorpInfo
			.builder()
			.resBusinessCode(corp.resBusinessCode())
			.resClosingStandards(resClosingStandards)
			.resCompanyEngNm(corp.resCompanyEngNm()).resCompanyPhoneNumber(corp.resCompanyNumber()).build();

		return scrapFinancialStatements(user, dto); // 국세청 - 표준재무제표
	}

	private ApiResponse.ApiResult scrapFinancialStatements(User user, ConnectedMngDto.CorpInfo dto) throws Exception {
		Corp corp = user.corp();
		Long corpIdx = corp.idx();
		String licenseNo = corp.resCompanyIdentityNo();
		String connectedId = connectedMngService.getConnectedId(user.idx());
		String resClosingStandards = StringUtils.isEmpty(dto.getResClosingStandards()) ? ScrapingCommonUtils.DEFAULT_CLOSING_STANDARDS_MONTH : dto.getResClosingStandards();
		List<String> listYyyyMm = getFindClosingStandards(resClosingStandards);
		ApiResponse.ApiResult response = null;

		// 국세청 - 증명발급 표준재무재표
		for(String yyyyMm : listYyyyMm){
			String standardFinancialResult  = requestStandardFinancialScraping(connectedId, yyyyMm, licenseNo);
			JSONObject[] jsonObjectStandardFinancial = scrapingResultService.getApiResult(standardFinancialResult);
			String code = scrapingResultService.getCode();
			String message = scrapingResultService.getMessage();
			String extraMessage = scrapingResultService.getExtraMessage();
			response = ApiResponse.ApiResult.builder().code(code).desc(message).extraMessage(extraMessage).build();
			ScrapingLogDto logDto = ScrapingLogDto.builder().email(user.email()).code(code).message(message).extraMessage(extraMessage).build();

			if(corpService.isNewCorp(Integer.parseInt(resClosingStandards), LocalDate.parse(corp.resOpenDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))){
				D1530 d1530 = fullTextService.findFirstByIdxCorpIn1530(corpIdx);
				fullTextService.saveDefault1520(d1530, corp);
				printScrapingErrorLog("It is a new corporation.", logDto);
				break;
			}

			if(isScrapingSuccess(code)) {
				JSONObject scrapingResult = jsonObjectStandardFinancial[1];
				D1520 d1520 = fullTextService.build1520(corp, scrapingResult);
				fullTextService.save1520(d1520);
				imageService.sendFinancialStatementsImage(user.cardCompany(), yyyyMm, standardFinancialResult, licenseNo);
			} else if(isNotAvailableScrapingTime(code)) {
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
	}

	private void saveFullText(Corp corp, ConnectedMngDto.CorpInfo dto) {
		Long corpIdx = corp.idx();

		fullTextService.save1100(corp);
		D1400 d1400 = fullTextService.findFirstByIdxCorpIn1400(corpIdx);
		fullTextService.save1400(d1400, dto);
		D1000 d1000 = fullTextService.findFirstByIdxCorpIn1000(corpIdx);
		fullTextService.save1000(d1000, dto);
	}

	private String requestStandardFinancialScraping(String connectedId, String yyyyMm, String licenseNo) throws InterruptedException, ParseException, IOException {
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
