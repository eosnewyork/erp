package com.eosrp.network;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class HttpPostHelper {
	
	private String strUrl;
	private String strReqJson;
	
	//~ Constructor
	public HttpPostHelper(String _strUrl, String _strReqJson) {
		strUrl = _strUrl;
		strReqJson = _strReqJson;
	}
	
	//~ Sends an HTTP POST request to the URL passed into the constructor
	//~ and uses the JSON parameters in a string passed into the constructor
	//~
	//~ Example parameter string (for RAM market):
	//~ {"json":"true","code":"eosio","scope":"eosio","table":"rammarket","limit":"10"}
	//~ 
	//~ Returns a JSON object of the response JSON
	public JSONObject sendRequest() throws IOException {
		String strRespString;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(strUrl);
		StringEntity strentReqString = new StringEntity(strReqJson);
		httpPost.setEntity(strentReqString);
		httpPost.setHeader("Content-type", "application/json");
		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {
			//~ DEBUG
		    //~ System.out.println("POST Response Code: " + response.getStatusLine());
		    HttpEntity respEntity = response.getEntity();
		    strRespString = EntityUtils.toString(respEntity);
		    EntityUtils.consume(respEntity);
		} finally {
		    response.close();
		}
		
		return (JSONObject) JSONValue.parse(strRespString);
	}

}
