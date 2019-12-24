package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.repository.CardTransactionRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CardTransactionCustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistService {


	private final CardTransactionRepository repo;
	private final UserService serviceUser;

	/**
	 * 해당월의 총금액
	 * @param idx 식별자(사용자), year 검색년도, month 검색월, cards 카드정보
	 * @return 해당달 총금액
	 */
	@Transactional
	public Long monthSum(Long idx, Integer year, Integer month, List<Long> cards) {
		User user = serviceUser.getUser(idx);

		log.info( user.name());
		log.info("([ monthSum ]), $year='{}'", year  );
		log.info("([ monthSum ]), $month='{}'", month  );

		if(cards == null ) {
			// -- 사용가능한 카드리스트 Corp 기준으로 전부가져옴 제약 필요
			log.info("([ monthSum ]), $cards='{}'", "null"  );
			cards = repo.findCardList(user.corp().idx());
		}

		Long longValue = null;
		try {
			longValue = repo.findMonthAmount(String.valueOf(year), String.valueOf(month), cards);
		}catch ( Exception e ){
			log.info("([ monthSum ])", e);
		}

		if( longValue == null){ longValue = 0L; }

		return longValue;
	}

	/**
	 * 이용내역 목록 조회
	 *
	 * @param idx 식별자(사용자), type 형태구분(날짜,항목, 지역), year 검색년도, month 검색월, cards 카드정보
	 * @return 이용내역 목록
	 **/
	@Transactional
	public List<CardTransactionCustomRepository.PerDailyDto> historyByDate(Long idx, Integer year, Integer month, List<Long> cards) {
		log.info("([ historyByDate ]), $idx='{}'", idx  );
		log.info("([ historyByDate ]), $year='{}'", year  );
		log.info("([ historyByDate ]), $month='{}'", month  );
		log.info("([ historyByDate ]), $cards='{}'", cards  );

		List perDaily = repo.findHistoryByDate(year + String.format("%02d",month) + "01", cards);
		return perDaily;
	}


	/*

	public CardTransactionDto.MonthSum monthSum( Long idxUser, Integer year ,Integer month, List<Long> cards){
		User user = serviceUser.getUser(idxUser);

		log.info( user.name());
		log.info("([ monthSum ]), $year='{}'", year  );
		log.info("([ monthSum ]), $month='{}'", month  );

		CardTransactionDto.MonthSum mSum = new CardTransactionDto.MonthSum();
		if(cards == null ) {
			// 회사의 카드리스트
			// -- 사용가능한 카드리스트 Corp 기준으로 전부가져옴 제약 필요
			cards = repo.findCardList(user.corp().idx());
			// 해당월의 총금액
			mSum.setAllAmount(repo.findMonthAmount(String.valueOf(year), String.valueOf(month), cards ));
		}else{
			// 해당월의 총금액
			mSum.setAllAmount(repo.findMonthAmount(String.valueOf(year), String.valueOf(month), cards ));
		}
		return mSum;
	}
	*/

	/**
	 * 이용내역 목록 조회
	 *
	 * @param idxUser 식별자(사용자), type 형태구분(날짜,항목, 지역), year 검색년도, month 검색월, cards 카드정보
	 * @return 이용내역 목록
	 **/
	/*
	@Transactional
	public List<CardTransactionCustomRepository.PerDaily> getHistoryByDate(
			Long idxUser,
			Integer year,
			Integer month,
			List<Long> cardIdx)
	{
		User user = serviceUser.getUser(idxUser);

		log.info("([ monthSum ]), $year='{}'", year  );
		log.info("([ monthSum ]), $month='{}'", month  );
		log.info("([ monthSum ]), $cardIdx='{}'", cardIdx  );


		//CardTransactionDto cardTransactionDto = new CardTransactionDto();

		//cardTransactionDto.setIdx(idxUser);
		// etc : 20190101 의 형태가 필요함
		//cardTransactionDto.setSearchDate( String.valueOf(year) + String.valueOf(month) + "01");

		if (log.isDebugEnabled()) {
			log.debug("([ getHistoryByDate ]) $user='{}' ", user );
		}
		// return repo.findHistoryByDate(cardTransactionDto.getSearchDate(), cardTransactionDto.getCards());
		return null;
	}



			//return repo.findHistoryByHour(cardTransactionDto.getSearchDate(), cardTransactionDto.getCards() ,pageable)
			//.map(CardTransactionDto.HistHeaders::from);


	/**
	 * 기간별 카드 리스트 ( 관리자/일반사용자 )
	 *
	 * @param idxUser 식별자(사용자), year 검색년도, month 검색월, cards 카드정보
	 * @return 이용내역 목록
	 */
	/*
	public List<CardDto.CardBasicInfo> getCardList(Long idxUser,Integer year, Integer month, Pageable pageable) {

		User user = serviceUser.getUser(idxUser);
		{
			if (user.corp() == null) {
				throw CorpNotRegisteredException.builder()
						.account(user.email())
						.build();
			}
		}

		List<CardDto.CardBasicInfo> returnList;

		if (user.authorities().stream().anyMatch(o -> o.role().equals(Role.ROLE_MASTER) || o.role().equals(Role.ROLE_ADMIN) )) {
			returnList = repo.fidnByCardAdmin(user.corp().idx(), pageable).map(HistDto.HistCards::from);
		}else{
			returnList = repo.fidnByCardUser(user.idx(), pageable).map(HistDto.HistCards::from);
		}

		return returnList ;
	}
	*/
}