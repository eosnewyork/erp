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

public class HttpPostHelper {
	
	/* Take in URL to send a request to and JSON payload for request body
	 * Parse map of parameters into JSON to send in request
	 * Return JSON of response
	 */
	
	private String url;
	private String returnResponse;
	private JSONObject reqJson;
	
	
	//~~ Constructor ~~/
	public HttpPostHelper(String _url, JSONObject _reqJson) {
		url = _url;
		reqJson = _reqJson;
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
