/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newnius.learn.server;

import java.sql.ResultSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newnius.learn.util.ErrorCode;
import com.newnius.learn.util.Msg;
import com.newnius.learn.util.TXObject;

/**
 *
 * @author Newnius
 */
public class User {
	/*
	 * private String username; private String password; private String email;
	 * private String sid;
	 */
	private User() {
	}

	public static Msg login(TXObject user) {
		if (!user.hasKey("username"))
			return new Msg(ErrorCode.USERNAME_IS_EMPTY);
		if (!user.hasKey("password"))
			return new Msg(ErrorCode.PASSWORD_IS_EMPTY);
		if (!validateUsername(user.get("username")))
			return new Msg(ErrorCode.USERNAME_IS_INVALID);

		try {
			String sql = "select password,verified,sid from user where username = ?";
			String[] args = { user.get("username") };
			ResultSet rs = DAO.executeQuery(sql, args);
			if (rs == null || rs.wasNull()) {
				return new Msg(ErrorCode.USER_NOT_EXIST);
			}
			if (rs.next()) {
				if (!cryptPwd(user.get("password")).equals(rs.getString("password"))) {
					return new Msg(ErrorCode.WRONG_PASSWORD);
				} else {
					user.set("sid", rs.getString("sid"));
					user.set("password", "");
					addSignInLog(user);
					return new Msg(ErrorCode.SUCCESS, user);
				}
			} else {
				return new Msg(ErrorCode.USER_NOT_EXIST);
			}

		} catch (Exception e) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static Msg register(TXObject user) {
		if (!user.hasKey("username"))
			return new Msg(ErrorCode.USERNAME_IS_EMPTY);
		if (!user.hasKey("password"))
			return new Msg(ErrorCode.PASSWORD_IS_EMPTY);
		if (!user.hasKey("email"))
			return new Msg(ErrorCode.EMAIL_IS_EMPTY);
		if (!validateUsername(user.get("username")))
			return new Msg(ErrorCode.USERNAME_IS_INVALID);
		if (!validateEmail(user.get("email")))
			return new Msg(ErrorCode.EMAIL_IS_INVALID);
		if (isUsernameReged(user.get("username")))
			return new Msg(ErrorCode.USERNAME_OCCUPIED);
		if (isEmailReged(user.get("email")))
			return new Msg(ErrorCode.EMAIL_OCCUPIED);

		try {
			String sql = "insert into user(`username`, `password`, `email`, `sid`,`reg_time`) values(?, ?, ?, ?, ?)";
			String[] args = { user.get("username"), cryptPwd(user.get("password")), user.get("email"), randomString(32),
					System.currentTimeMillis() + "" };
			int affected_rows = DAO.executeUpdate(sql, args);
			if (affected_rows == 1) {
				return new Msg(ErrorCode.SUCCESS);
			} else {
				return new Msg(ErrorCode.UNKNOWN);
			}
		} catch (Exception ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static Msg forgetPwd(TXObject user) {

		return null;
	}

	public static Msg updatePwd(TXObject user) {
		//
		return null;
	}

	public static Msg resetPwd(TXObject user) {
		//
		return null;
	}

	public static Msg signOut(TXObject user) {
		return null;
	}

	private static boolean validateEmail(String email) {
		if (email == null) 
			return false;
		if (email.length() > 45) 
			return false;
		String regex = "[0-9A-Za-z\\-_\\.]+@[0-9a-z]+\\.edu(.cn)?";
		return email.matches(regex);
	}

	private static boolean validateUsername(String username) {
		if (username == null)
			return false;
		if (username.contains("@"))
			return false;
		if(validateMobile(username))
			return false;
		if (username.length() < 1 || username.length() > 12) 
			return false;
		return true;
	}
	
	private static boolean validateMobile(String mobile) {
		if (mobile == null) 
			return false;
		if (mobile.length() != 11) 
			return false;
		String regex = "[0-9]{11}";
		return mobile.matches(regex);
	}

	private static boolean isUsernameReged(String username) {
		try {
			String sql = "select count(1) from user where `username` = ?";
			String[] args = { username };
			ResultSet rs = DAO.executeQuery(sql, args);
			return rs.next()&&rs.getInt(1)>0;
		} catch (Exception ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
			return true;
		}
	}

	private static boolean isEmailReged(String email) {
		try {
			String sql = "select 1 from `user` where `email` = ?";
			String[] args = { email };
			ResultSet rs = DAO.executeQuery(sql, args);
			return rs.next()&&rs.getInt(1) > 0;

		} catch (Exception ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
			return true;
		}
	}

	private static String cryptPwd(String password) {
		return password;
	}

	private static void addSignInLog(TXObject user) {
		return;
	}

	private static String randomString(int length) {
		String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String str = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			str += chars.charAt(random.nextInt(chars.length()));
		}
		return str;
	}



}
