package com.cds.demo.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.cds.demo.paymentservice.dto.ErrorResponse;

@ControllerAdvice
public class PaymentExceptionHandler {

	@ExceptionHandler(value = {PaymentException.class})
	public ResponseEntity<ErrorResponse> handleException(PaymentException e){
		ErrorResponse errorResponse = ErrorResponse.builder().code(HttpStatus.NOT_FOUND.name())
										.message(e.getMessage())
										.build();
		return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = {HttpMessageNotReadableException.class})
	public ResponseEntity<ErrorResponse> handlePaymentExceptionHandler(HttpMessageNotReadableException e){
		ErrorResponse errorResponse = ErrorResponse.builder().code(HttpStatus.BAD_REQUEST.name())
										.message(e.getMessage())
										.build();
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
		ErrorResponse errorResponse = ErrorResponse.builder().code(HttpStatus.BAD_REQUEST.name())
										.message(e.getMessage())
										.build();
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
	}
	
	/*
	 * @ExceptionHandler(value = {BadCredentialsException.class}) public
	 * ResponseEntity<ErrorResponse>
	 * handleBadCredentialsException(BadCredentialsException e){ ErrorResponse
	 * errorResponse = ErrorResponse.builder().code(HttpStatus.FORBIDDEN.name())
	 * .message(e.getMessage()) .build(); return new
	 * ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN); }
	 */
	
	
	
	
}
