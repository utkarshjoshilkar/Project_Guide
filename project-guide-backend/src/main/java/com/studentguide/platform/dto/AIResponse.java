package com.studentguide.platform.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Confirmation response returned to the client after a successful
 * roadmap generation and persistence.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {

    /** ID of the persisted Roadmap entity. */
    private Long roadmapId;

    /** ID of the project this roadmap belongs to. */
    private Long projectId;

    /** Human-readable confirmation message. */
    private String message;

    /** Timestamp when the roadmap was generated and persisted. */
    private LocalDateTime generatedAt;
}
