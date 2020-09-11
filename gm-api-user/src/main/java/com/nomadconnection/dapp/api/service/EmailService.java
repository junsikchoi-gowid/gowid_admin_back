package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
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
					context.setVariable("email", emailDto.getEmail());
				}

				helper.setFrom(emailConfig.getSender());
				helper.setTo(emailConfig.getSender());
				helper.setSubject("[신한카드 심사완료] " + emailDto.getCompanyName());
				helper.setText(templateEngine.process("mail-template-issuance-approve", context), true);


			}
		};
		sender.send(preparator);
	}

	public void sendApproveEmail(String licenseNo, String issuanceCount, String targetStatus) {
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
					context.setVariable("email", emailDto.getEmail());
					context.setVariable("issuanceCount", issuanceCount);
					context.setVariable("targetStatus", targetStatus);
				}

				helper.setFrom(emailConfig.getSender());
				helper.setTo(emailConfig.getSender());
				helper.setSubject("[Gowid] 신한카드 심사완료 " + emailDto.getCompanyName());
				helper.setText(templateEngine.process("mail-template-issuance-approve2", context), true);
			}
		};
		sender.send(preparator);
	}

	public void sendReceiptEmail(String licenseNo, String[] issuanceCounts, CardCompany cardCompany, String targetStatus) {
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
					context.setVariable("email", emailDto.getEmail());
					context.setVariable("issuanceCount", issuanceCounts);
                    context.setVariable("targetStatus", targetStatus);
					context.setVariable("cardCompanyCode", cardCompany.getCode());
					context.setVariable("cardCompanyName", cardCompany.getName());
				}

				helper.setFrom(emailConfig.getSender());
				helper.setTo(emailConfig.getSender());
				helper.setSubject("[Gowid] " + cardCompany.getName() + " 접수완료 " + emailDto.getCompanyName());
				helper.setText(templateEngine.process("mail-template-issuance-receipt", context), true);
			}
		};
		sender.send(preparator);
	}
}
