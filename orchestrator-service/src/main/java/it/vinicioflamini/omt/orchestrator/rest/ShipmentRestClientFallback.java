package it.vinicioflamini.omt.orchestrator.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.source.ShipmentFailedEventSource;

@Component
public class ShipmentRestClientFallback implements ShipmentRestClient {

	@Autowired
	ShipmentFailedEventSource shipmentFailedEventSource;
	
	private static final Logger logger = LoggerFactory.getLogger(ShipmentRestClientFallback.class);
	
	@Override
	public ResponseEntity<String> processShipment(OrderRequest req) {
		/*
		 * If you have an error in the shipping procedure, you must reiterate it by
		 * posting a SHIPMENTFAILED event.
		 */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing Fallback during shipment for order %d", req.getOrderId()));
		}
		
		shipmentFailedEventSource.publishShipmentFailedEvent(req.getOrderId());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
