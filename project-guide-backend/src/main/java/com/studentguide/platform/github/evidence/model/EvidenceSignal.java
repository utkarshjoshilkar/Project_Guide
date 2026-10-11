package com.studentguide.platform.github.evidence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceSignal {
    private String description;
    
    @JsonProperty("type")
    private String signalType;
    
    private String filePath;
    private EvidenceStrength strength;
}
