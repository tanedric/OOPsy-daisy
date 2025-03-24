package com.example;

import com.example.dto.CropRequest;
import com.example.model.ImageState;
import com.example.services.CropImageService;
import com.example.services.UploadImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ApiController {

    private final ImageState state = new ImageState();

    @Autowired
    private UploadImageService uploadImageService;

    @Autowired
    private CropImageService cropImageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            BufferedImage uploadedImage = ImageIO.read(file.getInputStream());
            state.setOriginalImage(uploadedImage);
            state.setCurrentImage(state.cloneImage(uploadedImage));
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading image: " + e.getMessage());
        }
    }

    @PostMapping("/crop")
    public ResponseEntity<?> crop(@RequestBody CropRequest cropRequest) {
        return cropImageService.crop(cropRequest, state);
    }

    @GetMapping("/image/get")
    public ResponseEntity<?> getCurrentImage() throws IOException {
        BufferedImage img = state.getCurrentImage();
        if (img == null)
            return ResponseEntity.notFound().build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(baos.toByteArray());
    }

    @GetMapping("/undo")
    public ResponseEntity<?> undo() {
        if (!state.hasHistory()) {
            return ResponseEntity.badRequest().body("No history available");
        }

        BufferedImage previous = state.popHistory();
        state.setCurrentImage(previous);
        return ResponseEntity.ok("Undo successful");
    }

    @GetMapping("/redo")
    public ResponseEntity<?> redo() {
        BufferedImage originalImage = state.getOriginalImage();
        if (originalImage == null) {
            return ResponseEntity.badRequest().body("No original image available");
        }

        state.pushHistory(state.getCurrentImage());
        state.setCurrentImage(state.cloneImage(originalImage));
        return ResponseEntity.ok("Redo successful - restored to original image");
    }

    @GetMapping("/history/status")
    public ResponseEntity<Map<String, Boolean>> getHistoryStatus() {
        return ResponseEntity.ok(Map.of(
                "hasHistory", state.hasHistory(),
                "hasOriginal", state.getOriginalImage() != null));
    }
}
