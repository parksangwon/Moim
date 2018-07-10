package com.navercorp.board.exception;

public class BadRequestException extends RuntimeException{
	
	private String message;
	
	public BadRequestException() {}
	
	public BadRequestException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
