package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * @interfaceID : 1000
 * @description : 법인신규심사
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1000 extends CommonPart {

    @JsonAlias("D001")
    private String businessLicenseNo;    // 사업자등록번호

    @JsonAlias("D002")
    private String companyRegisterNo;  // 법인등록번호

    @JsonAlias("D003")
    private String companyName; // 법인명

    @JsonAlias("D004")
    private String specCode; // 법인자격코드

    @JsonAlias("D005")
    private String scaleCode;    // 법인규모코드

    @JsonAlias("D006")
    private String companyEngName;  // 법인영문명

    @JsonAlias("D007")
    private String openDate; // 설립일자

    @JsonAlias("D008")
    private String industryCode;    //업종코드

    @JsonAlias("D009")
    private String ceoCode; // 대표자코드

    @JsonAlias("D010")
    private String ceoName1; // 대표자명1

    @JsonAlias("D011")
    private String ceoRegisterNo1; // 대표자주민등록번호1

    @JsonAlias("D012")
    private String ceoEngName1; // 대표자영문명1

    @JsonAlias("D013")
    private String ceoNtnCode1;  // 대표자국적코드1

    @JsonAlias("D014")
    private String ceoName2; // 대표자명2

    @JsonAlias("D015")
    private String ceoRegisterNo2; // 대표자주민등록번호2

    @JsonAlias("D016")
    private String ceoEngName2; // 대표자영문명2

    @JsonAlias("D017")
    private String ceoNtnCode2;  // 대표자국적코드2

    @JsonAlias("D018")
    private String ceoName3; // 대표자명3

    @JsonAlias("D019")
    private String ceoRegisterNo3; // 대표자주민등록번호3

    @JsonAlias("D020")
    private String ceoEngName3; // 대표자영문명3

    @JsonAlias("D021")
    private String ceoNtnCode3;  // 대표자국적코드3

    @JsonAlias("D022")
    private String officePostFrontNo; // 직장우편앞번호

    @JsonAlias("D023")
    private String officePostBackNo; // 직장우편뒷번호

    @JsonAlias("D024")
    private String address; // 직장기본주소

    @JsonAlias("D025")
    private String detailAddress; // 직장상세주소

    @JsonAlias("D026")
    private String localTelNo; // 직장전화지역번호

    @JsonAlias("D027")
    private String stationTelNo; // 직장전화국번호

    @JsonAlias("D028")
    private String uniqueTelNo;    // 직장전화고유번호

    @JsonAlias("D029")
    private String localFaxNo;  // 팩스전화지역번호

    @JsonAlias("D030")
    private String stationFaxNo;    // 팩스전화국번호

    @JsonAlias("D031")
    private String uniqueFaxNo; // 팩스전화고유번호

    @JsonAlias("D032")
    private String managerDepartment;   // 신청관리자부서명

    @JsonAlias("D033")
    private String managerPosition; // 신청관리자직위명

    @JsonAlias("D034")
    private String managerRegisterNo;   // 신청관리자주민등록번호

    @JsonAlias("D035")
    private String managerName; // 신청관리자명

    @JsonAlias("D036")
    private String managerLocalTelNo;   // 신청관리자전화지역번호

    @JsonAlias("D037")
    private String managerStationTelNo; // 신청관리자전화국번호

    @JsonAlias("D038")
    private String managerUniqueTelNo;  // 신청관리자전화고유번호

    @JsonAlias("D039")
    private String managerExtTelNo; // 신청관리자전화내선번호

    @JsonAlias("D040")
    private String managerIdentifyMobileNo; // 신청관리자휴대전화식별번호

    @JsonAlias("D041")
    private String managerStationMobileNo;  // 신청관리자휴대전화국번호

    @JsonAlias("D042")
    private String managerUniqueMobileNo;   // 신청관리자휴대전화고유번호

    @JsonAlias("D043")
    private String managerEmail;    // 신청관리자이메일주소

    @JsonAlias("D044")
    private String registerBranchCode;  // 등록지점코드

    @JsonAlias("D045")
    private String validCode;   // 유효기간코드

    @JsonAlias("D046")
    private String guarantorExists; // 보증인등록여부

    @JsonAlias("D047")
    private String mortgageExists;  // 담보등록여부

    @JsonAlias("D048")
    private String applyPathCode;   // 신청경로코드

    @JsonAlias("D049")
    private String cardProductCode; // 카드상품번호

    @JsonAlias("D050")
    private Long approvalLimitAmount; // 제휴약정한도코드

    @JsonAlias("D051")
    private String applyCode;   // 신청구분코드

    @JsonAlias("D052")
    private String exceptionExists; // 예외접수여부

    @JsonAlias("D053")
    private String opinion; // 의견내용

    @JsonAlias("D054")
    private String addressCode; // 주소종류코드

    @JsonAlias("D055")
    private String streetAddressKey;    // 도로명참조KEY값

    @JsonAlias("D056")
    private String isCheckCeoInfo1; // 대표자정보체크여부1

    @JsonAlias("D057")
    private String isCheckCeoInfo2; // 대표자정보체크여부2

    @JsonAlias("D058")
    private String companyTypeCode; // 법인기업형태코드

    @JsonAlias("D059")
    private String ownerName;    // 법인실소유자한글명

    @JsonAlias("D060")
    private String ownerEngName; // 법인실소유자영문명

    @JsonAlias("D061")
    private String ownerBirthDate; // 법인실소유자생년월일

    @JsonAlias("D062")
    private String ownerNtnCode; // 법인실소유자국적코드

    @JsonAlias("D063")
    private String ownerExceptCode; // 법인실소유자제외사유코드

    @JsonAlias("D064")
    private String ownerTypeCode;    // 법인실소유자유형코드

    @JsonAlias("D065")
    private String amlOwnerStake;   // 자금세탁방지실소유자지분율

    @JsonAlias("D066")
    private String isForeigner; // 외국인여부

    @JsonAlias("D067")
    private String agentName;    // 법인신청대리인명

    @JsonAlias("D068")
    private String agentRegisterNo; // 법인신청대리인주민등록번호

    @JsonAlias("D069")
    private String agentDepartment; // 법인신청대리인부서명

    @JsonAlias("D070")
    private String agentPosition;   // 법인신청대리인직위명

}
