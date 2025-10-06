package com.codelogium.ticketing.service;

import org.springframework.stereotype.Service;

/**
 * Interface for handling external file storage (e.g., S3, Google Cloud Storage).
 */
public interface FileStorageService {
    /**
     * Uploads file content provided as a Base64 encoded string.
     * @param base64Data The Base64 string containing the file data (e.g., "data:image/png;base64,...").
     * @return The public URL of the uploaded image.
     */
    String uploadImageFromBase64(String base64Data);
}
