/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order.kafka.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.order.kafka.channel.OrderChannel;

@Component
public class OrderEventSource {

	@Autowired
	private OrderChannel orderChannel;

	public void publishOrderEvent(Order order, boolean placed) {

		OrderEvent message = new OrderEvent();
		message.setOrderId(order.getId());
		message.setItemId(order.getItemId());
		message.setCustomerId(order.getCustomerId());
		message.setAction(placed ? OrderEvent.Action.ORDERPLACED : OrderEvent.Action.ORDERNOTPLACED);

		MessageChannel messageChannel = orderChannel.outboundOrder();
		messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}
