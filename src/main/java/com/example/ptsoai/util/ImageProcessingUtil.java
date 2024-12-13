package com.example.ptsoai.util;

import org.springframework.web.multipart.MultipartFile;

public class ImageProcessingUtil {

    /**
     * Extracts a description or text from the uploaded image.
     * For now, this is a placeholder that just returns a dummy description.
     * In a real implementation, you could use OCR or image classification.
     */
    public static String extractDescription(MultipartFile file) {
        // Placeholder: Replace this with actual image processing logic
        if (file == null || file.isEmpty()) {
            return "No image provided.";
        }

        // Example logic: Use the file name as the description for now
        return "Description based on image: " + file.getOriginalFilename();
    }
}
