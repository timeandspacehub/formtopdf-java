package com.timeandspacehub.formtopdf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/**
 * Since the automatic configuration is failing, force the redirect manually using Java configuration. 
 */
public class FallbackSwaggerConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // This maps "/swagger-ui" to the index page directly if the automatic redirect fails
        registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html");
    }
}