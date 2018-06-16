package com.eosrp.network;

import java.io.IOException;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpPostHelper {
	
	/* Take in a map of parameters (<param1, val1>, <param2, val2>)
	 * Use this map to construct the JSON of the POST request
	 * Return a map of the JSON result
	 */
	
	private String url;
	private HashMap<String, String> parameters;
	
	public HttpPostHelper(String _url, HashMap<String, String> _parameters) {
		url = _url;
		parameters = _parameters;
	}
	
	public int sendRequest() throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://targethost/homepage");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		
		try {
		    System.out.println(response1.getStatusLine());
		    HttpEntity entity1 = response1.getEntity();
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    EntityUtils.consume(entity1);
		} finally {
		    response1.close();
		}
		return 0;
		
	}

}
