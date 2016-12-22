package com.cs494.client.controller;

import com.cs494.client.Client;
import com.cs494.client.Main;
import com.cs494.common.Command;
import com.cs494.common.ErrorMessage;
import com.cs494.common.SuccessMessage;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable,ControlledScreen {
    @FXML private JFXTextField userContainer;
    @FXML private JFXPasswordField pwdContainer;
    @FXML private StackPane stackPane;
    @FXML private StackPane root;

    private ScreenController myController;
    private Stage myStage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML private void signInOnClicked(ActionEvent event){
        if(!isEmptyFields()) return;

        try {
            boolean isLogged = Main.client.authenticate(userContainer.getText(),pwdContainer.getText());
            if(isLogged){
                Main.client.setCurrentUser(userContainer.getText());
                myController.loadScreen(Main.GALLERY_SCREEN, Main.GALLERY_FILE,myStage);

                myController.setScreen(Main.GALLERY_SCREEN);

            }else{
                this.showErrorDialog(ErrorMessage.INVALID_USERNAME_PASSWORD);

            }
        } catch (IOException e) {
            this.showErrorDialog(ErrorMessage.CONNECTION_NOT_AVAILABLE);

        }

    }
    @FXML private void signUpOnCliked(ActionEvent event){
        if(!isEmptyFields()) return;
        try{
            String isRegistered = Main.client.register(userContainer.getText(),pwdContainer.getText());
            if(isRegistered.equals(Command.RES_OK)){
                showSuccessDialog(SuccessMessage.REGIST_SUCCESS);
            }else if(isRegistered.equals(Command.EXISTED_USERNAME)){
                showErrorDialog(ErrorMessage.EXISTED_USERNAME);
            }else{
                showErrorDialog(ErrorMessage.CONNECTION_NOT_AVAILABLE);
            }
        } catch (IOException e) {
            showErrorDialog(ErrorMessage.CONNECTION_NOT_AVAILABLE);
        }
    }


    private boolean isEmptyFields(){
        if(userContainer.getText().isEmpty()){
            this.showErrorDialog(ErrorMessage.MISSING_USERNAME);
            return false;
        }
        if(pwdContainer.getText().isEmpty()){
            this.showErrorDialog(ErrorMessage.MISSNG_PASSWORD);
            return false;
        }
        return true;
    }
    private void showErrorDialog(String error){
        this.createDialog(error,"Error");
    }
    private void showSuccessDialog(SuccessMessage successMessage){
        this.createDialog(successMessage.toString(),"Success");
    }
    private void createDialog(String body,String header){
        JFXDialogLayout content = new JFXDialogLayout();
        JFXButton button = new JFXButton("Okay");
        JFXDialog dialog = new JFXDialog(stackPane,content, JFXDialog.DialogTransition.CENTER);
        button.setOnAction(event -> dialog.close());
        content.setHeading(new Text(header));
        content.setBody(new Text(body));
        content.setActions(button);
        dialog.show();
    }


    @Override
    public void setScreenParent(ScreenController screenParent) {
        myController = screenParent;
    }

    @Override
    public void setStage(Stage stage) {
        myStage = stage;
    }


}
