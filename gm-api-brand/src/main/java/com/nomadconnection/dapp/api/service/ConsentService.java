package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.dto.BrandFaqDto;
import com.nomadconnection.dapp.core.domain.Consent;
import com.nomadconnection.dapp.core.domain.Role;
import com.nomadconnection.dapp.core.domain.repository.ConsentRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository repoConsent;

    /**
     * 이용약관 현재 사용여부 등
     * 이용약관 목록
     */
    @Transactional(rollbackFor = Exception.class)
    public Page<BrandConsentDto> consents(BrandConsentDto dto, Pageable pageable) {

        // findall 기타 조건 설정 없음
        return repoConsent.findAll(pageable).map(BrandConsentDto::from);
    }

    /**
     * 이용약관 단건 저장
     * idx 키값이 있으면 수정됨
     *
     * @param title,contents,enabled,essential,version,idx
     * @return body success , 정상처리
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BusinessResponse> postConsent(org.springframework.security.core.userdetails.User user, BrandConsentDto dto) {

        //	권한
        if (user.getAuthorities().stream().anyMatch(o -> o.getAuthority().equals(Role.ROLE_MASTER.toString()))) {
            BrandConsentDto.from(repoConsent.save(Consent.builder()
                    .title(dto.title)
                    .contents(dto.contents)
                    .enabled(dto.enabled)
                    .essential(dto.essential)
                    .version(dto.version)
                    .idx(dto.idx)
                    .build()));
        } else {
            if (log.isErrorEnabled()) {
                log.error("([ postConsent ]) auth check , $user='{}', $dto='{}'", user, dto);
            }
            throw new RuntimeException("마스터 권한이 없음");
        }

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .category("postConsent")
                .value("success")
                .reason("success")
                .build());
    }

    /**
     * 이용약관 단건 삭제
     *
     * @param idx, user
     * @return body success , 정상처리
     */

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ErrorResponse> consentDel(org.springframework.security.core.userdetails.User user, Long idx) {

        if (user.getAuthorities().stream().anyMatch(o -> o.getAuthority().equals(Role.ROLE_MASTER.toString()))) {

            repoConsent.deleteById(idx);
        } else {
            if (log.isErrorEnabled()) {
                log.error("([ postConsent ]) auth check , $user='{}', $key='{}'", user, idx);
            }
            throw new RuntimeException("마스터 권한이 없음");
        }

        return ResponseEntity.ok().body(ErrorResponse.from("success", "정상처리"));
    }
}
