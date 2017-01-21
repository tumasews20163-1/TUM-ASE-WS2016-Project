package com.atse.group_2;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.googlecode.objectify.ObjectifyService;

public class SignUp extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("/signup.jsp");
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
			response.sendRedirect("/signup.jsp");
		
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
				response.sendRedirect("/signup.jsp");
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