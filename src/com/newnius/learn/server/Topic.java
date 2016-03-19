package com.newnius.learn.server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.newnius.learn.util.Msg;
import com.newnius.learn.util.TXObject;


public class Topic {
	
	public static int sendBoardAction(Msg msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String sql = "SELECT `username`FROM `join_group` WHERE `group_id` = ?  AND  `accepted`='t'";
					String[] args = { "1" };
					ResultSet rs = DAO.executeQuery(sql, args);

					if (rs != null) {
						List<TXObject> users = new ArrayList<>();
						while (rs.next()) {
							TXObject user = new TXObject();
							user.set("username", rs.getString("username"));
							users.add(user);
						}
						System.out.println(users.size());
						// sql = "SELECT last_insert_id()";
						// rs = DAO.executeQuery(sql, null);

						// if (rs != null && rs.next()) {
						// int cid = rs.getInt(1);
						// Chat newChat = new
						// Chat(cid,chat.getType(),chat.getTopicID(),chat.getGroupID(),chat.getUsername(),chat.getContent(),System.currentTimeMillis());

						// Object obj=new Gson().fromJson(msg.toString(), );

						S2CServer.broadcast(users, msg);
						// }

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		return 32200;
	}
}
