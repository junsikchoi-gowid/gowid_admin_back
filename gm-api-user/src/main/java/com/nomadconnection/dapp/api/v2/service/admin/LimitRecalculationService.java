package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.exception.limit.LimitAlreadyExistException;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationQueryRepository;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationRepository;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationCondition;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationDetail;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LimitRecalculationService {

	private final LimitRecalculationRepository limitRecalculationRepository;

	private final LimitRecalculationQueryRepository limitRecalculationQueryRepository;

	private final CorpService corpService;

	@Transactional(readOnly = true)
	public Page<LimitRecalculationDto> findAll(LimitRecalculationCondition dto, Pageable pageable){
		List<LimitRecalculationDto> limitRecalculations = limitRecalculationQueryRepository.findAll(dto ,pageable.getPageNumber(), 10);

		return new PageImpl(limitRecalculations, pageable, limitRecalculations.size());
	}

	@Transactional(readOnly = true)
	public LimitRecalculationDto findByCorpAndDate(Long idxCorp, LocalDate date) {
		Corp corp = corpService.findByCorpIdx(idxCorp);
		LimitRecalculation limitRecalculation = limitRecalculationRepository.findByCorpAndDate(corp, date)
			.orElseThrow(() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));

		return LimitRecalculationDto.from(limitRecalculation);
	}

	@Transactional(rollbackFor = Exception.class)
	public void requestRecalculate(Long idxCorp, LimitRecalculationDetail dto) {
		Corp corp = corpService.findByCorpIdx(idxCorp);
		boolean exists = limitRecalculationRepository.findByCorpAndDate(corp, dto.getDate()).isPresent();
		if(exists){
			throw new LimitAlreadyExistException(ErrorCode.Api.RECALCULATION_ALREADY_EXIST);
		}

		save(corp, dto);
	}

	public void save(Corp corp, LimitRecalculationDetail dto){
		LimitRecalculation limitRecalculation = LimitRecalculation.of(corp, dto);

		limitRecalculationRepository.save(limitRecalculation);
	}

}
