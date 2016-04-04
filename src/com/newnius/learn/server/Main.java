/*
 * 
 * entry of server
 * load config params and start C2SServer && S2CServer 
 */
package com.newnius.learn.server;

/**
 * 
 * @author Newnius
 */
public class Main {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		init();
		Config.startServer();
		new Thread(new C2SServer()).start();
		new Thread(new S2CServer()).start();
	}
	
	private static void init(){
		//check environment and something else
	    //load config
	    loadConfig();
	}
	
	private static void loadConfig(){
		//load params from file
	}

}
