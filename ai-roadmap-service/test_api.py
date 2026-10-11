from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

source_code = """
package com.example.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    public void createProject() {
        validateProject();
        saveProject();
    }

    private void validateProject() {
    }

    private void saveProject() {
    }
}
"""

response = client.post(
    "/api/code-analysis/java",
    json={
        "filePath": "src/main/java/com/example/service/ProjectService.java",
        "sourceCode": source_code
    }
)

print(response.status_code)
print(response.json())
