package com.atse.group_2;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;


// Student
// - QR + info

// Tutor
// - Send POST with QR string, session date/ID/string and boolean for presentation


public class API extends HttpServlet{
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {		
		// Post can be for:
		// - Marking attendance/presentation for user
		// - Retrieving QR code
		
		response.setContentType("application/json");
		String responseText = new String();
		// response.getWriter().println(request.getPathInfo()); // Prints the url segment after the api
		
		// If request contains a valid username/password combination, return string to be converted into QR code
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(username != null && password != null){			
			// Try username/pw combination
			Person person = ObjectifyService.ofy().load().type(Person.class).id(username).now();		

			if(person != null){
				if(person.password.equals(password)){
					if(request.getPathInfo().equals("/student")){
						// User is requesting student api access
						if(person.role == Person.Roles.STUDENT.getValue()){
							// User is a student - generate a QR code string	
							responseText = handleStudentPostRequest(request, response, person);							
						} else {
							responseText = new APIError(3, "Role mismatch. You must be a student to access the student API").toJson();
						}
						
					} else if(request.getPathInfo().equals("/tutor")){
						// User is requesting tutor api access
						if(person.role == Person.Roles.TUTOR.getValue()){
							// User is a tutor - try to mark attendance for the student
							responseText = handleTutorPostRequest(request, response, person);							
						} else {
							responseText = new APIError(3, "Role mismatch. You must be a tutor to access the tutor API").toJson();
						}
						
					} else {
						// Unknown destination
						responseText = new APIError(4, String.format("%s is an invalid API path.", request.getPathInfo())).toJson();
					}
					
				} else {
					// Invalid password
					responseText = new APIError(1, "Invalid password").toJson();
				}
				
			} else {
				// Username not found
				responseText = new APIError(0, "Username not found").toJson();
			}			

		} else {		
			responseText = new APIError(2, "Incomplete credentials: Username or password missing").toJson();
			// printRequest(request, response);
		}
		
		response.getWriter().println(responseText);
		response.getWriter().flush();
	}
	
	private String handleStudentPostRequest(HttpServletRequest request, 
			HttpServletResponse response, Person person){
		person.newQR();
		String responseText = person.toJson();
		return responseText;
	}
	
	private String handleTutorPostRequest(HttpServletRequest request, 
			HttpServletResponse response, Person person){
		// person in this context is the TUTOR, not the student
		
		String challengeQR = request.getParameter("QRString");
		String studentUsername = challengeQR.split(":")[0];
		System.out.println(challengeQR);
		System.out.println(studentUsername);
		Person student = ObjectifyService.ofy().load().type(Person.class).id(studentUsername).now();
		
		
		String match = "No match.";
		if(student.verifyQR(challengeQR)){
			// QR matches, so mark student present/presented
			 match = "Match!";
			 System.out.println("matchmatch");
		}
		String responseText = String.format("%s : %s : %s - %s", challengeQR, studentUsername, student.username, match);
		return responseText;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {		
		// GET is not supported for any functionality. Display the whole request (debug)
		printRequest(request, response);
	}
	
	private void printRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		response.getWriter().println("<h1>Welcome to the API page.</h1>");
		response.getWriter().println("<h2>GET requests are not supported. Please use POST.</h2>");
		
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