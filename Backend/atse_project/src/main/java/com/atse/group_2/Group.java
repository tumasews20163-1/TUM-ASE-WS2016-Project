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
	String name; 			// Name of the Group
	String tutor; 			// Username of the tutor responsible for the group
	Set<String> students; 	// Usernames of the students in the group
	Set<String> sessions; 	// List of session IDs for this group

	public Group() {
		if (this.students == null)
			students = new HashSet<String>();
	}

	public Group(String name, String tutor) {
		this.name 		= name;
		this.tutor 		= tutor;
		this.students 	= new HashSet<String>();
		this.sessions 	= new HashSet<String>();
	}
	

	// Adds a student username to the list of students in this Group. 
	// If the student is already in the Group, no change is made.
	public void addStudent(String username){
		if (this.students == null) { this.students = new HashSet<String>(); }
		
		this.students.add(username);
		ObjectifyService.ofy().save().entity(this).now();
	}
	
	
	// Adds a new session to the Group. If the session already exists, no change is made.
	public void addGroupSession(String sessionID){		
		if(this.sessions == null) { this.sessions = new HashSet<String>(); }
		
		this.sessions.add(sessionID);
		ObjectifyService.ofy().save().entity(this).now();
	}
	
	
	// Returns a list of students eligible for a bonus
	public List<Person> calculateBonuses(){
		List<Person> qualifiedStudents = new ArrayList<Person>();
		
		for (String studentUsername : students){
			Person student = ObjectifyService.ofy().load().type(Person.class).id(studentUsername).now();
			
			if (student.attendance != null &&
					 this.sessions != null &&
					 this.sessions.size() - student.attendance.size() <= 2){
				
				if (student.attendance.containsValue(Person.Scores.PARTICIPANT)){
					// Award the bonus if the student has missed 2 or fewer sessions AND has participated					
					qualifiedStudents.add(student);
				}
			}
		} 

		return qualifiedStudents;
	}
	
	
	// Sends bonus notifications to eligible students
	public void sendBonusNotifications(){
		List<Person> eligibleStudents = calculateBonuses();

		for (Person student : eligibleStudents){
			// Mark the student has having earned the bonus.
			student.awardBonus();
		}
		
		// Could send email or push notification?
		
	}
	
	// Static methods
	// ============================================================================================
	
	// Adds a user to the list of group participants for a given group
	public static void addPersonToGroup(String group, String username){
		ObjectifyService.ofy().load().type(Group.class).id(group).now().addStudent(username);
	}
	
	// Adds a session to the list of sessions for a given group.
	public static void addNewSessionToGroup(String group, String sessionID){
		ObjectifyService.ofy().load().type(Group.class).id(group).now().addGroupSession(sessionID);
	}
}
