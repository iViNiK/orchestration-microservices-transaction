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
import it.vinicioflamini.omt.orchestrator.rest.ShipmentRestClient;

@Component
public class ShipmentFailedEventListener {

	@Autowired
	private ShipmentRestClient shipmentRestClient;

	private static final Logger logger = LoggerFactory.getLogger(ShipmentFailedEventListener.class);

	private OrderEvent receivedMessage;
	
	@StreamListener(OrchestratorChannel.INPUT_SHIPMENT)
	public void listenPaymentFailed(@Payload OrderEvent event) {
		receivedMessage = event;
		
		if (Action.SHIPMENTFAILED.equals(event.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received a \"ShipmentFailedEvent\" for order %d",
						event.getOrderId()));
			}
			if (event.getOrderId() != null) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Going to call shipment service to process shipment for order %d",
							event.getOrderId()));
				}
				
				OrderRequest req = new OrderRequest();
				req.setOrderId(event.getOrderId());

				shipmentRestClient.processShipment(req);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("Unable to call shipment service to process shipment. Order id is NULL");
				}
			}
		}
	}
	
	public OrderEvent getReceivedMessage() {
		return receivedMessage;
	}
}
