/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newnius.learn.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Newnius
 */
public class DAO {

	private static Connection dbConnection = null;
	private static PreparedStatement preparedStatement = null;

	private static void connect() {
		try {
			dbConnection = null;
			Class.forName("com.mysql.jdbc.Driver");
			dbConnection = DriverManager.getConnection(
					"jdbc:mysql://mysq;:3306/learn?useUnicode=true&characterEncoding=utf-8", "tongxueclient", "VYCxRQKd8dSF");
		} catch (Exception e) {
			Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static ResultSet executeQuery(String sql, String[] args) {
		while (true) {
			try {
				if (dbConnection == null || !dbConnection.isValid(0)) {
					connect();
				}

				preparedStatement = dbConnection.prepareStatement(sql);
				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						preparedStatement.setString(i + 1, args[i]);
					}
				}
				System.out.println(preparedStatement);
				return preparedStatement.executeQuery();

			} catch (Exception e) {
				Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, e);
				return null;
			}
		}
	}

	public static int executeUpdate(String sql, String[] args) {
		while (true) {
			try {
				if (dbConnection == null || !dbConnection.isValid(0)) {
					connect();
				}

				preparedStatement = dbConnection.prepareStatement(sql);
				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						preparedStatement.setString(i + 1, args[i]);
					}
				}
				System.out.println(preparedStatement);
				return preparedStatement.executeUpdate();

			} catch (Exception e) {
				Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, e);
				return 0;
			}
		}
	}

	public static void close() {
		try {
			if (preparedStatement != null) {
				preparedStatement.close();
				preparedStatement = null;
			}
			if (dbConnection != null) {
				dbConnection.close();
				dbConnection = null;
			}
		} catch (SQLException ex) {
			Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
