/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.inventory;

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
import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.common.repository.OutboxRepository;
import it.vinicioflamini.omt.inventory.kafka.source.InventoryEventSource;
import it.vinicioflamini.omt.inventory.repository.InventoryRepository;

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "it.vinicioflamini.omt.inventory.repository",
		"it.vinicioflamini.omt.common.repository" })
@EntityScan(basePackages = { "it.vinicioflamini.omt.common.entity" })
@ComponentScan(basePackages = { "it.vinicioflamini.omt.inventory.*", "it.vinicioflamini.omt.common.*" })
public class InventoryServiceApplication {

	@Bean
	public EventPublisher<Item> eventPublisher(InventoryEventSource inventoryEventSource,
			OutboxRepository outboxRepository, InventoryRepository inventoryRepository) {
		return new EventPublisher<>(inventoryEventSource, outboxRepository, inventoryRepository);
	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
