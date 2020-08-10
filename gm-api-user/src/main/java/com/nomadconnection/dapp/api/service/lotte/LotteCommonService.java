package com.nomadconnection.dapp.api.service.lotte;

import com.nomadconnection.dapp.api.dto.lotte.CommonPart;
import com.nomadconnection.dapp.api.dto.lotte.enums.LotteGwApiType;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.common.GatewayTransactionIdx;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.domain.lotte.Lotte_GwTranHist;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_GwTranHistRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.GatewayTransactionIdxRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.SignatureHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteCommonService {

	private final Lotte_GwTranHistRepository repoLotte_gwTranHist;
	private final GatewayTransactionIdxRepository repoGatewayTransactionIdx;
	private final UserRepository RepoUser;
	private final SignatureHistoryRepository repoSignatureHistory;

	// 연동 기록 저장
	@Async
	@Transactional(noRollbackFor = Exception.class)
	public void saveGwTran(CommonPart commonPart, Long idxUser) {
		log.debug("## save tran {} - start", commonPart.getProtocolCode());
		Lotte_GwTranHist gwTranHist = new Lotte_GwTranHist();
		BeanUtils.copyProperties(commonPart, gwTranHist);
		gwTranHist.setUserIdx(idxUser);
		gwTranHist.setCorpIdx(getCorpIdx(idxUser));
		repoLotte_gwTranHist.save(gwTranHist);
		log.debug("## save tran {} - end", commonPart.getProtocolCode());
	}

	private Long getCorpIdx(Long userIdx) {
		if (ObjectUtils.isEmpty(userIdx)) {
			return null;
		}
		User user = RepoUser.findById(userIdx).orElse(null);
		if (user == null) {
			return null;
		}
		return !ObjectUtils.isEmpty(user.corp()) ? user.corp().idx() : null;
	}

	protected CommonPart getCommonPart(LotteGwApiType apiType) {
		// common part 세팅.
		// optional : 응답 코드, 대외 기관 코드, 응답 메시지
		return CommonPart.builder()
				.protocolCode(apiType.getProtocolCode())
				.transferCode(apiType.getTransferCode())
				.guid(getTransactionId(Integer.parseInt(apiType.getProtocolCode())))
				.transferDate(CommonUtil.getNowYYYYMMDD() + CommonUtil.getNowHHMMSS())
				.build();
	}

	private String getTransactionId(Integer interfaceId) {
		GatewayTransactionIdx gatewayTransactionIdx = GatewayTransactionIdx.builder()
				.interfaceId(interfaceId)
				.cardCompany(CardCompany.LOTTE)
				.build();
		repoGatewayTransactionIdx.save(gatewayTransactionIdx);
		repoGatewayTransactionIdx.flush();

		long tmpTranId = 20000000000L + gatewayTransactionIdx.getIdx();
		return "0" + tmpTranId;     // 020000000001, 신한이랑 동일하게 set
	}

	public SignatureHistory getSignatureHistoryByApplicationInfo(String applicationDate, String applicationNum) {
		return repoSignatureHistory.findFirstByApplicationDateAndApplicationNumOrderByUpdatedAtDesc(applicationDate, applicationNum).orElseThrow(
				() -> new BadRequestException(ErrorCode.Api.NOT_FOUND,
						"not found signatureHistoryRepository. applicationDate[" + applicationDate + "], applicationNum[" + applicationNum + "]")
		);
	}
}
