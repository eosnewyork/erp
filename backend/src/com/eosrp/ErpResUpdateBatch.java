package com.eosrp;

import java.io.IOException;
import java.sql.SQLException;
import com.eosrp.db.DbContract;
import com.eosrp.db.PostgresHelper;
import com.eosrp.resources.EosResources;

public class ErpResUpdateBatch {	

	private static PostgresHelper initDatabase() {
		//~ Initialize DB connection
		PostgresHelper client = new PostgresHelper(
				DbContract.HOST, 
				DbContract.DB_NAME,
				DbContract.USERNAME,
				DbContract.PASSWORD);

		try {
			if (client.connect()) {
				//~ DEBUG
				//~ System.out.println("DB connected");
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return client;
	}

	public static void storePrice(String _table, EosResources _eosRes, PostgresHelper _client, String _strUrlEosPriceUsd, String _strUrlGetTableRows) throws SQLException, IOException {
		Boolean result;
		
		//~ DEBUG
		//~ System.out.println(_table);
		
		switch (_table) {
		case "eosram":
			if (_client != null) {
				result = _client.sendQuery("eosram", _eosRes.getEosPriceUsd(_strUrlEosPriceUsd), _client, _eosRes.getRamBytesPrice(_strUrlGetTableRows));
				if (result == true) {
					//~ DEBUG
					//~ System.out.println("Successful query");
				}
				else
				{
					//~ DEBUG
					//~ System.out.println("Unsuccessful query");
				}
			}
			break;
			
		case "eoscpu":
			if (_client != null) {
				result = _client.sendQuery("eoscpu", _eosRes.getEosPriceUsd(_strUrlEosPriceUsd), _client, _eosRes.getCpuMicSecPrice(_strUrlGetTableRows));
				if (result == true) {
					//~ DEBUG
					//~ System.out.println("Successful query");
				}
				else
				{
					//~ DEBUG
					//~ System.out.println("Unsuccessful query");
				}
			}
			break;
			
		case "eosnet":
			if (_client != null) {
				result = _client.sendQuery("eosnet", _eosRes.getEosPriceUsd(_strUrlEosPriceUsd), _client, _eosRes.getNetBandBytesPrice(_strUrlGetTableRows));
				if (result == true) {
					//~ DEBUG
					//~ System.out.println("Successful query");
				}
				else
				{
					//~ DEBUG
					//~ System.out.println("Unsuccessful query");
				}
			}
			break;
		}
		


		//~ Print the DB records
		/*ResultSet rs = _client.execQuery("SELECT * FROM eosram");
		while(rs.next()) {
			System.out.printf("%s\t%s\t%s\n", 
					rs.getString(1),
					rs.getString(2),
					rs.getString(3));
		}*/
	}

	public static void main(String[] args) throws SQLException, IOException {
		//~ Backup node: node1.eosphere.io:8888
		String strUrlGetAccount = "http://api.eosnewyork.io/v1/chain/get_account";
		String strUrlEosPriceUsd = "https://api.coinmarketcap.com/v2/ticker/1765";
		String strUrlGetTableRows = "http://api.eosnewyork.io/v1/chain/get_table_rows";

		//~ Instantiate objects
		PostgresHelper client = initDatabase();
		EosResources eosRes = new EosResources(strUrlEosPriceUsd, strUrlGetTableRows);

		storePrice("eosram", eosRes, client, strUrlEosPriceUsd, strUrlGetTableRows);
		storePrice("eoscpu", eosRes, client, strUrlEosPriceUsd, strUrlGetAccount);
		storePrice("eosnet", eosRes, client, strUrlEosPriceUsd, strUrlGetAccount);
	}

}