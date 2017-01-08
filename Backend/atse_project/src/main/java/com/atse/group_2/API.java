package com.atse.group_2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;

public class API extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		
		// Get can be for:
		// - Getting QR code for user
		

		
		// If no params set in uri path or request parameters, display the whole request (debug)
		printRequest(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {		
		// Post can be for:
		// - Marking attendance for user
		// - Retrieving QR code
		
		// If request contains a valid username/password combination, return string to be converted into QR code
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(username != null && password != null){
			response.setContentType("application/json");
			String responseText = new String();
			// response.getWriter().println("<h1>~~~~Welcome to the POST page1.</h1>");

			Gson gson = new Gson();
			//System.out.println("test");
			// response.getWriter().println(json);
			//writer.println("<h1>Welcome to the POST page2.</h1>");
			//writer.flush();
			
			// Try username/pw combination
			Person person = ObjectifyService.ofy().load().type(Person.class).id(username).now();		

			if(person != null){
				if(person.password.equals(password)){
					if(person.role == Person.Roles.STUDENT.getValue()){
						// User is a student - generate a QR code string						
						responseText = person.toJson();
					} else if(person.role == Person.Roles.TUTOR.getValue()){
						// User is a tutor - try to mark attendance for the student
					} else {
						// This is an undefined role
						throw new UnsupportedOperationException(String.format("The role %i is not a defined role.", person.role));
					}
				} else {
					// Invalid password
					System.out.println("Invalid password");
				}
			} else {
				// Username not found
				System.out.println("Username not found");
			}
			
			response.getWriter().println(responseText);
		} else {		
			printRequest(request, response);
		}
	}
	
	private void printRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.getWriter().println("<h1>Welcome to the API page.</h1>");
		response.setContentType("text/html");
		
		response.getWriter().println(request.getMethod());
		response.getWriter().println("<br/>");
		
		response.getWriter().println("<p>Header fields:</p>");
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
		  String headerName = (String)headerNames.nextElement();
		  response.getWriter().println("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
		  response.getWriter().println("<br/>");
		}
		response.getWriter().println("<br/>");
		
		response.getWriter().println("<p>Parameter fields:</p>");
		Enumeration<String> params = request.getParameterNames(); 
		while(params.hasMoreElements()){
		 String paramName = (String)params.nextElement();
		 response.getWriter().println("Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName));
		 response.getWriter().println("<br/>");
		}
		response.getWriter().println("<br/>");
		
		response.getWriter().println("<p>Attribute fields:</p>");
		Enumeration<String> attributes = request.getAttributeNames(); 
		while(attributes.hasMoreElements()){
		 String attributeName = (String)attributes.nextElement();
		 response.getWriter().println("Attribute Name - " + attributeName + ", Value - " + request.getAttribute(attributeName));
		 response.getWriter().println("<br/>");
		}
		
		response.getWriter().println(request.getMethod());
	}
}