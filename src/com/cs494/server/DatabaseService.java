package com.cs494.server;



import com.cs494.common.ErrorMessage;
import com.cs494.common.Image;
import com.cs494.common.ImageService;
import com.cs494.common.UserInfo;
import com.cs494.server.service.ImageDataService;
import com.cs494.server.service.UserDataService;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by giangle on 12/9/16.
 */
public class DatabaseService {
    private static Connection connection;



    public static Connection getConnection(){return connection;}
    public static String IMAGE_DATA_DIR = "/images";
    public static void start(){
        try {
            Class.forName("org.sqlite.JDBC");
            String dbURL = "jdbc:sqlite:shpicture.db";
             connection = DriverManager.getConnection(dbURL);
            if (connection != null) {
                System.out.println("Connected to the database");
                DatabaseMetaData dm = connection.getMetaData();

            }
        } catch (ClassNotFoundException e) {
            System.out.println("Please download sqlite jar library and insert to the project");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean authenticate(String username, String password){
        try {
            return UserDataService.isUserExist(username,password,connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static int register(String username, String password){
        try {
            ResultSet resultSet = UserDataService.findByUsername(username,connection);
            if(resultSet.isBeforeFirst()){
                return -1;
            }
            UserDataService.save(username,password,connection);
            return 0;
        }  catch (SQLException ex) {
            return 1;
        }
    }
    public static void storeImagesForUser(List<Image> images,String authorName) throws IOException{
        try {
            ImageService.saveMultipleImage(images, IMAGE_DATA_DIR + "/" + authorName);
            int userId = UserDataService.findUserIdByUsername(authorName, connection);
            if(userId == -1){throw new IOException();}
            ImageDataService.saveImageList(images, userId, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ResultSet retrieveImagesInfoByUser(String username) {
        try{
            ResultSet result = ImageDataService.getImageInfoByUser(username,connection);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isUserExisted(String username){
        try {
            return UserDataService.isUserExist(username,connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean close(){
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static void updateUserStatus(boolean status,String username) throws IOException{
        try {
            UserDataService.updateUserStatus(status,username,connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(ErrorMessage.INVALID_USERNAME);
        }
    }
    public static String getUserStatusText(String username) throws IOException{
        try{
            return UserDataService.getUserStatusText(username,connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(ErrorMessage.INVALID_USERNAME);
        }
    }
    public static void updateUserStatusText(String status, String username) throws IOException {
        try{
            UserDataService.updateUserStatusText(status,username,connection);
        }catch (SQLException e){
            e.printStackTrace();
            throw new IOException(ErrorMessage.INVALID_USERNAME);
        }
    }
    public static List<UserInfo> retrieveUserInfoList(){
        try{
            List<UserInfo> users = new ArrayList<>();
            ResultSet results = UserDataService.getUserList(connection);
            while(results.next()){
                String username= results.getString(("username"));
                int status = results.getInt("status");
                String text = results.getString("status_text");
                users.add(new UserInfo(username,text,status));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Map<String,Map<String,Image>> searchImageByTheme(String theme){
        try {
            Map<String,Map<String,Image>> imageList = new TreeMap<>();
            ResultSet resultSet = ImageDataService.findImageByTheme(theme,connection);
            while (resultSet.next()){
                int userId = resultSet.getInt("USER_ID");
                String author = UserDataService.findUsernameByUserId(userId,connection);
                String name = resultSet.getString("NAME");
                String category = theme;
                int byteSize = resultSet.getInt("BYTE_SIZE");
                String type = name.substring(name.lastIndexOf(".")+1);
                Image image = new Image(byteSize,author,name,type,category,null);
                if(imageList.get(author) == null){
                    Map<String,Image> images = new TreeMap<>();
                    images.put(name,image);
                    imageList.put(author,images);
                }else{
                    imageList.get(author).put(name,image);
                }
            }
            return imageList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
