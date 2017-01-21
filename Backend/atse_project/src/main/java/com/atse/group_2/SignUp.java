package com.atse.group_2;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.googlecode.objectify.ObjectifyService;

public class SignUp extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// response.sendRedirect("/signup.jsp");
		
		HttpSession session = request.getSession();	
		String failureMessage = (String) session.getAttribute("failureMessage");			
		
		if (failureMessage != null) {			
			failureMessage = "<div class=\"alert alert-danger\">" + failureMessage + "</div>";			
			session.setAttribute("failureMessage", null);
			
		} else {
			failureMessage = ""; // Simply don't print the alert if there is no error
		}
		
		// This kills me to do but App Engine doesn't play nicely with JSPs + Servlets...
		String pageContent = 
				"<html>" +
					"<head>" +
						"<style type=\"text/css\">" +
						"label {" +
						"width: 6em;" +
						"}" +
						"</style>" +			
						"<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.5/css/bootstrap.min.css\" " +
						"integrity=\"sha384-AysaV+vQoT3kOAXZkl02PThvDr8HYKPZhNT5h/CXfBThSRXQ6jW5DO2ekP5ViFdi\" crossorigin=\"anonymous\">" +								
					"</head>" +
					"<body>" +
						failureMessage +
						"<h1>Create a new account:</h1>" +						
						"<form class=\"form-inline\" action=\"signup\" method=\"POST\">" +
						"<div class=\"form-group\">" +
							"<label for=\"username\">Username:</label><input id=\"username\"" +
								"name=\"username\" type=\"text\">" +
						"</div>" +
						"<br>" +
						"<div class=\"form-group\">" +
							"<label for=\"firstname\">First Name:</label><input id=\"firstname\"" +
								"name=\"firstname\" type=\"text\">" +
						"</div>" +
						"<br>" +
						"<div class=\"form-group\">" +
							"<label for=\"lastname\">Last Name:</label><input id=\"lastname\"" +
								"name=\"lastname\" type=\"text\">" +
						"</div>" +
						"<br>" +
						"<div class=\"form-group\">" +
							"<label for=\"password\">Password:</label><input id=\"password\"" +
								"name=\"password\" type=\"password\">" +
						"</div>" +
						"<button type=\"submit\" class=\"btn btn-primary\">Sign Up</button>" +
						"</form>" +
					"</body>" +
				"</html>";	
		
		response.getWriter().println(pageContent);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username 	= request.getParameter("username");
		String password 	= request.getParameter("password");
		String firstName 	= request.getParameter("firstname");
		String lastName 	= request.getParameter("lastname");		
		
	    HttpSession session = request.getSession();
		
		if (username.trim().equals("") || password.trim().equals("") || firstName.trim().equals("") || lastName.trim().equals("")){
			// Set a message
			session.setAttribute("failureMessage", "Please complete all required fields.");

			// Redirect to signup page
			response.sendRedirect("/signup");
		
		} else {
			if (!usernameExists(username)){
				// Register the user
				registerNewUser(username, password, firstName, lastName);
				
				// Set a success message
			    session.setAttribute("successMessage", "Registration was successful! Try your new login below.");
				
				// Redirect to the login page
				response.sendRedirect("/login");
			
			} else {
				// Handle case where the user already exists
				// Set a rejection message
			    session.setAttribute("failureMessage", "Oops! That username already exists. Please try again.");
				
				// Redirect to signup page
				response.sendRedirect("/signup");
			}	
		}	
	}
	
	// Returns true if a username exists, false otherwise
	private static boolean usernameExists(String username){
		return ObjectifyService.ofy().load().type(Person.class).id(username).now() != null;
	}
	
	// Adds a new user to the Objectify store
	private static void registerNewUser(String username, String password, String firstName, String lastName){
		ObjectifyService.ofy().save().entity(
				new Person(username, password, firstName, lastName, Person.Roles.STUDENT.getValue(), null)
				).now();
	}	
}