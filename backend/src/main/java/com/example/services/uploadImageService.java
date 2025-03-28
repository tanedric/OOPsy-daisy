package com.example.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadImageService {
    public void saveImage(MultipartFile file) throws IOException {
        InputStream imageInputStream = file.getInputStream();
        String destinationPath = "uploaded_images/" + file.getOriginalFilename();  // Save to a folder "uploaded_images"

        File destinationFile = new File(destinationPath);
        if (!destinationFile.exists()) {
            destinationFile.getParentFile().mkdirs(); // Create directories if they don't exist
            destinationFile.createNewFile();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = imageInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Image saved successfully at: " + destinationPath);
        } catch (IOException e) {
            System.out.println("Error saving the image: " + e.getMessage());
            throw e;  // Throw exception so it can be handled in controller
        } finally {
            imageInputStream.close();
        }
    }
    
} 