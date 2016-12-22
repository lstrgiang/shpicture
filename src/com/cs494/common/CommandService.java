package com.cs494.common;

import com.cs494.server.DatabaseService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cs494.common.Command.*;
import static com.cs494.common.HeaderInfo.*;

/**
 * Created by giangle on 12/14/16.
 */
public class CommandService {
    public static ByteArrayOutputStream converBufferToArray(BufferedImage content, String type) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(content, type, byteArrayOutputStream);
        return byteArrayOutputStream;
    }
    public static String currentPath(){
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        return s;
    }
    public static void sendUploadedImage(String username,PrintStream out,DataInputStream in, OutputStream outStream) {
        try {
            List<Image> images = ImageService.readImageFromUser(username);
            if(images != null &&!images.isEmpty() ){
                sendMultipleImage(in,out,outStream,images);
            }
        } catch (IOException e) {
            if(e.getMessage().equals(ErrorMessage.INVALID_THEME) || e.getMessage().equals(ErrorMessage.INVALID_USERNAME) ||
                    e.getMessage().equals(ErrorMessage.NO_IMAGE_FOUND)){
                out.println(HeaderInfo.errorOf(e.getMessage()));
            }
            e.printStackTrace();
        }

    }
    public static String readErrorInfo(DataInputStream in) throws IOException{
        while (true){
            String message = in.readLine();
            if(message != null && !message.isEmpty() && message.startsWith(HeaderInfo.ERROR.toString())){
                return message.substring(HeaderInfo.ERROR.toString().length());
            }
        }
    }
    public static String readMessageInfo(HeaderInfo header, DataInputStream in, int headerLength) throws IOException {
        while (true){
            String message = in.readLine();
            if(message!= null && !message.isEmpty() && message.startsWith(Command.of(header))){
                return message.substring(headerLength);
            }
        }
    }
    public static boolean waitForCommand(String command, DataInputStream in) throws IOException{
        while(true){
            String message = in.readLine();
            if(message!= null && !message.isEmpty() && message.equals(command)){
                return true;
            }
        }
    }


    public static void sendMultipleImage(DataInputStream in, PrintStream out, OutputStream outStream,List<Image> images)
            throws IOException {
        out.println(ILIST_SEND);
        out.println(Command.of(ILIST_SIZE) + String.valueOf(images.size()));
        for(Image image : images){
            boolean isReceived = sendImage(in, out, outStream, image);
            int count = 0;
            while (!isReceived && count <=10){
                isReceived = sendImage(in, out, outStream, image);
                count++;
            }
        }
    }
    public static boolean sendImage(DataInputStream in,PrintStream out, OutputStream outStream, Image image)
            throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = ImageService.convertToArrayOutputStream(image);
        System.out.println("Sending image: " + image.getName());
        writeImageInfo(out,image);
        System.out.println(byteArrayOutputStream.toByteArray().length);
        try {
            outStream.write(byteArrayOutputStream.toByteArray());
        }catch (Exception e){

        }
        System.out.println("Writing " + image.getName());
        outStream.flush();
        System.out.println("Sent");
        return waitForAck(in);
    }
    public static void writeImageInfo(PrintStream out,Image image){
        out.println(Command.of(IMAGE_SIZE) + String.valueOf(image.getByteSize()));
        out.println(Command.of(IMAGE_NAME) + image.getName());
        out.println(Command.of(IMAGE_TYPE) + image.getContentType());
        out.println(Command.of(IMAGE_AUTH) + image.getAuthor());
        out.println(Command.of(IMAGE_CATE) + image.getCategory());
        System.out.println("Sending image info");
    }
    // Wait for OK response
    public static boolean waitForAck(DataInputStream in) throws IOException {
        while(true){
            System.out.println("Waiting for Ack");
            String message= in.readLine();
            if(message!= null && !message.isEmpty()) {
                if(message.equals(RES_OK)) return true;
                else return false;
            }
        }
    }
    public static boolean waitForNak(DataInputStream in) throws IOException{
        while(true){
            String message=in.readLine();
            if(message!= null && !message.isEmpty()){

                return message.equals(RES_ERROR) ? true : false;
            }
        }
    }
    //Send username and password for authentication or registration
    public static void sendUserInfo(PrintStream out, String command, String username, String password){
        out.println(command);
        out.println(Command.of(USERNAME)+username);
        out.println(Command.of(PASSWORD)+password);
    }

    //Read image information from the sender
    public static Image readImage(DataInputStream in,PrintStream out) throws IOException {
        int byteSize = Integer.parseInt(readMessageInfo(IMAGE_SIZE,in, IMAGE_INFO_HEADER_LENGTH));
        String imageName = readMessageInfo(IMAGE_NAME,in,IMAGE_INFO_HEADER_LENGTH);
        String imageType = readMessageInfo(IMAGE_TYPE,in,IMAGE_INFO_HEADER_LENGTH);
        String imageAuthor = readMessageInfo(IMAGE_AUTH,in,IMAGE_INFO_HEADER_LENGTH);
        String imageCateogry = readMessageInfo(IMAGE_CATE,in,IMAGE_INFO_HEADER_LENGTH);
        System.out.println("Size:" +byteSize );
        BufferedImage imageContent = readImageContent(byteSize,in);
        while (imageContent == null){
            imageContent = readImageContent(byteSize,in);
        }
        System.out.println("Image "+imageName+" received");
        return imageContent!=null ? new Image(byteSize,imageAuthor,imageName,imageType,imageCateogry,imageContent) : null;
    }
    public static List<Image> receiveMultipleImages(PrintStream out,DataInputStream in) throws IOException{
        List<Image> images = new ArrayList<>();
        int imageListSize = Integer.parseInt(readMessageInfo(ILIST_SIZE,in,IMAGE_INFO_HEADER_LENGTH));
        if(imageListSize == 0){
            return null;
        }
        for(int i=0;i<imageListSize;i++){
            Image image = readImage(in,out);
            while (image == null){
                System.out.println("ERROR");
                out.println(RES_ERROR);
                image = readImage(in,out);
            }
            out.println(RES_OK);
            images.add(image);
        }
        return images;
    }
    public static BufferedImage readImageContent(int byteSize,DataInputStream in) throws IOException {
        BufferedImage image = null;
        int count = 0;
        while(true) {
            System.out.println("Reading content "+count);
            byte[] imageAr = readByte(in,byteSize);
            image = ImageIO.read(new ByteArrayInputStream(imageAr));
            if(image == null){
                if(count<= 10) {
                    count++;
                    continue;
                }
                else
                    return null;
            }
            break;
        }
        return image;
    }
    public static byte[] readByte(DataInputStream in,int byteSize) throws IOException{
        System.out.println("Reading bytes");
        int length = in.available();
        if(length >= byteSize){
            System.out.println("Reading with "+byteSize);
            byte[] array = new byte[byteSize];
            in.readFully(array);
            System.out.println("Reading finished ");
            return array;
        }else{
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            while(in.available()!= 0){
                System.out.println("Reading with "+in.available());
                byte[] tmp= new byte[in.available()];
                in.readFully(tmp);
                array.write(tmp);
            }
            System.out.println("Reading finished");
            return array.toByteArray();
        }

    }

    public static List<UserInfo> readUserInfoList(DataInputStream in) {
        try {
            int size = Integer.parseInt(readMessageInfo(HeaderInfo.USER_LIST_SIZE,in,Command.USER_INFO_LIST_HEADER_LENGTH));
            List<UserInfo> list = new ArrayList<>();
            for(int i=0;i<size;i++){
                String username = readMessageInfo(HeaderInfo.USER_NAME,in,Command.USER_INFO_LIST_HEADER_LENGTH);
                int statusCode = Integer.parseInt(readMessageInfo(HeaderInfo.USER_STATUS,in,Command.USER_INFO_LIST_HEADER_LENGTH));
                String statusText = readMessageInfo(HeaderInfo.USER_TEXT,in,Command.USER_INFO_LIST_HEADER_LENGTH);
                list.add(new UserInfo(username,statusText,statusCode));
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Image> findImageByTheme(DataInputStream in) throws IOException {
        List<Image> imageList = new ArrayList<>();
        String category = readMessageInfo(HeaderInfo.SEARCH_CAT,in,Command.IMAGE_INFO_HEADER_LENGTH);
        Map<String,Map<String,Image>> imageMap = DatabaseService.searchImageByTheme(category);
        for(String username : imageMap.keySet()){
            Map<String,Image> userImage = imageMap.get(username);
            Map<String,BufferedImage> imageBuffer = ImageService.readImageFromUserDir(username);
            for(String name : userImage.keySet()){
                Image image = userImage.get(name);
                image.setImageContent(imageBuffer.get(name));
                imageList.add(image);
            }
        }
        return imageList;
    }
    public static void sendUserInfoList(PrintStream out){
        List<UserInfo> userList = DatabaseService.retrieveUserInfoList();
        if(userList == null || userList.size() <= 0){
            out.println(HeaderInfo.USER_LIST_SIZE+"0");
            return;
        }
        out.println(HeaderInfo.USER_LIST_SIZE+String.valueOf(userList.size()));
        for(UserInfo user : userList){
            out.println(HeaderInfo.USER_NAME+user.getUsername());
            out.println(HeaderInfo.USER_STATUS+String.valueOf(user.getStatus()));
            out.println(HeaderInfo.USER_TEXT+String.valueOf(user.getStatusText()));
        }
    }
//    public static int readFully(InputStream in, byte b[]) throws IOException {
//        int off = 0, len = b.length;
//        if (len < 0)
//            throw new IndexOutOfBoundsException();
//        int n = 0;
//        while (n < len) {
//            int count = in.read(b, off + n, len - n);
//            if (count < 0)
//                break;
//            n += count;
//            if(len - n < )
//            System.out.println("Count: "+count);
//            System.out.println("N: " +n);
//        }
//        return n;
//    }

}
