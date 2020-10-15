package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.QCommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.res.*;
import com.nomadconnection.dapp.core.domain.risk.QRisk;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
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
    private final QRisk risk = QRisk.risk;
    private final QCommonCodeDetail commonCodeDetail = QCommonCodeDetail.commonCodeDetail;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;
    private final QResBatch resBatch = QResBatch.resBatch;
    private final QResAccount resAccount1 = QResAccount.resAccount1;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;

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





    @Override
    public Page<ScrapAccountListDto> scrapAccountList(ScrapAccountDto dto, Pageable pageable){

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
                        resAccount1.resAccountDisplay.as("resAccountDisplay"),
                        resBatchList.errMessage.as("errorMessage"),
                        resBatchList.transactionId.as("transactionId"),
                        resBatchList.updatedAt.as("updatedAt")
                ));
        if( pageable.getSort().isEmpty()) {
            query.orderBy(resBatchList.updatedAt.desc());
        }

        if (dto.getCorpName() != null) {
            query.where(corp.resCompanyNm.toLowerCase().contains(dto.getCorpName().toLowerCase()));
        }

        if (dto.getUpdatedAt() != null) {
            query.where(resBatchList.updatedAt.after(LocalDateTime.parse(dto.getUpdatedAt(), DateTimeFormatter.BASIC_ISO_DATE)));
        }

        if ( dto.getErrorYn() != null ) {
            if (dto.getErrorYn().toLowerCase().equals("true")){
                query.where(resBatchList.errCode.eq("CF-00000"));
            }else if (dto.getErrorYn().toLowerCase().equals("false")){
                query.where(resBatchList.errCode.notEqualsIgnoreCase("CF-00000"));
            }
        }

        list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(list, pageable, query.fetchCount());
    }
}
