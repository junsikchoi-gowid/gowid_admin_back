package com.nomadconnection.dapp.api.service.lotte;

import com.nomadconnection.dapp.api.common.AsyncService;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.lotte.CommonPart;
import com.nomadconnection.dapp.api.dto.lotte.DataPart1000;
import com.nomadconnection.dapp.api.dto.lotte.DataPart1100;
import com.nomadconnection.dapp.api.dto.lotte.DataPart1200;
import com.nomadconnection.dapp.api.dto.lotte.enums.LotteGwApiType;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.CommonCardService;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.service.lotte.rpc.LotteGwRpc;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.util.SignVerificationUtil;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CeoType;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgressType;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1000;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1200;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.common.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1200Repository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1530Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteIssuanceService {

	private final UserRepository repoUser;
	private final IssuanceProgressRepository repoIssuanceProgress;
	private final Lotte_D1100Repository repoD1100;
	private final Lotte_D1000Repository repoD1000;
	private final Lotte_D1200Repository repoD1200;
	private final SignatureHistoryRepository repoSignatureHistory;
	private final RiskRepository repoRisk;
	private final D1000Repository shinhanRepoD1000;
	private final D1530Repository shinhanRepoD1530;

	private final AsyncService asyncService;
	private final UserService userService;
	private final LotteCommonService commonService;
	private final LotteGwRpc lotteGwRpc;
	private final EmailService emailService;
	private final CommonCardService commonCardService;

	@Value("${mail.receipt.send-enable}")
	boolean sendReceiptEmailEnable;

	@Transactional(noRollbackFor = Exception.class)
	public String verifyNewMember(Long userIdx) {
		// 1000(법인회원신규여부검증)
		Corp userCorp = getCorpByUserIdx(userIdx);
		userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.LP_1000, CardCompany.LOTTE);
		DataPart1000 resultOfD1000 = proc1000(userCorp);
		userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.LP_1000, CardCompany.LOTTE);

		if ("Y".equals(resultOfD1000.getBzNewYn())) {
			repoD1100.save(Lotte_D1100.builder().idxCorp(userCorp.idx()).build());
			repoD1200.save(Lotte_D1200.builder().idxCorp(userCorp.idx()).build());
			return "SUCCESS";
		} else if ("N".equals(resultOfD1000.getBzNewYn())) {
			commonCardService.deleteAllIssuanceInfo(userCorp.user());
			return "FAIL";
		} else {
			String msg = "bzNewYn is not Y/N. resultOfD1000.getBzNewYn() = " + resultOfD1000.getBzNewYn();
			CommonUtil.throwBusinessException(ErrorCode.External.INTERNAL_ERROR_LOTTE_1000, msg);
			return "NONE";
		}
	}

	// 테스트 용도
	@Transactional(noRollbackFor = Exception.class)
	public String verifyNewMemberTest(Long userIdx) {
		Corp userCorp = getCorpByUserIdx(userIdx);
		Lotte_D1000 d1000 = Lotte_D1000.builder().idxCorp(userCorp.idx()).build();
		d1000.setBzno(userCorp.resCompanyIdentityNo().replaceAll("-", ""));
		repoD1000.save(d1000);
		repoD1100.save(Lotte_D1100.builder().idxCorp(userCorp.idx()).build());
		repoD1200.save(Lotte_D1200.builder().idxCorp(userCorp.idx()).build());
		return "SUCCESS";
	}

	@Transactional(noRollbackFor = Exception.class)
	public void issuance(Long userIdx, CardIssuanceDto.IssuanceReq request, Long signatureHistoryIdx) {
		User user = findUser(userIdx);
		paramsLogging(request);
		request.setUserIdx(userIdx);
		userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.SIGNED, CardCompany.LOTTE);
		Corp userCorp = getCorpByUserIdx(userIdx);
		userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.SIGNED, CardCompany.LOTTE);
		repoIssuanceProgress.flush();

		// 신규(1100) 신청
		DataPart1100 resultOfD1100 = null;
		userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.LP_1100, CardCompany.LOTTE);
		resultOfD1100 = proc1100(userCorp);
		saveSignatureHistory(signatureHistoryIdx, resultOfD1100);
		userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.LP_1100, CardCompany.LOTTE);

		// 1200 전자서명값 제출
		userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.LP_1200, CardCompany.LOTTE);
		DataPart1200 resultOfD1200 = proc1200(userCorp, resultOfD1100);
		userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.LP_1200, CardCompany.LOTTE);

		// 이미지 전송(비동기)
		asyncService.run(() -> procImage(userCorp, resultOfD1200, userIdx));
	}

	@Async
	void procImage(Corp userCorp, DataPart1200 resultOfD1200, Long userIdx) {
		userService.saveIssuanceProgFailed(userIdx, IssuanceProgressType.P_IMG, CardCompany.LOTTE);
		for (int i = 0; i < 4; i++) {
			try {
				lotteGwRpc.requestImageTransfer(userCorp.resCompanyIdentityNo(), userIdx);     // 이미지 전송요청
				userService.saveIssuanceProgSuccess(userIdx, IssuanceProgressType.P_IMG, CardCompany.LOTTE);
				sendReceiptEmail(resultOfD1200);
				return;
			} catch (Exception e) {
				log.error("[procBpr] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
			}
		}
	}

	private void sendReceiptEmail(DataPart1200 resultOfD1200) {
		if (!sendReceiptEmailEnable) {
			return;
		}
		emailService.sendReceiptEmail(resultOfD1200.getBzno(), CardCompany.LOTTE);
		log.debug("## receipt email sent. biz no = " + resultOfD1200.getBzno());
	}

	private DataPart1000 proc1000(Corp userCorp) {
		CommonPart commonPart = commonService.getCommonPart(LotteGwApiType.LT1000);
		Lotte_D1000 d1000 = repoD1000.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
		if (d1000 == null) {
			d1000 = Lotte_D1000.builder().idxCorp(userCorp.idx()).build();
		}

		d1000.setTransferDate(commonPart.getTransferDate());
		d1000.setBzno(userCorp.resCompanyIdentityNo().replaceAll("-", ""));

		// 연동
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

		// 법인정보
		d1100.setBzno(userCorp.resCompanyIdentityNo().replaceAll("-", ""));
		d1100.setCpNo(userCorp.resUserIdentiyNo().replaceAll("-", ""));
		d1100.setEstbDt(userCorp.resOpenDate());
		d1100.setDpOwRrno(userCorp.resCompanyIdentityNo().replaceAll("-", ""));
		d1100.setCpOgNm(userCorp.resCompanyNm());

		// 신한 전문 데이터
		D1000 shinhanD1000 = shinhanRepoD1000.getTopByIdxCorpOrderByIdxDesc(userCorp.idx());
		d1100.setDgTc(CeoType.covertShinhanToLotte(shinhanD1000.getD009()));

		D1530 shinhanD1530 = shinhanRepoD1530.findFirstByIdxCorpOrderByUpdatedAtDesc(userCorp.idx());
		d1100.setEstbDt(shinhanD1530.getD057());

		// Risk정보
		Risk risk = findRisk(userCorp.user());
		d1100.setGowidEtrGdV(risk.grade());
		d1100.setGowid45dAvBalAm(String.valueOf(risk.dma45()));
		d1100.setGowid45dMidBalAm(String.valueOf(risk.dmm45()));
		d1100.setGowidPsBalAm(String.valueOf(risk.currentBalance()));
		d1100.setGowidCriBalAm(String.valueOf(risk.minCashNeed()));

		// 연동
		DataPart1100 requestRpc = new DataPart1100();
		BeanUtils.copyProperties(d1100, requestRpc);
		BeanUtils.copyProperties(commonPart, requestRpc);
		DataPart1100 responseRpc = lotteGwRpc.request1100(requestRpc, userCorp.user().idx());

		BeanUtils.copyProperties(responseRpc, d1100);
		repoD1100.save(d1100);

		return responseRpc;
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
		d1200.setBzno(userCorp.resCompanyIdentityNo().replaceAll("-", ""));
		d1200.setApfRcpno(resultOfD1100.getApfRcpno());
		d1200.setIdentifyValue(CommonUtil.encodeBase64(signedPlainString));

		// 연동
		DataPart1200 requestRpc = new DataPart1200();
		BeanUtils.copyProperties(d1200, requestRpc);
		BeanUtils.copyProperties(commonPart, requestRpc);
		DataPart1200 responseRpc = lotteGwRpc.request1200(requestRpc, userCorp.user().idx());

		BeanUtils.copyProperties(responseRpc, d1200);
		repoD1200.save(d1200);

		return responseRpc;
	}

	/**
	 * 전자서명 검증 및 저장
	 * 저장은 바이너리
	 * 전송시 평문화 + base64
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
		SignatureHistory signatureHistory = commonService.getSignatureHistoryByApplicationInfo(resultOfD1100.getTransferDate(), resultOfD1100.getApfRcpno());
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
		signatureHistory.setApplicationDate(resultOfD1100.getTransferDate());
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

	private Corp getCorpByUserIdx(Long userIdx) {
		User user = findUser(userIdx);
		Corp userCorp = user.corp();
		if (userCorp == null) {
			log.error("not found corp. userIdx=" + userIdx);
			throw new BadRequestException(ErrorCode.Api.NOT_FOUND, "corp(userIdx=" + userIdx + ")");
		}
		return userCorp;
	}

	private User findUser(Long idx_user) {
		return repoUser.findById(idx_user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("User")
						.idx(idx_user)
						.build()
		);
	}

	private Risk findRisk(User user) {
		return repoRisk.findTopByUserAndDateLessThanOrderByDateDesc(user, CommonUtil.getNowYYYYMMDD()).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("Risk")
						.build()
		);
	}
}
