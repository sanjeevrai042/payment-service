package com.cds.demo.paymentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cds.demo.paymentservice.config.RabbitMessageConfiguration;
import com.cds.demo.paymentservice.dto.TransactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SendMessageToExchange {

	private static final Logger logger = LoggerFactory.getLogger(SendMessageToExchange.class);
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	RabbitMessageConfiguration rabbitMessageConfiguration;
	
	public void send(TransactionResponse message) throws InterruptedException {
		logger.info("Sending messsage to exchange..");
		try {
			String payload = objectMapper.writeValueAsString(message);
			rabbitTemplate.convertAndSend(rabbitMessageConfiguration.EXCHANGE_NAME, 
									      rabbitMessageConfiguration.ROUTING_KEY, 
									      payload);
		    logger.info("message sent successfully");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	    
	}
}
