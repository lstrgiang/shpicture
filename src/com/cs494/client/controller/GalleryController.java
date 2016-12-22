package com.cs494.client.controller;


import com.cs494.client.Client;
import com.cs494.client.Main;
import com.cs494.common.ErrorMessage;
import com.cs494.common.Image;
import com.cs494.common.ImageService;
import com.cs494.common.UserInfo;
import com.jfoenix.controls.*;
import com.sun.javafx.font.freetype.HBGlyphLayout;
import com.sun.tools.internal.ws.wsdl.document.Input;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.util.*;
import java.util.List;

/**
 * Created by giangle on 12/13/16.
 */
public class GalleryController implements ControlledScreen,Initializable{

    @FXML private JFXButton uploadBtn;
    @FXML private JFXButton logoutBtn;
    @FXML private JFXButton searchBtn;
    @FXML private JFXButton mainBtn;
    @FXML private JFXButton updateStatus;
    @FXML private JFXButton userList;
    @FXML private AnchorPane root;

    @FXML private ScrollPane viewRoot;
    @FXML private Text statusText;
    @FXML private StackPane mainRoot;
    @FXML private Text usernameText;

    private List<Image> images;
    private Stage myStage;
    private ScreenController myController;
    private List<Node> contents;
    private boolean isDrawerToggled = false;
    @Override
    public void setScreenParent(ScreenController screenParent) {
        this.myController = screenParent;
    }

    @Override
    public void setStage(Stage stage) {
        this.myStage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contents = new ArrayList<>();
        setButtonEventHandler();
        usernameText.setText(Main.client.getcurrentUser());
        statusText.setText(Main.client.currentStatusText());
        if(prepareImages()){
            showImages();
        }
    }
    private void showImages(){
        TilePane tile = new TilePane();
        viewRoot.setStyle("-fx-background-color: DAE6F3;");
        tile.setPadding(new Insets(15, 15, 15, 15));
        tile.setHgap(15);
        viewRoot.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
        viewRoot.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
        viewRoot.setFitToWidth(true);
        viewRoot.setContent(tile);
        contents.add(tile);
        for(Image image : images){
            ImageView imageView = createImageView(image);
            if(imageView != null) {
                VBox box = new VBox();
                Text text = new Text();
                text.setText(image.getName());
                box.getChildren().addAll(imageView,text);
                tile.getChildren().addAll(box);
            }
        }

    }
    private ImageView createViewFromFile(File file) throws IOException {
        DropShadow ds = new DropShadow( 20, Color.CHOCOLATE );
        ImageView imageView = new ImageView();
        javafx.scene.image.Image viewImage = SwingFXUtils.toFXImage(ImageIO.read(file),null);
        imageView.setImage(viewImage);
        imageView.setOnMouseClicked( ( MouseEvent event ) -> imageView.requestFocus());
        imageView.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue ) -> {
            if ( newValue ) {imageView.setEffect( ds );}
            else {
                imageView.setEffect( null );
            }
        });
        double ratio =  viewImage.getWidth()/ viewImage.getHeight();
        imageView.setFitWidth(150*ratio);
        imageView.setFitHeight(150);
        return imageView;
    }
    private ImageView createImageView(Image image){
        ImageView imageView = new ImageView();
        javafx.scene.image.Image viewImage = SwingFXUtils.toFXImage(image.getImageContent(),null);
        imageView.setImage(viewImage);
        DropShadow ds = new DropShadow( 20, Color.CHOCOLATE );
        imageView.setOnMouseClicked( ( MouseEvent event ) ->{
            if(event.getClickCount() == 2)
                this.appendAndOpenDrawer(viewImage);
            else if(event.getClickCount() == 1){
                imageView.requestFocus();
                if(event.isShiftDown()){
                    String directory = this.openDirectoryChooser();
                    try {
                        ImageIO.write(image.getImageContent(), image.getContentType(), new File(directory+"/"+image.getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        imageView.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue ) -> {
            if ( newValue ) {imageView.setEffect( ds );}
            else {
                imageView.setEffect( null );
            }
        });
        double ratio = viewImage.getWidth() / viewImage.getHeight();
        imageView.setFitWidth(ratio*150);
        imageView.setFitHeight(150);
        return imageView;
    }

    private String openDirectoryChooser(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(myStage);
        if(selectedDirectory != null){
            return selectedDirectory.getAbsolutePath();
        }else{
            return null;
        }
    }
    private boolean prepareImages() {
        try{
            List<Image> getImages = Main.client.getUploadedImage();
            if(getImages!= null){
                images = new ArrayList<>();
                images.addAll(getImages);
                return true;
            }
        } catch (IOException e) {
            switch (e.getMessage()){
                case ErrorMessage.INVALID_THEME:
                    break;
                case ErrorMessage.INVALID_USERNAME:
                    break;
            }
        }
        return false;
    }
    private void logOut(){
        try {
            Main.client.logOut();
            myController.setScreen(Main.LOGIN_SCREEN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private File openFileChooser(){
        FileChooser fileChooser = new FileChooser();
        this.configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(myStage);
        return file;
    }
    private List<File> openMultipleFileChooser(){
        FileChooser fileChooser = new FileChooser();
        this.configureFileChooser(fileChooser);
        List<File> files =fileChooser.showOpenMultipleDialog(myStage);
        return files;
    }
    private void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("image files (jpg,png,bmp,tiff)", "*.jpg","*.png","*.bmp","*.tiff");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private void createUpdateStatusDialog(){
        StackPane pane = new StackPane();
        JFXDialogLayout content = new JFXDialogLayout();
        JFXButton submitBtn = new JFXButton("Submit");
        JFXButton closeBtn = new JFXButton("Close");
        JFXTextField textField = new JFXTextField();
        JFXDialog dialog = new JFXDialog(pane,content, JFXDialog.DialogTransition.CENTER);
        configTextFieldComponent(pane,content,textField,"Update your status","Status");
        closeBtn.setOnAction(event -> {
            dialog.close();
            root.getChildren().remove(pane);
        });
        submitBtn.setOnAction(event -> {
            dialog.close();
            this.updateStatus(textField.getText());
        });

        List<Node> actions = new ArrayList<>(Arrays.asList(submitBtn,closeBtn));
        content.setActions(actions);
        dialog.show();
    }
    private void createCategoryDialog(File file){
        StackPane pane = new StackPane();
        JFXDialogLayout content = new JFXDialogLayout();
        JFXButton submitBtn = new JFXButton("Submit");
        JFXButton closeBtn = new JFXButton("Close");
        JFXTextField textField = new JFXTextField();
        JFXDialog dialog = new JFXDialog(pane,content, JFXDialog.DialogTransition.CENTER);
        configTextFieldComponent(pane,content,textField,"Insert your image theme","Image Theme");
        closeBtn.setOnAction(event -> {
            dialog.close();
            root.getChildren().remove(pane);
        });
        submitBtn.setOnAction(event -> {
            dialog.close();
            this.appendCategoryAndSendImage(textField.getText(),file);
        });

        List<Node> actions = new ArrayList<>(Arrays.asList(submitBtn,closeBtn));
        content.setActions(actions);
        dialog.show();
    }
    private void appendCategoryAndSendImage(String category, File file ){
        try {
            List<Image> images = new ArrayList<>();
            String author = Main.client.getcurrentUser();
            BufferedImage content = ImageIO.read(file);
            String path = file.getPath();
            String type = path.substring(path.lastIndexOf(".")+1);
            String name = path.substring(path.lastIndexOf("/")+1);
            ByteArrayOutputStream array = ImageService.convertToArrayOutputStream(content,type);
            int byteSize = array.size();
            images.add(new Image(byteSize,author,name,type,category,content));
            showImages();
            Main.client.uploadImageList(images);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void configTextFieldComponent(StackPane pane,JFXDialogLayout content ,JFXTextField textField,String header,
                                          String inputField){
        pane.setLayoutX(500);
        pane.setLayoutY(350);
        pane.setPrefSize(350,200);
        root.getChildren().add(pane);
        content.setPrefSize(350,200);
        textField.setPromptText(inputField);
        content.getChildren().add(textField);
        content.setHeading(new Text(header));
    }
    private void updateStatus(String status){
        statusText.setText(status);
        Main.client.updateStatus(status);
        System.out.println(status);
    }
    private JFXDrawer createDrawer(Pane content){
        if(isDrawerToggled){
            return null;
        }
        JFXDrawer drawer = new JFXDrawer();
        JFXButton btn = new JFXButton();
        btn.setText("Close");
        AnchorPane.setLeftAnchor(btn,10.0);
        AnchorPane.setTopAnchor(btn,100.0);
        content.getChildren().add(btn);
        btn.setOnAction(event -> {
            isDrawerToggled = false;
            drawer.close();
            mainRoot.getChildren().remove(mainRoot.getChildren().size() -1);
        });
        drawer.setSidePane(content);
        mainRoot.getChildren().add(drawer);
        isDrawerToggled = true;
        drawer.open();
        return drawer;
    }
    private JFXDrawer appendAndOpenDrawer(javafx.scene.image.Image image) {
        ImageView imageView = new ImageView(image);
        double ratio = image.getWidth() / image.getHeight();
        imageView.setFitHeight(400);
        imageView.setFitWidth(400*ratio);
        AnchorPane pane = new AnchorPane();
        AnchorPane.setTopAnchor(imageView,100.0);
        AnchorPane.setLeftAnchor(imageView,100.0);
        pane.getChildren().addAll(imageView);
        return createDrawer(pane);
    }
    private void uploadImage(){
        File file = openFileChooser();
        createCategoryDialog(file);
    }
    private void uploadVBox(List<File> files) throws IOException {
        if(files == null || files.isEmpty()){
            return;
        }
        VBox vBox= new VBox();
        vBox.setPadding(new Insets(15, 15, 15, 15));
        vBox.setSpacing(15);
        contents.add(vBox);
        viewRoot.setContent(vBox);
        for(File file : files){
            ImageView imageView = createViewFromFile(file);
            if(imageView != null){
                vBox.getChildren().add(imageView);
            }
        }
    }
    private void showUserList(){
        List<UserInfo> userList = Main.client.retrieveUserInfoList();
        if(userList != null){
            VBox vBox = prepareUserBoxes(userList);
            System.out.println("Finished preparing user boxes");
            createDrawer(vBox);
        }
    }
    private HBox prepareUserBox(UserInfo user){
        System.out.println("Preparing inside box");
        Text status = new Text();
        if(user.getStatus() == 1){
            status.setText("Online");
            status.setFill(Color.GREEN);
        }else{
            status.setText("Offline");
            status.setFill(Color.RED);
        }
        VBox vBox= new VBox();
        Text name = new Text(user.getUsername());
        name.setFont(Font.font("Open Sans",20));
        Text statusText = new Text(user.getStatusText());
        vBox.getChildren().addAll(name,statusText);
        HBox hBox= new HBox();
        hBox.getChildren().addAll(status,vBox);
        return hBox;
    }
    private VBox prepareUserBoxes(List<UserInfo> userList){
        System.out.println("Preparing user boxes");
        VBox vBox = new VBox();
        for(UserInfo user : userList){
            vBox.getChildren().add(prepareUserBox(user));
        }
        return vBox;
    }
    private void createChooseDialog(){
        StackPane pane = new StackPane();
        JFXDialogLayout content = new JFXDialogLayout();
        JFXButton submitBtn = new JFXButton("User");
        JFXButton closeBtn = new JFXButton("Theme");
        JFXDialog dialog = new JFXDialog(pane,content, JFXDialog.DialogTransition.CENTER);
        pane.setLayoutX(500);
        pane.setLayoutY(350);
        pane.setPrefSize(350,200);
        root.getChildren().add(pane);
        content.setPrefSize(350,200);
        content.setHeading(new Text("Search by User or Theme?"));
        closeBtn.setOnAction(event -> {
            dialog.close();
            root.getChildren().remove(pane);
            userSearchDialog(0);
        });
        submitBtn.setOnAction(event -> {
            dialog.close();
            root.getChildren().remove(pane);
            userSearchDialog(1);
        });

        List<Node> actions = new ArrayList<>(Arrays.asList(submitBtn,closeBtn));
        content.setActions(actions);
        dialog.show();
    }
    private void userSearchDialog(int flag){
        StackPane pane = new StackPane();
        JFXDialogLayout content = new JFXDialogLayout();
        JFXButton submitBtn = new JFXButton("Submit");
        JFXButton closeBtn = new JFXButton("Close");
        JFXTextField textField = new JFXTextField();
        JFXDialog dialog = new JFXDialog(pane,content, JFXDialog.DialogTransition.CENTER);
        configTextFieldComponent(pane,content,textField,"Search for images","Search Term");
        closeBtn.setOnAction(event -> {
            dialog.close();
            root.getChildren().remove(pane);

        });
        submitBtn.setOnAction(event -> {
            dialog.close();
            root.getChildren().remove(pane);
            searchAndShowImage(textField.getText(),flag);
        });

        List<Node> actions = new ArrayList<>(Arrays.asList(submitBtn,closeBtn));
        content.setActions(actions);
        dialog.show();
    }

    private void searchAndShowImage(String text, int flag) {
        List<Image> imageList;
        if(flag == 1){
            imageList = Main.client.searchImageByUser(text);
        }else{
            imageList = Main.client.searchImageByThem(text);
        }
        System.out.println(imageList.size());
        if(imageList != null && imageList.size() >=0){
            images = new ArrayList<>();
            images.addAll(imageList);
            showImages();
        }
    }

    private void setButtonEventHandler(){
        uploadBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            uploadImage();
        });
        logoutBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            logOut();
        });
        searchBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            createChooseDialog();
        });
        userList.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            showUserList();
        });
        mainBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            viewRoot.setContent(contents.get(0));
        });

        updateStatus.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            this.createUpdateStatusDialog();
        });
    }

}
