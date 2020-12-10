package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.limitInquiry.LimitInquiryDto;
import com.nomadconnection.dapp.api.dto.limitInquiry.LimitInquiryEmailType;
import com.nomadconnection.dapp.core.domain.etc.LimitInInquiry;
import com.nomadconnection.dapp.core.domain.repository.etc.LimitInquiryRepository;
import com.nomadconnection.dapp.core.utils.EnvUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LimitInquiryService {

    private final LimitInquiryRepository limitInquiryRepo;
    private final EmailService emailService;
    private final EmailConfig emailConfig;
    private final EnvUtil envUtil;

    public void saveLimitInquiry(LimitInquiryDto request) {
        LimitInInquiry limitInInquiry = new LimitInInquiry();
        BeanUtils.copyProperties(request, limitInInquiry);
        limitInquiryRepo.save(limitInInquiry);

        sendEmail(limitInInquiry);
    }

    public void sendEmail(LimitInInquiry limitInInquiry) {
        Map<String, Object> mailAttribute = getMailAttribute(limitInInquiry);

        String subject = LimitInquiryEmailType.SUBJECT.getValue();
        String[] mailTo = {emailConfig.getSender()};

        if (!envUtil.isProd()) {
            subject = "[테스트]" + subject;
            mailTo = new String[]{emailConfig.getSender(), "cy.lee@gowid.com"};
        }

        emailService.sendBenefitResultMail(mailAttribute,
                emailConfig.getSender(),
                mailTo,
                subject,
                LimitInquiryEmailType.TEMPLATE_NAME.getValue());
    }

    Map<String, Object> getMailAttribute(LimitInInquiry limitInInquiry) {
        Map<String, Object> mailAttributeMap = new HashMap<>();
        mailAttributeMap.put("hopeLimit", limitInInquiry.getHopeLimit());
        mailAttributeMap.put("contact", limitInInquiry.getContact());
        mailAttributeMap.put("corporationName", limitInInquiry.getCorporationName());
        mailAttributeMap.put("content", limitInInquiry.getContent());
        return mailAttributeMap;
    }
}
