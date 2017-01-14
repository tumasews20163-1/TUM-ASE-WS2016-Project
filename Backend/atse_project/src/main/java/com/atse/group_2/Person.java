package com.atse.group_2;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Person {	
	
	@Id
	String username; // username of the person
	transient String password; // password of the person
	String group; // id of the group
	int role; // 0 is a student 1 is a tutor
	boolean presentation; // true if the student presented
	int[] presence; // assumption- 8 tutorials. Value 0 if the student was
					// present and value 1 if he/she was not.
	ArrayList<String> oldQRs;
	private String currentQR;
	Map<String, Scores> attendance;

	public Person() {
		
	}

	public Person(String username, String password, int role,String group) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.group= group;		
		this.presentation = false;
		//this.presence = new int[8];
		//for (int i = 0; i < presence.length; i++) {
		//	presence[i] = 0;
		//}
		
		this.oldQRs = new ArrayList<String>();
		this.attendance = new HashMap<String, Scores>();
		this.markAttendance("dummy", false);
		this.newQR();
	}
	
	public void markAttendance(String sessionID, boolean participated){
		Scores score;
		if (participated){
			score = Scores.PARTICIPANT;
		} else {
			score = Scores.PRESENT;
		}
		updateScore(sessionID, score);
	}
	
	private void updateScore(String sessionID, Scores newScore){
		// Attendance is null unless filled with something in constructor. wtf?
		if (this.attendance.containsKey(sessionID)){
			Scores oldScore = this.attendance.get(sessionID);
			if (oldScore != null){
				if (oldScore.getValue() < newScore.getValue()){			
					// If the student already has an entry for a given session (attendance),
					// update the score iff the old score is lower than the new one.
					// (If student was already marked for participation, don't erase that score for the attendance)
					this.attendance.put(sessionID, newScore);
					ObjectifyService.ofy().save().entity(this).now();
				}
			}
		} else {
			this.attendance.put(sessionID, newScore);
			ObjectifyService.ofy().save().entity(this).now();
		}
	}
	
	public boolean verifyQR(String otherQR){
		return this.currentQR.equals(otherQR);
	}
	
	public String currentQR(){
		return this.currentQR;
	}
	
	public void newQR(){
		// Create a new QR string
		// Ultimately, a unique string. Hash-based? Crypto-based?
		// For now, just use the object data and some random int
		int r = (int) (Math.random() * 10);
		// r = 0; // For debugging. QR always the same.
		
		// Move QR to old QR list
		// if(this.currentQR != null) { this.oldQRs.add(this.currentQR); }		
		
		this.currentQR = this.username + ":" + this.group + this.role + "_" + r; // + new BigInteger(130, myRandom).toString(32);
		// This SecureRandom/BigInteger solution lifted from http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
		
		ObjectifyService.ofy().save().entity(this).now();
	}
	
	public String toJson(){
		return new Gson().toJson(this);
	}
	
	public String presenceToString() {
		String result = null;
		if (presence != null) {
			result = "{" + presence[0];
			for (int i = 0; i < presence.length; i++) {
				result = result + ":" + presence[i];
			}
			result += "}";

		}
		return result;

	}
	
	// Enums for "magic" values
	public enum Roles{
		STUDENT (0),
		TUTOR (1);
		private final int i;
		Roles(int i){
			this.i = i;
		}
		public int getValue() { return i; }
	}
	
	public enum Scores{
		ABSENT (0),
		PRESENT (1),
		PARTICIPANT (2);
		private final int i;
		Scores(int i){
			this.i = i;
		}
		public int getValue() { return i; }
	}

}
