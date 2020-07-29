package com.nomadconnection.dapp.api.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WasConfig {
    @Value("${tomcat.ajp.protocol}")
    String ajpProtocol;

    @Value("${tomcat.ajp.port}")
    int ajpPort;

    @Value("${tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;

    @Value("${tomcat.ajp.connection-timeout}")
    int tomcatAjpConnectionTimeout;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        if (tomcatAjpEnabled) {
            Connector ajpConnector = createAjpConnector();
            tomcat.addAdditionalTomcatConnectors(ajpConnector);
            tomcat.addConnectorCustomizers(connector ->
                    ((AbstractProtocol) ajpConnector.getProtocolHandler()).setConnectionTimeout(tomcatAjpConnectionTimeout));
        }
        return tomcat;
    }

    private Connector createAjpConnector() {
        Connector ajpConnector = new Connector(ajpProtocol);
        ajpConnector.setPort(ajpPort);
        ajpConnector.setSecure(false);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme("http");
        return ajpConnector;
    }
}
