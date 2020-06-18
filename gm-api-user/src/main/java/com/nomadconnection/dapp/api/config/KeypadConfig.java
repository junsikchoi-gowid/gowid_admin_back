package com.nomadconnection.dapp.api.config;

import com.nprotect.pluginfree.PluginFreeFilter;
import com.nprotect.pluginfree.PluginFreeServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeypadConfig {

	@Bean
	public ServletRegistrationBean getServletRegistrationBean() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new PluginFreeServlet());
		registrationBean.addUrlMappings("/nppfs.servlet.do");
		registrationBean.addInitParameter("PropertiesPath", "/META-INF/keypad/nprotect.properties");
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
		registrationBean.addInitParameter("PropertiesPath", "/META-INF/keypad/nprotect.properties");    //add init-param
		registrationBean.addInitParameter("RequestEncoding", "UTF-8");    //add init-param
		return registrationBean;
	}
}
