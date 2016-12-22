package com.cs494.server.service;

import com.cs494.common.Image;
import com.cs494.server.DatabaseService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by giangle on 12/14/16.
 */
public class ImageDataService {


    public static void save(Image image, int userId, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "INSERT INTO Images (NAME,USER_ID,CATEGORY,BYTE_SIZE) "
                + "VALUES ('"+image.getName()+"','"+userId+"','"+image.getCategory()+"','"+image.getByteSize()+"');";
        statement.executeUpdate(sql);
    }

    public static void saveImageList(List<Image> images, int userId, Connection connection) throws SQLException{
        for(Image image : images){
            save(image,userId,connection);
        }
    }

    public static ResultSet getImageInfoByUser(String username,Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        int userId = UserDataService.findUserIdByUsername(username,connection);
        String sql = "SELECT NAME,CATEGORY,BYTE_SIZE FROM Images WHERE USER_ID='"
                + userId +"';";
        ResultSet result = statement.executeQuery(sql);
        return result;
    }
    public static ResultSet findImageByTheme(String category,Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        String sql = "SELECT USER_ID,NAME,CATEGORY,BYTE_SIZE FROM Images WHERE CATEGORY='"
                + category +"';";
        ResultSet result = statement.executeQuery(sql);
        return result;
    }

}
