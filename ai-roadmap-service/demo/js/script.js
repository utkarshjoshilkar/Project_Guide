const API_URL = "http://127.0.0.1:8000/latest-roadmap";

const roadmapContent = document.getElementById("roadmapContent");
const loadButton = document.getElementById("loadRoadmapBtn");

loadButton.addEventListener("click", loadRoadmap);

async function loadRoadmap() {

    roadmapContent.innerHTML = `
        <div class="empty-state">

            <h2>⏳ Loading Roadmap...</h2>

            <p>Please wait...</p>

        </div>
    `;

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {

            throw new Error("No roadmap available.");

        }

        const roadmap = await response.json();

        console.log(roadmap);

        renderRoadmap(roadmap);

    }

    catch (error) {

        console.error(error);

        roadmapContent.innerHTML = `

            <div class="empty-state">

                <h2>❌ Error</h2>

                <p>${error}</p>

            </div>

        `;

    }

}

function renderRoadmap(roadmap) {

    roadmapContent.innerHTML = "";

    document.getElementById("projectTitle").innerHTML = `

        ${roadmap.project_summary.project_name}

        <br>

        <small>

            ${roadmap.project_summary.duration}
             
            ${roadmap.project_summary.weekly_effort}

        </small>

    `;

    renderProjectSummary(roadmap.project_summary); 

    renderPrerequisites(roadmap.prerequisites);

    renderLearningPhases(roadmap.phase_wise_learning_plan);

    renderTechnologies(roadmap.technologies_to_learn);

    renderResources(roadmap.learning_resources);

    renderMiniProjects(roadmap.mini_projects);

    renderMilestones(roadmap.milestones);

    renderCourses(roadmap.recommended_courses);

    renderCertifications(roadmap.recommended_certifications);

    renderFutureEnhancements(roadmap.future_enhancements);

    renderFinalOutcome(roadmap.final_expected_outcome);

}
function renderProjectSummary(summary) {

    roadmapContent.innerHTML += `

        <div class="card">

            <h2>📋 Project Summary</h2>

            <p><strong>Project:</strong> ${summary.project_name}</p>

            <br>

            <p>${summary.description}</p>

            <br>

            <p><strong>Duration:</strong> ${summary.duration}</p>

            <p><strong>Weekly Effort:</strong> ${summary.weekly_effort}</p>

        </div>

    `;

}
function renderPrerequisites(prerequisites) {

    let html = `
        <div class="card">

            <h2>📚 Prerequisites</h2>

            <ul>
    `;

    prerequisites.forEach(item => {

        html += `
            <li>

                <strong>${item.topic}</strong>

                <br>

                ${item.concepts}

            </li>

            <br>
        `;

    });

    html += `
            </ul>

        </div>
    `;

    roadmapContent.innerHTML += html;

}
function renderLearningPhases(phases) {

    let html = `

        <div class="card">

            <h2>🗺️ Phase-wise Learning Plan</h2>

    `;

    phases.forEach(phase => {

        html += `

            <div class="phase">

                <h3>${phase.phase}</h3>

                <p>

                    <strong>Timeline:</strong>

                    ${phase.timeline}

                </p>

                <p>

                    <strong>Weekly Allocation:</strong>

                    ${phase.weekly_allocation}

                </p>

                <br>

                <strong>Objectives</strong>

                <ul>

                    ${phase.objectives.map(item => `<li>${item}</li>`).join("")}

                </ul>

                <br>

                <strong>Topics To Cover</strong>

                <ul>

                    ${phase.topics_to_cover.map(item => `<li>${item}</li>`).join("")}

                </ul>

                <br>

                <strong>Action Items</strong>

                <ul>

                    ${phase.action_items.map(item => `<li>${item}</li>`).join("")}

                </ul>

            </div>

        `;

    });

    html += `

        </div>

    `;

    roadmapContent.innerHTML += html;

}
function renderTechnologies(technologies) {

    let html = `

        <div class="card">

            <h2>🛠 Technologies To Learn</h2>

    `;

    technologies.forEach(tech => {

        html += `

            <div class="phase">

                <h3>${tech.name}</h3>

                <p>

                    ${tech.purpose}

                </p>

            </div>

        `;

    });

    html += `

        </div>

    `;

    roadmapContent.innerHTML += html;

}
function renderResources(resources) {

    let html = `

        <div class="card">

            <h2>📚 Learning Resources</h2>

    `;

    resources.forEach(resource => {

        html += `

            <div class="phase">

                <h3>

                    ${resource.resource_name}

                </h3>

                <p>

                    <strong>Type:</strong>

                    ${resource.type}

                </p>

                <a

                    href="${resource.url}"

                    target="_blank"

                >

                    Open Resource →

                </a>

            </div>

        `;

    });

    html += `

        </div>

    `;

    roadmapContent.innerHTML += html;

}
function renderMiniProjects(projects) {

    let html = `

        <div class="card">

            <h2>💻 Mini Projects</h2>

    `;

    projects.forEach(project => {

        html += `

            <div class="phase">

                <h3>${project.name}</h3>

                <p>${project.description}</p>

                <p>

                    <strong>Estimated Hours:</strong>

                    ${project.estimated_hours} Hours

                </p>

            </div>

        `;

    });

    html += `

        </div>

    `;

    roadmapContent.innerHTML += html;

}
function renderMilestones(milestones) {

    let html = `

        <div class="card">

            <h2>🎯 Milestones</h2>

    `;

    milestones.forEach(milestone => {

        html += `

            <div class="phase">

                <h3>

                    ${milestone.target_week}

                </h3>

                <strong>

                    ${milestone.title}

                </strong>

                <br><br>

                <p>

                    ${milestone.description}

                </p>

            </div>

        `;

    });

    html += `

        </div>

    `;

    roadmapContent.innerHTML += html;

}
function renderCourses(courses) {

    let html = `

        <div class="card">

            <h2>🎓 Recommended Courses</h2>

    `;

    courses.forEach(course => {

        html += `

            <div class="phase">

                <h3>

                    ${course.course_name}

                </h3>

                <p>

                    <strong>Platform:</strong>

                    ${course.platform}

                </p>

                <p>

                    <strong>Price:</strong>

                    ${course.price}

                </p>

                <a

                    href="${course.link}"

                    target="_blank"

                >

                    Open Course →

                </a>

            </div>

        `;

    });

    html += `

        </div>

    `;

    roadmapContent.innerHTML += html;

}
function renderCertifications(certifications) {

    let html = `

    <div class="card">

        <h2>📜 Recommended Certifications</h2>

    `;

    certifications.forEach(certification => {

        html += `

        <div class="phase">

            <h3>${certification.name}</h3>

            <p>

                <strong>Issuer:</strong>

                ${certification.issuer}

            </p>

            <p>

                ${certification.benefits}

            </p>

        </div>

        `;

    });

    html += "</div>";

    roadmapContent.innerHTML += html;

}
function renderFutureEnhancements(enhancements) {

    let html = `

    <div class="card">

        <h2>🚀 Future Enhancements</h2>

    `;

    enhancements.forEach(item => {

        html += `

        <div class="phase">

            <h3>

                ${item.feature}

            </h3>

            <p>

                ${item.details}

            </p>

        </div>

        `;

    });

    html += "</div>";

    roadmapContent.innerHTML += html;

}
function renderFinalOutcome(outcome) {

    roadmapContent.innerHTML += `

    <div class="card">

        <h2>🏆 Final Expected Outcome</h2>

        <p>

            ${outcome}

        </p>

    </div>

    `;

}