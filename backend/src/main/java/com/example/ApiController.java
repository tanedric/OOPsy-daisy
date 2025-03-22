package com.example;

import java.io.InputStream;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.example.services.UploadImageService;
import com.example.dto.CropRequest;
import com.example.model.ImageState;
import com.example.services.CropImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")  // Allow Vite frontend
// Enables CORS to allow frontend (Vite at localhost:5173) to call backend APIs
// Without this, browser blocks requests due to cross-origin restrictions
// Best practice: Use a global CORS config instead of adding @CrossOrigin in every controller

public class ApiController {

    private final ImageState state = new ImageState();

    @Autowired
    private UploadImageService uploadImageService;

    @Autowired
    private CropImageService cropImageService;

    @GetMapping("/message")
    public String getMessage() {
        return "Hello from Spring Boot!";
    }
    @PostMapping("/upload")
    public String uploadImage(MultipartFile file) {
        try {
            // Call from service file
            uploadImageService.saveImage(file, state);
            return "Image uploaded successfully!";
        } catch (Exception e) {
            return "Error uploading image: " + e.getMessage();
        }
    }

    @PostMapping("/crop")
    public ResponseEntity<String> crop(@RequestBody CropRequest crop) {
        return cropImageService.crop(crop, state);
    }

    @GetMapping("/image/get")
public ResponseEntity<byte[]> getImage() throws IOException {
    BufferedImage img = state.getCurrentImage();
    if (img == null) return ResponseEntity.notFound().build();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(img, "png", baos);

    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(baos.toByteArray());
}

}