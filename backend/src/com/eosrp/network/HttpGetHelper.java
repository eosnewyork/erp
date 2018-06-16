package com.eosrp.network;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
	
	public void sendRequest() throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		
		try {
		    System.out.println(response.getStatusLine());
		    HttpEntity entity1 = response.getEntity();
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    EntityUtils.consume(entity1);
		} finally {
		    response.close();
		}
		
	}

}
