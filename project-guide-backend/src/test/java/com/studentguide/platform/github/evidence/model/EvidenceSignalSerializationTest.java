package com.studentguide.platform.github.evidence.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EvidenceSignalSerializationTest {

    @Test
    void testSerialization() throws Exception {
        EvidenceSignal signal = EvidenceSignal.builder()
                .description("Test description")
                .signalType("CLASS_MATCH")
                .filePath("src/main/java/com/example/MyClass.java")
                .strength(EvidenceStrength.STRONG)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(signal);

        System.out.println("Serialized JSON: " + json);

        assertTrue(json.contains("\"type\":\"CLASS_MATCH\""));
        assertFalse(json.contains("\"signalType\""));
        assertTrue(json.contains("\"filePath\":\"src/main/java/com/example/MyClass.java\""));
    }
}
