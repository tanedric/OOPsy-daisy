package com.example.model;

import java.awt.image.BufferedImage;
import java.util.Stack;

public class ImageState {

    private BufferedImage currentImage;
    private BufferedImage originalImage;
    private final Stack<BufferedImage> history = new Stack<>();

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(BufferedImage image) {
        this.currentImage = image;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage image) {
        this.originalImage = cloneImage(image);
        clearHistory();
    }

    public void pushHistory(BufferedImage image) {
        if (image != null) {
            history.push(cloneImage(image));
        }
    }

    public BufferedImage popHistory() {
        return !history.isEmpty() ? history.pop() : null;
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }

    public void clearHistory() {
        history.clear();
    }

    public BufferedImage cloneImage(BufferedImage img) {
        if (img == null)
            return null;
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        copy.getGraphics().drawImage(img, 0, 0, null);
        return copy;
    }
}
