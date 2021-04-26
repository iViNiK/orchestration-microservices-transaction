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

import it.vinicioflamini.omt.common.message.ItemEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;
import it.vinicioflamini.omt.orchestrator.rest.PaymentRestClient;

@Component
public class ItemFetchedEventListener {

	@Autowired
	private PaymentRestClient paymentRestClient;

	private static final Logger logger = LoggerFactory.getLogger(ItemFetchedEventListener.class);

	@StreamListener(OrchestratorChannel.INPUT_INVENTORY)
	public void listenItemFetchedEvent(@Payload ItemEvent itemFetchedMessage) {

		if (ItemEvent.Action.ITEMFETCHED.equals(itemFetchedMessage.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(
						String.format("Received an \"ItemFetchedEvent\" for item %d", itemFetchedMessage.getItemId()));
			}
			
			if (itemFetchedMessage.getOrderId() != null && itemFetchedMessage.getItemId() != null) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Going to call payment service for order %d",
							itemFetchedMessage.getOrderId()));
				}
				
				OrderRequest req = new OrderRequest();
				req.setOrderId(itemFetchedMessage.getOrderId());
				req.setItemId(itemFetchedMessage.getItemId());

				paymentRestClient.doPayment(req);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Unable to call payment service. Order ID (%d) and/or Item ID (%d) are NULL.",
							itemFetchedMessage.getOrderId(), itemFetchedMessage.getItemId()));
				}
			}
		}

	}

}
