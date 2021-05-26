package com.nomadconnection.dapp.api.v2.service.saas;

import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.v2.dto.saas.SaasTrackerCheckListDto;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasCheckInfoRepository;
import com.nomadconnection.dapp.core.domain.saas.SaasCheckInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasCheckType;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaasTrackerCheckListService {

	private final SaasCheckInfoRepository checkInfoRepository;
	private final UserService userService;

	/**
	 * 모든 항목의 체크리스트 건수 조회
	 *
	 * @param user
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public SaasTrackerCheckListDto.CheckCountRes getCheckListCount(CustomUser user) {
		log.info("[SaasTrackerCheckListService.getCheckListCount] start");
		List<SaasCheckInfo> saasCheckInfos = this.findSaasCheckInfoListByUser(userService.getUser(user.idx()));
		return SaasTrackerCheckListDto.CheckCountRes.builder()
			.needCancelCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.NEED_CANCEL))
			.newSaasCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.NEW))
			.reRegistrationCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.RE_REGISTRATION))
			.strangePaymentCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.STRANGE))
			.freeChangeCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.FREE_CHANGE))
			.increasedPaymentCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.INCREASED))
			.freeExpirationCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.FREE_EXPIRATION))
			.duplicatePaymentCnt(this.getCheckListCountByType(saasCheckInfos, SaasCheckType.DUPLICATE))
			.build();
	}

	/**
	 * 모든 항목의 체크리스트 데이터 조회
	 *
	 * @param user
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public SaasTrackerCheckListDto.CheckDataListRes getCheckListData(CustomUser user) {
			List<SaasCheckInfo> saasCheckInfos = this.findSaasCheckInfoListByUser(userService.getUser(user.idx()));
			return SaasTrackerCheckListDto.CheckDataListRes.builder()
				.needCancelList(this.getCheckListByType(saasCheckInfos, SaasCheckType.NEED_CANCEL))
				.newSaasList(this.getCheckListByType(saasCheckInfos, SaasCheckType.NEW))
				.reRegistrationList(this.getCheckListByType(saasCheckInfos, SaasCheckType.RE_REGISTRATION))
				.strangePaymentList(this.getCheckListByType(saasCheckInfos, SaasCheckType.STRANGE))
				.freeChangeList(this.getCheckListByType(saasCheckInfos, SaasCheckType.FREE_CHANGE))
				.increasedPaymentList(this.getCheckListByType(saasCheckInfos, SaasCheckType.INCREASED))
				.freeExpirationList(this.getCheckListByType(saasCheckInfos, SaasCheckType.FREE_EXPIRATION))
				.duplicatePaymentList(this.getCheckListByType(saasCheckInfos, SaasCheckType.DUPLICATE))
				.build();
	}

	/**
	 * 체크리스트 항목의 정보 수정
	 *
	 * @param user
	 * @param idxSaasCheckInfo
	 * @param req
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateSaasCheckInfo(CustomUser user, Long idxSaasCheckInfo, SaasTrackerCheckListDto.CheckInfoReq req) {
		this.updateSaasCheckInfo(userService.getUser(user.idx()), idxSaasCheckInfo, req);
	}

	/**
	 * 체크리스트 목록 조회
	 *
	 * @param user
	 * @return
	 */
	public List<SaasCheckInfo> findSaasCheckInfoListByUser(User user) {
		return checkInfoRepository.findAllByUserAndCheckedFalseOrderByCreatedAt(user);
	}

	/**
	 * 체크리스트 항목 조회
	 *
	 * @param idxSaasCheckInfo
	 * @return
	 */
	public SaasCheckInfo findSaasCheckInfo(User user, Long idxSaasCheckInfo) {
		return checkInfoRepository.findByUserAndIdx(user, idxSaasCheckInfo).orElseThrow(
			() -> EntityNotFoundException.builder()
				.message("Saas Tracker's Check Info is not found.")
				.entity("SaasCheckInfo")
				.idx(idxSaasCheckInfo)
				.build()
		);
	}

	/**
	 * 체크리스트 내에서 특정 항목 건수 조회
	 *
	 * @param saasCheckInfoList
	 * @param type
	 * @return
	 */
	private int getCheckListCountByType(List<SaasCheckInfo> saasCheckInfoList, SaasCheckType type) {
		return Long.valueOf(saasCheckInfoList.stream().filter(f -> f.saasCheckCategory().name().equals(type)).count()).intValue();
	}

	/**
	 * 체크리스트 내에서 특정 항목 데이터 조회
	 *
	 * @param saasCheckInfoList
	 * @param type
	 * @return
	 */
	private List<SaasTrackerCheckListDto.CheckData> getCheckListByType(List<SaasCheckInfo> saasCheckInfoList, SaasCheckType type) {
		return saasCheckInfoList.stream().filter(f -> f.saasCheckCategory().name().equals(type)).map(SaasTrackerCheckListDto.CheckData::from).collect(Collectors.toList());
	}

	/**
	 * 체크리스트 항목의 정보 수정(User 객첵로 처리)
	 *
	 * @param user
	 * @param idxSaasCheckInfo
	 * @param req
	 */
	private void updateSaasCheckInfo(User user, Long idxSaasCheckInfo, SaasTrackerCheckListDto.CheckInfoReq req) {
		this.findSaasCheckInfo(user, idxSaasCheckInfo).checked(req.getChecked());
	}

}