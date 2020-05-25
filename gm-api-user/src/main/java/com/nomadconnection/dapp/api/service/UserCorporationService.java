package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.core.domain.User;
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

    /**
     * 법인정보 등록
     *
     * @param idx_user  등록하는 User idx
     * @param code      전문 식별자
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerCorporation(Long idx_user, Long code, UserCorporationDto.registerCorporation dto) {
        User user = findUser(idx_user);

        // TODO : 법인정보 저장
        return null;
    }

    /**
     * 벤처기업정보 등록
     *
     * @param idx_user  등록하는 User idx
     * @param code      전문 식별자
     * @param dto       등록정보
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public Object registerVenture(Long idx_user, Long code, UserCorporationDto.registerVenture dto) {
        User user = findUser(idx_user);

        // TODO : 벤처기업정보 저장
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
}
