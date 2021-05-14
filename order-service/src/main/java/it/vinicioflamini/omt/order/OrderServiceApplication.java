/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import it.vinicioflamini.omt.common.domain.EventPublisher;
import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.repository.OutboxRepository;
import it.vinicioflamini.omt.order.kafka.source.OrderEventSource;
import it.vinicioflamini.omt.order.repository.OrderRepository;

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {  "it.vinicioflamini.omt.order.repository", "it.vinicioflamini.omt.common.repository" })
@EntityScan(basePackages = {  "it.vinicioflamini.omt.common.entity" })
@ComponentScan(basePackages = { "it.vinicioflamini.omt.order.*", "it.vinicioflamini.omt.common.*" })
public class OrderServiceApplication {
	
	@Bean
	public EventPublisher<Order> eventPublisher(OrderEventSource orderEventSource, OutboxRepository outboxRepository, OrderRepository orderRepository) {
		return new EventPublisher<>(orderEventSource, outboxRepository, orderRepository);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
