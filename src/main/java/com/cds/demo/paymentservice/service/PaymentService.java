package com.cds.demo.paymentservice.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cds.demo.paymentservice.dto.AuthenticationRequest;
import com.cds.demo.paymentservice.dto.AuthenticationResponse;
import com.cds.demo.paymentservice.dto.TransactionRequest;
import com.cds.demo.paymentservice.dto.TransactionResponse;
import com.cds.demo.paymentservice.exception.PaymentException;

@Service
public class PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private static final String PAYMENT_ENDPOINT = "/payments";
	private static final String AUTHENTICATE_ENDPOINT = "/authenticate";

	@Value("${payment.server.host}")
	private String HOST;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SendMessageToExchange messageExchange;
	
	@Autowired
	private SmsSender smsSender;
	
	ExecutorService executorService = Executors.newCachedThreadPool();
	
	
	

	/**
	 * Rest call to get a transaction by ID
	 * 
	 * @param header
	 * @param id
	 * @return TransactionResponse
	 * @throws URISyntaxException
	 */
	public TransactionResponse findById(String header, Integer id) throws URISyntaxException {
		logger.info("received request for listing a particular transaction");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", header);
		RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
				new URI(HOST + PAYMENT_ENDPOINT + "/" + id));
		return restTemplate.exchange(request, TransactionResponse.class).getBody();
	}

	/**
	 * Rest call to get all transactions
	 * 
	 * @param header
	 * @return List<TransactionResponse>
	 * @throws URISyntaxException
	 */
	public List<TransactionResponse> findAll(String header) throws URISyntaxException {
		logger.info("received request for listing all transactions");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", header);
		RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET, new URI(HOST + PAYMENT_ENDPOINT));
		return restTemplate.exchange(request, new ParameterizedTypeReference<List<TransactionResponse>>() {
		}).getBody();
	}

	/**
	 * Rest call to insert a transaction. Publish the message to a queue.
	 * 
	 * @param header
	 * @param TransactionRequest
	 * @return TransactionResponse
	 * @throws InterruptedException 
	 * @throws URISyntaxException
	 */
	public TransactionResponse save(String header, TransactionRequest transactionRequest)  {
		logger.info("received request for save transaction");
		TransactionResponse response = null;
		List<CompletableFuture<?>> futuresList = new ArrayList<>();
		
		CompletableFuture<TransactionResponse> executeSaveAsync = CompletableFuture.supplyAsync(()->savePayment(header, transactionRequest)); 
		CompletableFuture<Boolean> executeNotifyAsync = CompletableFuture.supplyAsync(()->notifySmsAsync(transactionRequest)); 
		
		futuresList.add(executeSaveAsync);
		futuresList.add(executeNotifyAsync);
		
		CompletableFuture<Void> taskList = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
		
		CompletableFuture<List<Object>> allCompletableFuture = taskList.thenApply(future -> {
			return futuresList.stream().map(completableFuture -> completableFuture.join()).collect(Collectors.toList());
        });
		try {
			for(Object object : allCompletableFuture.get()) {
				if(object instanceof TransactionResponse) {
					logger.info("response returned, sending message to queue");
					response = (TransactionResponse)object; 
					messageExchange.send(response);
					break;
				}
			}
			
		}catch (InterruptedException | ExecutionException e) {
			throw new PaymentException(e.getMessage());
		}
		return response;
	}

	/**
	 * Rest call to update/refund a transaction
	 * 
	 * @param header
	 * @param id
	 * @param TransactionRequest
	 * @returnTransactionResponse
	 * @throws URISyntaxException
	 */
	public TransactionResponse update(String header, Integer id, TransactionRequest transactionRequest)
			throws URISyntaxException {
		logger.info("received request for transaction update/refund");
		MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<String, String>();
		headerMap.add("Authorization", header);
		RequestEntity<TransactionRequest> request = new RequestEntity<>(transactionRequest, headerMap, HttpMethod.PUT,
				new URI(HOST + PAYMENT_ENDPOINT + "/" + id));
		return restTemplate.exchange(request, TransactionResponse.class).getBody();
	}

	/**
	 * Rest call to delete a transaction
	 * 
	 * @param header
	 * @param id
	 * @return TransactionResponse
	 * @throws URISyntaxException
	 */
	public TransactionResponse delete(String header, Integer id) throws URISyntaxException {
		logger.info("received request to delete a particular transaction");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", header);
		RequestEntity<String> request = new RequestEntity<>(header, HttpMethod.DELETE,
				new URI(HOST + PAYMENT_ENDPOINT + "/" + id));
		return restTemplate.exchange(request, TransactionResponse.class).getBody();
	}

	/**
	 * Rest call to get authorization token
	 * 
	 * @param AuthenticationResponse
	 * @return AuthenticationResponse
	 * @throws URISyntaxException
	 */
	public AuthenticationResponse authenticate(AuthenticationRequest request) throws URISyntaxException {
		logger.info("received request for authentication");
		RequestEntity<AuthenticationRequest> authRequest = new RequestEntity<>(request, HttpMethod.POST,
				new URI(HOST + AUTHENTICATE_ENDPOINT));
		return restTemplate.exchange(authRequest, AuthenticationResponse.class).getBody();
	}

	private TransactionResponse savePayment(String header, TransactionRequest transactionRequest)  {
		logger.info("received request for transaction save");
		MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<String, String>();
		headerMap.add("Authorization", header);
		RequestEntity<TransactionRequest> request;
		try {
			request = new RequestEntity<>(transactionRequest, headerMap, HttpMethod.POST, new URI(HOST + PAYMENT_ENDPOINT));
		} catch (URISyntaxException e) {
			throw new  PaymentException(e.getInput()+" " +e.getMessage());
		}
		return restTemplate.exchange(request, TransactionResponse.class).getBody();
		
	}
	
	//TODO uncomment sendSms in demo
	private Boolean notifySmsAsync(TransactionRequest transactionRequest) {
		StringBuilder message = new StringBuilder();
		
		message.append("Hi ")
		  .append(transactionRequest.getCustomerName())
		  .append(", This is a transaction alert of Rs. ")
		  .append(transactionRequest.getAmount())
		  .append("/- Intiated to ")
		  .append(transactionRequest.getReceivedBy());
		
		logger.info("message sent" + message);
		//smsSender.sendSms(transactionRequest.getMobileNumber(),message.toString());
		return true;
	}
}
