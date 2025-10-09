package com.codelogium.ticketing.service;

import org.springframework.stereotype.Service;

/**
 * Interface for handling external file storage (e.g., S3, Google Cloud Storage).
 */
public interface FileStorageService {
    String uploadImageFromBase64(String base64Data);
}
