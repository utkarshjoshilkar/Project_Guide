package com.studentguide.platform.github.scanner.service;

import org.springframework.stereotype.Service;

@Service
public class LanguageDetectionService {

    public String detectLanguage(String fileName) {

        String name = fileName.toLowerCase();

        if (name.endsWith(".java")) {
            return "JAVA";
        }

        if (name.endsWith(".py")) {
            return "PYTHON";
        }

        if (name.endsWith(".ts")) {
            return "TYPESCRIPT";
        }

        if (name.endsWith(".tsx")) {
            return "TSX";
        }

        if (name.endsWith(".js")) {
            return "JAVASCRIPT";
        }

        if (name.endsWith(".jsx")) {
            return "JSX";
        }

        if (name.endsWith(".html")) {
            return "HTML";
        }

        if (name.endsWith(".css")) {
            return "CSS";
        }

        if (name.endsWith(".json")) {
            return "JSON";
        }

        if (name.endsWith(".xml")) {
            return "XML";
        }

        if (name.endsWith(".yml") || name.endsWith(".yaml")) {
            return "YAML";
        }

        return "UNKNOWN";
    }
}