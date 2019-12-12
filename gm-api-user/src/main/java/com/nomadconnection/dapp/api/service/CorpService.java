package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.controller.ResxController;
import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.CorpStatus;
import com.nomadconnection.dapp.core.domain.ResxCategory;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.repository.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.resx.config.ResourceConfig;
import com.nomadconnection.dapp.resx.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"unused", "SameParameterValue"})
public class CorpService {

	private final ResourceConfig configResx;
	private final ResourceService serviceResx;
	private final UserService serviceUser;
	private final UserRepository repoUser;

	private final CorpRepository repo;

	public Path getResxStockholdersListPath(Long idxCorp) {
		return Paths.get(configResx.getRoot(), ResxCategory.RESX_CORP_SHAREHOLDERS_LIST.name(), idxCorp.toString()).toAbsolutePath().normalize();
	}

	public String getResxStockholdersListUri(Long idxCorp) {
		return configResx.getResxUriPrefix()
				+ ResxController.URI.BASE
				+ ResxController.URI.STOCKHOLDERSLIST
				+ "?corp="
				+ idxCorp.toString();
	}

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
		return repo.findByBizRegNo(bizRegNo).isPresent();
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
	@Transactional(rollbackFor = Exception.class)
	public CorpDto registerCorp(Long idxUser, CorpDto.CorpRegister dto) {

		//	중복체크
		if (repo.findByBizRegNo(dto.getBizRegNo()).isPresent()) {
			if (log.isDebugEnabled()) {
				log.debug("([ registerCorp ]) BRN ALREADY EXIST, $user.idx='{}', $brn='{}'", idxUser, dto.getBizRegNo());
			}
			throw AlreadyExistException.builder()
					.resource(dto.getBizRegNo())
					.build();
		}

		//	사용자 조회
		User user = serviceUser.getUser(idxUser);

		//	법인정보 저장(상태: 대기)
		Corp corp = repo.save(Corp.builder()
				.user(user)
				.name(dto.getName())
				.bizRegNo(dto.getBizRegNo())
				.reqCreditLimit(dto.getReqCreditLimit())
				.bankAccount(BankAccount.builder()
						.bankAccount(dto.getBankAccount().getAccount())
						.bankAccountHolder(dto.getBankAccount().getAccountHolder())
						.build())
				.status(CorpStatus.PENDING)
				.build());

		//	사용자-법인 매핑
		repoUser.save(user.corp(corp));

		//	주주명부 저장경로
		Path path = getResxStockholdersListPath(corp.idx());

		//	법인정보 갱신(주주명부)
//		corp.setResxStockholdersList(CorpStockholdersListResx.builder()
//				.resxStockholdersListPath(path.toString())
//				.resxStockholdersListFilenameOrigin(dto.getResxShareholderList().getOriginalFilename())
//				.resxStockholdersListSize(dto.getResxShareholderList().getSize())
//				.build());

		// 주주명부 저장
//		serviceResx.save(dto.getResxShareholderList(), path, true);

		//	fixme: dummy data - credit limit check
		Long creditLimit = dto.getReqCreditLimit();

		//	법인정보 갱신(상태: 승인/거절, 법인한도)
		corp.creditLimit(creditLimit);
		corp.status(CorpStatus.APPROVED);

		return CorpDto.builder()
				.idx(corp.idx())
				.name(corp.name())
				.bizRegNo(corp.bizRegNo())
				.uriShareholderList(getResxStockholdersListUri(corp.idx()))
				.creditLimit(corp.creditLimit())
				.build();
	}

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
