package com.newnius.learn.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newnius.learn.util.ErrorCode;
import com.newnius.learn.util.Msg;
import com.newnius.learn.util.RequestCode;
import com.newnius.learn.util.TXObject;

public class WhiteBoard {
	public static Msg sendBoardAction(TXObject action, TXObject currentUser){
		if(1==1){
			TXObject discuss = new TXObject();
			discuss.set("discussID", action.getInt("discussID"));
			List<TXObject> users = Discuss.getDiscussMembers(discuss);
			S2CServer.broadcast(users, new Msg(RequestCode.NEW_BOARD_ACTION, action));
			return new Msg(ErrorCode.SUCCESS);
		}
		
		
		if (action == null)
			return new Msg(ErrorCode.INCOMPLETE_INFORMATION);
		if (!action.hasKey("discussID"))
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		if (currentUser==null || !currentUser.hasKey("username"))
			return new Msg(ErrorCode.USER_NOT_EXIST);
		Msg msg = Discuss.getDiscussById(action);
		if (msg.getCode() != ErrorCode.SUCCESS)
			return new Msg(ErrorCode.DISCUSS_NOT_EXIST);
		@SuppressWarnings("unchecked")
		TXObject discusstmp = ((List<TXObject>) msg.getObj()).get(0);
		if (!discusstmp.get("controller").equals(action.get("currentUser")))
			return new Msg(ErrorCode.NO_ACCESS);

		try {
			String sql = "INSERT INTO `board_action` ( `discuss_id`, `username`, `time`) VALUES (?, ?, ?)";
			String[] args = { action.getInt("discussID") + "", currentUser.get("username"),
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
	
	public static Msg getWhiteBoardActions(TXObject discuss, TXObject currentUser) {
		return new Msg(ErrorCode.SUCCESS, new ArrayList<TXObject>());
	}
	
	private void saveCurveAction(TXObject action){
		
	}
	
	private void saveEraseAction(TXObject action){
		
	}
	

}
