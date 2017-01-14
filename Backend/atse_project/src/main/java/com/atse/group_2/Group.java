package com.atse.group_2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Group {

	@Id
	String name; 			// String
	String tutor; 			// username of the tutor responsible for the group
	List<String> students; 	// usernames of the students in the group
	Set<String> sessions; 	// List of session IDs for this group

	public Group() {
		if (this.students == null)
			students = new ArrayList<String>();
	}

	public Group(String name, String tutor) {
		this.name 		= name;
		this.tutor 		= tutor;
		this.students 	= new ArrayList<String>();
		this.sessions 	= new HashSet<String>();
	}
	
	public static void addPersonToGroup(String group, String username){
		ObjectifyService.ofy().load().type(Group.class).id(group).now().addStudent(username);
	}
	
	public static void addNewSessionToGroup(String group, String sessionID){
		ObjectifyService.ofy().load().type(Group.class).id(group).now().addGroupSession(sessionID);
	}
	
	public void addStudent(String username){
		if (this.students == null) { this.students = new ArrayList<String>(); }
		this.students.add(username);
		ObjectifyService.ofy().save().entity(this).now();
	}
	
	public void addGroupSession(String sessionID){
		if(this.sessions == null) { this.sessions = new HashSet<String>(); }
		this.sessions.add(sessionID);
		ObjectifyService.ofy().save().entity(this).now();
	}
	
	public List<Person> calculateBonuses(){
		List<Person> qualifiedStudents = new ArrayList<Person>();
		
		for (String studentUsername : students){
			Person student = ObjectifyService.ofy().load().type(Person.class).id(studentUsername).now();
			
			if (student.attendance != null &&
					 this.sessions != null &&
					 this.sessions.size() - student.attendance.size() <= 2){
				
				if (student.attendance.containsValue(Person.Scores.PARTICIPANT)){
					qualifiedStudents.add(student);
				}
			}
		} 

		return qualifiedStudents;
	}
}
