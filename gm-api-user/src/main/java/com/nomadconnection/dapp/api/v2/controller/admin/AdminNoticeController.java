package com.nomadconnection.dapp.api.v2.controller.admin;

import com.nomadconnection.dapp.api.controller.UserController;
import com.nomadconnection.dapp.api.v2.dto.BoardDto;
import com.nomadconnection.dapp.api.v2.service.board.BoardService;
import com.nomadconnection.dapp.core.domain.etc.NoticeBoard;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(AdminController.URI.BASE)
@CrossOrigin(allowCredentials = "true")
@Api(tags = "어드민 V2")
public class AdminNoticeController {

	public static class URI {
		public static final String NOTICE = "/notice";
	}

	private final BoardService boardService;

	@GetMapping(URI.NOTICE)
	@ApiOperation(value = "공지사항 조회")
	public ResponseEntity<?> findNotice(Pageable pageable){

		Page<NoticeBoard> data = boardService.findAll(pageable);

		return new ResponseEntity(data, HttpStatus.OK);
	}

	@ApiOperation(value = "공지사항 조회")
	@GetMapping(URI.NOTICE + "/{idx}")
	public ResponseEntity<?> findNotice(@PathVariable Long idx){

		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
	@ApiOperation(value = "공지사항 저장")
	@PostMapping(URI.NOTICE + "/{idx}")
	public ResponseEntity<?> saveNotice(@PathVariable(required = false) Long idx,
										@RequestBody BoardDto.Notice notice){

		boardService.saveNotice(idx, notice);

		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAnyAuthority('GOWID_ADMIN')")
	@ApiOperation( value = "공지사항 삭제")
	@DeleteMapping(URI.NOTICE + "/{idx}")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "정상"),
			@ApiResponse(code = 201, message = "생성"),
			@ApiResponse(code = 404, message = "권한없음"),
			@ApiResponse(code = 500, message = "")
	})
	public ResponseEntity<?> deleteNotice(@PathVariable Long idx){

		boardService.deleteNotice(idx);

		return ResponseEntity.ok().build();
	}
}
