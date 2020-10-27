package com.nomadconnection.dapp.api.security;

import com.nomadconnection.dapp.api.controller.*;
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

//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.addAllowedOrigin("*");
//		configuration.addAllowedMethod("*");
//		configuration.addAllowedHeader("*");
//		configuration.setAllowCredentials(true);
//		configuration.setMaxAge(3600L);
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", configuration);
//		return source;
//	}

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
                .antMatchers(AuthController.URI.BASE + AuthController.URI.SEND_VERIFICATION_CODE).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.CHECK_VERIFICATION_CODE).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.ACCOUNT).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.PASSWORD_RESET_EMAIL).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.PASSWORD).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.TOKEN_ISSUE).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.TOKEN_ISSUE_OUT).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.TOKEN_REISSUE).permitAll()
                .antMatchers(AuthController.URI.BASE + AuthController.URI.VERIFICATION_CODE).permitAll()
                .antMatchers(UserController.URI.BASE + UserController.URI.REGISTER).permitAll()
                .antMatchers(UserController.URI.BASE + UserController.URI.REGISTRATION_USER).permitAll()
                .antMatchers(UserController.URI.BASE + UserController.URI.REGISTRATION_CORP).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.ACCOUNT).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.USERDELETE).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.USERPASSWORDCHANGE_PRE).permitAll()
                .antMatchers(ConsentController.URI.BASE + ConsentController.URI.CONSENT).permitAll()
                .antMatchers(FaqController.URI.BASE + FaqController.URI.FAQ_SAVE).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.RECEPTION).permitAll()
                .antMatchers(UserEtcController.URI.BASE + UserEtcController.URI.ALARM).permitAll()
                .antMatchers(RiskController.URI.BASE + RiskController.URI.RISK).permitAll()
                .antMatchers(ScrapingController.URI.BASE + ScrapingController.URI.SCRAPING_ACCOUNT_HISTORY).permitAll()
                .antMatchers(ScrapingController.URI.BASE + ScrapingController.URI.SCRAPING_ACCOUNT).permitAll()
                .antMatchers(IrDashBoardController.URI.BASE + IrDashBoardController.URI.IRDASHBOARD).permitAll()
                .antMatchers(UserController.URI.BASE + UserController.URI.REGISTRATION_PW + 2).permitAll()
                .antMatchers(BankController.URI.BASE + BankController.URI.MONTH_BALANCE_EXT).permitAll()
                .antMatchers(ShinhanCardController.URI.BASE + ShinhanCardController.URI.RESUME).permitAll()
                .antMatchers(BenefitController.URI.BASE + BenefitController.URI.BENEFITS).permitAll()
                .antMatchers(BenefitController.URI.BASE + BenefitController.URI.BENEFIT).permitAll()
                .antMatchers(BenefitController.URI.BASE + BenefitController.URI.BENEFIT_CATEGORIES).permitAll()
                .anyRequest().authenticated();
    }
}
