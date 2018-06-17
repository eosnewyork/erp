package com.eosrp;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.*;
import com.eosrp.db.DbContract;
import com.eosrp.db.PostgresHelper;
import com.eosrp.network.*;

public class PriceDbWriter {	

	private static double getNetBandBytesPrice(String _url) throws IOException {
		Double netPriceEos;
		
		String strAccountInfoJson = "{\"account_name\":\"eosnewyorkio\"}";

		//~ Instantiate the POST request object 
		//~ and fetch latest price for CPU
		HttpPostHelper postReq = new HttpPostHelper(_url, strAccountInfoJson);
		JSONObject jsonResp = postReq.sendRequest();

		//~ We need to access 'total_resources' and 'net_limit' to get the balance values
		JSONObject strTotalResources = (JSONObject) jsonResp.get("total_resources");
		JSONObject strNetLimit = (JSONObject) jsonResp.get("net_limit");
		String strNetWeight = (String) strTotalResources.get("net_weight");
		Long lngNetLimitMax = (Long) strNetLimit.get("max");
		
		//~ The value is a string with a unit on the end, select only the numerical part
		String strTrimmedNetLimit = strNetWeight.substring(0, strNetWeight.indexOf(' '));
		
		//~ Now cast it into a double so we can do math on it
		Double dblNetStaked = Double.parseDouble(strTrimmedNetLimit);
		netPriceEos = dblNetStaked / lngNetLimitMax;
		
		//~ DEBUG
		//~ System.out.println(strNetWeight);
		//~ System.out.println(dblNetStaked);
		//~ System.out.println(netPriceEos);
		
		return netPriceEos;
	}
	
	//~ Get the current price of CPU bandwidth in EOS/ms
	private static double getCpuMicSecPrice(String _url) throws IOException {
		Double cpuPriceEos;
		
		String strAccountInfoJson = "{\"account_name\":\"eosnewyorkio\"}";

		//~ Instantiate the POST request object 
		//~ and fetch latest price for CPU
		HttpPostHelper postReq = new HttpPostHelper(_url, strAccountInfoJson);
		JSONObject jsonResp = postReq.sendRequest();

		//~ We need to access 'total_resources' and 'cpu_limit' to get the balance values
		JSONObject strTotalResources = (JSONObject) jsonResp.get("total_resources");
		JSONObject strCpuLimit = (JSONObject) jsonResp.get("cpu_limit");
		String strCpuWeight = (String) strTotalResources.get("cpu_weight");
		Long lngCpuLimitMax = (Long) strCpuLimit.get("max");
		
		//~ The value is a string with a unit on the end, select only the numerical part
		String strTrimmedCpuLimit = strCpuWeight.substring(0, strCpuWeight.indexOf(' '));
		
		//~ Now cast it into a double so we can do math on it
		Double dblNetCpuStaked = Double.parseDouble(strTrimmedCpuLimit);
		cpuPriceEos = dblNetCpuStaked / lngCpuLimitMax;
		
		//~ DEBUG
		//~ System.out.println(strCpuWeight);
		//~ System.out.println(dblNetCpuStaked);
		//~ System.out.println(cpuPriceEos);
		
		return cpuPriceEos;
	}

	//~ Get current price per byte (RAM/EOS) for next byte of RAM
	//~ Note this does not include the price slippage or 0.5% fee
	//~ Todo: use Bancor algorithm to calculate exact price of X bytes
	private static double getRamBytesPrice(String _url) throws IOException {
		Double ramPriceEos;

		//~ This is from running the following command in cleos to get the JSON payload:
		//~ cleos --print-response --print-request get table eosio eosio rammarket
		String strRamMarketJson = "{\"json\":\"true\",\"code\":\"eosio\",\"scope\":\"eosio\",\"table\":\"rammarket\",\"limit\":\"10\"}";

		//~ DEBUG
		//~ System.out.println("reqJson: " + ramMarketJson);

		//~ Instantiate the POST request object 
		//~ and fetch latest price for ram
		HttpPostHelper postReq = new HttpPostHelper(_url, strRamMarketJson);
		JSONObject jsonResp = postReq.sendRequest();

		//~ The JSON response looks like this:
		//~ {"rows":[{"supply":"10000000000.0000 RAMCORE","base":{"balance":"67172012846 RAM","weight":"0.50000000000000000"},"quote":{"balance":"1023040.5170 EOS","weight":"0.50000000000000000"}}],"more":false}
		//~ First use a JSONArray to read the first (only) row in the data
		JSONArray rowsArray = (JSONArray) jsonResp.get("rows");
		JSONObject strRow = (JSONObject) rowsArray.get(0);

		//~ We need to access 'quote' and 'base' to get the balance values
		JSONObject strQuote = (JSONObject) strRow.get("quote");
		JSONObject strBase = (JSONObject) strRow.get("base");
		String strQuoteBalance = (String) strQuote.get("balance").toString();
		String strBaseBalance = (String) strBase.get("balance").toString();

		//~ The balance value is a string with a unit on the end, select only the numerical part
		String strTrimmedQuoteBalance = strQuoteBalance.substring(0, strQuoteBalance.indexOf(' '));
		String strTrimmedBaseBalance = strBaseBalance.substring(0, strBaseBalance.indexOf(' '));

		//~ Now cast it into a double so we can do math on it
		Double dblQuoteBalance = Double.parseDouble(strTrimmedQuoteBalance);
		Double dblBaseBalance = Double.parseDouble(strTrimmedBaseBalance);

		//~ Finaly calculate the RAM/EOS price before returning it
		ramPriceEos = dblQuoteBalance / dblBaseBalance;

		//~ DEBUG
		//~ System.out.printf("Quote Balance: %f\n", dblQuoteBalance);
		//~ System.out.printf("Base Balance: %f\n", dblBaseBalance);
		//~ System.out.printf("Price: %.8f",  ramPriceEos);

		return ramPriceEos;
	}

	//~ Get the current EOS/USD price from coinmarketcap's API
	private static double getEosPriceUsd(String _url) throws IOException {
		Double eosPriceUsd;

		//~ Instantiate the GET request object 
		//~ and fetch latest price for EOS/USD
		HttpGetHelper getReq = new HttpGetHelper(_url);

		//~ Capture request response JSON in a JSON object
		JSONObject jsonResp = getReq.sendRequest();

		//~ Get USD price of EOS from the JSON response
		JSONObject data = (JSONObject) jsonResp.get("data");
		JSONObject quotes = (JSONObject) data.get("quotes");
		JSONObject usd = (JSONObject) quotes.get("USD");
		eosPriceUsd = (Double) usd.get("price"); 

		//~ DEBUG
		//~ System.out.println(eosPriceUsd);

		return eosPriceUsd;
	}

	//~ Generic method to insert into any table
	private static int sendQuery(String _table, Double _eosPriceUsd, PostgresHelper _client, Double _resourcePriceEos) throws SQLException, IOException {
		//~ Get timestamp
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		java.sql.Timestamp dt = new java.sql.Timestamp(date.getTime());


		//~ Attempt to insert record
		//~ DEBUG
		//~ System.out.printf("Table: %s,  dt: %s, _eosPriceUsd: %s, _resourcePriceEos: %s\n", _table, dt, _eosPriceUsd, _resourcePriceEos);
		if (_client.insert(_table, dt, _eosPriceUsd, _resourcePriceEos) == 1) {
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

	public static void updateRamPrice() throws SQLException, IOException {
		PostgresHelper client = initDatabase();
		String strUrlEosPriceUsd = "https://api.coinmarketcap.com/v2/ticker/1765";
		String strUrlGetTableRows = "http://api.eosnewyork.io/v1/chain/get_table_rows";

		if (client != null) {
			int result = sendQuery("eosram", getEosPriceUsd(strUrlEosPriceUsd), client, getRamBytesPrice(strUrlGetTableRows));
			if (result == 1) {
				//~ DEBUG
				//~ System.out.println("Successful query");
			}
			else
			{
				//~ DEBUG
				//~ System.out.println("Unsuccessful query");
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

	public static void updateNetPrice() {

	}

	public static void updateCpuPrice() {

	}

	public static void main(String[] args) throws SQLException, IOException {
		//~ Backup node: node1.eosphere.io:8888
		String strUrlGetAccount = "http://api.eosnewyork.io/v1/chain/get_account";

		//~ updateRamPrice();
		//~ getCpuMicSecPrice(strUrlGetAccount);
		getNetBandBytesPrice(strUrlGetAccount);
	}

}