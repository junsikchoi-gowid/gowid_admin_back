package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.AuthDto;
import com.nomadconnection.dapp.api.exception.ExpiredException;
import com.nomadconnection.dapp.api.exception.UnauthorizedException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.user.Authority;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.exception.UnacceptableJwtException;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final EmailConfig config;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final UserRepository repoUser;
	private final ConnectedMngRepository repoConnectedMng;
	private final PasswordEncoder encoder;

	/**
	 * ?????????(?????????) ???????????? ??????
	 *
	 * @param account ?????????(?????????)
	 * @return ?????????(?????????) ????????????
	 */
	public boolean isPresent(String account) {
		return repoUser.findByAuthentication_EnabledAndEmail(true, account).isPresent();
	}

	public void ifPresentThrowNotFound(String account) {
		repoUser.findByAuthentication_EnabledAndEmail(true, account).orElseThrow(
			() -> UserNotFoundException.builder()
				.email(account)
				.build()
		);
	}

	/**
	 * ????????? ?????? ??????
	 *
	 * @param name ??????
	 * @param mdn  ?????????(???)
	 * @return ?????? ??????
	 */
	@Transactional
	public List<String> findAccount(String name, String mdn) {
		return repoUser.findByNameAndMdn(name, mdn)
				.map(User::email)
				.map(email -> email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*"))
				.collect(Collectors.toList());
	}

	/**
	 * ???????????? ????????? - ????????? ??????
	 *
	 * @param email ???????????????
	 */
	public void sendPasswordResetEmail(String email) throws UserNotFoundException {
		User user = repoUser.findByAuthentication_EnabledAndEmail(true, email).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(email)
						.build()
		);
		Role role = Authority.from(user.authorities());

		TokenDto.Token token = jwt.issue(email, TokenDto.TokenType.JWT_FOR_AUTHENTICATION, new Date(), role.name());
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			helper.setFrom(config.getSender());
			helper.setTo(email);
			helper.setSubject("[Gowid] ???????????? ?????????");
			helper.setText("?????????: " + token.getJwt(), false);
		};
		sender.send(preparator);
	}

	/**
	 * ???????????? ????????? - ??? ???????????? ??????
	 *
	 * @param authenticationKey ?????????
	 * @param password          ????????????(??????)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void resetPassword(String authenticationKey, String password) {
		TokenDto token = jwt.parse(authenticationKey);
		{
			if (!TokenDto.TokenType.JWT_FOR_AUTHENTICATION.equals(token.getTokenType())) {
				throw UnacceptableJwtException.builder()
						.jwt(authenticationKey)
						.tokenType(token.getTokenType())
						.expectedTokenType(TokenDto.TokenType.JWT_FOR_AUTHENTICATION)
						.build();
			}
		}
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiration = LocalDateTime.ofInstant(token.getExpiration().toInstant(), ZoneId.systemDefault());
		{
			if (!now.isBefore(expiration)) {
				throw ExpiredException.builder()
						.now(now)
						.expiration(expiration)
						.build();
			}
		}
		User user = repoUser.findByAuthentication_EnabledAndEmail(true, token.getIdentifier()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(token.getIdentifier())
						.build()
		);
		user.password(encoder.encode(password));
	}

	/**
	 * ???????????? ??????
	 * <p>
	 * - ????????????
	 * - ????????????
	 * - ????????????
	 * - ????????????(????????????)
	 * - ????????????(????????????)
	 *
	 * @param dto ???????????? - ?????????, ????????????
	 * @return ????????????(??????) - ????????????, ????????????, ????????????, ????????????(????????????), ????????????(????????????), ????????????(??????, ...)
	 */
	@Transactional(readOnly = true)
	public TokenDto.TokenSet issueTokenSet(AccountDto dto) {
		User user = repoUser.findByAuthentication_EnabledAndEmail(true, dto.getEmail()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(dto.getEmail())
						.build()
		);


		Role role = Authority.from(user.authorities());

		if (!encoder.matches(dto.getPassword(), user.password())) {
			throw UnauthorizedException.builder()
					.account(dto.getEmail())
					.build();
		}

		boolean corpMapping = !StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = !StringUtils.isEmpty(user.cardCompany());

		Set<Authority> authorities = new HashSet<>();
		authorities.addAll(user.authorities());

		if(!ObjectUtils.isEmpty(user.corp())){
			if(!ObjectUtils.isEmpty(user.corp().authorities())) authorities.addAll(user.corp().authorities());
		}

		return jwt.issue(dto.getEmail(), authorities, user.idx(), corpMapping, cardCompanyMapping, user.hasTmpPassword(), role.name());
	}

	/**
	 * ???????????? ??????
	 *
	 * @param email ?????????(?????????)
	 * @param jwt   ????????????
	 * @return ???????????? ???????????? ?????? - ????????????, ????????????, ????????????
	 */
	public TokenDto.Token reissueAccessToken(String email, String jwt) {
		return this.jwt.reissueAccessToken(email, jwt);
	}

	/**
	 * ?????? ??????
	 * <p>
	 * - ????????? ??????
	 * - ?????? ?????? ??????
	 * - ??? ?????? ??????
	 *
	 * @param idxUser ?????????(?????????)
	 * @return ??????
	 */
	@Transactional
	public AuthDto.AuthInfo info(Long idxUser) {
		User user = serviceUser.getUser(idxUser);
		Long idxCorp = user.corp() == null ? null: user.corp().idx();

		Set<Authority> authorities = new HashSet<>();
		authorities.addAll(user.authorities());

		if(!ObjectUtils.isEmpty(user.corp())){
			if(!ObjectUtils.isEmpty(user.corp().authorities())) authorities.addAll(user.corp().authorities());
		}

		boolean corpMapping = !StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = !StringUtils.isEmpty(user.cardCompany());
		boolean signMapping = false;
		if(idxCorp != null & repoConnectedMng.findByCorp(user.corp()).size() > 0 ){
			signMapping = true;
		}

		boolean refreshMapping = true;
		if(idxCorp != null & repoConnectedMng.findRefresh(idxCorp) > 0 ){
			refreshMapping = false;
		}

		return AuthDto.AuthInfo.builder()
				.idx(user.idx())
				.idxCorp(user.corp() != null ? idxCorp : null)
				.email(user.email())
				.name(user.name())
				.mdn(user.mdn())
				.companyCard(!ObjectUtils.isEmpty(user.cardCompany()) ? user.cardCompany().name() : null)
				.corpStatus(!ObjectUtils.isEmpty(user.corp()) ? user.corp().status() : null)
				.info(TokenDto.TokenSet.AccountInfo.builder()
						.authorities(authorities.stream().map(Authority::role).collect(Collectors.toList()))
						.cardCompanyMapping(cardCompanyMapping)
						.corpMapping(corpMapping)
						.signMapping(signMapping)
						.refreshMapping(refreshMapping)
						.build())
				.isSendSms(user.reception().getIsSendSms())
				.isSendEmail(user.reception().getIsSendEmail())
				.build();
	}
}
