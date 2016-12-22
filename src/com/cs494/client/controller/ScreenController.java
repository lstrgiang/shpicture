package com.cs494.client.controller;

import com.cs494.client.controller.ControlledScreen;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by giangle on 12/13/16.
 */
public class ScreenController extends StackPane {
    private HashMap<String, Node> screens = new HashMap<>();

    public ScreenController() {
        super();
    }

    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    public Node getScreen(String name) {
        return screens.get(name);
    }

    public boolean loadScreen(String name, String resource,Stage stage) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen screenController = ((ControlledScreen) myLoader.getController());
            screenController.setScreenParent(this);
            screenController.setStage(stage);
            addScreen(name, loadScreen);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Timeline screenTransition(DoubleProperty opacity,final String name){
        Timeline fade = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity,1.0)),
                new KeyFrame(new Duration(800), event -> {

                    Timeline fadeIn= fadeIn(opacity,400);
                    fadeIn.play();
                }, new KeyValue(opacity,0.0)));
        return fade;
    }
    private Timeline fadeIn(DoubleProperty opacity,int time){
        Timeline fadeIn= new Timeline(
                new KeyFrame(Duration.ZERO,new KeyValue(opacity,0.0)),
                new KeyFrame(new Duration(time),new KeyValue(opacity,1.0)));
        return fadeIn;
    }
    public boolean setScreen(final String name){
        if(screens.get(name) != null){
            if(!getChildren().isEmpty()){
                getChildren().remove(0);
                getChildren().add(0,screens.get(name));
            }else{
                getChildren().add(screens.get(name));
            }
            return true;
        }else{
            System.out.println("Screen hasn't been loaded");
            return false;
        }
    }
    public boolean unloadScreen(String name){
        if(screens.remove(name) == null){
            System.out.println("Screen does not exist");
            return false;
        }else{
            return true;
        }
    }
}
