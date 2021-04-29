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

import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Shipment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.shipping.kafka.channel.ShippingChannel;

@Component
public class ShipmentEventSource implements EventSource<Shipment> {

	@Autowired
	private ShippingChannel shippingChannel;

	public boolean publishEvent(Shipment shipment, OrderEvent orderEvent) {
		OrderEvent message = new OrderEvent();
		message.setOrderId(shipment.getOrderId());
		message.setItemId(shipment.getItemId());
		message.setPaymentId(shipment.getPaymentId());
		message.setCustomerId(shipment.getCustomerId());
		message.setCustomerId(shipment.getShipmentId());

		MessageChannel messageChannel = shippingChannel.outboundShippingt();
		return messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}
