package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.BrandFaqDto;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqService {

    private final JavaMailSenderImpl sender;
    private final EmailConfig config;
    private final ITemplateEngine templateEngine;

    /**
     * FaQ 메일전송
     *
     * @param dto BrandFaq 내용
     * @return body success , 정상처리
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity faqSend(BrandFaqDto dto) {

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
            {
                Context context = new Context();
                {
                    context.setVariable("from", dto.email);
                    context.setVariable("contents", dto.contents);
                }

                helper.setFrom(config.getSender());
                helper.setTo(config.getSender());
                helper.setSubject("[Gowid 문의] " + dto.title);
                helper.setText(templateEngine.process("mail-template_qna", context), true);
            }
        };
        sender.send(preparator);

        return ResponseEntity.ok().body(BusinessResponse.builder().build());
    }
}