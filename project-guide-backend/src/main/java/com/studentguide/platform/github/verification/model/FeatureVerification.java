package com.studentguide.platform.github.verification.model;

import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.FeatureEvidence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureVerification {
    private Task expectedTask;
    private FeatureEvidence evidence;
    private VerificationStatus status;
    private String reasoning;
}
