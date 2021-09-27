package com.cds.demo.paymentservice.exception;


/**
 * Exception class for Payment server to catch unwanted results.
 * @author rahul.ghosh
 * @since 2021
 */
public class PaymentException extends RuntimeException{
	
	public PaymentException() {
		super();
	}

	public PaymentException(String message) {
		super(message);
	}

}
