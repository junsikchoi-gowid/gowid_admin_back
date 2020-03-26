package com.nomadconnection.dapp.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.BankDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.dto.UserDto;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import com.nomadconnection.dapp.codef.io.helper.HttpRequest;
import com.nomadconnection.dapp.codef.io.sandbox.bk.*;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BankService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final ScrapingService serviceScraping;
	private final CodefService serviceCodef;


	private final UserRepository repoUser;
	private final ResBatchListRepository repoResBatchList;

	private final ResAccountRepository repoResAccount;
	private final ResAccountHistoryRepository repoResAccountHistory;

	private final ConnectedMngRepository repoConnectedMng;
	private final ResBatchRepository repoResBatch;

	private final PasswordEncoder encoder;
	private final VerificationCodeRepository repoVerificationCode;

	private final String urlPath = CommonConstant.getRequestDomain();
	private ForkJoinPool forkJoinPool = new ForkJoinPool();

	public ResponseEntity findConnectedIdList(ConnectedMngDto.ConnectedId dto, Long idx) {
		return ResponseEntity.ok().body(BusinessResponse.builder().data(dto).build());
	}

	public ResponseEntity findAccountList(ConnectedMngDto.Account dto, Long idx) {
		return ResponseEntity.ok().body(BusinessResponse.builder().data(dto).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public Long saveLog(String account) {
		ResBatchList result = repoResBatchList.save(ResBatchList.builder().account(account).build());
		return result.idx();
	}


	private JSONObject[] getApiResult(String str) throws ParseException {
		JSONObject[] result = new JSONObject[2];

		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParse.parse(str);

		result[0] = (JSONObject) jsonObject.get("result");
		result[1] = (JSONObject) jsonObject.get("data");

		return result;
	}



	/**
	 * (기간별) 일별 입출금 잔고
	 * @param idx 엔터티(사용자)
	 * @param dto 보유정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity dayBalance(BankDto.DayBalance dto, Long idx) {

		String strDate = dto.getDay();
		if(strDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Calendar c1 = Calendar.getInstance();
			strDate = sdf.format(c1.getTime());
		}

		List<ResAccountRepository.CaccountCountDto> transactionList = repoResAccount.findDayHistory(strDate.substring(0, 6) + "00", strDate.substring(0, 6) + "32", idx);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
	}

	/**
	 * (기간별) 월별 입출금 잔고
	 * @param idx 엔터티(사용자)
	 * @param dto 보유정보
	 */
	public ResponseEntity monthBalance(BankDto.MonthBalance dto, Long idx) {

		String startDate = dto.getMonth();
		String endDate = dto.getMonth();
		if(startDate == null) startDate = getMonth(-11);
		if(endDate == null) endDate = getMonth(0);

		List<ResAccountRepository.CaccountMonthDto> transactionList = repoResAccount.findMonthHistory(startDate, endDate, idx);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
	}

	/**
	 * (기간별) 일별 입출금 잔고
	 * @param idx 엔터티(사용자)
	 */
	public ResponseEntity burnRate(Long idx) {

		List<Long> firstBalance = repoResAccount.findBalance(idx);

		Long longFirstBalance = 0L;
		Long longEndBalance = 0L;

		longFirstBalance = Long.valueOf(firstBalance.get(0));
		longEndBalance = Long.valueOf(firstBalance.get(3));

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
	 * 유저의 계좌정보
	 * @param idx 엔터티(사용자)
	 */
	@Transactional(readOnly = true)
	public ResponseEntity accountList(Long idx) {

		List<BankDto.ResAccountDto> resAccount = repoResAccount.findConnectedId(idx).stream()
				.map(BankDto.ResAccountDto::from)
				.collect(Collectors.toList());

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.size(resAccount.size())
				.data(resAccount).build());
	}

	/**
	 *
	 * 거래내역
	 * @param dto 보유정보
	 * @param idx 엔터티(사용자)
	 */
	@Transactional(readOnly = true)
	public ResponseEntity transactionList(BankDto.TransactionList dto, Long idx, Integer page, Integer pageSize) {
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

		List<ResAccountRepository.CaccountHistoryDto> transactionList ;

		if(strDate != null && strDate.length() == 6){
			transactionList = repoResAccount.findAccountHistory( strDate + "00" , strDate + "32", dto.getResAccount() , idx, pageSize, pageSize*(page-1), intIn, intOut, booleanForeign);
		}else{
			transactionList = repoResAccount.findAccountHistory( strDate , strDate, dto.getResAccount(), idx, pageSize, pageSize*(page-1), intIn, intOut, booleanForeign);
		}
		return ResponseEntity.ok().body(BusinessResponse.builder().data(transactionList).build());
	}

	/**
	 * 계좌 별명수정
	 * @param idx 엔터티(사용자)
	 * @param dto 보유정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity nickname(BankDto.Nickname dto, Long idx) {

		ResAccount resAccount = repoResAccount.findById(dto.getIdxAccount()).orElseThrow(
				() -> new RuntimeException("ACCOUNT NOT FOUND")
		);

		resAccount.nickName(dto.getNickName());

		return ResponseEntity.ok().body(BusinessResponse.builder().data(dto).build());
	}

	public ResponseEntity checkAccount(Long idx) throws IOException, InterruptedException {
		ObjectMapper mapper = new ObjectMapper();

		List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(idx);
		if(returnData.size()>0 && Integer.valueOf(returnData.get(0).getMin()) < 3){
			return ResponseEntity.ok().body(BusinessResponse.builder().normal(
					BusinessResponse.Normal.builder()
							.value("Request again after 3 minutes").build()
			).build());
		}
		serviceScraping.scrapingRegister1YearAll(idx);
		Thread.sleep(1000);
		return refresh(idx);
	}

	public ResponseEntity checkAccountList(Long idx) throws IOException, InterruptedException {
		ObjectMapper mapper = new ObjectMapper();

		List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(idx);
		if(returnData.size()>0 && Integer.valueOf(returnData.get(0).getMin()) < 3){
			return ResponseEntity.ok().body(BusinessResponse.builder().normal(
					BusinessResponse.Normal.builder()
							.value("Request again after 3 minutes").build()
			).build());
		}
		serviceScraping.scrapingRegister1YearList(idx);
		Thread.sleep(1000);
		return refresh(idx);
	}

	public ResponseEntity refresh(Long idx) {
		List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(idx);
		return ResponseEntity.ok().body(BusinessResponse.builder().data(returnData).build());
	}


	public static void sendREST(String sendUrl, String jsonValue) throws IllegalStateException {

		String inputLine = null;

		try{
			URL url = new URL(sendUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setConnectTimeout(1000);
			conn.setReadTimeout(1000);
			OutputStream os = conn.getOutputStream();
			os.write(jsonValue.getBytes("UTF-8"));

			os.flush();
			try {
				conn.getInputStream();
			}catch (Exception e){}

			conn.disconnect();
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public List<ConnectedMng> getConnectedMng(Long idx) {
		return repoConnectedMng.findByIdxUser(idx);
	}


	@Transactional(rollbackFor = Exception.class)
	List<ResAccount> getFindByConnectedIdAndResAccountDepositIn(String connectedId, List<String> asList) {
		return repoResAccount.findByConnectedIdAndResAccountDepositIn(connectedId, asList);
	}

	@Transactional(rollbackFor = Exception.class)
	List<ConnectedMng> getFindByIdxUser(Long idx) {
		return repoConnectedMng.findByIdxUser(idx);
	}

	@Transactional
	public ResBatch startBatchLog(Long userIdx) {
		return repoResBatch.save(ResBatch.builder()
				.idxUser(userIdx)
				.endFlag(false)
				.build());
	}

	@Transactional
	public void endBatchLog(Long idx) {
		repoResBatch.findById(idx).ifPresent(resBatch -> {
			repoResBatch.save(
					ResBatch.builder()
							.idx(idx)
							.idxUser(resBatch.idxUser())
							.endFlag(true)
							.build());
		});
	}

	public ResponseEntity monthInOutSum(BankDto.MonthInOutSum dto, Long idx) {

		boolean b = Pattern.matches("\\d{6}", dto.getDate());

		String start = dto.getDate();
		String end = dto.getDate();

		if(b){
			start = start + "00";
			end = end + "32";
		}else{

		}

		ResAccountHistoryRepository.CMonthInOutSumDto CMonthInOutSumDto = repoResAccountHistory.findMonthInOutSum(start, end ,idx);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(CMonthInOutSumDto).build());
	}
}