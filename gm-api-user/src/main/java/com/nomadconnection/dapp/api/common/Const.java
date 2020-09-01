package com.nomadconnection.dapp.api.common;

public class Const {
    public static final String API_GW_RESULT_SUCCESS = "200";
    public static final String API_SHINHAN_RESULT_SUCCESS = "00";
    public static final String API_LOTTE_RESULT_SUCCESS = "0000";
    public static final String API_LOTTE_D1000_RESULT_SUCCESS2 = "0002";
    public static final String API_LOTTE_RECEIVE_SUCCESS = "Y";
    public static final String API_GW_OKNAME_SUCCESS = "B000";
    public static final String CARD_RECEIVE_ADDRESS_CODE = "1";
    public static final String API_GW_IMAGE_NOT_EXIST_ERROR_CODE = "90004";
    public static final String D1200_MEMBER_TYPE_CODE = "01";
    public static final String SHINHAN_STOCKHOLDER_GW_FILE_CODE = "9999";
    public static final String LOTTE_STOCKHOLDER_GW_FILE_CODE = "8504";
    public static final String ELEC_SIGNATURE_CERTI_PROD_CODE = "07";      // 1800 > 전자서명인증제품코드
    public static final String REPORTING_SERVER = "/m2/ReportingServer/report/";
    public static final String REPORTING_FILE_CODE = "9991";
    public static final String SHINHAN_CORP_OWNER_CODE_1 = "1";
    public static final String SHINHAN_CORP_OWNER_CODE_2 = "2";
    public static final String SHINHAN_CORP_OWNER_CODE_5 = "5";
    public static final String ID_VERIFICATION_NO = "GOWID1";
    public static final String SHINHAN_REGISTER_BRANCH_CODE = "0113";
    public static final String LOTTE_CORP_OWNER_CODE_1 = "11";
    public static final String LOTTE_CORP_OWNER_CODE_2 = "21";
    public static final String LOTTE_CORP_OWNER_CODE_5 = "31";
    public static final String LOTTE_CORP_rlOwrVdMdc_CODE_09 = "09";
    public static final String LOTTE_CORP_rlOwrVdMdc_CODE_01 = "01";
    public static final String LOTTE_CORP_rlOwrDc_CODE_4 = "4";
    public static final String LOTTE_CORP_rlOwrDc_CODE_1 = "1";

    // 1000, 1400 일때 03 + "1차 서류 심사중 일 때만  발송처리  처리 가능합니다." 일 경우 성공처리.
    public static final String API_SHINHAN_RESULT_1000_1400_SUCCESS_CODE = "03";
    public static final String API_SHINHAN_RESULT_1000_1400_SUCCESS_MSG = "1차 서류 심사중 일 때만  발송처리  처리 가능합니다.";
    // TODO : 9/3(목) 저녁때 API_SHINHAN_RESULT_1000_1400_SUCCESS_CODE=47 일때만 성공으로 변경작업 필요, 위 두줄은 제거.
//    public static final String API_SHINHAN_RESULT_1000_1400_SUCCESS_CODE = "09";

}
