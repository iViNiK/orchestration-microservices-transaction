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
public class OrderEventListener {

	private OrderEvent receivedMessage;

	private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

	@StreamListener(target = NotificationChannel.INPUT_ORDER)
	public void listenOrderEvent(@Payload OrderEvent event) {
		receivedMessage = event;

		if (Action.ORDERPLACED.equals(event.getAction())) {
			event.setAction(Action.CUSTOMERNOTIFIEDORDERPROCESSED);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received event %s for order %d", event.getAction().getName(),
						event.getOrderId()));
				logger.info(String.format("Going to notify user that order place with id %d was processed",
						event.getOrderId()));
			}
		} else if (Action.ORDERNOTPLACED.equals(event.getAction())) {
			event.setAction(Action.CUSTOMERNOTIFIEDORDERNOTPROCESSED);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Received event %s for order %d", event.getAction().getName(),
						event.getOrderId()));
				logger.info(String.format("Going to notify user that order place with id %d could not be processed",
						event.getOrderId()));
			}
		}
	}

	public OrderEvent getReceivedMessage() {
		return receivedMessage;
	}

}
