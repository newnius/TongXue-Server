package com.newnius.learn.util;

/**
 *
 * @author Newnius
 */
public class Msg {
    private final int code;
    private String msg;
    private Object obj;

    public Msg(int code){
        this.code = code;
    }

    public Msg(int code, Object obj) {
        this.code = code;
        this.obj = obj;
    }

    public int getCode() {
        return code;
    }

    public Object getObj() {
        return obj;
    }
    
    public void setObj(Object obj){
    	this.obj = obj;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
