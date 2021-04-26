package it.vinicioflamini.omt.orchestrator.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.source.ItemNotCompensatedEventSource;
import it.vinicioflamini.omt.orchestrator.kafka.source.OrderNotPlacedEventSource;

@Component
public class InventoryRestClientFallback implements InventoryRestClient {

	@Autowired
	OrderNotPlacedEventSource orderNotPlacedEventSource;

	@Autowired
	ItemNotCompensatedEventSource itemNotCompensatedEventSource;

	private static final Logger logger = LoggerFactory.getLogger(InventoryRestClientFallback.class);

	@Override
	public ResponseEntity<String> doInventory(OrderRequest req) {
		/*
		 * We are at the first step of the SAGA, the order has just been received. If
		 * you have an error in updating the inventory, you must cancel the order by
		 * posting an ORDERNOTPLACED event.
		 */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing Fallback during inventory update for order %d", req.getOrderId()));
		}

		orderNotPlacedEventSource.publishOrderNotPlacedEvent(req.getOrderId());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> compensateInventory(OrderRequest req) {
		/*
		 * Re-iterate inventory stock update on failure by posting an ITEMNOTCOMPENSATED
		 * event
		 */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing Fallback for order %d compensation", req.getOrderId()));
		}
		
		itemNotCompensatedEventSource.publishItemNotCompensatedEvent(req.getOrderId(), req.getItemId());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
