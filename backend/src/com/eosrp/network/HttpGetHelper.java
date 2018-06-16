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
	
	private String strUrl;
	
	//~~ Constructor
	public HttpGetHelper(String _url) {
		strUrl = _url;
	}
	
	public String sendRequest() throws IOException {
		String _strRespString;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(strUrl);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		
		try {
			//~ DEBUG
		    //~ System.out.println(response.getStatusLine());
		    HttpEntity respEntity = response.getEntity();
		    _strRespString = EntityUtils.toString(respEntity);
		    EntityUtils.consume(respEntity);
		} finally {
		    response.close();
		}
		return _strRespString;
	}
	
	

}
