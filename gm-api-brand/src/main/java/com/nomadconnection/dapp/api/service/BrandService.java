package com.nomadconnection.dapp.api.service;
import com.nomadconnection.dapp.api.dto.BrandDto;
import com.nomadconnection.dapp.core.domain.Authority;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final UserRepository repoUser;

    /**
     * 사용자 계정 찾기
     *
     * @param name 이름
     * @param mdn 연락처(폰)
     * @return 계정 정보
     */
    @Transactional
    public ResponseEntity findAccount(String name, String mdn) {
        List<String> user = repoUser.findByNameAndMdn(name, mdn)
                .map(User::email)
                .map(email -> email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*"))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(BusinessResponse.builder().data(user).build());
    }

    /**
     * 사용자 회사 설정 MAPPING
     *
     * @param dto 카드회사
     * @param idxUser 사용자정보
     * @return 계정 정보
     */
    public ResponseEntity companyCard(BrandDto.CompanyCard dto, Long idxUser) {

        User user = repoUser.findById(idxUser).orElseThrow(
                () -> new RuntimeException("UserNotFound")
        );

        user.cardCompany(dto.getCompanyCode());

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .data(repoUser.save(user))
                .build());
    }

    public ResponseEntity deleteEmail(String email) {
        User user = repoUser.findByEmail(email).get();

        user.authentication(Authentication.builder().enabled(false).build());

        repoUser.save(user);

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .normal(BusinessResponse.Normal.builder()
                        .build())
                .build());
    }
}
