package com.cds.demo.paymentservice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@ToString
public class TransactionRequest  implements Serializable{

	private Integer id;
	private String customerName;
	private TransactionMode transactionMode;
	private Float amount;
	private String comment;
	private String receivedBy;
	private String emailId;
	private String mobileNumber;

	
}
