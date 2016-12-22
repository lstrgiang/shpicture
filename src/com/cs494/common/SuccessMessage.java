package com.cs494.common;

/**
 * Created by giangle on 12/13/16.
 */
public enum SuccessMessage {

    AUTH_SUCCESS("Sign in successfully. Welcome"),
    REGIST_SUCCESS("Sign up successfully. Now you can sign in"),
            ;


    private String text;

    private SuccessMessage(String text){this.text = text;}

    @Override
    public String toString(){return this.text;}


}
