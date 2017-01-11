package com.atse.group_2;

import com.google.gson.Gson;

class APIError{
	int ErrorType;
	String ErrorMessage;
	
	// Types ErrorType;
	public enum Types{ // Can use a Map. TBD
		USERNAME_NOT_FOUND (0),
		INVALID_PASSWORD (1),
		MISSING_CREDENTIAL (2),
		ROLE_MISMATCH (3),
		UNDEFINED_ROLE (4),
		UNSPECIFIED_ERROR (666);
		private final int i; 
		Types(int i){
			this.i = i;
		}
		public int getValue() { return i; }
	}
	
	public APIError(int type, String message){
		ErrorType = type;
		ErrorMessage = message;
	}
	
	public String toJson(){
		return new Gson().toJson(this);
	}
}