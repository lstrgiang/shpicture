package com.cs494.server.service;

import com.cs494.common.UserInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by giangle on 12/14/16.
 */
public class UserDataService {



    public enum UserTable{

        COL_ID("id"),
        COL_USERNAME("username"),
        COL_PASSWORD("password"),
        COL_STATUS("status");

        private String colName;
        UserTable(String name){this.colName = name;}
        @Override
        public String toString(){return this.colName;}
    }

    public static int findUserIdByUsername(String username,Connection connection) throws SQLException{
        ResultSet resultSet = findByUsername(username,connection);
        if(resultSet.next()){
            return resultSet.getInt(UserTable.COL_ID.toString());
        }
        return -1;
    }
    public static String findUsernameByUserId(int userId, Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        String sql = "SELECT USERNAME FROM User WHERE ID='"
                + userId+"';";
        ResultSet result = statement.executeQuery(sql);
        if(result.next()){
            return result.getString(UserTable.COL_USERNAME.toString());
        }
        return null;
    }
    public static boolean isUserExist(String username, Connection connection) throws SQLException{
        ResultSet resultSet = UserDataService.findByUsername(username,connection);
        if(!resultSet.wasNull()){
            return true;
        }
        return false;
    }
    public static boolean isUserExist(String username,String password, Connection connection) throws SQLException{
        String retrievedPassword;
        ResultSet resultSet = UserDataService.findByUsername(username,connection);
        while(resultSet.next()){
            retrievedPassword = resultSet.getString(UserTable.COL_PASSWORD.toString());
            if(retrievedPassword!= null &&retrievedPassword.equals(password)){
                return true;
            }
        }
        return false;
    }
    public static ResultSet findByUsername(String username,Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "SELECT ID,USERNAME,PASSWORD, STATUS FROM User WHERE USERNAME='"
                + username +"';";
        ResultSet result = statement.executeQuery(sql);
        return result;
    }
    public static void save(String username,String password,Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "INSERT INTO User (USERNAME,PASSWORD) " + "VALUES ('"+username+"','"+password+"');";
        statement.executeUpdate(sql);
    }
    public static void updateUserStatus(boolean status, String username, Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        int statusCode = status ? 1 : 0;
        String sql = "UPDATE USER SET STATUS = "+statusCode+" WHERE USERNAME='"+username+"';";
        statement.executeUpdate(sql);
    }
    public static void updateUserStatusText(String status, String username, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String newStatus = status.replace("'","''");
        String sql = "UPDATE USER SET STATUS_TEXT = '"+newStatus+"' WHERE USERNAME='"+username+"';";
        statement.executeUpdate(sql);
    }
    public static String getUserStatusText(String username, Connection connection) throws  SQLException{
        Statement statement = connection.createStatement();
        String sql = "SELECT STATUS_TEXT FROM USER WHERE USERNAME = '"+username+"';";
        ResultSet result = statement.executeQuery(sql);
        if(result.next()){
            return result.getString("status_text");
        }
        return null;

    }
    public static ResultSet getUserList(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        String sql = "SELECT USERNAME,STATUS,STATUS_TEXT FROM USER;";
        return statement.executeQuery(sql);
    }
}
