/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newnius.learn.server;

import com.google.gson.Gson;
import com.newnius.learn.util.ErrorCode;
import com.newnius.learn.util.Msg;
import com.newnius.learn.util.RequestCode;
import com.newnius.learn.util.TXObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Newnius
 */
public class C2SStaff extends Thread {

	private Socket socket = null;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private TXObject currentUser = null;
	private TXObject discuss = null;

	public C2SStaff(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		Msg res = new Msg(ErrorCode.UNKNOWN, null);
		// TXObject user;
		TXObject group;
		TXObject message;
		TXObject article;
		TXObject comment;
		TXObject question;
		TXObject answer;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));// 获得客户端的输入流
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);// 获得客户端输出流)
			if (socket.isConnected()) {
				Logger.getLogger(C2SStaff.class.getName()).log(Level.INFO,
						"Client " + socket.getInetAddress().getHostAddress() + "  connected");
			}

			while (Config.isServerOn() && socket.isConnected()) {
				String str = reader.readLine();

				if (str == null) {
					break;
				}
				// str = new String(str.getBytes(), "UTF-8");
				// System.out.println(str);

				Logger.getLogger(C2SStaff.class.getName()).log(Level.INFO, "C2SStaff received：" + str);

				Msg msg = new Gson().fromJson(str, Msg.class);
				String tmp = new Gson().toJson(msg.getObj());
				// forbid request without login || register while not loged
				if (msg.getCode() != RequestCode.LOGIN && msg.getCode() != RequestCode.REGISTER
						&& currentUser == null) {
					writer.println(new Gson().toJson(new Msg(ErrorCode.NOT_LOGED)));
					continue;
				}

				// hand out requests
				switch (msg.getCode()) {

				case RequestCode.LOGIN:// login
					TXObject tmpuser = new Gson().fromJson(tmp, TXObject.class);
					res = User.login(tmpuser);
					if (res.getCode() == ErrorCode.SUCCESS) {
						currentUser = tmpuser;
						C2SServer.addStaff(currentUser.get("username"), this);
					}
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.REGISTER:// register
					tmpuser = new Gson().fromJson(tmp, TXObject.class);
					res = User.register(tmpuser);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.SEARCH_GROUP_BY_USER:// search group by name
					group = new Gson().fromJson(tmp, TXObject.class);
					List<TXObject> groupList = Group.searchGroups(group);
					res = new Msg(ErrorCode.SUCCESS, groupList);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.CREATE_GROUP:// create group
					group = new Gson().fromJson(tmp, TXObject.class);
					int code = Group.createGroup(currentUser, group);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.SEARCH_GROUP:// search group
					group = new Gson().fromJson(tmp, TXObject.class);
					groupList = Group.searchGroups(group);
					res = new Msg(ErrorCode.SUCCESS, groupList);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.APPLY_GROUP:// apply for a group
					group = new Gson().fromJson(tmp, TXObject.class);
					int code2 = Group.apply(currentUser, group);
					res = new Msg(code2, null);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.SEND_GROUP_MESSAGE:// send a chat in a group
					message = new Gson().fromJson(tmp, TXObject.class);
					message.set("username", currentUser.get("username"));
					code = Group.sendGroupMessage(message);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.GET_GROUP_MESSAGE:// get all group chats
					group = new Gson().fromJson(tmp, TXObject.class);
					List<TXObject> chatList = Group.getGroupMessage(currentUser, group);
					res = new Msg(ErrorCode.SUCCESS, chatList);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.QUIT_GROUP:// quit group || cancel apply
					group = new Gson().fromJson(tmp, TXObject.class);
					int code3 = Group.quitGroup(currentUser, group);
					res = new Msg(code3);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.CREATE_DISCUSS:
					discuss = new Gson().fromJson(tmp, TXObject.class);
					res = Discuss.createDiscuss(discuss, currentUser);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.GET_ALL_DISCUSSES:
					discuss = new Gson().fromJson(tmp, TXObject.class);
					res = Discuss.getAllDiscusses(discuss);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.JOIN_DISCUSS:
					break;

				case RequestCode.QUIT_DISCUSS:
					break;

				case RequestCode.SEND_WHITEBOARD_ACTION:// send blackboard
														// action(s)
					res = new Msg(ErrorCode.SUCCESS);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					// List<TXObject> users = ;
					// S2CServer.broadcast(users, str);
					break;

				case RequestCode.GET_WHITEBOARD_ACTION:// request blackboard
														// action(s)
					break;

				case RequestCode.POST_ARTICLE:// post article
					article = new Gson().fromJson(tmp, TXObject.class);
					code = Blog.postArticle(currentUser, article);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.UPDATE_ARTICLE:// update article
					article = new Gson().fromJson(tmp, TXObject.class);
					code = Blog.upArticle(currentUser, article);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;
				case RequestCode.SEARCH_ARTICLE:// get article by id
					article = new Gson().fromJson(tmp, TXObject.class);
					List<TXObject> articles = Blog.getArticles(article);
					res = new Msg(ErrorCode.SUCCESS, articles);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.COMMENT_AT_ARTICLE:// add comment
					comment = new Gson().fromJson(tmp, TXObject.class);
					code = Blog.commentAtArticle(currentUser, comment);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.SEARCH_ARTICLE_COMMENT:// get comments of an
														// article
					article = new Gson().fromJson(tmp, TXObject.class);
					List<TXObject> comments = Blog.getCommentsOfArticle(article);
					res = new Msg(ErrorCode.SUCCESS, comments);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.ASK_QUESTION:// ask question
					question = new Gson().fromJson(tmp, TXObject.class);
					code = FAQ.askQuestion(currentUser, question);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;
				case RequestCode.SEARCH_QUESTION:// get all questions before
													// question
					question = new Gson().fromJson(tmp, TXObject.class);
					List<TXObject> questions = FAQ.getQuestions(question);
					res = new Msg(ErrorCode.SUCCESS, questions);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;
				case RequestCode.UPDATE_QUESTION:// update question
					question = new Gson().fromJson(tmp, TXObject.class);
					code = FAQ.updateQuestion(currentUser, question);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;
				case RequestCode.DELETE_QUESTION:// delete question
					question = new Gson().fromJson(tmp, TXObject.class);
					code = FAQ.deleteQuestion(currentUser, question);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.SEARCH_QUESTION_ANSWER:// get answers by
														// question id
					question = new Gson().fromJson(tmp, TXObject.class);
					List<TXObject> answers = FAQ.getAnswersByQuestion(question);
					res = new Msg(ErrorCode.SUCCESS, answers);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				case RequestCode.ANSWER_QUESTION:// answer question
					answer = new Gson().fromJson(tmp, TXObject.class);
					code = FAQ.answerQuestion(currentUser, answer);
					res = new Msg(code);
					System.out.println("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					break;

				default:// unrecognized request
					writer.println(new Gson().toJson(new Msg(ErrorCode.UNKNOWN)));
					break;
				}

			}

			Logger.getLogger(C2SStaff.class.getName()).log(Level.INFO, "Staff sent: " + new Gson().toJson(res));

			stopConn();

		} catch (Exception ex) {
			try {
				writer.println(new Gson().toJson(new Msg(ErrorCode.INVALID_DATA_FORMAT, null)));
				Logger.getLogger(C2SStaff.class.getName()).log(Level.SEVERE, null, ex);
				stopConn();
			} catch (Exception e) {
				Logger.getLogger(C2SStaff.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	public void stopConn() {
		// has been stopped
		if (socket == null) {
			return;
		}

		try {
			reader.close();
			writer.close();
			socket.close();
			socket = null;
			C2SServer.removeStaff(currentUser.get("username"));
			System.out.println(currentUser.get("username") + " quit.");
		} catch (IOException e) {
			Logger.getLogger(C2SStaff.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public String getSid() {
		return currentUser.get("sid");
	}

}
