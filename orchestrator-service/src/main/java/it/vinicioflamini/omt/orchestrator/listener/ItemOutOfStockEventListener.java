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
import it.vinicioflamini.omt.orchestrator.rest.OrderRestClient;

@Component
public class ItemOutOfStockEventListener {

	@Autowired
	private OrderRestClient orderRestClient;

	private static final Logger logger = LoggerFactory.getLogger(ItemOutOfStockEventListener.class);

	@StreamListener(OrchestratorChannel.INPUT_INVENTORY)
	public void listenOutOfStockItem(@Payload ItemEvent itemEvent) {

		if (ItemEvent.Action.ITEMOUTOFSTOCK.equals(itemEvent.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received an \"ItemOutOfStock\" for item %d", itemEvent.getItemId()));
				logger.info(String.format("Going to call order service to compensate order %d",
						itemEvent.getOrderId()));
			}
			
			OrderRequest req = new OrderRequest();
			req.setOrderId(itemEvent.getOrderId());
			req.setItemId(itemEvent.getItemId());

			orderRestClient.compensateOrder(req);
		}
	}
}
