package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.DeptDto;
import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.api.exception.DeptNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.core.domain.Dept;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.repository.DeptRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.DeptCustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeptService {

	private final UserService serviceUser;

	private final DeptRepository repo;
	private final UserRepository repoUser;

	/**
	 * 등록된 부서 목록 조회
	 *
	 * @param idxUser 식별자(사용자)
	 * @return 등록된 부서 목록
	 */
	@Transactional(rollbackFor = Exception.class)
	public List<DeptDto> getDepts(Long idxUser) {
		return repo.findByCorp(serviceUser.getUser(idxUser).corp())
				.map(DeptDto::new)
				.collect(Collectors.toList());
	}

	@Transactional
	public List<DeptCustomRepository.DeptWithMemberCountDto> depts(Long idxUser) {
		return repo.findDeptWithMemberCount(serviceUser.getUser(idxUser).corp());
	}

	/**
	 * 부서 추가
	 *
	 * @param idxUser 엔터티(사용자)
	 * @param name 부서 명
	 * @return 추가된 부서정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public DeptDto addDept(Long idxUser, String name) {
		User user = serviceUser.getUser(idxUser);
		if (repo.findByCorpAndName(user.corp(), name).isPresent()) {
			throw AlreadyExistException.builder()
					.status(HttpStatus.BAD_REQUEST)
					.resource(name)
					.build();
		}
		return new DeptDto(repo.save(Dept.builder()
				.corp(user.corp())
				.name(name)
				.build()));
	}

	/**
	 * 부서명 변경
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxDept 식별자(부서)
	 * @param deptName 변경할 부서명
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateDeptName(Long idxUser, Long idxDept, String deptName) {
		User user = serviceUser.getUser(idxUser);
		Dept dept = repo.findById(idxDept).orElseThrow(
				() -> DeptNotFoundException.builder().dept(idxDept).build()
		);
		if (!dept.corp().idx().equals(user.corp().idx())) {
			throw MismatchedException.builder()
					.category(MismatchedException.Category.CORP)
					.object(user.corp().idx())
					.build();
		}
		dept.name(deptName);
	}

	/**
	 * 부서 삭제
	 *
	 * - 부서 내 멤버가 존재하는 경우, 모두 부서 미지정 상태로 설정됨
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxDept 식별자(부서)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteDept(Long idxUser, Long idxDept) {
		User user = serviceUser.getUser(idxUser);
		{
			//
			//	todo: check user authorities
			//
		}
		Dept dept = repo.findById(idxDept).orElseThrow(
				() -> DeptNotFoundException.builder().dept(idxDept).build()
		);
		repoUser.clearDept(dept);
		repo.deleteById(dept.idx());
	}

	/**
	 * 등록된 멤버 조회
	 *
	 * @param idxUser 식별자(사용자)
//	 * @param key 검색어(이름/부서명/이메일)
	 * @param idxDept 식별자(부서)
	 * @return 등록된 멤버 조회
	 */
	@Transactional
//	public List<DeptDto.DeptMember> getDeptMembers(Long idxUser, String key) {
	public List<DeptDto.DeptMember> getDeptMembers(Long idxUser, Long idxDept) {
		return repoUser.findDeptMembers(serviceUser.getUser(idxUser).corp(), idxDept)
				.map(DeptDto.DeptMember::from)
				.collect(Collectors.toList());
//		//
//		//	todo: keyword(name/dept/email) search
//		//
//		return repoUser.findByCorp(serviceUser.getUser(idxUser).corp())
//				.map(DeptDto.DeptMember::from)
//				.collect(Collectors.toList());
	}
}
