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

import it.vinicioflamini.omt.common.message.ShippingEvent;
import it.vinicioflamini.omt.notification.kafka.channel.NotificationChannel;

@Component
public class GoodsShippedEventListener {

	private static final Logger logger = LoggerFactory.getLogger(GoodsShippedEventListener.class);

	@StreamListener(NotificationChannel.INPUT_SHIPPING)
	public void listenGoodsShipped(@Payload ShippingEvent goodsShippedMessage) {

		if (ShippingEvent.Action.SHIPMENTPROCESSED.equals(goodsShippedMessage.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received a\" GoodsShippedEvent\" for order id: %d",
						goodsShippedMessage.getOrderId()));
				logger.info(String.format("Going to notify user for shippment id %d reached", goodsShippedMessage.getShippingId()));
			}
		}

	}

}
