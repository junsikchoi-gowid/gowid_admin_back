package com.nomadconnection.dapp.core.domain.repository.kised;

import com.nomadconnection.dapp.core.abstracts.AbstractJpaTest;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class KisedRepositoryTest extends AbstractJpaTest {

	@Autowired
	private KisedRepository kisedRepository;

	@Autowired
	private CardIssuanceInfoRepository cardIssuanceInfoRepository;

	Long idxCardIssuanceInfo;
	String projectId;

	@BeforeEach
	void tearDown(){
		projectId = String.valueOf(new Random().nextInt(8));
		idxCardIssuanceInfo = 621L;
	}

	private CardIssuanceInfo getOrSaveCardIssuanceInfo(Long idx){
		return cardIssuanceInfoRepository.findByIdx(idx).orElseGet(
			() -> cardIssuanceInfoRepository.save(CardIssuanceInfo.builder().idx(idx).build())
		);
	}

	@Test
	@DisplayName("창업진흥원_신청목록에_저장한다")
	@Transactional
	void save(){
		String licenseNo = "12345678";

		Kised kised = Kised.builder().projectId(projectId).licenseNo(licenseNo).build();

		Kised saved = kisedRepository.save(kised);

		assertThat(saved).isEqualTo(kised);
		assertThat(saved.getProjectId()).isEqualTo(projectId);
	}

	@Test
	@DisplayName("사업자번호와_과제번호로_찾는다")
	@Transactional
	void findOneByLicenseNoAndProjectId() throws Exception {
		save();

		CardIssuanceInfo cardIssuanceInfo = getOrSaveCardIssuanceInfo(idxCardIssuanceInfo);
//		Kised saved = kisedRepository.findByCardIssuanceInfoAndProjectId(cardIssuanceInfo, projectId).orElseThrow(
//			() -> new Exception("Not Found")
//		);

//		assertThat(saved).isNotNull();
//		assertThat(saved.getProjectId()).isEqualTo(projectId);
	}

}
