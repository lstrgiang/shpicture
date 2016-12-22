package com.cs494.common;

/**
 * Created by giangle on 12/13/16.
 */
public class Command {

    //Signal
    public static final String AUTHENTICATE = "AUTH REQ" ;
    public static final String REGISTER = "REGIST REG";
    public static final String LOGOUT = "LOGOUT REG";
    public static final String STATUS = "STATUS";

    public static final String CLOSE = "TERMINATE";
    public static final String RES_OK = "OK";
    public static final String RES_ERROR = "ERROR";
    public static final String EXISTED_USERNAME = "USR_EXISTED";

    //Image commands
    public static final int IMAGE_CMD_HEADER_LENGTH = 10;
    public static final String IMAGE_SEND = "SEND_IMAGE";
    public static final String ILIST_SEND = "SEND_ILIST";
    public static final String ILIST_REQU = "ILIST_REQU";
    public static final String STATUS_TEXT = "STATUS_TXT";
    public static final String USER_LIST = "USERS_LIST";
    public static final String SEARCH_USR = "SEARCH_USR";
    public static final String SEARCH_CAT = "SEARCH_CAT";
    public static String of(HeaderInfo header){
        return header.toString();
    }

    //Image information
    public static final int IMAGE_INFO_HEADER_LENGTH=11;
    public static final int USER_INFO_HEADER_LENGTH =4;
    public static final int STATUS_INFO_HEADER_LENGTH = 7;
    public static final int USER_INFO_LIST_HEADER_LENGTH = 9;

}