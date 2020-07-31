package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.core.domain.repository.common.EmailRepository;
import com.nomadconnection.dapp.core.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final EmailConfig emailConfig;
	private final JavaMailSenderImpl sender;
	private final ITemplateEngine templateEngine;
	private final EmailRepository repository;

	public void sendApproveEmail(String licenseNo) {
		EmailDto emailDto = repository.findTopByLicenseNo(licenseNo);
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("licenseNo", emailDto.getLicenseNo());
					context.setVariable("companyName", emailDto.getCompanyName());
					context.setVariable("hopeLimit", emailDto.getHopeLimit());
					context.setVariable("grantLimit", emailDto.getGrantLimit());
				}

				helper.setFrom(emailConfig.getSender());
				helper.setTo(emailConfig.getSender());
				helper.setSubject("[신한카드 심사완료] " + emailDto.getCompanyName());
				helper.setText(templateEngine.process("mail-template-issuance-approve", context), true);
			}
		};
		sender.send(preparator);
	}
}
