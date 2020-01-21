package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.controller.base.PageableController;
import com.nomadconnection.dapp.api.dto.CardDto;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.api.service.CardService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequestMapping(CardController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "카드", description = CardController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class CardController implements PageableController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {

		public static final String BASE = "/card/v1";
		public static final String AUTHENTICATION = "/authentication";
		public static final String ISSUANCES = "/issuances";
		public static final String ISSUANCES_ISSUANCE = "/issuances/{issuance}";
		public static final String CARDS = "/cards";
		public static final String CARDS_CARD = "/cards/{card}";
		public static final String CARDS_CARD_PASSWORD = "/cards/{card}/password";
		public static final String CARDS_CARD_STATUS = "/cards/{card}/status";
		public static final String REISSUE = "/reissue";
	}

	private final CardService service;


	//==================================================================================================================
	//
	//	발급신청: 직원수, 희망카드개수, 명세서수령방법, 수령인, 수령인연락처, 주소(우편번호,기본,상세),부재시대리수령여부
	//
	//==================================================================================================================

	@ApiOperation(value = "발급신청", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@PostMapping(URI.ISSUANCES)
	public CardDto.CardIssuance postCardIssuances(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody CardDto.CardIssuance.CardIssuanceRequest dto
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ postCardIssuances ]) $user='{}', $card.issuance='{}'", user, dto);
		}
		return service.postCardIssuances(user.idx(), dto);
	}

	//==================================================================================================================
	//
	//	발급신청내역(건) 조회: 직원수, 희망카드개수, 명세서수령방법, 수령인, 수령인연락처, 주소(우편번호,기본,상세),부재시대리수령여부
	//
	//==================================================================================================================

	@ApiOperation(value = "발급신청내역 조회", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "issuance", value = "식별자(발급신청)", dataType = "Long")
	})
	@GetMapping(URI.ISSUANCES_ISSUANCE)
	public CardDto.CardIssuance getCardIssuance(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long issuance
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ getCardIssuance ]) $user='{}', $card.issuance.idx='{}'", user, issuance);
		}
		return service.getCardIssuance(user.idx(), issuance);
	}

	//==================================================================================================================
	//
	//	발급신청이력(목록) 조회: 직원수, 희망카드개수, 명세서수령방법, 수령인, 수령인연락처, 주소(우편번호,기본,상세),부재시대리수령여부
	//
	//==================================================================================================================

	@ApiPageable
	@ApiOperation(value = "발급신청이력(목록) 조회", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@GetMapping(URI.ISSUANCES)
	public Page<CardDto.CardIssuance> getCardIssuances(
			@ApiIgnore @CurrentUser CustomUser user,
			@ApiIgnore Pageable pageable
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ getCardIssuances ]) $user='{}', $pageable='{}'", user, pageable);
		}
		return service.getCardIssuances(user.idx(), revise(pageable));
	}

	//==================================================================================================================
	//
	//	카드 등록
	//
	//==================================================================================================================

	@ApiOperation(value = "[C1] 카드 등록", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@PostMapping(URI.CARDS)
	public CardDto postCards(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody CardDto.CardRegister dto) {
		if (log.isDebugEnabled()) {
			log.debug("([ postCards ]) $user='{}', $dto='{}'", user, dto);
		}
		return service.postCards(user.idx(), dto);
	}

	//=================================================================================================================
	//
	//	카드 정보조회
	//
	//==================================================================================================================

	@ApiImplicitParams({
			@ApiImplicitParam(name = "card", value = "식별자(카드)", dataType = "Long")
	})
	@GetMapping(URI.CARDS_CARD)
	public CardDto getCard(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long card
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ getCard ]) $user='{}', $card.idx='{}'", user, card);
		}
		return service.getCard(user.idx(), card);
	}

	//=================================================================================================================
	//
	//	카드 정보변경 - 국내/해외 결제가능여부, 월한도
	//
	//==================================================================================================================

	@ApiOperation(value = "카드 정보변경", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - 국내결제 가능여부: OWNER or MASTER/ADMIN" +
			"\n - 해외결제 가능여부: OWNER or MASTER/ADMIN" +
			"\n - 월한도: MASTER/ADMIN" +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "card", value = "식별자(카드)", dataType = "Long")
	})
	@PatchMapping(URI.CARDS_CARD)
	public ResponseEntity<?> patchCard(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long card,
			@RequestBody CardDto.CardPatch dto) {
		if (log.isDebugEnabled()) {
			log.debug("([ patchCard ]) $user='{}', $card.idx='{}', $dto='{}'", user, card, dto);
		}
		service.patchCard(user.idx(), card, dto);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	카드 비밀번호 변경
	//
	//==================================================================================================================

	@ApiOperation(value = "카드 비밀번호 변경", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - OWNER only" +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "card", value = "식별자(카드)", dataType = "Long")
	})
	@PutMapping(URI.CARDS_CARD_PASSWORD)
	public ResponseEntity<?> putCardPassword(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long card,
			@RequestBody CardDto.CardPasswordReset dto) {
		if (log.isDebugEnabled()) {
			log.debug("([ putCardPassword ]) $user='{}', $card.idx='{}', $dto='{}'", user, card, dto);
		}
		service.putCardPassword(user.idx(), card, dto);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	카드 상태변경 - 활성화 / 비활성화 / 분실신고 / 분실신고해제
	//
	//==================================================================================================================

	@ApiOperation(value = "카드 상태 변경", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - 활성화" +
			"\n - 비활성화" +
			"\n - 분실신고" +
			"\n - 분실신고해제" +
			"\n")
	@PutMapping(URI.CARDS_CARD_STATUS)
	public ResponseEntity<?> putCardStatus(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long card,
			@RequestBody CardDto.CardStatusPatch dto) {

		if (log.isDebugEnabled()) {
			log.debug("([ putCardStatus ]) $user='{}', $card.idx='{}', $dto='{}'", user, card, dto);
		}
		service.putCardStatus(user.idx(), card, dto);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	카드 재발급신청
	//
	//==================================================================================================================

	@ApiOperation(value = "카드 재발급 신청", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - 수령인/수령지 정보: /auth/v1/info" +
			"\n")
	@PostMapping(URI.REISSUE)
	public ResponseEntity<?> postCardReissue(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody CardDto.CardReissue dto
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ postCardReissue ]) $user='{}', $dto='{}'", user, dto);
		}
		service.postCardReissue(user.idx(), dto);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	카드 인증(비밀번호)
	//
	//==================================================================================================================

	@PostMapping(URI.AUTHENTICATION)
	public ResponseEntity<?> checkCardAuthentication(
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestBody CardDto.CardAuthentication dto
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ checkCardAuthentication ]) $user='{}', $dto='{}'", user, dto);
		}
		service.checkCardAuthentication(user.idx(), dto);
		return ResponseEntity.ok().build();
	}

	//==================================================================================================================
	//
	//	카드 해지
	//
	//==================================================================================================================

	@ApiOperation(value = "카드 해지", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - 파라미터: 식별자(카드) + 해지정보(비밀번호 + 해지사유)" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "card", value = "식별자(카드)", dataType = "Long")
	})
	@DeleteMapping(URI.CARDS_CARD)
	public ResponseEntity<?> deleteCard(
			@ApiIgnore @CurrentUser CustomUser user,
			@PathVariable Long card,
			@RequestBody CardDto.CardCancellation dto
	) {
		if (log.isDebugEnabled()) {
			log.debug("([ deleteCard ]) $user='{}', $card.idx='{}', $dto='{}'", user, card, dto);
		}
		service.deleteCard(user.idx(), card, dto);
		return ResponseEntity.ok().build();
	}
}