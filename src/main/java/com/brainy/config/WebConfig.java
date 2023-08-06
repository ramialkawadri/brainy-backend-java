package com.brainy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    private UserInterceptor userInterceptor;

    public WebConfig(UserInterceptor userInterceptor) {
        this.userInterceptor = userInterceptor;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor);
    }

    @Override
    protected void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {

        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
