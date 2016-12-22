package com.cs494.client;

import com.cs494.client.controller.ScreenController;
import com.cs494.common.CommandService;
import com.cs494.common.Image;
import com.cs494.common.ImageService;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
    public static String LOGIN_SCREEN= "login";
    public static String LOGIN_FILE = "/com/cs494/client/screen/LoginScreen.fxml";
    public static String GALLERY_SCREEN ="gallery";
    public static String GALLERY_FILE = "/com/cs494/client/screen/GalleryScreen.fxml";
    public static Client client = new Client();
    private static ScreenController mainContainer;
    @Override
    public void start(Stage primaryStage) throws Exception{
        client.start();
        mainContainer = new ScreenController();
        mainContainer.loadScreen(LOGIN_SCREEN,LOGIN_FILE,primaryStage);
        mainContainer.setScreen(LOGIN_SCREEN);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop() throws Exception{
        client.terminateConnection();
        System.out.println("The program is terminated");
    }
    public static void returnToLogin(){
        mainContainer.setScreen(LOGIN_SCREEN);
    }
    private static Image prepareImage(String path,String category) throws IOException {

        BufferedImage content= ImageIO.read(new File(path));
        String name = path.substring(path.lastIndexOf("/")+1);
        String type = name.substring(name.lastIndexOf(".")+1);
        int size = CommandService.converBufferToArray(content,type).size();
        Image image = new Image(size,"lstrgiang",name,type,category,content);
        return image;
    }
    public static void uploadMultipleImage() throws IOException {
        List<Image> image = new ArrayList<Image>();
        String[] paths ={"/Users/giangle/02_StudySpace/02_Assignments/CS494-SHPicture/src/com/cs494/client/screen/assets/Logo.png",
                    "/Users/giangle/02_StudySpace/02_Assignments/CS494-SHPicture/src/com/cs494/client/screen/assets/my_logo.png",
                "/Users/giangle/02_StudySpace/02_Assignments/CS494-SHPicture/src/com/cs494/client/screen/assets/Splash.jpg"};
        String[] category = { "Testing", "Logo", "Slash"};
        for(int i=0;i<paths.length;i++){
            image.add(prepareImage(paths[i],category[i]));
        }
        client.uploadImageList(image);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
