package com.newnius.learn.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.newnius.learn.util.Msg;
import com.newnius.learn.util.TXObject;

public class S2CServer implements Runnable {

	private static HashMap<String, S2CStaff> user2staff = new HashMap<String, S2CStaff>();
	private static ServerSocket server;
	private static Object user2staffMutex = new Object();

	@Override
	public void run() {
		try {
			server = new ServerSocket(Config.getS2CPORT());// 创建服务器套接字
			Logger.getLogger(S2CServer.class.getName()).log(Level.INFO, "S2CServer opened, waiting for client.");
			while (Config.isServerOn()) {
				Socket socket = server.accept();// 等待客户端连接
				new S2CStaff(socket).start();
			}
			server.close();
		} catch (Exception e) {
			Logger.getLogger(S2CServer.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static void broadcast(List<TXObject> users, Msg msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (user2staffMutex) {
					try {
						String sendStr = new Gson().toJson(msg);
						Logger.getLogger(S2CServer.class.getName()).log(Level.INFO, "S2C sent: " + sendStr);

						for (TXObject user : users) {
							Logger.getLogger(S2CServer.class.getName()).log(Level.INFO, "to:" + user.get("username"));
							S2CStaff staff = user2staff.get(user.get("username"));
							if (staff == null)
								continue;
							staff.send(sendStr);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						//Logger.getLogger(S2CServer.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}).start();
	}
	
	public static void broadcast(List<TXObject> users, String sendStr) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (user2staffMutex) {
					try {
						Logger.getLogger(S2CServer.class.getName()).log(Level.INFO, "S2C sent: " + sendStr);

						for (TXObject user : users) {
							Logger.getLogger(S2CServer.class.getName()).log(Level.INFO, "to:" + user.get("username"));
							S2CStaff staff = user2staff.get(user.get("username"));
							if (staff == null)
								continue;
							staff.send(sendStr);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						//Logger.getLogger(S2CServer.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}).start();
	}

	public static void addStaff(String username, S2CStaff staff) {
		synchronized (user2staffMutex) {
			if (user2staff.containsKey(username)) {
				user2staff.remove(username);
				System.out.print("tick out " + username);
			}
			user2staff.put(username, staff);
			System.out.print("add " + username);
			
//			List<User> u = new ArrayList<>();
//			u.add(new User(username, null, null));
//			S2CServer.broadcast(u, new Msg(91200, null));
		}
	}

	public static void removeStaff(String username) {
		synchronized (user2staffMutex) {
			if (user2staff.containsKey(username)) {
				user2staff.remove(username);
				System.out.print("tick out " + username);
			}
		}
	}

	public static boolean chkSid(TXObject user) {
		return C2SServer.chkSid(user);
	}
}
