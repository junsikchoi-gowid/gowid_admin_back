package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.D1000;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.Card;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.Stockholder;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.Venture;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.repository.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCorporationService {

    private final UserRepository repoUser;
    private final CorpRepository repoCorp;
    private final CardIssuanceInfoRepository repoCardIssuance;
    private final D1000Repository repoD1000;

    /**
     * 법인정보 등록
     *
     * @param idx_user 등록하는 User idx
     * @param dto      등록정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.CorporationRes registerCorporation(Long idx_user, UserCorporationDto.RegisterCorporation dto, Long idx_CardInfo) {
        User user = findUser(idx_user);

        D1000 d1000 = findD100(user.corp().idx());
        Corp corp = repoCorp.save(user.corp().resCompanyEngNm(dto.getEngCorName())
                .resCompanyNumber(dto.getCorNumber())
                .resBusinessCode(dto.getBusinessCode())
                .resUserType(d1000.d009())
        );

        CardIssuanceInfo cardInfo;
        try {
            cardInfo = findCardIssuanceInfo(user.corp());
            if (cardInfo.idx().equals(idx_CardInfo)) {
                throw MismatchedException.builder().build();
            }

        } catch (EntityNotFoundException e) {
            cardInfo = repoCardIssuance.save(CardIssuanceInfo.builder().corp(corp).build());
            repoD1000.save(d1000.d006(dto.getEngCorName()).d008(dto.getBusinessCode()));
        }
        return UserCorporationDto.CorporationRes.from(corp, cardInfo.idx());
    }

    /**
     * 벤처기업정보 등록
     *
     * @param idx_user 등록하는 User idx
     * @param dto      등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.VentureRes registerVenture(Long idx_user, UserCorporationDto.RegisterVenture dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().build();
        }

        cardInfo.venture(Venture.builder().investAmount(dto.getAmount())
                .isVC(dto.getIsVC())
                .isVerifiedVenture(dto.getIsVerifiedVenture())
                //TODO: 벤처기업관련 테이블정보 저장
                .build()
        );

        return UserCorporationDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    /**
     * 주주정보 등록
     *
     * @param idx_user 등록하는 User idx
     * @param dto      등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.VentureRes registerStockholder(Long idx_user, UserCorporationDto.RegisterStockholder dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().build();
        }

        cardInfo.stockholder(Stockholder.builder()
                .isStockHold25(dto.getIsHold25())
                .isStockholderList(dto.getIsStockholderList())
                .isStockholderPersonal(dto.getIsPersonal())
                .stockholderName(dto.getName())
                .stockholderEngName(dto.getEngName())
                .stockholderBirth(dto.getBirth())
                .stockholderNation(dto.getNation())
                .stockRate(dto.getRate())
                .build());

        return UserCorporationDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    /**
     * 카드발급정보 등록
     *
     * @param idx_user 등록하는 User idx
     * @param dto      등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.CardRes registerCard(Long idx_user, UserCorporationDto.RegisterCard dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().build();
        }

        cardInfo.card(Card.builder()
                .addressBasic(dto.getAddressBasic())
                .addressDetail(dto.getAddressDetail())
                .zipCode(dto.getZipCode())
                .hopeLimit(dto.getAmount())
                .receiveType(dto.getReceiveType())
                .requestCount(dto.getCount())
                .build());

        return UserCorporationDto.CardRes.from(repoCardIssuance.save(cardInfo));
    }

    /**
     * 결제 계좌정보 등록
     *
     * @param idx_user 등록하는 User idx
     * @param dto      등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.AccountRes registerAccount(Long idx_user, UserCorporationDto.RegisterAccount dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().build();
        }

        cardInfo.bankAccount(BankAccount.builder()
                .bankAccount(dto.getAccountNumber())
                .bankName(dto.getBank())
                .bankAccountHolder(dto.getAccountHolder())
                .build());

        return UserCorporationDto.AccountRes.from(repoCardIssuance.save(cardInfo));
    }

    /**
     * 대표자 정보
     *
     * @param idx_user 조회하는 User idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public String getCeoType(Long idx_user) {
        User user = findUser(idx_user);
        return findD100(user.corp().idx()).d009();
    }

    /**
     * 대표자 등록
     *
     * @param idx_user 등록하는 User idx
     * @param dto      등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerCeo(Long idx_user, UserCorporationDto.RegisterCeo dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().build();
        }

        // TODO : 카드발급정보 저장
        return null;
    }

    private User findUser(Long idx_user) {
        return repoUser.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }

    private CardIssuanceInfo findCardIssuanceInfo(Corp corp) {
        return repoCardIssuance.findByCorpAndDisabledTrue(corp).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("CardIssuanceInfo")
                        .build()
        );
    }

    private D1000 findD100(Long idxCorp) {
        return repoD1000.findTopByIdxCorpOrderByIdxDesc(idxCorp).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("D1000")
                        .build()
        );
    }
}
