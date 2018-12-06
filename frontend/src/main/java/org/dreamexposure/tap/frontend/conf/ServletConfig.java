package org.dreamexposure.tap.frontend.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @author NovaFox161
 * Date Created: 12/6/2018
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@Configuration
@EnableAutoConfiguration
public class ServletConfig {
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/"));
            container.setPort(Integer.valueOf(FrontendSettings.PORT.get()));
        });
    }
}