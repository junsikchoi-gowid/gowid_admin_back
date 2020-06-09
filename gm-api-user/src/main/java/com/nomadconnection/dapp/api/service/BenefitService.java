package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BenefitDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.core.domain.Benefit;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.repository.BenefitRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BenefitService {

	private final UserRepository repoUser;
	private final BenefitRepository repoBenefit;

	private final AwsS3Service s3Service;

	/**
	 * 베네핏 생성
	 *
	 * @param idx_user 생성 User idx
	 * @param dto      생성정보
	 * @return 베네핏 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public BenefitDto.BenefitRes createBenefit(Long idx_user, BenefitDto.BenefitReq dto) {
		User user = findUser(idx_user);
		// TODO: ROLE 필터

		return BenefitDto.BenefitRes.from(repoBenefit.save(Benefit.builder()
				.title(dto.getTitle())
				.content(dto.getContent())
				.summary(dto.getSummary())
				.build()));
	}

	/**
	 * 베네핏 파일 등록
	 *
	 * @param file        등록정보
	 * @param idx_benefit Benefit idx
	 * @return 베네핏 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public BenefitDto.BenefitRes uploadFile(MultipartFile file, Long idx_benefit) {
		Benefit benefit = findBenefit(idx_benefit);
		String fileName = UUID.randomUUID().toString();
		try {
			File uploadFile = new File(fileName);
			FileOutputStream fos = new FileOutputStream(uploadFile);
			fos.write(file.getBytes());
			fos.close();
			String s3Key = "benefit/" + idx_benefit + "/" + fileName;
			String s3Link = s3Service.s3FileUpload(uploadFile, s3Key);
			uploadFile.delete();

			return BenefitDto.BenefitRes.from(repoBenefit.save(benefit.fname(fileName)
					.orgfname(file.getOriginalFilename())
					.size(file.getSize()))
					.s3Key(s3Key)
					.s3Link(s3Link)
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 베네핏 목록
	 *
	 * @return 베네핏 정보 목록
	 */
	@Transactional(readOnly = true)
	public List<BenefitDto.BenefitRes> getBenefits() {
		return repoBenefit.findAllByDisabledFalse().stream().map(BenefitDto.BenefitRes::from).collect(Collectors.toList());
	}

	/**
	 * 베네핏 목록
	 *
	 * @return 베네핏 정보 목록
	 */
	@Transactional(readOnly = true)
	public BenefitDto.BenefitRes getBenefit(Long idx_benefit) {
		return BenefitDto.BenefitRes.from(findBenefit(idx_benefit));
	}

	private User findUser(Long idx_user) {
		return repoUser.findById(idx_user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("User")
						.idx(idx_user)
						.build()
		);
	}

	private Benefit findBenefit(Long idx_benefit) {
		return repoBenefit.findById(idx_benefit).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("Benefit")
						.idx(idx_benefit)
						.build()
		);
	}
}
