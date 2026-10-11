package com.studentguide.platform.github.parser.service;

import com.studentguide.platform.config.AiServiceConfig;
import com.studentguide.platform.github.parser.model.CodeAnalysisRequest;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiCodeAnalysisService {

    private final RestTemplate restTemplate;
    private final AiServiceConfig aiServiceConfig;

    public ParsedArtifact analyzeJavaFile(String filePath, String sourceCode) {
        String url = aiServiceConfig.getAiServiceUrl() + "/api/code-analysis/java";
        CodeAnalysisRequest request = new CodeAnalysisRequest(filePath, sourceCode);
        
        try {
            return restTemplate.postForObject(url, request, ParsedArtifact.class);
        } catch (Exception e) {
            log.error("Failed to analyze Java file: {} via FastAPI. Error: {}", filePath, e.getMessage());
            throw new RuntimeException("FastAPI analysis failed for " + filePath, e);
        }
    }
}
