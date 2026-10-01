package com.shikhar.VisionCraft.Controller;

import com.shikhar.VisionCraft.Service.ImageGenerationService;
import com.shikhar.VisionCraft.dto.ImageGenerationRequest;
import com.shikhar.VisionCraft.entity.GeneratedImage;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageGenerationService imageGenerationService;


    public ImageController(
            ImageGenerationService imageGenerationService
    ) {
        this.imageGenerationService =
                imageGenerationService;
    }


    // ============================================================
    // GENERATE IMAGE
    // ============================================================

    @PostMapping("/generate")
    public ResponseEntity<GeneratedImage> generateImage(
            @Valid @RequestBody ImageGenerationRequest request
    ) throws IOException {

        GeneratedImage generatedImage =
                imageGenerationService.generateImage(
                        request.prompt()
                );

        return ResponseEntity.ok(
                generatedImage
        );
    }


    // ============================================================
    // GET ALL IMAGES
    // ============================================================

    @GetMapping
    public ResponseEntity<List<GeneratedImage>> getAllImages() {

        return ResponseEntity.ok(
                imageGenerationService.getAllImages()
        );
    }


    // ============================================================
    // GET IMAGE BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<GeneratedImage> getImage(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                imageGenerationService
                        .getImageById(id)
        );
    }


    // ============================================================
    // DELETE IMAGE
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long id
    ) {

        imageGenerationService.deleteImage(id);

        return ResponseEntity.noContent()
                .build();
    }
}
