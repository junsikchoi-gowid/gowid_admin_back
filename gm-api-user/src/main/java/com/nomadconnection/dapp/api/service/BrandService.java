package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BrandDto;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.user.VerificationCodeRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final UserRepository repoUser;
    private final VerificationCodeRepository repoVerificationCode;
    private final PasswordEncoder encoder;

    /**
     * 사용자 계정 찾기
     *
     * @param name 이름
     * @param mdn 연락처(폰)
     * @return 계정 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity findAccount(String name, String mdn) {
        List<String> user = repoUser.findByNameAndMdn(name, mdn)
                .map(User::email)
                .map(email -> {
                    return email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
                })
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
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity companyCard(BrandDto.CompanyCard dto, Long idxUser) {

        User user = repoUser.findById(idxUser).orElseThrow(
                () -> new RuntimeException("UserNotFound")
        );

        user.cardCompany(dto.getCompanyCode());

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .data(repoUser.save(user))
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity deleteEmail(String email) {
        User user = repoUser.findByAuthentication_EnabledAndEmail(true,email).get();

        user.authentication(Authentication.builder().enabled(false).build());
        user.enabledDate(LocalDateTime.now());

        repoUser.save(user);

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .normal(BusinessResponse.Normal.builder()
                        .build())
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity passwordAuthPre(String email, String value, String password) {

        if(repoVerificationCode.findByVerificationKeyAndCode(email, value).isPresent()){
            User user = repoUser.findByAuthentication_EnabledAndEmail(true,email).orElseThrow(
                    () -> new RuntimeException("UserNotFound")
            );

            repoVerificationCode.deleteById(email);

            log.debug("pass $pass='{}'" , encoder.encode(password));
            user.password(encoder.encode(password));
            repoUser.save(user);
        }else{

            return ResponseEntity.ok().body(BusinessResponse.builder()
                    .normal(BusinessResponse.Normal.builder()
                            .status(false).value("비밀번호 or Email 이 맞지않음").build())
                    .build());
        }

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .normal(BusinessResponse.Normal.builder().build())
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity passwordAuthAfter(Long Idx, String prePassword, String afterPassword) {

        User userEmail = repoUser.findById(Idx).orElseThrow(
                () -> new RuntimeException("UserNotFound")
        );

        User user = repoUser.findByAuthentication_EnabledAndEmail(true, userEmail.email()).orElseThrow(
                () -> new RuntimeException("UserNotFound")
        );

        if (!encoder.matches(prePassword, user.password())) {
            return ResponseEntity.ok().body(BusinessResponse.builder()
                    .normal(BusinessResponse.Normal.builder()
                            .status(false)
                            .key("1")
                            .value("현재 비밀번호가 맞지않음")
                            .build())
                    .build());
        }

        user.password(encoder.encode(afterPassword));
        User returnUser = repoUser.save(user);

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .normal(BusinessResponse.Normal.builder()
                        .build())
                .data(returnUser)
                .build());
    }
}
