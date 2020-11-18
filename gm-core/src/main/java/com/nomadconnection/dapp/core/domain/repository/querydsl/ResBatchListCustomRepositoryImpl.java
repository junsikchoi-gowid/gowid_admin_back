package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.QCommonCodeDetail;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.res.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResBatchListCustomRepositoryImpl extends QuerydslRepositorySupport implements ResBatchListCustomRepository{

    private final QCorp corp = QCorp.corp;
    private final QCommonCodeDetail commonCodeDetail = QCommonCodeDetail.commonCodeDetail;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;
    private final QResAccount resAccount1 = QResAccount.resAccount1;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public ResBatchListCustomRepositoryImpl() {
        super(ResBatchList.class);
    }

    @Override
    public Page<ScrapAccountListDto> scrapAccountList(ScrapAccountDto dto, Pageable pageable){

        LocalDate ld = LocalDate.parse(dto.getUpdatedAt().replaceAll("-", ""), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDateTime from = ld.atTime(0,0);
        LocalDateTime to = from.plusDays(1);

        List<ScrapAccountListDto> list;

        JPQLQuery<ScrapAccountListDto> query = from(resBatchList)
                .join(corp).on(corp.user.idx.eq(resBatchList.idxUser))
                .leftJoin(commonCodeDetail).on(commonCodeDetail.code.eq(CommonCodeType.BANK_1).and(commonCodeDetail.code1.eq(resBatchList.bank)))
                .leftJoin(resAccount1).on(resAccount1.resAccount.eq(resBatchList.account))
                .select(Projections.bean(ScrapAccountListDto.class,
                        corp.idx.as("idxCorp"),
                        corp.resCompanyNm.as("idxCorpName"),
                        commonCodeDetail.code1.as("bankCode"),
                        commonCodeDetail.value1.as("bankName"),
                        resAccount1.type.as("accountType"),
                        resAccount1.resAccount.as("resAccount"),
                        resAccount1.resAccountBalance.as("resAccountBalance"),
                        resAccount1.resAccountDisplay.as("resAccountDisplay"),
                        resBatchList.errMessage.as("errorMessage"),
                        resBatchList.transactionId.as("transactionId"),
                        resBatchList.updatedAt.as("updatedAt")
                ));

        query.where(resBatchList.createdAt.between(from,to));

        if (dto.getIdxCorp() != null) {
            query.where(corp.idx.eq(dto.getIdxCorp()));
        }

        if (dto.getCorpName() != null) {
            query.where(corp.resCompanyNm.toLowerCase().contains(dto.getCorpName().toLowerCase()));
        }

        if (dto.getBankName() != null) {
            query.where(commonCodeDetail.value1.toLowerCase().contains(dto.getBankName().toLowerCase()));
        }

        if (dto.getAccountType() != null) {
            query.where(resAccount1.type.toLowerCase().contains(dto.getAccountType().toLowerCase()));
        }

        if ( dto.getErrorYn() != null ) {
            if (dto.getErrorYn().toLowerCase().equals("true")){
                query.where(resBatchList.errCode.eq("CF-00000"));
            }else if (dto.getErrorYn().toLowerCase().equals("false")){
                query.where(resBatchList.errCode.notEqualsIgnoreCase("CF-00000"));
            }
        }

        if( pageable.getSort().isEmpty()) {
            query.orderBy(resBatchList.updatedAt.desc());
        }

        list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(list, pageable, query.fetchCount());
    }
}
