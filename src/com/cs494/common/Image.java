package com.cs494.common;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

/**
 * Created by giangle on 12/14/16.
 */
public class Image {

    private String author;
    private String name;
    private String contentType;
    private String category;
    private int byteSize;
    private BufferedImage imageContent;


    public Image(int byteSize, String author, String name, String contentType, String category, BufferedImage imageContent) {
        this.author = author;
        this.name = name;
        this.contentType = contentType;
        this.byteSize = byteSize;

        this.category = category;
        this.imageContent = imageContent;
    }



    public int getHeight(){
        return this.imageContent.getHeight();
    }
    public int getWidth(){return this.imageContent.getWidth();}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BufferedImage getImageContent() {
        return imageContent;
    }

    public void setImageContent(BufferedImage imageContent) {
        this.imageContent = imageContent;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getByteSize() {
        return byteSize;
    }

    public void setByteSize(int byteSize) {
        this.byteSize = byteSize;
    }
}
