package com.newnius.learn.util;

/**
 * Created by newnius on 16-3-16.
 */
public class ErrorCode {
    public static final int SUCCESS = 0;
    public static final int CONNECTION_FAIL = 1;
    public static final int INVALID_DATA_FORMAT = 2;
    public static final int NOT_LOGED = 3;
    public static final int UNKNOWN = 4;


    public static final int TXOBJECT_IS_NULL = 5;


    /*login*/
    public static final int USER_NOT_EXIST = 6;
    public static final int USERNAME_IS_EMPTY = 7;
    public static final int PASSWORD_IS_EMPTY = 8;
    public static final int EMAIL_IS_EMPTY = 9;
    public static final int WRONG_PASSWORD = 10;
    public static final int NOT_VERIFIED = 11;

    /* register */
    public static final int INVALID_EMAIL = 12;
    public static final int USERNAME_OCCUPIED = 13;
    public static final int EMAIL_OCCUPIED = 14;

    /* create group */
    public static final int GROUP_NAME_IS_EMPTY = 15;
    public static final int CATEGORY_IS_EMPTY = 16;
    public static final int INCOMPLETE_INFORMATION = 17;
    public static final int MAX_GROUP_JOINED_EXCEEDED = 18;

    /* search group by category */
    public static final int NO_SUCH_CATEGORY = 19;

    public static final int ALREADY_IN_PROCESS = 20;

    /* send group chat*/
    public static final int GROUP_NOT_EXIST = 21;
    public static final int NO_ACCESS = 22;


    public static final int WHITEBOARD_NOT_EXIST = 23;
    public static final int TITLE_IS_TOO_LONG = 24;
    public static final int TITLE_IS_EMPTY = 25;

    public static final int ARTICLE_NOT_EXIST = 26;

    public static final int CONTENT_IS_TOO_LONG = 27;
    public static final int CONTENT_IS_EMPTY = 28;

    public static final int TYPE_IS_EMPTY = 29;

    public static final int QUESTION_NOT_EXIST = 30;
    public static final int ANSWER_NOT_EXIST = 31;
    public static final int COMMENT_NOT_EXIST = 32;
    
    public static final int USERNAME_IS_INVALID = 33;
    public static final int EMAIL_IS_INVALID = 34;
    
    public static final int LENGTH_NOT_MATCH = 35;
    
    public static final int REJECTED = 36;
    public static final int ALREADY_IN_GROUP = 37;
    public static final int MESSAGE_IS_EMPTY = 38;
    
    public static final int CONNECTION_CREATED = 39;
    public static final int AUTH_FAIL = 40;
    
    public static final int DISCUSS_NAME_IS_EMPTY = 41;
    public static final int GROUP_ID_NOT_ASSIGNED = 42;
    public static final int DISCUSS_NOT_EXIST = 43;
    

    public static String getMsg(int errorCode){
        return "Error (code:"+errorCode+")";
    }



}
