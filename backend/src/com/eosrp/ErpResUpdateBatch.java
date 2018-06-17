package com.eosrp;

import java.io.IOException;
import java.sql.SQLException;
import com.eosrp.db.DbContract;
import com.eosrp.db.PostgresHelper;
import com.eosrp.resources.EosResources;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

	public static void storePrice(String _table, EosResources _eosRes, PostgresHelper _client) throws SQLException, IOException {
		Boolean result;

		//~ DEBUG
		//~ System.out.println(_table);

		switch (_table) {
		case "eosram":
			if (_client != null) {
				result = _client.sendQuery("eosram", _eosRes.getEosPriceUsd(), _client, _eosRes.getRamBytesPrice());
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
				result = _client.sendQuery("eoscpu", _eosRes.getEosPriceUsd(), _client, _eosRes.getCpuMicSecPrice());
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
				result = _client.sendQuery("eosnet", _eosRes.getEosPriceUsd(), _client, _eosRes.getNetBandBytesPrice());
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

	public static void main(String[] args){
		//~ Backup node: node1.eosphere.io:8888
		String strUrlEosPriceUsd = "https://api.coinmarketcap.com/v2/ticker/1765";
		String strUrlGetTableRows = "http://api.eosnewyork.io/v1/chain/get_table_rows";
		String strUrlGetAccount = "http://api.eosnewyork.io/v1/chain/get_account";

		//~ Instantiate objects
		PostgresHelper client = initDatabase();
		EosResources eosRes = new EosResources(strUrlEosPriceUsd, strUrlGetTableRows, strUrlGetAccount);
		
		System.out.println("Initialized objects, spawning task..");
		
		//~ Spawn a thread to run the storage/update procedures
		Runnable runnable = new Runnable() {
			public void run() {
				System.out.println("Running task");
				try {
					storePrice("eosram", eosRes, client);
					storePrice("eoscpu", eosRes, client);
					storePrice("eosnet", eosRes, client);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		ScheduledExecutorService service = Executors
				.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
	}
	/*	try {
			storePrice("eosram", eosRes, client);
			storePrice("eoscpu", eosRes, client);
			storePrice("eosnet", eosRes, client);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
}