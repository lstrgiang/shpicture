package com.cs494.client.controller;

import javafx.stage.Stage;

/**
 * Created by giangle on 12/13/16.
 */
public interface ControlledScreen {

    void setScreenParent(ScreenController screenParent);
    void setStage(Stage stage);
}
