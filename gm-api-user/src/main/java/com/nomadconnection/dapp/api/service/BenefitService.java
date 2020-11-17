package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BenefitDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.core.domain.benefit.*;
import com.nomadconnection.dapp.core.domain.repository.benefit.*;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BenefitService {

	private final BenefitRepository repoBenefit;
	private final BenefitItemRepository repoBenefitItem;
	private final BenefitPaymentHistoryRepository repoBenefitPaymentHistory;
	private final BenefitPaymentItemRepository repoBenefitPaymentItem;
	private final BenefitCategoryRepository repoBenefitCategory;
	private final BenefitSearchHistoryRepository repoBenefitSearchHistory;

	private final UserService userService;
	private final EmailService emailService;
	private final AdminService adminService;

	/**
	 * 베네핏 목록 조회
	 *
	 * @return 베네핏 정보 목록
	 */
	@Transactional(readOnly = true)
	public ResponseEntity getBenefits(Pageable pageable) {

		log.debug(">>>>> getBenefits.start");
		Page<BenefitDto.BenefitRes> resBenefitPage = repoBenefit.findAllByDisabledFalseOrderByPriority(pageable).map(BenefitDto.BenefitRes::from);

		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(resBenefitPage).build()
		);
	}


	/**
	 * 베네핏 카테고리 목록 조회
	 *
	 * @return 베네핏 카테고리 목록
	 */
	@Transactional(readOnly = true)
	public ResponseEntity getBenefitCategories() {

		log.debug(">>>>> getBenefitCategories.start");
		List<BenefitDto.BenefitCategoryRes> resBenefitCategories = repoBenefitCategory.findAllByCategoryGroupCodeIsNullOrderByPriorityAsc()
																						.stream().map(BenefitDto.BenefitCategoryRes::from)
																						.collect(Collectors.toList());;

		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(resBenefitCategories).build()
		);
	}


	/**
	 * 베네핏 항목 조회
	 *
	 * @param idxBenefit 조회할 Benefit ID
	 * @return 베네핏 항목
	 */
	@Transactional(readOnly = true)
	public ResponseEntity getBenefit(Long idxBenefit) {

		log.debug(">>>>> getBenefit.start");
		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(
						BenefitDto.BenefitRes.from(
								findBenefit(idxBenefit)
						)
				).build()
		);
	}


	/**
	 * Benefit 결제 이력 저장
	 *
	 * @param dto	Benefit 결제 정보
	 * @param userIdx	사용자 ID
	 * @return 저장 결과
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveBenefitPaymentHistory(BenefitDto.BenefitPaymentHistoryReq dto, Long userIdx) {

		log.info(">>>>> saveBenefitPaymentLog.start");

		// 0. 해당 User 있는지 확인
		User user = userService.getUser(userIdx);
		log.debug("     >>> user check OK!");

		// 1. 해당 Benefit이 있는지 확인
		Benefit benefit = findBenefit(dto.getIdxBenefit());
		log.debug("     >>> user benefit OK!");

		// benefit 결제 내역 저장(benefitPaymentHistory, benefitPaymentItem)
		BenefitPaymentHistory benefitPaymentHistory = saveBenefitPaymentHistory(user, benefit, dto);
		List<BenefitPaymentItem> paymentItemList = saveBenefitPaymentItems(benefitPaymentHistory, dto);
		List<BenefitDto.BenefitPaymentItemRes> paymentItemListRes = paymentItemList.stream().map(BenefitDto.BenefitPaymentItemRes::from).collect(Collectors.toList());

		// 2. 결과 메일 전송
		Map<String, Object> mailAttribute = this.getMailAttribute(benefit, benefitPaymentHistory, paymentItemListRes);
		if(dto.getErrCode()) {

			// 2.1. 결제 결과가 성공일 경우
			// 2.1.1. 고객사에게 메일 전송
			//     - 사용자 메일과 입력한 구매자 메일이 동일할 경우 구매자 메일 전송
			//     - 사용자 메일과 입력한 구매자 메일이 다를 경우 둘 다에게 메일 전송
			boolean isSuccessSendPaymentMail = (user.email().equals(dto.getCustomerEmail())) ?
				emailService.sendBenefitResultMail(mailAttribute,
						BenefitPaymentEmailType.BENEFIT_GOWID_EMAIL_ADDR.getValue(),
						dto.getCustomerEmail(),
						BenefitPaymentEmailType.BENEFIT_PAYMENT_SUCCESS_EMAIL_TITLE.getValue(),
						BenefitPaymentEmailType.BENEFIT_PAYMENT_SUCCESS_TEMPLATE.getValue())
				:
				emailService.sendBenefitResultMail(mailAttribute,
						BenefitPaymentEmailType.BENEFIT_GOWID_EMAIL_ADDR.getValue(),
						new String[]{dto.getCustomerEmail(), user.email()},
						BenefitPaymentEmailType.BENEFIT_PAYMENT_SUCCESS_EMAIL_TITLE.getValue(),
						BenefitPaymentEmailType.BENEFIT_PAYMENT_SUCCESS_TEMPLATE.getValue());

			// 2.1.2. 발주서 메일 전송
			boolean isSuccessSendOrderMail = emailService.sendBenefitResultMail(mailAttribute,
																					BenefitPaymentEmailType.BENEFIT_GOWID_EMAIL_ADDR.getValue(),
																					benefit.benefitProviders().get(0).sendOrderEmail(),
																					BenefitPaymentEmailType.BENEFIT_PAYMENT_ORDER_EMAIL_TITLE.getValue(),
																					BenefitPaymentEmailType.BENEFIT_PAYMENT_ORDER_TEMPLATE.getValue());

			// 메일 결과 저장
			benefitPaymentHistory.sendPaymentMailErrCode(isSuccessSendPaymentMail ? 0 : 1).sendOrderMailErrCode(isSuccessSendOrderMail ? 0 : 1);

		}else {
			// 3. 결제 결과가 실패일 경우
			// 3.1. 결과 실패 메일 전송
			emailService.sendBenefitResultMail(mailAttribute,
												BenefitPaymentEmailType.BENEFIT_GOWID_EMAIL_ADDR.getValue(),
												BenefitPaymentEmailType.BENEFIT_GOWID_EMAIL_ADDR.getValue(),
												BenefitPaymentEmailType.BENEFIT_PAYMENT_FAILED_EMAIL_TITLE.getValue(),
												BenefitPaymentEmailType.BENEFIT_PAYMENT_FAILED_TEMPLATE.getValue());
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.status(true)
						.build())
				.build());
	}

	/**
	 * Benefit 결제 이력 저장(to DB)
	 *
	 * @param user	사용자 정보
	 * @param benefit	Benefit 정보
	 * @param dto	Benefit 결제 정보
	 * @return	Benefit 결제 정보
	 */
	BenefitPaymentHistory saveBenefitPaymentHistory(User user, Benefit benefit, BenefitDto.BenefitPaymentHistoryReq dto) {

		BenefitPaymentHistory benefitPaymentHistory = BenefitPaymentHistory.builder()
				.user(user)
				.customerName(dto.getCustomerName())
				.customerMdn(dto.getCustomerMdn())
				.customerEmail(dto.getCustomerEmail())
				.customerDeptName(dto.getCustomerDeptName())
				.companyName(dto.getCompanyName())
				.companyAddr(dto.getCompanyAddr())
				.standardPrice(dto.getStandardPrice())
				.totalPrice(dto.getTotalPrice())
				.errCode(dto.getErrCode() ?  0 : 1)
				.errMessage(dto.getErrMessage())
				.paidAt(dto.getPaidAt())
				.receiptUrl(dto.getReceiptUrl())
				.impUid(dto.getImpUid())
				.benefit(benefit)
				.cardNum(dto.getCardNum())
				.status(dto.getErrCode() ? BenefitPaymentStatusType.SUCCESS.getValue() : BenefitPaymentStatusType.FAILED.getValue())
				.sendOrderMailErrCode(0)
				.sendPaymentMailErrCode(0)
				.build();
		repoBenefitPaymentHistory.save(benefitPaymentHistory);

		return benefitPaymentHistory;
	}

	/**
	 * Benefit 결제 Item 저장(to DB)
	 *
	 * @param benefitPaymentHistory	Benefit 결제 이력 정보
	 * @param dto Benefit 결제 정보
	 * @return	결제된 Benefit 항목 List
	 */
	List<BenefitPaymentItem> saveBenefitPaymentItems(BenefitPaymentHistory benefitPaymentHistory, BenefitDto.BenefitPaymentHistoryReq dto) {

		List<BenefitPaymentItem> paymentItemList = new ArrayList<>();
		dto.getItems().forEach(item -> {
			BenefitPaymentItem benefitItem = BenefitPaymentItem.builder()
					.benefitItem(findBenefitItem(item.getIdxBenefitItem()))
					.benefitPaymentHistory(benefitPaymentHistory)
					.quantity(item.getQuantity())
					.price(item.getPrice())
					.build();

			repoBenefitPaymentItem.save(benefitItem);
			paymentItemList.add(benefitItem);
		});
		return paymentItemList;
	}

	/**
	 * Benefit 결제 메일 전송을 위한 Attribute Map 셋팅
	 *
	 *
	 * @param benefit
	 * @param benefitPaymentHistory    benefit 결제 정보
	 * @param paymentItems 구매 항목 목록
	 * @return Benefit 결제 메일 Attribute Map
	 */
	Map<String, Object> getMailAttribute(Benefit benefit, BenefitPaymentHistory benefitPaymentHistory, List<BenefitDto.BenefitPaymentItemRes> paymentItems) {

		Map<String, Object> mailAttributMap = new HashMap<>();

		mailAttributMap.put("customerName", benefitPaymentHistory.customerName());			// 고객명
		mailAttributMap.put("customerMdn", benefitPaymentHistory.customerMdn());			// 고객 연락처
		mailAttributMap.put("customerEmail", benefitPaymentHistory.customerEmail());		// 고객 이메일
		mailAttributMap.put("customerDeptName", benefitPaymentHistory.customerDeptName());	// 부서명

		mailAttributMap.put("companyName", benefitPaymentHistory.companyName());			// 회사명
		mailAttributMap.put("companyAddr", benefitPaymentHistory.companyAddr());			// 회사주소

		mailAttributMap.put("benefitName", benefitPaymentHistory.benefit().name());		    // Benefit 이름
		mailAttributMap.put("paidAt", benefitPaymentHistory.paidAt());						// 결제일

		mailAttributMap.put("paymentItems", paymentItems);									// 결제 항목
		mailAttributMap.put("totalPrice", benefitPaymentHistory.totalPrice());				// 최종 결제 금액
		mailAttributMap.put("totalPurchase", paymentItems.stream().mapToLong(o -> o.getPurchase()).sum());				// 최종 구매 금액(발주서)

		mailAttributMap.put("errMessage", benefitPaymentHistory.errMessage());				// 오류 메세지 (null 가능)

		mailAttributMap.put("providerEmail", benefit.benefitProviders().get(0).email());	// 제공 업체 email
		mailAttributMap.put("providerTel", benefit.benefitProviders().get(0).tel());		// 제공 업체 전화번호

		return mailAttributMap;
	}


	/**
	 * Benefit 결제 목록 조회
	 *
	 * @param userIdx User ID
	 * @param pageable Pageable
	 * @return Benefit 결제 목록
	 */
	@Transactional(readOnly = true)
	public ResponseEntity getBenefitPaymentHistories(Long userIdx, Pageable pageable) {

		log.debug(">>>>> getBenefitPaymentHistories.start");

		User user = userService.getUser(userIdx);
		Page<BenefitDto.BenefitPaymentHistoryRes> resBenefitPaymentHistoryPage;

		// 해당 사용자가 gowid admin이면 모든 이력 조회, 아니면 해당 사용자의 성공 이력만 조회
		if(adminService.isGowidAdmin(userIdx)) {
			resBenefitPaymentHistoryPage = repoBenefitPaymentHistory.findAll(pageable)
					.map(BenefitDto.BenefitPaymentHistoryRes::from);
		}else {
			resBenefitPaymentHistoryPage = repoBenefitPaymentHistory.findAllByUserAndStatus(user,
												pageable,
												BenefitPaymentStatusType.SUCCESS.getValue())
											.map(BenefitDto.BenefitPaymentHistoryRes::from);
		}

		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(resBenefitPaymentHistoryPage).build()
		);
	}

	/**
	 * Benefit 결제 내용 상세 조회
	 *
	 * @param idxBenefitPaymentHistory Benefit 결제 ID
	 * @return Benefit 결제 상내 내용
	 */
	@Transactional(readOnly = true)
	public ResponseEntity getBenefitPaymentHistory(Long idxBenefitPaymentHistory) {

		log.debug(">>>>> getBenefitPaymentHistory.start");

		return ResponseEntity.ok().body(
				BusinessResponse.builder().data(
						BenefitDto.BenefitPaymentHistoryRes.from(
								findBenefitPaymentHistory(idxBenefitPaymentHistory)
						)
				).build()
		);
	}

	/**
	 * Benefit 엔티티 조회
	 *
	 * @param idxBenefit Benefit ID
	 * @return Benefit 정보
	 */
	Benefit findBenefit(Long idxBenefit) {
		return repoBenefit.findById(idxBenefit).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("Benefit")
						.idx(idxBenefit)
						.build()
		);
	}

	/**
	 * BenefitPaymentHistory 항목 조회
	 *
	 * @param idxBenefitPaymentHistory BenefitPaymentHistory ID
	 * @return BenefitPaymentHistory 엔티티보
	 */
	@Transactional(readOnly = true)
	BenefitPaymentHistory findBenefitPaymentHistory(Long idxBenefitPaymentHistory) {

		return repoBenefitPaymentHistory.findById(idxBenefitPaymentHistory).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("BenefitPaymentHistory")
						.idx(idxBenefitPaymentHistory)
						.build()
		);
	}

	/**
	 * Benefit Item 항목 조회
	 *
	 * @param idxBenefitItem 식별자(BenefitItem)
	 * @return BenefitItem 항목
	 */
	@Transactional(readOnly = true)
	BenefitItem findBenefitItem(long idxBenefitItem) {

		return repoBenefitItem.findById(idxBenefitItem).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("BenefitItem")
						.idx(idxBenefitItem)
						.build()
		);
	}


	/**
	 * Benefit 검색어 저장
	 *
	 * @param dto	검색어 정보
	 * @param idx	검색한 사용자 idx
	 * @return	저장 결과
	 */
	public ResponseEntity saveBenefitSearchHistory(BenefitDto.BenefitSearchHistoryReq dto, CustomUser user) {

		BenefitSearchHistory benefitSearchHistory = BenefitSearchHistory.builder()
				.idxUser(StringUtils.isEmpty(user) ? null : user.idx())
				.searchText(dto.getSearchText())
				.build();
		repoBenefitSearchHistory.save(benefitSearchHistory);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.status(true)
						.build())
				.build());
	}
}