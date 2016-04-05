package com.newnius.learn.server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.newnius.learn.util.ErrorCode;
import com.newnius.learn.util.TXObject;

public class Blog {

	public static int postArticle(TXObject user, TXObject article) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (article == null)
			return ErrorCode.ARTICLE_NOT_EXIST;
		if (!article.hasKey("title"))
			return ErrorCode.TITLE_IS_EMPTY;
		if (!article.hasKey("content"))
			return ErrorCode.CONTENT_IS_EMPTY;
		if (!article.hasKey("category"))
			article.set("category", 0);
		if (!article.hasKey("cover"))
			article.set("cover", "default-article-cover.gif");
		article.set("author", user.get("username"));

		try {
			String sql = "INSERT INTO `blog_article` ( `title`, `content`, `time`, `author`, `category`, `cover`) VALUES( ?, ?, ?, ?, ?, ?)";
			String[] args1 = { article.get("title"), article.get("content"), System.currentTimeMillis() + "",
					article.get("author"), article.getInt("category") + "", article.get("cover") };
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

	public static int updateArticle(TXObject user, TXObject article) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (article == null || !article.hasKey("articleID"))
			return ErrorCode.ARTICLE_NOT_EXIST;
		if (!article.hasKey("title"))
			return ErrorCode.TITLE_IS_EMPTY;
		if (!article.hasKey("content"))
			return ErrorCode.CONTENT_IS_EMPTY;
		if (!article.hasKey("category"))
			article.set("category", 0);
		if (!article.hasKey("cover"))
			article.set("cover", "default-article-cover.gif");
		article.set("author", user.get("username"));

		try {
			String sql = "UPDATE `blog_article` SET  `title` =?  , `content` = ?, `time` = ?, `category` = ?, `cover` = ? ) VALUES( ?, ?, ?, ?, ?) WHERE `article_id` = ? AND `author` = ?";
			String[] args1 = { article.get("title"), article.get("content"), System.currentTimeMillis() + "",
					article.getInt("category") + "", article.get("cover"), article.getInt("articleID") + "",
					article.get("author") };
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

	public static List<TXObject> getArticles(TXObject article) {
		if (article == null)
			return getHottestArticle(article);
		else if (article.hasKey("articleID"))
			return getArticleById(article);
		else if (article.hasKey("username"))
			return getArticleByAuthor(article);
		else if (article.hasKey("type-hottest"))
			return getHottestArticle(article);
		return getHottestArticle(article);

	}

	private static List<TXObject> getArticleById(TXObject article) {
		List<TXObject> articles = new ArrayList<>();
		try {
			String sql = "SELECT * FROM `blog_article` WHERE `article_id` = ?";
			String[] args1 = { article.getInt("articleID") + "" };
			ResultSet rs = DAO.executeQuery(sql, args1);

			if (rs != null && !rs.wasNull() && rs.next()) {
				TXObject tmparticle = new TXObject();
				tmparticle.set("articleID", rs.getInt("article_id"));
				tmparticle.set("title", rs.getString("title"));
				tmparticle.set("content", rs.getString("content"));
				tmparticle.set("category", rs.getInt("category"));
				tmparticle.set("time", rs.getLong("time"));
				tmparticle.set("author", rs.getString("author"));
				tmparticle.set("cover", rs.getString("cover"));
				tmparticle.set("views", rs.getInt("views"));
				tmparticle.set("upVotes", rs.getInt("ups"));
				articles.add(tmparticle);
			}
			return articles;
		} catch (Exception e) {
			e.printStackTrace();
			return articles;
		}
	}

	private static List<TXObject> getArticleByAuthor(TXObject user) {
		List<TXObject> articles = new ArrayList<>();
		if (user == null || !user.hasKey("username"))
			return articles;
		TXObject article = new TXObject();
		article.set("author", user.get("username"));
		int page = 1;
		if (article.hasKey("page-no"))
			page = article.getInt("page-no");
		if (page < 1)
			page = 1;
		int offset = (page - 1) * 10;
		try {
			String sql = "SELECT * FROM `blog_article` WHERE `author` = ? ORDER BY `article_id` LIMIT " + offset
					+ ", 10";
			String[] args1 = { user.get("username") };
			ResultSet rs = DAO.executeQuery(sql, args1);

			if (rs == null || rs.wasNull())
				return articles;

			while (rs.next()) {
				TXObject tmparticle = new TXObject();
				tmparticle.set("articleID", rs.getInt("article_id"));
				tmparticle.set("title", rs.getString("title"));
				tmparticle.set("content", rs.getString("content"));
				tmparticle.set("category", rs.getInt("category"));
				tmparticle.set("time", rs.getLong("time"));
				tmparticle.set("author", rs.getString("author"));
				tmparticle.set("cover", rs.getString("cover"));
				tmparticle.set("views", rs.getInt("views"));
				tmparticle.set("upVotes", rs.getInt("ups"));
				articles.add(tmparticle);
			}
			return articles;
		} catch (Exception e) {
			e.printStackTrace();
			return articles;
		}
	}

	private static List<TXObject> getHottestArticle(TXObject article) {
		List<TXObject> articles = new ArrayList<>();
		if (article == null)
			article = new TXObject();
		int page = 1;
		if (article.hasKey("page-no"))
			page = article.getInt("page-no");
		if (page < 1)
			page = 1;
		int offset = (page - 1) * 10;
		try {
			String sql = "SELECT * FROM `blog_article` ORDER BY `views` DESC LIMIT " + offset + " ,10";
			String[] args1 = {};
			ResultSet rs = DAO.executeQuery(sql, args1);
			if (rs == null || rs.wasNull())
				return articles;

			while (rs.next()) {
				TXObject tmparticle = new TXObject();
				tmparticle.set("articleID", rs.getInt("article_id"));
				tmparticle.set("title", rs.getString("title"));
				tmparticle.set("content", rs.getString("content"));
				tmparticle.set("category", rs.getInt("category"));
				tmparticle.set("time", rs.getLong("time"));
				tmparticle.set("author", rs.getString("author"));
				tmparticle.set("cover", rs.getString("cover"));
				tmparticle.set("views", rs.getInt("views"));
				tmparticle.set("upVotes", rs.getInt("ups"));
				articles.add(tmparticle);
			}
			return articles;
		} catch (Exception e) {
			e.printStackTrace();
			return articles;
		}
	}

	public static int deleteArticle(TXObject user, TXObject article) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (article == null || !article.hasKey("articleID"))
			return ErrorCode.ARTICLE_NOT_EXIST;
		List<TXObject> articles = getArticleById(article);
		if (articles.size() == 0)
			return ErrorCode.QUESTION_NOT_EXIST;
		if (!articles.get(0).get("author").equals(user.get("username")))
			return ErrorCode.NO_ACCESS;

		try {
			String sql = "DELETE FROM `blog_article` WHERE `article_id` = ? AND `author` = ?";
			String[] args1 = { article.getInt("articleID") + "", user.get("username") };
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

	public static int upArticle(TXObject user, TXObject article) {
		return ErrorCode.SUCCESS;
	}

	public static int commentAtArticle(TXObject user, TXObject comment) {
		if (user == null || !user.hasKey("username"))
			return ErrorCode.USER_NOT_EXIST;
		if (comment == null || !comment.hasKey("articleID"))
			return ErrorCode.ARTICLE_NOT_EXIST;
		if (!comment.hasKey("content"))
			return ErrorCode.CONTENT_IS_EMPTY;
		List<TXObject> articles = getArticleById(comment);
		if (articles.size() == 0) {
			return ErrorCode.ARTICLE_NOT_EXIST;
		}
		String sql = "INSERT INTO `blog_comment`(`article_id`, `content`, `author`, `time`) VALUES(?, ?, ?, ?)";
		String[] args = { comment.getInt("articleID") + "", comment.get("content"), user.get("username"),
				System.currentTimeMillis() + "" };
		int affected_rows = DAO.executeUpdate(sql, args);
		if (affected_rows == 1) {
			return ErrorCode.SUCCESS;
		} else {
			return ErrorCode.UNKNOWN;
		}
	}

	public static List<TXObject> getCommentsOfArticle(TXObject article) {
		List<TXObject> comments = new ArrayList<>();
		if (article == null || !article.hasKey("articleID"))
			return comments;
		if (getArticleById(article).size() == 0)
			return comments;
		try {
			String sql = "SELECT * FROM `blog_comment` WHERE `article_id` = ?";
			String[] args1 = { article.getInt("articleID") + "" };
			ResultSet rs = DAO.executeQuery(sql, args1);
			if (rs != null && !rs.wasNull()) {
				while (rs.next()) {
					TXObject comment = new TXObject();
					comment.set("commentID", rs.getInt("comment_id"));
					comment.set("articleID", rs.getInt("article_id"));
					comment.set("author", rs.getString("author"));
					comment.set("content", rs.getString("content"));
					comment.set("time", rs.getLong("time"));
					comments.add(comment);
				}
			}
			return comments;
		} catch (Exception ex) {
			ex.printStackTrace();
			return comments;
		}
	}

}
