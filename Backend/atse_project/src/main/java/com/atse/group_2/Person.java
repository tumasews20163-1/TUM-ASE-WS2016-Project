package com.atse.group_2;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Person {	
	// Crypto isn't fully implemented. Leave this at false
	private static final boolean USE_CRYPTO = false;
	
	@Id
	String username; 			// username of the person
	transient String password; 	// password of the person
	String group; 				// id of the group
	int role; 					// 0 is a student 1 is a tutor
	private String currentQR;
	Map<String, Scores> attendance;
	// String randomString; 		// For Crypto implementation. Not supported yet.
	// ArrayList<String> oldQRs;	// See note in newQR()

	
	public Person() {
		
	}

	
	public Person(String username, String password, int role, String group) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.group= group;		
		if (group != null && this.role != Roles.TUTOR.getValue()) { Group.addPersonToGroup(this.group, this.username); }
		this.attendance = new HashMap<String, Scores>();
		
		// this.randomString = newRandomString(); // For Crypto implementation. Not supported yet.
		// this.oldQRs = new ArrayList<String>(); // See note in newQR()
		
		this.newQR();
	}
	
	
	// Marks the current Person as being present at a given session.
	// Optionally, marks the current Person has having participated in the session.
	public void markAttendance(String sessionID, boolean participated){
		Scores score = participated ? Scores.PARTICIPANT : Scores.PRESENT;
		
		Group.addNewSessionToGroup(this.group, sessionID);
		
		updateScore(sessionID, score);
	}	
	
	
	// Returns true if the provided QR code matches this Person's current QR code
	public boolean verifyQR(String otherQR){
		return this.currentQR.equals(otherQR);
	}
	
	
	// Returns the current QR code as a string
	public String currentQR(){
		return this.currentQR;
	}
	
	
	// Generate a new QR code for the current person
	public void newQR(){
		// Create a new QR string
		
		// Move QR to old QR list = GAE doesn't like this. Probably not worth investing much time in right now.
		// if(this.currentQR != null) { this.oldQRs.add(this.currentQR); }	
		
		String newQR;
		if(Person.USE_CRYPTO){
			newQR = newCryptoQRString();
		} else {
			newQR = newRandomQRString();
		}
		
		this.currentQR = newQR;
				
		ObjectifyService.ofy().save().entity(this).now();
	}
	
	
	// Returns the JSON representation of the current person
	public String toJson(){
		return new Gson().toJson(this);
	}
	
	
	// Enums for "magic" values: Roles and Scores
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
	
	
	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ============================================================================================
	
	// Enters a score for a student for a given session
	private void updateScore(String sessionID, Scores newScore){
		if (this.attendance == null) { this.attendance = new HashMap<String, Scores>(); }
		
		if (!this.attendance.containsKey(sessionID) 
				|| (this.attendance.get(sessionID) != null 
				&& this.attendance.get(sessionID).getValue() < newScore.getValue())){
			// If the student does NOT have a score for this session, add the new score.
			// If the student already has an entry for a given session,
			// update the score iff the old score is lower than the new one.
			// (If student was already marked for attendance+participation, don't erase that score for just attendance)					
			this.attendance.put(sessionID, newScore);
			ObjectifyService.ofy().save().entity(this).now();
		}
	}
	
	
	// Returns a new String to be used as a QR code
	private String newRandomQRString(){
		return this.username + ":" + newRandomString();	
	}
	
	
	// Returns a new random String which is NOT formatted to be a QR code
	private String newRandomString(){
		// This is expensive to create. It would be nice to move it to a property but GAE doesn't seem to like that
		SecureRandom myRandom = new SecureRandom();
		BigInteger i = new BigInteger(130, myRandom);	
		
		// This SecureRandom/BigInteger solution lifted from 
		// http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
		return i.toString(32);
	}
	
	
	// Returns a new cryptographic String suitable for use as a QR code - not yet supported
	private String newCryptoQRString(){		
		throw new UnsupportedOperationException("Operation not yet supported.");
		// this.randomString = newRandomString();
		// Need to add username:
		// return Crypto.getEncryptedString(this.toJson(), this.randomString);
	}
}
