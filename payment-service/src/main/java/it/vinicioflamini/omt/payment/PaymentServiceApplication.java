/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.payment;

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
import it.vinicioflamini.omt.common.entity.Payment;
import it.vinicioflamini.omt.common.repository.OutboxRepository;
import it.vinicioflamini.omt.payment.kafka.source.PaymentEventSource;
import it.vinicioflamini.omt.payment.repository.PaymentRepository;

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "it.vinicioflamini.omt.payment.repository",
		"it.vinicioflamini.omt.common.repository" })
@EntityScan(basePackages = { "it.vinicioflamini.omt.common.entity" })
@ComponentScan(basePackages = { "it.vinicioflamini.omt.payment.*", "it.vinicioflamini.omt.common.*" })
public class PaymentServiceApplication {

	@Bean
	public EventPublisher<Payment> eventPublisher(PaymentEventSource paymentEventSource,
			OutboxRepository outboxRepository, PaymentRepository paymentRepository) {
		return new EventPublisher<>(paymentEventSource, outboxRepository, paymentRepository);
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
