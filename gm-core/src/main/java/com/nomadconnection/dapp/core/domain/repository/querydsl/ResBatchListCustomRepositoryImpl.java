package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResBatchListCustomRepositoryImpl extends QuerydslRepositorySupport implements ResBatchListCustomRepository{

    private final QCorp corp = QCorp.corp;
    private final QCommonCodeDetail commonCodeDetail = QCommonCodeDetail.commonCodeDetail;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public ResBatchListCustomRepositoryImpl() {
        super(ResBatchList.class);
    }

    @Override
    public Page<ErrorResultDto> errorList(ErrorSearchDto dto, Pageable pageable){

        List<ErrorResultDto> list;

        JPQLQuery<ErrorResultDto> query = from(resBatchList)
                .join(corp).on(corp.user.idx.eq(resBatchList.idxUser))
                .leftJoin(commonCodeDetail).on(commonCodeDetail.code.eq(CommonCodeType.BANK_1).and(commonCodeDetail.code1.eq(resBatchList.bank)))
                .select(Projections.bean(ErrorResultDto.class,
                        corp.idx.as("idxCorp"),
                        resBatchList.updatedAt.as("updatedAt"),
                        corp.resCompanyNm.as("corpName"),
                        commonCodeDetail.value1.as("bankName"),
                        resBatchList.account.as("account"),
                        resBatchList.errMessage.as("errMessage"),
                        resBatchList.errCode.as("errCode"),
                        resBatchList.transactionId.as("transactionId")
                ))
                ;

        if (dto.getIdxCorp() != null) {
            query.where(corp.idx.eq(dto.getIdxCorp()));
        }

        if( pageable.getSort().isEmpty()) {
            query.orderBy(resBatchList.idx.desc());
        }

        if (dto.getCorpName() != null) {
            query.where(corp.resCompanyNm.toLowerCase().contains(dto.getCorpName().toLowerCase()));
        }

        if (dto.getBoolToday() != null) {

            LocalDateTime time = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00");
            
            if (dto.getBoolToday().toLowerCase().equals("true")){
                query.where(resBatchList.updatedAt.after(time));
            }
        }

        if ( dto.getErrorCode() != null ) {
            if (dto.getErrorCode().toLowerCase().equals("true")){
                query.where(resBatchList.errCode.eq("CF-00000"));
            }else{
                query.where(resBatchList.errCode.notEqualsIgnoreCase("CF-00000"));
            }
        }

        list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(list, pageable, query.fetchCount());
    }
}
