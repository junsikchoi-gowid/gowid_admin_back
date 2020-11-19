package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractMockitoTest;
import com.nomadconnection.dapp.api.dto.CommonCodeDto;
import com.nomadconnection.dapp.core.domain.common.CommonCode;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class CommonCodeServiceTests extends AbstractMockitoTest {

	@InjectMocks
	private CommonCodeService commonCodeService;

	@Mock
	private CommonCodeRepository commonCodeRepository;

	@Test
	@DisplayName("findByCode_존재하는경우_공통코드_리턴")
	void shouldGetCommonCodeByCode() {
		final CommonCodeType CODE = CommonCodeType.SURVEY_FUNNELS;
		final CommonCode funnelsSurveyCode = CommonCode.builder().code(CODE).build();

		//given
		given(commonCodeRepository.findAllByCode(CODE)).willReturn(Optional.of(funnelsSurveyCode));

		//when
		final CommonCodeDto commonCodeDto = commonCodeService.findByCode(CODE);

		//then
		verify(commonCodeRepository, atLeastOnce()).findAllByCode(CODE);
		assertEquals(funnelsSurveyCode.code(), commonCodeDto.getCode());
	}

}