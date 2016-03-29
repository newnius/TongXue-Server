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

public class Group {

	private Group() {
	}

	public static final int PUBLIC_ACCEPT_ALL = 0;
	public static final int PUBLIC_NEED_PROVE = 1;
	public static final int PUBLIC_REJECT_ALL = 2;
	
	public static List<TXObject> searchGroups(TXObject group){
		if(group==null)
			return new ArrayList<TXObject>();
		else if(group.hasKey("groupID"))
			return searchGroupById(group);
		else if(group.hasKey("username"))
			return searchGroupByUser(group);
		else if(group.hasKey("category"))
			return searchGroupByCategory(group);
		else if(group.hasKey("groupName") && group.hasKey("type-vague"))
			return searchGroupByNameVague(group);
		else if(group.hasKey("groupName"))
			return searchGroupByName(group);
		return new ArrayList<TXObject>();
	}
	

	private static List<TXObject> searchGroupByNameVague(TXObject group) {
		List<TXObject> groups = new ArrayList<>();
		if (group == null)
			return groups;
		if (!group.hasKey("groupName"))
			return groups;
		String sql = "SELECT * FROM `group` WHERE name LIKE ?";
		String[] args = { "%" + group.get("groupName") + "%" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return groups;
		try {
			while (rs.next()) {
				TXObject localGroup = new TXObject();
				localGroup.set("groupID", rs.getInt("group_id"));
				localGroup.set("groupName", rs.getString("name"));
				localGroup.set("category", rs.getInt("category"));
				localGroup.set("icon", rs.getString("icon"));
				localGroup.set("introduction", rs.getString("introduction"));
				localGroup.set("time", rs.getLong("time"));
				localGroup.set("public", rs.getInt("public"));
				groups.add(localGroup);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return groups;
		}
		return groups;
	}

	private static List<TXObject> searchGroupByName(TXObject group) {
		List<TXObject> groups = new ArrayList<>();
		if (group == null)
			return groups;
		if (!group.hasKey("groupName"))
			return groups;
		String sql = "SELECT * FROM `group` WHERE name = ?";
		String[] args = { group.get("groupName") };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null) {
			return groups;
		}
		try {
			while (rs.next()) {
				TXObject localGroup = new TXObject();
				localGroup.set("groupID", rs.getInt("group_id"));
				localGroup.set("groupName", rs.getString("name"));
				localGroup.set("category", rs.getInt("category"));
				localGroup.set("icon", rs.getString("icon"));
				localGroup.set("introduction", rs.getString("introduction"));
				localGroup.set("time", rs.getLong("time"));
				localGroup.set("public", rs.getInt("public"));
				groups.add(localGroup);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return groups;
		}
		return groups;
	}

	// select * from `user` join `join_group` on
	// `user`.username=`join_group`.username join `group` on
	// `group`.group_id=`join_group`.group_id where
	// `join_group`.`username`='pan';

	private static List<TXObject> searchGroupByUser(TXObject user) {
		List<TXObject> groups = new ArrayList<>();
		if (user == null)
			return groups;
		if (!user.hasKey("username"))
			return groups;

		String sql = "select * from `group` join `join_group` on `group`.group_id=`join_group`.group_id where `join_group`.username = ?";
		String[] args = { user.get("username") };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return groups;

		try {
			while (rs.next()) {
				TXObject localGroup = new TXObject();
				localGroup.set("groupID", rs.getInt("group_id"));
				localGroup.set("groupName", rs.getString("name"));
				localGroup.set("category", rs.getInt("category"));
				localGroup.set("icon", rs.getString("icon"));
				localGroup.set("introduction", rs.getString("introduction"));
				localGroup.set("time", rs.getLong("time"));
				localGroup.set("public", rs.getInt("public"));
				groups.add(localGroup);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return groups;
		}
		return groups;
	}

	private static List<TXObject> searchGroupByCategory(TXObject group) {
		List<TXObject> groups = new ArrayList<>();
		if (group == null)
			return groups;
		if (!group.hasKey("category"))
			return groups;

		String sql = "SELECT * FROM `group` WHERE category = ?";
		String[] args = { group.getInt("category") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return null;

		try {
			while (rs.next()) {
				TXObject localGroup = new TXObject();
				localGroup.set("groupID", rs.getInt("group_id"));
				localGroup.set("groupName", rs.getString("name"));
				localGroup.set("category", rs.getInt("category"));
				localGroup.set("icon", rs.getString("icon"));
				localGroup.set("introduction", rs.getString("introduction"));
				localGroup.set("time", rs.getLong("time"));
				localGroup.set("public", rs.getInt("public"));
				groups.add(localGroup);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return groups;
		}
		return groups;
	}

	public static int createGroup(TXObject user, TXObject group) {
		if (user == null)
			return ErrorCode.USER_NOT_EXIST;
		if (group == null)
			return ErrorCode.GROUP_NOT_EXIST;
		if (!user.hasKey("username"))
			return ErrorCode.USERNAME_IS_EMPTY;
		if (!group.hasKey("groupName"))
			return ErrorCode.GROUP_NAME_IS_EMPTY;
		if (!group.hasKey("category"))
			return ErrorCode.INCOMPLETE_INFORMATION;
		if (group.get("groupName").length() < 1 || group.get("groupName").length() > 20)
			return ErrorCode.LENGTH_NOT_MATCH;
		if (!group.hasKey("introduction"))
			group.set("introduction", "");
		if (group.get("introduction").length() > 200)
			return ErrorCode.LENGTH_NOT_MATCH;
		if (!Group.canApplyGroup(user))
			return ErrorCode.MAX_GROUP_JOINED_EXCEEDED;
		if (!group.hasKey("icon"))
			group.set("icon", "default.gif");
		if (!group.hasKey("public"))
			return ErrorCode.INCOMPLETE_INFORMATION;

		try {
			String sql = "INSERT INTO `group` (`name`,`category`,`introduction`,`icon`,`public`,`time`) VALUES(?, ?, ?, ?, ?,?) ";
			String[] args1 = { group.get("groupName"), group.getInt("category") + "", group.get("introduction"),
					group.get("icon"), group.getInt("public") + "", System.currentTimeMillis() + "" };
			int res = DAO.executeUpdate(sql, args1);
			if (res != 1)
				return ErrorCode.UNKNOWN;
			sql = "select @@identity";
			ResultSet rs = DAO.executeQuery(sql, null);
			if (rs.next()) {
				int groupID = rs.getInt(1);
				sql = "insert into `join_group`(`username`,`group_id`,`time`, `accepted`) values(?,?,?,'t')";
				String[] args2 = { user.get("username"), groupID + "", System.currentTimeMillis() + "" };
				res = DAO.executeUpdate(sql, args2);
				if (res != 1)
					return ErrorCode.UNKNOWN;
				else
					return ErrorCode.UNKNOWN;
			} else {
				return ErrorCode.UNKNOWN;
			}

		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return ErrorCode.UNKNOWN;
		}

	}

	private static List<TXObject> searchGroupById(TXObject group) {
		List<TXObject> groups = new ArrayList<>();
		if (group == null)
			return groups;
		if (!group.hasKey("groupID"))
			return groups;

		String sql = "SELECT * FROM `group` WHERE `group_id` = ?";
		String[] args = { group.getInt("groupID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return null;

		try {
			while (rs.next()) {
				TXObject localGroup = new TXObject();
				localGroup.set("groupID", rs.getInt("group_id"));
				localGroup.set("groupName", rs.getString("name"));
				localGroup.set("category", rs.getInt("category"));
				localGroup.set("icon", rs.getString("icon"));
				localGroup.set("introduction", rs.getString("introduction"));
				localGroup.set("time", rs.getLong("time"));
				localGroup.set("public", rs.getInt("public"));
				groups.add(localGroup);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return groups;
		}
		return groups;
	}

	public static int apply(TXObject user, TXObject group) {
		if (user == null)
			return ErrorCode.USER_NOT_EXIST;
		if (group == null)
			return ErrorCode.GROUP_NOT_EXIST;
		if (!user.hasKey("username"))
			return ErrorCode.USERNAME_IS_EMPTY;
		if (Group.canApplyGroup(user))
			return ErrorCode.MAX_GROUP_JOINED_EXCEEDED;
		if (!group.hasKey("groupID"))
			return ErrorCode.GROUP_NOT_EXIST;
		if (isUserMemberOf(user, group))
			return ErrorCode.ALREADY_IN_GROUP;
		try {
			String sql = "SELECT `public` FROM `group` where `group_id`=?";
			String[] args1 = { group.getInt("groupID") + "" };
			ResultSet rs = DAO.executeQuery(sql, args1);
			char accepted = 'f';
			if (rs.next()) {
				if (rs.getInt("public") == PUBLIC_NEED_PROVE) {
					return ErrorCode.SUCCESS;
				} else if (rs.getInt("public") == PUBLIC_REJECT_ALL) {
					accepted = 'f';
				} else {
					accepted = 't';
				}
			}
			sql = "INSERT INTO `join_group` (`username`, `group_id`, `time`, `accepted`) values(?, ?, ?, ?)";
			String[] args2 = { user.get("username"), group.getInt("groupID") + "", System.currentTimeMillis() + "",
					accepted + "" };
			int affected_rows = DAO.executeUpdate(sql, args2);
			if (affected_rows == 1)
				return ErrorCode.SUCCESS;
			else
				return ErrorCode.UNKNOWN;

		} catch (Exception e) {
			Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, e);
			return ErrorCode.UNKNOWN;
		}
	}

	public static int sendGroupMessage(TXObject message) {
		if (message == null)
			return ErrorCode.MESSAGE_IS_EMPTY;
		if (!message.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (!message.hasKey("groupID"))
			return ErrorCode.GROUP_NOT_EXIST;
		if (!message.hasKey("type"))
			return ErrorCode.TYPE_IS_EMPTY;
		if (!message.hasKey("content"))
			return ErrorCode.CONTENT_IS_EMPTY;
		TXObject user = new TXObject();
		user.set("username", message.get("username"));
		TXObject group = new TXObject();
		group.set("groupID", message.getInt("groupID"));
		if (!isUserMemberOf(user, group))
			return ErrorCode.NO_ACCESS;

		message.set("time", System.currentTimeMillis());

		try {
			String sql = "INSERT INTO `group_chat` (`group_id`, `username`, `type`, `content`, `time`) VALUES( ?, ?, ?, ?, ?)";
			String[] args1 = { message.getInt("groupID") + "", message.get("username"), message.getInt("type") + "",
					message.get("content"), message.getLong("time") + "" };
			int affected_rows = DAO.executeUpdate(sql, args1);
			if (affected_rows > 0) {
				sql = "SELECT last_insert_id()";
				ResultSet rs = DAO.executeQuery(sql, null);
				rs.next();
				int mid = rs.getInt(1);
				message.set("mid", mid);
				S2CServer.broadcast(getGroupMembers(group), new Msg(RequestCode.NEW_GROUP_MESSAGE, message));
				return ErrorCode.SUCCESS;
			} else {
				return ErrorCode.UNKNOWN;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorCode.UNKNOWN;
		}
	}

	public static List<TXObject> getGroupMessage(TXObject user, TXObject group) {
		List<TXObject> messages = new ArrayList<>();
		if (user == null)
			return messages;
		if (group == null)
			return messages;
		if (!user.hasKey("username"))
			return messages;
		if (!group.hasKey("groupID"))
			return messages;
		if (!isUserMemberOf(user, group))
			return messages;

		try {
			ResultSet rs;
			if (group.hasKey("gt-mid")) {
				String sql = "SELECT * FROM `group_chat` WHERE `group_id` = ? AND `cid`> ? LIMIT 20";
				String[] args = { group.getInt("groupID") + "", group.getInt("gt-mid") + "" };
				rs = DAO.executeQuery(sql, args);
			} else {
				String sql = "SELECT * FROM `group_chat` WHERE `group_id` = ? ORDER BY cid DESC LIMIT 20";
				String[] args = { group.getInt("groupID") + "" };
				rs = DAO.executeQuery(sql, args);
			}

			if (rs == null || rs.wasNull()) {
				return messages;
			}
			while (rs.next()) {
				TXObject message = new TXObject();
				message.set("mid", rs.getInt("cid"));
				message.set("type", rs.getInt("type"));
				message.set("content", rs.getString("content"));
				message.set("username", rs.getString("username"));
				message.set("groupID", rs.getInt("group_id"));
				message.set("time", rs.getLong("time"));
				messages.add(message);
			}
			rs.close();
			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			return messages;
		}
	}

	public static List<TXObject> getGroupMembers(TXObject group) {
		List<TXObject> users = new ArrayList<>();
		try {
			String sql = "SELECT username FROM `join_group` WHERE `group_id` = ? AND `accepted` = 't' ";
			String[] args1 = { group.getInt("groupID") + "" };
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

	public static List<TXObject> getGroupMembersIncludingApplyers(TXObject group) {
		List<TXObject> users = new ArrayList<>();
		try {
			String sql = "SELECT username FROM `join_group` WHERE `group_id` = ?";
			String[] args1 = { group.getInt("groupID") + "" };
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

	public static boolean isUserMemberOf(TXObject user, TXObject group) {
		if (user == null || !user.hasKey("username"))
			return false;
		if (group == null || !group.hasKey("groupID"))

			try {
				String sql = "SELECT count(1) FROM `join_group` WHERE `group_id` = ?  AND `username` = ? AND `accepted`='t' ";
				String[] args = { group.getInt("groupID") + "", user.get("username") };
				ResultSet rs = DAO.executeQuery(sql, args);
				int cnt = 0;
				if(rs.next())
					cnt = rs.getInt(0);
				return cnt == 1;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		return false;
	}

	public static int quitGroup(TXObject user, TXObject group) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (group == null || !group.hasKey("groupID"))
			return ErrorCode.GROUP_NOT_EXIST;
		if (!isUserMemberOf(user, group))
			return ErrorCode.NO_ACCESS;

		/*
		 * do delete
		 */

		return ErrorCode.SUCCESS;
	}

	public static boolean canApplyGroup(TXObject user) {
		try {
			String sql = "SELECT count(1) FROM `join_group` WHERE `username` = ? ";
			String[] args = { user.get("username") };
			ResultSet rs = DAO.executeQuery(sql, args);
			int groupCnt = Config.getMaxGroupPerUser();
			if(rs.next())
				groupCnt = rs.getInt(0);
			return  groupCnt >= Config.getMaxGroupPerUser();
		} catch (Exception ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

}
