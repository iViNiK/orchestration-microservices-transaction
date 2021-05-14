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
import it.vinicioflamini.omt.orchestrator.rest.InventoryRestClient;

@Component
public class PaymentFailedEventListener {

	@Autowired
	private InventoryRestClient inventoryRestClient;

	private static final Logger logger = LoggerFactory.getLogger(PaymentFailedEventListener.class);

	private OrderEvent receivedMessage;
	
	@StreamListener(OrchestratorChannel.INPUT_PAYMENT)
	public void listenPaymentFailed(@Payload OrderEvent event) {
		receivedMessage = event;
		
		if (Action.PAYMENTFAILED.equals(event.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received a \"PaymentFailedEvent\" for order id: %d",
						event.getOrderId()));
			}
			if (event.getOrderId() != null) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Going to call inventory service to compensate item for order id: %d",
							event.getOrderId()));
				}
				
				OrderRequest req = new OrderRequest();
				req.setOrderId(event.getOrderId());
				req.setItemId(event.getItemId());
				req.setCustomerId(event.getCustomerId());

				inventoryRestClient.compensateInventory(req);
			}
		}
	}
	
	public OrderEvent getReceivedMessage() {
		return receivedMessage;
	}
}
