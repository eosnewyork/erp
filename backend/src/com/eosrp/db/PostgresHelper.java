package com.eosrp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PostgresHelper {

	private Connection conn;
	private String strHost;
	private String strDbName;
	private String strUser;
	private String strPass;
	private String strStatementRam;
	private String strStatementCpu;
	private String strStatementNet;
	private String strStatement;
	private PreparedStatement prepStatement;

	public PostgresHelper(String host, String dbName, String user, String pass) {
		this.strHost = host;
		this.strDbName = dbName;
		this.strUser = user;
		this.strPass = pass;
		
		strStatementRam = "INSERT INTO eosram (dt, peos, pusd) VALUES (?, ?, ?)";
		strStatementCpu = "INSERT INTO eoscpu (dt, peos, pusd) VALUES (?, ?, ?)";
		strStatementNet = "INSERT INTO eosnet (dt, peos, pusd) VALUES (?, ?, ?)";
	}
	
	public boolean connect() throws SQLException, ClassNotFoundException {
		if (strHost.isEmpty() || strDbName.isEmpty() || strUser.isEmpty() || strPass.isEmpty()) {
			throw new SQLException("Database credentials missing");
		}
		
		Class.forName("org.postgresql.Driver");
		this.conn = DriverManager.getConnection(
				this.strHost + this.strDbName,
				this.strUser, this.strPass);
		return true;
	}
	
	public ResultSet execQuery(String query) throws SQLException {
		return this.conn.createStatement().executeQuery(query);
	}
	
	public int insert(String table, Timestamp _dt, double _eosPriceUsd, double _resourcePriceEos) throws SQLException {

		switch (table) {
		case "eosram":
			strStatement = strStatementRam;
			break;
			
		case "eoscpu":
			strStatement = strStatementCpu;
			break;
			
		case "eosnet":
			strStatement = strStatementNet;
			break;
			
		}
		prepStatement = conn.prepareStatement(strStatement);		
		prepStatement.setTimestamp(1, _dt);
		prepStatement.setDouble(2, (_resourcePriceEos * _eosPriceUsd));
		prepStatement.setDouble(3, _resourcePriceEos);
		
		//~ DEBUG
		//~ System.out.println(prepStatement.toString());
		return prepStatement.executeUpdate();
	}
}