package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1200;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.service.lotte.LotteIssuanceService;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.api.v2.dto.ImageReqDto;
import com.nomadconnection.dapp.api.v2.enums.ScrapingType;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.nomadconnection.dapp.api.v2.enums.ScrapingType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecoveryService {

	private final FinancialStatementsService financialStatementsService;
	private final IssuanceService shinhanIssuanceService;
	private final LotteIssuanceService lotteIssuanceService;
	private final ScrapingService scrapingService;
	private final UserService userService;
	private final CorpService corpService;

	@Transactional
	public void scrapByScrapingType(Long userIdx, ScrapingType scrapingType, String resClosingStandards) throws Exception {
		User user = userService.getUser(userIdx);

		if(CORP_LICENSE.equals(scrapingType)){
			scrapingService.scrapCorpLicense(user);
		} else if (CORP_REGISTRATION.equals(scrapingType)) {
			scrapingService.scrapCorpRegistration(user);
		} else if(FINANCIAL_STATEMENTS.equals(scrapingType)){
			financialStatementsService.scrap(user, resClosingStandards);
		}
	}

	@Transactional
	public void sendFullText(Long userIdx, ScrapingType scrapingType) throws Exception {
		Corp corp = corpService.getCorpByUserIdx(userIdx);
		DataPart1200 resultOfD1200 = shinhanIssuanceService.makeDataPart1200(corp.idx());
		String applyDate = resultOfD1200.getD007();
		String applyNo = resultOfD1200.getD008();

		if(CORP_LICENSE.equals(scrapingType)){
			shinhanIssuanceService.proc1510(corp, applyDate, applyNo);
		} else if (CORP_REGISTRATION.equals(scrapingType)){
			shinhanIssuanceService.proc1530(corp, applyDate, applyNo);
		} else if(FINANCIAL_STATEMENTS.equals(scrapingType)){
			shinhanIssuanceService.proc1520(corp, applyDate, applyNo);
		}
	}

	@Transactional
	public void sendImage(Long userIdx, ImageReqDto dto){
		Corp corp = corpService.getCorpByUserIdx(userIdx);
		DataPart1200 resultOfD1200 = shinhanIssuanceService.makeDataPart1200(corp.idx());

		if(CardCompany.isShinhan(dto.getCardCompany())){
			shinhanIssuanceService.procBprByHand(resultOfD1200, userIdx, dto.getImageFileType().getFileType());
		} else if(CardCompany.isLotte(dto.getCardCompany())){
			lotteIssuanceService.procImageZipByHand(userIdx);
		}
	}

}
