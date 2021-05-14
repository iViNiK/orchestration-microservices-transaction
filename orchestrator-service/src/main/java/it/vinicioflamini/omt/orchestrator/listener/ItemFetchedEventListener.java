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
import it.vinicioflamini.omt.orchestrator.rest.PaymentRestClient;

@Component
public class ItemFetchedEventListener {

	@Autowired
	private PaymentRestClient paymentRestClient;

	private static final Logger logger = LoggerFactory.getLogger(ItemFetchedEventListener.class);

	private OrderEvent receivedMessage;
	
	@StreamListener(OrchestratorChannel.INPUT_INVENTORY)
	public void listenItemFetchedEvent(@Payload OrderEvent event) {
		receivedMessage = event;
		
		if (Action.ITEMFETCHED.equals(event.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(
						String.format("Received an \"ItemFetchedEvent\" for order %d", event.getOrderId()));
			}
			
			if (event.getOrderId() != null && event.getItemId() != null) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Going to call payment service for order %d",
							event.getOrderId()));
				}
				
				OrderRequest req = new OrderRequest();
				req.setOrderId(event.getOrderId());
				req.setItemId(event.getItemId());

				paymentRestClient.doPayment(req);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Unable to call payment service. Order ID (%d) and/or Item ID (%d) are NULL.",
							event.getOrderId(), event.getItemId()));
				}
			}
		}

	}

	public OrderEvent getReceivedMessage() {
		return receivedMessage;
	}

}
