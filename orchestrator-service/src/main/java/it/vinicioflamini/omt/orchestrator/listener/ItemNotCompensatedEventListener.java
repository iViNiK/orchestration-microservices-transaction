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
import it.vinicioflamini.omt.orchestrator.rest.InventoryRestClient;

@Component
public class ItemNotCompensatedEventListener {

	@Autowired
	private InventoryRestClient inventoryRestClient;

	private static final Logger logger = LoggerFactory.getLogger(ItemNotCompensatedEventListener.class);

	@StreamListener(OrchestratorChannel.INPUT_INVENTORY)
	public void listenItemNotFetched(@Payload ItemEvent itemEvent) {

		if (ItemEvent.Action.ITEMNOTCOMPENSATED.equals(itemEvent.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received an \"ItemNotCompensated\" for item id: %d", itemEvent.getItemId()));
				logger.info(String.format("Going to call order service to compensate inventory for order with id: %d",
						itemEvent.getOrderId()));
			}
			
			OrderRequest req = new OrderRequest();
			req.setOrderId(itemEvent.getOrderId());
			req.setItemId(itemEvent.getItemId());

			inventoryRestClient.compensateInventory(req);
		}
	}
}
