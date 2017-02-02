package com.atse.group_2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;


class APIDebugClient{	
	// Remote url strings
	static String url  = "http://utility-node-147216.appspot.com/api/student";
	static String url2 = "http://utility-node-147216.appspot.com/api/tutor";
	
	// Local url strings
	// static String url  = "http://localhost:8080/api/student";
	// static String url2 = "http://localhost:8080/api/tutor";

	
	public static void main(String[] args) throws IOException{		               
		String studentUsername = "egazetic";
		String studentPassword = "1111";
		
		String tutorUsername = "tutor1";
		String tutorPassword = "1111";
		
		String QRString1;
		String QRString2;
		
		String SessionID1 = "TestSession1";
		String SessionID2 = "TestSession2";
		
		String response;
		
		// Get a student
		response = sendStudentRequest(studentUsername, studentPassword);
		Person student = new Gson().fromJson(response, Person.class);
		QRString1 = student.currentQR();
		System.out.println(response);
		
		// Mark the student present for a session
		response = sendTutorRequest(tutorUsername, tutorPassword, QRString1, SessionID1, "false");
		System.out.println(response);
		
		// Get the student again
		response = sendStudentRequest(studentUsername, studentPassword);
		student = new Gson().fromJson(response, Person.class);
		QRString2 = student.currentQR();
		System.out.println(response);
		
		// Mark the student participating at a different session
		response = sendTutorRequest(tutorUsername, tutorPassword, QRString2, SessionID2, "true");
		System.out.println(response);
		
		// Get the student again
		response = sendStudentRequest(studentUsername, studentPassword);
		System.out.println(response);

	}	
	
	// Sends a request as a student to get a QR code and the student's information
	private static String sendStudentRequest(String username, String password) throws IOException{
		Map<String, String> params;
		{
			 params = new HashMap<String,String>();
			 params.put(API.Params.USERNAME.getValue(), username);
			 params.put(API.Params.PASSWORD.getValue(), password);
		}
		
		String html = sendRequest(params, url);
		
		return html;
	}
	
	// Sends a request as a tutor user to mark a student present
	private static String sendTutorRequest(String username, String password, String QRString, String SessionID, String participation) throws IOException{
		Map<String, String> params;
		{
			 params = new HashMap<String,String>();
			 params.put(API.Params.USERNAME.getValue(), username);
			 params.put(API.Params.PASSWORD.getValue(), password);
			 params.put(API.Params.CHALLENGE_QR.getValue(), QRString);
			 params.put(API.Params.SESSION.getValue(), SessionID);
			 params.put(API.Params.PARTICIPATION_FLAG.getValue(), participation);
		}
		
		String html = sendRequest(params, url2);
		
		return html;
	}
	
	// Sends an HTTP POST request to a server and returns a string containing the html of the response.
	// If needed, you can get the rest of the response from the response variable at the end of this method.
	private static String sendRequest(Map<String, String> params, String url) throws IOException{
		
		// Create HttpClient pointing to the url of the server
		HttpClient client = HttpClients.createDefault();		
		HttpPost post = new HttpPost(url);
		
		if(!params.isEmpty()){
			// Take any params above and include them in the POST request
			// List<BasicNameValuePair> postParameters
			// 	= new ArrayList<BasicNameValuePair>();
			
			for (String key : params.keySet()){
				// postParameters.add(new BasicNameValuePair(key, params.get(key)));
				post.addHeader(key, params.get(key));
			}
			
			// post.setEntity(new UrlEncodedFormEntity(postParameters));
			
		}		
		
		// Execute the request
		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post);
		String html = EntityUtils.toString(response.getEntity());
		response.close();
		
		return html;
	}
	
	
	
	// Old post methods for debugging. Probably better to just use the new ones above ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// =======================================================================================================================================
	/*
	 * 
	 * 	
	/*
	// Map containing parameters which will be passed in POST method
	static Map<String, String> params;
	static {
		 params = new HashMap<String,String>();
		 params.put("username", "MyTestUsername");
		 params.put("password", "MyTestPassword123");
	}
	
	static Map<String, String> params2;
	static {
		 params2 = new HashMap<String,String>();
		 params2.put("username", "tutor1");
		 params2.put("password", "1111");
		 params2.put("QRString", "MyTestUsername:10_0");
		 params2.put("SessionID", "MyTestSessionID123");
		 params2.put("Participation", "true");
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
	
	private static void doPost2() throws IOException{
		// Create HttpClient pointing to the url of the server
		HttpClient client = HttpClients.createDefault();		
		HttpPost post = new HttpPost(url2);
		
		if(!params2.isEmpty()){
			// Take any params above and include them in the POST request
			List<BasicNameValuePair> postParameters
				= new ArrayList<BasicNameValuePair>();
			
			for (String key : params2.keySet()){
				postParameters.add(new BasicNameValuePair(key, params2.get(key)));
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
	
	private static void doPost3() throws IOException{
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
	
	// GET isn't used for anything, but if you want to use it anyway, you'll have to add the import statement:
	// import org.apache.http.client.methods.HttpGet;	
	private static void doGet() throws IOException{
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		
		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get);
		
		System.out.println(response.toString());
		String html = EntityUtils.toString(response.getEntity());
		System.out.println(html);
		response.close();
	}*/
}