package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.EmailRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final EmailConfig emailConfig;
	private final JavaMailSenderImpl sender;
	private final ITemplateEngine templateEngine;
	private final EmailRepository repository;
	private final CardIssuanceInfoRepository cardIssuanceInfoRepository;

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

	public void sendDeleteAccountEmailtoUser(User user){
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("userName", user.name());
				}

				helper.setFrom(emailConfig.getSender());
				helper.setTo(user.email());
				helper.setSubject("[고위드] 그 동안 고위드를 이용해 주셔서 감사합니다.");
				helper.setText(templateEngine.process("delete-account-user", context), true);
			}
		};
		sender.send(preparator);
	}


    public void sendDeleteAccountEmailtoSupport(User user, String reason){
        CardIssuanceInfo cardInfo = cardIssuanceInfoRepository.findTopByUserAndDisabledFalseOrderByIdxDesc(user).orElse(null);

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
            {
                Context context = new Context();
                {
                	if (user.cardCompany() != null) {
						context.setVariable("cardCompany", user.cardCompany().getName());
					}
                	if (user.corp() != null) {
						context.setVariable("companyName", user.corp().resCompanyNm());
						context.setVariable("licenseNo", user.corp().resCompanyIdentityNo());
					}
					if (cardInfo != null) {
						context.setVariable("issuanceStatus", cardInfo.issuanceStatus().getStatus());
					}
					context.setVariable("email", user.email());
					context.setVariable("reason", reason);
                }

                helper.setFrom(emailConfig.getSender());
                helper.setTo(emailConfig.getSender());
                helper.setSubject("[Gowid] 회원탈퇴 요청 " + user.email());
                helper.setText(templateEngine.process("delete-account-support", context), true);
            }
        };
        sender.send(preparator);
    }

	public void sendWelcomeEmail(String licenseNo, String issuanceCount){
		EmailDto emailDto = repository.findTopByLicenseNo(licenseNo);
		String[] sendTo = {emailDto.getEmail(), emailConfig.getSender()};
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("companyName", emailDto.getCompanyName());
					context.setVariable("issuanceCount", issuanceCount);
				}

				helper.setFrom(emailConfig.getSender());
				helper.setTo(sendTo);
				helper.setSubject("[고위드] 고위드 카드 심사 승인 및 이용 안내");
				helper.setText(templateEngine.process("shinhan-welcome", context), true);
			}
		};
		sender.send(preparator);
	}

	public void sendResetEmail(String licenseNo){
		EmailDto emailDto = repository.findTopByLicenseNo(licenseNo);
		String[] sendTo = {emailDto.getEmail(), emailConfig.getSender()};
		String resetDate = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"));
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("companyName", emailDto.getCompanyName());
					context.setVariable("date", resetDate);
				}

				helper.setFrom(emailConfig.getSender());
				helper.setTo(sendTo);
				helper.setSubject("[고위드] 고위드 카드 신청정보가 7일 후 삭제될 예정입니다.");
				helper.setText(templateEngine.process("reset-expired-corp", context), true);
			}
		};
		sender.send(preparator);
	}


	public void induceEmail(String email) {
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				helper.setFrom(emailConfig.getSender());
				helper.setTo(email);
				helper.setSubject("[고위드] 스타트업 법인카드, 고위드 카드 신청 안내드립니다.");
				helper.setText(templateEngine.process("pop-poing-template", context), true);
			}
		};

		sender.send(preparator);
	}
}
