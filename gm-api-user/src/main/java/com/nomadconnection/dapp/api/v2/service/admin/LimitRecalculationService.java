package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.v2.dto.LimitRecalculationDto;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LimitRecalculationService {

	private final LimitRecalculationRepository limitRecalculationRepository;

	private final CorpService corpService;

	@Transactional
	public Page<LimitRecalculationDto> findAll(Pageable pageable){
		Page<LimitRecalculation> limitRecalculations = limitRecalculationRepository.findAll(pageable);
		List<LimitRecalculationDto> dtos = LimitRecalculationDto.from(limitRecalculations.getContent());
		
		return new PageImpl(dtos, pageable, limitRecalculations.getTotalElements());
	}

	@Transactional
	public List<LimitRecalculationDto> findByCorp(Long idxCorp){
		Corp corp = corpService.findByCorpIdx(idxCorp);
		return limitRecalculationRepository.findByCorp(corp).stream().map(LimitRecalculationDto::from).collect(Collectors.toList());
	}

	@Transactional
	public LimitRecalculationDto findByCorpAndDate(Long idxCorp, LocalDate date) throws Exception {
		Corp corp = corpService.findByCorpIdx(idxCorp);
		LimitRecalculation limitRecalculation = limitRecalculationRepository.findByCorpAndDate(corp, date)
			.orElseThrow(() -> new Exception());    //FIXME : exception

		return LimitRecalculationDto.from(limitRecalculation);
	}

	public void recalculate(){

	}

}
