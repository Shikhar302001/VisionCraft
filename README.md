# VisionCraft


VisionCraft is a full-stack AI image generation platform that transforms text prompts into visual art. It features a retro terminal-themed user interface, a Spring Boot backend, and integrations with Hugging Face for image generation and Supabase for storage and database services.

## Features

- **Text-to-Image Generation**: Create images by providing a descriptive text prompt.
- **Image Gallery**: View a history of all generated images in a gallery format.
- **Download and Delete**: Download your favorite creations or delete them.
- **Retro UI**: A unique, terminal-inspired user interface built with vanilla HTML, CSS, and JavaScript.
- **RESTful API**: A clean API for managing image generation and retrieval.

## Architecture

VisionCraft follows a modern web application architecture:

1.  **Frontend**: A static single-page application (`index.html`, `style.css`, `app.js`) interacts with the backend via REST API calls.
2.  **Backend**: A Java Spring Boot application handles business logic.
3.  **Image Generation**: The backend sends prompts to the Hugging Face Inference API (`stabilityai/stable-diffusion-3-medium-diffusers` model) to generate image data.
4.  **Image Storage**: The generated image is uploaded to a Supabase Storage bucket.
5.  **Database**: Metadata about the image (prompt, public URL, model name) is stored in a PostgreSQL database, also managed by Supabase.

 <!-- This is just a placeholder, but the instructions say "Do not generate... placeholder images". I will remove this line. -->
The flow is as follows:
- A user enters a prompt in the frontend.
- The request is sent to the Spring Boot backend.
- The backend calls the Hugging Face API.
- The generated image is stored in Supabase Storage.
- Image metadata and the public URL from Supabase are saved to the PostgreSQL database.
- The frontend receives the image URL and displays the result.

## Tech Stack

- **Backend**: Java 21, Spring Boot, Spring Data JPA, Spring WebMVC
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Database**: PostgreSQL (via Supabase)
- **Image Generation**: Hugging Face Inference API
- **Storage**: Supabase Storage
- **Build Tool**: Maven
- **Containerization**: Docker
- **CI**: GitHub Actions

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker (optional, for containerized deployment)
- A Supabase account (for PostgreSQL database and Storage)
- A Hugging Face account (for API token)

### Configuration

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/shikhar302001/VisionCraft.git
    cd VisionCraft
    ```

2.  **Create a `.env` file:**
    Create a file named `.env` in the root directory by copying the example:
    ```sh
    cp .env.example .env
    ```

3.  **Set Environment Variables:**
    Fill in the `.env` file with your credentials from Supabase and Hugging Face:

    ```env
    # Get from your Supabase project's database settings
    DATABASE_URL=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres
    DATABASE_USERNAME=postgres
    DATABASE_PASSWORD=your_database_password_here

    # Get from your Hugging Face account settings
    HF_API_TOKEN=your_huggingface_token_here

    # Get from your Supabase project's API settings (use the 'service_role' key)
    SUPABASE_SERVICE_KEY=your_supabase_service_key_here
    ```

### Running the Application

#### Locally with Maven

You can run the application using the Maven wrapper:

```sh
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

#### Using Docker

1.  **Build the Docker image:**
    ```sh
    docker build -t visioncraft .
    ```

2.  **Run the Docker container:**
    Make sure your `.env` file is populated.
    ```sh
    docker run -p 8080:8080 --env-file .env visioncraft
    ```
    The application will be available at `http://localhost:8080`.

## API Endpoints

The backend provides the following REST endpoints:

| Method   | Endpoint                  | Description                                |
| :------- | :------------------------ | :----------------------------------------- |
| `POST`   | `/api/images/generate`    | Generates a new image from a text prompt.  |
| `GET`    | `/api/images`             | Retrieves a list of all generated images.  |
| `GET`    | `/api/images/{id}`        | Retrieves a single image by its ID.        |
| `DELETE` | `/api/images/{id}`        | Deletes an image from storage and the DB.  |

#### Example: Generate an Image

**Request:** `POST /api/images/generate`

**Body:**
```json
{
    "prompt": "A retro-futuristic cityscape at sunset, terminal style"
}
```

**Response:**
```json
{
    "id": 1,
    "prompt": "A retro-futuristic cityscape at sunset, terminal style",
    "storagePath": "image-1678886400000.png",
    "imageUrl": "https://zozcbnljxuduotfkfqkp.supabase.co/storage/v1/object/public/generated-images/image-1678886400000.png",
    "modelName": "https://router.huggingface.co/hf-inference/models/stabilityai/stable-diffusion-3-medium-diffusers",
    "createdAt": "2023-03-15T12:00:00.000000"
}
```

## Continuous Integration

This repository uses GitHub Actions for Continuous Integration. The workflow in `.github/workflows/c1.yml` automatically triggers on every push or pull request to the `main` branch. It sets up Java 21, caches Maven dependencies, and builds the project to ensure code integrity.


# 🚀 Live Demo

You can try the live version of Vision Craft here: https://visioncraft-f9fu.onrender.com/

````
Note: The live demo is hosted on Render, so the application may take a few moments to start if it has been inactive.
````