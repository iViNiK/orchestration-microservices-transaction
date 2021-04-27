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

import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;
import it.vinicioflamini.omt.orchestrator.rest.InventoryRestClient;

@Component
public class OrderPlacedEventListener {

	@Autowired
	private InventoryRestClient inventoryRestClient;

	private static final Logger logger = LoggerFactory.getLogger(OrderPlacedEventListener.class);

	@StreamListener(target = OrchestratorChannel.INPUT_ORDER)
	public void listenOrderPlaced(@Payload OrderEvent orderEvent) {

		if (OrderEvent.Action.ORDERPLACED.equals(orderEvent.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received an \"OrderPlacedEvent\" for order id: %d", orderEvent.getOrderId()));
				logger.info(String.format("Going to call inventory service for order id: %d", orderEvent.getOrderId()));
			}
			
			OrderRequest req = new OrderRequest();
			req.setOrderId(orderEvent.getOrderId());
			req.setItemId(orderEvent.getItemId());
			req.setCustomerId(orderEvent.getCustomerId());

			inventoryRestClient.doInventory(req);
		}
	}

}