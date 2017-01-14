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
	
	public static enum Params{
		USERNAME ("username"),
		PASSWORD ("password"),
		CHALLENGE_QR ("QRString"),
		SESSION ("SessionID"),
		PARTICIPATION_FLAG ("Participation"),
		PARTICIPATION_TRUE ("true"),
		STUDENT_URL_PATH ("/student"),
		TUTOR_URL_PATH ("/tutor");
		
		private final String value;
		Params(String value){
			this.value = value;
		}
		public String getValue() { return value; }
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		// Post can be for:
		// - Marking attendance/presentation for user
		// - Retrieving QR code
		
		response.setContentType("application/json");
		String responseText = new APIError(666, "An unknown error occurred handling the request.").toJson();
		
		// If request contains a valid username/password combination, return string to be converted into QR code
		String username = request.getParameter(Params.USERNAME.getValue());
		String password = request.getParameter(Params.PASSWORD.getValue());
		
		if(username != null && password != null){			
			// Try username/pw combination
			Person person = ObjectifyService.ofy().load().type(Person.class).id(username).now();		

			if(person != null){
				
				if(person.password.equals(password)){
					
					if(request.getPathInfo().equals(Params.STUDENT_URL_PATH.getValue())){
						
						// User is requesting student api access
						if(person.role == Person.Roles.STUDENT.getValue()){
							// User is a student - generate a QR code string	
							responseText = handleStudentPostRequest(request, response, person);		
							
						} else {
							responseText = new APIError(3, "Role mismatch. You must be a student to access the student API").toJson();
						}
						
					} else if(request.getPathInfo().equals(Params.TUTOR_URL_PATH.getValue())){
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
	
	// Responds to POST requests for student data. 
	// Refreshes the student's QR code and replies to the request.
	private String handleStudentPostRequest(HttpServletRequest request, HttpServletResponse response, Person person){
		person.newQR();
		String responseText = person.toJson();
		return responseText;
	}
	
	// Responds to POST request for tutors.
	private String handleTutorPostRequest(HttpServletRequest request, HttpServletResponse response, Person person){
		// person in this context is the TUTOR, not the student
		
		String responseText;
		
		// Get the Student who owns the QR code
		String challengeQR 		= request.getParameter(Params.CHALLENGE_QR.getValue());
		String studentUsername 	= challengeQR.split(":")[0];
		Person student 			= ObjectifyService.ofy().load().type(Person.class).id(studentUsername).now();
		
		if (student != null) {
			// Check that the Student is in the Tutor's group
			// - If so, check the QR code
			// - Otherwise, give an error		
			
			if (student.group != null){
				
				if (student.group.equals(person.group)){
					// Check that the QRString matches the student's current QR
					// - If so, update the student's attendance
					// - Otherwise, give an error	
					
					if(student.verifyQR(challengeQR)){
						// QR matches, so mark student present/presented
						String sessionID 			= request.getParameter(Params.SESSION.getValue());					
						String participationString 	= request.getParameter(Params.PARTICIPATION_FLAG.getValue());
						
						if (sessionID != null && participationString != null){
							boolean participated = participationString.equalsIgnoreCase(Params.PARTICIPATION_TRUE.getValue());
							
							student.markAttendance(sessionID, participated);
							responseText = new APISuccess(1, "Success").toJson();
							
						} else {
							responseText = new APIError(7, "SessionID or Participation parameter is not set.").toJson();
						}		
						
					} else {
						responseText = new APIError(6, "QR code does not match the student's current QR code.").toJson();
					}
					
				} else {
					responseText = new APIError(5, "Student is not assigned to the current tutor.").toJson();
				}
				
			} else {
				responseText = new APIError(8, "Student is not registered for a tutorial group.").toJson();
			}
			
		} else {
			responseText = new APIError(0, "Student username was not found.").toJson();
		}
		
		return responseText;
	}
	
	// Handles incoming GET requests (there shouldn't be any)
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		// GET is not supported for any functionality. Display the whole request (debug)
		printRequest(request, response);
	}
	
	// Prints the HTTP Request fields and content to the Response output
	@SuppressWarnings("unchecked")
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