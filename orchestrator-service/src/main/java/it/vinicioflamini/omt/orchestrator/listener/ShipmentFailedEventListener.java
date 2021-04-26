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

import it.vinicioflamini.omt.common.message.ShippingEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;
import it.vinicioflamini.omt.orchestrator.rest.ShipmentRestClient;

@Component
public class ShipmentFailedEventListener {

	@Autowired
	private ShipmentRestClient shipmentRestClient;

	private static final Logger logger = LoggerFactory.getLogger(ShipmentFailedEventListener.class);

	@StreamListener(OrchestratorChannel.INPUT_SHIPMENT)
	public void listenPaymentFailed(@Payload ShippingEvent shipmentFailedMessage) {

		if (ShippingEvent.Action.SHIPMENTFAILED.equals(shipmentFailedMessage.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received a \"ShipmentFailedEvent\" for order %d",
						shipmentFailedMessage.getOrderId()));
			}
			if (shipmentFailedMessage.getOrderId() != null) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Going to call shipment service to process shipment for order %d",
							shipmentFailedMessage.getOrderId()));
				}
				
				OrderRequest req = new OrderRequest();
				req.setOrderId(shipmentFailedMessage.getOrderId());

				shipmentRestClient.processShipment(req);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("Unable to call shipment service to process shipment. Order id is NULL");
				}
			}
		}
	}
}
