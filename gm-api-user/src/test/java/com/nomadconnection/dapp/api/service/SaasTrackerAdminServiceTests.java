package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.SaasTrackerAdminDto;
import com.nomadconnection.dapp.api.dto.SaasTrackerDto;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("stage")
class SaasTrackerAdminServiceTests extends AbstractSpringBootTest {

	@Autowired
	private SaasTrackerAdminService service;

	@Autowired
	private UserService userService;

	User user;

	@BeforeEach
	void init() {
		user = userService.getUser(467L);
	}

	@Test
	@Order(1)
	@DisplayName("SaaS Tracker 사용자 조회")
	void getSaasTrackerUser() {
		service.getSaasTrackerUser(user.idx());
		log.info(">>>>> complete.");
	}

	@Test
	@Order(2)
	@DisplayName("(업체별) SaaS 지출 내역 조회")
	void getSaasPaymentHistories() {
//		service.getSaasPaymentHistories(user.idx(), user.idx(), );
		log.info(">>>>> complete.");
	}

	@Test
	@Order(3)
	@DisplayName("(업체별) SaaS 지출 내역 상세 조회")
	void getSaasPaymentHistoryDetail() {
		service.getSaasPaymentHistoryDetail(4113434L);
		log.info(">>>>> complete.");
	}

	@Test
	@Order(4)
	@DisplayName("SaaS 지출 내역 저장")
	void saveSaasPaymentHistory() {
		SaasTrackerAdminDto.SaasPaymentHistoryReq req = new SaasTrackerAdminDto.SaasPaymentHistoryReq();
		req.setIdxUser(467L);
		req.setIdxSaasInfo(1L);
		req.setOrganization("0306");
		req.setPaymentDate("20211231");
		req.setPaymentPrice(100000L);
		req.setPaymentMethod(1);
		req.setAccountNumber("1111****33334444");
		req.setCardNumber("7777****99990000");
		req.setForeignType(1);
		req.setCurrency("KRW");

		service.saveSaasPaymentHistory(req);
		log.info(">>>>> complete.");
	}

	@Test
	@Order(5)
	@DisplayName("SaaS 지출 내역 삭제")
	void deleteSaasPaymentHistory() {
		service.deleteSaasPaymentHistory(4115632L);
	}

	@Test
	@Order(6)
	@DisplayName("(업체별) SaaS 사용 현황 조회")
	void getSaasPaymentInfos() {
//		service.getSaasPaymentInfos(user.idx(), user.idx(), );
		log.info(">>>>> complete.");
	}

	@Test
	@Order(7)
	@DisplayName("(업체별) SaaS 사용 현황 상세 조회")
	void getSaasPaymentInfoDetail() {
		service.getSaasPaymentInfoDetail(1158L);
		log.info(">>>>> complete.");
	}

	@Test
	@Order(8)
	@DisplayName("SaaS 사용 현황 저장")
	void saveSaasPaymentInfo() {
		SaasTrackerAdminDto.SaasPaymentInfoReq req = new SaasTrackerAdminDto.SaasPaymentInfoReq();
		req.setIdxUser(66L);
		req.setIdxSaasInfo(1L);
		req.setOrganization("0306");
		req.setCurrentPaymentDate("20210118");
		req.setCurrentPaymentPrice(100000L);
		req.setPaymentType(0);
		req.setPaymentMethod(1);
//		req.setAccountNumber("1111****33334444");
		req.setCardNumber("7777****99990000");
		req.setPaymentScheduleDate("20210218");
		req.setActiveSubscription(true);
		req.setIsDup(false);

		service.saveSaasPaymentInfo(req);
		log.info(">>>>> complete.");
	}

	@Test
	@Order(9)
	@DisplayName("SaaS 사용 현황 수정")
	void updateSaasPaymentInfo() {
		SaasTrackerAdminDto.SaasPaymentInfoReq req = new SaasTrackerAdminDto.SaasPaymentInfoReq();
		req.setIdxUser(66L);
		req.setOrganization("0311");
		req.setCurrentPaymentDate("20210120");
		req.setCurrentPaymentPrice(100011L);
		req.setPaymentType(0);
		req.setPaymentMethod(2);
		req.setAccountNumber("1111");
		req.setCardNumber("0000");
		req.setPaymentScheduleDate("20210220");
		req.setActiveSubscription(false);
		req.setIsDup(true);

		service.updateSaasInfo(1571L, req);
	}

	@Test
	@Order(10)
	@DisplayName("SaaS 구독/구독만료 목록 조회")
	void getSaasSubscriptions() {
		service.getSaasSubscriptions(66L);
	}
}