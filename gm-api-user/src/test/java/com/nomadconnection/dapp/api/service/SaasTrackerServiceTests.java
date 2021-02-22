package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.dto.SaasTrackerDto;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("stage")
class SaasTrackerServiceTests extends AbstractSpringBootTest {

	@Autowired
	private SaasTrackerService service;

	@Autowired
	private UserService userService;

	User user;

	@BeforeEach
	void init() {
		user = userService.getUser(467L);
	}

	@Test
	@Order(1)
	@DisplayName("SaaS_정보_제보_저장")
	void saveSaasIssueReport() {

		SaasTrackerDto.SaasTrackerReportsReq dto = new SaasTrackerDto.SaasTrackerReportsReq();
		dto.setReportType(3);
		dto.setSaasName("Test SaaS");
		dto.setPaymentMethod(1);
		dto.setPaymentPrice(10000000L);
		dto.setIssue("이것은 저러하고, 이것은 요러하다! 그래서 제보한다!");
		dto.setExperationDate("20201231");
		dto.setActiveExperationAlert(true);

		try {
			service.saveSaasTrackerReports(user.idx(), dto);
		} catch (Exception e){
			//Do Nothing
			log.error(e.getMessage(), e);
		}
	}

	@Test
	@Order(1)
	@DisplayName("기간별 SaaS 지출 내역 조회")
	void getUsageSums() {
		service.getUsageSums(66L, "202001", "202005");
	}

	@Test
	@Order(2)
	@DisplayName("기간별 SaaS 지출 내역 조회")
	void getUsageSumsDetails() {
		service.getUsageSumsDetails(48L, "202001");
	}

	@Test
	@Order(3)
	@DisplayName("기간별 SaaS 카테고리 내역 조회")
	void getUsageSumsCategories() {
		service.getUsageCategories(66L, "202001", "202005");
	}

	@Test
	@Order(4)
	@DisplayName("SaaS 카테고리 상세 지출 내역 조회")
	void getUsageSumsCategoriesDetails() {
		service.getUsageCategoriesDetails(66L, 1L, "202011", "202102");
	}

	@Test
	@Order(5)
	@DisplayName("SaaS 카테고리 목록 조회")
	void getSaasCategories() {
		service.getUseSaasByCategory(66L);
	}

	@Test
	@Order(6)
	@DisplayName("구독 중/구독만료 SaaS 목록 조회")
	void getUseSaasList() {
		service.getUseSaasList(66L);
	}

	@Test
	@Order(7)
	@DisplayName("SaaS_정보_수정")
	void updateSaasPaymentInfo() {

		SaasTrackerDto.UpdateSaasInfoReq req = new SaasTrackerDto.UpdateSaasInfoReq();
		req.setManagerName("hahaha");
		req.setManagerEmail("hahaha@gowid.com");
		req.setActiveSubscription(false);
		req.setActiveAlert(false);

		try {
			service.updateSaasInfo(66L, 9L, req);
		} catch (Exception e){
			//Do Nothing
			log.error(e.getMessage(), e);
		}
	}

	@Test
	@Order(8)
	@DisplayName("스케줄 목록")
	void getSaasPaymentSchedule() {
		service.getSaasPaymentSchedules(66L);
	}

	@Test
	@Order(9)
	@DisplayName("SaaS Payment Info 상세조회")
	void getSaasPaymentInfoByIdx() {
		service.getSaasPaymentDetailInfo(66L, 1L);
	}

	@Test
	@Order(10)
	@DisplayName("SaaS Insights 조회")
	void getSaasInsights() {
		service.getInsights(66L);
	}

	@Test
	@Order(11)
	@DisplayName("SaaS Tracker 준비")
	void updateSaasTracker() {
		service.updateSaasTrackerProgress(467L, 3);
	}


	@Test
	@Order(12)
	@DisplayName("SaaS 목록 조회")
	void getSaasInfos() {
		service.getSaasInfos();
	}
}