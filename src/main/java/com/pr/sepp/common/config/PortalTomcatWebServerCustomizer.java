package com.pr.sepp.common.config;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class PortalTomcatWebServerCustomizer implements WebServerFactoryCustomizer {

    @Override
    public void customize(WebServerFactory container) {
        TomcatServletWebServerFactory containerFactory = (TomcatServletWebServerFactory) container;
        containerFactory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            connector.setAttribute("relaxedQueryChars", "[]|{}^\" < > ");
            connector.setAttribute("relaxedPathChars", "[]|");
        });
    }
}
