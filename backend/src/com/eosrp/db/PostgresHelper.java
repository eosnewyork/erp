package com.eosrp.db;

import java.io.IOException;
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
		strHost = host;
		strDbName = dbName;
		strUser = user;
		strPass = pass;
		
		strStatementRam = "INSERT INTO eosram (dt, peos, pusd) VALUES (?, ?, ?)";
		strStatementCpu = "INSERT INTO eoscpu (dt, peos, pusd) VALUES (?, ?, ?)";
		strStatementNet = "INSERT INTO eosnet (dt, peos, pusd) VALUES (?, ?, ?)";
	}
	
	//~ Generic method to insert into any table
	public boolean sendQuery(String _table, Double _eosPriceUsd, PostgresHelper _client, Double _resourcePriceEos) throws SQLException, IOException {
		//~ Get timestamp
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		java.sql.Timestamp dt = new java.sql.Timestamp(date.getTime());


		//~ Attempt to insert record
		//~ DEBUG
		//~ System.out.printf("Table: %s,  dt: %s, _eosPriceUsd: %s, _resourcePriceEos: %s\n", _table, dt, _eosPriceUsd, _resourcePriceEos);
		if (_client.insert(_table, dt, _eosPriceUsd, _resourcePriceEos) == 1) {
			//~ DEBUG
			//~ System.out.println("Record added");
			return true;
		}
		return false;
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