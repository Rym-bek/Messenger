package com.example.telegram.models;

public class Slide {
    int id;
    String slide_image, slide_main, slide_description;

    public Slide(int id, String slide_image, String slide_main, String slide_description) {
        this.id = id;
        this.slide_image = slide_image;
        this.slide_main = slide_main;
        this.slide_description = slide_description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlide_image() {
        return slide_image;
    }

    public void setSlide_image(String slide_image) {
        this.slide_image = slide_image;
    }

    public String getSlide_main() {
        return slide_main;
    }

    public void setSlide_main(String slide_main) {
        this.slide_main = slide_main;
    }

    public String getSlide_description() {
        return slide_description;
    }

    public void setSlide_description(String slide_description) {
        this.slide_description = slide_description;
    }
}
