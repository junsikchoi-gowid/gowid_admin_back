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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto.SaasTrackerNotiReq.getSlackSaasTrackerMessage;
import static com.nomadconnection.dapp.api.dto.Notification.SlackNotiDto.SaasTrackerNotiReq.getSlackSaasTrackerUsageRequestMessage;

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
	private final SaasPaymentManageInfoRepository repoSaasPaymentManageInfo;

	private final CorpService corpService;
	private final UserService userService;
	private final SlackNotiService slackNotiService;

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveUsageRequest(SaasTrackerDto.SaasTrackerUsageReq req) {

		log.info(">>>>> saveUsageRequest.start");

		try {
			// 제보 Slack Notification
			this.sendSlackNotificationForUsageRequest(req);

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
			this.sendSlackNotificationForWrongInformation(userIdx, dto);

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

		if(Integer.parseInt(fromDt) > Integer.parseInt(toDt)) {
			log.error("'$toDt' must be greater than '$fromDt'...");
			throw new SystemException(ErrorCode.Api.VALIDATION_FAILED);
		}

		try {

			SaasTrackerDto.UsageSumsRes usageSumsRes = new SaasTrackerDto.UsageSumsRes();

			List<SaasTrackerDto.UsageSumsByPaymentRes> usageSumsByPaymentRes = new ArrayList<>();
			List<SaasTrackerDto.UsageSumsByPaymentRes> usageSumsByForecastList = new ArrayList<>();

			// 1. 월별 결제 금액
			List<SaasPaymentHistoryRepository.UsageSumsDto> usageSums =
				repoSaasPaymentHistory.getUsageSums(userIdx, fromDt + "01", toDt + "31");
			if(!ObjectUtils.isEmpty(usageSums)) {
				Map<String, Long> usageSumsMap = usageSums.stream().collect(Collectors.toMap(SaasPaymentHistoryRepository.UsageSumsDto::getPDate, SaasPaymentHistoryRepository.UsageSumsDto::getPSum));

				String tempToDt = CommonUtil.addMonths(toDt, 1);
				while(!fromDt.equals(tempToDt)) {
					SaasTrackerDto.UsageSumsByPaymentRes tempUsageSum = new SaasTrackerDto.UsageSumsByPaymentRes();
					tempUsageSum.setPdate(fromDt);
					tempUsageSum.setPsum(ObjectUtils.isEmpty(usageSumsMap.get(fromDt)) ? 0 : usageSumsMap.get(fromDt));
					usageSumsByPaymentRes.add(tempUsageSum);

					fromDt = CommonUtil.addMonths(fromDt, 1);
				}

				// 2. 예상 결제 금액(toDt, toDt+1) total :2
				usageSumsByForecastList.add(this.getForecastPaymentAtMonth(userIdx, toDt, true));
				usageSumsByForecastList.add(this.getForecastPaymentAtMonth(userIdx, CommonUtil.addMonths(toDt, 1), false));
			}
			usageSumsRes.setPaymentList(usageSumsByPaymentRes);
			usageSumsRes.setForecastList(usageSumsByForecastList);

			log.info(">>>>> getUsageSums.complete");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(usageSumsRes).build());

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	// 예상 결제 금액(toDt, toDt+1) total :2

	/**
	 * 예상 결제 금액 조회
	 * 	- 1. 최근 3개월 (toDt-3, toDt-2, toDt-1)의 모든 지출금액 / 3
	 * 	- 2. toDt에 예상 결제일이 있는 분기결제, 연결제
	 * 	- 3. 1, 2의 합
	 *
	 * @param idxUser
	 * @param dateYYYYMM
	 * @return
	 */
	private SaasTrackerDto.UsageSumsByPaymentRes getForecastPaymentAtMonth(Long idxUser, String dateYYYYMM, boolean isCurrent) {

		Long forecastPaymentAvg = this.getSaasUsageAvgAtMonth(idxUser, dateYYYYMM, isCurrent);
		Long scheduledPaymentAtMonth = repoSaasPaymentInfo.getScheduledPriceSumAtMonth(idxUser, dateYYYYMM + "01", dateYYYYMM + "31");

		return SaasTrackerDto.UsageSumsByPaymentRes.builder().pdate(dateYYYYMM).psum(forecastPaymentAvg + (ObjectUtils.isEmpty(scheduledPaymentAtMonth) ? 0L : scheduledPaymentAtMonth)).build();
	}

	private Long getSaasUsageAvgAtMonth(Long idxUser, String dateYYYYMM, boolean isCurrent) {
		try {
			String searchFromDate = isCurrent ? CommonUtil.addMonths(dateYYYYMM, -3) : CommonUtil.addMonths(dateYYYYMM, -4);
			String searchToDate = isCurrent ? CommonUtil.addMonths(dateYYYYMM, -1) : CommonUtil.addMonths(dateYYYYMM, -2);
			return repoSaasPaymentHistory.getUsageAvgAtMonth(idxUser, searchFromDate, searchToDate);
		}catch(ParseException pe) {
			log.error(pe.getMessage(), pe);
			return 0L;
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
				saasCategory.setListOfSaas(getListOfSaasInCategory(userIdx, category.idx()));
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

	private List<SaasTrackerDto.UseSaasInfoInCategoryRes> getListOfSaasInCategory(Long userIdx, Long categoryIdx) {
		List<SaasTrackerDto.UseSaasInfoInCategoryRes> listOfSaas = new ArrayList<>();
		for(SaasCategoryRepository.UseSaasByCategoryDto dto : repoSaasCategory.getUseSaasByCategoryId(userIdx, categoryIdx)) {
			SaasTrackerDto.UseSaasInfoInCategoryRes res = SaasTrackerDto.UseSaasInfoInCategoryRes.from(dto);
			res.setPaymentTypeList(repoSaasPaymentInfo.findPaymentType(userIdx, dto.getIdxSaasInfo(), true));
			listOfSaas.add(res);
		}
		return listOfSaas;
	}


	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getUseSaasList(Long userIdx) {

		log.info(">>>>> getUseSaasList.start");
		User user = userService.getUser(userIdx);

		try {

			SaasTrackerDto.UseSaasListRes useSaasRes = new SaasTrackerDto.UseSaasListRes();

			List<SaasTrackerDto.UseSaasRes> subscriptionList = new ArrayList<>();
			List<SaasTrackerDto.SaasMaxTop5Res> paymentRate = repoSaasPaymentHistory.getBestPaymentTop5(userIdx).stream().map(SaasTrackerDto.SaasMaxTop5Res::from).collect(Collectors.toList());
			String nowYYYYMM = CommonUtil.addMonths(CommonUtil.getNowYYYYMM(), -1);

			for(SaasPaymentInfoRepository.SubscriptSaasDto dto : repoSaasPaymentInfo.findAllSubscriptionByUser(user.idx(), nowYYYYMM + "01", nowYYYYMM + "31")) {
				SaasTrackerDto.UseSaasRes res = SaasTrackerDto.UseSaasRes.from(dto);
				res.setPaymentTypeList(repoSaasPaymentInfo.findPaymentType(userIdx, dto.getIdxSaasInfo(), true));
				if(paymentRate.stream().filter(s -> s.getIdxSaasInfo().equals(res.getIdxSaasInfo())).findFirst().isPresent()) {
					res.setMom(paymentRate.stream().filter(s -> s.getIdxSaasInfo().equals(res.getIdxSaasInfo())).findFirst().get().getMom());
				}
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

			List<SaasPaymentInfo> infos = repoSaasPaymentInfo.findAllByUserAndSaasInfo(user, saasInfo);
			if(ObjectUtils.isEmpty(infos.get(0).saasPaymentManageInfo())) {
				SaasPaymentManageInfo manageInfo = new SaasPaymentManageInfo();
				manageInfo.managerName(dto.getManagerName());
				manageInfo.managerEmail(dto.getManagerEmail());
				repoSaasPaymentManageInfo.save(manageInfo);
				infos.forEach(paymentInfo -> paymentInfo.saasPaymentManageInfo(manageInfo));
			}else {
				SaasPaymentManageInfo manageInfo = infos.get(0).saasPaymentManageInfo();
				manageInfo.managerName(dto.getManagerName());
				manageInfo.managerEmail(dto.getManagerEmail());
			}
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

			List<SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes> currentPaymentRes =
					repoSaasPaymentHistory.findTop5ByUserOrderByPaymentDateDesc(user).stream()
							.map(SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes::from).collect(Collectors.toList());
			for(SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes res: currentPaymentRes) {
				res.setIsTerminateRequired(isTermiateRequired(userIdx, res.getIdxSaasInfo(), res.getOrganizationCode(), res.getAccountNumber(), res.getCardNumber(), res.getCurrentPaymentDate()));
			}

			SaasTrackerDto.SaasPaymentScheduleAtCalendarRes saasPaymentSchedulesAtCalendarRes = new SaasTrackerDto.SaasPaymentScheduleAtCalendarRes();
			saasPaymentSchedulesAtCalendarRes.setCalendarList(calendarRes);
			saasPaymentSchedulesAtCalendarRes.setScheduleList(scheduleRes);
			saasPaymentSchedulesAtCalendarRes.setCurrentPaymentList(currentPaymentRes);

			log.info(">>>>> getSaasPaymentSchedulesAtCalendar.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(saasPaymentSchedulesAtCalendarRes).build()
			);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	private Boolean isTermiateRequired(Long userIdx, Long idxSaasInfo, String organizationCode, String accountNumber, String cardNumber, String paymentDate) {
		User user = userService.getUser(userIdx);
		SaasInfo saasInfo = findSaasInfo(idxSaasInfo);
		accountNumber = StringUtils.isEmpty(accountNumber) ? "" : accountNumber;
		cardNumber = StringUtils.isEmpty(cardNumber) ? "" : cardNumber;
		Optional<SaasPaymentInfo> paymentInfo = repoSaasPaymentInfo.findByUserAndSaasInfoAndOrganizationAndAccountNumberAndCardNumberContains(user, saasInfo, organizationCode, accountNumber, cardNumber);
		if(paymentInfo.isPresent() && !StringUtils.isEmpty(paymentInfo.get().expirationDate())) {
			return Integer.parseInt(paymentDate) >= Integer.parseInt(paymentInfo.get().expirationDate());
		}
		return false;
	}

	private SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes isTermiateRequired2(Long userIdx, SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes res) {
		User user = userService.getUser(userIdx);
		SaasInfo saasInfo = findSaasInfo(res.getIdxSaasInfo());

		String accountNumber = res.getAccountNumber();
		String cardNumber = res.getCardNumber();

		accountNumber = StringUtils.isEmpty(accountNumber) ? "" : accountNumber;
		cardNumber = StringUtils.isEmpty(cardNumber) ? "" : cardNumber;

		Optional<SaasPaymentInfo> paymentInfo = repoSaasPaymentInfo.findByUserAndSaasInfoAndOrganizationAndAccountNumberAndCardNumberContains(user, saasInfo, res.getOrganizationCode(), accountNumber, cardNumber);
		if(paymentInfo.isPresent()) {
			res.setPaymentType(paymentInfo.get().paymentType());
			if(!ObjectUtils.isEmpty(paymentInfo.get().saasPaymentManageInfo())) {
				res.setManagerName(paymentInfo.get().saasPaymentManageInfo().managerName());
				res.setManagerEmail(paymentInfo.get().saasPaymentManageInfo().managerEmail());
			}
			if(!StringUtils.isEmpty(paymentInfo.get().expirationDate())) {
				res.setIsTerminateRequired(Integer.parseInt(res.getCurrentPaymentDate()) >= Integer.parseInt(paymentInfo.get().expirationDate()));
			}
		}
		return res;
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentSchedules(Long userIdx) {

		log.info(">>>>> getSaasPaymentSchedules.start");
		User user = userService.getUser(userIdx);

		String dateStr = CommonUtil.getNowYYYYMMDD();
		SaasTrackerDto.SaasPaymentScheduleRes scheduleRes = new SaasTrackerDto.SaasPaymentScheduleRes();

		try {
			String searchFromDate = CommonUtil.getMinusDay(CommonUtil.getNowYYYYMMDD(), 60);
			String searchEndDate = CommonUtil.getNowYYYYMMDD();

			List<SaasPaymentInfo> paymentInfos = repoSaasPaymentInfo.findAllByUserAndActiveSubscriptionIsTrueOrderByPaymentScheduleDateAsc(user);
			List<SaasPaymentHistory> paymentHistories = repoSaasPaymentHistory.findAllByUserAndPaymentDateBetweenOrderByPaymentDateDesc(user, searchFromDate, searchEndDate);

			// 1. 정기 결제 목록
			List<SaasTrackerDto.SaasPaymentScheduleDetailRes> regularList = paymentInfos.stream()
																			.map(SaasTrackerDto.SaasPaymentScheduleDetailRes::from)
																			.filter(p -> ((p.getPaymentType() == 1 || p.getPaymentType() == 2)) && !StringUtils.isEmpty(p.getPaymentScheduleDate()) && Integer.parseInt(p.getPaymentScheduleDate()) >= Integer.parseInt(dateStr))
																			.collect(Collectors.toList());
			scheduleRes.setRegularList(regularList);

			// 2. 최근 결제 목록
			List<SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes> currentPaymentList = paymentHistories.stream()
								.map(SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes::from)
								.collect(Collectors.toList());
			for(SaasTrackerDto.SaasCurrentPaymentAtCalendarListRes res: currentPaymentList) {
				res = this.isTermiateRequired2(userIdx, res);
//				res.setIsTerminateRequired(isTermiateRequired(userIdx, res.getIdxSaasInfo(), res.getOrganizationCode(), res.getAccountNumber(), res.getCardNumber(), res.getCurrentPaymentDate()));
			}
			scheduleRes.setCurrentPaymentList(currentPaymentList);

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
		List<SaasPaymentInfo> saasPaymentInfos = repoSaasPaymentInfo.findAllByUserAndSaasInfoOrderByDisabledAscCurrentPaymentDateDesc(user, saasInfo);




		try {

			// 2. 1에서 조회한 데이터로 기본 정보 및 결제 수단 목록까지 세팅
			boolean hasSaasPaymentMangeInfo = !ObjectUtils.isEmpty(saasPaymentInfos.get(0).saasPaymentManageInfo());
			SaasTrackerDto.SaasPaymentDetailInfoRes saasPaymentDetailInfoRes = new SaasTrackerDto.SaasPaymentDetailInfoRes();
			saasPaymentDetailInfoRes.setIdxSaasInfo(saasInfo.idx());
			saasPaymentDetailInfoRes.setSaasName(saasInfo.name());
			saasPaymentDetailInfoRes.setSaasImageName(saasInfo.imageName());
			saasPaymentDetailInfoRes.setCategoryName(saasInfo.saasCategory().name());
			saasPaymentDetailInfoRes.setHomepageUrl(saasInfo.homepageUrl());
			saasPaymentDetailInfoRes.setPriceUrl(saasInfo.priceUrl());
			saasPaymentDetailInfoRes.setSaasDesc(saasInfo.description());
			saasPaymentDetailInfoRes.setManagerName(hasSaasPaymentMangeInfo ? saasPaymentInfos.get(0).saasPaymentManageInfo().managerName() : null);
			saasPaymentDetailInfoRes.setManagerEmail(hasSaasPaymentMangeInfo? saasPaymentInfos.get(0).saasPaymentManageInfo().managerEmail() : null);
			saasPaymentDetailInfoRes.setSaasPaymentInfos(saasPaymentInfos.stream().map(SaasTrackerDto.SaasPaymentInfoRes::from).collect(Collectors.toList()));

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
	public ResponseEntity getSaasPaymentDetailHistories(Long idxUser, Long idxSaasInfo, Long idxSaasPaymentInfo, String fromDt, String toDt) {

		log.info(">>>>> getSaasPaymentDetailHistories.start");

		User user = userService.getUser(idxUser);
		SaasInfo saasInfo = findSaasInfo(idxSaasInfo);
		SaasPaymentInfo saasPaymentInfo = ObjectUtils.isEmpty(idxSaasPaymentInfo) ? null : findSaasPaymentInfo(idxSaasPaymentInfo);

		try {
			List<SaasTrackerDto.SaasPaymentHistoryRes> paymentHistoies =
					repoSaasPaymentHistory.findAllByUserAndSaasInfoAndPaymentDateBetweenOrderByPaymentDateDesc(user, saasInfo, fromDt, toDt).stream()
							.filter(p -> {
								if(ObjectUtils.isEmpty(saasPaymentInfo)) {
									return true;
								}else {
									return p.organization().equals(saasPaymentInfo.organization()) &&
										(StringUtils.isEmpty(p.cardNumber()) ? p.accountNumber().equals(saasPaymentInfo.accountNumber()) : p.cardNumber().equals(saasPaymentInfo.cardNumber()));
								}
							})
							.map(SaasTrackerDto.SaasPaymentHistoryRes::from).collect(Collectors.toList());

			log.info(">>>>> getSaasPaymentDetailHistories.complete");
			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(paymentHistoies).build()
			);

		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw new SystemException(ErrorCode.Api.INTERNAL_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasPaymentDetailCharts(Long userIdx, Long saasInfoIdx, Long idxSaasPaymentInfo) {

		log.info(">>>>> getSaasPaymentDetailCharts.start");

		// 1. SsssPaymentInfo에서 UserIdx, SaaSInfoIdx로 해당하는 데이터 조회
		User user = userService.getUser(userIdx);
		SaasInfo saasInfo = findSaasInfo(saasInfoIdx);
		SaasPaymentInfo saasPaymentInfo = ObjectUtils.isEmpty(idxSaasPaymentInfo) ? null : findSaasPaymentInfo(idxSaasPaymentInfo);

		try {

			String fromDt = CommonUtil.addMonths(CommonUtil.getNowYYYYMM(), -11); // 1년 전(현재 3월이면.. 작년 4월)
			String toDt = CommonUtil.getNowYYYYMM(); // 현재 달(현재 3월이면.. 3월)

			List<SaasPaymentHistoryRepository.UsageSumsDto> usageSums = (ObjectUtils.isEmpty(saasPaymentInfo)
					? repoSaasPaymentHistory.getUsageSumsBySaasInfoIdxAll(userIdx, saasInfoIdx, fromDt + "01", toDt + "31")
					: repoSaasPaymentHistory.getUsageSumsBySaasInfoIdx(userIdx, saasInfoIdx, fromDt + "01", toDt + "31",
																		saasPaymentInfo.organization(),
																		StringUtils.isEmpty(saasPaymentInfo.accountNumber()) ? "" : saasPaymentInfo.accountNumber(),
																		StringUtils.isEmpty(saasPaymentInfo.cardNumber()) ? "" : saasPaymentInfo.cardNumber()));

			List<SaasTrackerDto.UsageSumsByPaymentRes> usageSumsByPaymentList = new ArrayList<>();
			for(int i = 11; i >= 0; i--) {

				String tempMonth = CommonUtil.addMonths(toDt, -i);

				SaasTrackerDto.UsageSumsByPaymentRes res = new SaasTrackerDto.UsageSumsByPaymentRes();
				Optional<SaasPaymentHistoryRepository.UsageSumsDto> tempMonthDto = usageSums.stream().filter(f -> f.getPDate().equals(tempMonth)).findFirst();
				res.setPdate(tempMonth);
				res.setPsum(tempMonthDto.isPresent() ? tempMonthDto.get().getPSum() : 0L);
				res.setMom(getPaymentMomAtMonth(userIdx, saasInfoIdx, saasPaymentInfo, tempMonth));
				usageSumsByPaymentList.add(res);
			}

			log.info(">>>>> getUsageCategoriesDetails.complete");

			return ResponseEntity.ok().body(
					BusinessResponse.builder().data(usageSumsByPaymentList).build());

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
			insightsRes.setBestPaymentTop5List(repoSaasPaymentHistory.getBestPaymentTop5(userIdx).stream().sorted(Comparator.comparing(SaasPaymentHistoryRepository.BestPaymentTop5Dto::getbSum).reversed()).limit(5).map(SaasTrackerDto.SaasMaxTop5Res::from).collect(Collectors.toList()));

			// 3. 증가율 기준 Top 5 SaaS
			insightsRes.setMomSortList(repoSaasPaymentHistory.getBestPaymentTop5(userIdx).stream().filter(f -> !ObjectUtils.isEmpty(f.getMom())).sorted(Comparator.comparing(SaasPaymentHistoryRepository.BestPaymentTop5Dto::getMom).reversed()).limit(5).map(SaasTrackerDto.SaasMaxTop5Res::from).collect(Collectors.toList()));

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

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity updateSaasPaymentInfo(Long userIdx, Long idxSaasPaymentInfo, SaasTrackerDto.UpdateSaasPaymentInfoReq req) {

		try {

			SaasPaymentInfo paymentInfo = this.findSaasPaymentInfo(idxSaasPaymentInfo);
			if(!ObjectUtils.isEmpty(req.getPaymentType())) paymentInfo.paymentType(req.getPaymentType());
			paymentInfo.expirationDate(req.getExpirationDate());
			paymentInfo.memo(req.getMemo());
			if(!ObjectUtils.isEmpty(req.getDisabled())) paymentInfo.disabled(req.getDisabled());

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

	private void sendSlackNotification(Long userIdx) {
		this.sendSlackNotification(getSlackSaasTrackerMessage(corpService.getCorpByUserIdx(userIdx)));
	}

	private void sendSlackNotificationForWrongInformation(Long userIdx, SaasTrackerDto.SaasTrackerReportsReq req) {
		this.sendSlackNotification(getSlackSaasTrackerMessage(corpService.getCorpByUserIdx(userIdx), req));
	}

	private void sendSlackNotificationForUsageRequest(SaasTrackerDto.SaasTrackerUsageReq req) {
		this.sendSlackNotification(getSlackSaasTrackerUsageRequestMessage(req));
	}

	private void sendSlackNotification(String slackMessage) {
		slackNotiService.sendSlackNotification(slackMessage, slackNotiService.getSlackSaasTrackerUrl());
	}

	private boolean isMonthlyUse(List<SaasPaymentInfo> saasPaymentInfos) {
		for(int i=0; i<saasPaymentInfos.size(); i++) {
			if(saasPaymentInfos.get(i).paymentType() == 1)
				return true;
		}
		return false;
	}

	private String getPaymentMomAtMonth(Long userIdx, Long saasInfoIdx, SaasPaymentInfo saasPaymentInfo, String date) throws ParseException {
		return (ObjectUtils.isEmpty(saasPaymentInfo) ?
				repoSaasPaymentHistory.getPaymentMomAtMonthAll(userIdx, saasInfoIdx, CommonUtil.addMonths(date, -1), date) :
				repoSaasPaymentHistory.getPaymentMomAtMonth(userIdx, saasInfoIdx, CommonUtil.addMonths(date, -1), date, saasPaymentInfo.organization(), saasPaymentInfo.accountNumber(), saasPaymentInfo.cardNumber()));
	}

	SaasPaymentInfo findSaasPaymentInfo(Long idx) {
		return repoSaasPaymentInfo.findById(idx).orElseThrow(
				() -> EntityNotFoundException.builder()
						.message("idxSaasPaymentInfo is not found.")
						.entity("SaasPaymentInfo")
						.idx(idx)
						.build()
		);
	}

	SaasPaymentHistory findSaasPaymentHistory(Long idx) {
		return repoSaasPaymentHistory.findById(idx).orElseThrow(
				() -> EntityNotFoundException.builder()
						.message("idxSaasPaymentHistory is not found.")
						.entity("SaasPaymentHistory")
						.idx(idx)
						.build()
		);
	}

	SaasInfo findSaasInfo(Long idx) {
		return repoSaasInfo.findById(idx).orElseThrow(
				() -> EntityNotFoundException.builder()
						.message("idxSaasInfo is not found.")
						.entity("SaasInfo")
						.idx(idx)
						.build()
		);
	}

	public SaasTrackerProgress findSaasTrackerProgress(User user) {
		return repoSaasTrackerProgress.findByUser(user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.message("Saas Tracker's User is not found.")
						.entity("SaasTrackerProgress")
						.idx(user.idx())
						.build()
		);
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity getSaasInfos() {
		List<SaasTrackerDto.SaasInfoRes> resSaasInfos = repoSaasInfo.findAllByOrderByName()
				.stream().map(SaasTrackerDto.SaasInfoRes::from)
				.collect(Collectors.toList());

		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(resSaasInfos).build()
		);
	}
}