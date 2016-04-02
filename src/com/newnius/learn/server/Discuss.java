package com.newnius.learn.server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newnius.learn.util.ErrorCode;
import com.newnius.learn.util.Msg;
import com.newnius.learn.util.RequestCode;
import com.newnius.learn.util.TXObject;

public class Discuss {
	public static Msg createDiscuss(TXObject discuss, TXObject currentUser) {
		if (!discuss.hasKey("name"))
			return new Msg(ErrorCode.DISCUSS_NAME_IS_EMPTY);
		if (!discuss.hasKey("groupID"))
			return new Msg(ErrorCode.GROUP_ID_NOT_ASSIGNED);
		if (currentUser == null)
			return new Msg(ErrorCode.USER_NOT_EXIST);
		discuss.set("controller", currentUser.get("username"));

		try {
			String sql = "INSERT INTO `discuss` (`name`, `group_id`, `controller`, `time`) VALUES (?, ?, ?, ?)";
			String[] args = { discuss.get("name"), discuss.getInt("groupID") + "", discuss.get("controller"),
					System.currentTimeMillis() + "" };
			int cnt = DAO.executeUpdate(sql, args);
			if (cnt == 1) {
				sql = "select @@identity";
				ResultSet rs = DAO.executeQuery(sql, null);
				int discussID = -1;
				if (rs.next())
					discussID = rs.getInt(1);
				discuss.set("discussID", discussID);
				return new Msg(ErrorCode.SUCCESS, discuss);
			} else {
				return new Msg(ErrorCode.UNKNOWN);
			}
		} catch (Exception e) {
			Logger.getLogger(Discuss.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static Msg getAllDiscusses(TXObject discuss) {
		List<TXObject> discusses = new ArrayList<>();
		if (discuss == null)
			discuss = new TXObject();
		int offset = 0;
		if (discuss.hasKey("page-no"))
			offset = discuss.getInt("page-no") * 10;
		if (offset < 0)
			offset = 0;
		String sql = "SELECT * FROM `discuss` ORDER BY `discuss_id` DESC LIMIT " + offset + ", 10";
		String[] args = {};
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return new Msg(ErrorCode.UNKNOWN);
		try {
			while (rs.next()) {
				TXObject localDiscuss = new TXObject();
				localDiscuss.set("discussID", rs.getInt("discuss_id"));
				localDiscuss.set("discussName", rs.getString("name"));
				localDiscuss.set("category", rs.getInt("category"));
				localDiscuss.set("introduction", rs.getString("introduction"));
				localDiscuss.set("public", rs.getInt("public"));
				localDiscuss.set("groupID", rs.getInt("group_id"));
				localDiscuss.set("status", rs.getInt("status"));
				localDiscuss.set("controller", rs.getString("controller"));
				localDiscuss.set("time", rs.getLong("time"));
				discusses.add(localDiscuss);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
		return new Msg(ErrorCode.SUCCESS, discusses);
	}

	public static Msg joinDiscuss(TXObject discuss, TXObject currentUser) {
		if (!discuss.hasKey("discussID"))
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		if (currentUser == null || ! currentUser.hasKey("username"))
			return new Msg(ErrorCode.USER_NOT_EXIST);

		try {
			String sql = "INSERT INTO `discuss_member` ( `discuss_id`, `username`, `time`) VALUES (?, ?, ?)";
			String[] args = { discuss.getInt("discussID") + "", currentUser.get("username"),
					System.currentTimeMillis() + "" };
			int cnt = DAO.executeUpdate(sql, args);
			if (cnt == 1) {
				return new Msg(ErrorCode.SUCCESS);
			} else {
				return new Msg(ErrorCode.UNKNOWN);
			}
		} catch (Exception e) {
			Logger.getLogger(Discuss.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static Msg quitDiscuss(TXObject discuss, TXObject currentUser) {
		if (!discuss.hasKey("discussID"))
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		if (currentUser == null || ! currentUser.hasKey("username"))
			return new Msg(ErrorCode.USER_NOT_EXIST);

		try {
			String sql = "DELETE FROM `discuss_member` WHERE `username` = ?";
			String[] args = { currentUser.get("username") };
			int cnt = DAO.executeUpdate(sql, args);
			if (cnt == 1) {
				return new Msg(ErrorCode.SUCCESS);
			} else {
				return new Msg(ErrorCode.UNKNOWN);
			}
		} catch (Exception e) {
			Logger.getLogger(Discuss.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static Msg getDiscussById(TXObject discuss) {
		TXObject res = null;
		if (discuss == null || !discuss.hasKey("discussID"))
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);

		String sql = "SELECT * FROM `discuss` WHERE `discuss_id` = ?";
		String[] args = { discuss.getInt("discussID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return new Msg(ErrorCode.UNKNOWN);

		try {
			if (rs.next()) {
				TXObject localDiscuss = new TXObject();
				localDiscuss.set("discussID", rs.getInt("discuss_id"));
				localDiscuss.set("name", rs.getString("name"));
				localDiscuss.set("category", rs.getInt("category"));
				localDiscuss.set("introduction", rs.getString("introduction"));
				localDiscuss.set("time", rs.getLong("time"));
				localDiscuss.set("public", rs.getInt("public"));
				localDiscuss.set("groupID", rs.getInt("group_id"));
				localDiscuss.set("status", rs.getInt("status"));
				localDiscuss.set("controller", rs.getString("controller"));
				res = localDiscuss;
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Discuss.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
		if (res != null)
			return new Msg(ErrorCode.SUCCESS, res);
		else
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
	}

	public static Msg sendDiscussMessage(TXObject message,  TXObject currentUser) {
		if (message == null)
			return new Msg(ErrorCode.MESSAGE_IS_EMPTY);
		if (!message.hasKey("discussID"))
			return new Msg(ErrorCode.GROUP_NOT_EXIST);
		if (!message.hasKey("type"))
			return new Msg(ErrorCode.TYPE_IS_EMPTY);
		if (!message.hasKey("content"))
			return new Msg(ErrorCode.CONTENT_IS_EMPTY);
		if (currentUser == null || !currentUser.hasKey("username"))
			return new Msg(ErrorCode.USER_NOT_EXIST);
		TXObject discuss = new TXObject();
		discuss.set("discussID", message.getInt("discussID"));
		if (!isUserMemberOf(currentUser, discuss))
			return new Msg(ErrorCode.NO_ACCESS);

		message.set("time", System.currentTimeMillis());

		try {
			String sql = "INSERT INTO `discuss_message` (`discuss_id`, `username`, `type`, `content`, `time`) VALUES( ?, ?, ?, ?, ?)";
			String[] args1 = { message.getInt("discussID") + "", currentUser.get("username"), message.getInt("type") + "",
					message.get("content"), message.getLong("time") + "" };
			int affected_rows = DAO.executeUpdate(sql, args1);
			if (affected_rows > 0) {
				sql = "SELECT last_insert_id()";
				ResultSet rs = DAO.executeQuery(sql, null);
				rs.next();
				int mid = rs.getInt(1);
				message.set("messageID", mid);
				S2CServer.broadcast(getDiscussMembers(message), new Msg(RequestCode.NEW_BOARD_MESSAGE, message));
				return new Msg(ErrorCode.SUCCESS);
			} else {
				return new Msg(ErrorCode.UNKNOWN);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static Msg getDiscussMessage( TXObject discuss, TXObject currentUser) {
		List<TXObject> messages = new ArrayList<>();
		if (currentUser == null || !currentUser.hasKey("username"))
			return new Msg(ErrorCode.USER_NOT_EXIST);
		if (discuss == null)
			return new Msg(ErrorCode.INCOMPLETE_INFORMATION);
		if (!discuss.hasKey("discussID"))
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		if (!isUserMemberOf(currentUser, discuss))
			return new Msg(ErrorCode.NO_ACCESS);

		try {
			ResultSet rs;
			String sql = "SELECT * FROM `discuss_message` WHERE `discuss_id` = ? ORDER BY message_id DESC LIMIT 30";
			String[] args = { discuss.getInt("discussID") + "" };
			rs = DAO.executeQuery(sql, args);
			
			if (rs == null || rs.wasNull()) {
				return new Msg(ErrorCode.UNKNOWN);
			}
			while (rs.next()) {
				TXObject message = new TXObject();
				message.set("messageID", rs.getInt("message_id"));
				message.set("type", rs.getInt("type"));
				message.set("content", rs.getString("content"));
				message.set("username", rs.getString("username"));
				message.set("time", rs.getLong("time"));
				messages.add(message);
			}
			rs.close();
			return new Msg(ErrorCode.SUCCESS, messages);
		} catch (Exception e) {
			e.printStackTrace();
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static boolean isUserMemberOf(TXObject user, TXObject discuss) {
		if (user == null || !user.hasKey("username"))
			return false;
		if (discuss == null || !discuss.hasKey("discussID"))
			return false;

		try {
			String sql = "SELECT count(1) FROM `discuss_member` WHERE `discuss_id` = ?  AND `username` = ? ";
			String[] args = { discuss.getInt("discussID") + "", user.get("username") };
			ResultSet rs = DAO.executeQuery(sql, args);
			int cnt = 0;
			if (rs.next())
				cnt = rs.getInt(1);
			return cnt == 1;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static List<TXObject> getDiscussMembers(TXObject discuss) {
		List<TXObject> users = new ArrayList<>();
		try {
			String sql = "SELECT username FROM `discuss_member` WHERE `discuss_id` = ? ";
			String[] args1 = { discuss.getInt("discussID") + "" };
			ResultSet rs = DAO.executeQuery(sql, args1);

			if (rs == null || rs.wasNull()) {
				return users;
			}
			while (rs.next()) {
				TXObject user = new TXObject();
				user.set("username", rs.getString("username"));
				users.add(user);
			}
			rs.close();
			return users;
		} catch (Exception e) {
			e.printStackTrace();
			return users;
		}
	}

}
