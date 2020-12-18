package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.repository.common.EmailRepository;
import com.nomadconnection.dapp.core.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
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
				helper.setSubject("[Gowid] 신한카드 심사완료 " + emailDto.getCompanyName());
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

	public void sendReceiptEmail(String licenseNo, Map issuanceCounts, CardCompany cardCompany, String targetStatus) {
		try {
            log.info("[ sendReceiptEmail ] findTopByLicenseNo {}", licenseNo);
            EmailDto emailDto = repository.findTopByLicenseNo(licenseNo);
            log.info("[ sendReceiptEmail ] emailDto {}", emailDto);
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
			log.info("[ sendReceiptEmail ] send start");
			sender.send(preparator);
			log.info("[ sendReceiptEmail ] send done");
		} catch (Exception e) {
			log.error("[ sendReceiptEmail ] Error Occur! {}", e);
		}
	}

	public boolean sendBenefitResultMail(Map<String, Object> mailAttribute, String emailFrom, String emailTo, String mailSubject, String mailTemplate) {
		return sendBenefitResultMail(mailAttribute, emailFrom, new String[]{ObjectUtils.isEmpty(emailTo) ? emailFrom : emailTo}, mailSubject, mailTemplate);
	}

	/**
	 * Send Benefit Payment Result Mail
	 *
	 * @param mailAttribute	Mail Attribute Map
	 * @param emailTo		Send To
	 * @param mailSubject	Mail Subject
	 * @param mailTemplate	Mail Template (success: benefit-payment-success, order: benefit-payment-order, fail: benefit-payment-failed)
	 */
	public boolean sendBenefitResultMail(Map<String, Object> mailAttribute, String emailFrom, String[] emailTo, String mailSubject, String mailTemplate) {

		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					mailAttribute.forEach(context::setVariable);
				}
				helper.setFrom(emailFrom);
				helper.setTo(ObjectUtils.isEmpty(emailTo) ? new String[]{emailFrom} : emailTo);
				helper.setCc(emailFrom);
				helper.setSubject(mailSubject);
				helper.setText(templateEngine.process(mailTemplate, context), true);
			}
		};

		try {
			sender.send(preparator);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	public void send(EmailDto emailDto){
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					emailDto.getContext().forEach((attributeName, attributeValue)
						-> context.setVariable(attributeName, attributeValue));
				}
				helper.setFrom(emailConfig.getSender());
				helper.setTo(emailDto.getTo());
				helper.setSubject(emailDto.getSubject());
				helper.setText(templateEngine.process(emailDto.getTemplate(), context), true);
			}
		};
		sender.send(preparator);
	}

}
