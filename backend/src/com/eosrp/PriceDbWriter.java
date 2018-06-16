package com.eosrp;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.*;
import com.eosrp.db.DbContract;
import com.eosrp.db.PostgresHelper;
import com.eosrp.network.*;

public class PriceDbWriter {	

	private static double getRamPriceEos(String _url) {
		Double _ramPriceEos;
		
		String ramMarketJson = "{\"json\":true,\"code\":\"eosio\",\"scope\":\"eosio\",\"table\":\"rammarket\",\"table_key\":\"\",\"lower_bound\":\"\",\"upper_bound\":\"\",\"limit\":10}";

		//~ Instantiate the POST request object 
		//~ and fetch latest price for ram
		HttpPostHelper postReq = new HttpPostHelper(_url, ramMarketJson);
		String _strJsonResp = postReq.sendRequest();

		//~ Get USD price of EOS from CoinMarketCap API~//
		JSONObject jsonObject = (JSONObject) JSONValue.parse(_jsonResp);
		JSONObject data = (JSONObject) jsonObject.get("data");
		JSONObject quotes = (JSONObject) data.get("quotes");
		JSONObject usd = (JSONObject) quotes.get("USD");
		_eosPriceUsd = (Double) usd.get("price"); 
		//~ DEBUG
		//~ System.out.println(_eosPriceUsd);
		return _eosPriceUsd;
		
		
		return 0;
		
	}
	
	private static double getEosPrice(String _url) throws IOException {
		Double _eosPriceUsd;

		//~ Instantiate the GET request object 
		//~ and fetch latest price for EOS/USD
		HttpGetHelper getReq = new HttpGetHelper(_url);
		String _strJsonResp = getReq.sendRequest();

		//~ Get USD price of EOS from CoinMarketCap API~//
		JSONObject jsonObject = (JSONObject) JSONValue.parse(_strJsonResp);
		JSONObject data = (JSONObject) jsonObject.get("data");
		JSONObject quotes = (JSONObject) data.get("quotes");
		JSONObject usd = (JSONObject) quotes.get("USD");
		_eosPriceUsd = (Double) usd.get("price"); 
		//~ DEBUG
		//~ System.out.println(_eosPriceUsd);
		return _eosPriceUsd;
	}

	//~ Generic method to insert into any table
	private static int sendQuery(String _table, Double _eosPriceUsd, PostgresHelper _client, Double _urlEosPriceUsd) throws SQLException, IOException {
		//~ Get timestamp
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		java.sql.Timestamp dt = new java.sql.Timestamp(date.getTime());

		//~ Attempt to insert record
		if (_client.insert(_table, dt, _eosPriceUsd, 0.12345) == 1) {
			//~ DEBUG
			//~ System.out.println("Record added");
			return 1;
		}
		return 0;
	}
	
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

	public static void main(String[] args) throws SQLException, IOException {
		String urlEosPriceUsd = "https://api.coinmarketcap.com/v2/ticker/1765/";
		String urlGetTableRows = "http://node1.eosphere.io:8888";
		String urlGetAccount = "http://node1.eosphere.io:8888/v1/chain/get_account";

		//~ Initialize DB connection
		PostgresHelper client = initDatabase();
		
		//~ Get the latest prices
		
		
		//~ Send the DB query using prices we just got
		if (client != null) {
			int result = sendQuery("eosram", getEosPrice(urlEosPriceUsd), client, 0.12345);
			if (result == 1) {
				//~ DEBUG
				//~ System.out.println("Successful query");
			}
			else
			{
				//~ DEBUG
				//~ System.out.println("Unsuccessful querty");
			}
		}

		//~ Print the DB records
		ResultSet rs = client.execQuery("SELECT * FROM eosram");
		while(rs.next()) {
			System.out.printf("%s\t%s\t%s\n", 
					rs.getString(1),
					rs.getString(2),
					rs.getString(3));
		}

	}

}