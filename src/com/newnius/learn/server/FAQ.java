package com.newnius.learn.server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.newnius.learn.util.ErrorCode;
import com.newnius.learn.util.TXObject;

public class FAQ {
	public static int askQuestion(TXObject user, TXObject question) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (question == null)
			return ErrorCode.QUESTION_NOT_EXIST;
		if (!question.hasKey("title"))
			return ErrorCode.TITLE_IS_EMPTY;
		if (!question.hasKey("content"))
			return ErrorCode.CONTENT_IS_EMPTY;
		question.set("keywords", "");

		try {
			String sql = "INSERT INTO `FAQ_question` ( `title`, `description`, `keywords`, `author`, `time`) VALUES( ?, ?, ?, ?, ?)";
			String[] args1 = { question.get("title"), question.get("content"), question.get("keywords"),
					user.get("username"), System.currentTimeMillis() + "" };
			int affected_rows = DAO.executeUpdate(sql, args1);
			if (affected_rows == 1) {
				return ErrorCode.SUCCESS;
			} else {
				return ErrorCode.UNKNOWN;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorCode.UNKNOWN;
		}
	}

	public static int updateQuestion(TXObject user, TXObject question) {

		return 0;
	}

	public static int deleteQuestion(TXObject user, TXObject question) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (question == null || !question.hasKey("questionID"))
			return ErrorCode.QUESTION_NOT_EXIST;
		List<TXObject> questions = getQuestionById(question);
		if (questions.size() == 0)
			return ErrorCode.QUESTION_NOT_EXIST;
		if (!questions.get(0).get("author").equals(user.get("username")))
			return ErrorCode.NO_ACCESS;

		try {
			String sql = "DELETE FROM `FAQ_question` WHERE `question_id` = ?";
			String[] args1 = { question.getInt("questionID") + "" };
			int affected_rows = DAO.executeUpdate(sql, args1);
			if (affected_rows == 1) {
				return ErrorCode.SUCCESS;
			} else {
				return ErrorCode.UNKNOWN;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorCode.UNKNOWN;
		}
	}

	public static List<TXObject> getQuestions(TXObject question) {
		if (question == null)
			return getAllQuestions(question);
		else if (question.hasKey("questionID"))
			return getQuestionById(question);
		else if (question.hasKey("author"))
			return getQuestionsByAuthor(question);
		else if (question.hasKey("type-unsolved"))
			return getUnsolvedQuestions(question);
		else
			return getAllQuestions(question);

	}

	private static List<TXObject> getAllQuestions(TXObject question) {
		List<TXObject> questions = new ArrayList<>();
		ResultSet rs;
		int page = 1;
		if (question != null && question.hasKey("page-no"))
			page = question.getInt("page-no");
		if (page < 1)
			page = 1;
		int offset = (page - 1) * 10;

		try {
			String sql = "SELECT * FROM `FAQ_question` ORDER BY `question_id` DESC LIMIT  " + offset + ",10";
			String[] args1 = {};
			rs = DAO.executeQuery(sql, args1);

			if (rs != null && !rs.wasNull()) {
				while (rs.next()) {
					TXObject localquestion = new TXObject();
					localquestion.set("questionID", rs.getInt("question_id"));
					localquestion.set("title", rs.getString("title"));
					localquestion.set("content", rs.getString("description"));
					localquestion.set("keywords", rs.getString("keywords"));
					localquestion.set("author", rs.getString("author"));
					localquestion.set("time", rs.getLong("time"));
					localquestion.set("views", rs.getInt("views"));
					questions.add(localquestion);
				}
			}
			return questions;
		} catch (Exception e) {
			e.printStackTrace();
			return questions;
		}
	}

	private static List<TXObject> getUnsolvedQuestions(TXObject question) {
		List<TXObject> questions = new ArrayList<>();
		ResultSet rs;
		int page = 1;
		if (question != null && question.hasKey("page-no"))
			page = question.getInt("page-no");
		if (page < 1)
			page = 1;
		int offset = (page - 1) * 10;
		try {
			String sql = "SELECT * FROM `FAQ_question` ORDER BY `question_id` DESC LIMIT  " + offset + ",10";
			String[] args1 = {};
			rs = DAO.executeQuery(sql, args1);
			if (rs != null && !rs.wasNull()) {
				while (rs.next()) {
					TXObject localquestion = new TXObject();
					localquestion.set("questionID", rs.getInt("question_id"));
					localquestion.set("title", rs.getString("title"));
					localquestion.set("content", rs.getString("description"));
					localquestion.set("keywords", rs.getString("keywords"));
					localquestion.set("author", rs.getString("author"));
					localquestion.set("time", rs.getLong("time"));
					localquestion.set("views", rs.getInt("views"));
					questions.add(localquestion);
				}
			}
			return questions;
		} catch (Exception e) {
			e.printStackTrace();
			return questions;
		}
	}

	private static List<TXObject> getQuestionsByAuthor(TXObject question) {
		List<TXObject> questions = new ArrayList<>();
		if (question == null || !question.hasKey("author"))
			return questions;
		ResultSet rs;
		int page = 1;
		if (question != null && question.hasKey("page-no"))
			page = question.getInt("page-no");
		if (page < 1)
			page = 1;
		int offset = (page - 1) * 10;
		try {
			String sql = "SELECT * FROM `FAQ_question` WHERE `author` = ? ORDER BY `question_id` DESC LIMIT  " + offset
					+ ",10";
			String[] args1 = { question.get("author") };
			rs = DAO.executeQuery(sql, args1);
			if (rs != null && !rs.wasNull()) {
				while (rs.next()) {
					TXObject localquestion = new TXObject();
					localquestion.set("questionID", rs.getInt("question_id"));
					localquestion.set("title", rs.getString("title"));
					localquestion.set("content", rs.getString("description"));
					localquestion.set("keywords", rs.getString("keywords"));
					localquestion.set("author", rs.getString("author"));
					localquestion.set("time", rs.getLong("time"));
					localquestion.set("views", rs.getInt("views"));
					questions.add(localquestion);
				}
			}
			return questions;
		} catch (Exception e) {
			e.printStackTrace();
			return questions;
		}
	}

	private static List<TXObject> getQuestionById(TXObject question) {
		List<TXObject> questions = new ArrayList<>();
		if (question == null || !question.hasKey("questionID"))
			return questions;
		ResultSet rs;
		try {
			String sql = "SELECT * FROM `FAQ_question` WHERE `question_id` = ?";
			String[] args1 = { question.getInt("questionID") + "" };
			rs = DAO.executeQuery(sql, args1);
			if (rs != null && !rs.wasNull()) {
				while (rs.next()) {
					TXObject localquestion = new TXObject();
					localquestion.set("questionID", rs.getInt("question_id"));
					localquestion.set("title", rs.getString("title"));
					localquestion.set("content", rs.getString("description"));
					localquestion.set("keywords", rs.getString("keywords"));
					localquestion.set("author", rs.getString("author"));
					localquestion.set("time", rs.getLong("time"));
					localquestion.set("views", rs.getInt("views"));
					questions.add(localquestion);
					sql = "UPDATE `FAQ_question` SET `views` = `views` + 1 WHERE `question_id` = ?";
					String[] args = { question.getInt("questionID") + "" };
					DAO.executeUpdate(sql, args);
				}
			}
			return questions;
		} catch (Exception e) {
			e.printStackTrace();
			return questions;
		}
	}

	public static int answerQuestion(TXObject user, TXObject answer) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (answer == null || !answer.hasKey("questionID"))
			return ErrorCode.QUESTION_NOT_EXIST;
		if (!answer.hasKey("content"))
			return ErrorCode.ANSWER_NOT_EXIST;
		List<TXObject> questions = getQuestionById(answer);
		if (questions.size() == 0)
			return ErrorCode.QUESTION_NOT_EXIST;
		String sql = "INSERT INTO `FAQ_answer`(`question_id`, `content`, `author`, `time`) VALUES(?, ?, ?, ?)";
		String[] args = { answer.getInt("questionID") + "", answer.get("content"), user.get("username"),
				System.currentTimeMillis() + "" };
		int affected_rows = DAO.executeUpdate(sql, args);
		if (affected_rows == 1) {
			return ErrorCode.SUCCESS;
		} else {
			return ErrorCode.UNKNOWN;
		}
	}

	public static List<TXObject> getAnswersByQuestion(TXObject question) {
		List<TXObject> answers = new ArrayList<>();
		if (question == null || !question.hasKey("questionID"))
			return answers;
		List<TXObject> questions = getQuestionById(question);
		if (questions.size() == 0)
			return answers;

		try {
			String sql = "SELECT * FROM `FAQ_answer` WHERE `question_id` = ?";
			String[] args1 = { question.getInt("questionID") + "" };
			ResultSet rs = DAO.executeQuery(sql, args1);
			if (rs != null && !rs.wasNull()) {
				while (rs.next()) {
					TXObject answer = new TXObject();
					answer.set("questionID", rs.getInt("question_id"));
					answer.set("answerID", rs.getInt("answer_id"));
					answer.set("content", rs.getString("content"));
					answer.set("author", rs.getString("author"));
					answer.set("time", rs.getLong("time"));
					answer.set("upVotes", rs.getInt("ups"));
					answer.set("downVotes", rs.getInt("downs"));
					answer.set("helpful", rs.getString("helpful"));
					answers.add(answer);
				}
			}
			return answers;
		} catch (Exception ex) {
			ex.printStackTrace();
			return answers;
		}
	}

}
