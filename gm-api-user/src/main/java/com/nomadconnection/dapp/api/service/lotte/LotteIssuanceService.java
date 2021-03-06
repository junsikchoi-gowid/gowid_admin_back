package com.nomadconnection.dapp.api.service.lotte;

import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.SurveyDto;
import com.nomadconnection.dapp.api.dto.lotte.*;
import com.nomadconnection.dapp.api.dto.lotte.enums.LotteGwApiType;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.*;
import com.nomadconnection.dapp.api.service.lotte.rpc.LotteGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.util.SignVerificationUtil;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1000;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1200;
import com.nomadconnection.dapp.core.domain.repository.common.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1200Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.lotte.Lotte_Seed128;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteIssuanceService {

	private final UserRepository repoUser;
	private final Lotte_D1100Repository repoD1100;
	private final Lotte_D1000Repository repoD1000;
	private final Lotte_D1200Repository repoD1200;
	private final SignatureHistoryRepository repoSignatureHistory;
	private final RiskService riskService;

	private final CorpService corpService;
	private final LotteCommonService commonService;
	private final LotteGwRpc lotteGwRpc;
	private final EmailService emailService;
	private final CardIssuanceInfoService cardIssuanceInfoService;
	private final SurveyService surveyService;

	@Value("${mail.receipt.send-enable}")
	boolean sendReceiptEmailEnable;

	@Transactional(noRollbackFor = Exception.class)
	public void issuance(Long userIdx, CardIssuanceDto.IssuanceReq request, Long signatureHistoryIdx) {
		try {
			paramsLogging(request);
			request.setUserIdx(userIdx);
			Corp userCorp = corpService.getCorpByUserIdx(userIdx);

			// ??????(1100) ??????
			DataPart1100 resultOfD1100 = proc1100(userCorp);
			saveSignatureHistory(signatureHistoryIdx, resultOfD1100);

			// ????????? zip?????? ????????????
			procImageZip(resultOfD1100.getBzno(), resultOfD1100.getApfRcpno());

			// ????????? ??????
			sendReceiptEmail(userCorp);

			// 1200 ??????????????? ??????
			proc1200(userCorp, resultOfD1100);

			cardIssuanceInfoService.updateIssuanceStatus(request.getCardIssuanceInfoIdx(), IssuanceStatus.APPLY);
		} catch (Exception e){
			log.error("[lotte issuance] {}", e);
			throw e;
		}
	}

	private void procImageZip(String licenseNo, String registrationNo) {
		ImageZipReq requestRpc = new ImageZipReq();
		requestRpc.setEnrollmentDate(CommonUtil.getNowYYYYMMDD());
		requestRpc.setLicenseNo(licenseNo);
		requestRpc.setRegistrationNo(registrationNo);
		lotteGwRpc.requestImageZip(requestRpc);
	}

	@Transactional(rollbackFor = Exception.class)
	public void procImageZipByHand(Long userIdx){
		Corp corp = corpService.getCorpByUserIdx(userIdx);
		Lotte_D1200 lotteD1200 = repoD1200.getTopByIdxCorpOrderByIdxDesc(corp.idx());

		ImageZipReq requestRpc = new ImageZipReq();
		requestRpc.setEnrollmentDate(CommonUtil.getNowYYYYMMDD());
		requestRpc.setLicenseNo(lotteD1200.getBzno());
		requestRpc.setRegistrationNo(lotteD1200.getApfRcpno());
		lotteGwRpc.requestImageZip(requestRpc);
	}

	public void sendReceiptEmail(Corp userCorp) {
		if (!sendReceiptEmailEnable) {
			return;
		}
		log.info("[ sendReceiptEmail ] prepare to send email {}", userCorp.resCompanyNm());
		Lotte_D1100 d1100 = repoD1100.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
		Map<String, String> issuanceCounts = new HashMap<>();
		if (!StringUtils.isEmpty(d1100.getRgAkCt())) {
			issuanceCounts.put(d1100.getUnitCdC(), getLotteCardsCount(d1100.getRgAkCt()));
		}
		if (!StringUtils.isEmpty(d1100.getRgAkCt2())) {
			issuanceCounts.put(d1100.getUnitCdC2(), getLotteCardsCount(d1100.getRgAkCt2()));
		}
		if (!StringUtils.isEmpty(d1100.getRgAkCt3())) {
			issuanceCounts.put(d1100.getUnitCdC3(), getLotteCardsCount(d1100.getRgAkCt3()));
		}
		if (!StringUtils.isEmpty(d1100.getRgAkCt4())) {
			issuanceCounts.put(d1100.getUnitCdC4(), getLotteCardsCount(d1100.getRgAkCt4()));
		}
		if (!StringUtils.isEmpty(d1100.getRgAkCt5())) {
			issuanceCounts.put(d1100.getUnitCdC5(), getLotteCardsCount(d1100.getRgAkCt5()));
		}
		SurveyDto surveyResult = surveyService.findAnswerByUser(userCorp.user());
		emailService.sendReceiptEmail(CommonUtil.replaceHyphen(userCorp.resCompanyIdentityNo()), issuanceCounts,
			CardCompany.LOTTE, null, surveyResult, d1100.getTkpPnadd() + " " + d1100.getTkpBpnoAdd());
		log.info("[ sendReceiptEmail ] Complete send email {}", userCorp.resCompanyNm());
	}

	private String getLotteCardsCount(String count) {
		return Integer.toString(Integer.parseInt(count) + 1);
	}

	private DataPart1000 proc1000(Corp userCorp) {
		CommonPart commonPart = commonService.getCommonPart(LotteGwApiType.LT1000);
		Lotte_D1000 d1000 = repoD1000.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
		if (d1000 == null) {
			d1000 = Lotte_D1000.builder().idxCorp(userCorp.idx()).build();
		}

		d1000.setTransferDate(commonPart.getTransferDate());
		d1000.setBzno(CommonUtil.replaceHyphen(userCorp.resCompanyIdentityNo()));
		repoD1000.saveAndFlush(d1000);

		// ??????
		DataPart1000 requestRpc = new DataPart1000();
		BeanUtils.copyProperties(d1000, requestRpc);
		BeanUtils.copyProperties(commonPart, requestRpc);
		DataPart1000 responseRpc = lotteGwRpc.request1000(requestRpc, userCorp.user().idx());

		BeanUtils.copyProperties(responseRpc, d1000);
		repoD1000.save(d1000);

		return responseRpc;
	}

	private DataPart1100 proc1100(Corp userCorp) {
		CommonPart commonPart = commonService.getCommonPart(LotteGwApiType.LT1100);
		Lotte_D1100 d1100 = repoD1100.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
		if (d1100 == null) {
			d1100 = Lotte_D1100.builder().idxCorp(userCorp.idx()).build();
		}

		d1100.setTransferDate(commonPart.getTransferDate());
		d1100.setBzrgcIssd(CommonUtil.getNowYYYYMMDD());

		// ????????????
		d1100 = setD1100Corp(d1100, userCorp);
		// Risk??????
		d1100 = setD1100Risk(d1100, userCorp.user());
		d1100.setEstbDt(userCorp.resOpenDate());

		repoD1100.saveAndFlush(d1100);

		// ??????
		DataPart1100 requestRpc = new DataPart1100();
		BeanUtils.copyProperties(d1100, requestRpc);
		BeanUtils.copyProperties(commonPart, requestRpc);

		return lotteGwRpc.request1100(requestRpc, userCorp.user().idx(), d1100);
	}

	private Lotte_D1100 setD1100Risk(Lotte_D1100 d1100, User user) {
		Risk risk = riskService.findRiskByUserAndDateLessThanEqual(user, CommonUtil.getNowYYYYMMDD());
		d1100.setGowidEtrGdV(risk.grade());

		String gowid45DAvBalAm = String.valueOf(Math.round(Math.floor(risk.dma45())));
		d1100.setGowid45DAvBalAm(gowid45DAvBalAm);

		String gowid45DMidBalAm = String.valueOf(Math.round(Math.floor(risk.dmm45())));
		d1100.setGowid45DMidBalAm(gowid45DMidBalAm);

		String gowidPsBalAm = String.valueOf(Math.round(Math.floor(risk.actualBalance())));
		d1100.setGowidPsBalAm(gowidPsBalAm);

		//d1100.setGowidCriBalAm(getGowidCriBalAm(gowid45DAvBalAm, gowid45DMidBalAm, gowidPsBalAm));
		d1100.setGowidCriBalAm(String.valueOf(Math.round(Math.floor(risk.cashBalance()))));
		return d1100;
	}

	private Lotte_D1100 setD1100Corp(Lotte_D1100 d1100, Corp corp) {
		String companyIdentityNo = CommonUtil.replaceHyphen(corp.resCompanyIdentityNo());
		d1100.setBzno(companyIdentityNo);
		d1100.setCpNo(CommonUtil.replaceHyphen(corp.resUserIdentiyNo()));
		d1100.setEstbDt(corp.resOpenDate());
		d1100.setDpOwRrno(Lotte_Seed128.encryptEcb(companyIdentityNo));
		d1100.setCpOgNm(CommonUtil.cutString(corp.resCompanyNm(), 20));
		return d1100;
	}

	private DataPart1200 proc1200(Corp userCorp, DataPart1100 resultOfD1100) {
		CommonPart commonPart = commonService.getCommonPart(LotteGwApiType.LT1200);
		Lotte_D1200 d1200 = repoD1200.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
		if (d1200 == null) {
			d1200 = Lotte_D1200.builder().idxCorp(userCorp.idx()).build();
		}
		SignatureHistory signatureHistory = getSignatureHistory(resultOfD1100);
		String signedPlainString = SignVerificationUtil.verifySignedBinaryStringAndGetPlainString(signatureHistory.getSignedBinaryString());

		d1200.setTransferDate(commonPart.getTransferDate());
		d1200.setBzno(resultOfD1100.getBzno());
		d1200.setApfRcpno(resultOfD1100.getApfRcpno());

		// ??????
		DataPart1200 requestRpc = new DataPart1200();
		BeanUtils.copyProperties(d1200, requestRpc);
		BeanUtils.copyProperties(commonPart, requestRpc);
		requestRpc.setIdentifyValue(CommonUtil.encodeBase64(signedPlainString));

		return lotteGwRpc.request1200(requestRpc, userCorp.user().idx(), d1200);
	}

	/**
	 * ???????????? ?????? ??? ??????
	 * ????????? ????????????
	 * ????????? ????????? + base64
	 */
	@Transactional(noRollbackFor = Exception.class)
	public SignatureHistory verifySignedBinaryAndSave(Long userIdx, String signedBinaryString) {
		SignVerificationUtil.verifySignedBinaryString(signedBinaryString);

		User user = findUser(userIdx);
		SignatureHistory signatureHistory = SignatureHistory.builder()
				.userIdx(user.idx())
				.corpIdx(user.corp().idx())
				.signedBinaryString(signedBinaryString)
				.cardCompany(CardCompany.LOTTE)
				.build();

		return repoSignatureHistory.save(signatureHistory);
	}

	private SignatureHistory getSignatureHistory(DataPart1100 resultOfD1100) {
		SignatureHistory signatureHistory = commonService.getSignatureHistoryByApplicationInfo(resultOfD1100.getTransferDate().substring(0, 8), resultOfD1100.getApfRcpno());
		updateApplicationCount(signatureHistory);
		return signatureHistory;
	}

	private void updateApplicationCount(SignatureHistory signatureHistory) {
		Long count = signatureHistory.getApplicationCount();
		if (count == null) {
			count = 0L;
		}
		signatureHistory.setApplicationCount(count + 1);
		repoSignatureHistory.save(signatureHistory);
	}

	private void saveSignatureHistory(Long signatureHistoryIdx, DataPart1100 resultOfD1100) {
		SignatureHistory signatureHistory = getSignatureHistory(signatureHistoryIdx);
		signatureHistory.setApplicationDate(resultOfD1100.getTransferDate().substring(0, 8));
		signatureHistory.setApplicationNum(resultOfD1100.getApfRcpno());
	}

	private SignatureHistory getSignatureHistory(Long signatureHistoryIdx) {
		return repoSignatureHistory.findById(signatureHistoryIdx).orElseThrow(
				() -> new SystemException(ErrorCode.External.INTERNAL_SERVER_ERROR,
						"signatureHistory(" + signatureHistoryIdx + ") is not found")
		);
	}

	private void paramsLogging(CardIssuanceDto.IssuanceReq request) {
		log.debug("## request params : " + request.toString());
	}

	private User findUser(Long idx_user) {
		return repoUser.findById(idx_user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("User")
						.idx(idx_user)
						.build()
		);
	}

}
