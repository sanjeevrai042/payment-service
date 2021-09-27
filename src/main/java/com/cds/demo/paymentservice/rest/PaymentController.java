package com.cds.demo.paymentservice.rest;

import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cds.demo.paymentservice.dto.AuthenticationRequest;
import com.cds.demo.paymentservice.dto.AuthenticationResponse;
import com.cds.demo.paymentservice.dto.TransactionRequest;
import com.cds.demo.paymentservice.dto.TransactionResponse;
import com.cds.demo.paymentservice.service.PaymentService;

@RestController
public class PaymentController {

	private static Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private PaymentService service;
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws URISyntaxException{
		return new ResponseEntity<>(service.authenticate(request),HttpStatus.OK);
	}

	@GetMapping("/payments/{id}")
	public ResponseEntity<TransactionResponse> findById(@RequestHeader("Authorization") String authorization,
														@PathVariable("id") Integer id) throws URISyntaxException {
		logger.info("received request for listing a particular transaction");
		return new ResponseEntity<>(service.findById(authorization,id), HttpStatus.OK);
	}

	@GetMapping("/payments")
	public ResponseEntity<List<TransactionResponse>> findAll(@RequestHeader("Authorization") String authorization) throws URISyntaxException {
		logger.info("received request for listing all transactions");
		return new ResponseEntity<>(service.findAll(authorization), HttpStatus.OK);
	}

	@PostMapping("/payments")
	public ResponseEntity<TransactionResponse> save(@RequestHeader("Authorization") String authorization,
													@RequestBody TransactionRequest transactionRequest)
			throws URISyntaxException {
		logger.info("received request for transaction save");
		return new ResponseEntity<TransactionResponse>(service.save(authorization,transactionRequest), HttpStatus.CREATED);
	}

	@PutMapping("/payments/{id}")
	public ResponseEntity<TransactionResponse> update(@RequestHeader("Authorization") String authorization,
												      @PathVariable("id") Integer id,
												      @RequestBody TransactionRequest transactionRequest) throws URISyntaxException {
		return new ResponseEntity<TransactionResponse>(service.update(authorization,id, transactionRequest), HttpStatus.OK);
	}

	@DeleteMapping("/payments/{id}")
	public ResponseEntity<?> delete(@RequestHeader("Authorization") String authorization,
									@PathVariable("id") Integer id) throws URISyntaxException {
		return new ResponseEntity<>(service.delete(authorization,id), HttpStatus.NO_CONTENT);
	}
}
