package com.newnius.learn.util;

/**
 * Created by newnius on 16-3-16.
 */
public class RequestCode {
    public static final int LOGIN = 0;
    public static final int REGISTER = 1;

    public static final int SEARCH_GROUP = 2;
    public static final int SEARCH_GROUP_BY_USER = 3;
    public static final int CREATE_GROUP = 4;
    public static final int UPDATE_GROUP = 5;
    public static final int DISMISS_GROUP = 6;

    public static final int APPLY_GROUP = 7;
    public static final int QUIT_GROUP = 8;

    public static final int SEND_GROUP_MESSAGE = 9;
    public static final int GET_GROUP_MESSAGE = 10;

    public static final int SEARCH_ARTICLE = 11;
    public static final int POST_ARTICLE = 12;
    public static final int UPDATE_ARTICLE = 13;
    public static final int DELETE_ARTICLE = 14;

    public static final int SEARCH_ARTICLE_COMMENT = 15;
    public static final int COMMENT_AT_ARTICLE = 16;
    public static final int UPDATE_ARTICLE_COMMENT = 17;
    public static final int DELETE_ARTICLE_COMMENT = 18;

    public static final int ASK_QUESTION = 19;
    public static final int SEARCH_QUESTION = 20;
    public static final int UPDATE_QUESTION = 21;
    public static final int DELETE_QUESTION = 22;

    public static final int ANSWER_QUESTION = 23;
    public static final int SEARCH_QUESTION_ANSWER = 24;
    public static final int UPDATE_QUESTION_ANSWER = 25;
    public static final int DELETE_QUESTION_ANSWER = 26;
    
    public static final int SEND_WHITEBOARD_ACTION = 27;
    public static final int GET_WHITEBOARD_ACTION = 28;
    
    public static final int CREATE_DISCUSS = 29;
    public static final int JOIN_DISCUSS = 30;
    public static final int QUIT_DISCUSS = 31;
    public static final int SEND_BOARD_MESSAGE = 32;
    public static final int GET_BOARD_MESSAGE = 33;
    public static final int GET_ALL_DISCUSSES = 34;
    public static final int GET_DISCUSS_BY_DISCUSS_ID = 35;
    
    /* received code */
    public static final int NEW_GROUP_MESSAGE = 1000;
    public static final int NEW_BOARD_MESSAGE = 1001;
    public static final int NEW_BOARD_ACTION = 1002;
    
}
