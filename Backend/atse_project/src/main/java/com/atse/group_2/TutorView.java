package com.atse.group_2;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.googlecode.objectify.ObjectifyService;

// This should really be converted to use a JSP

public class TutorView extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		response.getWriter().println("<h1>Welcome to the tutor page.</h1>");
		response.setContentType("text/html");
	      response.getWriter().println("<html><head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.5/css/bootstrap.min.css\" "
	      		+ "integrity=\"sha384-AysaV+vQoT3kOAXZkl02PThvDr8HYKPZhNT5h/CXfBThSRXQ6jW5DO2ekP5ViFdi\" crossorigin=\"anonymous\"></head><body>");
	      String bonusMessage = (String) request.getSession().getAttribute("bonusMessage");
			if (bonusMessage != null) {
				response.getWriter().println("<div class=\"alert alert-success\">" + bonusMessage + "</div>");
				HttpSession session = request.getSession();
				session.setAttribute("bonusMessage", null);
			}
	      response.getWriter().println("<form action=\"tutorview\" method=\"POST\"><table class=\"table\"><thead class=\"thead\">"+
"<tr><th>Group</th><th>Student</th><th>Presence</th><th>Presentation</th></tr></thead><tbody>");
	      
		      
		      String username = (String)request.getSession().getAttribute("username");
		      Person tutor = ObjectifyService.ofy().load().type(Person.class).id(username).now();
		      
		      // This implementation gets everybody in the database and compares their groups and roles manually. 
		      // Should be rewritten to be more efficient. Example:
		      // List<Car> cars = ofy().load().type(Car.class).filter("year >", 1999).list()
		      List<Person> students = ObjectifyService.ofy().load().type(Person.class).list();
		      Iterator<Person> it = students.iterator();
		      
		      while(it.hasNext())
		      {		    	  
		      	Person student = it.next();
		      	
		      	if(student.group.equals(tutor.group) && student.role == Person.Roles.STUDENT.getValue()) {
			      	Group g = ObjectifyService.ofy().load().type(Group.class).id(tutor.group).now();
		      		response.getWriter().println(
			      			"<tr><td>" 
			      			+ student.group 
		      				+ "</td><td>" 
			      			+ student.username
			      			+ "</td><td>" 
			      			+ (student.attendance == null ? 0 : student.attendance.size()) 
			      			+ " out of " + (g.sessions == null ? 0 : g.sessions.size()) + " sessions"
			      			+ "</td><td>" 
			      			+ (student.attendance == null ? "false" : student.attendance.containsValue(Person.Scores.PARTICIPANT)) 
			      			+ "</td></tr>");
		      	}
		      }     
		      
	      response.getWriter().println("</tbody></table><button style=\"margin-right: 5px;\"type=\"submit\" class=\"btn btn-primary\">Calculate bonuses</button>"
	      		+ "<a href=\"login\" class=\"btn btn-primary\">Log out</a></form></body>");		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		String message 			= "There are no students eligible for the bonus.";
		String username 		= (String)request.getSession().getAttribute("username");
	    Person tutor 			= ObjectifyService.ofy().load().type(Person.class).id(username).now();
	    Group group 			= ObjectifyService.ofy().load().type(Group.class).id(tutor.group).now();
	    List<Person> bonuses 	= group.calculateBonuses();
	    
	    if(!bonuses.isEmpty()){
	    	message = "The students eligible for bonuses are: ";
	    	for(Person student : bonuses){
	    		message += student.username + ", ";
	    	}
	    	message = message.substring(0, message.length()-2);	// Trim trailing comma and space
	    }
	    
	    HttpSession session = request.getSession();
	    session.setAttribute("bonusMessage", message);
		response.sendRedirect("/tutorview");		
	}
}
