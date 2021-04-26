/**
 * @author Vinicio Flamini (io@vinicioflamini.it)
 *
 */

package it.vinicioflamini.omt.orchestrator.config;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages={"it.vinicioflamini.omt.orchestrator.rest"})
@EnableCircuitBreaker
public class ApplicationConfiguration {

}
