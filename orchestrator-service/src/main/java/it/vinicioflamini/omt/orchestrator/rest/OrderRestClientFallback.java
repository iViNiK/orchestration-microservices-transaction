package it.vinicioflamini.omt.orchestrator.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.source.OrderNotPlacedEventSource;

@Component
public class OrderRestClientFallback implements OrderRestClient {

	@Autowired
	OrderNotPlacedEventSource orderNotPlacedEventSource;
	
	private static final Logger logger = LoggerFactory.getLogger(OrderRestClientFallback.class);
	
	@Override
	public ResponseEntity<String> compensateOrder(OrderRequest req) {
		/* Re-iterate order cancellation on failure by posting ORDERNOTPLACED event. */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing Fallback during order %d compensation", req.getOrderId()));
		}

		orderNotPlacedEventSource.publishOrderNotPlacedEvent(req.getOrderId());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
