/**
 * @author Vinicio Flamini (io@vinicioflamini.it)
 *
 */

package it.vinicioflamini.omt.orchestrator.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "it.vinicioflamini.omt.common.repository" })
@EntityScan(basePackages = { "it.vinicioflamini.omt.common.entity" })
@ComponentScan(basePackages = { "it.vinicioflamini.omt.orchestrator.*", "it.vinicioflamini.omt.common.*" })
@EnableFeignClients(basePackages={"it.vinicioflamini.omt.orchestrator.rest"})
@EnableCircuitBreaker
public class ApplicationConfiguration {

}
