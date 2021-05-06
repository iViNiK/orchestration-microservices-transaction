package it.vinicioflamini.omt.orchestrator.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;

@Component
public class InventoryRestClientFallback implements InventoryRestClient {

	@Autowired
	private OutboxProxy outboundProxy;

	private static final Logger logger = LoggerFactory.getLogger(InventoryRestClientFallback.class);

	@Override
	public ResponseEntity<String> doInventory(OrderRequest req) {
		/*
		 * We are at the first step of the SAGA, the order has just been received. If
		 * you have an error in updating the inventory, you must cancel the order by
		 * posting an ORDERNOTPLACED event.
		 */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing update inventory fallback for order %d", req.getOrderId()));
		}

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(req.getOrderId());
		orderEvent.setAction(Action.ORDERNOTPLACED);

		try {
			outboundProxy.requestMessage(req.getOrderId(), DomainObjects.ORDER, OrchestratorChannel.OUTPUT_INVENTORY, orderEvent);
		} catch (JsonProcessingException e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						String.format("Could not execute update inventory fallback for order %d.%nError is: %s",
								req.getOrderId(), e.getLocalizedMessage()));
			}

			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> compensateInventory(OrderRequest req) {
		/*
		 * Re-iterate inventory stock update on failure by posting an ITEMNOTCOMPENSATED
		 * event
		 */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing compensate inventory fallback for order %d", req.getOrderId()));
		}

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(req.getOrderId());
		orderEvent.setItemId(req.getItemId());
		orderEvent.setAction(Action.ITEMNOTCOMPENSATED);

		try {
			outboundProxy.requestMessage(req.getItemId(), DomainObjects.ITEM, OrchestratorChannel.OUTPUT_INVENTORY, orderEvent);
		} catch (JsonProcessingException e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						String.format("Could not execute compensate inventory fallback for order %d.%nError is: %s",
								req.getOrderId(), e.getLocalizedMessage()));
			}

			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
