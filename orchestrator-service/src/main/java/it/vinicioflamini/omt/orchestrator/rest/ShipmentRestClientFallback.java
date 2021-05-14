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
public class ShipmentRestClientFallback implements ShipmentRestClient {

	@Autowired
	private OutboxProxy outboundProxy;
	
	private static final Logger logger = LoggerFactory.getLogger(ShipmentRestClientFallback.class);
	
	@Override
	public ResponseEntity<String> processShipment(OrderRequest req) {
		/*
		 * If you have an error in the shipping procedure, you must reiterate it by
		 * posting a SHIPMENTFAILED event.
		 */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing shipment fallback for order %d", req.getOrderId()));
		}
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(req.getOrderId());
		orderEvent.setAction(Action.SHIPMENTFAILED);

		try {
			outboundProxy.requestMessage(req.getOrderId(), DomainObjects.ORDER, OrchestratorChannel.OUTPUT_SHIPMENT, orderEvent);
		} catch (JsonProcessingException e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						String.format("Could not execute shipment fallback for order %d.%nError is: %s",
								req.getOrderId(), e.getLocalizedMessage()));
			}

			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
