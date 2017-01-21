package com.atse.group_2;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.googlecode.objectify.ObjectifyService;

public class Login extends HttpServlet {

	static {
		ObjectifyService.register(Group.class);
		ObjectifyService.register(Person.class);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.getWriter().println("<h1>Welcome to the log-in page.</h1>");
		response.getWriter().println(
				"<html><head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.5/css/bootstrap.min.css\" "
						+ "integrity=\"sha384-AysaV+vQoT3kOAXZkl02PThvDr8HYKPZhNT5h/CXfBThSRXQ6jW5DO2ekP5ViFdi\" crossorigin=\"anonymous\"></head><body>");

		String errorMessage = (String) request.getSession().getAttribute("errorMessage");
		if (errorMessage != null) {
			response.getWriter().println("<div class=\"alert alert-danger\">" + errorMessage + "</div>");
			HttpSession session = request.getSession();
			session.setAttribute("errorMessage", null);
		}
		
	    String successMessage = (String) request.getSession().getAttribute("successMessage");
		
	    if (successMessage != null) {
	    	response.getWriter().println("<div class=\"alert alert-success\">" + successMessage + "</div>");
			HttpSession session = request.getSession();
			session.setAttribute("successMessage", null);
		}
		
		response.getWriter()
				.println("<form class=\"form-inline\" action=\"login\" method=\"POST\">"
						+ "     <div class=\"form-group\"><label for=\"username\">Username:</label><input id=\"username\" type=\"text\" name=\"username\"></div>"
						+

						"     <div class=\"form-group\"><label for=\"password\">Password:</label><input id=\"password\" type=\"password\" name=\"password\" /></div>"
						+ "     <button type=\"submit\" class=\"btn btn-primary\">Log in</button> " + "  </form>"
						+ " </body>" + "</html>");

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String errorMessage = null;
		HttpSession session = request.getSession();
		if (username == "" || password == ""){
			errorMessage = "Error: Please enter a username and/or password.";
			session.setAttribute("errorMessage", errorMessage);
			response.sendRedirect("/login");
		}
		else {
			Person person = ObjectifyService.ofy().load().type(Person.class).id(username).now();

			if (person == null){
				errorMessage = "Error: The username does not exist.";
				session.setAttribute("errorMessage", errorMessage);
				response.sendRedirect("/login");
			}
			else if (!person.password.equals(password)){
				errorMessage = "Error: The password is not correct.";
				session.setAttribute("errorMessage", errorMessage);
				response.sendRedirect("/login");
			}
				
			else if (person != null && person.password.equals(password)) {

				session.setAttribute("username", person.username);
				if (person.role == 0)
					response.sendRedirect("/studentselectgroup");
				else if (person.role == 1)
					response.sendRedirect("/tutorview");
			}

		}
	}
		

}
