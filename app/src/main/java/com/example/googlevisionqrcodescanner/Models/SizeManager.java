package com.example.googlevisionqrcodescanner.Models;

public class SizeManager {
    public float width;
    public float height;

    public SizeManager() {
    }

    public SizeManager(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
