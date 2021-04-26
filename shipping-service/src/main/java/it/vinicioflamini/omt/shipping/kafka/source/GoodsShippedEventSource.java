/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.shipping.kafka.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.message.ShippingEvent;
import it.vinicioflamini.omt.shipping.kafka.channel.ShippingChannel;

@Component
public class GoodsShippedEventSource {

	@Autowired
	private ShippingChannel shippingChannel;

	public void publishGoodsShippedEvent(Long shippingId, Long orderId, boolean processed) {

		ShippingEvent message = new ShippingEvent();
		message.setShippingId(shippingId);
		message.setOrderId(orderId);
		message.setAction(processed ? ShippingEvent.Action.SHIPMENTPROCESSED : ShippingEvent.Action.SHIPMENTFAILED);
		
		MessageChannel messageChannel = shippingChannel.outboundShippingt();
		messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}
