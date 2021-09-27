package com.cds.demo.paymentservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cds.demo.paymentservice.service.RabbitMqMessageListener;

@Configuration
public class RabbitMessageConfiguration {
	
	@Value("${rabbit.exchange.name}")
	public String EXCHANGE_NAME;
	
	@Value("${rabbit.queue.name}")
	public String QUEUE_NAME;
	
	@Value("${rabbit.routing.key}")
	public String ROUTING_KEY;

	@Bean
	public Queue queue() {
		return new Queue(QUEUE_NAME, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
	}

	/**
	 * Listens for messages on the  our Queue
	 * @param connectionFactory
	 * @param listenerAdapter
	 * @return SimpleMessageListenerContainer
	 */
	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_NAME);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(RabbitMqMessageListener receiver) {
		return new MessageListenerAdapter(receiver, "onMessage");
	}
}
