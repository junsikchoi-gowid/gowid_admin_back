package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.service.admin.LimitRecalculationService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;

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
	public ResponseEntity<?> findRecalculationCorps(@ModelAttribute LimitRecalculationDto.LimitRecalculationCondition dto,
	                                                @PageableDefault Pageable pageable){
		Page<LimitRecalculationDto> limitRecalculationPage = limitRecalculationService.findAll(dto, pageable);

		return ResponseEntity.ok().body(limitRecalculationPage);
	}

	@GetMapping(value = AdminLimitController.URI.LIMIT_RECALCULATION + "/{idxCorp}")
	public ResponseEntity<?> findRecalculationCorp(@PathVariable Long idxCorp, @DateTimeFormat(pattern = "yyyyMMdd") @RequestParam LocalDate date) {
		LimitRecalculationDto limitRecalculations = limitRecalculationService.findByCorpAndDate(idxCorp, date);

		return ResponseEntity.ok().body(limitRecalculations);
	}

	@PostMapping(value = AdminLimitController.URI.LIMIT_RECALCULATION + "/{idxCorp}")
	public ResponseEntity<?> requestRecalculateLimit(
		@ApiIgnore @CurrentUser CustomUser user,
		@PathVariable Long idxCorp, @RequestBody LimitRecalculationDto.LimitRecalculationDetail dto) {
		limitRecalculationService.requestRecalculate(user, idxCorp, dto);
		return ResponseEntity.ok().build();
	}
}
