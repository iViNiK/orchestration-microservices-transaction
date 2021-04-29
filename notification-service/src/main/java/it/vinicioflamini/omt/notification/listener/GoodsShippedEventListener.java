/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.notification.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.notification.kafka.channel.NotificationChannel;

@Component
public class GoodsShippedEventListener {

	private static final Logger logger = LoggerFactory.getLogger(GoodsShippedEventListener.class);

	@StreamListener(NotificationChannel.INPUT_SHIPPING)
	public void listenGoodsShipped(@Payload OrderEvent event) {

		if (Action.SHIPMENTPROCESSED.equals(event.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received %s for shipment %d", event.getAction().getName(),
						event.getShipmentId()));
				logger.info(String.format("Going to notify customer for shippment %d reached", event.getShipmentId()));
			}
		}

	}

}
