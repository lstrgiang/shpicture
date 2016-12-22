package com.cs494.common;

/**
 * Created by giangle on 12/13/16.
 */
public enum LogMessage {


    CONNECTION_NOT_AVAILABLE("Error, connection is not available");


    private String text;
    private LogMessage(String text){this.text = text;}

    @Override
    public String toString(){return this.text;}

    public String of(String text){return this.text+": "+text;}
}
