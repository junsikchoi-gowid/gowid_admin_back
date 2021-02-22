package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.service.admin.LimitRecalculationService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationCondition;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationDetail;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController("AdminV2Controller")
@RequiredArgsConstructor
@RequestMapping(AdminController.URI.BASE)
@CrossOrigin(allowCredentials = "true")
@Api(tags = "어드민 V2")
public class AdminController {

	public static class URI {
		public static final String BASE = "/admin/v2";

		public static final String LIMIT_RECALCULATION = "/limit/recalculation";
	}

	private final LimitRecalculationService limitRecalculationService;

	@ApiPageable
	@GetMapping(value = URI.LIMIT_RECALCULATION)
	public ResponseEntity<?> findRecalculationCorps(@ModelAttribute LimitRecalculationCondition dto,
	                                                @PageableDefault Pageable pageable){
		Page<LimitRecalculationDto> limitRecalculationPage = limitRecalculationService.findAll(dto, pageable);

		return ResponseEntity.ok().body(limitRecalculationPage);
	}

	@GetMapping(value = URI.LIMIT_RECALCULATION + "/{idxCorp}")
	public ResponseEntity<?> findRecalculationCorp(@PathVariable Long idxCorp, @DateTimeFormat(pattern = "yyyyMMdd") @RequestParam LocalDate date) {
		LimitRecalculationDto limitRecalculations = limitRecalculationService.findByCorpAndDate(idxCorp, date);

		return ResponseEntity.ok().body(limitRecalculations);
	}

	@PostMapping(value = URI.LIMIT_RECALCULATION + "/{idxCorp}")
	public ResponseEntity<?> requestRecalculateLimit(@PathVariable Long idxCorp, @RequestBody LimitRecalculationDetail dto) {
		limitRecalculationService.requestRecalculate(idxCorp, dto);
		return ResponseEntity.ok().build();
	}

}
