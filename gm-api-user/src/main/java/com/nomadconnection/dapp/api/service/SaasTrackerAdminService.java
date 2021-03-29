package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.SaasTrackerAdminDto;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasPaymentHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasPaymentInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasTrackerProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.user.AuthorityRepository;
import com.nomadconnection.dapp.core.domain.saas.SaasInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentHistory;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasTrackerProgress;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaasTrackerAdminService {

	private final SaasTrackerProgressRepository repoSaasTrackerProgress;
	private final SaasPaymentHistoryRepository repoSaasPaymentHistory;
	private final SaasPaymentInfoRepository repoSaasPaymentInfo;

	private final AuthorityRepository authorityRepository;

	private final UserService userService;
	private final SaasTrackerService saasTrackerService;

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasTrackerUser(Long userIdx) {

		log.info(">>>>> admin.getSaasTrackerUser.start");
		User user = userService.getUser(userIdx);

		try {
			List<SaasTrackerAdminDto.SaasTrackerUserRes> saasTrackerUserList =
					repoSaasTrackerProgress.getSaasTrackerUsers().stream().map(SaasTrackerAdminDto.SaasTrackerUserRes::from).collect(Collectors.toList());

			log.info(">>>>> admin.getSaasTrackerUser.end");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(saasTrackerUserList).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentHistories(Long idxUser, String fromPaymentDate, String toPaymentDate, Pageable pageable) {

		log.info(">>>>> admin.getSaasPaymentHistories.start");
		User user = userService.getUser(idxUser);

		try {
			Page<SaasTrackerAdminDto.SaasPaymentHistoryRes> saasPaymentHistories =
					repoSaasPaymentHistory.findAllByUserAndPaymentDateBetweenOrderByPaymentDateDesc(user,
							StringUtils.isEmpty(fromPaymentDate) ? "20190101" : fromPaymentDate,
							StringUtils.isEmpty(toPaymentDate) ? "20301231" : toPaymentDate,
							pageable)
							.map(SaasTrackerAdminDto.SaasPaymentHistoryRes::from);

			log.info(">>>>> admin.getSaasPaymentHistories.end");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(saasPaymentHistories).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentHistoryDetail(Long idx) {

		log.info(">>>>> admin.getSaasPaymentHistoryDetail.start");
		SaasPaymentHistory saasPaymentHistory = saasTrackerService.findSaasPaymentHistory(idx);
		log.info(">>>>> admin.getSaasPaymentHistoryDetail.end");

		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(SaasTrackerAdminDto.SaasPaymentHistoryRes.from(saasPaymentHistory)).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveSaasPaymentHistory(SaasTrackerAdminDto.SaasPaymentHistoryReq req) {

		log.info(">>>>> admin.saveSaasPaymentHistory.start");
		User user = userService.getUser(req.getIdxUser());
		SaasInfo saasInfo = saasTrackerService.findSaasInfo(req.getIdxSaasInfo());

		try {
			SaasPaymentHistory history = SaasPaymentHistory.builder()
					.organization(req.getOrganization())
					.paymentDate(req.getPaymentDate())
					.paymentPrice(req.getPaymentPrice())
					.paymentMethod(req.getPaymentMethod())
					.accountNumber(req.getAccountNumber())
					.cardNumber(req.getCardNumber())
					.foreignType(req.getForeignType())
					.currency(req.getCurrency())
					.user(user)
					.saasInfo(saasInfo)
					.item(req.getItem())
					.build();
			repoSaasPaymentHistory.save(history);

			log.info(">>>>> admin.saveSaasPaymentHistory.end");

			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(true)
							.build())
					.build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity deleteSaasPaymentHistory(Long idx) {

		log.info(">>>>> admin.deleteSaasPaymentHistory.start");

		SaasPaymentHistory saasPaymentHistory = saasTrackerService.findSaasPaymentHistory(idx);
		repoSaasPaymentHistory.delete(saasPaymentHistory);

		log.info(">>>>> admin.deleteSaasPaymentHistory.end");

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.status(true)
						.build())
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentInfos(Long idxUser, Pageable pageable) {

		log.info(">>>>> admin.getSaasPaymentInfos.start");
		User user = userService.getUser(idxUser);

		try {
			Page<SaasTrackerAdminDto.SaasPaymentInfoRes> saasPaymentInfos =
					repoSaasPaymentInfo.findAllByUser(user, pageable).map(SaasTrackerAdminDto.SaasPaymentInfoRes::from);
			log.info(">>>>> admin.getSaasPaymentInfos.end");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(saasPaymentInfos).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentInfoDetail(Long idx) {

		log.info(">>>>> admin.getSaasPaymentInfoDetail.start");
		SaasPaymentInfo saasPaymentInfo = saasTrackerService.findSaasPaymentInfo(idx);
		log.info(">>>>> admin.getSaasPaymentInfoDetail.end");

		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(SaasTrackerAdminDto.SaasPaymentInfoRes.from(saasPaymentInfo)).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveSaasPaymentInfo(SaasTrackerAdminDto.SaasPaymentInfoReq req) {

		log.info(">>>>> admin.saveSaasPaymentInfo.start");
		User user = userService.getUser(req.getIdxUser());
		SaasInfo saasInfo = saasTrackerService.findSaasInfo(req.getIdxSaasInfo());

		try {
			SaasPaymentInfo info = SaasPaymentInfo.builder()
					.organization(req.getOrganization())
					.currentPaymentDate(req.getCurrentPaymentDate())
					.currentPaymentPrice(req.getCurrentPaymentPrice())
					.paymentType(req.getPaymentType())
					.paymentMethod(req.getPaymentMethod())
					.accountNumber(req.getAccountNumber())
					.cardNumber(req.getCardNumber())
					.paymentScheduleDate(req.getPaymentScheduleDate())
					.activeSubscription(req.getActiveSubscription())
					.isDup(req.getIsDup())
					.disabled(req.getDisabled())
					.user(user)
					.saasInfo(saasInfo)
					.build();
			repoSaasPaymentInfo.save(info);

			log.info(">>>>> admin.saveSaasPaymentInfo.end");

			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(true)
							.build())
					.build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity deleteSaasPaymentInfo(Long idx) {

		log.info(">>>>> admin.deleteSaasPaymentInfo.start");

		SaasPaymentInfo saasPaymentInfo = saasTrackerService.findSaasPaymentInfo(idx);
		repoSaasPaymentInfo.delete(saasPaymentInfo);

		log.info(">>>>> admin.deleteSaasPaymentInfo.end");

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.status(true)
						.build())
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity updateSaasInfo(Long idx, SaasTrackerAdminDto.SaasPaymentInfoReq req) {

		log.info(">>>>> admin.updateSaasInfo.start");

		SaasPaymentInfo saasPaymentInfo = saasTrackerService.findSaasPaymentInfo(idx);

		try {
			if(!StringUtils.isEmpty(req.getOrganization())) saasPaymentInfo.organization(req.getOrganization());
			if(!StringUtils.isEmpty(req.getCurrentPaymentDate())) saasPaymentInfo.currentPaymentDate(req.getCurrentPaymentDate());
			if(!ObjectUtils.isEmpty(req.getCurrentPaymentPrice())) saasPaymentInfo.currentPaymentPrice(req.getCurrentPaymentPrice());
			if(!ObjectUtils.isEmpty(req.getPaymentType())) saasPaymentInfo.paymentType(req.getPaymentType());
			if(!ObjectUtils.isEmpty(req.getPaymentMethod())) saasPaymentInfo.paymentMethod(req.getPaymentMethod());
			if(req.getPaymentMethod() == 1) {
				saasPaymentInfo.cardNumber(req.getCardNumber());
				saasPaymentInfo.accountNumber(null);
			}else {
				saasPaymentInfo.cardNumber(null);
				saasPaymentInfo.accountNumber(req.getAccountNumber());
			}
			if(!StringUtils.isEmpty(req.getPaymentScheduleDate())) saasPaymentInfo.paymentScheduleDate(req.getPaymentScheduleDate());
			if(!ObjectUtils.isEmpty(req.getActiveSubscription())) saasPaymentInfo.activeSubscription(req.getActiveSubscription());
			if(!ObjectUtils.isEmpty(req.getIsDup())) saasPaymentInfo.isDup(req.getIsDup());
			if(!ObjectUtils.isEmpty(req.getDisabled())) saasPaymentInfo.disabled(req.getDisabled());

			log.info(">>>>> admin.updateSaasInfo.end");

			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(true)
							.build())
					.build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	public ResponseEntity getSaasSubscriptions(Long idx) {
		return saasTrackerService.getUseSaasList(idx);
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity findUserAtSaasTracker(String email) {
		log.info(">>>>> admin.findUserAtSaasTracker.start");
		SaasTrackerAdminDto.SaasTrackerFindUserRes findUser = SaasTrackerAdminDto.SaasTrackerFindUserRes.from(userService.findByEmail(email));
		log.info(">>>>> admin.findUserAtSaasTracker.end");

		return ResponseEntity.ok().body(
			BusinessResponse.builder().data(findUser).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveSaasTrackerUser(SaasTrackerAdminDto.SaasTrackerUserReq req) {

		log.info(">>>>> admin.saveSaasTrackerUser.start");
		User user = userService.getUser(req.getIdxUser());
		user.authorities().add(authorityRepository.findByRole(Role.GOWID_SAASTRACKER).get());
		log.info(">>>>> admin.saveSaasTrackerUser.end");

		return ResponseEntity.ok().body(BusinessResponse.builder()
			.normal(BusinessResponse.Normal.builder()
				.status(true)
				.build())
			.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity updateSaasTrackerUser(Long idxUser, SaasTrackerAdminDto.SaasTrackerUserUpdateReq req) {

		log.info(">>>>> admin.saveSaasTrackerUser.start");

		SaasTrackerProgress progress = repoSaasTrackerProgress.findByUser(userService.getUser(idxUser)).orElseThrow(
			() -> UserNotFoundException.builder()
			.id(idxUser)
			.build());

		progress.status(req.getStatus());
		if(!ObjectUtils.isEmpty(req.getStep())) progress.step(req.getStep());

		log.info(">>>>> admin.saveSaasTrackerUser.end");

		return ResponseEntity.ok().body(BusinessResponse.builder()
			.normal(BusinessResponse.Normal.builder()
				.status(true)
				.build())
			.build());
	}
}