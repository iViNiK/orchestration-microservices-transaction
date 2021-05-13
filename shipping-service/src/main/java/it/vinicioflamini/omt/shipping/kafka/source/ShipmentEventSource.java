/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.shipping.kafka.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Shipment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.shipping.kafka.channel.ShippingChannel;

@Component
public class ShipmentEventSource implements EventSource<Shipment> {

	@Autowired
	private ShippingChannel shippingChannel;
	
	private static final Logger logger = LoggerFactory.getLogger(ShipmentEventSource.class);

	public boolean publishEvent(Shipment shipment, OrderEvent orderEvent) {
		Assert.notNull(shipment, "PAYMENT IS NULL");
		Assert.notNull(orderEvent, "ORDER EVENT IS NULL");
		Assert.notNull(orderEvent.getAction(), "ACTION IS NULL");

		orderEvent.setOrderId(shipment.getOrderId());
		orderEvent.setItemId(shipment.getItemId());
		orderEvent.setPaymentId(shipment.getId());
		orderEvent.setCustomerId(shipment.getCustomerId());
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Going to send an %s for order %d", orderEvent.getAction().getName(), shipment.getOrderId()));
		}


		MessageChannel messageChannel = shippingChannel.outboundShipping();
		return messageChannel.send(MessageBuilder.withPayload(orderEvent)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}
