package com.eosrp.network;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

public class HttpGetHelper {
	
	/* Take in only the URL to send a request to
	 * Return a map of the JSON result
	 * (<param1, val1>, <param2, val2>)
	 */
	
	private String url;
	private String returnResponse;
	
	//~~ Constructor ~~/
	public HttpGetHelper(String _url) {
		url = _url;
	}
	
	public String sendRequest() throws IOException {
		//JSONObject json = new JSONObject();
		//json.put("key1", "val1");
		String respString;
		
		//System.out.println(json.get("key1"));
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		
		try {
		    System.out.println(response.getStatusLine());
		    HttpEntity respEntity = response.getEntity();
		    respString = EntityUtils.toString(respEntity);
		    EntityUtils.consume(respEntity);
		} finally {
		    response.close();
		}
		return respString;
	}

}
