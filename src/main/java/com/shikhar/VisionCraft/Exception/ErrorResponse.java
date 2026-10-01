package com.shikhar.VisionCraft.Exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp
) {
}