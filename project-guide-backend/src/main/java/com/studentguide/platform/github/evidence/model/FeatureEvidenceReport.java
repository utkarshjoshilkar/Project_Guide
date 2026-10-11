package com.studentguide.platform.github.evidence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureEvidenceReport {
    private List<FeatureEvidence> evidences;
}
