package com.navercorp.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value = BadRequestException.class)
	public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handleRuntimeException(Exception e) {
		return new ResponseEntity<String>("관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
