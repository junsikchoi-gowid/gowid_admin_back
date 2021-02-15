package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.v2.dto.LimitRecalculationDto;
import com.nomadconnection.dapp.api.v2.service.admin.LimitRecalculationService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("AdminV2Controller")
@RequiredArgsConstructor
@RequestMapping(AdminController.URI.BASE)
@CrossOrigin(allowCredentials = "true")
@Api(tags = "어드민 V2")
public class AdminController {

	public static class URI {
		public static final String BASE = "/admin/v2";

		public static final String LIMIT = "/limit";
		public static final String RECALCULATION = "/recalculation";
	}

	private final LimitRecalculationService limitRecalculationService;

	@ApiPageable
	@GetMapping(value = URI.LIMIT + URI.RECALCULATION)
	public ResponseEntity<?> findRecalculationCorps(@PageableDefault Pageable pageable){
		Page<LimitRecalculationDto> limitRecalculationPage = limitRecalculationService.findAll(pageable);

		return ResponseEntity.ok().build();
	}

	@GetMapping(value = URI.LIMIT + URI.RECALCULATION + "/{idxCorp}")
	public ResponseEntity<?> findRecalculationCorp(@PathVariable Long idxCorp){

		return ResponseEntity.ok().build();
	}

	@PostMapping(value = URI.LIMIT + URI.RECALCULATION + "/{idxCorp}")
	public ResponseEntity<?> recalculateLimit(@PathVariable Long idxCorp){

		return ResponseEntity.ok().build();
	}

}
