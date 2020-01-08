package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BrandFaqDto;
import com.nomadconnection.dapp.core.domain.Faq;
import com.nomadconnection.dapp.core.domain.Role;
import com.nomadconnection.dapp.core.domain.repository.FaqRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    /**
     * FaQ 조회
     * @param id
     * @return body success , 정상처리
     */
    @Transactional(rollbackFor = Exception.class)
    public Page<BrandFaqDto> faqs(BrandFaqDto dto, Pageable pageable) {

        return faqRepository.findAll(pageable).map(BrandFaqDto::from);
    }

    /**
     * FaQ 조회
     * @param BrandFaqDto
     * @return body success , 정상처리
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity faqSave(BrandFaqDto dto) {

        faqRepository.save(
                Faq.builder()
                .idx(dto.idx)
                .contents(dto.contents)
                .title(dto.title)
                .email(dto.email)
                .replyStatus(dto.replyStatus)
                .build()
        );

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .category("faqSave")
                .value("success")
                .reason("success")
                .build());
    }

    /**
     * FaQ 조회
     * @param id , user
     * @return body success , 정상처리
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity faqDel(List<Long> faqIds ,org.springframework.security.core.userdetails.User user) {

        //	권한
        if (user.getAuthorities().stream().anyMatch(o -> o.getAuthority().equals(Role.ROLE_MASTER.toString()))) {
            faqRepository.deleteFaqDeleteQuery(faqIds);
        } else {
            if (log.isErrorEnabled()) {
                log.error("([ postConsent ]) auth check , $user='{}', $faqIds='{}'", user, faqIds);
            }
            throw new RuntimeException("마스터 권한이 없음");
        }

        return ResponseEntity.ok().body(BusinessResponse.builder()
                .category("faqDel")
                .value("success")
                .reason("success")
                .build());
    }
}
