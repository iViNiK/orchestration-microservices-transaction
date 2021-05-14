/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.shipping;

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
import it.vinicioflamini.omt.common.entity.Shipment;
import it.vinicioflamini.omt.common.repository.OutboxRepository;
import it.vinicioflamini.omt.shipping.kafka.source.ShipmentEventSource;
import it.vinicioflamini.omt.shipping.repository.ShipmentRepository;

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "it.vinicioflamini.omt.shipping.repository",
		"it.vinicioflamini.omt.common.repository" })
@EntityScan(basePackages = { "it.vinicioflamini.omt.common.entity" })
@ComponentScan(basePackages = { "it.vinicioflamini.omt.shipping.*", "it.vinicioflamini.omt.common.*" })
public class ShippingServiceApplication {

	@Bean
	public EventPublisher<Shipment> eventPublisher(ShipmentEventSource shipmentEventSource,
			OutboxRepository outboxRepository, ShipmentRepository shipmentRepository) {
		return new EventPublisher<>(shipmentEventSource, outboxRepository, shipmentRepository);
	}

	public static void main(String[] args) {
		SpringApplication.run(ShippingServiceApplication.class, args);
	}

}
