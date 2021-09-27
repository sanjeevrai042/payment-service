package com.cds.demo.paymentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cds.demo.paymentservice.dto.TransactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RabbitMqMessageListener {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMqMessageListener.class);

	@Autowired
	private SmsSender smsSender;
	
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Send an SMS
	 * @param String
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public void onMessage(String message) throws JsonMappingException, JsonProcessingException {
		logger.info("received message from Queue" + message);
		
		TransactionResponse response = objectMapper.readValue(message, TransactionResponse.class);
//		smsSender.sendSms(response.getMobileNumber(), prepareSmsBody(response));
		prepareSmsBody(response);
	}
	
	
    /**
     * Prepare the SMS body which is to be sent to a user
     * @param TransactionResponse
     * @return String
     */
	private String prepareSmsBody(TransactionResponse response) {
		logger.info("Building body for the SMS");
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hi ")
		  .append(response.getCustomerName())
		  .append(", Rs. ")
		  .append(response.getAmount())
		  .append("/- is been paid to ")
		  .append(response.getReceivedBy())
		  .append(" on ")
		  .append(response.getAudit().getTimestamp())
		  .append(". Transaction: ")
		  .append(response.getTransactionId());
		System.out.println(sb);

		return sb.toString();
	}

}