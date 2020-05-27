package com.nomadconnection.dapp.codef.io.helper;

/**
 *	상수 클래스
 */
public class CommonConstant {

	public static final String API_DOMAIN 	= "https://api.codef.io";										// API서버 도메인
	public static final String TEST_DOMAIN 	= "https://development.codef.io";								// API서버 데모 도메인

	public static final String TOKEN_DOMAIN = "https://oauth.codef.io";										// OAUTH2.0 테스트 도메인
	public static final String GET_TOKEN 	= "/oauth/token";												// OAUTH2.0 토큰 발급 요청 URL

	//https://developer.codef.io/products/bank/overview
	public static final String[] LISTBANK	= {"0002","0003","0004","0007","0011","0020","0023","0027","0031","0032","0034","0035","0037","0039","0045","0048","0071","0081","0088","0089"};

	public static final String CONNECTED_ID = "connectedId";												// 유저 식별 연결 아이디
	public static final String PAGE_NO 		= "pageNo";														// 페이지 번호

	public static final String KR_BK_1_B_001	= "/v1/kr/bank/b/account/account-list";                     // 은행 기업 보유계좌
	public static final String KR_BK_1_B_002	= "/v1/kr/bank/b/account/transaction-list";                 // 은행 기업 수시입출 거래내역
	public static final String KR_BK_1_B_003	= "/v1/kr/bank/b/installment-savings/transaction-list";     // 은행 기업 적금 거래내역
	public static final String KR_BK_1_B_004	= "/v1/kr/bank/b/loan/transaction-list";                    // 은행 기업 대출 거래내역
	public static final String KR_BK_1_B_005	= "/v1/kr/bank/b/exchange/transaction-list";                // 은행 기업 외화 거래내역
	public static final String KR_BK_1_B_006	= "/v1/kr/bank/b/fund/transaction-list";                    // 은행 기업 펀드 거래내역
	public static final String KR_BK_1_B_007	= "/v1/kr/bank/b/fast-account/transaction-list";            // 은행 기업 빠른계좌조회

	public static final String KR_BK_1_P_001	= "/v1/kr/bank/p/account/account-list";                     // 은행 개인 보유계좌
	public static final String KR_BK_1_P_002	= "/v1/kr/bank/p/account/transaction-list";                 // 은행 개인 수시입출 거래내역
	public static final String KR_BK_1_P_003	= "/v1/kr/bank/p/installment-savings/transaction-list";     // 은행 개인 적금 거래내역
	public static final String KR_BK_1_P_004	= "/v1/kr/bank/p/loan/transaction-list";                    // 은행 개인 대출 거래내역
	public static final String KR_BK_1_P_005	= "/v1/kr/bank/p/fast-account/transaction-list";            // 은행 개인 빠른계좌조회

	public static final String KR_BK_2_P_001	= "/v1/kr/bank2/p/account/account-list";                    // 저축은행 개인 보유계좌 조회
	public static final String KR_BK_2_P_002	= "/v1/kr/bank2/p/account/transaction-list";                // 저축은행 개인 수시입출 거래내역

	public static final String KR_CD_B_001	= "/v1/kr/card/b/account/card-list";                            // 카드 법인 보유카드
	public static final String KR_CD_B_002	= "/v1/kr/card/b/account/approval-list";                        // 카드 법인 승인내역
	public static final String KR_CD_B_003	= "/v1/kr/card/b/account/billing-list";                         // 카드 법인 청구내역
	public static final String KR_CD_B_004	= "/v1/kr/card/b/account/limit";                                // 카드 법인 한도조회

	public static final String KR_CD_P_001	= "/v1/kr/card/p/account/card-list";                            // 카드 개인 보유카드
	public static final String KR_CD_P_002	= "/v1/kr/card/p/account/approval-list";                        // 카드 개인 승인내역
	public static final String KR_CD_P_003	= "/v1/kr/card/p/account/billing-list";                         // 카드 개인 청구내역
	public static final String KR_CD_P_004	= "/v1/kr/card/p/account/limit";                                // 카드 개인 한도조회

	public static final String KR_PB_NT_001	= "/v1/kr/public/nt/business/status";                            // 공공 사업자상태
	public static final String KR_PB_CK_001	= "/v1/kr/public/ck/real-estate-register/status";                // 공공 부동산등기
	public static final String KR_PB_EF_001	= "/v1/kr/public/ef/driver-license/status";                      // 공공 운전면허 진위여부
	public static final String KR_PB_MW_001	= "/v1/kr/public/mw/identity-card/status";                       // 공공 주민등록 진위여부

	public static final String KR_IS_0001_001	= "/v1/kr/insurance/0001/credit4u/contract-info";     	  	// 보험다보여-계약정보조회
	public static final String KR_IS_0001_002	= "/v1/kr/insurance/0001/credit4u/register";     	  		// 보험다보여-회원가입신청
	public static final String KR_IS_0001_003	= "/v1/kr/insurance/0001/credit4u/find-id";     	  		// 보험다보여-아이디찾기
	public static final String KR_IS_0001_004	= "/v1/kr/insurance/0001/credit4u/change-pwd";     	  		// 보험다보여-비밀번호변경
	public static final String KR_IS_0001_005	= "/v1/kr/insurance/0001/credit4u/unregister";     	  		// 보험다보여-회원탈퇴신청


	public static final String TAX_INVOICE			= "/v1/kr/public/nt/tax-invoice/a-check-list"; 						// 국세청 - 전자세금계산서 목록
	public static final String CASH_PURCHASE		= "/v1/kr/public/nt/cash-receipt/a-purchase-details"; 				// 국세청 - 현금영수증 매입내역
	public static final String CASH_SALES			= "/v1/kr/public/nt/cash-receipt/a-sales-details"; 					// 국세청 - 전자세금계산서 목록
	public static final String PROOF_ISSUE			= "/v1/kr/public/nt/proof-issue/a-corporate-registration"; 			// 국세청 - 증명발급 사업자등록
	public static final String REPORT_WITHHOLDING	= "/v1/kr/public/nt/report/a-withholding-tax"; 						// 국세청 - 신고서 원천징수 이행상황 신고서
	public static final String STANDARD_FINANCIAL	= "/v1/kr/public/nt/proof-issue/standard-financial-statements"; 	// 국세청 - 증명발급 표준재무재표
	public static final String CORP_REGISTER		= "/v1/kr/public/ck/corp-register/issue"; 							// 대법원 - 법인등기부등본

	public static final String GET_CONNECTED_IDS = "/v1/account/connectedId-list";       					// 커넥티드아이디 목록 조회
	public static final String GET_ACCOUNTS = "/v1/account/list";            								// 계정 목록 조회
	public static final String CREATE_ACCOUNT = "/v1/account/create";            							// 계정 등록(커넥티드아이디 발급)
	public static final String ADD_ACCOUNT = "/v1/account/add";            									// 계정 추가
	public static final String UPDATE_ACCOUNT = "/v1/account/update";            							// 계정 수정
	public static final String DELETE_ACCOUNT = "/v1/account/delete";            							// 계정 삭제

	public static final String COUNTRYCODE = "KR"; // 국가코드 KR
	public static final String BUSINESSTYPE = "BK"; // 업무구분코드 BK
	public static final String CLIENTTYPE = "B"; // 고객구분(P: 개인, B: 기업)
	public static final String CERTTYPE = "pfx";


	/**
	 * API 요청 도메인 반환
	 * @return
	 */
	public static String getRequestDomain() {
		// return CommonConstant.API_DOMAIN;
		return CommonConstant.SANDBOX_DOMAIN;
	}


	/**	CODEF로부터 발급받은 클라이언트 아이디	*/
	//public static final String CLIENT_ID 	= "3b9fdab9-f3e6-400e-967e-9b3cf9e53583";
	public static final String CLIENT_ID 	= "ef27cfaa-10c1-4470-adac-60ba476273f9"; // CODEF 샌드박스 클라이언트 아이디

	/**	CODEF로부터 발급받은 시크릿 키	*/
	//public static final String SECERET_KEY 	= "48c4a43a-f6a4-4e8a-892e-9ee1393f9907";
	public static final String SECERET_KEY 	= "83160c33-9045-4915-86d8-809473cdf5c3"; // CODEF 샌드박스 클라이언트 시크릿

	/**	CODEF로부터 발급받은 퍼블릭 키	*/
	public static final String PUBLIC_KEY 	= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAouzCsPKacIrhRGu/5rq5LDE0SbZaEVSDcPKgqmk277N2esdhJVD19hpIy8HgWYgXY/j3DQ9I6By0uflPuI4CSF33jq/o36G/oIccB3coPhMDGYr3mEGauqt2ZSB8Aa4QUAISOeEIyp8ow3M7TtOFGvGoykvOiVK071nQmgcqTcWkZM/Bz5gW0THRozViuTe+gUG6FTQbJ/XGzTMS/UGE6yi7DixDonugLC3kBKNy4N3/rWhvptJ7F6l3SOF2slhxq0gFIjif4Q9kHxAuQTp2wkI9fBIJo07e/R2i1+jvXIQQesNF+QefE+QCqJYkM2O+kB1XHGBBxVpcBggZJGXwnwIDAQAB";

	/**	OAUTH2.0 토큰 샘플	*/
	public static String ACCESS_TOKEN = "";


	/**	샌드박스 테스트용 상수	*/
	public static final String SANDBOX_DOMAIN = "https://sandbox.codef.io";									// API서버 샌드박스 도메인
	public static final String SANDBOX_CLIENT_ID 	= "ef27cfaa-10c1-4470-adac-60ba476273f9";				// CODEF 샌드박스 클라이언트 아이디
	public static final String SANDBOX_SECERET_KEY 	= "83160c33-9045-4915-86d8-809473cdf5c3";				// CODEF 샌드박스 클라이언트 시크릿

}
