package com.atse.group_2;

import java.math.BigInteger;
import java.security.SecureRandom;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Person {	
	public enum Roles{
		STUDENT (0),
		TUTOR (1);
		private final int i;
		Roles(int i){
			this.i = i;
		}
		public int getValue() { return i; }
	}
	
	@Id
	String username; // username of the person
	transient String password; // password of the person
	String group; // id of the group
	int role; // 0 is a student 1 is a tutor
	boolean presentation; // true if the student presented
	int[] presence; // assumption- 8 tutorials. Value 0 if the student was
					// present and value 1 if he/she was not.
	String currentQR;
	// SecureRandom myRandom;

	public Person() {
		
	}

	public Person(String username, String password, int role,String group) {
		// TODO Auto-generated constructor stub
		this.username = username;
		this.password = password;
		this.role = role;
		this.presentation = false;
		this.presence = new int[8];
		for (int i = 0; i < presence.length; i++) {
			presence[i] = 0;
		}
		this.group= group;
		this.newQR();
		// this.myRandom = new SecureRandom();
	}
	
	public boolean verifyQR(String otherQR){
		// Need to compare the 'other' QR code with mine and then create a new one
		// - only if they match? or always?
		return true;
	}
	
	public void newQR(){
		// Create a new QR string
		// Ultimately, a unique string. Hash-based? Crypto-based?
		// For now, just use the object data and some random int
		this.currentQR = this.username + this.group + this.role; // + new BigInteger(130, myRandom).toString(32);
		// This SecureRandom/BigInteger solution lifted from http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
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

}
