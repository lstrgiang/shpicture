package com.cs494.common;

import com.cs494.server.DatabaseService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giangle on 12/14/16.
 */
public class ImageService {

    public static void saveImage(Image image,String directory) throws IOException {
        String path = CommandService.currentPath()+directory;
        createDirIfNotExist(path);
        ImageIO.write(image.getImageContent(), image.getContentType(), new File(path+"/"+image.getName()));
    }
    private static void createDirIfNotExist(String directory){
        File file = new File(directory);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static ByteArrayOutputStream convertToArrayOutputStream(Image image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image.getImageContent(), image.getContentType(), byteArrayOutputStream);
        return byteArrayOutputStream;
    }
    public static ByteArrayOutputStream convertToArrayOutputStream(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image,type, byteArrayOutputStream);
        return byteArrayOutputStream;
    }
    public static void saveMultipleImage(List<Image> images, String directory) throws IOException{
        for(Image image : images){
            saveImage(image,directory);
        }
    }
    public static Map<String,BufferedImage> readImageFromUserDir(String username)throws IOException {
        String path = CommandService.currentPath()+DatabaseService.IMAGE_DATA_DIR+"/"+username;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        Map<String,BufferedImage> imageBuffer = new HashMap<>();
        if(listOfFiles == null || listOfFiles.length <= 0){
            throw new IOException(ErrorMessage.NO_IMAGE_FOUND);
        }
        for(File file : listOfFiles){
            BufferedImage image = ImageIO.read(file);
            imageBuffer.put(file.getName(),image);
        }
        return imageBuffer;
    }
    public static List<Image> readImageFromUser(String username) throws IOException {
        Map<String,BufferedImage> imageBuffer = readImageFromUserDir(username);
        ResultSet imageInfo = DatabaseService.retrieveImagesInfoByUser(username);
        try {
            if (!imageInfo.isBeforeFirst() || imageInfo == null) {
                if(DatabaseService.isUserExisted(username)){
                    throw new IOException(ErrorMessage.NO_IMAGE_FOUND);
                }
                throw new IOException(ErrorMessage.INVALID_USERNAME);
            }
            return combineImageInfo(imageInfo, imageBuffer, username);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(ErrorMessage.INVALID_USERNAME);
        }
    }
    private static List<Image> combineImageInfo(ResultSet imageInfo, Map<String,BufferedImage> imageBuffer,String username) throws SQLException {
        List<Image> imageList = new ArrayList<>();

        while(imageInfo.next()){
            int byteSize = imageInfo.getInt("byte_size");
            String imageName = imageInfo.getString("name");
            String category = imageInfo.getString("category");
            String contentType = imageName.substring(imageName.lastIndexOf(".")+1);

            if(imageBuffer.containsKey(imageName)) {
                Image image = new Image(byteSize, username, imageName, contentType, category, imageBuffer.get(imageName));
                imageList.add(image);
            }
        }
        return imageList;
    }
    //String.valueOf(byteArrayOutputStream.size()

}
