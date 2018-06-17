package com.eosrp.network;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class HttpGetHelper {

	private String strUrl;
	
	//~ Constructor
	public HttpGetHelper(String _url) {
		strUrl = _url;
	}
	
	//~ Sends an HTTP GET request to the URL passed into the constructor
	//~ 
	//~ Returns a JSON object of the response JSON
	public JSONObject sendRequest() throws IOException {
		String strRespString;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(strUrl);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		
		try {
			//~ DEBUG
		    //~ System.out.println(response.getStatusLine());
		    HttpEntity respEntity = response.getEntity();
		    strRespString = EntityUtils.toString(respEntity);
		    EntityUtils.consume(respEntity);
		} finally {
		    response.close();
		}
		
		return (JSONObject) JSONValue.parse(strRespString);
	}
	
	

}
