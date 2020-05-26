package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.repository.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserCorporationService {

    private final UserRepository repoUser;
    private final CorpRepository repoCorp;

    /**
     * 법인정보 등록
     *
     * @param idx_user  등록하는 User idx
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerCorporation(Long idx_user, UserCorporationDto.RegisterCorporation dto) {
        User user = findUser(idx_user);

        // TODO : 법인정보 저장
        return null;
    }

    /**
     * 벤처기업정보 등록
     *
     * @param idx_user  등록하는 User idx
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerVenture(Long idx_user, UserCorporationDto.RegisterVenture dto) {
        User user = findUser(idx_user);

        // TODO : 벤처기업정보 저장
        return null;
    }

    /**
     * 주주정보 등록
     *
     * @param idx_user  등록하는 User idx
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerStockholder(Long idx_user, UserCorporationDto.RegisterStockholder dto) {
        User user = findUser(idx_user);

        // TODO : 주주정보 저장
        return null;
    }

    /**
     * 카드발급정보 등록
     *
     * @param idx_user  등록하는 User idx
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerCard(Long idx_user, UserCorporationDto.RegisterCard dto) {
        User user = findUser(idx_user);

        // TODO : 카드발급정보 저장
        return null;
    }

    /**
     * 결제 계좌정보 등록
     *
     * @param idx_user  등록하는 User idx
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerAccount(Long idx_user, UserCorporationDto.RegisterAccount dto) {
        User user = findUser(idx_user);

        // TODO : 카드발급정보 저장
        return null;
    }

    /**
     * 대표자 등록
     *
     * @param idx_user  등록하는 User idx
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerAccount(Long idx_user, UserCorporationDto.RegisterCeo dto) {
        User user = findUser(idx_user);

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

    private Corp findCorp(Long idx_corp) {
        return repoCorp.findById(idx_corp).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("Corp")
                        .idx(idx_corp)
                        .build()
        );
    }
}
