package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.abstracts.AbstractMockitoTest;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1710;
import com.nomadconnection.dapp.api.enums.ShinhanResponse;
import com.nomadconnection.dapp.api.exception.kised.KisedException;
import com.nomadconnection.dapp.api.v2.dto.kised.KisedRequestDto;
import com.nomadconnection.dapp.api.v2.dto.kised.KisedResponseDto;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.domain.repository.kised.KisedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

class KisedServiceTest extends AbstractMockitoTest {

	@InjectMocks
	private KisedService kisedService;

	@Mock
	private IssuanceService issuanceService;

	@Mock
	private KisedRepository kisedRepository;

	private String licenseNo;
	private String projectId;

	@BeforeEach
	void init(){
		licenseNo = "3547812123";
		projectId = "12345678";
	}

	private KisedRequestDto buildRequestDto(){
		return KisedRequestDto.builder().licenseNo(licenseNo).projectId(projectId).build();
	}

	private KisedResponseDto buildResponseDto(){
		KisedResponseDto dto =
			KisedResponseDto.builder()
				.licenseNo(licenseNo).projectId(projectId).startDate("20210301").endDate("20211231")
				.bankCode("088").accountHolder("홍길동").accountNo("100077033329")
				.orgName(null).projectName("에그타르트 프로젝트")
				.spot(0L).cash(100000000L).build();

		return dto;
	}

	private Kised buildKised(KisedResponseDto dto){
		return Kised.builder()
			.idx(1L)
			.licenseNo(dto.getLicenseNo())
			.projectId(dto.getProjectId())
			.projectName(dto.getProjectName())
			.startDate(dto.getStartDate())
			.endDate(dto.getEndDate())
			.orgName(dto.getOrgName())
			.accountHolder(dto.getAccountHolder())
			.accountNo(dto.getAccountNo())
			.bankCode(dto.getBankCode())
			.cash(dto.getCash())
			.spot(dto.getSpot())
			.build();
	}

	@Test
	@Transactional
	@DisplayName("사업자번호와 과제번호로 기존 저장되어있는 창진원 데이터를 찾지못하면 DTO로 넘어온 데이터를 창진원 데이터로 저장")
	void Should_Save_DefaultKised_When_NotFound() {
		KisedResponseDto dto = buildResponseDto();
		given(kisedRepository.findByLicenseNoAndProjectId(dto.getLicenseNo(), dto.getProjectId()))
			.willReturn(Optional.empty());
		given(kisedRepository.save(any(Kised.class)))
			.willReturn(buildKised(dto));

		Kised savedKised = kisedService.save(dto);

		KisedResponseDto savedDto = KisedResponseDto.from(savedKised);
		assertThat(dto.getLicenseNo()).isEqualTo(savedDto.getLicenseNo());
		assertThat(dto.getProjectId()).isEqualTo(savedDto.getProjectId());
		assertThat(dto.getProjectName()).isEqualTo(savedDto.getProjectName());
		assertThat(dto.getBankCode()).isEqualTo(savedDto.getBankCode());
	}

	@Test
	@Transactional
	@DisplayName("사업자번호와 과제번호로 기존 저장되어있는 창진원 데이터가 있으면 그대로 리턴한다")
	void Should_ReturnExistsEntity_When_Exists() {
		KisedResponseDto dto = buildResponseDto();
		given(kisedRepository.findByLicenseNoAndProjectId(dto.getLicenseNo(), dto.getProjectId()))
			.willReturn(Optional.ofNullable(buildKised(dto)));

		Kised savedKised = kisedService.save(dto);

		KisedResponseDto savedDto = KisedResponseDto.from(savedKised);
		assertThat(dto.getLicenseNo()).isEqualTo(savedDto.getLicenseNo());
		assertThat(dto.getProjectId()).isEqualTo(savedDto.getProjectId());
	}

	@Test
	@Transactional
	@DisplayName("신한카드로부터 에러 응답 받을때 KisedException 예외를 받는다")
	void Should_ReturnErrorCode_When_GetSuccessrFromShinhan() {
		Long userIdx = 1L;
		KisedRequestDto requestDto = buildRequestDto();
		final String INVALID_PROJECT_RESPONSE_CODE = ShinhanResponse.SH1710_INVALID_PROJECT.getResponseCode();
		DataPart1710 shinhanErrorResponse = DataPart1710.builder().c009(INVALID_PROJECT_RESPONSE_CODE).c013("").build();

		given(issuanceService.proc1710(userIdx, requestDto))
			.willReturn(shinhanErrorResponse);
		KisedException kisedException =
			assertThrows(KisedException.class, () -> kisedService.verify(userIdx, requestDto));

		assertThat(ShinhanResponse.SH1710_INVALID_PROJECT.getResponseCode()).isEqualTo(kisedException.getCode());
		assertThat(ShinhanResponse.SH1710_INVALID_PROJECT.getResponseMessage()).isEqualTo(kisedException.getDesc());
	}

	@Test
	@Transactional
	@DisplayName("신한카드로부터 성공 응답 받을때 신한카드에서 데이터를 받아 응답한다")
	void Should_ReturnResponse_When_GetSuccessFromShinhan() throws Exception {
		Long userIdx = 1L;
		KisedRequestDto requestDto = buildRequestDto();
		final String SUCCESS = Const.API_SHINHAN_RESULT_SUCCESS;
		DataPart1710 shinhanSuccessResponse = DataPart1710.builder().d001(licenseNo).d002(projectId).c009(SUCCESS).c013("").build();

		given(issuanceService.proc1710(userIdx, requestDto))
			.willReturn(shinhanSuccessResponse);
		KisedResponseDto responseDto = kisedService.verify(userIdx, requestDto);

		assertThat(responseDto.getLicenseNo()).isEqualTo(licenseNo);
		assertThat(responseDto.getProjectId()).isEqualTo(projectId);
	}

}