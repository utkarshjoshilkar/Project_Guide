package com.studentguide.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * AiServiceConfig — configuration for the FastAPI AI roadmap microservice.
 *
 * Provides:
 *   - A RestTemplate bean with explicit connect and read timeouts (Phase 2, Priority 4).
 *   - The configured AI service base URL read from application.properties.
 *
 * Why timeouts matter:
 *   Without timeouts, a hanging FastAPI process causes the calling Spring thread
 *   to block indefinitely. Under concurrent load, this exhausts the thread pool
 *   and brings down the entire application. A read timeout of 60s gives the AI
 *   service sufficient time to call Gemini while still protecting the thread pool.
 *
 * Timeout values are externalised to application.properties so they can be
 * adjusted per environment without recompiling.
 */
@Slf4j
@Configuration
public class AiServiceConfig {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Value("${ai.service.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${ai.service.read-timeout-ms:60000}")
    private int readTimeoutMs;

    /**
     * Returns the base URL of the FastAPI AI service.
     * Used by AIIntegrationService to build request URLs.
     */
    public String getAiServiceUrl() {
        return aiServiceUrl;
    }

    /**
     * Shared RestTemplate bean with timeouts.
     *
     * RestTemplate is thread-safe once constructed; a single instance
     * is sufficient for the whole application.
     *
     * connect-timeout: max time to establish the TCP connection to FastAPI.
     * read-timeout:    max time to wait for FastAPI to return a response.
     *                  Set to 60s because Gemini AI generation can be slow.
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        log.info("RestTemplate configured: connectTimeout={}ms, readTimeout={}ms",
                connectTimeoutMs, readTimeoutMs);

        return new RestTemplate(factory);
    }
}
