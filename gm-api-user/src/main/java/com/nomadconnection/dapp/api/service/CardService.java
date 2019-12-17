package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.CardDto;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.embed.Address;
import com.nomadconnection.dapp.core.domain.repository.CardIssuanceRepository;
import com.nomadconnection.dapp.core.domain.repository.CardRepository;
import com.nomadconnection.dapp.core.domain.repository.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

	private final AuthService serviceAuth;

	private final UserService serviceUser;
	private final CardRepository repo;
	private final CorpRepository repoCorp;
	private final UserRepository repoUser;
	private final CardIssuanceRepository repoIssuance;

	private final PasswordEncoder passwordEncoder;

	/**
	 * 카드 발급 신청
	 *
	 * - 직원수
	 * - 희망카드개수
	 * - 명세서수령방법
	 * - 수령인
	 * - 수령인연락처
	 * - 주소(우편번호,기본,상세)
	 * - 부재시대리수령여부
	 *
	 * @param idxUser 식별자(사용자)
	 * @param dto 발급정보
	 * @return 식별자(카드발급신청)
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardDto.CardIssuance postCardIssuances(Long idxUser, CardDto.CardIssuance.CardIssuanceRequest dto) {
		//
		//	todo: 권한체크 (법인관리자)
		//
		User user = serviceUser.getUser(idxUser);
		user.corp().staffs(dto.getStaffs());
		repoUser.save(user);
		CardIssuance issuance = CardIssuance.builder()
				.corp(user.corp())
				.staffs(dto.getStaffs())
				.reqCards(dto.getReqCards())
				.reception(dto.getReception())
				.recipient(dto.getRecipient())
				.recipientNo(dto.getRecipientNo())
				.recipientAddress(Address.builder()
						.addressZipCode(dto.getRecipientAddress().getZip())
						.addressBasic(dto.getRecipientAddress().getBasic())
						.addressDetails(dto.getRecipientAddress().getDetail())
						.build())
				.substituteRecipient(dto.isSubstituteRecipient())
				.build();
		issuance = repoIssuance.save(issuance);
		if (issuance.corp().recipientAddress() == null) {
			issuance.corp().recipientAddress(issuance.recipientAddress());
		}
		//
		//	todo: 카드발급요청
		//
		return CardDto.CardIssuance.from(issuance);
	}

	/**
	 * 발급신청내역(건) 조회
	 *
	 * - 직원수
	 * - 희망카드개수
	 * - 명세서수령방법
	 * - 수령인
	 * - 수령인연락처
	 * - 주소(우편번호,기본,상세)
	 * - 부재시대리수령여부
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxCardIssuance 식별자(카드발급신청)
	 * @return 발급신청내역(건)
	 */
	public CardDto.CardIssuance getCardIssuance(Long idxUser, Long idxCardIssuance) {
		User user = serviceUser.getUser(idxUser);
		return repoIssuance.findById(idxCardIssuance)
				.filter(issuance -> issuance.user().equals(user))
				.map(CardDto.CardIssuance::from).orElseThrow(
						() -> EntityNotFoundException.builder()
								.entity("CardIssuance")
								.idx(idxCardIssuance)
								.build()
				);
	}

	/**
	 * 발급신청이력(목록) 조회
	 *
	 * -
	 *
	 * @param idxUser 식별자(사용자)
	 * @param pageable 페이지 정보
	 * @return 발급신청이력(목록)
	 */
	public Page<CardDto.CardIssuance> getCardIssuances(Long idxUser, Pageable pageable) {
		User user = serviceUser.getUser(idxUser);
		return repoIssuance.findByUser(user, pageable).map(CardDto.CardIssuance::from);
	}

	/**
	 * 카드 등록
	 *
	 * @param idxUser 식별자(사용자)
	 * @param dto 등록정보
	 * @return 등록된 카드정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public CardDto postCards(Long idxUser, CardDto.CardRegister dto) {
		User user = serviceUser.getUser(idxUser);
		Long creditLimit = user.creditLimit();
		if (creditLimit == null) {
			if (user.authorities().stream().anyMatch(o -> o.role().equals(Role.ROLE_MASTER))) {
				creditLimit = user.corp().creditLimit();
			} else {
				if (log.isErrorEnabled()) {
					log.error("([ postCards ]) EMPTY CREDIT LIMIT(NOT MASTER), $user.idx='{}', $dto='{}'", user.idx(), dto);
				}
				throw new RuntimeException("마스터가 아닌데 월한도가 설정되어 있지 않음");
			}
		}
		//
		//	todo: 카드 분실/재발급 후 재등록하는 경우에 대한 처리
		//
		repo.disableCards(user);
		Card card = repo.save(Card.builder()
				.cardNo(dto.getCardNo())
				.cvc(dto.getCvc())
				.cvt(dto.getCvt())
				.creditLimit(creditLimit)
				.status(CardStatus.CS_ACTIVATED)
				.password(passwordEncoder.encode(dto.getPassword()))
				.domestic(true)
				.overseas(false)
				.corp(user.corp())
				.owner(user)
				.build());
		repoUser.save(user.card(card));
		return CardDto.from(card);
	}

	/**
	 * 카드 정보조회
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxCard 식별자(카드)
	 * @return 카드 정보
	 */
	public CardDto getCard(Long idxUser, Long idxCard) {
		//
		//	todo: authority check
		//
		return repo.findById(idxCard)
				.map(CardDto::from)
				.orElseThrow(
						() -> EntityNotFoundException.builder()
								.idx(idxCard)
								.entity("card")
								.build()
				);
	}

	/**
	 * 카드 정보변경 - 값이 존재하는 필드만 패치
	 *
	 * - 국내결제가능여부
	 * - 해외결제가능여부
	 * - 월한도
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxCard 식별자(카드)
	 * @param dto 변경정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public void patchCard(Long idxUser, Long idxCard, CardDto.CardPatch dto) {
		User user = serviceUser.getUser(idxUser);
		Card card = repo.findById(idxCard).orElseThrow(
				() -> EntityNotFoundException.builder()
						.idx(idxCard)
						.entity("card")
						.build()
		);
		if (!user.corp().equals(card.corp())) {
			throw UnauthorizedException.builder()
					.account(user.email())
					.idx(card.idx())
					.build();
		}
		//
		//	todo: authorities check
		//
		if (dto.getDomestic() != null) {
			card.domestic(dto.getDomestic());
		}
		if (dto.getOverseas() != null) {
			card.overseas(dto.getOverseas());
		}
		if (dto.getCreditLimit() != null) {
			if (user.authorities().stream().map(Authority::role).anyMatch(Role::isUpdatableCreditLimit)) {
				card.creditLimit(dto.getCreditLimit());
			}
		}
	}

	/**
	 * 카드 비밀번호 변경
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxCard 식별자(카드)
	 * @param dto 변경정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public void putCardPassword(Long idxUser, Long idxCard, CardDto.CardPasswordReset dto) {
		User user = serviceUser.getUser(idxUser);
		Card card = repo.findById(idxCard).orElseThrow(
				() -> EntityNotFoundException.builder()
						.idx(idxCard)
						.entity("card")
						.build()
		);
		if (!card.owner().equals(user)) {
			throw UnauthorizedException.builder()
					.account(user.email())
					.idx(card.idx())
					.build();
		}
		if (!card.cvc().equals(dto.getCvc())) {
			throw UnverifiedException.builder()
					.idx(card.idx())
					.resource(UnverifiedException.Resource.CVC)
					.build();
		}
		if (!card.cvt().equals(dto.getCvt())) {
			throw UnverifiedException.builder()
					.idx(card.idx())
					.resource(UnverifiedException.Resource.CVT)
					.build();
		}
		card.password(passwordEncoder.encode(dto.getPassword()));
	}

	/**
	 * 카드 상태변경 - 활성화 / 비활성화 / 분실신고 / 분실신고해제
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxCard 식별자(카드)
	 * @param dto 상태정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public void putCardStatus(Long idxUser, Long idxCard, CardDto.CardStatusPatch dto) {
		User user = serviceUser.getUser(idxUser);
		Card card = repo.findById(idxCard).orElseThrow(
				() -> EntityNotFoundException.builder()
						.idx(idxCard)
						.entity("card")
						.build()
		);
		if (!card.status().equals(dto.getStatus())) {
			if (CardStatus.CS_LOST_REPORTED.equals(dto.getStatus())) {
				//
				//	todo: 분실신고 처리
				//
				return;
			}
			if (CardStatus.CS_LOST_REPORTED.equals(card.status())) {
				if (!card.cvc().equals(dto.getCvc())) {
					throw MismatchedException.builder().category(MismatchedException.Category.VERIFICATION_CODE).build();
				}
				if (!card.cvt().equals(dto.getCvt())) {
					throw MismatchedException.builder().category(MismatchedException.Category.VALID_THRU).build();
				}
				if (!card.password().equals(dto.getPassword())) {
					throw MismatchedException.builder().category(MismatchedException.Category.PASSWORD).build();
				}
				//
				//	todo: 분실신고 해제 처리
				//
				return;
			}
			card.status(dto.getStatus());
		}
	}

	/**
	 * 카드 재발급 신청
	 *
	 * - 수령인, 수령인연락처, 수령지(주소), 부재시대리수령여부
	 *
	 * @param idxUser 식별자(사용자)
	 * @param dto 재발급신청정보
	 */
	public void postCardReissue(Long idxUser, CardDto.CardReissue dto) {
		//
		//	todo: 카드 재발급 신청
		//
	}

	/**
	 * 카드 인증(비밀번호)
	 *
	 * @param idxUser 식별자(사용자)
	 * @param dto 패스워드(카드)
	 */
	public void checkCardAuthentication(Long idxUser, CardDto.CardAuthentication dto) {
		User user = serviceUser.getUser(idxUser);
		if (user.card() == null) {
			throw CardNotRegisteredException.builder()
					.account(user.email())
					.build();
		}
		if (!passwordEncoder.matches(dto.getPassword(), user.card().password())) {
			throw MismatchedException.builder()
					.category(MismatchedException.Category.PASSWORD)
					.build();
		}
	}

	/**
	 * 카드 해지
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxCard 식별자(카드)
	 */
	public void deleteCard(Long idxUser, Long idxCard) {
		//
		//	todo: 카드 해지
		//
	}
}
