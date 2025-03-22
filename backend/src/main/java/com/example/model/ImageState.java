package com.example.model;

import java.awt.image.BufferedImage;
import java.util.Stack;

public class ImageState {
    private BufferedImage currentImage;
    private final Stack<BufferedImage> history = new Stack<>();

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(BufferedImage image) {
        this.currentImage = image;
    }

    public void pushHistory(BufferedImage image) {
        history.push(image);
    }

    public BufferedImage popHistory() {
        return history.pop();
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }

    public void clearHistory() {
        history.clear();
    }
}
