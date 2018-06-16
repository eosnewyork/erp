package com.eosrp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PostgresHelper {

	private Connection conn;
	private String host;
	private String dbName;
	private String user;
	private String pass;
	
	private String statementRam = "INSERT INTO eosram (dt, peos, pusd) VALUES (?, ?, ?)";
	private String statementCpu = "INSERT INTO eoscpu (dt, peos, pusd) VALUES (?, ?, ?)";
	private String statementNet = "INSERT INTO eosnet (dt, peos, pusd) VALUES (?, ?, ?)";
	private String statement;
	private PreparedStatement prepStatement;
	
	protected PostgresHelper() {}
	
	public PostgresHelper(String host, String dbName, String user, String pass) {
		this.host = host;
		this.dbName = dbName;
		this.user = user;
		this.pass = pass;
	}
	
	public boolean connect() throws SQLException, ClassNotFoundException {
		if (host.isEmpty() || dbName.isEmpty() || user.isEmpty() || pass.isEmpty()) {
			throw new SQLException("Database credentials missing");
		}
		
		Class.forName("org.postgresql.Driver");
		this.conn = DriverManager.getConnection(
				this.host + this.dbName,
				this.user, this.pass);
		return true;
	}
	
	public ResultSet execQuery(String query) throws SQLException {
		return this.conn.createStatement().executeQuery(query);
	}
	
	public int insert(String table, Timestamp dt, double pusd, double peos) throws SQLException {

		switch (table) {
		case "eosram":
			statement = statementRam;
			break;
			
		case "eoscpu":
			statement = statementCpu;
			break;
			
		case "eosnet":
			statement = statementNet;
			break;
			
		}
		prepStatement = conn.prepareStatement(statement);		
		prepStatement.setTimestamp(1, dt);
		prepStatement.setDouble(2, pusd);
		prepStatement.setDouble(3, peos);
		
		//~ DEBUG ~//
		//~ System.out.println(prepStatement.toString());
		return prepStatement.executeUpdate();
	}
}