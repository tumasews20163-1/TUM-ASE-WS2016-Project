package com.atse.group_2;

import java.util.ArrayList;

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
	String password; // password of the person
	String group; // id of the group
	int role; // 0 is a student 1 is a tutor
	boolean presentation; // true if the student presented
	int[] presence; // assumption- 8 tutorials. Value 0 if the student was
					// present and value 1 if he/she was not.

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
