/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order.kafka.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.order.kafka.channel.OrderChannel;

@Component
public class OrderEventSource implements EventSource<Order> {

	@Autowired
	private OrderChannel orderChannel;

	private static final Logger logger = LoggerFactory.getLogger(OrderEventSource.class);
	
	public boolean publishEvent(Order order) {
		OrderEvent message = new OrderEvent();
		message.setOrderId(order.getId());
		message.setItemId(order.getItemId());
		message.setCustomerId(order.getCustomerId());
		message.setAction(OrderEvent.Action.ORDERPLACED);

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Going to send an \"OrderPlacedEvent\" for order %d", order.getId()));
		}

		MessageChannel messageChannel = orderChannel.outboundOrder();
		return messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}
