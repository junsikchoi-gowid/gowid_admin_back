package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.CardTransaction;
import com.nomadconnection.dapp.core.domain.QCardTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CardTransactionCustomRepositoryImpl extends QuerydslRepositorySupport implements CardTransactionCustomRepository {

	private final QCardTransaction cardTransaction = QCardTransaction.cardTransaction;

	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 */
	public CardTransactionCustomRepositoryImpl() {super(CardTransaction.class); }

	// 카드정보로 카드이용 내역 출력 - 날짜별 총금액
	// 카드리스트의 월간 총금액

	// 카드정보로 카드이용 내역 출력 - 날짜별 총금액
//	@Query(value = "select DATE_FORMAT(usedAt , '%m.%d' ) as asUsedAt \n" +
//			",SUBSTR( _UTF8'일월화수목금토' , DAYOFWEEK(usedAt), 1) AS week \n" +
//			",sum(usedAmount) as usedAmount \n" +
//			"from CardTransaction \n" +
//			"where ( usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:strDate), '%Y%m%d'))- interval 1 month) \n" +
//			"AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:strDate), '%Y%m%d')) \n" +
//			"AND idxCard in (:cards)" +
//			"group by asUsedAt, week "
//			, nativeQuery = true)
//	public List<PerDailyDto> findHistoryByDate(@Param("strDate") String strDate, @Param("cards") List<Long> cards){
//
//		List<PerDailyDto> perDaily = (List<PerDailyDto>) new PerDailyDto();
//		return perDaily;
//	}

	@Override
	public Page<PerHour> findHistoryByHour(String strDate, List<Long> cards, Pageable pageable) {
		return null;
	}

	@Override
	public DetailInfo findHistoryByOne(String strDate, List<Long> cards) {
		return null;
	}

	//public Page<PerHour> findHistoryByHour(String strDate, List<Long> cards, Pageable pageable) {
	//	return null;
	//}

}
