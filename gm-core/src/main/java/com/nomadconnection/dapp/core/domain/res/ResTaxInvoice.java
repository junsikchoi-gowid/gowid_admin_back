package com.nomadconnection.dapp.core.domain.res;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ResTaxInvoice extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;
    private Long idxUser;
    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(255) COMMENT '발급형태'")
    private String resIssueNm;

    @Column(columnDefinition = "varchar(255) COMMENT '세액'")
    private String resTaxAmt;

    @Column(columnDefinition = "varchar(255) COMMENT '발급일자'")
    private String resIssueDate;

    @Column(columnDefinition = "varchar(255) COMMENT '승인번호'")
    private String resApprovalNo;

    @Column(columnDefinition = "varchar(255) COMMENT '공급가액'")
    private String resSupplyValue;

    @Column(columnDefinition = "varchar(255) COMMENT '작성일자'")
    private String resReportingDate;

    @Column(columnDefinition = "varchar(255) COMMENT '전송일자'")
    private String resTransferDate;

    @Column(columnDefinition = "varchar(255) COMMENT '공급자 등록번호'")
    private String resSupplierRegNumber;

    @Column(columnDefinition = "varchar(255) COMMENT '공급자 종사업장번호'")
    private String resSupplierEstablishNo;

    @Column(columnDefinition = "varchar(255) COMMENT '공급자 상호'")
    private String resSupplierCompanyName;

    @Column(columnDefinition = "varchar(255) COMMENT '공급자 대표자명'")
    private String resSupplierName;

    @Column(columnDefinition = "varchar(255) COMMENT '공급받는자 등록번호'")
    private String resContractorRegNumber;

    @Column(columnDefinition = "varchar(255) COMMENT '공급받는자 종사업장번호'")
    private String resContractorEstablishNo;

    @Column(columnDefinition = "varchar(255) COMMENT '공급받는자 상호'")
    private String resContractorCompanyName;

    @Column(columnDefinition = "varchar(255) COMMENT '공급받는자 대표자명'")
    private String resContractorName;

    @Column(columnDefinition = "varchar(255) COMMENT '합계금액'")
    private String resTotalAmount;

    @Column(columnDefinition = "varchar(255) COMMENT '전자세금계산서 종류'")
    private String resETaxInvoiceType;

    @Column(columnDefinition = "varchar(255) COMMENT '비고'")
    private String resNote;

    @Column(columnDefinition = "varchar(255) COMMENT '영수/청구'")
    private String resReceiptOrCharge;

    @Column(columnDefinition = "varchar(255) COMMENT '이메일'")
    private String resEmail;

    @Column(columnDefinition = "varchar(255) COMMENT '이메일1'")
    private String resEmail1;

    @Column(columnDefinition = "varchar(255) COMMENT '이메일2'")
    private String resEmail2;

    @Column(columnDefinition = "varchar(255) COMMENT '대표품목'")
    private String resRepItems;
}
