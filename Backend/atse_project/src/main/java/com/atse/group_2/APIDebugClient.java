package com.atse.group_2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


class APIDebugClient{
	// Common variables located here for convenience.
	// Location of the server where requests should be sent
	static String url = "http://localhost:8080/api/tutor";	
	
	// Map containing parameters which will be passed in POST method
	static Map<String, String> params;
	static {
		 params = new HashMap<String,String>();
		 params.put("username", "MyTestUsername");
		 params.put("password", "MyTestPassword123");
	}
	
	public static void main(String[] args) throws IOException{		
		// doGet();
		doPost();
	}	
	
	private static void doGet() throws IOException{
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		
		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get);
		
		System.out.println(response.toString());
		String html = EntityUtils.toString(response.getEntity());
		System.out.println(html);
		response.close();
	}
	
	private static void doPost() throws IOException{
		// Create HttpClient pointing to the url of the server
		HttpClient client = HttpClients.createDefault();		
		HttpPost post = new HttpPost(url);
		
		if(!params.isEmpty()){
			// Take any params above and include them in the POST request
			List<BasicNameValuePair> postParameters
				= new ArrayList<BasicNameValuePair>();
			
			for (String key : params.keySet()){
				postParameters.add(new BasicNameValuePair(key, params.get(key)));
			}
			
			post.setEntity(new UrlEncodedFormEntity(postParameters));
		}
		
		// Execute the request
		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post);
		
		// Print the response
		System.out.println(response.toString());
		String html = EntityUtils.toString(response.getEntity());
		System.out.println(html);
		response.close();
	}
}