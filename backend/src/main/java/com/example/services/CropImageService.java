package com.example.services;

import com.example.dto.CropRequest;
import com.example.model.ImageState;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

@Service
public class CropImageService {
    public ResponseEntity<String> crop(CropRequest crop, ImageState state) {
        BufferedImage img = state.getCurrentImage();
        if (img == null) return ResponseEntity.badRequest().body("No image loaded.");

        state.pushHistory(cloneImage(img));

        int x = Math.max(0, crop.getX());
        int y = Math.max(0, crop.getY());
        int width = Math.min(img.getWidth() - x, crop.getWidth());
        int height = Math.min(img.getHeight() - y, crop.getHeight());

        BufferedImage cropped = img.getSubimage(x, y, width, height);
        state.setCurrentImage(cropped);

        return ResponseEntity.ok("Image cropped.");
    }

    private BufferedImage cloneImage(BufferedImage img) {
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return copy;
    }
}