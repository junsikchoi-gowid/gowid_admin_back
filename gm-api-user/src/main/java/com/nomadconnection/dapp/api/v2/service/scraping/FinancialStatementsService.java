package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.codef.io.helper.ResponseCode;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.nomadconnection.dapp.api.util.CommonUtil.replaceHyphen;
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
	public void scrap(Long userIdx, ConnectedMngDto.CorpInfo financialStatementsParam) throws Exception {
		User user = userService.getUser(userIdx);
		scrapFinancialStatements(user, financialStatementsParam); // 국세청 - 표준재무제표
	}

	private void scrapFinancialStatements(User user, ConnectedMngDto.CorpInfo financialStatementsParam) throws Exception {
		Corp corp = user.corp();
		Long corpIdx = corp.idx();
		String licenseNo = corp.resCompanyIdentityNo();
		String connectedId = connectedMngService.getConnectedId(user.idx());
		List<String> listYyyyMm = getFindClosingStandards(financialStatementsParam.getResClosingStandards().trim());
		AtomicReference<Boolean> isFirst = new AtomicReference<>(true);

		// 국세청 - 증명발급 표준재무재표
		for(String yyyyMm : listYyyyMm){
			String standardFinancialResult  = requestStandardFinancialScraping(connectedId, yyyyMm, licenseNo);
			JSONObject[] jsonObjectStandardFinancial = scrapingResultService.getApiResult(standardFinancialResult);

			if(isScrapingSuccess(scrapingResultService.getCode())) {
				JSONObject scrapingResult = jsonObjectStandardFinancial[1];
				D1520 d1520 = fullTextService.build1520(corp, scrapingResult);
				fullTextService.save1520(d1520);
				imageService.sendFinancialStatementsImage(user.cardCompany(), yyyyMm, standardFinancialResult, licenseNo);
			} else if(isFirst.get()){
				D1530 d1530 = fullTextService.findFirstByIdxCorpIn1530(corpIdx);
				fullTextService.saveDefault1520(d1530, corp);
			}
			isFirst.set(false);
		}

		fullTextService.save1100(corp);
		D1400 d1400 = fullTextService.findFirstByIdxCorpIn1400(corpIdx);
		fullTextService.save1400(d1400, financialStatementsParam);

		// TODO: 기업주소 입력변경으로 인하여 해당로직 필요여부 논의 필요
		corpService.save(user.corp()
			.resCompanyEngNm(financialStatementsParam.getResCompanyEngNm())
			.resCompanyNumber(financialStatementsParam.getResCompanyPhoneNumber())
			.resBusinessCode(financialStatementsParam.getResBusinessCode())
		);

		D1000 d1000 = fullTextService.findFirstByIdxCorpIn1000(corpIdx);
		fullTextService.save1000(d1000, financialStatementsParam);

		// 지급보증 파일생성 및 전송
		imageService.sendGuaranteeImage(corp, user.cardCompany(), licenseNo);
	}

	private String requestStandardFinancialScraping(String connectedId, String yyyyMm, String licenseNo) throws InterruptedException, ParseException, IOException {
		String code;
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
		scrapingResultService.getApiResult(scrapingResult);
		code = scrapingResultService.getCode();

		if(!isScrapingSuccess(code)){
			throw new CodefApiException(ResponseCode.findByCode(code));
		}
		log.info( " FinancialStatements strResultTemp = {} " , scrapingResult);
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

}
