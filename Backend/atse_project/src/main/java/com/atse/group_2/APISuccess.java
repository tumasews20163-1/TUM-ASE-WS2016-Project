package com.atse.group_2;

import com.google.gson.Gson;

class APISuccess{
	String Message;
	int ResponseType;
	
	public APISuccess(int type, String message){
		ResponseType = type;
		Message = message;
	}
	
	public String toJson(){
		return new Gson().toJson(this);
	}
}