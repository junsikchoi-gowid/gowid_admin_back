package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

public class CardTransactionCustomRepositoryImpl extends QuerydslRepositorySupport implements CardTransactionCustomRepository {

	private final QCardTransaction cardTransaction = QCardTransaction.cardTransaction;
	private final QUser user = QUser.user;
	private final QDept dept = QDept.dept;
	private final QCard card = QCard.card;

	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 */
	public CardTransactionCustomRepositoryImpl() {super(CardTransaction.class); }


	@Override
	@Transactional(readOnly = true)
	public List<PerDailyDto> findCustomHistoryByDate(String strDate, List<Long> cards){
		if(strDate == null){
			return Collections.emptyList();
		}
		Query nativeQuery = getEntityManager().createNativeQuery(
				"select DATE_FORMAT(usedAt , '%m.%d' ) as asUsedAt \n" +
						",SUBSTR( _UTF8'일월화수목금토' , DAYOFWEEK(usedAt), 1) AS week \n" +
						",sum(usedAmount) as usedAmount  \n" +
						"from CardTransaction \n" +
						"where ( usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:strDate), '%Y%m%d'))- interval 1 month) \n" +
						"AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:strDate), '%Y%m%d')) \n" +
						"AND idxCard in (:cards)" +
						"group by asUsedAt, week ", "PerDailyDtoMapping");
		nativeQuery.setParameter("strDate",strDate);
		nativeQuery.setParameter("cards",cards);

		List<PerDailyDto> resultList  = nativeQuery.getResultList();
		
		return resultList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CardListDto> findCardAdmin(String iYearMon, Long idx) {
		if(iYearMon == null){
			return Collections.emptyList();
		}
		Query nativeQuery = getEntityManager().createNativeQuery(
				"select\n" +
						"		 c.idx as cardIdx, \n" +
						"        c.cardNo, \n" +
						"        c.idxUser, \n" +
						"        sum(ct.usedAmount) as usedAmount  \n" +
						"    from\n" +
						"        CardTransaction ct \n" +
						"        right join Card c on c.idxCorp = :idx and c.idx = ct.idxCard \n" +
						"    where\n" +
						"        (usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:iYearMon), '%Y%m%d'))- interval 1 month)  \n" +
						"        AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:iYearMon), '%Y%m%d'))   \n" +
						"    group by c.idx","CardListDtoMapping");
		nativeQuery.setParameter("iYearMon",iYearMon+"01");
		nativeQuery.setParameter("idx",idx);

		return (List<CardListDto>) nativeQuery.getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CardListDto> findCardUser(String iYearMon, Long idx) {
		if(iYearMon == null){
			return Collections.emptyList();
		}
		Query nativeQuery = getEntityManager().createNativeQuery(
				"select\n" +
						"		 c.idx, \n" +
						"        c.cardNo, \n" +
						"        c.idxUser, \n" +
						"        sum(ct.usedAmount) as usedAmount  \n" +
						"    from\n" +
						"        CardTransaction ct \n" +
						"        right join Card c on c.idxUser = :idx and c.idx = ct.idxCard \n" +
						"    where\n" +
						"        (usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:iYearMon), '%Y%m%d'))- interval 1 month)  \n" +
						"        AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:iYearMon), '%Y%m%d'))   \n" +
						"    group by c.idx","CardListDtoMapping");
		nativeQuery.setParameter("iYearMon",iYearMon);
		nativeQuery.setParameter("idx",idx);

		return (List<CardListDto>) nativeQuery.getResultList();
	}


	@Override
	@Transactional(readOnly = true)
	public DetailInfo findDetailInfo(String strDate, List<Long> cards) {
		return null;
	}


	@Override
	public Page<PerDailyDetailDto> findHistoryByTypeDate(Integer date, List<Long> cards, Pageable pageable) {
//		LocalDateTime tempDate ;
//		JPQLQuery<PerDailyDetailDto> query = from(cardTransaction)
//				.select(Projections.bean(PerDailyDetailDto.class,
//						cardTransaction.idx.as("idx"),
//						cardTransaction.card.cardNo.as("cardNo"),
//						cardTransaction.card.owner.name.as("userName"),
//						cardTransaction.card.owner.dept.name.as("deptName"),
//						cardTransaction.usedAmount.as("usedAmount"),
//						cardTransaction.usedAt.dayOfMonth().as("typeValue"),
//						cardTransaction.memberStoreName.as("memberStoreName")))
//				.where(cardTransaction.usedAt.eq()
		return null;
	}

	@Override
	public Page<PerDailyDetailDto> findHistoryByTypeCategory(Integer date, List<Long> cards, Pageable pageable) {
		return null;
	}

	@Override
	public Page<PerDailyDetailDto> findHistoryByTypeArea(Integer date, List<Long> cards, Pageable pageable) {
		return null;
	}






}
