package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.QCardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.etc.QSurvey;
import com.nomadconnection.dapp.core.domain.etc.QSurveyAnswer;
import com.nomadconnection.dapp.core.domain.user.QUser;
import com.nomadconnection.dapp.core.domain.user.User;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class UserCustomRepositoryImpl extends QuerydslRepositorySupport implements UserCustomRepository {

    private final QUser user = QUser.user;
    private final QCorp corp = QCorp.corp;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QCardIssuanceInfo cardIssuanceInfo = QCardIssuanceInfo.cardIssuanceInfo;
    private final QSurvey survey = QSurvey.survey;
    private final QSurveyAnswer surveyAnswer = QSurveyAnswer.surveyAnswer;

    public UserCustomRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Page<UserListDto> userList(String keyWord, Pageable pageable) {
        List<UserListDto> list;

        JPQLQuery<UserListDto> query = from(user)
            .leftJoin(corp).on(user.idx.eq(corp.user.idx))
            .leftJoin(cardIssuanceInfo).on(user.idx.eq(cardIssuanceInfo.user.idx))
            .leftJoin(surveyAnswer).on(user.idx.eq(surveyAnswer.user.idx))
            .leftJoin(survey).on(surveyAnswer.answer.eq(survey.answer))
            .leftJoin(connectedMng).on(connectedMng.idxUser.eq(user.idx))
            .select(Projections.bean(UserListDto.class,
                user.idx.as("idxUser"),
                corp.idx.as("idxCorp"),
                cardIssuanceInfo.idx.as("idxCardIssuanceInfo"),
                user.name.as("userName"),
                user.email.as("email"),
                user.position.as("position"),
                user.cardCompany.as("cardCompany"),
                corp.resCompanyNm.as("resCompanyNm"),
                cardIssuanceInfo.issuanceDepth.as("issuanceDepth"),
                cardIssuanceInfo.issuanceStatus.as("issuanceStatus"),
                survey.answerName.as("surveyAnswer"),
                user.createdAt.as("signUpDate"),
                connectedMng.createdAt.as("certRegisterDate")
            ));
        query.where(user.authentication.enabled.isTrue());

//        if (dto.getKeyWord() != null) {
//            query.where(corp.resCompanyNm.toLowerCase().contains(dto.getKeyWord().toLowerCase()));
//        }

        list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(list, pageable, query.fetchCount());
    }

    @Override
    public UserInfoDto userInfo(Long idxUser) {
        JPQLQuery<UserInfoDto> query = from(user)
            .leftJoin(surveyAnswer).on(user.idx.eq(surveyAnswer.user.idx))
            .leftJoin(survey).on(surveyAnswer.answer.eq(survey.answer))
            .leftJoin(connectedMng).on(connectedMng.idxUser.eq(user.idx))
            .select(Projections.constructor(UserInfoDto.class,
                user.idx.as("idxUser"),
                user.name.as("userName"),
                user.mdn.as("phone"),
                user.corpName.as("corpName"),
                user.position.as("position"),
                user.reception.isSendSms.as("smsReception"),
                user.reception.isSendEmail.as("emailReception"),
                user.otherServiceUsage.expenseStatus.as("expenseStatus"),
                user.otherServiceUsage.saasUsage.as("saasUsage"),
                user.email.as("email"),
                survey.answerName.as("surveyAnswer"),
                surveyAnswer.detail.as("surveyAnswerDetail"),
                user.createdAt.as("signUpDate")
            ));
        query.where(user.authentication.enabled.isTrue());
        query.where(user.idx.eq(idxUser));

        return query.fetch().get(0);
    }
}