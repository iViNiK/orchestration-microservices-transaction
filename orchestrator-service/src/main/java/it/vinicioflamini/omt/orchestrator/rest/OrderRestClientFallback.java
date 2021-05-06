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
public class OrderRestClientFallback implements OrderRestClient {

	@Autowired
	private OutboxProxy outboundProxy;
	
	private static final Logger logger = LoggerFactory.getLogger(OrderRestClientFallback.class);
	
	@Override
	public ResponseEntity<String> compensateOrder(OrderRequest req) {
		/* Re-iterate order cancellation on failure by posting ORDERNOTPLACED event. */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing compensate order fallback for order %d", req.getOrderId()));
		}

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(req.getOrderId());
		orderEvent.setAction(Action.ORDERNOTPLACED);

		try {
			outboundProxy.requestMessage(req.getOrderId(), DomainObjects.ORDER, OrchestratorChannel.OUTPUT_ORDER, orderEvent);
		} catch (JsonProcessingException e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						String.format("Could not execute compensate order fallback for order %d.%nError is: %s",
								req.getOrderId(), e.getLocalizedMessage()));
			}

			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
