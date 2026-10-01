package com.shikhar.VisionCraft.Service;

import com.shikhar.VisionCraft.Exception.ImageNotFoundException;
import com.shikhar.VisionCraft.Repository.GeneratedImageRepository;
import com.shikhar.VisionCraft.entity.GeneratedImage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;

@Service
public class ImageGenerationService {

    private final GeneratedImageRepository imageRepository;
    private final RestClient restClient;
    private final SupabaseStorageService storageService;


    @Value("${huggingface.api.url}")
    private String huggingFaceUrl;

    @Value("${huggingface.api.token}")
    private String huggingFaceToken;


    /*
     * OLD LOCAL STORAGE CONFIG
     *
     * No longer required because images are now
     * stored in Supabase Storage.
     *
     * application.properties:
     *
     * visioncraft.storage.path=generated-images
     *
     * @Value("${visioncraft.storage.path}")
     * private String storagePath;
     */


    public ImageGenerationService(
            GeneratedImageRepository imageRepository,
            RestClient.Builder restClientBuilder,
            SupabaseStorageService storageService
    ) {

        this.imageRepository = imageRepository;

        this.restClient =
                restClientBuilder.build();

        this.storageService =
                storageService;
    }


    // ============================================================
    // GENERATE IMAGE
    // ============================================================

    public GeneratedImage generateImage(String prompt)
            throws IOException {

        /*
         * 1. Generate image using Hugging Face
         */
        byte[] imageBytes =
                callHuggingFace(prompt);


        /*
         * ========================================================
         * OLD LOCAL FILE SAVING CODE
         * ========================================================
         *
         * Images were previously saved inside:
         *
         * generated-images/
         *
         * This is NOT used anymore because local filesystem
         * storage is not suitable for production hosting.
         *
         * Path directory = Paths.get(storagePath);
         *
         * if (!Files.exists(directory)) {
         *     Files.createDirectories(directory);
         * }
         *
         * String fileName =
         *         "image-" +
         *         System.currentTimeMillis() +
         *         ".png";
         *
         * Path imagePath =
         *         directory.resolve(fileName);
         *
         * Files.write(imagePath, imageBytes);
         *
         * ========================================================
         */


        /*
         * 2. Create unique filename
         */

        String fileName =
                "image-" +
                        System.currentTimeMillis() +
                        ".png";


        /*
         * 3. Upload image to Supabase Storage
         */

        String imageUrl =
                storageService.uploadImage(
                        fileName,
                        imageBytes
                );


        /*
         * 4. Save image metadata + Supabase URL
         *    into PostgreSQL
         */

        GeneratedImage generatedImage =
                GeneratedImage.builder()
                        .prompt(prompt)
                        .storagePath(fileName)
                        .imageUrl(imageUrl)
                        .modelName(huggingFaceUrl)
                        .build();


        /*
         * 5. Save database record
         */

        return imageRepository.save(
                generatedImage
        );
    }


    // ============================================================
    // GET ALL IMAGES
    // ============================================================

    public List<GeneratedImage> getAllImages() {

        return imageRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );
    }


    // ============================================================
    // GET IMAGE BY ID
    // ============================================================

    public GeneratedImage getImageById(Long id) {

        return imageRepository.findById(id)
                .orElseThrow(
                        () -> new ImageNotFoundException(id)
                );
    }


    /*
     * ============================================================
     * OLD GET IMAGE FILE METHOD
     * ============================================================
     *
     * This method was used when images were stored locally.
     *
     * It is no longer required because Supabase gives us
     * an image URL.
     *
     * Frontend can directly use:
     *
     * image.imageUrl
     *
     *
     * public byte[] getImageFile(Long id)
     *         throws IOException {
     *
     *     GeneratedImage image =
     *             getImageById(id);
     *
     *     Path path =
     *             Paths.get(image.getImagePath());
     *
     *     if (!Files.exists(path)) {
     *         throw new IOException(
     *                 "Image file does not exist: " + path
     *         );
     *     }
     *
     *     return Files.readAllBytes(path);
     * }
     *
     * ============================================================
     */


    // ============================================================
    // DELETE IMAGE
    // ============================================================

    public void deleteImage(Long id) {

        /*
         * 1. Find database record
         */

        GeneratedImage image =
                imageRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ImageNotFoundException(id)
                        );


        /*
         * ========================================================
         * OLD LOCAL FILE DELETE CODE
         * ========================================================
         *
         * This was used when images were stored locally.
         *
         * Path path =
         *         Paths.get(image.getImagePath());
         *
         * if (Files.exists(path)) {
         *     Files.delete(path);
         * }
         *
         * ========================================================
         */


        /*
         * 2. Get Supabase image URL
         */

        String imageUrl =
                image.getImageUrl();


        /*
         * 3. Extract filename from URL
         *
         * Example:
         *
         * https://xxxxx.supabase.co/storage/v1/object/
         * public/generated-images/image-123.png
         *
         * becomes:
         *
         * image-123.png
         */

        String fileName =
                imageUrl.substring(
                        imageUrl.lastIndexOf("/") + 1
                );


        /*
         * 4. Delete actual image from Supabase Storage
         */

        storageService.deleteImage(
                image.getStoragePath()
        );
        imageRepository.delete(image);

        /*
         * 5. Delete database record
         */

    }


    // ============================================================
    // HUGGING FACE API
    // ============================================================

    private byte[] callHuggingFace(
            String prompt
    ) {

        return restClient.post()
                .uri(huggingFaceUrl)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + huggingFaceToken
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .body("""
                        {
                            "inputs": "%s"
                        }
                        """.formatted(
                        escapeJson(prompt)
                ))
                .retrieve()
                .body(byte[].class);
    }


    // ============================================================
    // ESCAPE JSON
    // ============================================================

    private String escapeJson(
            String value
    ) {

        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                );
    }
}
