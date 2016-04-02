package com.newnius.learn.server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newnius.learn.util.ErrorCode;
import com.newnius.learn.util.Msg;
import com.newnius.learn.util.RequestCode;
import com.newnius.learn.util.TXLogger;
import com.newnius.learn.util.TXObject;

public class WhiteBoard {
	public static final int ACTION_TYPE_CURVE = 1;
	public static final int ACTION_TYPE_ERASER = 2;
	public static final int ACTION_TYPE_LINE = 3;
	public static final int ACTION_TYPE_CIRCLE = 4;
	public static final int ACTION_TYPE_CLEAR = 5;
	public static final int ACTION_TYPE_REDO = 6;
	public static final int ACTION_TYPE_UNDO = 7;
	public static final int ACTION_TYPE_COLOR = 8;
	public static final int ACTION_TYPE_WEIGHT = 9;
	public static final int ACTION_TYPE_TEXT = 10;
	public static final int ACTION_TYPE_IMAGE = 11;
	public static final int ACTION_TYPE_VIDEO = 12;
	private static final String TAG = "WhiteBoard";

	public static Msg sendBoardAction(TXObject action, TXObject currentUser) {
		if (action == null)
			return new Msg(ErrorCode.INCOMPLETE_INFORMATION);
		if (!action.hasKey("type"))
			return new Msg(ErrorCode.INCOMPLETE_INFORMATION);
		if (!action.hasKey("discussID"))
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		if (currentUser == null || !currentUser.hasKey("username"))
			return new Msg(ErrorCode.USER_NOT_EXIST);
		Msg msg = Discuss.getDiscussById(action);
		if (msg.getCode() != ErrorCode.SUCCESS)
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		TXObject discusstmp = (TXObject) msg.getObj();
		if (!discusstmp.get("controller").equals(currentUser.get("username")))
			return new Msg(ErrorCode.NO_ACCESS);

		try {
			String sql = "INSERT INTO `board_action` ( `type`, `discuss_id`, `username`, `time`) VALUES (?, ?, ?, ?)";
			String[] args = { action.getInt("type") + "", action.getInt("discussID") + "", currentUser.get("username"),
					System.currentTimeMillis() + "" };
			int cnt = DAO.executeUpdate(sql, args);
			if (cnt == 1) {
				sql = "select @@identity";
				ResultSet rs = DAO.executeQuery(sql, null);
				if (rs.next()) {
					int actionID = rs.getInt(1);
					action.set("actionID", actionID);
					if (saveSubAction(action)) {
						TXObject discuss = new TXObject();
						discuss.set("discussID", action.getInt("discussID"));
						List<TXObject> users = Discuss.getDiscussMembers(discuss);
						S2CServer.broadcast(users, new Msg(RequestCode.NEW_BOARD_ACTION, action));
						return new Msg(ErrorCode.SUCCESS);
					}
				}
			}
			return new Msg(ErrorCode.UNKNOWN);
		} catch (Exception e) {
			Logger.getLogger(Discuss.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
	}

	public static Msg getWhiteBoardActions(TXObject discuss, TXObject currentUser) {
		List<TXObject> actions = new ArrayList<>();
		if (discuss == null || !discuss.hasKey("discussID"))
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		if(currentUser==null || !currentUser.hasKey("username"))
			return new Msg(ErrorCode.USER_NOT_EXIST);
		if (!Discuss.isUserMemberOf(currentUser, discuss))
			return new Msg(ErrorCode.NO_ACCESS);	
		
		String sql = "SELECT * FROM `board_action` WHERE `discuss_id` = ?";
		String[] args = { discuss.getInt("discussID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return new Msg(ErrorCode.UNKNOWN);
		try {
			while (rs.next()) {
				TXObject localAction = new TXObject();
				localAction.set("actionID", rs.getInt("action_id"));
				localAction.set("type", rs.getInt("type"));
				localAction.set("discussID", rs.getInt("discuss_id"));
				localAction.set("username", rs.getString("username"));
				localAction.set("time", rs.getLong("time"));
				getSubAction(localAction);
				actions.add(localAction);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(Discuss.class.getName()).log(Level.SEVERE, null, e);
			return new Msg(ErrorCode.UNKNOWN);
		}
			return new Msg(ErrorCode.SUCCESS, actions);
	}
	
	private static TXObject getSubAction(TXObject action){
		switch (action.getInt("type")) {
		case WhiteBoard.ACTION_TYPE_CURVE:
			return getCurveAction(action);
		case WhiteBoard.ACTION_TYPE_ERASER:
			return getEraseAction(action);
		case WhiteBoard.ACTION_TYPE_CLEAR:
			return action;
		case WhiteBoard.ACTION_TYPE_COLOR:
			return getColorAction(action);
		case WhiteBoard.ACTION_TYPE_WEIGHT:
			return getWeightAction(action);
		case WhiteBoard.ACTION_TYPE_REDO:
			return action;
		case WhiteBoard.ACTION_TYPE_UNDO:
			return action;
		case WhiteBoard.ACTION_TYPE_LINE:
			return getLineAction(action);
		default:
			return action;
		}
	}
	

	private static boolean saveSubAction(TXObject action) {
		switch (action.getInt("type")) {
		case WhiteBoard.ACTION_TYPE_CURVE:
			return saveCurveAction(action);
		case WhiteBoard.ACTION_TYPE_ERASER:
			return saveEraseAction(action);
		case WhiteBoard.ACTION_TYPE_CLEAR:
			return true;
		case WhiteBoard.ACTION_TYPE_COLOR:
			return saveColorAction(action);
		case WhiteBoard.ACTION_TYPE_WEIGHT:
			return saveWeightAction(action);
		case WhiteBoard.ACTION_TYPE_REDO:
			return true;
		case WhiteBoard.ACTION_TYPE_UNDO:
			return true;
		case WhiteBoard.ACTION_TYPE_LINE:
			return saveLineAction(action);
		default:
			return false;
		}
	}

	private static boolean saveCurveAction(TXObject action) {
		if(action == null || !action.hasKey("actionID"))
			return false;
		if (!action.hasKey("type") || action.getInt("type") != WhiteBoard.ACTION_TYPE_CURVE)
			return false;
		if (!action.hasKey("points"))
			return false;
		String sql = "INSERT INTO `board_action_curve` ( `points`, `action_id`) VALUES (?, ?)";
		String[] args = { action.get("points"), action.getInt("actionID") + "" };
		int cnt = DAO.executeUpdate(sql, args);
		if (cnt == 1)
			return true;
		TXLogger.warn(TAG, action.get("points"));
		return false;
	}
	
	private static TXObject getCurveAction(TXObject action){
		if(action==null || !action.hasKey("actionID"))
			return action;
		String sql = "SELECT * FROM `board_action_curve` WHERE `action_id` = ?";
		String[] args = { action.getInt("actionID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return action;
		try {
			if (rs.next()) {
				action.set("points", rs.getString("points"));
			}
			rs.close();
		} catch (Exception ex) {
			TXLogger.error(TAG, ex);
		}
		return action;
	}

	private static boolean saveEraseAction(TXObject action) {
		if(action == null || !action.hasKey("actionID"))
			return false;
		if (!action.hasKey("type") || action.getInt("type") != WhiteBoard.ACTION_TYPE_ERASER)
			return false;
		if (!action.hasKey("points"))
			return false;
		String sql = "INSERT INTO `board_action_erase` ( `points`, `action_id`) VALUES (?, ?)";
		String[] args = { action.get("points"), action.getInt("actionID") + "" };
		int cnt = DAO.executeUpdate(sql, args);
		if (cnt == 1)
			return true;
		TXLogger.warn(TAG, action.get("points"));
		return false;
	}
	
	private static TXObject getEraseAction(TXObject action){
		if(action==null || !action.hasKey("actionID"))
			return action;
		String sql = "SELECT * FROM `board_action_erase` WHERE `action_id` = ?";
		String[] args = { action.getInt("actionID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return action;
		try {
			if (rs.next()) {
				action.set("points", rs.getString("points"));
			}
			rs.close();
		} catch (Exception ex) {
			TXLogger.error(TAG, ex);
		}
		return action;
	}

	private static boolean saveColorAction(TXObject action) {
		if(action == null || !action.hasKey("actionID"))
			return false;
		if (!action.hasKey("type") || action.getInt("type") != WhiteBoard.ACTION_TYPE_COLOR)
			return false;
		if (!action.hasKey("color"))
			return false;
		String sql = "INSERT INTO `board_action_color` ( `color`, `action_id`) VALUES (?, ?)";
		String[] args = { action.getInt("color") + "", action.getInt("actionID") + "" };
		int cnt = DAO.executeUpdate(sql, args);
		if (cnt == 1)
			return true;
		TXLogger.warn(TAG, "Save Subaction fail " + action.getInt("actionID"));
		return false;
	}
	
	private static TXObject getColorAction(TXObject action){
		if(action==null || !action.hasKey("actionID"))
			return action;
		String sql = "SELECT * FROM `board_action_color` WHERE `action_id` = ?";
		String[] args = { action.getInt("actionID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return action;
		try {
			if (rs.next()) {
				action.set("color", rs.getInt("color"));
			}
			rs.close();
		} catch (Exception ex) {
			TXLogger.error(TAG, ex);
		}
		return action;
	}

	private static boolean saveWeightAction(TXObject action) {
		if(action == null || !action.hasKey("actionID"))
			return false;
		if (!action.hasKey("type") || action.getInt("type") != WhiteBoard.ACTION_TYPE_WEIGHT)
			return false;
		if (!action.hasKey("weight"))
			return false;
		String sql = "INSERT INTO `board_action_weight` ( `weight`, `action_id`) VALUES (?, ?)";
		String[] args = { action.getInt("weight") + "", action.getInt("actionID") + "" };
		int cnt = DAO.executeUpdate(sql, args);
		if (cnt == 1)
			return true;
		TXLogger.warn(TAG, "Save Subaction fail " + action.getInt("actionID"));
		return false;
	}
	
	private static TXObject getWeightAction(TXObject action){
		if(action==null || !action.hasKey("actionID"))
			return action;
		String sql = "SELECT * FROM `board_action_weight` WHERE `action_id` = ?";
		String[] args = { action.getInt("actionID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return action;
		try {
			if (rs.next()) {
				action.set("weight", rs.getInt("weight"));
			}
			rs.close();
		} catch (Exception ex) {
			TXLogger.error(TAG, ex);
		}
		return action;
	}

	private static boolean saveLineAction(TXObject action) {
		if(action == null || !action.hasKey("actionID"))
			return false;
		if (!action.hasKey("type") || action.getInt("type") != WhiteBoard.ACTION_TYPE_LINE)
			return false;
		if (!action.hasKey("startX") || !action.hasKey("startY") || !action.hasKey("endX") || !action.hasKey("endY"))
			return false;
		String sql = "INSERT INTO `board_action_line` ( `startX`, `startY`, `endX`, `endY`, `action_id`) VALUES (?, ?, ?, ?, ?)";
		String[] args = { action.getFloat("startX") + "", action.getFloat("startY") + "", action.getFloat("endX") + "",
				action.getFloat("endY") + "", action.getInt("actionID") + "" };
		int cnt = DAO.executeUpdate(sql, args);
		if (cnt == 1)
			return true;
		TXLogger.warn(TAG, "Save Subaction fail " + action.getInt("actionID"));
		return false;
	}
	
	private static TXObject getLineAction(TXObject action){
		if(action==null || !action.hasKey("actionID"))
			return action;
		String sql = "SELECT * FROM `board_action_line` WHERE `action_id` = ?";
		String[] args = { action.getInt("actionID") + "" };
		ResultSet rs = DAO.executeQuery(sql, args);
		if (rs == null)
			return action;
		try {
			if (rs.next()) {
				action.set("startX", rs.getFloat("startX"));
				action.set("startY", rs.getFloat("startY"));
				action.set("endX", rs.getFloat("endX"));
				action.set("endY", rs.getFloat("endY"));
			}
			rs.close();
		} catch (Exception ex) {
			TXLogger.error(TAG, ex);
		}
		return action;
	}

}
