/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newnius.learn.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newnius.learn.util.TXObject;

/**
 * 
 * @author Newnius
 */
public class C2SServer implements Runnable {

	private ServerSocket server;
	private static HashMap<String, C2SStaff> user2staff = new HashMap<String, C2SStaff>();
	private static Object user2staffMutex = new Object();

	@Override
	public void run() {
		try {
			server = new ServerSocket(Config.getC2SPORT());// 创建服务器套接字
			Logger.getLogger(C2SServer.class.getName()).log(Level.INFO, "C2SServer opened, waiting for client.");
			while (Config.isServerOn()) {
				Socket socket = server.accept();// 等待客户端连接
				new C2SStaff(socket).start();
			}
			server.close();
		} catch (Exception e) {
			Logger.getLogger(C2SServer.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static void addStaff(String username, C2SStaff staff) {
		synchronized (user2staffMutex) {
			if (user2staff.containsKey(username)) {
				System.out.print("tick out " + username);
				user2staff.remove(username);
				
			}
			user2staff.put(username, staff);
			System.out.print("add " + username);
			System.out.print(user2staff.size()+"");
		}
	}

	public static void removeStaff(String username) {
		synchronized (user2staffMutex) {
			if (user2staff.containsKey(username)) {
				user2staff.remove(username);
				System.out.print("tick out " + username);
			}
			System.out.print(user2staff.size()+"");
		}
	}

	public static boolean chkSid(TXObject user) {
		if(user==null || !user.hasKey("username") || !user.hasKey("sid"))
			return false;
		String sid = null;
		synchronized (user2staffMutex) {
			if (user2staff.containsKey(user.get("username"))) {
				sid = user2staff.get(user.get("username")).getSid();
			}
		}
		return sid != null && sid.equals(user.get("sid"));
	}

}
