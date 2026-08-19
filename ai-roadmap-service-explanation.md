# Deep Dive: The AI Roadmap Service (FastAPI / Gemini)

First, a quick clarification on terminology! While you mentioned "Flask", this microservice is actually built using **FastAPI**, a modern, high-performance Python framework very similar to Flask but optimized for speed and automatic data validation.

This document breaks down exactly **how** this Python service receives requests from your Spring Boot backend, talks to the Google Gemini AI, and returns the roadmap.

## The Architecture (How the files connect)

The Python service is structured very similarly to Spring Boot, using a Controller -> Service -> Provider pattern.

### 1. The Entry Point: `RoadmapController.py`
**Location:** `ai-roadmap-service/app/controller/RoadmapController.py`

This is where the Spring Boot backend sends its HTTP POST request.
*   **`@router.post("/generate-roadmap", response_model=RoadmapResponse)`**: This defines the endpoint. 
*   **The Request:** It accepts a `RoadmapRequest` object (which is a Pydantic model in the `dto` folder). Pydantic automatically validates that the incoming JSON from Spring Boot has the correct fields (project_name, current_skills, etc.).
*   **What it does:** It immediately passes the request to the `RoadmapService`.

### 2. The Orchestrator: `RoadmapService.py`
**Location:** `ai-roadmap-service/app/service/RoadmapService.py`

This file coordinates the actual generation process.
*   **`self.prompt_builder.build_prompt(request)`**: First, it tells the `PromptBuilder` to turn the raw data into a text prompt.
*   **`self.llm_service.generate_response(prompt)`**: Next, it sends that text prompt to the LLM (Large Language Model) service.
*   **`RoadmapStorage.latest_roadmap = roadmap`**: Before returning, it saves the generated roadmap in a temporary in-memory store so the frontend or backend can fetch the "latest" roadmap quickly without regenerating it.

### 3. Crafting the Prompt: `PromptBuilder.py`
**Location:** `ai-roadmap-service/app/ai/prompts/PromptBuilder.py`

This is where the magic happens. AI models need clear instructions, and this class builds them.
*   **Loading the Template:** When initialized, it reads a text file (`roadmap_prompt.txt`) which contains the base instructions for the AI (e.g., "You are an expert tutor...").
*   **String Replacement:** It loops through a dictionary of `placeholders` (like `{project_name}`) and replaces them with the actual data from the user's request.
*   **Enforcing JSON Output:** Crucially, it calls `SchemaGenerator` to get the exact JSON structure we expect (matching our Pydantic `RoadmapResponse`). It appends this schema to the bottom of the prompt with `Output JSON Structure:{schema}`. This forces Gemini to reply in a strict JSON format instead of plain text, which is vital so our Spring Boot backend can parse it later!

### 4. Calling Google Gemini: `LLMService.py` & `GeminiProvider.py`
**Location:** `ai-roadmap-service/app/ai/LLMService.py` and `ai-roadmap-service/app/ai/providers/GeminiProvider.py`

*   **`GeminiProvider.py`**: This is the lowest level. It uses the official Google `genai` Python SDK.
    *   It pulls the `GEMINI_API_KEY` from your environment variables (`settings.py` / `.env`).
    *   It uses the `gemini-3.6-flash` model, which is optimized for fast, accurate text/JSON generation.
    *   **Error Handling:** It wraps the API call in a `try/except` block. If Gemini is down or the API key is invalid, it throws a custom `AIException`, which FastAPI will automatically convert into a 500 error for Spring Boot to handle.
*   **`LLMService.py`**: This bridges the provider and the parser. It takes the raw string output from `GeminiProvider` and passes it to the `ResponseParser`.
    *   **`ResponseParser.py`**: Because Gemini sometimes wraps its JSON output in Markdown blocks (like ```json ... ```), the parser strips those out and converts the raw string into the Python `RoadmapResponse` object.

## Summary of the Full Flow

1.  **Spring Boot** sends a JSON POST request to `FastAPI` (`RoadmapController`).
2.  `FastAPI` validates the JSON into a `RoadmapRequest`.
3.  `RoadmapService` passes the data to `PromptBuilder`.
4.  `PromptBuilder` reads the template, fills in the blanks, and attaches the strict JSON schema.
5.  `GeminiProvider` sends the massive text prompt to Google's servers.
6.  Google replies with a JSON string.
7.  `ResponseParser` cleans the string and converts it to a `RoadmapResponse` object.
8.  `FastAPI` automatically turns that object back into HTTP JSON and sends it back to **Spring Boot**.

This architecture cleanly separates the web layer (FastAPI), the business logic (Building Prompts), and the external API (Gemini Provider), making it very easy to swap out Gemini for another AI in the future if needed!
