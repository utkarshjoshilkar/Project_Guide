import requests
import json
import time

BASE_URL = "http://localhost:8080/api"
EMAIL = "testuser16@example.com"

# 1. Register
print("Registering user...")
res = requests.post(f"{BASE_URL}/auth/register", json={
    "username": "testuser12",
    "email": EMAIL,
    "password": "Password123!",
    "fullName": "Test User"
})
if res.status_code == 201 or res.status_code == 409:
    print("Registration OK or user exists", res.status_code, res.text)
else:
    print("Failed to register:", res.text)
    exit(1)

# 2. Login
print("Logging in...")
res = requests.post(f"{BASE_URL}/auth/login", json={
    "email": EMAIL,
    "password": "Password123!"
})
if res.status_code != 200:
    print("Login failed:", res.text)
    exit(1)
token = res.json()["token"]
headers = {"Authorization": f"Bearer {token}"}

# 2.5 Create Student Profile
print("Creating student profile...")
res = requests.post(f"{BASE_URL}/student-profile", headers=headers, json={
    "college": "Test College",
    "branch": "Computer Science",
    "year": 3,
    "cgpa": 9.5,
    "learningGoal": "Learn Spring Boot"
})
if res.status_code != 201:
    print("Failed to create student profile:", res.text)
    if "already exists" not in res.text:
        exit(1)

# 3. Create Project
print("Creating project...")
res = requests.post(f"{BASE_URL}/projects", headers=headers, json={
    "title": "Test Project",
    "description": "A test project",
    "domain": "Web",
    "preferredTechStack": "Java, Spring Boot",
    "skillLevel": "BEGINNER",
    "deadline": "2026-12-31",
    "expectedOutcome": "Learning"
})
if res.status_code != 201:
    if "already exists" in res.text or res.status_code == 400:
        print("Project might already exist. Fetching existing projects...")
        res = requests.get(f"{BASE_URL}/projects", headers=headers)
        if res.status_code == 200 and len(res.json()) > 0:
            project_id = res.json()[0]["id"]
        else:
            print("Failed to fetch project:", res.text)
            exit(1)
    else:
        print("Failed to create project:", res.text)
        exit(1)
else:
    project_id = res.json()["id"]

print("Project ID:", project_id)

# 3.5 Generate Roadmap
print("Generating roadmap...")
res = requests.post(f"{BASE_URL}/projects/{project_id}/generate-roadmap", headers=headers)
if res.status_code != 200:
    print("Failed to generate roadmap:", res.text)
    if "already exists" not in res.text:
        print("Warning: Failed to generate roadmap, but continuing...")
print("Roadmap generated.")

# Wait for roadmap generation if needed
time.sleep(2)

# 4. Connect GitHub
# Use a small public Java repo
repo_url = "https://github.com/octocat/Hello-World"
# Wait, octocat/Hello-World doesn't have java files. Let's use something small.
repo_url = "https://github.com/spring-guides/gs-rest-service"
print("Connecting GitHub repository...", repo_url)
res = requests.post(f"{BASE_URL}/projects/{project_id}/github", headers=headers, json={
    "repositoryUrl": repo_url
})
if res.status_code != 200:
    print("Failed to connect repo:", res.text)
    exit(1)
print("Connected repo:", res.json())

# 5. Acquire
print("Acquiring repository...")
res = requests.post(f"{BASE_URL}/projects/{project_id}/github/acquire", headers=headers)
if res.status_code != 200:
    print("Failed to acquire repo:", res.text)
    exit(1)
print("Acquired repo locally at:", res.text)

# 6. Scan
print("Scanning repository...")
res = requests.get(f"{BASE_URL}/projects/{project_id}/github/scan", headers=headers)
if res.status_code != 200:
    print("Failed to scan repo:", res.text)
    exit(1)
files_json = res.json()
print(f"Scanned {len(files_json)} files")
print(json.dumps(files_json[:5], indent=2))

# 7. Analyze
print("Analyzing repository...")
res = requests.get(f"{BASE_URL}/projects/{project_id}/github/analyze", headers=headers)
if res.status_code != 200:
    print("Failed to analyze repo:", res.text)
    exit(1)
artifacts = res.json()
print(f"Parsed {len(artifacts)} artifacts")
if len(artifacts) > 0:
    print("All artifacts:", json.dumps(artifacts, indent=2))

# 8. Verify
print("Verifying repository progress...")
res = requests.get(f"{BASE_URL}/projects/{project_id}/github/verify", headers=headers)
if res.status_code != 200:
    print("Failed to verify repo:", res.text)
    exit(1)
try:
    print("Verify report:", json.dumps(res.json(), indent=2))
except Exception as e:
    print("Verify report JSON decode error:", str(e))
    print("Raw text:", res.text)
