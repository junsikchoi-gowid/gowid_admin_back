package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.CardIssuanceInfoService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.ShinhanConsumerEmployeesType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.lotte.Lotte_D1200Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.*;
import com.nomadconnection.dapp.core.domain.shinhan.*;
import com.nomadconnection.dapp.core.encryption.shinhan.Seed128;
import com.nomadconnection.dapp.core.utils.NumberUtils;
import com.nomadconnection.dapp.core.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.nomadconnection.dapp.api.util.CommonUtil.replaceHyphen;
import static com.nomadconnection.dapp.api.v2.utils.FullTextJsonParser.*;
import static com.nomadconnection.dapp.api.v2.utils.ScrapingCommonUtils.ifNullReplaceObject;

/**
 * 전문 저장
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class FullTextService {

	//TODO: 파라미터 확인 Corp

	private final D1000Repository repoD1000;
	private final D1100Repository repoD1100;
	private final D1200Repository repoD1200;
	private final D1400Repository repoD1400;
	private final D1510Repository repoD1510;
	private final D1520Repository repoD1520;
	private final D1530Repository repoD1530;
	private final Lotte_D1000Repository repoD1000Lotte;
	private final Lotte_D1100Repository repoD1100Lotte;
	private final Lotte_D1200Repository repoD1200Lotte;
	private final CardIssuanceInfoRepository cardIssuanceInfoRepository;

	@Deprecated
	public D1000 findFirstByIdxCorpIn1000(Long corpIdx){
		return repoD1000.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
				() -> CorpNotRegisteredException.builder().build()
		);
	}

	@Deprecated
	public D1400 findFirstByIdxCorpIn1400(Long corpIdx){
		return repoD1400.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
				() -> CorpNotRegisteredException.builder().build()
		);
	}

	@Transactional(readOnly = true)
	public D1000 findFirstByCardIssuanceInfoIn1000(CardIssuanceInfo cardIssuanceInfo){
		return repoD1000.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
			() -> CorpNotRegisteredException.builder().build()
		);
	}

	@Transactional(readOnly = true)
	public D1400 findFirstByCardIssuanceInfoIn1400(CardIssuanceInfo cardIssuanceInfo){
		return repoD1400.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).orElseThrow(
			() -> CorpNotRegisteredException.builder().build()
		);
	}

	public D1530 findFirstByIdxCorpIn1530(Long corpIdx){
		return repoD1530.findFirstByIdxCorpOrderByUpdatedAtDesc(corpIdx).orElseThrow(
				() -> CorpNotRegisteredException.builder().build()
		);
	}

	private void save1000(D1000 d1000){
		repoD1000.save(d1000);
	}

	public void save1000(D1000 d1000, ConnectedMngDto.CorpInfo financialStatementsParam){
		String[] corNumber = financialStatementsParam.getResCompanyPhoneNumber().split("-");
		d1000.setD006(!StringUtils.hasText(d1000.getD006()) ? financialStatementsParam.getResCompanyEngNm() : d1000.getD006());    //법인영문명
		d1000.setD008(!StringUtils.hasText(d1000.getD008()) ? financialStatementsParam.getResBusinessCode() : d1000.getD008());    //업종코드
		d1000.setD026(corNumber[0]);            //직장전화지역번호
		d1000.setD027(corNumber[1]);            //직장전화국번호
		d1000.setD028(corNumber[2]);            //직장전화고유번호
		d1000.setD036(d1000.getD026());            //신청관리자전화지역번호
		d1000.setD037(d1000.getD027());            //신청관리자전화국번호
		d1000.setD038(d1000.getD028());            //신청관리자전화고유번호
		repoD1000.save(d1000);
	}

	public D1100 build1100(Corp corp, CardIssuanceInfo cardIssuanceInfo){
		return D1100.builder()
			.idxCorp(corp.idx())
			.c007(CommonUtil.getNowYYYYMMDD())
			.c007("")
			.d001(replaceHyphen(corp.resCompanyIdentityNo()))
			.d002("01")
			.d003("3")
			.d004(null)
			.d006("G1")
			.d007("1")
			.d008("00")
			.d009("A")
			.d010("3")
			.d011("0")
			.d012("0")
			.d013("0")
			.d015("N")
			.d018("01")
			.d019("Y")
			.d020("")
			.d021("")
			.d022("2")
			.d023("15")
			.d024("")
			.d025("")
			.d026("")
			.d027(replaceHyphen(corp.resCompanyIdentityNo()))
			.d028("901")
			.d029("")
			.d030("2")
			.d031("")
			.d032("")
			.d033("")
			.d034("")
			.d035("")
			.d036("")
			.d037("")
			.d038("N")
			.d039("")
			.d040(null)
			.d041(null)
			.d042("Y")
			.d043("Y")
			.d044("")
			.d045("")
			.d046("")
			.d047("")
			.d048("Y")
			.d049(null)
			.cardIssuanceInfo(cardIssuanceInfo)
			.build();
	}

	private void save1100(D1100 d1100){
		repoD1100.save(d1100);
	}

	private void save1400(D1400 d1400){
		repoD1400.save(d1400);
	}

	public void save1400(D1400 d1400, ConnectedMngDto.CorpInfo financialStatementsParam){
		String[] corNumber = financialStatementsParam.getResCompanyPhoneNumber().split("-");
		d1400.setD011(!StringUtils.hasText(d1400.getD011()) ? financialStatementsParam.getResBusinessCode() : d1400.getD011());        //업종코드
		d1400.setD029(!StringUtils.hasText(d1400.getD029()) ? financialStatementsParam.getResCompanyEngNm() : d1400.getD029());    //법인영문명
		d1400.setD048(corNumber[0]);        //직장전화지역번호
		d1400.setD049(corNumber[1]);        //직장전화국번호
		d1400.setD050(corNumber[2]);        //직장전화고유번호
		d1400.setD058(d1400.getD048());        //신청관리자전화지역번호
		d1400.setD059(d1400.getD049());        //신청관리자전화국번호
		d1400.setD060(d1400.getD050());        //신청관리자전화고유번호
		repoD1400.save(d1400);
	}

	private void save1510(D1510 d1510){
		repoD1510.save(d1510);
	}

	public void saveDefault1520(D1530 d1530, Corp corp){
		repoD1520.save(D1520.builder()
			.idxCorp(corp.idx())
			.c007(CommonUtil.getNowYYYYMMDD())
			.d003(replaceHyphen(corp.resCompanyIdentityNo())) // 사업자등록번호
			.d004(replaceHyphen(corp.resIssueNo())) // 발급(승인)번호
			.d005(replaceHyphen(corp.resUserIdentiyNo())) // 주민번호
			.d006(corp.resCompanyNm()) // 상호(사업장명)
			.d007("N") // 발급가능여부
			.d008("") // 시작일자
			.d009("") // 종료일자
			.d010("") // 성명
			.d011("") // 주소
			.d012("") // 종목
			.d013("") // 업태
			.d014("") // 작성일자
			.d015(CommonUtil.getNowYYYYMMDD().substring(0,4)) // 귀속연도
			.d016(d1530.getD042()) // 총자산 대차대조표 상의 자본총계(없으면 등기부등본상의 자본금의 액) 희남 버그중
			.d017("") // 매출   손익계산서 상의 매출액
			.d018("") // 납입자본금   대차대조표 상의 자본금
			.d019("") // 자기자본금   대차대조표 상의 자본 총계
			.d020(d1530.getD057()) // 재무조사일   종료일자 (없으면 등기부등본상의 회사성립연월일)
			.build());
	}

	public void save1520(D1520 d1520){
		repoD1520.save(d1520);
	}

	private void save1530(D1530 d1530){
		repoD1530.save(d1530);
	}

	public D1000 build1000(Corp corp, JSONArray jsonArrayResCEOList, CardIssuanceInfo cardIssuanceInfo){
		List<String> listResCeoList = getJSONArrayCeo(jsonArrayResCEOList);
		String ceoType = getCeoType(jsonArrayResCEOList);

		return D1000.builder()
			.idxCorp(corp.idx())
			.c007(CommonUtil.getNowYYYYMMDD())
			.d001(replaceHyphen(OptionalUtil.getOrEmptyString(corp.resCompanyIdentityNo())))
			.d002(replaceHyphen(OptionalUtil.getOrEmptyString(corp.resUserIdentiyNo())))
			.d003(OptionalUtil.getOrEmptyString(corp.resCompanyNm()))
			.d004("400")
			.d005("06")
			.d007(OptionalUtil.getOrEmptyString(corp.resRegisterDate()))
			.d009(ceoType) // 1: 단일대표 2: 개별대표 3: 공동대표
			.d010(listResCeoList.size() >= 2 ? getOnlyKorLan(listResCeoList.get(1)) : "")// 대표이사_성명1
			.d011(listResCeoList.size() >= 3 ? Seed128.encryptEcb(replaceHyphen(listResCeoList.get(2))) : "")// 대표이사_주민번호1
			.d014(listResCeoList.size() >= 6 ? getOnlyKorLan(listResCeoList.get(5)) : "")// 대표이사_성명2
			.d015(listResCeoList.size() >= 7 ? Seed128.encryptEcb(replaceHyphen(listResCeoList.get(6))) : "")// 대표이사_주민번호2
			.d018(listResCeoList.size() >= 10 ? getOnlyKorLan(listResCeoList.get(9)) : "")// 대표이사_성명3
			.d019(listResCeoList.size() >= 11 ? Seed128.encryptEcb(replaceHyphen(listResCeoList.get(10))) : "")// 대표이사_주민번호3
			.d029(null)
			.d030(null)
			.d031(null)
			.d032("대표이사")
			.d033("대표이사")
			.d034(listResCeoList.size() >= 3 ? Seed128.encryptEcb(listResCeoList.get(2).replaceAll("-", "")) : "")// 대표이사_주민번호1
			.d035(listResCeoList.size() >= 2 ? getOnlyKorLan(listResCeoList.get(1)) : "")// 대표이사_성명1
			.d044("0113")
			.d045("5")
			.d047("N")
			.d048("09")
			.d051("10")
			.d052("N")
			.d054("1")
			.d056("N")
			.d057("N")
			.d058(null) // 001 IFRS, 002 외감, 003 비외감, 004 비일반공공
			.d063(null)
			.d067(null)
			.d068(null)
			.d069(null)
			.d070(null)
			.cardIssuanceInfo(cardIssuanceInfo)
			.build();
	}

	public D1400 build1400(Corp corp, JSONArray jsonArrayResCEOList, CardIssuanceInfo cardIssuanceInfo){
		List<String> listResCeoList = getJSONArrayCeo(jsonArrayResCEOList);
		String ceoType = getJSONArrayCeoType(jsonArrayResCEOList);
		return D1400.builder()
			.idxCorp(corp.idx())
			.c007(CommonUtil.getNowYYYYMMDD())
			.d001("2")
			.d002(replaceHyphen(corp.resCompanyIdentityNo()))
			.d004(replaceHyphen(corp.resCompanyNm()))
			.d005("06")
			.d011("")
			.d013("12")
			.d015("GOWID1")
			.d016("GOWID1")
			.d027(replaceHyphen(OptionalUtil.getOrEmptyString(corp.resUserIdentiyNo())))    // 법인등록번호
			.d028("400")    // 법인자격코드
			.d030(OptionalUtil.getOrEmptyString(corp.resRegisterDate()))    // 설립일자
			.d031(ceoType)    // 대표자코드
			.d032(listResCeoList.size() >= 2 ? getOnlyKorLan(listResCeoList.get(1)) : "")    // 대표자명1
			.d033(listResCeoList.size() >= 3 ? replaceHyphen(Seed128.encryptEcb(listResCeoList.get(2))) : "")    // 대표자주민등록번호1
			.d051(null)    // 팩스전화지역번호
			.d052(null)    // 팩스전화국번호
			.d053(null)    // 팩스전화고유번호
			.d054("대표이사")    // 신청관리자부서명
			.d055("대표이사")    // 신청관리자직위명
			.d056(listResCeoList.size() >= 3 ? replaceHyphen(Seed128.encryptEcb(listResCeoList.get(2))) : "")    // 신청관리자주민등록번호
			.d057(listResCeoList.size() >= 2 ? getOnlyKorLan(listResCeoList.get(1)) : "")    // 신청관리자명
			.cardIssuanceInfo(cardIssuanceInfo)
			.build();
	}

	public D1510 build1510(Corp corp){
		return D1510.builder()
			.idxCorp(corp.idx())
			.c007(CommonUtil.getNowYYYYMMDD())
			.d003(replaceHyphen(corp.resIssueNo())) // 발급번호
			.d004(corp.resCompanyNm()) // 법인명(상호)
			.d005(replaceHyphen(corp.resCompanyIdentityNo())) // 사업자등록번호
			.d006(corp.resBusinessmanType()) // 사업자종류
			.d007(corp.resUserNm()) // 성명(대표자)
			.d008(corp.resUserAddr()) // 사업장소재지(주소)
			.d009(replaceHyphen(corp.resUserIdentiyNo())) // 주민등록번호
			.d010(corp.resOpenDate()) // 개업일
			.d011(corp.resRegisterDate()) // 사업자등록일
			.d012(corp.resIssueOgzNm()) // 발급기관
			.d013(corp.resBusinessTypes()) // 업태
			.d014(corp.resBusinessItems()) // 종목
			.build();
	}

	public D1520 build1520(Corp corp, JSONObject jsonData2){
		JSONArray resBalanceSheet = (JSONArray) jsonData2.get("resBalanceSheet");
		JSONArray resIncomeStatement = (JSONArray) jsonData2.get("resIncomeStatement");

		AtomicReference<String> strCode001 = new AtomicReference<>();
		AtomicReference<String> strCode228 = new AtomicReference<>();
		AtomicReference<String> strCode334 = new AtomicReference<>();
		AtomicReference<String> strCode382 = new AtomicReference<>();

		resBalanceSheet.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			if(GowidUtils.getEmptyStringToString(obj, "_code").equals("228")){
				strCode228.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
			}
			if(GowidUtils.getEmptyStringToString(obj, "_code").equals("334")){
				strCode334.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
			}
			if(GowidUtils.getEmptyStringToString(obj, "_code").equals("382")){
				strCode382.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
			}
		});

		resIncomeStatement.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			if(GowidUtils.getEmptyStringToString(obj, "_code").equals("001")){
				strCode001.set(GowidUtils.getEmptyStringToString(obj, "_amt"));
			}
		});


		return D1520.builder()
			.idxCorp(corp.idx())
			.c007(CommonUtil.getNowYYYYMMDD())
			.d003(replaceHyphen(corp.resCompanyIdentityNo())) // 사업자등록번호
			.d004(replaceHyphen(GowidUtils.getEmptyStringToString(jsonData2, "resIssueNo"))) // 발급(승인)번호
			.d005(replaceHyphen(corp.resUserIdentiyNo())) // 주민번호
			.d006(corp.resCompanyNm()) // 상호(사업장명)
			.d007("Y") // 발급가능여부
			.d008(GowidUtils.getEmptyStringToString(jsonData2, "commStartDate")) // 시작일자
			.d009(GowidUtils.getEmptyStringToString(jsonData2, "commEndDate")) // 종료일자
			.d010(GowidUtils.getEmptyStringToString(jsonData2, "resUserNm")) // 성명
			.d011(GowidUtils.getEmptyStringToString(jsonData2, "resUserAddr")) // 주소
			.d012(GowidUtils.getEmptyStringToString(jsonData2, "resBusinessItems")) // 종목
			.d013(GowidUtils.getEmptyStringToString(jsonData2, "resBusinessTypes")) // 업태
			.d014(GowidUtils.getEmptyStringToString(jsonData2, "resReportingDate")) // 작성일자
			.d015(GowidUtils.getEmptyStringToString(jsonData2, "resAttrYear")) // 귀속연도
			.d016(strCode228.get()) // 총자산   대차대조표 상의 자본총계(없으면 등기부등본상의 자본금의 액)
			.d017(strCode001.get()) // 매출   손익계산서 상의 매출액
			.d018(NumberUtils.emptyStringToZero(strCode334.get())) // 납입자본금   대차대조표 상의 자본금
			.d019(NumberUtils.emptyStringToZero(strCode382.get())) // 자기자본금   대차대조표 상의 자본 총계
			.d020(GowidUtils.getEmptyStringToString(jsonData2, "commEndDate")) // 재무조사일   종료일자 (없으면 등기부등본상의 회사성립연월일)
			.build();
	}

	public D1530 build1530(Corp corp, JSONArray resRegisterEntriesList){

		JSONObject resRegisterEntry = (JSONObject) resRegisterEntriesList.get(0);

		JSONArray jsonArrayResCompanyNmList = (JSONArray) resRegisterEntry.get("resCompanyNmList");
		JSONArray jsonArrayResUserAddrList = (JSONArray) resRegisterEntry.get("resUserAddrList");
		JSONArray jsonArrayResOneStocAmtList = (JSONArray) resRegisterEntry.get("resOneStocAmtList");
		JSONArray jsonArrayResTCntStockIssueList = (JSONArray) resRegisterEntry.get("resTCntStockIssueList");
		JSONArray jsonArrayResStockList = (JSONArray) resRegisterEntry.get("resStockList");
		JSONArray jsonArrayResCorpEstablishDateList = (JSONArray) resRegisterEntry.get("resCorpEstablishDateList");
		JSONArray jsonArrayResCEOList = (JSONArray) resRegisterEntry.get("resCEOList");

		List<String> listResCompanyNmList = saveJSONArray1(jsonArrayResCompanyNmList);
		List<String> listResUserAddrList = saveJSONArray2(jsonArrayResUserAddrList);
		List<String> listResOneStocAmtList = saveJSONArray4(jsonArrayResOneStocAmtList);
		List<String> listResTCntStockIssueList = saveJSONArray5(jsonArrayResTCntStockIssueList);
		List<String> listResCeoList = getJSONArrayCeo(jsonArrayResCEOList);
		List<Object> listResStockList = saveJSONArray6(jsonArrayResStockList);
		String ResCorpEstablishDate = saveJSONArray20(jsonArrayResCorpEstablishDateList);

		JSONArray jsonArrayResStockItemList = (JSONArray) listResStockList.get(2);
		List<String> listD = new ArrayList<>(20);
		jsonArrayResStockItemList.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			listD.add(GowidUtils.getEmptyStringToString(obj, "resStockType"));
			listD.add(GowidUtils.getEmptyStringToString(obj, "resStockCnt"));
		});


		return D1530.builder()
			.idxCorp(corp.idx())
			.c007(CommonUtil.getNowYYYYMMDD())
			.d003("등기사항전부증명서")// 문서제목
			.d004(replaceHyphen(GowidUtils.getEmptyStringToString(resRegisterEntry, "resRegistrationNumber")))// 등기번호
			.d005(replaceHyphen(GowidUtils.getEmptyStringToString(resRegisterEntry, "resRegNumber")))// 등록번호
			.d006(GowidUtils.getEmptyStringToString(resRegisterEntry, "commCompetentRegistryOffice"))// 관할등기소
			.d007(GowidUtils.getEmptyStringToString(resRegisterEntry, "resPublishRegistryOffice"))// 발행등기소
			.d008(GowidUtils.getEmptyStringToString(resRegisterEntry, "resPublishDate"))// 발행일자
			.d009(ifNullReplaceObject(listResCompanyNmList, 0, ResCorpEstablishDate))// 상호
			.d010(ifNullReplaceObject(listResCompanyNmList, 1, ResCorpEstablishDate)) // 상호_변경일자//
			.d011(ifNullReplaceObject(listResCompanyNmList, 2, ResCorpEstablishDate)) // 상호_등기일자
			.d012(ifNullReplaceObject(listResUserAddrList, 0, ""))// 본점주소
			.d013(ifNullReplaceObject(listResUserAddrList, 1, ResCorpEstablishDate))// 본점주소_변경일자
			.d014(ifNullReplaceObject(listResUserAddrList, 2, ResCorpEstablishDate))// 본점주소_등기일자
			.d015(ifNullReplaceObject(listResOneStocAmtList, 0, ""))// 1주의금액
			.d016(ifNullReplaceObject(listResOneStocAmtList, 1, ResCorpEstablishDate))// 1주의금액_변경일자
			.d017(ifNullReplaceObject(listResOneStocAmtList, 2, ResCorpEstablishDate))// 1주의금액_등기일자
			.d018(ifNullReplaceObject(listResTCntStockIssueList, 0, ""))// 발행할주식의총수
			.d019(ifNullReplaceObject(listResTCntStockIssueList, 1, ResCorpEstablishDate))// 발행할주식의총수_변경일자
			.d020(ifNullReplaceObject(listResTCntStockIssueList, 2, ResCorpEstablishDate))// 발행할주식의총수_등기일자
			.d021(ifNullReplaceObject(listResStockList, 0, ""))// 발행주식현황_총수
			.d022(listD.size() >= 1 ? listD.get(0) : "")// 발행주식현황_종류1
			.d023(listD.size() >= 2 ? listD.get(1).substring(listD.get(1).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류1_수량
			.d024(listD.size() >= 3 ? listD.get(2) : "")// 발행주식현황_종류2
			.d025(listD.size() >= 4 ? listD.get(3).substring(listD.get(3).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류2_수량
			.d026(listD.size() >= 5 ? listD.get(4) : "")// 발행주식현황_종류3
			.d027(listD.size() >= 6 ? listD.get(5).substring(listD.get(5).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류3_수량
			.d028(listD.size() >= 7 ? listD.get(6) : "")// 발행주식현황_종류4
			.d029(listD.size() >= 8 ? listD.get(7).substring(listD.get(7).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류4_수량
			.d030(listD.size() >= 9 ? listD.get(8) : "")// 발행주식현황_종류5
			.d031(listD.size() >= 10 ? listD.get(9).substring(listD.get(9).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류5_수량
			.d032(listD.size() >= 11 ? listD.get(10) : "")// 발행주식현황_종류6
			.d033(listD.size() >= 12 ? listD.get(11).substring(listD.get(11).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류6_수량
			.d034(listD.size() >= 13 ? listD.get(12) : "")// 발행주식현황_종류7
			.d035(listD.size() >= 14 ? listD.get(13).substring(listD.get(13).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류7_수량
			.d036(listD.size() >= 15 ? listD.get(14) : "")// 발행주식현황_종류8
			.d037(listD.size() >= 16 ? listD.get(15).substring(listD.get(15).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류8_수량
			.d038(listD.size() >= 17 ? listD.get(16) : "")// 발행주식현황_종류9
			.d039(listD.size() >= 18 ? listD.get(17).substring(listD.get(17).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류9_수량
			.d040(listD.size() >= 19 ? listD.get(18) : "")// 발행주식현황_종류10
			.d041(listD.size() >= 20 ? listD.get(19).substring(listD.get(19).indexOf(" ") + 1).trim() : "")// 발행주식현황_종류10_수량
			.d042(ifNullReplaceObject(listResStockList, 1, ""))// 발행주식현황_자본금의액
			.d043(ifNullReplaceObject(listResStockList, 3, ResCorpEstablishDate))// 발행주식현황_변경일자
			.d044(ifNullReplaceObject(listResStockList, 4, ResCorpEstablishDate))// 발행주식현황_등기일자
			.d045(listResCeoList.size() >= 1 ? listResCeoList.get(0) : "")// 대표이사_직위1
			.d046(listResCeoList.size() >= 2 ? getOnlyKorLan(listResCeoList.get(1)) : "")// 대표이사_성명1
			.d047(listResCeoList.size() >= 3 ? Seed128.encryptEcb(listResCeoList.get(2).replaceAll("-", "")) : "")// 대표이사_주민번호1
			.d048(listResCeoList.size() >= 4 ? listResCeoList.get(3) : "")// 대표이사_주소1
			.d049(listResCeoList.size() >= 5 ? listResCeoList.get(4) : "")// 대표이사_직위2
			.d050(listResCeoList.size() >= 6 ? getOnlyKorLan(listResCeoList.get(5)) : "")// 대표이사_성명2
			.d051(listResCeoList.size() >= 7 ? Seed128.encryptEcb(listResCeoList.get(6).replaceAll("-", "")) : "")// 대표이사_주민번호2
			.d052(listResCeoList.size() >= 8 ? listResCeoList.get(7) : "")// 대표이사_주소2
			.d053(listResCeoList.size() >= 9 ? listResCeoList.get(8) : "")// 대표이사_직위3
			.d054(listResCeoList.size() >= 10 ? getOnlyKorLan(listResCeoList.get(9)) : "")// 대표이사_성명3
			.d055(listResCeoList.size() >= 11 ? Seed128.encryptEcb(listResCeoList.get(10).replaceAll("-", "")) : "")// 대표이사_주민번호3
			.d056(listResCeoList.size() >= 12 ? listResCeoList.get(11) : "")// 대표이사_주소3
			.d057(ResCorpEstablishDate)// 법인성립연월일
			.build();
	}

	public String getCeoType(JSONArray jsonArrayResCEOList){
		return getJSONArrayCeoType(jsonArrayResCEOList);
	}

	public Corp setCeoCount(Corp corp, JSONArray jsonArrayResCEOList) {
		List<String> listResCeoList = getJSONArrayCeo(jsonArrayResCEOList);

		if (listResCeoList.size()>=4) {
			corp.ceoCount(1);
		}
		if (listResCeoList.size()>=8) {
			corp.ceoCount(2);
		}
		if (listResCeoList.size()>=12) {
			corp.ceoCount(3);
		}

		return corp;
	}

	public void deleteAllShinhanFulltext(Long idxCorp) {
		repoD1000.deleteByCorpIdx(idxCorp);
		repoD1100.deleteByCorpIdx(idxCorp);
		repoD1200.deleteByCorpIdx(idxCorp);
		repoD1400.deleteByCorpIdx(idxCorp);
		repoD1510.deleteByCorpIdx(idxCorp);
		repoD1520.deleteByCorpIdx(idxCorp);
		repoD1530.deleteByCorpIdx(idxCorp);
	}

	public void deleteAllLotteFulltext(Long idxCorp) {
		repoD1000Lotte.deleteByCorpIdx(idxCorp);
		repoD1100Lotte.deleteByCorpIdx(idxCorp);
		repoD1200Lotte.deleteByCorpIdx(idxCorp);
	}

	public void saveAfterFinancialStatements(Corp corp, ConnectedMngDto.CorpInfo dto) {

		CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.findByCorpAndCardType(corp, dto.getCardType()).orElseThrow(
			() -> CorpNotRegisteredException.builder().build()
		);

		D1100 d1100 = build1100(corp, cardIssuanceInfo);
		D1400 d1400 = findFirstByCardIssuanceInfoIn1400(cardIssuanceInfo);
		D1000 d1000 = findFirstByCardIssuanceInfoIn1000(cardIssuanceInfo);
		if (CardType.GOWID.equals(dto.getCardType())) {
			setFinancialStatementsGowid(d1100);
		} else if (CardType.KISED.equals(dto.getCardType())) {
			setFinancialStatementsKised(d1100);
		}
		save1100(d1100);
		save1400(d1400, dto);
		save1000(d1000, dto);

		updateCardIssuanceInfo(d1000, d1100, d1400, cardIssuanceInfo);
	}

	private void setFinancialStatementsGowid(D1100 d1100) {
		d1100.setD005(CardType.GOWID.getNumber());
		d1100.setD014("1");
		d1100.setD016("고위드 스타트업 T&E");
		d1100.setD017("10");
		d1100.setD051(CardType.GOWID.getCode());
	}

	private void setFinancialStatementsKised(D1100 d1100) {
		d1100.setD005(CardType.KISED.getNumber());
		d1100.setD014("0");
		d1100.setD016("고위드창진원");
		d1100.setD017("40");
		d1100.setD051(CardType.KISED.getCode());
	}

	public void saveAfterCorpRegistration(JSONObject jsonDataCorpRegister, Corp corp, CardType cardType){
		JSONArray resRegisterEntriesList = (JSONArray) jsonDataCorpRegister.get("resRegisterEntriesList");
		JSONObject resRegisterEntry = (JSONObject) resRegisterEntriesList.get(0);
		JSONArray jsonArrayResCEOList = (JSONArray) resRegisterEntry.get("resCEOList");

		corp.resUserType(getCeoType(jsonArrayResCEOList));
		corp = setCeoCount(corp, jsonArrayResCEOList);
		CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.findByCorpAndCardType(corp, cardType).orElseThrow(
			() -> CorpNotRegisteredException.builder().build()
		);

		D1000 d1000 = build1000(corp, jsonArrayResCEOList, cardIssuanceInfo);
		D1400 d1400 = build1400(corp, jsonArrayResCEOList, cardIssuanceInfo);
		D1510 d1510 = build1510(corp);
		if (cardType.equals(CardType.GOWID)) {
			setCorpRegistrationGowid(d1000, d1400);
		} else if (cardType.equals(CardType.KISED)) {
			setCorpRegistrationKised(d1000, d1400);
		}
		save1000(d1000);
		save1400(d1400);
		save1510(d1510);

	}

	private void updateCardIssuanceInfo(D1000 d1000, D1100 d1100, D1400 d1400, CardIssuanceInfo cardIssuanceInfo){
		d1000.setCardIssuanceInfo(cardIssuanceInfo);
		d1100.setCardIssuanceInfo(cardIssuanceInfo);
		d1400.setCardIssuanceInfo(cardIssuanceInfo);
	}

	private void setCorpRegistrationGowid(D1000 d1000, D1400 d1400) {
		d1000.setD046("Y");
		d1000.setD049(CardType.GOWID.getNumber());
		d1000.setD053("고위드제휴카드신규입회");
		d1000.setD073(CardType.GOWID.getCode());
		d1400.setD003("01");
		d1400.setD008("261-81-25793");
		d1400.setD009("고위드");
		d1400.setD012(CardType.GOWID.getNumber());
		d1400.setD067(CardType.GOWID.getCode());
	}

	private void setCorpRegistrationKised(D1000 d1000, D1400 d1400) {
		d1000.setD046("N");
		d1000.setD049(CardType.KISED.getNumber());
		d1000.setD053("창진원카드신규입회");
		d1000.setD073(CardType.KISED.getCode());
		d1400.setD003("06");
		d1400.setD012(CardType.KISED.getNumber());
		d1400.setD067(CardType.KISED.getCode());
	}

	public void save1530(JSONObject jsonDataCorpRegister, Corp corp){
		JSONArray resRegisterEntriesList = (JSONArray) jsonDataCorpRegister.get("resRegisterEntriesList");
		D1530 d1530 = build1530(corp, resRegisterEntriesList);
		save1530(d1530);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateEmployeesType(CardIssuanceInfo cardIssuanceInfo, boolean overFiveEmployees){
		String consumerEmployeesType = ShinhanConsumerEmployeesType.from(overFiveEmployees).getCode();

		repoD1000.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).ifPresent(
			d1000 -> d1000.setD074(consumerEmployeesType)
		);
		repoD1400.findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(cardIssuanceInfo).ifPresent(
			d1400 -> d1400.setD068(consumerEmployeesType)
		);
	}

}
