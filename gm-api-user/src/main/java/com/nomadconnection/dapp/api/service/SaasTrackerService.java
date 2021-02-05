package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.SaasTrackerDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.notification.SlackNotiService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.repository.saas.*;
import com.nomadconnection.dapp.core.domain.saas.*;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto.RecoveryNotiReq.getSlackRecoveryMessage;
import static com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto.SaasTrackerNotiReq.getSlackSaasTrackerMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaasTrackerService {

	private final SaasTrackerProgressRepository repoSaasTrackerProgress;
	private final SaasCategoryRepository repoSaasCategory;
	private final SaasIssueReportRepository repoSaasIssueReport;
	private final SaasPaymentHistoryRepository repoSaasPaymentHistory;
	private final SaasPaymentInfoRepository repoSaasPaymentInfo;
	private final SaasInfoRepository repoSaasInfo;

	private final CorpService corpService;
	private final UserService userService;
	private final SlackNotiService slackNotiService;

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveSaasTrackerReports(Long userIdx, SaasTrackerDto.SaasTrackerReportsReq dto) {

		log.info(">>>>> saveSaasTrackerReports.start");
		User user = userService.getUser(userIdx);

		try {
			SaasIssueReport report = SaasIssueReport.builder()
					.reportType(dto.getReportType())
					.saasName(dto.getSaasName())
					.paymentMethod(dto.getPaymentMethod())
					.paymentPrice(dto.getPaymentPrice())
					.issue(dto.getIssue())
					.experationDate(dto.getExperationDate())
					.activeExperationAlert(dto.getActiveExperationAlert())
					.user(user)
					.build();

			repoSaasIssueReport.save(report);

			log.info(">>>>> saveSaasTrackerReports.complete");

			// 제보 Slack Notification
			this.sendSlackNotification(userIdx, dto);

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
	public ResponseEntity getSaasTrackerProgress(Long userIdx) {

		log.info(">>>>> getSaasTrackerProgress.start");
		User user = userService.getUser(userIdx);

		SaasTrackerProgress progress = new SaasTrackerProgress();
		try {
			progress = findSaasTrackerProgress(user);
		}catch(EntityNotFoundException enfe) {
			progress = new SaasTrackerProgress();
			progress.user(user)
					.status(SaaSTrackerType.STATUS_REQUEST.getValue())
					.step(SaaSTrackerType.STEP_INIT.getValue());
			repoSaasTrackerProgress.save(progress);
		}
		SaasTrackerDto.SaaSTrackerProgressRes saasTrackerProgressRes = SaasTrackerDto.SaaSTrackerProgressRes.from(progress);

		log.info(">>>>> getSaasTrackerProgress.complete");
		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(saasTrackerProgressRes).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity updateSaasTrackerProgress(Long userIdx, Integer step) {

		log.info(">>>>> updateSaasTrackerProgress.start");
		User user = userService.getUser(userIdx);

		try {
			SaasTrackerProgress progress = findSaasTrackerProgress(user);
			if(SaaSTrackerType.STEP_ALL_COMPLETE.getValue() == step) {
				progress.status(SaaSTrackerType.STATUS_REQUEST_COMPLETE.getValue());
				this.sendSlackNotification(userIdx);
			}
			progress.step(step);

			log.info(">>>>> updateSaasTrackerProgress.complete");
			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(true)
							.build())
					.build());
		}catch(EntityNotFoundException enfe) {
			log.error(enfe.getMessage(), enfe);
			throw new SystemException(ErrorCode.Api.NOT_FOUND);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUsageSums(Long userIdx, String fromDt, String toDt) {

		log.info(">>>>> getUsageSums.start");

		try {

			List<SaasTrackerDto.UsageSumsRes> usageSumsRes = new ArrayList<>();
			List<SaasPaymentHistoryRepository.UsageSumsDto> usageSums =
					repoSaasPaymentHistory.getUsageSums(userIdx, fromDt + "01", toDt + "31");
			Map<String, Long> usageSumsMap = usageSums.stream().collect(Collectors.toMap(SaasPaymentHistoryRepository.UsageSumsDto::getPDate, SaasPaymentHistoryRepository.UsageSumsDto::getPSum));

			long tempPrevPSum = 0L;
			toDt = CommonUtil.addMonths(toDt, 1);
			while(!fromDt.equals(toDt)) {
				SaasTrackerDto.UsageSumsRes tempUsageSum = new SaasTrackerDto.UsageSumsRes();
				tempUsageSum.setPdate(fromDt);
				tempUsageSum.setPsum(ObjectUtils.isEmpty(usageSumsMap.get(fromDt)) ? 0 : usageSumsMap.get(fromDt));
				usageSumsRes.add(tempUsageSum);

				fromDt = CommonUtil.addMonths(fromDt, 1);
			}
			log.info(">>>>> getUsageSums.complete");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(usageSumsRes).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUsageSumsDetails(Long userIdx, String searchDt) {

		log.info(">>>>> getUsageSumsDetails.start");

		try {
			List<SaasPaymentHistoryRepository.UsageSumsDetailsDto> usageSumsDetails =
					repoSaasPaymentHistory.getUsageSumsDetails(userIdx, searchDt + "01", searchDt + "31");
			log.info(">>>>> getUsageSumsDetails.complete");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(usageSumsDetails).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUsage(Long userIdx, String searchDt) {

		log.info(">>>>> getUsage.start");
		User user = userService.getUser(userIdx);

		try {
			List<SaasTrackerDto.UsageRes> usageList =
					repoSaasPaymentHistory.findAllByUserAndPaymentDateBetweenOrderByPaymentDateDesc(user, searchDt + "01", searchDt + "31")
							.stream().map(SaasTrackerDto.UsageRes::from).collect(Collectors.toList());

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(usageList).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUsageCategories(Long userIdx, String fromDt, String toDt) {

		log.info(">>>>> getUsageCategories.start");

		try {
			List<SaasPaymentHistoryRepository.UsageCategoriesDto> usageCategories =
					repoSaasPaymentHistory.getUsageCategories(userIdx, fromDt + "01", toDt + "31");

			log.info(">>>>> getUsageCategories.complete");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(usageCategories).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUsageCategoriesDetails(Long userIdx, Long saasCategoryIdx, String fromDt, String toDt) {

		log.info(">>>>> getUsageCategoriesDetails.start");

		List<SaasTrackerDto.UsageCategoriesDetailsRes> usageCategoriesDetails = new ArrayList<>();

		try {
			for(int i = 0; i <= CommonUtil.subtractMonth(fromDt, toDt); i++) {

				String tempMonth = i == 0 ? fromDt : CommonUtil.addMonths(fromDt, i);

				SaasTrackerDto.UsageCategoriesDetailsRes categoriesDetail = new SaasTrackerDto.UsageCategoriesDetailsRes();
				categoriesDetail.setPdate(tempMonth);
				categoriesDetail.setListOfCategories(repoSaasPaymentHistory.getUsageCategoriesDetails(userIdx, saasCategoryIdx, tempMonth + "01", tempMonth + "31"));
				usageCategoriesDetails.add(categoriesDetail);
			}

			log.info(">>>>> getUsageCategoriesDetails.complete");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(usageCategoriesDetails).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUseSaasByCategory(Long userIdx) {

		log.info(">>>>> getUseSaasByCategory.start");

		List<SaasTrackerDto.SaasCategoriesRes> saasCategoriesRes = new ArrayList<>();
		List<SaasCategory> saasCategories = repoSaasCategory.findAll();

		try {
			saasCategories.forEach(category -> {

				SaasTrackerDto.SaasCategoriesRes saasCategory = new SaasTrackerDto.SaasCategoriesRes();
				saasCategory.setIdxSaasCategory(category.idx());
				saasCategory.setCategoryName(category.name());
				saasCategory.setListOfSaas(repoSaasCategory.getUseSaasByCategoryId(userIdx, category.idx()));

				saasCategoriesRes.add(saasCategory);
			});

			log.info(">>>>> getUseSaasByCategory.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(saasCategoriesRes).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUseSaasList(Long userIdx) {

		log.info(">>>>> getUseSaasList.start");
		User user = userService.getUser(userIdx);

		try {

			SaasTrackerDto.UseSaasListRes useSaasRes = new SaasTrackerDto.UseSaasListRes();

			List<SaasTrackerDto.UseSaasRes> subscriptionList = new ArrayList<>();
			for(SaasPaymentInfoRepository.SubscriptSaasDto dto : repoSaasPaymentInfo.findAllSubscriptionByUser(user.idx())) {
				SaasTrackerDto.UseSaasRes res = SaasTrackerDto.UseSaasRes.from(dto);
				res.setPaymentTypeList(repoSaasPaymentInfo.findPaymentType(userIdx, dto.getIdxSaasInfo(), true));
				subscriptionList.add(res);
			}
			useSaasRes.setSubscriptionList(subscriptionList);

			List<SaasTrackerDto.UseSaasRes> unsubscriptionList = new ArrayList<>();
			for(SaasPaymentInfoRepository.SubscriptSaasDto dto : repoSaasPaymentInfo.findAllUnsubscriptionByUser(user.idx())) {
				SaasTrackerDto.UseSaasRes res = SaasTrackerDto.UseSaasRes.from(dto);
				res.setPaymentTypeList(repoSaasPaymentInfo.findPaymentType(userIdx, dto.getIdxSaasInfo(), false));
				unsubscriptionList.add(res);
			}
			useSaasRes.setUnsubscriptionList(unsubscriptionList);

			log.info(">>>>> getUseSaasList.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(useSaasRes).build()
			);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity updateSaasInfo(Long userIdx, Long idxSaasInfo, SaasTrackerDto.UpdateSaasInfoReq dto) {

		log.info(">>>>> updateSaasInfo.start");

		try {

			User user = userService.getUser(userIdx);
			SaasInfo saasInfo = findSaasInfo(idxSaasInfo);

			repoSaasPaymentInfo.findAllByUserAndSaasInfo(user, saasInfo).forEach(paymentInfo -> {
				if(!StringUtils.isEmpty(dto.getManagerName())) paymentInfo.saasPaymentManageInfo().managerName(dto.getManagerName());
				if(!StringUtils.isEmpty(dto.getManagerEmail())) paymentInfo.saasPaymentManageInfo().managerEmail(dto.getManagerEmail());
				if(!ObjectUtils.isEmpty(dto.getActiveSubscription())) paymentInfo.activeSubscription(dto.getActiveSubscription());
				if(!ObjectUtils.isEmpty(dto.getActiveAlert())) paymentInfo.saasPaymentManageInfo().activeAlert(dto.getActiveAlert());
			});

			log.info(">>>>> updateSaasInfo.complete");
			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(true)
							.build())
					.build());
		}catch(EntityNotFoundException enfe) {
			log.error(enfe.getMessage(), enfe);
			throw new SystemException(ErrorCode.Api.NOT_FOUND);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentSchedulesAtCalendar(Long userIdx) {

		log.info(">>>>> getSaasPaymentSchedulesAtCalendar.start");
		User user = userService.getUser(userIdx);

		try {

			List<SaasTrackerDto.SaasPaymentScheduleAtCalendarListRes> calendarRes =
					repoSaasPaymentInfo.findAllByUserAndActiveSubscriptionIsTrue(user).stream()
							.map(SaasTrackerDto.SaasPaymentScheduleAtCalendarListRes::from).filter(p -> !StringUtils.isEmpty(p.getPaymentDate()))
							.collect(Collectors.toList());

			List<SaasTrackerDto.SaasPaymentScheduleAtCalendarListRes> scheduleRes =
					repoSaasPaymentInfo.findAllByUserScheduleList(user.idx()).stream()
							.map(SaasTrackerDto.SaasPaymentScheduleAtCalendarListRes::from).collect(Collectors.toList());

			SaasTrackerDto.SaasPaymentScheduleAtCalendarRes saasPaymentSchedulesAtCalendarRes = new SaasTrackerDto.SaasPaymentScheduleAtCalendarRes();
			saasPaymentSchedulesAtCalendarRes.setCalendarList(calendarRes);
			saasPaymentSchedulesAtCalendarRes.setScheduleList(scheduleRes);

			log.info(">>>>> getSaasPaymentSchedulesAtCalendar.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(saasPaymentSchedulesAtCalendarRes).build()
			);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentSchedules(Long userIdx) {

		log.info(">>>>> getSaasPaymentSchedules.start");
		User user = userService.getUser(userIdx);

		String dateStr = CommonUtil.getNowYYYYMMDD();
		SaasTrackerDto.SaasPaymentScheduleRes scheduleRes = new SaasTrackerDto.SaasPaymentScheduleRes();

		try {
			List<SaasPaymentInfo> paymentInfos = repoSaasPaymentInfo.findAllByUserAndActiveSubscriptionIsTrueOrderByPaymentScheduleDateAsc(user);

			// 1. 정기 결제 목록
			scheduleRes.setRegularList(paymentInfos.stream()
					.map(SaasTrackerDto.SaasPaymentScheduleDetailRes::from)
					.filter(p -> ((p.getPaymentType() == 1 || p.getPaymentType() == 2)) && !StringUtils.isEmpty(p.getPaymentScheduleDate()) && Integer.parseInt(p.getPaymentScheduleDate()) >= Integer.parseInt(dateStr))
					.collect(Collectors.toList()));

			// 2. 비정기 결제 목록
			scheduleRes.setIrregularList(paymentInfos.stream()
					.map(SaasTrackerDto.SaasPaymentScheduleDetailRes::from)
					.filter(p -> p.getPaymentType() == 4)
					.collect(Collectors.toList()));

			// 3. 미분류 목록
			scheduleRes.setUnclassifiedList(paymentInfos.stream()
					.map(SaasTrackerDto.SaasPaymentScheduleDetailRes::from)
					.filter(p -> p.getPaymentType() == 0)
					.collect(Collectors.toList()));

			log.info(">>>>> getSaasPaymentSchedules.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(scheduleRes).build()
			);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentDetailInfo(Long userIdx, Long saasInfoIdx) {

		log.info(">>>>> getSaasPaymentDetailInfo.start");

		// 1. SsssPaymentInfo에서 UserIdx, SaaSInfoIdx로 해당하는 데이터 조회
		User user = userService.getUser(userIdx);
		SaasInfo saasInfo = findSaasInfo(saasInfoIdx);
		List<SaasPaymentInfo> saasPaymentInfos = repoSaasPaymentInfo.findAllByUserAndSaasInfo(user, saasInfo);

		try {

			// 2. 1에서 조회한 데이터로 기본 정보 및 결제 수단 목록까지 세팅
			boolean hasSaasPaymentMangeInfo = !ObjectUtils.isEmpty(saasPaymentInfos.get(0).saasPaymentManageInfo());
			SaasTrackerDto.SaasPaymentDetailInfoRes saasPaymentDetailInfoRes = new SaasTrackerDto.SaasPaymentDetailInfoRes();
			saasPaymentDetailInfoRes.setIdxSaasInfo(saasInfo.idx());
			saasPaymentDetailInfoRes.setSaasName(saasInfo.name());
			saasPaymentDetailInfoRes.setActiveSubscription(repoSaasPaymentInfo.findSaasSubscriptionByUserAndSaasInfo(userIdx, saasInfoIdx) > 0 ? true : false);
			saasPaymentDetailInfoRes.setManagerName(hasSaasPaymentMangeInfo ? saasPaymentInfos.get(0).saasPaymentManageInfo().managerName() : null);
			saasPaymentDetailInfoRes.setManagerEmail(hasSaasPaymentMangeInfo? saasPaymentInfos.get(0).saasPaymentManageInfo().managerEmail() : null);
			saasPaymentDetailInfoRes.setActiveAlert(hasSaasPaymentMangeInfo ? saasPaymentInfos.get(0).saasPaymentManageInfo().activeAlert() : null);
			saasPaymentDetailInfoRes.setSaasPaymentInfos(saasPaymentInfos.stream().map(SaasTrackerDto.SaasPaymentInfoRes::from).collect(Collectors.toList()));

			// 3. 결제 내역은 saasPaymentHistory에서 useridx, saasInfoIdx로 조회
			saasPaymentDetailInfoRes.setSaasPaymentHistories(repoSaasPaymentHistory.findAllByUserAndSaasInfoOrderByPaymentDateDesc(user, saasInfo).stream().map(SaasTrackerDto.SaasPaymentHistoryRes::from).collect(Collectors.toList()));

			// 4. 6개월 추이 차트 조회(saasPaymentHistory에서 userIdx, saasInfoIdx로 통계)
			String toDt = CommonUtil.getNowYYYYMM();
			String fromDt = CommonUtil.addMonths(toDt, -5);
			saasPaymentDetailInfoRes.setListOfSums(repoSaasPaymentHistory.getUsageSumsBySaasInfoIdx(userIdx, saasInfoIdx, fromDt + "01", toDt + "31"));

			log.info(">>>>> getSaasPaymentDetailInfo.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(saasPaymentDetailInfoRes).build()
			);

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getInsights(Long userIdx) {

		log.info(">>>>> getInsights.start");

		User user = userService.getUser(userIdx);
		SaasTrackerDto.SaaSInsightsRes insightsRes = new SaasTrackerDto.SaaSInsightsRes();

		try {

			// 1. 새로운 SaaS
			insightsRes.setNewSaasList(repoSaasPaymentHistory.findTop5ByUserIsNew(user.idx()).stream().map(SaasTrackerDto.SaasNewTop5Res::from).collect(Collectors.toList()));

			// 2. 월결제 증가 SaaS
			insightsRes.setBestPaymentTop5List(repoSaasPaymentHistory.getBestPaymentTop5(userIdx).stream().map(SaasTrackerDto.SaasMaxTop5Res::from).collect(Collectors.toList()));

			// 3. 중복결제 의심 SaaS
			insightsRes.setDuplicatePaymentList(repoSaasPaymentHistory.getDuplicatePaymentList(userIdx).stream().map(SaasTrackerDto.SaasDuplicatePaymentRes::from).collect(Collectors.toList()));

			log.info(">>>>> getInsights.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(insightsRes).build()
			);

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity updateSaasInsightAtDuplicatePayment(Long userIdx, Long idxSaasInfo) {

		log.info(">>>>> updateSaasInsightAtDuplicatePayment.start");

		try {

			User user = userService.getUser(userIdx);
			SaasInfo saasInfo = findSaasInfo(idxSaasInfo);

			repoSaasPaymentInfo.findAllByUserAndSaasInfo(user, saasInfo).forEach(paymentInfo -> paymentInfo.isDup(false));

			log.info(">>>>> updateSaasInsightAtDuplicatePayment.complete");
			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(true)
							.build())
					.build());
		}catch(EntityNotFoundException enfe) {
			log.error(enfe.getMessage(), enfe);
			throw new SystemException(ErrorCode.Api.NOT_FOUND);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	private void sendSlackNotification(Long userIdx) {
		this.sendSlackNotification(userIdx, getSlackSaasTrackerMessage(corpService.getCorpByUserIdx(userIdx)));
	}

	private void sendSlackNotification(Long userIdx, SaasTrackerDto.SaasTrackerReportsReq dto) {
		this.sendSlackNotification(userIdx, getSlackSaasTrackerMessage(corpService.getCorpByUserIdx(userIdx), dto));
	}

	private void sendSlackNotification(Long userIdx, String slackMessage) {
		slackNotiService.sendSlackNotification(slackMessage, slackNotiService.getSlackSaasTrackerUrl());
	}

	private boolean isMonthlyUse(List<SaasPaymentInfo> saasPaymentInfos) {
		for(int i=0; i<saasPaymentInfos.size(); i++) {
			if(saasPaymentInfos.get(i).paymentType() == 1)
				return true;
		}
		return false;
	}

	SaasPaymentInfo findSaasPaymentInfo(Long idx) {
		return repoSaasPaymentInfo.findById(idx).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("SaasPaymentInfo")
						.idx(idx)
						.build()
		);
	}

	SaasInfo findSaasInfo(Long idx) {
		return repoSaasInfo.findById(idx).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("SaasInfo")
						.idx(idx)
						.build()
		);
	}

	SaasTrackerProgress findSaasTrackerProgress(User user) {
		return repoSaasTrackerProgress.findByUser(user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("SaasTrackerProgress")
						.idx(user.idx())
						.build()
		);
	}
}