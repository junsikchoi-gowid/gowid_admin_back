package com.nomadconnection.dapp;

import com.nprotect.pluginfree.PluginFreeFilter;
import com.nprotect.pluginfree.PluginFreeServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class SecuKeypadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuKeypadApplication.class, args);
	}

	@PostConstruct
	private void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	@Bean
	public ServletRegistrationBean getServletRegistrationBean() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new PluginFreeServlet());
		registrationBean.addUrlMappings("/nppfs.servlet.do");
		registrationBean.addInitParameter("PropertiesPath", SecuKeypadApplication.class.getClassLoader().getResource("nprotect.properties").getPath());
		registrationBean.addInitParameter("ResponseEncoding", "UTF-8");
		registrationBean.addInitParameter("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,UPDATE,OPTIONS");
		registrationBean.addInitParameter("Access-Control-Allow-Headers", "Authorization, AuthorizationKey, X-Requested-With, X-HTTP-Method-Override, Content-Type, Accept");
		registrationBean.addInitParameter("Access-Control-Max-Age", "1728000");
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean getFilterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new PluginFreeFilter());
		registrationBean.addUrlPatterns("/*"); //add pattern
		registrationBean.addInitParameter("PropertiesPath", SecuKeypadApplication.class.getClassLoader().getResource("nprotect.properties").getPath());    //add init-param
		registrationBean.addInitParameter("RequestEncoding", "UTF-8");    //add init-param
		return registrationBean;
	}
}
