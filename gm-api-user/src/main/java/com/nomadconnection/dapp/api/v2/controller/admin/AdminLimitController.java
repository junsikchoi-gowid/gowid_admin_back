package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.service.admin.LimitRecalculationService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationPageDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationRequestDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationResponseDto;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminLimitController extends AdminBaseController {

	public static class URI {
		public static final String LIMIT_RECALCULATION = "/limit/recalculation";
	}

	private final LimitRecalculationService limitRecalculationService;

	@ApiPageable
	@GetMapping(value = AdminLimitController.URI.LIMIT_RECALCULATION)
	public ResponseEntity<?> findRecalculationCorps(@ModelAttribute LimitRecalculationPageDto.LimitRecalculationCondition dto,
	                                                @PageableDefault Pageable pageable){
		Page<LimitRecalculationPageDto> limitRecalculationPage = limitRecalculationService.findAll(dto, pageable);

		return ResponseEntity.ok().body(limitRecalculationPage);
	}

	@GetMapping(value = AdminLimitController.URI.LIMIT_RECALCULATION + "/{idxCorp}")
	public ResponseEntity<?> findRecalculationCorp(@PathVariable Long idxCorp) {
		LimitRecalculationResponseDto responseDto = limitRecalculationService.findOne(idxCorp);

		return ResponseEntity.ok().body(responseDto);
	}

	@ApiOperation(
		value = "requestRecalculateLimit", notes = "ContactType" +
		"BOTH/EMAIL/PHONE")
	@PostMapping(value = AdminLimitController.URI.LIMIT_RECALCULATION)
	public ResponseEntity<?> requestRecalculateLimit(
		@ApiIgnore @CurrentUser CustomUser user,
		@RequestBody LimitRecalculationRequestDto dto) {
		limitRecalculationService.requestRecalculate(user, dto);
		return ResponseEntity.ok().build();
	}

}
