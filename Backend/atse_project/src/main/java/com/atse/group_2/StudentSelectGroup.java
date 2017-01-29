package com.atse.group_2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.googlecode.objectify.ObjectifyService;

// This should really be converted to use a JSP

public class StudentSelectGroup extends HttpServlet {

		public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
			response.setContentType("text/html");
		    response.getWriter().println("<h1>Welcome to the group selection page.</h1>");
		    response.getWriter().println("<html><head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.5/css/bootstrap.min.css\" "
		     		+ "integrity=\"sha384-AysaV+vQoT3kOAXZkl02PThvDr8HYKPZhNT5h/CXfBThSRXQ6jW5DO2ekP5ViFdi\" crossorigin=\"anonymous\"></head><body>");
		    response.getWriter().println("<form action=\"studentselectgroup\" method=\"POST\"><table class=\"table\"><thead class=\"thead\">"+
"<tr><th>Group Name</th><th>Tutor Name</th><th>Current Participans</th><th>Choose group</th></tr></thead><tbody>");
				    
			List<Group> groups = ObjectifyService.ofy().load().type(Group.class).list();
			String username = (String)request.getSession().getAttribute("username");
			Person person = ObjectifyService.ofy().load().type(Person.class).id(username).now();
			Iterator<Group> it2 = groups.iterator();
			      
			while(it2.hasNext()) {
				Group group_temp = it2.next();
			    List<String> students = new ArrayList<String>(group_temp.students);
			    response.getWriter().println("<tr><td>"+group_temp.name
			     	+"</td><td>"+group_temp.tutor+"</td><td>"+group_temp.students.size()+"</td><td><input type=\"radio\" ");
			    if(person.group != null && (person.group.equals(group_temp.name))) {
			    	response.getWriter().println("checked=\"checked\" "); 
			    }
			    
			   response.getWriter().println("name=\"groupSelection\" value="+group_temp.name+"></td> </tr>");
			}
			      

		      
		    response.getWriter().println("</tbody></table><button style=\"margin-right: 5px;\"type=\"submit\" class=\"btn btn-primary\">Submit</button>"
		    	+ "<a href=\"login\" class=\"btn btn-primary\">Log out</a></form></body>");		    
		  }
		
		public void doPost(HttpServletRequest request, HttpServletResponse response) 
			      throws IOException {
			  response.setContentType("text/html");
			  String username = (String) request.getSession().getAttribute("username");
			  String newGroupName = request.getParameter("groupSelection");
			  
			  Person person = ObjectifyService.ofy().load().type(Person.class).id(username).now();
			  String oldGroupName = person.group;
			  
			  Group newGroup = ObjectifyService.ofy().load().type(Group.class).id(newGroupName).now();
			  List<String> participants = new ArrayList<String>(newGroup.students);
			  
			  if(oldGroupName == null || (oldGroupName != null && !oldGroupName.equals(newGroupName)))
			  {
				  if(oldGroupName != null)
				  {
					  Group oldGroup = ObjectifyService.ofy().load().type(Group.class).id(oldGroupName).now();
					  List<String> oldGroupParticipants = new ArrayList<String>(oldGroup.students);
					  oldGroupParticipants.remove(username);
					  ObjectifyService.ofy().save().entity(oldGroup).now();  
				  }
				  
				  participants.add(person.username);
				  ObjectifyService.ofy().save().entity(newGroup).now();
				  person.group = newGroupName;
				  ObjectifyService.ofy().save().entity(person).now();
			  }
			  response.sendRedirect("/studentselectgroup");
			  
		}
}
