package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CorpCustomRepositoryImpl extends QuerydslRepositorySupport implements CorpCustomRepository {

    private final QRisk risk = QRisk.risk;
    private final QUser user = QUser.user;
    private final QCorp corp = QCorp.corp;
    private final QResAccount resAccount1 = QResAccount.resAccount1;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QResBatch resBatch = QResBatch.resBatch;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public CorpCustomRepositoryImpl() {
        super(Corp.class);
    }

    @Override
    public Page<SearchCorpResultDto> corpList(SearchCorpDto dto, Long idxUser, Pageable pageable) {

        final List<SearchCorpResultDto> riskList;
        final JPQLQuery<SearchCorpResultDto> query = from(corp)
                .select(Projections.bean(SearchCorpResultDto.class,
                        corp.resCompanyNm.as("cardLimitNow"),
                        corp.resCompanyIdentityNo.as("resCompanyIdentityNo"),
                        corp.resUserNm.as("resUserNm"),
                        corp.resBusinessItems.as("resBusinessItems"),
                        corp.resBusinessTypes.as("resBusinessTypes"),
                        corp.riskConfig.ceoGuarantee.as("ceoGuarantee"),
                        corp.riskConfig.depositGuarantee.as("depositGuarantee"),
                        corp.riskConfig.depositPayment.as("depositPayment"),
                        corp.riskConfig.cardIssuance.as("cardIssuance"),
                        corp.riskConfig.ventureCertification.as("ventureCertification"),
                        corp.riskConfig.vcInvestment.as("vcInvestment")
                ));

        query.where(corp.riskConfig.enabled.isTrue());

        if (dto.getResCompanyNm() != null) {
            query.where(corp.resCompanyNm.like(dto.getResCompanyNm()));
        }

        if (dto.getResCompanyIdentityNo() != null) {
            query.where(corp.resCompanyIdentityNo.eq(dto.getResCompanyIdentityNo()));
        }

        if (dto.getVentureCertification() != null) {
            query.where(corp.riskConfig.ventureCertification.eq(dto.getVentureCertification().toLowerCase().equals("true")));
        }

        if (dto.getVcInvestment() != null) {
            query.where(corp.riskConfig.vcInvestment.eq(dto.getVcInvestment().toLowerCase().equals("true")));
        }

        riskList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(riskList, pageable, query.fetchCount());
    }

    @Override
    public Page<CorpCustomRepository.ScrapingResultDto> scrapingList(Pageable pageable){

        final List<CorpCustomRepository.ScrapingResultDto> list;

        final JPQLQuery<CorpCustomRepository.ScrapingResultDto> query = from(corp)
                .join(resBatch).on(corp.user.idx.eq(resBatch.idxUser))
                .select(Projections.bean(CorpCustomRepository.ScrapingResultDto.class,
                        corp.idx.as("idxCorp"),
                        corp.resCompanyNm.as("idxCorpName"),
                        resBatch.createdAt.max().as("createdAt"),
                        resBatch.updatedAt.max().as("updatedAt"),
                        resBatch.endFlag.as("endFlag"),
                        resBatch.idxUser.as("idxUser")
                ))
                .groupBy(corp.idx)
                ;

        if( pageable.getSort().isEmpty()) query.orderBy(corp.idx.asc());

        list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(list, pageable, query.fetchCount());
    }
}
