package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.CardTransaction;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionDto {
    @ApiModelProperty("식별자(사용자)")
    private Long idx;

    @ApiModelProperty("검색항목")
    private String searchType;

    @ApiModelProperty("검색년도")
    private Integer searchYear;

    @ApiModelProperty("검색월")
    private Integer searchMonth;

    @ApiModelProperty("검색년월")
    private String searchDate;

    @ApiModelProperty("선택된 카드(전체카드가 선택된 경우 null)")
    private List<Long> cards;

    @ApiModelProperty("월 총금액")
    private Integer allAmount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  MonthSum{
        @ApiModelProperty("식별자(사용자)")
        private Long idx;

        @ApiModelProperty("검색년도")
        private Integer searchYear;

        @ApiModelProperty("검색월")
        private Integer searchMonth;

        @ApiModelProperty("선택된 카드(전체카드가 선택된 경우 null)")
        private List<BigInteger> cards;

        @ApiModelProperty("월 총금액")
        private Long allAmount;

        public static MonthSum from(CardTransaction cardTransaction) {
            return MonthSum.builder()
                    .allAmount(cardTransaction.usedAmount())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistHeaders {

        @ApiModelProperty("식별자(사용자)")
        private Long idx;

        @ApiModelProperty("이름")
        private String name;

        @ApiModelProperty("식별자(부서)")
        private Long idxDept;

        @ApiModelProperty("부서명")
        private String dept;

        @ApiModelProperty("검색종류별 데이터")
        private String headerType;

        @ApiModelProperty("건수")
        private Integer headerCount;

        @ApiModelProperty("금액")
        private long amount;


       public static HistHeaders from(CardTransaction cardTransaction) {
           return HistHeaders.builder()
                  .idx(cardTransaction.idx())
//                    .name(cardTransaction.name())
//                    .idxDept(cardTransaction.idxDept())
//                    .dept(cardTransaction.dept())
//                    .headerType(cardTransaction.headerType())
//                    .headerCount(cardTransaction.headerCount())
//                    .amount(cardTransaction.amount())
                   .build();
      }
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo{
        @ApiModelProperty("식별자(카드)")
        private String idxCard;

        @ApiModelProperty("카드사용자")
        private String cardUserName;

        @ApiModelProperty("식별자(부서)")
        private Long idxDept;

        @ApiModelProperty("부서명")
        private Long deptName;

        @ApiModelProperty("카드No")
        private String cardNo;

        @ApiModelProperty("카드/월별/사용금액")
        private String usedAmount;

        public static CardInfo from(CardInfo cardInfo) {
            return CardInfo.builder()
                    // .asUsedAt(perDaily.getAsUsedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayHeader {
        @ApiModelProperty("날짜/항목/지역")
        private String asUsedAt;

        @ApiModelProperty("요일")
        private String week;

        @ApiModelProperty("총금액")
        private Long usedAmount;

        public static DayHeader from(DayHeader perDaily) {
            return DayHeader.builder()
                    .asUsedAt(perDaily.getAsUsedAt())
                    .week(perDaily.getWeek())
                    .usedAmount(perDaily.getUsedAmount())
                    .build();
        }
    }

    public class UsedInfo {
    }

    public class MonthUsedCard {
        @ApiModelProperty("식별자(사용자)")
        private Long idx;

        @ApiModelProperty("검색년월")
        private String yearMon;
    }
}

