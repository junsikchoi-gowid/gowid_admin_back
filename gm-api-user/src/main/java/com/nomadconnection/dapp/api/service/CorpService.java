package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.core.domain.corp.Corp;
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

	public void save(Corp corp){
		repo.save(corp);
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
