const API_URL = "https://visioncraft-f9fu.onrender.com/api/images";

const promptInput = document.getElementById("promptInput");
const generateBtn = document.getElementById("generateBtn");
const terminalOutput = document.getElementById("terminalOutput");
const resultSection = document.getElementById("resultSection");
const generatedImage = document.getElementById("generatedImage");
const imageId = document.getElementById("imageId");
const imagePrompt = document.getElementById("imagePrompt");
const imageModel = document.getElementById("imageModel");
const downloadBtn = document.getElementById("downloadBtn");
const gallery = document.getElementById("gallery");
const refreshBtn = document.getElementById("refreshBtn");

/* ==========================================
   TERMINAL MESSAGE
========================================== */

function terminalMessage(message, type = "system") {
    const line = document.createElement("div");
    const color =
        type === "error"
            ? "red"
            : type === "ai"
                ? "blue"
                : "green";

    line.innerHTML = `
        <span class="${color}">
            [${type.toUpperCase()}]
        </span>
        ${message}
    `;

    terminalOutput.appendChild(line);
    terminalOutput.scrollTop = terminalOutput.scrollHeight;
}

/* ==========================================
   GENERATE IMAGE
========================================== */

async function generateImage() {
    const prompt = promptInput.value.trim();

    if (!prompt) {
        terminalMessage("Please enter a prompt.", "error");
        promptInput.focus();
        return;
    }

    generateBtn.disabled = true;
    resultSection.classList.add("hidden");

    terminalMessage("Prompt received.");
    terminalMessage("Connecting to Hugging Face inference engine...");
    terminalMessage("Generating visual representation...", "ai");

    generateBtn.innerHTML = "⟳ GENERATING...";

    try {
        const response = await fetch(`${API_URL}/generate`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ prompt: prompt })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Image generation failed");
        }

        const image = await response.json();

        terminalMessage("Image generated successfully.", "ai");
        terminalMessage(`Image ID: ${image.id}`);

        displayResult(image);
        loadGallery();

    } catch (error) {
        console.error(error);
        terminalMessage("Generation failed: " + error.message, "error");
    } finally {
        generateBtn.disabled = false;
        generateBtn.innerHTML = '<span class="button-icon">▶</span> GENERATE_IMAGE';
    }
}

generateBtn.addEventListener("click", generateImage);

/* ==========================================
   DISPLAY RESULT
========================================== */

function displayResult(image) {
    resultSection.classList.remove("hidden");

    generatedImage.src = image.imageUrl;
    imageId.textContent = image.id;
    imagePrompt.textContent = image.prompt;
    imageModel.textContent = image.modelName || "VisionCraft AI";

    downloadBtn.href = image.imageUrl;
    downloadBtn.download = `visioncraft-${image.id}.png`;

    resultSection.scrollIntoView({
        behavior: "smooth"
    });
}

/* ==========================================
   LOAD HISTORY
========================================== */

async function loadGallery() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Could not load image history");
        }

        const images = await response.json();
        renderGallery(images);

    } catch (error) {
        console.error(error);
        gallery.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">[ ! ]</div>
                <p>FAILED TO LOAD HISTORY</p>
            </div>
        `;
    }
}

/* ==========================================
   RENDER GALLERY
========================================== */

function renderGallery(images) {
    if (!images || !images.length) {
        gallery.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">[ _ ]</div>
                <p>NO GENERATED IMAGES FOUND</p>
                <small>Your creations will appear here.</small>
            </div>
        `;
        return;
    }

    gallery.innerHTML = "";

    images.slice().reverse().forEach(image => {
        const card = document.createElement("div");
        card.className = "gallery-card";

        const imageUrl = `${API_URL}/${image.id}/file`;

        card.innerHTML = `
            <img
                src="${image.imageUrl}"
                alt="${escapeHtml(image.prompt)}"
                loading="lazy"
            >
            <div class="card-info">
                <div class="card-prompt">
                    ${escapeHtml(image.prompt)}
                </div>
                <div class="card-actions">
                    <a href="${imageUrl}" download="visioncraft-${image.id}.png">
                        ↓ DOWNLOAD
                    </a>
                    <button class="delete-btn" data-id="${image.id}">
                        × DELETE
                    </button>
                </div>
            </div>
        `;

        // Attach safe listener for delete button to fix scope issues
        const deleteBtn = card.querySelector(".delete-btn");
        deleteBtn.addEventListener("click", () => deleteImage(image.id));

        gallery.appendChild(card);
    });
}

/* ==========================================
   DELETE IMAGE
========================================== */

async function deleteImage(id) {
    const confirmed = confirm("Delete this generated image?");
    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Delete failed");
        }

        terminalMessage(`Image ${id} deleted.`);
        loadGallery();

        if (imageId.textContent == String(id)) {
            resultSection.classList.add("hidden");
        }

    } catch (error) {
        terminalMessage(error.message, "error");
    }
}

// Expose deleteImage globally just in case inline fallback is ever needed
window.deleteImage = deleteImage;

/* ==========================================
   ENTER KEY
========================================== */

promptInput.addEventListener("keydown", function(event) {
    if (event.key === "Enter" && !event.shiftKey) {
        event.preventDefault();
        generateImage();
    }
});

/* ==========================================
   REFRESH
========================================== */

refreshBtn.addEventListener("click", loadGallery);

/* ==========================================
   HTML ESCAPE
========================================== */

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value;
    return div.innerHTML;
}

/* ==========================================
   INITIAL LOAD
========================================== */

loadGallery();