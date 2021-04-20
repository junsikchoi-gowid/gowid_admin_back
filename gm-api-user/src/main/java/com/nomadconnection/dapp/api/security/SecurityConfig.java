package com.nomadconnection.dapp.api.security;

import com.nomadconnection.dapp.api.controller.*;
import com.nomadconnection.dapp.api.v2.controller.admin.AdminNoticeController;
import com.nomadconnection.dapp.api.v2.controller.card.ShinhanCardControllerV2;
import com.nomadconnection.dapp.api.v2.controller.card.SurveyController;
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
import org.springframework.web.cors.CorsUtils;

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
            "/partner/**",
            "/v2/api-docs",
            "/webjars/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/csrf",
            "/static/**",
            "/META-INF/resources/WEB-INF/resources/**",
            "/favicon.ico",
            "/error",
            "/logout",
            "/index",
            "/pluginfree/**",
            "/nppfs.servlet.do/**",
			"/recovery/**",
            "/user/v1/members/{email}/password", // for sync user info
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

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(service).passwordEncoder(passwordEncoder());
    }

    @Override
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
                .antMatchers(AuthController.URI.BASE + AuthController.URI.ACCOUNT).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.TOKEN_ISSUE).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.TOKEN_REISSUE).permitAll()
                .antMatchers(UserController.URI.BASE + UserController.URI.REGISTRATION_USER).permitAll()
                .antMatchers(UserController.URI.BASE + UserController.URI.ENABLE).permitAll()
                .antMatchers(UserController.URI.BASE + UserController.URI.REGISTRATION_CORP).permitAll() // to be deprecated
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.INDUCE_EMAIL).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.INDUCE_EMAIL_MOBILE).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.INDUCE_EMAIL).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.INDUCE_EMAIL_MOBILE).permitAll()
                .antMatchers(ConsentController.URI.BASE + ConsentController.URI.CONSENT).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.RECEPTION).permitAll()
                .antMatchers(ScrapingController.URI.BASE + ScrapingController.URI.SCRAPING_ACCOUNT).permitAll()
                .antMatchers(IrDashBoardController.URI.BASE + IrDashBoardController.URI.IRDASHBOARD).permitAll()
                .antMatchers(BenefitController.URI.BASE + BenefitController.URI.BENEFITS).permitAll()
                .antMatchers(BenefitController.URI.BASE + BenefitController.URI.BENEFIT).permitAll()
                .antMatchers(BenefitController.URI.BASE + BenefitController.URI.BENEFIT_CATEGORIES).permitAll()
                .antMatchers(BenefitController.URI.BASE + BenefitController.URI.BENEFIT_SEARCH).permitAll()
                .antMatchers(FaqController.URI.BASE + FaqController.URI.FAQ_SAVE).permitAll()
                .antMatchers(com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.BASE + com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.SEND).permitAll()
                .antMatchers(com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.BASE + com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.VERIFY).permitAll()
                .antMatchers(com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.BASE + com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.CHANGE_PASSWORD_BEFORE_LOGIN).permitAll()
                .antMatchers(com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.BASE + com.nomadconnection.dapp.api.v2.controller.auth.AuthController.URI.TOKEN_ISSUE).permitAll()
                .antMatchers(com.nomadconnection.dapp.api.v2.controller.admin.AdminBaseController.URI.BASE + AdminNoticeController.URI.NOTICE).permitAll()
	            .antMatchers(ShinhanCardControllerV2.URI.BASE + ShinhanCardControllerV2.URI.RESUME).permitAll()
                .antMatchers(SurveyController.URI.BASE).permitAll()
                .antMatchers(LimitInquiryController.URI.BASE).permitAll()
	            .antMatchers(AdminController.URI.BASE).permitAll()
                .antMatchers(SaasTrackerController.URI.BASE + SaasTrackerController.URI.USAGE_REQUEST).permitAll()
                .anyRequest().authenticated();
    }
}
