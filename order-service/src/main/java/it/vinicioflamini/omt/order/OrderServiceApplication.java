/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import it.vinicioflamini.omt.common.domain.EventPublisher;
import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.repository.OutboxRepository;
import it.vinicioflamini.omt.order.kafka.source.OrderEventSource;
import it.vinicioflamini.omt.order.repository.OrderRepository;

@SpringBootApplication
@Configuration
@EnableScheduling
public class OrderServiceApplication {
	
	@Bean
	public EventPublisher<Order> eventPublisher(OrderEventSource orderEventSource, OutboxRepository outboxRepository, OrderRepository orderRepository) {
		return new EventPublisher<>(orderEventSource, outboxRepository, orderRepository);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
