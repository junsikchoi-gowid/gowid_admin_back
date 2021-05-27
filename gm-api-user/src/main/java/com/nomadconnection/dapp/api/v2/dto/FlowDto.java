package com.nomadconnection.dapp.api.v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.core.domain.flow.FlowComment;
import com.nomadconnection.dapp.core.domain.flow.FlowReportMonth;
import com.nomadconnection.dapp.core.domain.flow.FlowTagConfig;
import com.nomadconnection.dapp.core.domain.repository.flow.FlowTagMonthRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResAccountCustomRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccountHistory;
import com.nomadconnection.dapp.core.domain.res.ResAccountStatus;
import com.nomadconnection.dapp.core.domain.res.ResAccountTypeStatus;
import com.nomadconnection.dapp.core.dto.flow.FlowReportExcelDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

public class FlowDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowReportByPeriodDto {

        @ApiModelProperty(value = "날짜", example = "202101")
        public String flowDate;

        @ApiModelProperty(value = "입금", example = "2000000000")
        public Double flowIn;

        @ApiModelProperty(value = "출금", example = "1000000000")
        public Double flowOut;

        @ApiModelProperty(value = "총액", example = "3000000000")
        public Double flowTotal;

        public static FlowReportByPeriodDto from(FlowReportMonth dto) {
            return FlowReportByPeriodDto.builder()
                    .flowDate(dto.flowDate())
                    .flowIn(dto.flowIn())
                    .flowOut(dto.flowOut())
                    .flowTotal(dto.flowTotal())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFlowAccount {
        @ApiModelProperty(value = "즐겨찾기여부", example = "true")
        public Boolean favorite;

        @ApiModelProperty(value = "은행코드", example = "0002")
        public List<String> organization;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude
    public static class FlowAccountDto{
        @ApiModelProperty(value = "즐겨찾기여부", example = "true")
        public Boolean favorite;

        @ApiModelProperty(value = "별칭", example = "마이너스 통장")
        public String nickName;

        @ApiModelProperty(value = "idxAccount", example = "1")
        public Long idxAccount;

        @ApiModelProperty(value = "통화", example = "KRW")
        public String currency;

        @ApiModelProperty(value = "계좌종류", example = "예적금")
        public String type;

        @ApiModelProperty(value = "은행종류", example = "0002")
        public String organization;

        @ApiModelProperty(value = "계좌번호", example = "123333-42-123122")
        public String resAccount;

        @ApiModelProperty(value = "현재잔액", example = "123333-42-123122")
        public Double resAccountBalance;

        @ApiModelProperty(value = "마이너스 통장 여부", example = "1")
        public String resOverdraftAcctYN;

        @ApiModelProperty(value = "계좌번호", example = "123333-42-123122")
        public String resAccountDisplay;

        @ApiModelProperty(value = "상태", example = "0002")
        public ResAccountStatus status;

        @ApiModelProperty(value = "상태 정보", example = "0002")
        public String statusDesc;

        public static FlowAccountDto from(ResAccountCustomRepository.CorpAccountDto dto) {
            String nickName;
            if( !ObjectUtils.isEmpty(dto.getNickName())){
                nickName = dto.getNickName();
            }else if(!ObjectUtils.isEmpty(dto.getResAccountNickName())){
                nickName = dto.getResAccountNickName();
            }else {
                nickName = dto.getResAccountName();
            }

            if(dto.getStatus().equals(ResAccountStatus.STOP)){
                dto.setStatusDesc("연동이 중단된 계좌입니다.");
            }else if(dto.getStatus().equals(ResAccountStatus.ERROR)){
                dto.setStatusDesc("업데이트에 실패헀습니다.");
            }
            if(dto.getType().equals(ResAccountTypeStatus.DepositTrust.name())) {
                dto.setType(ResAccountTypeStatus.DepositTrust.getStatus());
            }else if(dto.getType().equals(ResAccountTypeStatus.ForeignCurrency.name())) {
                dto.setType(ResAccountTypeStatus.ForeignCurrency.getStatus());
            }else if(dto.getType().equals(ResAccountTypeStatus.Fund.name())) {
                dto.setType(ResAccountTypeStatus.Fund.getStatus());
            }else if(dto.getType().equals(ResAccountTypeStatus.Loan.name())) {
                dto.setType(ResAccountTypeStatus.Loan.getStatus());
            }else if(dto.getType().equals(ResAccountTypeStatus.Stock.name())) {
                dto.setType(ResAccountTypeStatus.Stock.getStatus());
            }

                return FlowAccountDto.builder()
                    .idxAccount(dto.getIdxResAccount())
                    .favorite(dto.getFavorite())
                    .nickName(nickName)
                    .type(dto.getType())
                    .currency(dto.getCurrency())
                    .organization(dto.getOrganization())
                    .resAccount(dto.getResAccount())
                    .resAccountDisplay(dto.getResAccountDisplay())
                    .status(dto.getStatus())
                    .statusDesc(dto.getStatusDesc())
                    .resAccountBalance(dto.getResAccountBalance())
                    .resOverdraftAcctYN(dto.getResOverdraftAcctYN())
                .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFlowAccountHistory {
        @ApiModelProperty( value = "계좌번호", example = "13927442123123,139274123123,139272123123", required = true)
        public List<String> arrayResAccount;

        @ApiModelProperty(value = "입출금 in out all", example = "all", required = true)
        public String inOutType;

        @ApiModelProperty(value = "검색어")
        public String searchWord;

        @ApiModelProperty(value = "시작일", example = "20200101", required = true)
        public String from;

        @ApiModelProperty(value = "종료일", example = "20210401", required = true)
        public String to;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowAccountHistoryDto {
        @ApiModelProperty(value = "idx", example = "1")
        public Long idx;

        @ApiModelProperty(value = "계좌번호", example = "123333-42-123122")
        public String resAccount;

        @ApiModelProperty(value = "거래일자 거래시각")
        public String resAccountTrDateTime;

        @ApiModelProperty(value = "입금", example = "2000000")
        public String resAccountOut;

        @ApiModelProperty(value = "출금", example = "2000000")
        public String resAccountIn;

        @ApiModelProperty(value = "잔액", example = "112000000")
        public String resAfterTranBalance;

        @ApiModelProperty(value = "적요1", example = "-")
        public String resAccountDesc1;

        @ApiModelProperty(value = "적요2", example = "-")
        public String resAccountDesc2;

        @ApiModelProperty(value = "적요3", example = "-")
        public String resAccountDesc3;

        @ApiModelProperty(value = "적요4", example = "-")
        public String resAccountDesc4;

        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        @ApiModelProperty(value = "idx 계정과목코드 ", example = "-")
        public FlowTagConfigDto flowTagConfigdto;

        @ApiModelProperty(value = "계정과목코드", example = "-")
        public String tagValue;

        @ApiModelProperty(value = "메모")
        public String memo;

        public static FlowAccountHistoryDto from (ResAccountHistory dto){

            FlowTagConfigDto obj = null;
            if( !ObjectUtils.isEmpty(dto.flowTagConfig())){
                obj = FlowTagConfigDto.from(dto.flowTagConfig());
            }

            return FlowAccountHistoryDto.builder()
                    .idx(dto.idx())
                    .resAccount(dto.resAccount())
                    .resAccountTrDateTime(dto.resAccountTrDate().concat(GowidUtils.getEmptyStringToString(dto.resAccountTrTime())))
                    .resAccountOut(dto.resAccountOut())
                    .resAccountIn(dto.resAccountIn())
                    .resAfterTranBalance(dto.resAfterTranBalance())
                    .resAccountDesc1(dto.resAccountDesc1())
                    .resAccountDesc2(dto.resAccountDesc2())
                    .resAccountDesc3(dto.resAccountDesc3())
                    .resAccountDesc4(dto.resAccountDesc4())
                    .flowTagConfigdto(obj)
                    .tagValue(dto.tagValue())
                    .memo(dto.memo())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowAccountHistoryUpdateDto {
        @ApiModelProperty(value = "idx", example = "1")
        public Long idx;

        @ApiModelProperty(value = "idx 계정과목코드 ", example = "1")
        public Long idxFlowTagConfig;

        @ApiModelProperty(value = "계정과목코드", example = "-")
        public String tagValue;

        @ApiModelProperty(value = "메모")
        public String memo;

        public static FlowAccountHistoryUpdateDto from (ResAccountHistory dto){
            return FlowAccountHistoryUpdateDto.builder()
                    .idx(dto.idx())
                    .idxFlowTagConfig(dto.flowTagConfig().idx())
                    .tagValue(dto.tagValue())
                    .memo(dto.memo())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowTagConfigDto{
        @ApiModelProperty(value = "식별자", example = "1")
        public Long idx;

        @ApiModelProperty(value = "flowCode CommonCode 설정", example = "FLOW")
        public String flowCode;

        @ApiModelProperty(value = "계정분류 1단계 (입출금)", example = "A")
        public String code1;

        @ApiModelProperty(value = "계정분류 2단계 (현금흐름 성격)", example = "AA")
        public String code2;

        @ApiModelProperty(value = "계정분류 3단계 (계정성격)", example = "AAA")
        public String code3;

        @ApiModelProperty(value = "계정분류 4단계 (계정)", example = "AAA-A01")
        public String code4;

        @ApiModelProperty(value = "계정분류 1단계 (입출금)", example = "입금")
        public String codeLv1;

        @ApiModelProperty(value = "계정분류 2단계 (현금흐름 성격)", example = "인건비")
        public String codeLv2;

        @ApiModelProperty(value = "계정분류 3단계 (계정성격)", example = "퇴직금")
        public String codeLv3;

        @ApiModelProperty(value = "계정분류 4단계 (계정)", example = "퇴직금")
        public String codeLv4;

        @ApiModelProperty(value = "계정분류 codeDesc (검색어)", example = "퇴직,퇴직금,퇴사")
        public String codeDesc;

        @ApiModelProperty(value = "View 유무", example = "true")
        public Boolean viewEnabled;

        @ApiModelProperty(value = "사용유무", example = "true")
        public Boolean enabled;

        @ApiModelProperty(value = "tag 순서", example = "1")
        public Integer tagOrder;

        public static FlowTagConfigDto from(FlowTagConfig dto){
            return FlowTagConfigDto.builder()
                    .idx(dto.idx())
                    .flowCode(dto.flowCode())
                    .code1(dto.code1())
                    .code2(dto.code2())
                    .code3(dto.code3())
                    .code4(dto.code4())
                    .codeLv1(dto.codeLv1())
                    .codeLv2(dto.codeLv2())
                    .codeLv3(dto.codeLv3())
                    .codeLv4(dto.codeLv4())
                    .codeDesc(dto.codeDesc())
                    .viewEnabled(dto.viewEnabled())
                    .enabled(dto.enabled())
                    .tagOrder(dto.tagOrder())
                    .build();
        }

        public static FlowTagConfigDto of(FlowTagConfigDto dto) {
            return FlowTagConfigDto.builder()
                    .flowCode(dto.getFlowCode())
                    .code1(dto.getCode1())
                    .code2(dto.getCode2())
                    .code3(dto.getCode3())
                    .code4(dto.getCode4())
                    .codeLv1(dto.getCodeLv1())
                    .codeLv2(dto.getCodeLv2())
                    .codeLv3(dto.getCodeLv3())
                    .codeLv4(dto.getCodeLv4())
                    .codeDesc(dto.getCodeDesc())
                    .enabled(dto.getEnabled())
                    .tagOrder(dto.getTagOrder())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowCashTag{
        @ApiModelProperty(value = "flow Tag code", example = "F0001")
        public String flowCode;

        @ApiModelProperty(value = "계정분류 1단계 (입출금)", example = "입금")
        public String codeLv1;

        @ApiModelProperty(value = "계정분류 2단계 (현금흐름 성격)", example = "인건비")
        public String codeLv2;

        @ApiModelProperty(value = "계정분류 3단계 (계정성격)", example = "월급여 등")
        public String codeLv3;

        @ApiModelProperty(value = "계정분류 4단계 (계정)", example = "월급, 급여, 월급여")
        public String codeLv4;

        @ApiModelProperty(value = "계정별 합계", example = "9280000000")
        public Double flowCash;

        @ApiModelProperty(value = "순서", example = "1")
        public String flowOrder;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowCashInfo{
        @ApiModelProperty(value = "시작 최종 잔액", example = "F0001")
        List<FlowCashFluctuationDto> flowCashFluctuationList;

        @ApiModelProperty(value = "현금흐름 상세내역(계정, 상세)", example = "F0001")
        List<FlowTagMonthDto> flowTagList;
    }



    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowCashFluctuationDto{

        @ApiModelProperty(value = "날짜 (년월)", example = "202101")
        public String flowDate;

        @ApiModelProperty(value = "총액", example = "3000000000")
        public Double flowTotal;

        public static FlowCashFluctuationDto from (FlowReportMonth dto) {
            return FlowCashFluctuationDto.builder()
                    .flowDate(dto.flowDate())
                    .flowTotal(dto.flowTotal())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowConnectedId{
        @ApiModelProperty(value = "날짜 (년월)", example = "202101")
        public String flowDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowTagMonthDto {

        @ApiModelProperty(value = "날짜 (년월)", example = "202101")
        public String flowDate;

        @ApiModelProperty(value = "총액", example = "3000000000")
        public Double flowTotal;

        // idxFlowTagConfig // 인덱스
        public String flowCode;
        public String codeLv1;
        public String codeLv2;
        public String codeLv3;
        public String codeLv4;
        public String codeDesc;
        public boolean enabled;
        public Integer tagOrder;

        public static FlowTagMonthDto from(FlowReportExcelDto dto) {
            return FlowTagMonthDto.builder()
                    .codeLv1(dto.getCodeLv1())
                    .codeLv2(dto.getCodeLv2())
                    .codeLv3(dto.getCodeLv3())
                    .codeLv4(dto.getCodeLv4())
                    .flowDate(dto.getFlowDate())
                    .flowTotal(dto.getFlowTotal())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowCashInfoExcel{
        @ApiModelProperty(value = "시작 최종 잔액", example = "F0001")
        List<FlowCashFluctuationDto> flowCashFluctuationList;

        @ApiModelProperty(value = "현금흐름 상세내역(계정, 상세)", example = "F0001")
        List<FlowTagMonthExcelDto> flowTagMonthExcelDtoList;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowTagMonthExcelDto {
        public String codeLv1;
        public String codeLv3;
        public String codeLv4;
        public Double before3;
        public Double before2;
        public Double before1;
        public Double before0;
        public Double beforesum;

        public static FlowTagMonthExcelDto excel(FlowTagMonthRepository.FlowReportExcelGroupDto dto) {
            return FlowTagMonthExcelDto.builder()
                    .codeLv1(dto.getCodeLv1())
                    .codeLv3(dto.getCodeLv3())
                    .codeLv4(dto.getCodeLv4())
                    .before3(dto.getBefore3())
                    .before2(dto.getBefore2())
                    .before1(dto.getBefore1())
                    .before0(dto.getBefore0())
                    .beforesum(dto.getBeforesum())
                    .build();
        }
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountFavoriteDto {

        @ApiModelProperty(value = "즐겨찾기여부", example = "true")
        public Boolean favorite;

        @ApiModelProperty(value = "사용유무", example = "true")
        public Boolean enabled;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowCommentDto {
        @ApiModelProperty(value = "식별자", example = "1")
        public Long idx;

        @ApiModelProperty(value = "Corp Info", example = "1")
        public Long idxCorp;

        @ApiModelProperty(value = "user info", example = "1")
        public Long idxUser;

        @ApiModelProperty(value = "user info", example = "회원")
        public String userName;

        @ApiModelProperty(value = "comment", example = "매출채권 속도를 검토할 필요가있습니다.")
        public String comment;

        @ApiModelProperty(value = "orgFileName", example = "orgFileName")
        public String orgFileName;

        @ApiModelProperty(value = "fileName", example = "fileName")
        public String fileName;

        @ApiModelProperty(value = "fileSize", example = "0")
        public Long fileSize;

        @ApiModelProperty(value = "s3Link", example = "s3Link")
        public String s3Link;

        @ApiModelProperty("최초 생성일")
        private LocalDateTime createdAt;

        @ApiModelProperty("최종 수정일")
        private LocalDateTime updatedAt;

        public static FlowCommentDto from (FlowComment dto){
            return FlowCommentDto.builder()
                    .idx(dto.idx())
                    .idxCorp(dto.corp().idx())
                    .idxUser(dto.user().idx())
                    .userName(dto.user().name())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .comment(dto.comment())
                    .fileName(dto.fileName())
                    .fileSize(dto.fileSize())
                    .s3Link(dto.s3Link())
                    .orgFileName(dto.orgFileName())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowCommentLastDto {

        @ApiModelProperty(value = "name", example = "홍승재")
        public String name;

        @ApiModelProperty(value = "commnet", example = "true")
        public String comment;

        @ApiModelProperty(value = "코멘트 신규 여부", example = "true")
        public Boolean readYn;

        @ApiModelProperty("최초 생성일")
        private LocalDateTime createdAt;

        @ApiModelProperty("최종 수정일")
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowReportDto {

        @ApiModelProperty(value = "commnet", example = "true")
        public String comment;

        @ApiModelProperty(value = "통계마지막 생성시간")
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowExcelPath {

        @ApiModelProperty(value = "fileName", example = "temp.xls")
        public String fileName;

        @ApiModelProperty(value = "file")
        public byte[] file;
    }
}
