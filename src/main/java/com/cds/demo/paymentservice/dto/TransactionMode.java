package com.cds.demo.paymentservice.dto;

import lombok.Getter;

public enum TransactionMode {
	CreditCard("C"),
	UPI("U"),
	DebitCard("D"),
	NetBanking("N"),
	Wallet("W");
	
	@Getter
	private String value;
	
	TransactionMode(String value){
		this.value = value;
	}
	
	public static TransactionMode fromValue(String value) {
		for(TransactionMode t : TransactionMode.values()) {
			if(t.getValue().equals(value)) {
				return t;
			}
		}
		throw new IllegalArgumentException("No Enum defined for "+ value);
	}
}
