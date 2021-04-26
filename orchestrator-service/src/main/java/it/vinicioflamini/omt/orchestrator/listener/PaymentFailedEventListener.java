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

import it.vinicioflamini.omt.common.message.PaymentEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;
import it.vinicioflamini.omt.orchestrator.rest.InventoryRestClient;

@Component
public class PaymentFailedEventListener {

	@Autowired
	private InventoryRestClient inventoryRestClient;

	private static final Logger logger = LoggerFactory.getLogger(PaymentFailedEventListener.class);

	@StreamListener(OrchestratorChannel.INPUT_PAYMENT)
	public void listenPaymentFailed(@Payload PaymentEvent paymentFailedMessage) {

		if (PaymentEvent.Action.PAYMENTFAILED.equals(paymentFailedMessage.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received a \"PaymentFailedEvent\" for order id: %d",
						paymentFailedMessage.getOrderId()));
			}
			if (paymentFailedMessage.getOrderId() != null) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Going to call item service to compensate item for order id: %d",
							paymentFailedMessage.getOrderId()));
				}
				
				OrderRequest req = new OrderRequest();
				req.setOrderId(paymentFailedMessage.getOrderId());

				inventoryRestClient.compensateInventory(req);
			}
		}
	}
}
