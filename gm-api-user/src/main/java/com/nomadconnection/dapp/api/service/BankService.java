package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchListRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.res.ResBatchList;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import org.springframework.util.StringUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

	private final ScrapingService serviceScraping;
	private final UserRepository repoUser;
	private final CorpRepository repoCorp;
	private final RiskRepository repoRisk;
	private final ResBatchListRepository repoResBatchList;
	private final ResAccountRepository repoResAccount;
	private final ResAccountHistoryRepository repoResAccountHistory;
	private final ResBatchRepository repoResBatch;
	private final PasswordEncoder encoder;

	/**
	 * (?????????) ?????? ????????? ??????
	 * @param idx ?????????(?????????)
	 * @param dto ????????????
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity dayBalance(BankDto.DayBalance dto, Long idx) {

		String strDate = dto.getDay();
		String endDate = dto.getDay();
		if(strDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Calendar c1 = Calendar.getInstance();
			endDate = sdf.format(c1.getTime());
			c1.add(Calendar.MONTH, -1);
			c1.add(Calendar.DATE, 1);
			strDate = sdf.format(c1.getTime());

		}

		List<ResAccountRepository.CaccountCountDto> transactionList = repoResAccount.findDayHistory(strDate, endDate, idx);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
	}

	/**
	 * (?????????) ?????? ????????? ??????
	 * @param idx ?????????(?????????)
	 * @param dto ????????????
	 */
	public ResponseEntity monthBalance(BankDto.MonthBalance dto, Long idx) {

		String startDate = dto.getMonth();
		String endDate = dto.getMonth();
		if(startDate == null) {
			startDate = getMonth(-11);
		}
		if(endDate == null) {
			endDate = getMonth(0);
		}

		List<ResAccountRepository.CaccountMonthDto> transactionList = repoResAccount.findMonthHistory(startDate, endDate, idx);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
	}


	/**
	 * BrunRate
	 * @param idxUser ?????????(?????????)
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity burnRate(Long idxUser, Long idxCorp) {

		if( idxCorp != null ){
			Corp corp = repoCorp.findById(idxCorp).orElseThrow(
					() -> CorpNotRegisteredException.builder().account(idxCorp.toString()).build()
			);
			idxUser = corp.user().idx();
		}

		List<Long> firstBalance = repoResAccount.findBalance(idxUser);

		Long longFirstBalance = 0L;
		Long longEndBalance = 0L;

		if (!ObjectUtils.isEmpty(firstBalance)) {
			longFirstBalance = Long.valueOf(firstBalance.get(0));
			if (firstBalance.size() > 3) {
				longEndBalance = Long.valueOf(firstBalance.get(3));
			}
		}

		Long BurnRate = (longFirstBalance - longEndBalance) / 3 ;
		Integer intMonth = 1 ;
		if( BurnRate > 0 ){
			intMonth = (int)Math.ceil((double)longEndBalance / BurnRate);
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				BankDto.BurnRate.builder()
						.burnRate(BurnRate)
						.month(intMonth)
						.build()
		).build());
	}

	private String getMonth(int i) {
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Date date = new Date();
		cal.setTime(date);
		cal.add(Calendar.MONDAY, i);
		return  df.format(cal.getTime());
	}

	/**
	 * ????????? ????????????
	 * @param idxUser ?????????(?????????)
	 */
	@Transactional(readOnly = true)
	public ResponseEntity accountList(Long idxUser, Long idxCorp, Boolean isMasking) {

		//todo auth
		idxUser = getaLong(idxUser, idxCorp);

		List<BankDto.ResAccountDto> resAccount = repoResAccount.findResAccount(idxUser).stream()
				.map(account -> BankDto.ResAccountDto.from(account, isMasking))
				.collect(Collectors.toList());

		for (BankDto.ResAccountDto dto : resAccount) {
			ResBatchList historyData = repoResBatchList.findFirstByAccountOrderByUpdatedAtDesc(dto.getResAccount());
			if(historyData != null ) {
				dto.setErrCode(historyData.errCode());
				dto.setErrMessage(historyData.errMessage());
				dto.setScrpaingUpdateTime(historyData.getUpdatedAt());
			}
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.size(resAccount.size())
				.data(resAccount).build());
	}

	/**
	 * ????????? ????????????
	 * @param idxUser ?????????(?????????)
	 */
	@Transactional(readOnly = true)
	public ResponseEntity accountListExt(Long idxUser, Long idxCorp, Boolean isMasking) {

		//todo auth
		idxUser = getaLong(idxUser, idxCorp);

		List<BankDto.ResSimpleAccountDto> resAccount = repoResAccount.findResAccount(idxUser).stream()
				.map(account -> BankDto.ResSimpleAccountDto.from(account, isMasking))
				.collect(Collectors.toList());

		for (BankDto.ResSimpleAccountDto dto : resAccount) {
			ResBatchList historyData = repoResBatchList.findFirstByAccountOrderByUpdatedAtDesc(dto.getResAccount());
			if(historyData != null ) {
				dto.setErrCode(historyData.errCode());
				dto.setErrMessage(historyData.errMessage());
				dto.setScrpaingUpdateTime(historyData.getUpdatedAt());
			}
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.size(resAccount.size())
				.data(resAccount).build());
	}

	/**
	 *
	 * ????????????
	 * @param dto ????????????
	 * @param idxUser ?????????(?????????)
	 */
	@Transactional(readOnly = true)
	public ResponseEntity transactionList(BankDto.TransactionList dto, Long idxUser, Integer page, Integer pageSize, Long idxCorp) {
		//todo auth
		idxUser = getaLong(idxUser, idxCorp);

		String strDate = dto.getSearchDate();
		Integer intIn = 0, intOut = 0, booleanForeign = 0;
		if (dto.getResInOut() != null) {
			if (dto.getResInOut().toLowerCase().equals("in")) {
				intIn = 1;
				intOut = 0;
			} else if (dto.getResInOut().toLowerCase().equals("out")) {
				intIn = 0;
				intOut = 1;
			}
		}
		if( dto.getBoolForeign() != null){
			if(dto.getBoolForeign()) {
				booleanForeign = 1;
			}
		}

		String currency = dto.getCurrency();
		if(StringUtils.isEmpty(currency)){
			Optional<ResAccount> resAccount = repoResAccount.findTopByResAccount(dto.getResAccount());
			if( resAccount != null ){
				currency = resAccount.get().resAccountCurrency();
			}
		}

		List<ResAccountRepository.CaccountHistoryDto> transactionList ;

		if(strDate != null && strDate.length() == 6){
			transactionList = repoResAccount.findAccountHistory( strDate + "00" , strDate + "32", dto.getResAccount() , idxUser, pageSize, pageSize*(page-1), intIn, intOut, booleanForeign, currency.toUpperCase());
		}else{
			transactionList = repoResAccount.findAccountHistory( strDate , strDate, dto.getResAccount(), idxUser, pageSize, pageSize*(page-1), intIn, intOut, booleanForeign, currency.toUpperCase());
		}
		return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
	}

	/**
	 * ?????? ????????????
	 * @param dto ????????????
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity nickname(BankDto.Nickname dto) {

		ResAccount resAccount = repoResAccount.findById(dto.getIdxAccount()).orElseThrow(
				() -> new RuntimeException("ACCOUNT NOT FOUND")
		);

		resAccount.nickName(dto.getNickName());

		return ResponseEntity.ok().body(BusinessResponse.builder().data(dto).build());
	}

	public ResponseEntity checkAccount(Long idx) throws InterruptedException {
		List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefreshUser(idx);
		if(returnData.size()>0 && Integer.valueOf(returnData.get(0).getMin()) < 3){
			return ResponseEntity.ok().body(BusinessResponse.builder().normal(
					BusinessResponse.Normal.builder()
							.value("Request again after 3 minutes").build()
			).build());
		}
		serviceScraping.scrapingRegister1YearAll(idx , null);
		Thread.sleep(1000);
		return refresh(idx, null);
	}

	public ResponseEntity checkAccountList(Long idx) throws InterruptedException {

		List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefreshUser(idx);
		if(returnData.size()>0 && Integer.valueOf(returnData.get(0).getMin()) < 3){
			return ResponseEntity.ok().body(BusinessResponse.builder().normal(
					BusinessResponse.Normal.builder()
							.value("Request again after 3 minutes").build()
			).build());
		}
		serviceScraping.scrapingRegister1YearList(idx);
		Thread.sleep(1000);
		return refresh(idx,null);
	}

	public ResponseEntity checkAccountList45(Long idx) throws InterruptedException {
		serviceScraping.scrapingBankN45DayDataList(idx);
		Thread.sleep(1000);
		return refresh(idx,null);
	}


	@Transactional(readOnly = true)
	public ResponseEntity refresh(Long idxUser, Long idxCorp) {

		idxUser = getaLong(idxUser, idxCorp);
		User user = repoUser.findById(idxUser).get();

		List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefreshUser(user.idx());

		return ResponseEntity.ok().body(BusinessResponse.builder().data(returnData).build());
	}

	private Long getaLong(Long idxUser, Long idxCorp) {
		if(idxCorp != null){
			if(repoUser.findById(idxUser).get().authorities().stream().anyMatch(o -> (o.role().equals(Role.GOWID_ADMIN) || o.role().equals(Role.GOWID_USER)))){
				idxUser = repoCorp.searchIdxUser(idxCorp);
			}
		}
		return idxUser;
	}

	public ResponseEntity monthInOutSum(BankDto.MonthInOutSum dto, Long idxUser, Long idxCorp) {

		idxUser = getaLong(idxUser, idxCorp);

		boolean b = Pattern.matches("\\d{6}", dto.getDate());

		String start = dto.getDate();
		String end = dto.getDate();

		if(b){
			start = start + "00";
			end = end + "32";
		}else{

		}

		ResAccountHistoryRepository.CMonthInOutSumDto CMonthInOutSumDto = repoResAccountHistory.findMonthInOutSum(start, end ,idxUser);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(CMonthInOutSumDto).build());
	}
}