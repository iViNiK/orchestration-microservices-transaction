/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.orchestrator.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;
import it.vinicioflamini.omt.orchestrator.rest.OrderRestClient;

@Component
public class OrderNotPlacedEventListener {

	@Autowired
	private OrderRestClient orderRestClient;

	private static final Logger logger = LoggerFactory.getLogger(OrderNotPlacedEventListener.class);

	private OrderEvent receivedMessage;
	
	@StreamListener(target = OrchestratorChannel.INPUT_ORDER)
	public void listenOrderNotPlaced(@Payload OrderEvent event) {
		receivedMessage = event;
		
		if (Action.ORDERNOTPLACED.equals(event.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received an \"OrderNotPlacedEvent\" for order %d", event.getOrderId()));
				logger.info(String.format("Going to call order service for order %d", event.getOrderId()));
			}
			
			OrderRequest req = new OrderRequest();
			req.setOrderId(event.getOrderId());

			orderRestClient.compensateOrder(req);
		}
	}

	public OrderEvent getReceivedMessage() {
		return receivedMessage;
	}
}
