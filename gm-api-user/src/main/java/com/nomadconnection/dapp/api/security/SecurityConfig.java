package com.nomadconnection.dapp.api.security;

import com.nomadconnection.dapp.api.controller.*;
import com.nomadconnection.dapp.core.domain.Faq;
import com.nomadconnection.dapp.core.security.CustomUserDetailsService;
import com.nomadconnection.dapp.jwt.authentication.CustomAuthenticationEntryPoint;
import com.nomadconnection.dapp.jwt.authentication.CustomAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	//
	//	fixme: permitted uris
	//
	private static final String[] PERMITTED_URIS = {
			"/",
			"/v2/api-docs",
			"/webjars/**",
			"/swagger-ui.html",
			"/swagger-resources/**",
			"/csrf",
			"/static/**",
			"/resources/**",
			"/favicon.ico",
			"/error",
			"/logout",
	};

	private final CustomUserDetailsService service;
	private final CustomAuthenticationFilter authenticationFilter;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("*");
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder.userDetailsService(service).passwordEncoder(passwordEncoder());
	}

	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		http.csrf()
				.disable();
		//
		//	Response Headers X-Frame-Options
		//
		http.headers()
				.frameOptions()
				.sameOrigin();
		//
		//	Session Stateless
		//
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		//
		//	JWT Authentication Filter & JWT Exception Handler
		//
		http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint);
		//
		//	Authorization Requests
		//
		http.authorizeRequests()
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				.antMatchers(HttpMethod.OPTIONS, "**").permitAll()
				.antMatchers(PERMITTED_URIS).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.EXISTS).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.SEND_VERIFICATION_CODE).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.CHECK_VERIFICATION_CODE).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.ACCOUNT).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.PASSWORD_RESET_EMAIL).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.PASSWORD).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.TOKEN_ISSUE).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.TOKEN_REISSUE).permitAll()
				.antMatchers(AuthController.URI.BASE + AuthController.URI.VERIFICATION_CODE).permitAll()
				.antMatchers(UserController.URI.BASE + UserController.URI.REGISTER).permitAll()
				.antMatchers(UserController.URI.BASE + UserController.URI.REGISTRATION_USER,
						UserController.URI.BASE + UserController.URI.REGISTRATION_CORP
				).permitAll()
				.antMatchers(CorpController.URI.BASE + CorpController.URI.REGISTRABLE).permitAll()
				.antMatchers(ConsentController.URI.BASE + ConsentController.URI.CONSENT,
						BrandController.URI.BASE + BrandController.URI.ACCOUNT,
						BrandController.URI.BASE + BrandController.URI.USERDELETE,
						BrandController.URI.BASE + BrandController.URI.USERPASSWORDCHANGE_PRE,
						FaqController.URI.BASE + FaqController.URI.FAQ_SAVE
				).permitAll()
				.anyRequest().authenticated();
	}
}
