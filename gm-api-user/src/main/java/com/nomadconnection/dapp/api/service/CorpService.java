package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"unused", "SameParameterValue"})
public class CorpService {

	private final UserService serviceUser;
	private final UserRepository repoUser;

	private final CorpRepository repo;

	/**
	 * 법인등록여부 조회
	 *
	 * @param bizRegNo 사업자등록번호(10 Digits)
	 * @return 법인등록여부
	 */
	public boolean isPresent(String bizRegNo) {
		//
		//	확인필요: 인증상테에서 호출되어야 하는지, 어떤 정보로 인증을 진행해야 하는지
		//
		return repo.findByResCompanyIdentityNo(bizRegNo).isPresent();
	}

	/**
	 * 법인정보등록: 법인명, 사업자등록번호, 결제계좌, 주주명부, 희망법인총한도
	 *
	 * - 법인명
	 * - 사업자등록번호(10 Digits): 기 가입된 번호인 경우 에러
	 * - 결제계좌
	 * - <s>법인인감증명서</s>
	 * - 주주명부
	 * - 희망법인총한도
	 *
	 * @param idxUser 식별자(사용자)
	 * @param dto 등록정보
	 */
//	@Transactional(rollbackFor = Exception.class)
//	public CorpDto registerCorp(Long idxUser, CorpDto.CorpRegister dto) {
//
//		//	중복체크
//		if (repo.findByBizRegNo(dto.getBizRegNo()).isPresent()) {
//			if (log.isDebugEnabled()) {
//				log.debug("([ registerCorp ]) BRN ALREADY EXIST, $user.idx='{}', $brn='{}'", idxUser, dto.getBizRegNo());
//			}
//			throw AlreadyExistException.builder()
//					.resource(dto.getBizRegNo())
//					.build();
//		}
//
//		//	사용자 조회
//		User user = serviceUser.getUser(idxUser);
//
//		Corp corpDto = new Corp();
//		BeanUtils.copyProperties(dto, corpDto);
//
//		corpDto.user(user);
//		corpDto.status(CorpStatus.PENDING);
//
//		//	법인정보 저장(상태: 대기)
//		Corp corp = repo.save(corpDto);
//
//		//	주주명부 저장경로
////		Path path = getResxStockholdersListPath(corp.idx());
//
//		//	법인정보 갱신(주주명부)
////		corp.setResxStockholdersList(CorpStockholdersListResx.builder()
////				.resxStockholdersListPath(path.toString())
////				.resxStockholdersListFilenameOrigin(dto.getResxShareholderList().getOriginalFilename())
////				.resxStockholdersListSize(dto.getResxShareholderList().getSize())
////				.build());
//
//		// 주주명부 저장
////		serviceResx.save(dto.getResxShareholderList(), path, true);
//
//		//	fixme: dummy data - credit limit check
//		Long creditLimit = dto.getReqCreditLimit();
//
//		//	법인정보 갱신(상태: 승인/거절, 법인한도)
////		corp.creditLimit(creditLimit);
//		corp.status(CorpStatus.APPROVED);
//
//		return CorpDto.builder()
//				.idx(corp.idx())
//				.name(corp.name())
//				.bizRegNo(corp.bizRegNo())
//				.creditLimit(corp.creditLimit())
//				.build();
//	}

	/**
	 * 동일법인에 소속된 모든 멤버 조회
	 *
	 * @param idxUser 식별자(사용자)
	 * @return 멤버목록
	 */
	@Transactional
	public List<CorpDto.CorpMember> members(Long idxUser) {
		User user = serviceUser.getUser(idxUser);
		{
			if (user.corp() == null) {
				throw CorpNotRegisteredException.builder()
						.account(user.email())
						.build();
			}
		}
		return repoUser.findByCorp(user.corp())
				.map(CorpDto.CorpMember::from)
				.collect(Collectors.toList());
	}
}
