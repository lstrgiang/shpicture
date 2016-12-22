package com.cs494.client;

import com.cs494.common.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.cs494.common.Command.*;
import static com.cs494.common.HeaderInfo.USERNAME;

/**
 * Created by giangle on 12/9/16.
 */
public final class Client implements Runnable {

    private Socket clientSocket;
    private PrintStream out;
    private DataInputStream in;
    private OutputStream outStream;
    private InputStream inStream;
    private boolean closed = true;
    private String host = "localhost";
    private int port = 2222;
    private String currentUser = null;
    private boolean running = true;

    @Override
    public void run() {
        while(running){
            start();
        }

    }

    public void start() {
        try{
            establishConnection();
        } catch (IOException e) {
            System.out.println("Unable to connect the service");
        }
    }
    public void send(Serializable data) throws IOException {
        if(!closed){
            out.println(data);
        }else{
            throw new IOException();
        }
    }

    public  void establishConnection() throws IOException {
        clientSocket = new Socket(host,port);
        inStream = clientSocket.getInputStream();
        outStream = clientSocket.getOutputStream();
        out = new PrintStream(outStream);
        in = new DataInputStream(inStream);
        closed = false;
    }
    public  void closeConnection() throws IOException{
        terminateConnection();
        clientSocket.close();
        in.close();
        out.close();
        inStream.close();
        outStream.close();
    }
    public  boolean isAuthenticted() {
        return (currentUser != null);
    }
    public void signOut() {
        currentUser = null;
        try{
            closeConnection();
        } catch (IOException e) {
            System.out.println("The connection is closed");
            e.printStackTrace();
        }
    }
    public void setCurrentUser(String username){currentUser = username;}
    public boolean authenticate(String username, String password) throws IOException{
        if(closed){throw new IOException();}
        CommandService.sendUserInfo(out,AUTHENTICATE,username,password);
        return CommandService.waitForAck(in);
    }
    public String register(String username, String password) throws IOException{
        if(closed){
            throw new IOException();
        }
        CommandService.sendUserInfo(out,REGISTER,username,password);
        while(true){
            String message= in.readLine();
            if(message != null){
                return message;
            }
        }
    }
    private void closeAndReturnUI(){
        currentUser = null;
        try {
            resetConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.returnToLogin();
    }
    public List<Image> getUploadedImage() throws IOException {
            if (!isAuthenticted()) throw new IOException();
            out.println(ILIST_REQU);
            out.println(Command.of(USERNAME) + currentUser);
            System.out.println(currentUser);
            String message;
            int count = 0;
            while (true) {
                message = in.readLine();
                System.out.println(count++);
                if (message != null && !message.isEmpty()) {
                    System.out.println("Message" + message);
                    break;
                }

            }
            if (message.equals(Command.ILIST_SEND)) {
                System.out.println("Receiving Image");
                List<Image> images = CommandService.receiveMultipleImages(out, in);
                if (images == null) {
                    String error = CommandService.readErrorInfo(in);
                    throw new IOException(error);
                }
                System.out.println("Received Images images.");
                return images;
            } else if (message.equals(HeaderInfo.ERROR)) {
                throw new IOException(message);
            }
            return null;

    }
    public String getcurrentUser(){
        return currentUser;
    }
    public String currentStatusText() {
        out.println(Command.STATUS_TEXT);
        try {
            String status = CommandService.readMessageInfo(HeaderInfo.STATUS_TEXT, in, Command.IMAGE_INFO_HEADER_LENGTH);
            return status;
        } catch (SocketException e) {
            closeAndReturnUI();
        } catch (IOException e) {
            e.printStackTrace();
            return "No status";
        }
        return null;
    }
    public  void updateStatus(String status) {
        try {
            if (isAuthenticted()) {
                out.println(Command.STATUS);
                out.println(HeaderInfo.STATUS + status);
            }
        }catch (Exception e){
            closeAndReturnUI();
        }
    }
    public void uploadImage(Image image) throws IOException{
        if(!isAuthenticted()) throw new IOException();
        out.println(ILIST_SEND);
        out.println(USERNAME+currentUser);
        List<Image> imageList = new ArrayList<>();
        imageList.add(image);
        CommandService.sendMultipleImage(in,out,outStream,imageList);
    }
    public void uploadImageList(List<Image> imageList) throws IOException{
        if(!isAuthenticted()  || imageList.isEmpty()) throw new IOException();
        out.println(ILIST_SEND);
        out.println(USERNAME+currentUser);
        CommandService.sendMultipleImage(in,out,outStream,imageList);
    }
    public void terminateConnection(){
        out.println(Command.CLOSE);
        running = false;
    }
    public void resetConnection() throws IOException {
        terminateConnection();
        running = true;
        closeConnection();
        establishConnection();
    }
    public void logOut() throws IOException {
        currentUser = null;
        resetConnection();
    }
    public List<UserInfo> retrieveUserInfoList(){
        out.println(Command.USER_LIST);
        return CommandService.readUserInfoList(in);
    }

    public List<Image> searchImageByUser(String text) {
        out.println(SEARCH_USR);
        out.println(HeaderInfo.SEARCH_USR+text);
        try {
            List<Image> imageList = CommandService.receiveMultipleImages(out,in);
            return imageList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Image> searchImageByThem(String text) {
        out.println(SEARCH_CAT);
        out.println(HeaderInfo.SEARCH_CAT+text);
        try {
            List<Image> imageList = CommandService.receiveMultipleImages(out,in);
            return imageList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

