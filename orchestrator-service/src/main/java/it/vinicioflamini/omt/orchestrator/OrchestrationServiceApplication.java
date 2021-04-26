/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import it.vinicioflamini.omt.orchestrator.config.ApplicationConfiguration;

@SpringBootApplication
@EnableAutoConfiguration
@Import( { ApplicationConfiguration.class } )
public class OrchestrationServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(OrchestrationServiceApplication.class, args);
	}

}
