package com.newnius.learn.server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Newnius
 */
public class Config {
    private static boolean isServerOn = false;
    private static final int maxGroupPerUser = 30;
	private static final int C2SPORT = 7096;  // accept connect from client, one response for one request
	private static final int S2CPORT = 32719; // client registers here, push msg from server
	private static final int FILEPORT = 32720; // port for file transfer



	public static boolean isServerOn() {
        return isServerOn;
    }
    
    
    public static void stopServer(){
    	isServerOn = false;
    }
    
    public static void startServer(){
    	isServerOn = true;
    }

    
	public static int getMaxGroupPerUser() {
		return maxGroupPerUser;
	}
	
	
	public static int getC2SPORT() {
		return C2SPORT;
	}
	
	
	public static int getS2CPORT() {
		return S2CPORT;
	}
	

    public static int getFileport() {
		return FILEPORT;
	}
      
    
    
}
