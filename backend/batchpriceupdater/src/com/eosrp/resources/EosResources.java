package com.eosrp.resources;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.eosrp.network.HttpGetHelper;
import com.eosrp.network.HttpPostHelper;

public class EosResources {
	
	private String strUrlEosPriceUsd;
	private String strUrlGetTable;
	private String strUrlGetAccount;
	
	public EosResources(String _strUrlEosPriceUsd, String _strUrlGetTable, String _strUrlGetAccount) {
		strUrlEosPriceUsd = _strUrlEosPriceUsd;
		strUrlGetTable = _strUrlGetTable;
		strUrlGetAccount = _strUrlGetAccount;
	}

	//~ Get the current price of CPU bandwidth in EOS/microseconds
	public double getCpuMicSecPrice() throws IOException {
		Double cpuPriceEos;
		
		String strAccountInfoJson = "{\"account_name\":\"eosnewyorkio\"}";

		//~ Instantiate the POST request object 
		//~ and fetch latest price for CPU
		HttpPostHelper postReq = new HttpPostHelper(strUrlGetAccount, strAccountInfoJson);
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
		System.out.println("cpuPriceEos: " + cpuPriceEos);
		
		return cpuPriceEos;
	}
	
	//~ Get the current price of Network bandwidth in EOS/bytes
	public double getNetBandBytesPrice() throws IOException {
		Double netPriceEos;
		
		String strAccountInfoJson = "{\"account_name\":\"eosnewyorkio\"}";

		//~ Instantiate the POST request object 
		//~ and fetch latest price for CPU
		HttpPostHelper postReq = new HttpPostHelper(strUrlGetAccount, strAccountInfoJson);
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
		System.out.println("netPriceEos: " + netPriceEos);
		
		return netPriceEos;
	}
	
	//~ Get current price per byte (RAM/EOS) for next byte of RAM
	//~ Note this does not include the price slippage or 0.5% fee
	//~ Todo: use Bancor algorithm to calculate exact price of X bytes
	public double getRamBytesPrice() throws IOException {
		Double ramPriceEos;

		//~ This is from running the following command in cleos to get the JSON payload:
		//~ cleos --print-response --print-request get table eosio eosio rammarket
		String strRamMarketJson = "{\"json\":\"true\",\"code\":\"eosio\",\"scope\":\"eosio\",\"table\":\"rammarket\",\"limit\":\"10\"}";

		//~ DEBUG
		//~ System.out.println("reqJson: " + ramMarketJson);

		//~ Instantiate the POST request object 
		//~ and fetch latest price for ram
		HttpPostHelper postReq = new HttpPostHelper(strUrlGetTable, strRamMarketJson);
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

		//~ Finally calculate the RAM/EOS price before returning it
		ramPriceEos = dblQuoteBalance / dblBaseBalance;

		//~ DEBUG
		//~ System.out.printf("Quote Balance: %f\n", dblQuoteBalance);
		//~ System.out.printf("Base Balance: %f\n", dblBaseBalance);
		System.out.println("ramPriceEos: " + ramPriceEos);

		return ramPriceEos;
	}
	
	//~ Get the current EOS/USD price from coinmarketcap's API
	public double getEosPriceUsd() throws IOException {
		Double eosPriceUsd;

		//~ Instantiate the GET request object 
		//~ and fetch latest price for EOS/USD
		HttpGetHelper getReq = new HttpGetHelper(strUrlEosPriceUsd);

		//~ Capture request response JSON in a JSON object
		JSONObject jsonResp = getReq.sendRequest();

		//~ Get USD price of EOS from the JSON response
		JSONObject data = (JSONObject) jsonResp.get("data");
		JSONObject quotes = (JSONObject) data.get("quotes");
		JSONObject usd = (JSONObject) quotes.get("USD");
		eosPriceUsd = (Double) usd.get("price"); 

		//~ DEBUG
		System.out.println("eosPriceUsd: " + eosPriceUsd);

		return eosPriceUsd;
	}
	
}
