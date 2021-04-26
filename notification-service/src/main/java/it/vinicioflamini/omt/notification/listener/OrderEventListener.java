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

import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.notification.kafka.channel.NotificationChannel;

@Component
public class OrderEventListener {

	private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

	@StreamListener(target = NotificationChannel.INPUT_ORDER)
	public void listenOrderEvent(@Payload OrderEvent message) {

		if (OrderEvent.Action.ORDERPLACED.equals(message.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received an \"OrderPlacedEvent\" for order %d", message.getOrderId()));
				logger.info(String.format("Going to notify user that order place with id %d was processed",
						message.getOrderId()));
			}
		} else if (OrderEvent.Action.ORDERNOTPLACED.equals(message.getAction())) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received an \"OrderNotPlacedEvent\" for order %d", message.getOrderId()));
				logger.info(String.format("Going to notify user that order place with id %d could not be processed",
						message.getOrderId()));
			}
		}

	}

}
