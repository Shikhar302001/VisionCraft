package com.shikhar.VisionCraft.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SupabaseStorageService {

    private final RestClient restClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String serviceKey;

    @Value("${supabase.storage.bucket}")
    private String bucket;


    /*
     * ============================================================
     * OLD LOCAL STORAGE
     * ============================================================
     *
     * Previously images were saved using:
     *
     * Files.write(...)
     *
     * inside:
     *
     * generated-images/
     *
     * This is no longer used.
     *
     * Images are now stored in:
     *
     * Supabase Storage
     *
     * ============================================================
     */


    public SupabaseStorageService(
            RestClient.Builder restClientBuilder
    ) {

        this.restClient =
                restClientBuilder.build();
    }


    // ============================================================
    // UPLOAD IMAGE TO SUPABASE STORAGE
    // ============================================================

    public String uploadImage(
            String fileName,
            byte[] imageBytes
    ) {

        /*
         * Supabase Storage upload endpoint
         *
         * /storage/v1/object/{bucket}/{fileName}
         */

        String uploadUrl =
                supabaseUrl
                        + "/storage/v1/object/"
                        + bucket
                        + "/"
                        + fileName;


        /*
         * Upload image
         */

        restClient.post()
                .uri(uploadUrl)

                /*
                 * Supabase authentication
                 */

                .header(
                        "Authorization",
                        "Bearer " + serviceKey
                )

                .header(
                        "apikey",
                        serviceKey
                )

                /*
                 * Image content type
                 */

                .contentType(
                        MediaType.IMAGE_PNG
                )

                /*
                 * Actual image bytes
                 */

                .body(imageBytes)

                /*
                 * Execute request
                 */

                .retrieve()

                .toBodilessEntity();


        /*
         * Return PUBLIC image URL
         *
         * This URL will be stored in PostgreSQL.
         */

        return supabaseUrl
                + "/storage/v1/object/public/"
                + bucket
                + "/"
                + fileName;
    }


    // ============================================================
    // DELETE IMAGE FROM SUPABASE STORAGE
    // ============================================================

    public void deleteImage(
            String fileName
    ) {

        /*
         * Supabase Storage delete endpoint
         */

        String deleteUrl =
                supabaseUrl
                        + "/storage/v1/object/"
                        + bucket
                        + "/"
                        + fileName;


        /*
         * Delete image
         */

        restClient.delete()
                .uri(deleteUrl)

                /*
                 * Supabase authentication
                 */

                .header(
                        "Authorization",
                        "Bearer " + serviceKey
                )

                .header(
                        "apikey",
                        serviceKey
                )

                /*
                 * Execute request
                 */

                .retrieve()

                .toBodilessEntity();
    }
}
