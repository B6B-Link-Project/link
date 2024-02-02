package com.link.back.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class RabbitConfig {
	@Bean
	public List<Queue> myQueue() {
		List<Queue> queueList = new ArrayList<>();
		queueList.add(new Queue("sonarqube_queue", true));
		return queueList;
	}

	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		AmqpAdmin admin = new RabbitAdmin(connectionFactory);
		myQueue().forEach(admin::declareQueue);
		return admin;
	}
}
