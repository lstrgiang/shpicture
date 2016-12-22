package com.cs494.server;

import com.cs494.common.*;
import com.cs494.server.service.ImageDataService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.List;
import static com.cs494.common.Command.*;
import static com.cs494.common.HeaderInfo.*;
/**
 * Created by giangle on 12/13/16.
 */
class ClientThread extends Thread{
    private DataInputStream in = null;
    private PrintStream out = null;
    private Socket clientSocket = null;
    private final List<ClientThread> threads;
    private int currentIndex;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private String currentUser = null;
    private boolean isRunning;

    public ClientThread(Socket clientSocket, List<ClientThread> threads,int index){
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.currentIndex = index;
        this.isRunning = true;
    }

    public void run(){
        try{
            outStream = clientSocket.getOutputStream();
            inStream = clientSocket.getInputStream();
            in = new DataInputStream(inStream);
            out = new PrintStream(outStream);
            while(isRunning){
                processMessage(in.readLine());
            }
        } catch (SocketException e){
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println(LogMessage.CONNECTION_NOT_AVAILABLE);
            e.printStackTrace();
        }
    }

    private void processMessage(String message) throws IOException {
        if(message == null) return;
        switch (message){
            case AUTHENTICATE:
                String authUsername = CommandService.readMessageInfo(USERNAME,in,USER_INFO_HEADER_LENGTH);
                String resAuth = DatabaseService.authenticate(authUsername,
                        CommandService.readMessageInfo(PASSWORD,in,USER_INFO_HEADER_LENGTH)) ? RES_OK : RES_ERROR;
                if(resAuth.equals(RES_OK)){
                    DatabaseService.updateUserStatus(true,authUsername);
                    currentUser = authUsername;
                }
                this.responseWithMessage(resAuth);
                break;
            case REGISTER:
                String[] resReg={EXISTED_USERNAME,RES_OK,RES_ERROR};
                int signal = DatabaseService.register(CommandService.readMessageInfo(USERNAME,in,USER_INFO_HEADER_LENGTH),
                        CommandService.readMessageInfo(PASSWORD,in,USER_INFO_HEADER_LENGTH));
                this.responseWithMessage(resReg[signal+1]);
                break;
            case ILIST_SEND:
                String storeUsername  = CommandService.readMessageInfo(HeaderInfo.USERNAME,in,Command.USER_INFO_HEADER_LENGTH);
                List<Image> images = CommandService.receiveMultipleImages(out,in);
                DatabaseService.storeImagesForUser(images,storeUsername);
                break;
            case ILIST_REQU:
                String regUsername = CommandService.readMessageInfo(HeaderInfo.USERNAME,in,Command.USER_INFO_HEADER_LENGTH);
                CommandService.sendUploadedImage(regUsername,out,in,outStream);
                break;
            case LOGOUT:
                if(currentUser!= null){
                    currentUser = null;
                    DatabaseService.updateUserStatus(false,currentUser);
                }
                break;
            case Command.STATUS:
                String status = CommandService.readMessageInfo(HeaderInfo.STATUS,in,Command.STATUS_INFO_HEADER_LENGTH);
                DatabaseService.updateUserStatusText(status,currentUser);
                break;
            case Command.STATUS_TEXT:
                if(currentUser != null){
                    String statusText = DatabaseService.getUserStatusText(currentUser);
                    out.println(HeaderInfo.STATUS_TEXT+statusText);
                }
                break;
            case Command.SEARCH_CAT:
                List<Image> imageList = CommandService.findImageByTheme(in);
                CommandService.sendMultipleImage(in,out,outStream,imageList);
                break;
            case Command.SEARCH_USR:
                String username = CommandService.readMessageInfo(HeaderInfo.SEARCH_USR,in,Command.IMAGE_INFO_HEADER_LENGTH);
                CommandService.sendUploadedImage(username,out,in,outStream);
                break;
            case Command.USER_LIST:
                CommandService.sendUserInfoList(out);
            case CLOSE:
                if(currentUser != null){
                    DatabaseService.updateUserStatus(false,currentUser);
                }
                close();
                break;



        }
    }
    private void responseWithMessage(String message) {
        out.println(message);
    }

    public void close() throws IOException {
        System.out.println("One connection is closed");
        in.close();
        out.close();
        clientSocket.close();
        this.isRunning = false;
        Server.closeThread(currentIndex);
    }
}
