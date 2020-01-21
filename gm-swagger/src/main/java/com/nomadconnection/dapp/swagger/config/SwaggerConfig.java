package com.nomadconnection.dapp.swagger.config;

import com.google.common.collect.Lists;
import com.nomadconnection.dapp.codef.io.helper.CommonConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "swagger")
@EnableSwagger2
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class SwaggerConfig {

	@Slf4j
	@Profile("prod")
	@RestController
	public static class Rejecter {
		@GetMapping("/swagger-ui.html")
		public ResponseEntity reject() {
			if (log.isWarnEnabled()) {
				log.warn("([ reject ]) $error='rejected: swagger-ui.html'");
			}
			return ResponseEntity.notFound().build();
		}
	}

	private ApiInfoConfig apiInfo = new ApiInfoConfig();
	private String basePackage = "com.nomadconnection.dapp";

	@Data
	@NoArgsConstructor
	private static class ApiInfoConfig {

		private ContactConfig contact = new ContactConfig();
		private String title = "API Server";
		private String description = "SWAGGER for APIs";
		private String version = "1.0.0";
		private String termsOfServiceUrl = "";
		private String license = "";
		private String licenseUrl = "";
		private Collection<VendorExtension> vendorExtensions = Collections.emptyList();

		@Data
		@NoArgsConstructor
		private static class ContactConfig {

			private String name = "";
			private String url = "";
			private String email = "";

			Contact build() {
				return new Contact(name, url, email);
			}
		}
	}

	@Bean
	public Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage(basePackage))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(getApiInfo())
				.ignoredParameterTypes()
				.securityContexts(Lists.newArrayList(securityContext()))
				.securitySchemes(Lists.newArrayList(apiKey()))
				/*.globalOperationParameters(
						Collections.singletonList(
								new ParameterBuilder()
										.name("Authorization")
										.parameterType("header")
										.modelRef(new ModelRef("string"))
										.description("액세스토큰(e.g. Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpb...ijFzeCcP19mJjd5g)")
										.required(false)
										.build()
						)
				)*/
				;
	}

	private ApiInfo getApiInfo() {
		return new ApiInfo(
				apiInfo.getTitle(),
				apiInfo.getDescription(),
				apiInfo.getVersion(),
				apiInfo.getTermsOfServiceUrl(),
				apiInfo.getContact().build(),
				apiInfo.getLicense(),
				apiInfo.getLicenseUrl(),
				apiInfo.getVendorExtensions()
		);
	}

	private ApiKey apiKey()
	{
		return new ApiKey("JWT","Authorization", CommonConstant.ACCESS_TOKEN);
	}

	private springfox.documentation.spi.service.contexts.SecurityContext securityContext() {
		return springfox.documentation.spi.service.contexts.SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
	}
}
