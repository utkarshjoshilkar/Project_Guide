package com.studentguide.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for the AI Roadmap microservice (FastAPI).
 *
 * Provides:
 * - A RestTemplate bean for synchronous HTTP calls (available throughout the app).
 * - The configured AI service base URL read from application.properties.
 */
@Configuration
public class AiServiceConfig {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    /**
     * Returns the base URL of the FastAPI AI service.
     * Used by AIIntegrationService to build request URLs.
     */
    public String getAiServiceUrl() {
        return aiServiceUrl;
    }

    /**
     * Shared RestTemplate bean.
     * RestTemplate is thread-safe once constructed; a single instance
     * is sufficient for the whole application.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
