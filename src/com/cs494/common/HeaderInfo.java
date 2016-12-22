package com.cs494.common;

/**
 * Created by giangle on 12/14/16.
 */
public enum HeaderInfo {

    //Image information
    IMAGE_SIZE ("IMAGE_SIZE:"),
    IMAGE_NAME ("IMAGE_NAME:"),
    IMAGE_TYPE ("IMAGE_TYPE:"),
    IMAGE_DESC ("IMAGE_DESC:"),
    IMAGE_AUTH ("IMAGE_AUTH:"),
    IMAGE_CATE ("IMAGE_CATE:"),

    //Image List information
    ILIST_SIZE ("ILIST_SIZE:"),

    //User List information
    USER_LIST_SIZE("LISTSIZE:"),
    USER_NAME("USERNAME:"),
    USER_STATUS("USERSTAT:"),
    USER_TEXT("USERTEXT:"),
    //Account information
    USERNAME ("USR:"),
    PASSWORD ("PWD:"),
    ERROR("ERROR:"),
    STATUS("STATUS:"),
    STATUS_TEXT("STATUS_TXT:"),
    SEARCH_CAT("SEARCH_CAT:"),
    SEARCH_USR("SEARCH_USR:");




    private String header;
    HeaderInfo(String text){this.header = text;}
    @Override
    public String toString(){return this.header;}
    public static String errorOf(String error){return HeaderInfo.ERROR.toString()+error;}
}
