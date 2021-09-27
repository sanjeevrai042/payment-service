package com.cds.demo.paymentservice.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


@Service
public class SmsSender {

	@Value("${twilio.auth.sid}")
	private String serviceId;
	
	@Value("${twilio.auth.token}")
	private String token;
	
	@Value("${twilio.from.no}")
	private String from;
	
	
	/**
	 * Method to send SMS 
	 * @param to
	 * @param body
	 */
    public void sendSms(String to, String body) {
        Twilio.init(serviceId, token);
        Message
                .creator(new PhoneNumber(to), 
                        new PhoneNumber(from), 
                        body)
                .create();
    	}
}