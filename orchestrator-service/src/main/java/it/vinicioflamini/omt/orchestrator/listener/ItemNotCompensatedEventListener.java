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
public class ItemNotCompensatedEventListener {

	@Autowired
	private InventoryRestClient inventoryRestClient;

	private static final Logger logger = LoggerFactory.getLogger(ItemNotCompensatedEventListener.class);

	@StreamListener(OrchestratorChannel.INPUT_INVENTORY)
	public void listenItemNotFetched(@Payload OrderEvent event) {

		if (Action.ITEMNOTCOMPENSATED.equals(event.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received an \"ItemNotCompensated\" for item id: %d", event.getItemId()));
				logger.info(String.format("Going to call order service to compensate inventory for order with id: %d",
						event.getOrderId()));
			}
			
			OrderRequest req = new OrderRequest();
			req.setOrderId(event.getOrderId());
			req.setItemId(event.getItemId());

			inventoryRestClient.compensateInventory(req);
		}
	}
}
