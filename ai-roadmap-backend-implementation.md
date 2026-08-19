# Deep Dive: Backend AI Roadmap Implementation

This document explains the overarching backend architecture, configuration, DTOs, and the entry point of the AI Roadmap feature. Read this to understand how the pieces are configured and how data enters the system.

## 1. The Entry Point: `AIController.java`

**Location:** `src/main/java/com/studentguide/platform/controller/AIController.java`

This is where the frontend's HTTP request physically hits your Spring Boot backend.

*   **`@RestController`**: Tells Spring this class handles web requests and automatically converts the returned Java objects into JSON for the frontend.
*   **`@RequestMapping("/api/projects")`**: Sets the base URL for all endpoints in this file.
*   **`@PostMapping("/{projectId}/generate-roadmap")`**: The specific endpoint. The `{projectId}` is extracted from the URL using the `@PathVariable` annotation.
*   **`Authentication authentication`**: Spring Security automatically injects the logged-in user's details into this parameter. The controller simply calls `authentication.getName()` (which returns the user's email) and passes it to the `AIIntegrationService`. 

**Why is it so small?** A Controller should *never* contain business logic. Its only job is to receive HTTP requests, extract parameters, call a Service, and return an HTTP response (e.g., `ResponseEntity.ok()`). 

## 2. Configuration & Network Resiliency: `AiServiceConfig.java`

**Location:** `src/main/java/com/studentguide/platform/config/AiServiceConfig.java`

This file is absolutely critical for the stability of the application. It configures the tool (`RestTemplate`) we use to talk to the Python AI service.

*   **`@Configuration`**: Tells Spring to run this class at startup to generate configurations.
*   **`@Value("${...}")`**: Pulls variables from `application.properties`. This allows us to change the AI Service URL or timeout settings without recompiling the Java code.
*   **`@Bean public RestTemplate restTemplate()`**: This method creates the network client.
*   **The Crucial "Why" - Timeouts:** We use `SimpleClientHttpRequestFactory` to explicitly set `connectTimeoutMs` (e.g., 5000ms/5s) and `readTimeoutMs` (e.g., 60000ms/60s). 
    *   If the Python server is completely down, the connection timeout fires in 5 seconds instead of hanging.
    *   If the Python server connects but Gemini is slow, the read timeout gives it 60 seconds to reply. If it takes longer, Spring cuts the connection. Without timeouts, threads would hang forever waiting for a response, eventually taking down the entire Spring application (Thread Starvation).

## 3. Data Transfer Objects (DTOs)

When communicating with external APIs (like the frontend or the FastAPI server), we never send our raw Database Entities (`@Entity`). We use DTOs.

### `AIRequest.java` (What we send)
**Location:** `src/main/java/com/studentguide/platform/dto/AIRequest.java`

*   **`@JsonProperty("snake_case")`**: The Python FastAPI server expects JSON fields in `snake_case` (e.g., `project_name`). Java uses `camelCase` (e.g., `projectName`). This annotation tells the Jackson JSON parser: "When converting this variable to JSON, name it `project_name`".
*   **`@JsonIgnore` on `projectId`**: We store the `projectId` internally for logging, but the FastAPI server doesn't need it. `@JsonIgnore` ensures this field is stripped out before the JSON is sent over the network, preventing validation errors on the Python side.

### `AIRoadmapResponse.java` (What we receive)
**Location:** `src/main/java/com/studentguide/platform/dto/ai/AIRoadmapResponse.java`

This class perfectly mirrors the JSON structure sent back by FastAPI. 
*   It contains Lists of other smaller DTOs (like `List<AIRoadmapPhase>` and `List<AIResource>`). 
*   Spring takes the massive JSON string returned by FastAPI and automatically parses it into this exact Java object hierarchy so our `RoadmapPersistenceService` can loop through it easily.

## 4. The Database Entities (`@Entity`)

Once the data is processed, it ends up here (in `src/main/java/com/studentguide/platform/entity/`):

1.  **`Roadmap.java`**: The root entity. Belongs to a `Project`. Holds `estimatedDurationWeeks` and `RoadmapStatus`.
2.  **`Milestone.java`**: Represents a major phase (e.g., "Week 1"). Holds a `title`, `description`, `sequenceOrder`, and has a Many-to-One relationship back to the `Roadmap`.
3.  **`Task.java`**: Granular action items (e.g., "Setup database"). Holds a `title`, `description`, and `status`. Has a Many-to-One relationship back to a `Milestone`.
4.  **`Resource.java`**: Links to learning materials. Holds a `url`, `title`, and `ResourceType` (Enum: ARTICLE, VIDEO, etc.). Has a Many-to-One relationship back to a `Task`.

## Summary of the Full Flow:
1. HTTP POST to `AIController` -> 
2. `AIIntegrationService` fetches DB data -> 
3. Builds `AIRequest` DTO -> 
4. `AiServiceConfig`'s `RestTemplate` sends request to Python server -> 
5. Receives JSON and maps to `AIRoadmapResponse` DTO -> 
6. `RoadmapPersistenceService` uses `@Transactional` to map DTOs to Database Entities and saves them.
