package com.timeandspacehub.formtopdf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc // Ensures MVC settings are enabled
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
            .allowedOrigins(
                "http://localhost:3000",
                            "https://dev.timeandspacehub.com") // Your React app's URL
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // List all required methods
            .allowedHeaders("*") // Allows all headers, including Authorization
            .allowCredentials(true) // Necessary if you use HTTP basic or cookies
            .maxAge(3600); // Caches the preflight request for 1 hour
    }
}